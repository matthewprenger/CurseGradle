package com.matthewprenger.cursegradle

import com.google.common.base.Strings
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
    static final Set<String> VALID_RELATIONS = ['requiredDependency', 'embeddedLibrary', 'optionalDependency', 'tool', 'incompatible']

    static String API_BASE_URL;
    static String getApiBaseUrl() {
        return API_BASE_URL
    }

    static String getVersionTypesUrl() {
        return "$API_BASE_URL/api/game/version-types"
    }

    static String getVersionUrl() {
        return "$API_BASE_URL/api/game/versions"
    }

    static String getUploadUrl() {
        return "$API_BASE_URL/api/projects/%s/upload-file"
    }

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
            if (project.state.failure != null) {
                project.logger.info 'Failure detected. Not running afterEvaluate'
            }

            extension.curseProjects.each { curseProject ->

                Util.check(!Strings.isNullOrEmpty(curseProject.id), "A CurseForge project was configured without an id")

                CurseUploadTask uploadTask = project.tasks.create("curseforge$curseProject.id", CurseUploadTask)
                curseProject.uploadTask = uploadTask
                uploadTask.group = TASK_GROUP
                uploadTask.description = "Uploads CurseForge project $curseProject.id"
                uploadTask.additionalArtifacts = curseProject.additionalArtifacts
                uploadTask.apiKey = curseProject.apiKey
                uploadTask.projectId = curseProject.id

                CurseExtension ext = project.extensions.getByType(CurseExtension)

                if (ext.curseGradleOptions.javaVersionAutoDetect && (!ext.curseGradleOptions.bukkitIntegration || !ext.curseGradleOptions.genericIntegration)) {
                    Integration.checkJavaVersion(project, curseProject)
                }

                if (ext.curseGradleOptions.javaIntegration && (!ext.curseGradleOptions.bukkitIntegration || !ext.curseGradleOptions.genericIntegration)) {
                    Integration.checkJava(project, curseProject)
                }
                if (ext.curseGradleOptions.forgeGradleIntegration && (!ext.curseGradleOptions.bukkitIntegration || !ext.curseGradleOptions.genericIntegration)) {
                    Integration.checkForgeGradle(project, curseProject)
                }

                API_BASE_URL = ext.curseGradleOptions.apiBaseUrl;
                if (ext.curseGradleOptions.bukkitIntegration && (!ext.curseGradleOptions.forgeGradleIntegration || !ext.curseGradleOptions.genericIntegration)) {
                    API_BASE_URL = 'https://dev.bukkit.org'
                }

                curseProject.copyConfig()

                uploadTask.mainArtifact = curseProject.mainArtifact

                // At this stage, all artifacts should be in a state ready to upload
                // ForgeGradle's reobf tasks are dependants of this
                uploadTask.dependsOn project.tasks.getByName('assemble')

                // Run after build if it's on the task graph. This is useful because if tests fail,
                // the artifacts won't be uploaded
                uploadTask.mustRunAfter project.tasks.getByName('build')

                mainTask.dependsOn uploadTask
                uploadTask.onlyIf { mainTask.enabled }

                curseProject.validate()

                if (curseProject.mainArtifact.artifact instanceof AbstractArchiveTask) {
                    uploadTask.dependsOn curseProject.mainArtifact.artifact
                }

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
