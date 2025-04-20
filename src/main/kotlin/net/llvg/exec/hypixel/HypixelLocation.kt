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

package net.llvg.exec.hypixel

import kotlin.jvm.optionals.getOrNull
import kotlinx.coroutines.Dispatchers
import net.hypixel.data.type.ServerType
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket
import net.llvg.exec.api.event.ExeCEventListenable
import net.llvg.exec.api.event.onEvent
import net.llvg.exec.api.event.post
import net.llvg.exec.hypixel.event.HypixelApiEvent
import net.llvg.exec.hypixel.skyblock.SkyBlockLocation
import net.llvg.exec.vanilla.event.WorldClientEvent

@Suppress("MemberVisibilityCanBePrivate")
object HypixelLocation : ExeCEventListenable {
        var serverName: String? = null
                private set
        
        var serverType: ServerType? = null
                private set
        
        var lobbyName: String? = null
                private set
        
        var mode: String? = null
                private set
        
        var map: String? = null
                private set
        
        fun reset() {
                serverName = null
                serverType = null
                lobbyName = null
                mode = null
                map = null
        }
        
        init {
                onEvent(dispatcher = Dispatchers.Default) { _: WorldClientEvent.Load.Pre ->
                        reset()
                        HypixelApiEvent.LocationSet.Impl.post(false)
                }
                
                HypixelModApiHelper.onPacket { p: ClientboundLocationPacket ->
                        serverName = p.serverName
                        serverType = p.serverType.getOrNull()
                        lobbyName = p.lobbyName.getOrNull()
                        mode = p.mode.getOrNull()
                        map = p.map.getOrNull()
                        
                        HypixelApiEvent.LocationSet.Impl.post(false)
                }
                
                SkyBlockLocation
        }
}