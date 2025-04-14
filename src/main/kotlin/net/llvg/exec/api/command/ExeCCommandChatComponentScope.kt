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

package net.llvg.exec.api.command

import net.llvg.exec.vanilla.utils.chat_component.ChatColor
import net.llvg.exec.vanilla.utils.chat_component.ChatComponentBuildScope.`--color`
import net.llvg.exec.vanilla.utils.chat_component.style
import net.minecraft.util.IChatComponent

@Suppress("ObjectPropertyName")
object ExeCCommandChatComponentScope {
        val IChatComponent.`--style command-name`: IChatComponent
                get() = style {
                        `--color`(ChatColor.GOLD)
                        bold = true
                }
        
        val IChatComponent.`--style command-text`: IChatComponent
                get() = style {
                        `--color`(ChatColor.YELLOW)
                }
        
        val IChatComponent.`--style parameter`: IChatComponent
                get() = style {
                        `--color`(ChatColor.LIGHT_PURPLE)
                }
        
        val IChatComponent.`--style split-mark`: IChatComponent
                get() = style {
                        `--color`(ChatColor.GRAY)
                }
        
        val IChatComponent.`--style warn`: IChatComponent
                get() = style {
                        `--color`(ChatColor.RED)
                }
}
