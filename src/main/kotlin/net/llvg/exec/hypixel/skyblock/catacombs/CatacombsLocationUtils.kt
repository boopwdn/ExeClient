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

@file:JvmName("CatacombsLocationUtils")

package net.llvg.exec.hypixel.skyblock.catacombs

import net.llvg.exec.hypixel.skyblock.isInCatacombs
import net.llvg.exec.vanilla.utils.mc

val isInCatacombsBoss: Boolean
        get() = isInCatacombs && mc.thePlayer?.run {
                when (CatacombsInfo.floor.floor) {
                        1       -> posX > -71 && posZ > -39
                        2, 3, 4 -> posX > -39 && posZ > -39
                        5, 6    -> posX > -39 && posZ > -7
                        7       -> posX > -7 && posZ > -7
                        else    -> false
                }
        } ?: false