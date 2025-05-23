/*
 * Copyright (C) 2025-2025 Water-OR
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

package net.llvg.exec.api.config

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.SubConfig
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.config.data.ModType
import net.llvg.exec.feature.catacombs_scan.CatacombsScanConfig
import net.llvg.exec.feature.freecam.FreeCamConfig
import net.llvg.exec.utils.classNameLogger

object ExeClientConfig : Config(
        Mod(
                "Exe Client",
                ModType.SKYBLOCK
        ),
        "exec-config.json",
        false
) {
        @Transient
        private val logger = classNameLogger<ExeClientConfig>()
        
        @SubConfig
        val configCatacombsScan =
                CatacombsScanConfig
        
        @SubConfig
        val configFreeCamera =
                FreeCamConfig
        
        init {
                logger.info("Begin initializing config")
                initialize()

                initializeExeCFeatureConfigs(this)
                
                logger.info("Finish initializing config")
        }
        
        fun active(
        ): Boolean = enabled
}