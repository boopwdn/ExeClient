@file:Suppress("PropertyName", "SpellCheckingInspection")

import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.utils.extendsFrom
import org.polyfrost.gradle.util.noServerRunConfigs


/*
* This file was generated by the Gradle 'init' task.
*
* This generated file contains a sample Kotlin library project to get you started.
* For more details on building Java & JVM projects, please refer to https://docs.gradle.org/8.12.1/userguide/building_java_projects.html in the Gradle documentation.
* This project uses @Incubating APIs which are subject to change.
*/

plugins {
        kotlin("jvm") version libz.versions.kotlin
        id("org.polyfrost.multi-version")
        id("org.polyfrost.defaults.repo")
        id("org.polyfrost.defaults.java")
        id("org.polyfrost.defaults.loom")
        alias(libz.plugins.shadow)
        `java-library`
}

val mod_version: String by properties
val mod_group: String by properties
val mod_name: String by properties
val mixin_id: String by properties

version = mod_version
group = mod_group
base.archivesName = "$mod_name-$platform"

loom {
        noServerRunConfigs()
        
        if (project.platform.isLegacyForge) runConfigs {
                "client" {
                        programArgs("--tweakClass", "net.llvg.exec.preload.ExeCTweaker")
                }
        }
        
        runConfigs.configureEach {
                property("mixin.debug.export", "true")
        }
        
        if (project.platform.isForge) forge {
                mixinConfig("mixin.$mixin_id.json")
        }
        
        @Suppress("UnstableApiUsage")
        mixin.defaultRefmapName = "refmap.$mixin_id.json"
}

val shade by configurations.registering
val modShade by configurations.registering

configurations {
        implementation.extendsFrom(shade)
        modImplementation.extendsFrom(modShade)
}

sourceSets.main {
        output.setResourcesDir(java.classesDirectory)
}

repositories {
        mavenLocal()
        mavenCentral()
        maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1") {
                name = "DevAuth Repo"
                content {
                        includeGroup("me.djtheredstoner")
                }
        }
        maven("https://maven.cleanroommc.com") {
                name = "Cleanroom Maven"
        }
        maven("https://repo.polyfrost.org/releases") {
                name = "Polyfrost Maven"
        }
        maven("https://cursemaven.com") {
                name = "Curse Maven"
                content {
                        includeGroup("curse.maven")
                }
        }
        maven("https://jitpack.io") {
                name = "Jitpack Maven"
        }
}

@Suppress("UnstableApiUsage")
dependencies {
        modCompileOnly("cc.polyfrost:oneconfig-$platform:" + properties["version_OneConfig"])
        
        val artifactDevAuth = "DevAuth-" + when {
                platform.isModernFabric -> "fabric"
                platform.isLegacyForge  -> "forge-legacy"
                platform.isForge        -> "forge-latest"
                platform.isNeoForge     -> "neoforge"
                else                    -> throw IllegalArgumentException("Unsupport platform $platform")
        }
        
        modRuntimeOnly("me.djtheredstoner:$artifactDevAuth:" + properties["version_DevAuth"])
        
        implementation("com.github.boopwdn:YqlossClientMixin:v0.7.0:dev")
        shade("com.github.Water-OR:llvg-utils:1.0")
        
        if (platform.isLegacyForge) {
                compileOnly("org.spongepowered:mixin:0.7.11-SNAPSHOT")
                shade("cc.polyfrost:oneconfig-wrapper-launchwrapper:1.0.0-beta17")
        }
}

java {
        withSourcesJar()
        withJavadocJar()
        
        toolchain {
                languageVersion = JavaLanguageVersion.of(8)
                vendor = JvmVendorSpec.AZUL
        }
}

kotlin {
        compilerOptions {
                jvmTarget = JvmTarget.JVM_1_8
                
                languageVersion = KotlinVersion.KOTLIN_2_0
                apiVersion = KotlinVersion.KOTLIN_2_0
        }
}

tasks {
        processResources {
                filesMatching("mixin.$mixin_id.json") {
                        expand(mapOf(
                                "refmap" to "refmap.$mixin_id.json",
                        ))
                }
        }
        
        shadowJar {
                archiveClassifier = "dev"
                configurations = listOf(shade.get(), modShade.get())
                duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        }
        
        remapJar {
                inputFile = shadowJar.get().archiveFile
                archiveClassifier = ""
        }
        
        jar {
                if (platform.isLegacyForge) {
                        val attr: MutableMap<String, Any> = HashMap()
                        attr["ModSide"] = "CLIENT"
                        attr["ForceLoadAsMod"] = true
                        attr["TweakClass"] = "net.llvg.exec.preload.ExeCTweaker"
                        attr["TweakOrder"] = 0
                        attr["MixinConfigs"] = "mixin.$mixin_id.json"
                        manifest.attributes += attr
                }
                
                dependsOn(shadowJar)
                archiveClassifier = ""
                enabled = false
        }
}
