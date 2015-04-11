package com.matthewprenger.cursegradle

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.bundling.AbstractArchiveTask

class CurseGradlePlugin implements Plugin<Project> {

    static final String TASK_NAME = 'curseforge'
    static final String TASK_GROUP = 'upload'
    static final String EXTENSION_NAME = 'curseforge'

    static final Set<String> VALID_RELEASE_TYPES = ['alpha', 'beta', 'release']
    static final Set<String> VALID_RELATIONS = ['requiredLibrary', 'embeddedLibrary', 'optionalLibrary', 'tool', 'incompatible']

    static final String VERSION_URL = 'https://minecraft.curseforge.com/api/game/versions'
    static final String UPLOAD_URL = 'https://minecraft.curseforge.com/api/projects/%s/upload-file'

    Project project
    CurseExtension extension

    @Override
    void apply(final Project project) {
        this.project = project

        final Task mainTask = project.tasks.create(TASK_NAME, DefaultTask)
        mainTask.description = "Uploads all CurseForge projects"
        mainTask.group = TASK_GROUP

        extension = project.extensions.create(EXTENSION_NAME, CurseExtension)


        project.afterEvaluate {
            extension.curseProjects.each { curseProject ->
                curseProject.copyConfig()
                curseProject.validate()

                CurseUploadTask uploadTask = createTask(curseProject)
                curseProject.uploadTask = uploadTask
                mainTask.dependsOn uploadTask

                curseProject.additionalArtifacts.each { artifact ->
                    if (artifact.artifact instanceof AbstractArchiveTask) {
                        AbstractArchiveTask archiveTask = (AbstractArchiveTask) artifact.artifact
                        uploadTask.dependsOn archiveTask
                    }
                }
            }

            Integration.checkJava(this)
            Integration.checkForgeGradle(this)
        }
    }

    CurseUploadTask createTask(CurseProject curseProject) {
        CurseUploadTask task = project.tasks.create("curseforge$curseProject.id", CurseUploadTask)
        task.group = TASK_GROUP
        task.description = "Uploads CurseForge project $curseProject.id"
        task.mainArtifact = curseProject.mainArtifact
        task.additionalArtifacts = curseProject.additionalArtifacts
        task.apiKey = curseProject.apiKey
        task.projectId = curseProject.id
        return task
    }
}
