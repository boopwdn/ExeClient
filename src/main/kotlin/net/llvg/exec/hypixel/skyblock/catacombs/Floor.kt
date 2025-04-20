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

enum class Floor {
        UNKNOWN,
        E,
        F1, F2, F3, F4, F5, F6, F7,
        M1, M2, M3, M4, M5, M6, M7;
        
        val isMasterMode: Boolean = name.startsWith('M')
        
        val floor: Int =
                when (name[0]) {
                        'E'      -> 0
                        'F', 'M' -> name[1].digitToInt()
                        else     -> -1
                }
        
        companion object {
                @JvmField
                val floorF = arrayOf(F1, F2, F3, F4, F5, F6, F7)
                
                @JvmField
                val floorM = arrayOf(M1, M2, M3, M4, M5, M6, M7)
        }
}