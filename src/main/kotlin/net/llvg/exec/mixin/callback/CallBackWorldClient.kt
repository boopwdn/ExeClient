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

@file:JvmName("CallBackWorldClient")

package net.llvg.exec.mixin.callback

import net.llvg.exec.api.event.postAndCheckCancel
import net.llvg.exec.vanilla.event.WorldClientEvent
import net.minecraft.block.state.IBlockState
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.util.BlockPos

fun postWorldClientEventBlockChangeByServerPre(
        worldClient: WorldClient,
        pos: BlockPos,
        state: IBlockState
): Boolean {
        val event = WorldClientEvent.BlockChangeByServer.Pre.Impl(
                worldClient,
                pos,
                state
        )
        return event.postAndCheckCancel
}