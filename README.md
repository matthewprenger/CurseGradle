# CurseGradle

[![Build Status](https://travis-ci.org/matthewprenger/CurseGradle.svg?branch=master)](https://travis-ci.org/matthewprenger/CurseGradle)
[![Project Status](http://stillmaintained.com/matthewprenger/CurseGradle.png)](https://stillmaintained.com/matthewprenger/CurseGradle)

A gradle plugin for publishing artifacts to [CurseForge](http://minecraft.curseforge.com/).

* IRC: `#matthewprenger` on [EsperNet](http://esper.net/)
* Documentation: [Wiki](https://github.com/matthewprenger/CurseGradle/wiki/)

## Simple Quickstart with ForgeGradle
If you're using ForgeGradle, which you probably are, the following script is a bare-minimum. For more details about customization, check out the [Wiki](https://github.com/matthewprenger/CurseGradle/wiki).

`build.gradle`
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
  id 'com.matthewprenger.curseforge' version '1.0.0'
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
