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

package net.llvg.exec.hypixel.skyblock

import kotlinx.coroutines.Dispatchers
import net.llvg.exec.api.event.ExeCEventListenable
import net.llvg.exec.api.event.onEvent
import net.llvg.exec.api.event.post
import net.llvg.exec.hypixel.isInSkyBlock
import net.llvg.exec.hypixel.skyblock.catacombs.CatacombsInfo
import net.llvg.exec.vanilla.event.TickEvent
import net.llvg.exec.vanilla.event.WorldClientEvent
import net.llvg.exec.vanilla.utils.scoreboard.ScoreboardFlags.FLAG_NO_MC_FORMAT
import net.llvg.exec.vanilla.utils.scoreboard.ScoreboardFlags.FLAG_NO_STUPID_CHAR
import net.llvg.exec.vanilla.utils.scoreboard.getScoreboardLines

object SkyBlockLocation : ExeCEventListenable {
        @get:JvmStatic
        var locationName: String = ""
                private set
        
        private var tickCounter = 0
        
        override val active: Boolean
                get() = isInSkyBlock
        
        init {
                onEvent(Dispatchers.Default, always = true) { _: WorldClientEvent.Load.Pre ->
                        tickCounter = 0
                        if (locationName.isNotEmpty()) {
                                SkyBlockLocationEvent.create("").post(true)
                                locationName = ""
                        }
                }
                
                onEvent(Dispatchers.Default) { _: TickEvent.Client.Pre ->
                        tickCounter = (tickCounter + 1) % 20
                        if (tickCounter == 0) {
                                var newLocationName: String? = null
                        
                                for (line in getScoreboardLines(FLAG_NO_MC_FORMAT + FLAG_NO_STUPID_CHAR)) {
                                        if (!line.startsWith(SKY_BLOCK_LOCATION_PREFIX)) continue
                                        newLocationName = line.substring(3)
                                        break
                                }
                                
                                if (newLocationName !== null && newLocationName != locationName) {
                                        SkyBlockLocationEvent.create(newLocationName).post(true)
                                        locationName = newLocationName
                                }
                        }
                }
                
                CatacombsInfo
        }
}