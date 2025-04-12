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

@file:JvmName("ChatComponentUtils")

package net.llvg.exec.vanilla.utils.chat_component

import net.minecraft.util.ChatStyle
import net.minecraft.util.IChatComponent

@Suppress("UNUSED")
inline fun buildChat(
        builder: ChatComponentBuildScope.() -> IChatComponent
): IChatComponent =
        ChatComponentBuildScope.builder()

inline fun IChatComponent.withChatStyle(
        chatStyle: ChatStyle,
        configure: ChatStyle.() -> Unit
): IChatComponent =
        apply { this.chatStyle = chatStyle.apply(configure) }

inline infix fun IChatComponent.withChatStyle(
        configure: ChatStyle.() -> Unit
): IChatComponent =
        withChatStyle(chatStyle, configure)