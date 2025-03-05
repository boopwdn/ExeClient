@file:Suppress("PropertyName", "SpellCheckingInspection")

/*
* This file was generated by the Gradle 'init' task.
*
* The settings file is used to specify which projects to include in your build.
* For more detailed information on multi-project builds, please refer to https://docs.gradle.org/8.12.1/userguide/multi_project_builds.html in the Gradle documentation.
* This project uses @Incubating APIs which are subject to change.
*/

pluginManagement {
        repositories {
                mavenLocal()
                mavenCentral()
                gradlePluginPortal()
                maven("https://repo.polyfrost.org/releases") {
                        name = "PolyFrost Repo"
                }
        }
        plugins {
                id("org.polyfrost.multi-version.root") version "0.6.7"
        }
}

plugins {
        // Apply the foojay-resolver plugin to allow automatic download of JDKs
        id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

val mod_name: String by settings
rootProject.name = mod_name

val version_kotlin: String by settings
val version_shadow: String by settings
val version_idea_ext: String by settings
val version_kotlinx_coroutines: String by settings
val version_kotlinx_serialization: String by settings

@Suppress("Unused")
val libz by dependencyResolutionManagement.versionCatalogs.registering {
        version("kotlin", version_kotlin)
        version("shadow", version_shadow)
        version("idea-ext", version_idea_ext)
        version("kotlinx-coroutines", version_kotlinx_coroutines)
        version("kotlinx-serialization", version_kotlinx_serialization)
        
        plugin("idea-ext", "org.jetbrains.gradle.plugin.idea-ext").versionRef("idea-ext")
        plugin("shadow", "com.github.johnrengelman.shadow").versionRef("shadow")
        
        val kotlin = "org.jetbrains.kotlin"
        library("kotlin-stdlib", kotlin, "kotlin-stdlib").versionRef("kotlin")
        library("kotlin-reflect", kotlin, "kotlin-reflect").versionRef("kotlin")
        library("kotlin-stdlib-jdk7", kotlin, "kotlin-stdlib-jdk7").versionRef("kotlin")
        library("kotlin-stdlib-jdk8", kotlin, "kotlin-stdlib-jdk8").versionRef("kotlin")
        bundle("kotlin", listOf(
                "kotlin-stdlib",
                "kotlin-reflect",
                "kotlin-stdlib-jdk7",
                "kotlin-stdlib-jdk8",
        ))
        
        val kotlinx = "org.jetbrains.kotlinx"
        library("kotlinx-coroutines-core", kotlinx, "kotlinx-coroutines-core").versionRef("kotlinx-coroutines")
        library("kotlinx-coroutines-test", kotlinx, "kotlinx-coroutines-test").versionRef("kotlinx-coroutines")
        library("kotlinx-serialization-cbor-jvm", kotlinx, "kotlinx-serialization-cbor-jvm")
        .versionRef("kotlinx-serialization")
        library("kotlinx-serialization-core-jvm", kotlinx, "kotlinx-serialization-core-jvm")
        .versionRef("kotlinx-serialization")
        library("kotlinx-serialization-json-jvm", kotlinx, "kotlinx-serialization-json-jvm")
        .versionRef("kotlinx-serialization")
        bundle("kotlinx", listOf(
                "kotlinx-coroutines-core",
                "kotlinx-coroutines-test",
                "kotlinx-serialization-cbor-jvm",
                "kotlinx-serialization-core-jvm",
                "kotlinx-serialization-json-jvm"
        ))
}

arrayOf(
        "1.8.9-forge"
).forEach {
        include(":$it")
        project(":$it").apply {
                projectDir = file("versions/$it")
                buildFileName = "../../build-mod.gradle.kts"
        }
}