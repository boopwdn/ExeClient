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

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableSortedMap
import java.util.LinkedList
import net.llvg.exec.ExeClient
import net.llvg.exec.utils.classNameLogger
import net.llvg.exec.utils.registry.Registry
import net.llvg.exec.vanilla.utils.chat_component.buildChat
import net.llvg.exec.vanilla.utils.player
import net.llvg.loliutils.iterator.subArray
import net.minecraft.command.ICommand
import net.minecraft.command.ICommandSender
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumChatFormatting
import net.minecraft.util.IChatComponent
import net.minecraftforge.client.ClientCommandHandler

object ExeCCommandManager : Registry<ExeCCommand>(
        ExeCCommandHelp,
        ExeCCommandThrow
) {
        init {
                ClientCommandHandler.instance.registerCommand(ExeCInternalCommand)
        }
        
        private val commands: Map<String, ExeCCommand> = ImmutableSortedMap
        .naturalOrder<String, ExeCCommand>()
        .apply {
                elements.forEach {
                        put(it.name, it)
                }
        }
        .build()
        
        private fun command(
                name: String
        ): ExeCCommand? = commands[name]
        .also {
                if (it === null) ExeClient.send {
                        with(ExeCCommandChatComponentScope) {
                                empty {
                                        styleWarn
                                } +
                                "No such sub-command starts with " +
                                name {
                                        styleCommandName
                                } +
                                ", use " +
                                "/exec" {
                                        styleCommandText
                                } +
                                " " +
                                "help" {
                                        styleCommandName
                                }
                        }
                }
        }
        
        private fun commandsStartsWith(
                name: String
        ): List<String> = commands
        .keys
        .filterTo(LinkedList()) {
                it.startsWith(name)
        }
        
        private object ExeCInternalCommand : ICommand {
                private fun sendUsage() {
                        ExeClient.send {
                                with(ExeCCommandChatComponentScope) {
                                        empty +
                                        "Use " +
                                        "/exec" {
                                                styleCommandText
                                        } +
                                        " " +
                                        "help" {
                                                styleCommandName
                                        }
                                }
                        }
                }
                
                override fun getCommandName(
                ): String = "exe_client"
                
                override fun getCommandUsage(
                        sender: ICommandSender?
                ): String {
                        sendUsage()
                        return ""
                }
                
                override fun getCommandAliases(
                ): List<String> = ImmutableList
                .of(
                        "exe_c",
                        "exec"
                )
                
                override fun processCommand(
                        sender: ICommandSender?,
                        args: Array<String>
                ) {
                        if (args.isEmpty()) {
                                sendUsage()
                                return
                        }
                        try {
                                command(args[0])?.process(args.subArray(1))
                        } catch (e: Throwable) {
                                logger.info("An error occur during processing command {}", args[0], e)
                                ExeClient.send {
                                        with(ExeCCommandChatComponentScope) {
                                                empty {
                                                        color = EnumChatFormatting.DARK_RED
                                                } +
                                                "An error occur during processing command " +
                                                args[0] {
                                                        styleCommandName
                                                } +
                                                ", check log for more information"
                                        }
                                }
                        }
                }
                
                override fun canCommandSenderUseCommand(
                        sender: ICommandSender?
                ): Boolean = sender === player
                
                override fun addTabCompletionOptions(
                        sender: ICommandSender?,
                        args: Array<String>,
                        pos: BlockPos?
                ): List<String> = if (args.size == 1) {
                        commandsStartsWith(args[0])
                } else try {
                        command(args[0])?.completeTab(args.subArray(1)) ?: emptyList()
                } catch (e: Throwable) {
                        logger.info("An error occur during completing tab by command {}", args[0], e)
                        ExeClient.send {
                                with(ExeCCommandChatComponentScope) {
                                        empty {
                                                color = EnumChatFormatting.DARK_RED
                                        } +
                                        "An error occur during completing tab by command " +
                                        args[0] {
                                                styleCommandName
                                        } +
                                        ", check log for more information"
                                }
                        }
                        emptyList()
                }
                
                override fun isUsernameIndex(
                        args: Array<String>,
                        index: Int
                ): Boolean = false
                
                override fun compareTo(
                        other: ICommand?
                ): Int = if (other === null) 1 else {
                        commandName.compareTo(other.commandName)
                }
        }
        
        private object ExeCCommandHelp : ExeCCommand {
                override val name: String = "help"
                
                override val usage: IChatComponent = buildChat {
                        with(ExeCCommandChatComponentScope) {
                                empty + // " - help | Display all known commands"
                                " " +
                                "-" {
                                        styleSplitMark
                                } +
                                " " +
                                "help" {
                                        styleCommandName
                                } +
                                " " +
                                "|" {
                                        styleSplitMark
                                } +
                                " " +
                                "Display all known commands" +
                                "\n" + // " - help <command> | Display usage of command <command>"
                                " " +
                                "-" {
                                        styleSplitMark
                                } +
                                " " +
                                "help" {
                                        styleCommandName
                                } +
                                " " +
                                "<command>" {
                                        styleParameter
                                } +
                                " " +
                                "|" {
                                        styleSplitMark
                                } +
                                " " +
                                "Display usage of command" +
                                " " +
                                "<command>" {
                                        styleParameter
                                }
                        }
                }
                
                override fun process(
                        args: Array<String>
                ) {
                        if (args.isEmpty()) ExeClient.send {
                                with(ExeCCommandChatComponentScope) {
                                        empty +
                                        "Known commands:" +
                                        {
                                                for (name in commands.keys) {
                                                        it +
                                                        "\n - " {
                                                                styleSplitMark
                                                        } +
                                                        name {
                                                                styleCommandName
                                                        }
                                                }
                                        }
                                }
                        } else {
                                command(args[0])?.sendUsage()
                        }
                }
                
                override fun completeTab(
                        args: Array<String>
                ): List<String> = if (args.size == 1) {
                        commandsStartsWith(name)
                } else {
                        emptyList()
                }
        }
        
        private object ExeCCommandThrow : ExeCCommand {
                override val name: String = "throw"
                
                override val usage: IChatComponent = buildChat {
                        with(ExeCCommandChatComponentScope) {
                                empty + // " - throw | Throw a runtime-exception"
                                " " +
                                "-" {
                                        styleSplitMark
                                } +
                                " " +
                                "throw" {
                                        styleCommandName
                                } +
                                " " +
                                "|" {
                                        styleSplitMark
                                } +
                                " " +
                                "Throw a runtime-exception"
                        }
                }
                
                override fun process(
                        args: Array<String>
                ) {
                        throw RuntimeException()
                }
                
                override fun completeTab(
                        args: Array<String>
                ): List<String> = throw RuntimeException()
        }
}

private val logger = classNameLogger<ExeCCommandManager>()