/*
 * Copyright (C) 2025 Water-OR
 *
 * This file is part of ExeClient
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package net.llvg.exec.preload.vanilla_tweaker

import cc.polyfrost.oneconfig.loader.stage0.LaunchWrapperTweaker
import java.io.File
import net.llvg.exec.utils.classNameLogger
import net.llvg.exec.utils.delegate.InitOnceRef
import net.llvg.loliutils.delegate.getValue
import net.llvg.loliutils.delegate.setValue
import net.llvg.loliutils.exception.cast
import net.minecraft.launchwrapper.ITweaker
import net.minecraft.launchwrapper.Launch
import net.minecraft.launchwrapper.LaunchClassLoader
import org.spongepowered.asm.launch.MixinBootstrap

@Suppress("UNUSED")
class ExeCTweaker : ITweaker {
        override fun acceptOptions(
                args: MutableList<String>?,
                gameDir: File?,
                assetsDir: File?,
                profile: String?
        ) {
                internalGameDir = gameDir ?: File(".").absoluteFile
        }
        
        override fun injectIntoClassLoader(
                classLoader: LaunchClassLoader
        ) {
                val tweakClasses: MutableList<String> = cast(Launch.blackboard["TweakClasses"])
                tweakClasses += LaunchWrapperTweaker::class.java.name
                tweakClasses += "org.spongepowered.asm.launch.MixinTweaker"
        }
        
        override fun getLaunchTarget(): String =
                throw IllegalStateException("You should not be here!")
        
        override fun getLaunchArguments(): Array<String> {
                addMixinConfig()
                return arrayOf()
        }
        
        private fun addMixinConfig() {
                val location = try {
                        File(javaClass.protectionDomain.codeSource.location.toURI())
                } catch (e: Throwable) {
                        logger.error("Failed to find mod location", e)
                        return
                }
                
                logger.info("Found mod location at {}", location.absolutePath)
                
                if (!location.isFile) {
                        logger.warn("Skip adding mixin config since mod location is not a file")
                        return
                }
                
                MixinBootstrap.getPlatform().addContainer(location.toURI())
        }
        
        companion object {
                @JvmStatic
                val gameDir: File
                        get() = internalGameDir
        }
}

private var internalGameDir by InitOnceRef<File>()

private val logger = classNameLogger<ExeCTweaker>()