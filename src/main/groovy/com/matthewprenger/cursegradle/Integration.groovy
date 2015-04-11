package com.matthewprenger.cursegradle

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging

class Integration {

    private static final Logger log = Logging.getLogger(Integration)

    static void checkJava(CurseGradlePlugin plugin) {
        final Project project = plugin.project
        if (project.plugins.hasPlugin('java')) {
            log.info 'Adding Java integration'
            Task jarTask = project.tasks.getByName('jar')
            if (jarTask == null) { // Should be impossible
                return
            }

            plugin.extension.curseProjects.each { curseProject ->
                if (curseProject.mainArtifact == null) {
                    log.info 'Setting main artifact to Java Jar'
                    CurseArtifact artifact = new CurseArtifact()
                    artifact.artifact = jarTask
                    artifact.changelog = curseProject.changelog
                    artifact.releaseType = curseProject.releaseType
                    artifact.gameVersionStrings = curseProject.gameVersionStrings
                    curseProject.curseRelations.each { relation ->
                        artifact.relations(relation)
                    }
                    curseProject.uploadTask.dependsOn jarTask
                }
            }
        }
    }

    static void checkForgeGradle(CurseGradlePlugin plugin) {
        final Project project = plugin.project
        if (project.plugins.hasPlugin('forge') || project.plugins.hasPlugin('fml')) {
            log.info 'Adding ForgeGradle integration'
            Task reobfTask = project.tasks.getByName('reobf')
            if (reobfTask == null) {
                return
            }

            plugin.extension.curseProjects.each { curseProject ->
                curseProject.uploadTask.dependsOn reobfTask
            }
        }
    }
}
