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

package net.llvg.exec.config

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.SubConfig
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.config.data.ModType
import net.llvg.exec.config.freecam.FreeCamConfig
import net.llvg.exec.utils.loggerTypeNamed

object ExeClientConfig : Config(Mod("Exe Client", ModType.SKYBLOCK), "exec-config.json", false) {
        @Transient
        val logger = loggerTypeNamed<ExeClientConfig>()
        
        @SubConfig
        @Suppress("UNUSED")
        val configFreeCamera = FreeCamConfig
        
        init {
                logger.info("Begin initializing config")
                initialize()
                
                ExeClientConfig::class.java.declaredFields.forEach {
                        (it.get(this) as? ExeFeatureConfig)?.initialize()
                }
                
                logger.info("Finish initializing config")
        }
        
        fun active(
        ): Boolean = enabled
}