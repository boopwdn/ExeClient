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

@file:JvmName("ExeCCommandUtils")

package net.llvg.exec.api.command

import net.llvg.exec.ExeClient
import net.llvg.exec.vanilla.utils.chat_component.buildChat
import net.minecraft.util.IChatComponent

fun ExeCCommand.sendUsage() {
        ExeClient.send {
                with(ExeCCommandChatComponentScope) {
                        of(
                                empty()
                                .."Following are the usage of command "
                                ..name()
                                .`--style command-name`
                                ..": \n"
                                ..usage
                        )
                }
        }
}

@Suppress("UNUSED")
fun ExeCCommand.sendWrongUsage() {
        ExeClient.send {
                with(ExeCCommandChatComponentScope) {
                        of(
                                empty()
                                .."Incorrect use of command "()
                                .`--style warn`
                                ..name()
                                .`--style command-name`
                        )
                }
        }
        sendUsage()
}

@Suppress("UNUSED")
val Map<String, ExeCCommand>.combineUsages: IChatComponent
        get() = buildChat {
                of(
                        empty()
                        ..{
                                if (isEmpty()) return it
                                
                                val i = values.iterator()
                                of(
                                        it
                                        ..i.next().usage
                                )
                                while (i.hasNext()) of(
                                        it
                                        .."\n"
                                        ..i.next().usage
                                )
                        }
                )
        }