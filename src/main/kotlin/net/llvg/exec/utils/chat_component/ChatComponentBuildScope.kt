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

package net.llvg.exec.utils.chat_component

import net.minecraft.util.ChatComponentText
import net.minecraft.util.ChatStyle
import net.minecraft.util.IChatComponent

object ChatComponentBuildScope {
        val empty: IChatComponent
                get() = ChatComponentEmpty()
        
        @Suppress("UNUSED")
        fun ChatComponentBuildScope.empty(
                configure: ChatStyle.() -> Unit
        ): IChatComponent = empty withChatStyle configure
        
        val space: IChatComponent
                get() = ChatComponentText(" ")
        
        @Suppress("UNUSED")
        fun ChatComponentBuildScope.space(
                configure: ChatStyle.() -> Unit
        ): IChatComponent = space withChatStyle configure
        
        val endl: IChatComponent
                get() = ChatComponentText("\n")
        
        @Suppress("UNUSED")
        fun ChatComponentBuildScope.endl(
                configure: ChatStyle.() -> Unit
        ): IChatComponent = endl withChatStyle configure
        
        fun text(
                text: String
        ): IChatComponent = ChatComponentText(text)
        
        @Suppress("UNUSED")
        fun ChatComponentBuildScope.text(
                text: String,
                configure: ChatStyle.() -> Unit
        ): IChatComponent = text(text) withChatStyle configure
        
        @Suppress("UNUSED", "UnusedReceiverParameter")
        fun ChatComponentBuildScope.combine(
                builder: () -> IChatComponent
        ): IChatComponent = builder()
}