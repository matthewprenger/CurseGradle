package com.matthewprenger.cursegradle.jsonresponse

import org.gradle.api.Nullable

/**
 * A game version
 */
class GameVersion {

    /**
     * The unique ID
     */
    int id

    /**
     * Optional game dependency ID. Currently this is only used for Java versions. *shrug*
     */
    @Nullable
    Integer gameDependencyID

    /**
     * A friendly name
     */
    String name

    /**
     * The unique slug
     */
    String slug
}
