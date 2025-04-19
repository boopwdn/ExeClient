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

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import net.hypixel.modapi.HypixelModAPI
import net.hypixel.modapi.packet.EventPacket
import net.llvg.exec.api.event.ExeCEventListenable
import net.llvg.exec.api.event.onEvent
import net.llvg.exec.vanilla.event.GameStartEvent
import net.llvg.loliutils.exception.cast

@Suppress("MemberVisibilityCanBePrivate")
object HypixelModApiHelper : ExeCEventListenable {
        val hypixelModAPI: HypixelModAPI = HypixelModAPI.getInstance()
        
        private val eventPacketListeners: MutableMap<Class<out EventPacket>, MutableList<(EventPacket) -> Unit>> =
                HashMap()
        
        init {
                HypixelLocation
                onEvent(Dispatchers.Default) { _: GameStartEvent.Post ->
                        val jobs = mutableListOf<Job>()
                        for ((type, listeners) in eventPacketListeners) jobs += launch {
                                hypixelModAPI.subscribeToEventPacket(type)
                                listeners.forEach {
                                        hypixelModAPI.createHandler(type, it)
                                }
                        }
                        jobs.joinAll()
                }
        }
        
        fun <P : EventPacket> onPacket(
                type: Class<P>,
                action: (P) -> Unit
        ) {
                val listeners = eventPacketListeners[type] ?: synchronized(eventPacketListeners) {
                        eventPacketListeners.getOrPut(type) { ArrayList() }
                }
                
                listeners += { action(cast(it)) }
        }
        
        inline fun <reified P : EventPacket> onPacket(
                noinline action: (P) -> Unit
        ) {
                onPacket(P::class.java, action)
        }
}