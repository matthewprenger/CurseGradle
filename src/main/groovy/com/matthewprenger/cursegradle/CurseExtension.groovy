package com.matthewprenger.cursegradle

class CurseExtension {

    /**
     * Optional global apiKey. Will be applied to all projects that don't declare one
     */
    def apiKey = '' // Initialize to empty string to delay error until the task is actually ran

	/**
	 * The base of the URL for the upload
	 */
    def apiUrl = CurseGradlePlugin.API_BASE_URL

    final Collection<CurseProject> curseProjects = new ArrayList<>()

    Options curseGradleOptions = new Options()

    @Deprecated
    boolean getDebug() {
        return curseGradleOptions.debug
    }

    @Deprecated
    void setDebug(boolean debug) {
        curseGradleOptions.debug = debug
    }

    /**
     * Define a new CurseForge project for deployment
     *
     * @param configClosure The configuration closure
     */
    void project(@DelegatesTo(CurseProject) Closure<?> configClosure) {
        CurseProject curseProject = new CurseProject()
        curseProject.with(configClosure)
        if (curseProject.apiKey == null) {
            curseProject.apiKey = this.apiKey
        }

        if (curseProject.apiUrl == null) {
            curseProject.apiUrl = this.apiUrl
        }

        curseProjects.add(curseProject)
    }

    void options(Closure<?> configClosure) {
        curseGradleOptions.with(configClosure)
    }
}
