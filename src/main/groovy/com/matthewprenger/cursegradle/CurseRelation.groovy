package com.matthewprenger.cursegradle

import static com.matthewprenger.cursegradle.Util.check

class CurseRelation implements Serializable {

    // Create methods for each type of project relation
    static {
        CurseGradlePlugin.VALID_RELATIONS.each { relation ->
            CurseRelation.metaClass."$relation" = { Object[] args ->
                projects.add(new Project(type: relation, slug: args[0]))
            }
        }
    }

    // Catches a missing method exception and gives a more user friendly error message
    def methodMissing(String name, def args) {
        throw new IllegalArgumentException("$name is not a valid relation type. Valid types are: $CurseGradlePlugin.VALID_RELATIONS")
    }

    Set<Project> projects = new HashSet<>()

    void validate() {
        projects.each { project -> project.validate() }
    }

    static class Project implements Serializable {

        /**
         * The unique slug for the project
         */
        String slug

        /**
         * The type of relationship. Must be one of {@link com.matthewprenger.cursegradle.CurseGradlePlugin#VALID_RELATIONS}
         */
        String type

        void validate() {
            check(slug != null, "Project relation slug not set")
            check(type != null && CurseGradlePlugin.VALID_RELATIONS.contains(type), "$type is not a valid relation type. Valid types are: $CurseGradlePlugin.VALID_RELATIONS")
        }

        @Override
        boolean equals(final o) {
            if (this.is(o)) {
                return true
            }
            if (getClass() != o.class) {
                return false
            }

            final Project project = (Project) o

            if (slug != project.slug) {
                return false
            }

            return true
        }

        @Override
        int hashCode() {
            return (slug != null ? slug.hashCode() : 0)
        }
    }
}
