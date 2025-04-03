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
import net.minecraft.util.IChatComponent

object ChatComponentBuildScope {
        val empty: IChatComponent
                get() = ChatComponentEmpty()
        
        val space: IChatComponent
                get() = ChatComponentText(" ")
        
        val endl: IChatComponent
                get() = ChatComponentText("\n")
        
        fun text(
                text: String
        ): IChatComponent = ChatComponentText(text)
        
        infix operator fun <C : IChatComponent> C.plus(
                o: IChatComponent
        ): C = apply { appendSibling(o) }
        
        inline infix operator fun <C : IChatComponent> C.plus(
                configure: (C) -> Unit
        ): C = apply(configure)
}