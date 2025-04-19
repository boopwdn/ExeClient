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

package net.llvg.exec.feature.catacombs_scan

import kotlinx.coroutines.Dispatchers
import net.llvg.exec.api.command.ExeCCommandManager
import net.llvg.exec.api.config.ExeClientConfig
import net.llvg.exec.api.event.onEvent
import net.llvg.exec.api.feature.ExeCFeature
import net.llvg.exec.hypixel.isInCatacombs
import net.llvg.exec.hypixel.skyblock.catacombs.map.scan.CatacombsMap

object CatacombsScan : ExeCFeature<CatacombsScanConfig> {
        init {
                onEvent(Dispatchers.Default, always = true) { e: ExeCCommandManager.Event ->
                        e register CatacombsScanCommand
                }
        }
        
        override fun initialize() {
                CatacombsMap
        }
        
        fun checkCatacombs(): Boolean =
                CatacombsScanConfig.onlyInCatacombs && !isInCatacombs
        
        override val config: CatacombsScanConfig
                get() = ExeClientConfig.configCatacombsScan
        
        override val active: Boolean
                get() = config.active
}