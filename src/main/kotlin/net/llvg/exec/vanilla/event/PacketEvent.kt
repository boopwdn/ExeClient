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

package net.llvg.exec.vanilla.event

import net.llvg.exec.api.event.ExeCEvent
import net.llvg.exec.api.event.ExeCEventCancellable
import net.minecraft.network.INetHandler
import net.minecraft.network.Packet
import net.minecraft.network.play.INetHandlerPlayClient
import net.minecraft.network.play.server.S22PacketMultiBlockChange
import net.minecraft.network.play.server.S23PacketBlockChange
import net.minecraft.network.play.server.S43PacketCamera

interface PacketEvent : ExeCEvent {
        val handler: INetHandler
        
        val packet: Packet<out INetHandler>
        
        interface Server : PacketEvent {
                override val handler: INetHandlerPlayClient
                
                override val packet: Packet<INetHandlerPlayClient>
                
                interface S22 : Server {
                        override val packet: S22PacketMultiBlockChange
                        
                        interface Pre : S22, ExeCEventCancellable {
                                data class Impl(
                                        override val handler: INetHandlerPlayClient,
                                        override val packet: S22PacketMultiBlockChange
                                ) : ExeCEventCancellable.Impl(), Pre
                        }
                }
                
                interface S23 : Server {
                        override val packet: S23PacketBlockChange
                        
                        interface Pre : S23, ExeCEventCancellable {
                                data class Impl(
                                        override val handler: INetHandlerPlayClient,
                                        override val packet: S23PacketBlockChange
                                ) : ExeCEventCancellable.Impl(), Pre
                        }
                }
                
                interface S43 : Server {
                        override val packet: S43PacketCamera
                        
                        interface Pre : S43, ExeCEventCancellable {
                                data class Impl(
                                        override val handler: INetHandlerPlayClient,
                                        override val packet: S43PacketCamera
                                ) : ExeCEventCancellable.Impl(), Pre
                        }
                }
        }
}