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

package net.llvg.exec.vanilla.utils.chat_component

import net.minecraft.util.ChatComponentText
import net.minecraft.util.ChatStyle
import net.minecraft.util.IChatComponent

object ChatComponentBuildScope {
        val empty: IChatComponent
                get() = ChatComponentEmpty()
        
        @Suppress("UNUSED")
        fun empty(
                configure: ChatStyle.() -> Unit = {}
        ): IChatComponent =
                empty withChatStyle configure
        
        infix operator fun IChatComponent.plus(
                o: String
        ): IChatComponent =
                plus { it.appendSibling(o()) }
        
        infix operator fun IChatComponent.plus(
                o: IChatComponent
        ): IChatComponent =
                plus { it.appendSibling(o) }
        
        inline infix operator fun IChatComponent.plus(
                configure: (IChatComponent) -> Unit
        ): IChatComponent =
                apply(configure)
        
        operator fun String.invoke(): IChatComponent =
                ChatComponentText(this)
        
        inline infix operator fun String.invoke(
                configure: ChatStyle.() -> Unit
        ): IChatComponent =
                ChatComponentText(this) withChatStyle configure
}