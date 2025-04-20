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

package net.llvg.exec.hypixel.skyblock.catacombs

import kotlinx.coroutines.Dispatchers
import net.llvg.exec.api.event.ExeCEventListenable
import net.llvg.exec.api.event.onEvent
import net.llvg.exec.hypixel.skyblock.SkyBlockLocationEvent
import net.llvg.exec.hypixel.skyblock.catacombs.map.RoomData
import net.llvg.exec.hypixel.skyblock.catacombs.map.scan.CatacombsMap
import net.llvg.exec.hypixel.skyblock.isInCatacombs
import net.llvg.exec.vanilla.event.WorldClientEvent

object CatacombsInfo : ExeCEventListenable {
        var floor: Floor = Floor.UNKNOWN
                private set
        
        override val active: Boolean
                get() = isInCatacombs
        
        init {
                onEvent(Dispatchers.Default, always = true) { _: WorldClientEvent.Load.Pre ->
                        floor = Floor.UNKNOWN
                }
                
                onEvent(Dispatchers.Default) { e: SkyBlockLocationEvent ->
                        if (floor !== Floor.UNKNOWN) return@onEvent
                        
                        if (!e.newLocationName.startsWith("The Catacombs")) return@onEvent
                        
                        if (e.newLocationName.length <= 16) return@onEvent
                        
                        floor = when (e.newLocationName[15]) {
                                'E'  -> Floor.E
                                'F'  -> Floor.floorF[e.newLocationName[16].digitToInt() - 1]
                                'M'  -> Floor.floorM[e.newLocationName[16].digitToInt() - 1]
                                else -> Floor.UNKNOWN
                        }
                }
                
                CatacombsMap
                RoomData
        }
}