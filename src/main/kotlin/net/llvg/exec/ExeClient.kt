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

import java.io.File
import net.llvg.exec.api.command.ExeCCommandManager
import net.llvg.exec.api.config.ExeClientConfig
import net.llvg.exec.api.event.ExeCEventManager
import net.llvg.exec.api.feature.ExeCFeatureManager
import net.llvg.exec.preload.vanilla_tweaker.ExeCTweaker
import net.llvg.exec.utils.classNameLogger
import net.llvg.exec.vanilla.utils.chat_component.ChatColor
import net.llvg.exec.vanilla.utils.chat_component.ChatComponentBuildScope
import net.llvg.exec.vanilla.utils.chat_component.buildChat
import net.llvg.exec.vanilla.utils.mc
import net.minecraft.util.IChatComponent

object ExeClient {
        @JvmField
        val logger = classNameLogger<ExeClient>()
        
        @JvmField
        val directory: File = File(ExeCTweaker.gameDir, "Exe Client")
        
        const val MIXIN_ID = "exec"
        
        @JvmStatic
        fun initialize() {
                ExeCEventManager
                ExeCFeatureManager
                ExeClientConfig
                ExeCCommandManager
        }
        
        fun send(
                message: IChatComponent
        ) {
                val player = mc.thePlayer
                if (player === null) {
                        logger.warn("Try to send \"{}\" to player but player is null", message.formattedText)
                }
                buildChat {
                        of(
                                empty()
                                ..execPrefix
                                ..message
                        )
                }.run(player::addChatMessage)
        }
        
        fun send(
                builder: ChatComponentBuildScope.() -> IChatComponent
        ) {
                send(ChatComponentBuildScope.builder())
        }
}

private val execPrefix = buildChat {
        of(
                empty()
                ..of(
                        empty()
                        .`--color`(ChatColor.WHITE)
                        .`--bold`(true)
                        .."["
                        .."Exe Client"()
                        .`--color`(ChatColor.AQUA)
                        .."] "
                )
        )
}