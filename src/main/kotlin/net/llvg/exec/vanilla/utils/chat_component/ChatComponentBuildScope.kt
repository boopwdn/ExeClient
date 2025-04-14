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

@Suppress("UNUSED", "FunctionName")
object ChatComponentBuildScope {
        fun empty(): IChatComponent =
                ChatComponentEmpty()
        
        fun of(
                component: IChatComponent
        ): IChatComponent =
                component
        
        infix operator fun IChatComponent.rangeTo(
                o: String
        ): IChatComponent =
                rangeTo { it.appendSibling(ChatComponentText(o)) }
        
        infix operator fun IChatComponent.rangeTo(
                o: IChatComponent
        ): IChatComponent =
                rangeTo { it.appendSibling(o) }
        
        inline infix operator fun IChatComponent.rangeTo(
                configure: (IChatComponent) -> Unit
        ): IChatComponent =
                apply(configure)
        
        operator fun String.invoke(): IChatComponent =
                ChatComponentText(this)
        
        inline infix operator fun String.invoke(
                configure: ChatStyle.() -> Unit
        ): IChatComponent =
                ChatComponentText(this) style configure
        
        inline infix operator fun IChatComponent.invoke(
                configure: ChatStyle.() -> Unit
        ): IChatComponent =
                style(configure)
        
        infix fun IChatComponent.`--color`(
                color: ChatColor
        ): IChatComponent =
                style {
                        color(color)
                }
        
        infix fun IChatComponent.`--bold`(
                bold: Boolean
        ): IChatComponent =
                style {
                        this.bold = bold
                }
        
        infix fun IChatComponent.`--italic`(
                italic: Boolean
        ): IChatComponent =
                style {
                        this.italic = italic
                }
        
        infix fun IChatComponent.`--strikethrough`(
                strikethrough: Boolean
        ): IChatComponent =
                style {
                        this.strikethrough = strikethrough
                }
        
        infix fun IChatComponent.`--underlined`(
                underlined: Boolean
        ): IChatComponent =
                style {
                        this.underlined = underlined
                }
        
        infix fun IChatComponent.`--obfuscated`(
                obfuscated: Boolean
        ): IChatComponent =
                style {
                        this.obfuscated = obfuscated
                }
}