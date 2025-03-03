import org.jetbrains.gradle.ext.Gradle
import org.jetbrains.gradle.ext.runConfigurations
import org.jetbrains.gradle.ext.settings

plugins {
        kotlin("jvm") version libz.versions.kotlin apply false
        id("org.polyfrost.multi-version.root")
        alias(libz.plugins.shadow) apply false
        alias(libz.plugins.idea.ext)
}

preprocess {
        "1.8.9-forge"(10809, "srg")
}

idea {
        module {
                isDownloadSources = true
                isDownloadJavadoc = true
        }
        
        project.settings {
                @Suppress("LocalVariableName")
                runConfigurations {
                        val `Setup Env` by registering(Gradle::class) {
                                taskNames = listOf(
                                        "genIdeaWorkspace",
                                        "idea",
                                        "genSources"
                                )
                        }
                        
                        val `Build Mod` by registering(Gradle::class) {
                                taskNames = listOf(
                                        "build"
                                )
                        }
                }
        }
}