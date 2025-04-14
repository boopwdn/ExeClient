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

package net.llvg.exec.vanilla.utils.chat_component

import net.minecraft.util.EnumChatFormatting

@Suppress("UNUSED")
enum class ChatColor(
        val format: EnumChatFormatting
) {
        BLACK(EnumChatFormatting.BLACK),
        DARK_BLUE(EnumChatFormatting.DARK_BLUE),
        DARK_GREEN(EnumChatFormatting.DARK_GREEN),
        DARK_AQUA(EnumChatFormatting.DARK_AQUA),
        DARK_RED(EnumChatFormatting.DARK_RED),
        DARK_PURPLE(EnumChatFormatting.DARK_PURPLE),
        GOLD(EnumChatFormatting.GOLD),
        GRAY(EnumChatFormatting.GRAY),
        DARK_GRAY(EnumChatFormatting.DARK_GRAY),
        BLUE(EnumChatFormatting.BLUE),
        GREEN(EnumChatFormatting.GREEN),
        AQUA(EnumChatFormatting.AQUA),
        RED(EnumChatFormatting.RED),
        LIGHT_PURPLE(EnumChatFormatting.LIGHT_PURPLE),
        YELLOW(EnumChatFormatting.YELLOW),
        WHITE(EnumChatFormatting.WHITE),
        RESET(EnumChatFormatting.RESET)
}