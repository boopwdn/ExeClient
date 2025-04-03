/*
 * Copyright (C) 2025-2025 Water-OR
 *
 * This file is part of ExeClient-temp1
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

package net.llvg.exec.utils.chat_component

import net.minecraft.util.ChatStyle
import net.minecraft.util.IChatComponent

@Suppress("UNUSED")
inline fun buildChatComponent(
        action: ChatComponentBuildScope.() -> IChatComponent
): IChatComponent = ChatComponentBuildScope.action()

inline fun <C : IChatComponent> C.withChatStyle(
        chatStyle: ChatStyle,
        configure: ChatStyle.() -> Unit
): C = apply { this.chatStyle = chatStyle.apply(configure) }

inline infix fun <C : IChatComponent> C.withChatStyle(
        configure: ChatStyle.() -> Unit
): C = withChatStyle(chatStyle, configure)

@Suppress("UNUSED")
context(scope: ChatComponentBuildScope)
fun empty(
        configure: ChatStyle.() -> Unit
): IChatComponent = with(scope) {
        empty withChatStyle configure
}

@Suppress("UNUSED")
context(scope: ChatComponentBuildScope)
fun space(
        configure: ChatStyle.() -> Unit
): IChatComponent = with(scope) {
        space withChatStyle configure
}

@Suppress("UNUSED")
context(scope: ChatComponentBuildScope)
fun endl(
        configure: ChatStyle.() -> Unit
): IChatComponent = with(scope) {
        endl withChatStyle configure
}

@Suppress("UNUSED")
context(scope: ChatComponentBuildScope)
fun text(
        text: String,
        configure: ChatStyle.() -> Unit
): IChatComponent = with(scope) {
        text(text) withChatStyle configure
}

@Suppress("UNUSED")
context(scope: ChatComponentBuildScope)
fun combine(
        action: () -> IChatComponent
): IChatComponent = action()