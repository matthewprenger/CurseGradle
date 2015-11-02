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
                log.info 'Adding Java integration'
                Task jarTask = project.tasks.getByName('jar')
                if (jarTask == null) { // Should be impossible
                    return
                }

                log.info 'Setting main artifact to Java Jar'
                CurseArtifact artifact = new CurseArtifact()
                artifact.artifact = jarTask
                artifact.changelog = curseProject.changelog
                artifact.releaseType = curseProject.releaseType
                artifact.gameVersionStrings = curseProject.gameVersionStrings
                curseProject.curseRelations.each { relation ->
                    artifact.relations(relation)
                }
                curseProject.mainArtifact = artifact
                curseProject.uploadTask.dependsOn jarTask
            }
        } catch (Throwable t) {
            log.info('Failed Java integration', t)
            log.warn("Failed Java integration")
        }
    }

    static void checkForgeGradle(Project project, CurseProject curseProject) {
        try {
            if (project.plugins.hasPlugin('forge') || project.plugins.hasPlugin('fml')) {
                log.info 'Adding ForgeGradle integration'
                Task reobfTask = project.tasks.getByName('reobf')
                if (reobfTask == null) {
                    return
                }

                curseProject.uploadTask.dependsOn reobfTask

                curseProject.addGameVersion(project.extensions.minecraft.version)
            }
        } catch (Throwable t) {
            log.info('Failed ForgeGradle integration', t)
            log.warn("Failed ForgeGradle integration")
        }

        try {
            if (project.plugins.hasPlugin('net.minecraftforge.gradle.forge')) {
                log.info 'Adding ForgeGradle2 integration'
                Task reobfTask = project.tasks.getByName('reobfJar')
                if (reobfTask == null) {
                    log.info 'Couldn\'t find reobfJar task'
                    return;
                }

                curseProject.uploadTask.dependsOn reobfTask
                curseProject.addGameVersion(project.extensions.minecraft.version)

            }
        } catch (Throwable t) {
            log.info('Failed ForgeGradle2 integration', t)
            log.warn("Failed ForgeGradle2 integration")
        }
    }
}
