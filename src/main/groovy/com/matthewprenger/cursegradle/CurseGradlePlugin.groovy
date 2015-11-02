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

                CurseUploadTask uploadTask = project.tasks.create("curseforge$curseProject.id", CurseUploadTask)
                curseProject.uploadTask = uploadTask
                uploadTask.group = TASK_GROUP
                uploadTask.description = "Uploads CurseForge project $curseProject.id"
                uploadTask.additionalArtifacts = curseProject.additionalArtifacts
                uploadTask.apiKey = curseProject.apiKey
                uploadTask.projectId = curseProject.id

                Integration.checkJava(project, curseProject)
                Integration.checkForgeGradle(project, curseProject)

                curseProject.copyConfig()

                uploadTask.mainArtifact = curseProject.mainArtifact
                mainTask.dependsOn uploadTask
                uploadTask.onlyIf { mainTask.enabled }

                curseProject.validate()

                curseProject.additionalArtifacts.each { artifact ->
                    if (artifact.artifact instanceof AbstractArchiveTask) {
                        AbstractArchiveTask archiveTask = (AbstractArchiveTask) artifact.artifact
                        uploadTask.dependsOn archiveTask
                    }
                }
            }
        }
    }
}
