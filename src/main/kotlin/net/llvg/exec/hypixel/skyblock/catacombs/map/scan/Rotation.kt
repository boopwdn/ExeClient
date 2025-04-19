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

package net.llvg.exec.hypixel.skyblock.catacombs.map.scan

import net.llvg.exec.utils.vector.Vec2I

enum class Rotation(
        x: Int,
        y: Int
) {
        UNKNOWN(0, 0),
        NORTH(+1, +1),
        SOUTH(-1, -1),
        EAST(+1, -1),
        WEST(-1, +1);
        
        val offset = Vec2I(x, y)
        
        companion object {
                @JvmField
                val directions: Array<Rotation> =
                        arrayOf(NORTH, SOUTH, EAST, WEST)
        }
}