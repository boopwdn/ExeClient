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
import net.minecraft.block.state.IBlockState
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.util.BlockPos

interface WorldClientEvent : ExeCEvent {
        val worldClient: WorldClient?
        
        operator fun component1(): WorldClient?
        
        interface Load : WorldClientEvent {
                interface Pre : Load {
                        data class Impl(
                                override val worldClient: WorldClient?
                        ) : Pre
                }
        }
        
        interface BlockChangeByServer : WorldClientEvent {
                override val worldClient: WorldClient
                
                val pos: BlockPos
                
                val state: IBlockState
                
                override fun component1(): WorldClient
                operator fun component2(): BlockPos
                operator fun component3(): IBlockState
                
                interface Pre : BlockChangeByServer, ExeCEventCancellable {
                        data class Impl(
                                override val worldClient: WorldClient,
                                override val pos: BlockPos,
                                override val state: IBlockState
                        ) : ExeCEventCancellable.Impl(), Pre
                }
        }
}