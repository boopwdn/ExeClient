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

package net.llvg.exec.utils.skyblock.catacombs.map.scan

import net.llvg.exec.utils.skyblock.catacombs.map.RoomData
import net.llvg.exec.utils.vector.Vec2I

@Suppress("MemberVisibilityCanBePrivate")
object CatacombsMap {
        const val BEGIN = -185
        
        @JvmStatic
        val roomEntries = Array(36) {
                Vec2I(
                        BEGIN + (it % 6 shl 5),
                        BEGIN + (it / 6 shl 5)
                ).let(::RoomEntry)
        }
        
        fun roomEntryAtPos(
                x: Int,
                z: Int
        ): RoomEntry? =
                roomEntryAt(
                        (x - BEGIN shr 4) + 1 shr 1,
                        (z - BEGIN shr 4) + 1 shr 1
                )
        
        fun roomEntryAt(
                x: Int,
                z: Int
        ): RoomEntry? =
                if (x in 0..5 && z in 0..5) {
                        roomEntries[x + 6 * z]
                } else null
        
        @JvmStatic
        val roomInfoSet: MutableSet<RoomInfo> = HashSet()
        
        init {
                RoomData
                Rotation
                CatacombsScanner
        }
        
        @JvmStatic
        fun reset() {
                for (i in roomEntries) {
                        i.info = null
                }
                roomInfoSet.clear()
        }
}