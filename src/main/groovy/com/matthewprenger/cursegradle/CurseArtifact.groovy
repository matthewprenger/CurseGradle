package com.matthewprenger.cursegradle

import com.google.gson.annotations.SerializedName
import org.gradle.api.Nullable
import org.gradle.api.Project
import org.gradle.util.ConfigureUtil

import static com.matthewprenger.cursegradle.Util.check

class CurseArtifact implements Serializable {

    /**
     * The file that should be uploaded
     */
    transient def artifact

    /**
     * <b>Internal Use Only</b>
     */
    @Nullable
    transient Collection<Object> gameVersionStrings

    /**
     * The changelog for this artifact. The {@link Object#toString()} method will be called to get the value
     */
    @SerializedName("changelog")
    def changelog

    /**
     * The user-friendly display name for this artifact. The {@link Object#toString()} method will be called to get the value
     */
    @Nullable
    @SerializedName("displayName")
    def displayName

    /**
     * The release type of this artifact. See {@link CurseGradlePlugin#VALID_RELEASE_TYPES} for valid release types
     */
    @SerializedName("releaseType")
    def releaseType

    /**
     * Internal use only. Will be set when game versions are resolved into their numerical representation
     */
    @Nullable
    @SerializedName("gameVersions")
    int[] gameVersions

    /**
     * Internal use only
     */
    @Nullable
    @SerializedName("parentFileID")
    Integer parentFileID

    @SerializedName("relations")
    Set<CurseRelation> curseRelations

    void relations(Closure<?> closure) {
        if (curseRelations == null) {
            curseRelations = new HashSet<>()
        }
        CurseRelation relation = new CurseRelation()
        ConfigureUtil.configure(closure, relation)
        curseRelations.add(relation)
    }

    /**
     * Validate this artifact
     */
    void validate() {
        check(artifact != null, "artifact not configured")
        check(changelog != null, "changelog not set")
        check(releaseType != null, "releaseType not set")
        check(CurseGradlePlugin.VALID_RELEASE_TYPES.contains(releaseType), "$releaseType is not a valid release type. Valid options are: $CurseGradlePlugin.VALID_RELEASE_TYPES")
        curseRelations.each { it.validate() }
    }

    /**
     * Resolve metadata into their final values
     */
    void resolve(Project project) {
        changelog = Util.resolveString(changelog)
        displayName = Util.resolveString(displayName)
        releaseType = Util.resolveString(releaseType)
        artifact = Util.resolveFile(project, artifact)
    }

    @Override
    public String toString() {
        return "CurseArtifact{" +
               "artifact=" + artifact +
               ", changelog=" + changelog +
               ", displayName=" + displayName +
               ", releaseType='" + releaseType + '\'' +
               ", gameVersionStrings=" + gameVersionStrings +
               '}';
    }
}
