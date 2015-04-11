package com.matthewprenger.cursegradle

import com.google.common.base.Throwables
import com.matthewprenger.cursegradle.jsonresponse.GameVersion
import gnu.trove.map.TObjectIntMap
import gnu.trove.map.hash.TObjectIntHashMap
import gnu.trove.set.TIntSet
import gnu.trove.set.hash.TIntHashSet
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging

class CurseVersions {

    private static final Logger log = Logging.getLogger(CurseVersions)

    private static final TObjectIntMap<String> gameVersions = new TObjectIntHashMap<>()

    /**
     * Load the valid game versions from CurseForge
     * @param apiKey The api key to use to connect to CurseForge
     */
    static void initialize(String apiKey) {
        if (!gameVersions.isEmpty()) {
            return
        }

        log.info 'Initializing CurseForge versions...'

        try {
            String json = Util.httpGet(apiKey, CurseGradlePlugin.VERSION_URL)
            //noinspection GroovyAssignabilityCheck
            GameVersion[] versions = Util.gson.fromJson(json, GameVersion[].class)
            versions.each { version ->
                if (version.gameDependencyID == null) {
                    gameVersions.put(version.name, version.id)
                }
            }

            log.info 'CurseForge versions initialized'
        } catch (Throwable t) {
            throw Throwables.propagate(t)
        }
    }

    static int[] resolveGameVersion(final Iterable<Object> objects) {
        TIntSet set = new TIntHashSet()
        objects.each { obj ->
            final String version = obj.toString()
            int id = gameVersions.get(version)
            if (id == 0) {
                throw new IllegalArgumentException("$version is not a valid game version. Valid versions are: ${gameVersions.keySet()}")
            }
            set.add(id)
        }

        return set.toArray()
    }
}
