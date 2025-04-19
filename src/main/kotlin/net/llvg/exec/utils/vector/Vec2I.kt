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

package net.llvg.exec.utils.vector

import kotlin.math.max
import kotlin.math.min

@Suppress("UNUSED")
data class Vec2I(
        val x: Int,
        val y: Int
) {
        operator fun unaryPlus(): Vec2I =
                if (this == ZERO) ZERO else this
        
        operator fun unaryMinus(): Vec2I =
                if (this == ZERO) ZERO else Vec2I(-x, -y)
        
        infix operator fun plus(
                o: Vec2I
        ): Vec2I =
                if (o == ZERO) this else if (this == ZERO) o else {
                        Vec2I(x + o.x, y + o.y)
                }
        
        infix operator fun minus(
                o: Vec2I
        ): Vec2I =
                if (o == ZERO) this else if (this == ZERO) -o else {
                        Vec2I(x - o.x, y - o.y)
                }
        
        infix fun min(
                o: Vec2I
        ): Vec2I =
                +Vec2I(
                        min(x, o.x),
                        min(y, o.y)
                )
        
        infix fun max(
                o: Vec2I
        ): Vec2I =
                +Vec2I(
                        max(x, o.x),
                        max(y, o.y)
                )
        
        infix operator fun times(
                o: Int
        ): Vec2I =
                if (this == ZERO || o == 0) ZERO else {
                        Vec2I(x * o, y * o)
                }
        
        companion object {
                @JvmField
                val ZERO = Vec2I(0, 0)
        }
}
