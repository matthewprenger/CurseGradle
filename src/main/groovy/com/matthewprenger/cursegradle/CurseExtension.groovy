package com.matthewprenger.cursegradle

class CurseExtension {

    def apiKey

    final Collection<CurseProject> curseProjects = new ArrayList<>()

    /**
     * Define a new CurseForge project for deployment
     *
     * @param configClosure The configuration closure
     */
    void project(Closure<?> configClosure) {
        CurseProject curseProject = new CurseProject()
        curseProject.with(configClosure)
        if (curseProject.apiKey == null) {
            curseProject.apiKey = this.apiKey
        }
        curseProjects.add(curseProject)
    }
}
