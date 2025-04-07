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

package net.llvg.exec.api.feature

import net.llvg.exec.api.config.ExeCFeatureConfig
import net.llvg.exec.api.event.ExeCEventListenable
import net.llvg.exec.vanilla.utils.chat_component.withChatStyle
import net.minecraft.util.ChatComponentText
import net.minecraft.util.EnumChatFormatting
import net.minecraft.util.IChatComponent

interface ExeCFeature<C : ExeCFeatureConfig<C>> : ExeCEventListenable {
        fun initialize()
        
        val config: C
        
        override val active: Boolean
        
        companion object {
                val MESSAGE_ENABLED: IChatComponent =
                        ChatComponentText("Enabled")
                        .withChatStyle {
                                color = EnumChatFormatting.GREEN
                        }
                
                val MESSAGE_DISABLED: IChatComponent =
                        ChatComponentText("Disabled")
                        .withChatStyle {
                                color = EnumChatFormatting.RED
                        }
        }
}