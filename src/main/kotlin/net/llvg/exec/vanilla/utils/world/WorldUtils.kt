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

@file:JvmName("WorldUtils")

package net.llvg.exec.vanilla.utils.world

import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockPos
import net.minecraft.world.World

@Suppress("UNUSED")
fun World.getActualBlockState(
        pos: BlockPos
): IBlockState =
        getBlockState(pos).let { it.block.getActualState(it, this, pos) }

@Synchronized
fun World.setBlockStateWithTile(
        pos: BlockPos,
        state: IBlockState,
        tile: TileEntity? = state.block.createTileEntity(this, state)
) {
        setBlockState(pos, state)
        removeTileEntity(pos)
        
        setTileEntity(pos, tile ?: return)
}