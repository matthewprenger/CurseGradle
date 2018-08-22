package com.matthewprenger.cursegradle

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.plugins.JavaPluginConvention

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
            log.warn("Failed Java integration", t)
        }
    }

    static void checkJavaVersion(Project project, CurseProject curseProject) {

        try {
            JavaPluginConvention javaConv = (JavaPluginConvention) project.getConvention().getPlugins().get("java");
            JavaVersion javaVersion = JavaVersion.toVersion(javaConv.targetCompatibility)

            if (JavaVersion.VERSION_1_6.compareTo(javaVersion) >= 0) {
                curseProject.addGameVersion('Java 6')
            }
            if (JavaVersion.VERSION_1_7.compareTo(javaVersion) >= 0) {
                curseProject.addGameVersion('Java 7')
            }
            if (JavaVersion.VERSION_1_8.compareTo(javaVersion) >= 0) {
                curseProject.addGameVersion('Java 8')
            }
            if (JavaVersion.VERSION_1_9.compareTo(javaVersion) >= 0) {
                curseProject.addGameVersion('Java 9')
            }
        } catch (Throwable t) {
            log.warn("Failed to check Java Version", t)
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
            log.warn('Failed ForgeGradle integration', t)
        }
    }
}
