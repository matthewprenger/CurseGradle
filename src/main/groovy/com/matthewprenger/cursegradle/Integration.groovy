package com.matthewprenger.cursegradle

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging

class Integration {

    private static final Logger log = Logging.getLogger(Integration)

    static void checkJava(Project project, CurseProject curseProject) {
        try {
            if (project.plugins.hasPlugin('java')) {
                log.info 'Java plugin detected, adding integration...'
                if (curseProject.mainArtifact == null) {
                    Task jarTask = project.tasks.getByName('jar')
                    log.info "Setting main artifact for CurseForge Project $curseProject.id to the java jar"
                    CurseArtifact artifact = new CurseArtifact()
                    artifact.artifact = jarTask
                    curseProject.mainArtifact = artifact
                    curseProject.uploadTask.dependsOn jarTask
                }
            }
        } catch (Throwable t) {
            log.info('Failed Java integration', t)
            log.warn("Failed Java integration")
        }
    }

    static void checkForgeGradle(Project project, CurseProject curseProject) {
        try {
            if (project.hasProperty('minecraft')) {
                log.info "ForgeGradle plugin detected, adding integration..."
                Task reobfTask = project.tasks.findByName('reobfJar')
                if (reobfTask == null) reobfTask = project.tasks.findByName('reobf')
                if (reobfTask == null) {
                    log.error("Couldn't find reobf or reobfJar task.")
                } else {
                    curseProject.uploadTask.dependsOn reobfTask
                }

                curseProject.addGameVersion(project.minecraft.version)
            }
        } catch (Throwable t) {
            log.info('Failed ForgeGradle integration', t)
            log.warn('Failed ForgeGradle integration')
        }
    }
}
