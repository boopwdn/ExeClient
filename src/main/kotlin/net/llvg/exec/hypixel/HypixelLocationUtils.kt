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

@file:JvmName("HypixelLocationUtils")

package net.llvg.exec.hypixel

import net.hypixel.data.type.GameType

val isInSkyBlock: Boolean
        get() = HypixelLocation.serverType === GameType.SKYBLOCK

val isInDungeon: Boolean
        get() = isInSkyBlock && HypixelLocation.mode == "dungeon"

val isInCatacombs: Boolean // There's only one type of dungeon called catacombs right now
        get() = isInDungeon