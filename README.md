# CurseGradle

[![Build Status](https://travis-ci.org/matthewprenger/CurseGradle.svg?branch=master)](https://travis-ci.org/matthewprenger/CurseGradle)
[![Project Status](http://stillmaintained.com/matthewprenger/CurseGradle.png)](https://stillmaintained.com/matthewprenger/CurseGradle)

A gradle plugin for publishing artifacts to [CurseForge](http://minecraft.curseforge.com/).

* IRC: `#matthewprenger` on [EsperNet](http://esper.net/)
* Documentation: [Wiki](https://github.com/matthewprenger/CurseGradle/wiki/)

## Simple Quickstart with ForgeGradle
If you're using ForgeGradle, which you probably are, the following script is a bare-minimum. For more details about customization, check out the [Wiki](https://github.com/matthewprenger/CurseGradle/wiki).

To find out which versions are available, check [HERE](https://plugins.gradle.org/plugin/com.matthewprenger.cursegradle).

```gradle
buildscript {
    repositories {
        mavenCentral()
        maven { url = "http://files.minecraftforge.net/maven" }
        maven { url = "https://oss.sonatype.org/content/repositories/snapshots/" }
    }
    dependencies { classpath 'net.minecraftforge.gradle:ForgeGradle:1.2-SNAPSHOT' }
}

plugins {
  id 'com.matthewprenger.cursegradle' version '<VERSION>'
}

apply plugin: 'forge'

curseforge {
  apiKey = '123-456'
  project {
    id = '12345'
    changelog = 'Changes' // A file can also be set using: changelog = file('changelog.txt')
    releaseType = 'beta'
  }
}
```

## Snapshots
If you want to test the latest and greatest version, you can use the snapshot builds, but **be warned**: they may be unstable! Your buildscript needs the following entries instead of the `plugins { }` block.

```gradle
buildscript {
    repositories {
        jcenter()
        maven { url = 'https://oss.sonatype.org/content/groups/public' }
    }
    dependencies {
        classpath 'com.matthewprenger:CurseGradle:<VERSION>-SNAPSHOT'
    }
}

apply plugin: 'com.matthewprenger.cursegradle'
```
