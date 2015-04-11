package com.matthewprenger.cursegradle

import org.gradle.util.ConfigureUtil

class CurseExtension {

    def apiKey

    final Collection<CurseProject> curseProjects = new ArrayList<>()

    /**
     * Define a new CurseForge project for deployment
     *
     * @param configureClosure The configuration closure
     */
    void project(Closure<?> configureClosure) {
        CurseProject curseProject = new CurseProject()
        ConfigureUtil.configure(configureClosure, curseProject)
        if (curseProject.apiKey == null) {
            curseProject.apiKey = this.apiKey
        }
        curseProjects.add(curseProject)
    }
}
