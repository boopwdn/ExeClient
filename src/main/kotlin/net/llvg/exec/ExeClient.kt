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

package net.llvg.exec

import net.llvg.exec.api.command.ExeCCommandManager
import net.llvg.exec.api.config.ExeClientConfig
import net.llvg.exec.api.feature.ExeCFeatureManager
import net.llvg.exec.utils.classNameLogger
import net.llvg.exec.vanilla.utils.chat_component.ChatComponentBuildScope
import net.llvg.exec.vanilla.utils.chat_component.buildChat
import net.llvg.exec.vanilla.utils.player
import net.minecraft.util.EnumChatFormatting
import net.minecraft.util.IChatComponent

object ExeClient {
        @JvmField
        val logger = classNameLogger<ExeClient>()
        
        @JvmStatic
        fun initialize() {
                ExeCFeatureManager
                ExeClientConfig
                ExeCCommandManager
        }
        
        fun send(
                message: IChatComponent
        ) {
                buildChat {
                        empty +
                        execPrefix +
                        message
                }.run(player::addChatMessage)
        }
        
        fun send(
                builder: ChatComponentBuildScope.() -> IChatComponent
        ) {
                send(ChatComponentBuildScope.builder())
        }
}

private val execPrefix = buildChat {
        empty +
        buildChat {
                empty {
                        bold = true
                        color = EnumChatFormatting.WHITE
                } +
                "[" +
                "Exe Client" {
                        color = EnumChatFormatting.AQUA
                } +
                "] "
        }
}