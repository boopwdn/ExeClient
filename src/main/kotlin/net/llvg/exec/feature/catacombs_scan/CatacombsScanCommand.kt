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

@file:Suppress("ClassName")

package net.llvg.exec.feature.catacombs_scan

import com.google.common.collect.ImmutableSortedMap
import kotlinx.coroutines.CancellationException
import net.llvg.exec.ExeClient
import net.llvg.exec.api.command.ExeCCommand
import net.llvg.exec.api.command.ExeCCommandChatComponentScope
import net.llvg.exec.api.command.combineUsages
import net.llvg.exec.api.command.sendUsage
import net.llvg.exec.api.command.sendWrongUsage
import net.llvg.exec.utils.skyblock.catacombs.map.scan.CatacombsMap
import net.llvg.exec.utils.skyblock.catacombs.map.scan.CatacombsScanner
import net.llvg.exec.vanilla.utils.chat_component.ChatColor
import net.llvg.exec.vanilla.utils.chat_component.buildChat
import net.llvg.exec.vanilla.utils.player
import net.llvg.exec.vanilla.utils.vector.component1
import net.llvg.exec.vanilla.utils.vector.component2
import net.llvg.exec.vanilla.utils.vector.component3
import net.llvg.loliutils.iterator.subArray
import net.llvg.loliutils.time.nanoSecondToMilliSecond
import net.llvg.loliutils.time.systemNanoTime
import net.minecraft.util.IChatComponent

object CatacombsScanCommand : ExeCCommand {
        override val name: String = "catacombs_scan"
        
        private val commands: Map<String, ExeCCommand> =
                ImmutableSortedMap
                .naturalOrder<String, ExeCCommand>()
                .apply {
                        arrayOf(
                                `sub-cmd curr`,
                                `sub-cmd reset`,
                                `sub-cmd rescan`
                        )
                        .forEach {
                                put(it.name, it)
                        }
                }
                .build()
        
        override val usage: IChatComponent = buildChat {
                with(ExeCCommandChatComponentScope) {
                        of(
                                empty() // " - $name | Display usage of the command
                                .." "
                                .."-"()
                                .`--style split-mark`
                                .." "
                                ..CatacombsScanCommand.name()
                                .`--style command-name`
                                .." "
                                .."|"()
                                .`--style split-mark`
                                .." "
                                .."Display usage of the command"
                                .."\n"
                                ..commands.combineUsages
                        )
                }
        }
        
        override fun process(
                args: Array<String>
        ) {
                if (args.isEmpty()) {
                        sendUsage()
                } else {
                        val command = commands[args[0]]
                        
                        if (command === null) {
                                sendWrongUsage()
                                return
                        }
                        
                        command.process(args.subArray(1))
                }
        }
        
        override fun completeTab(
                args: Array<String>
        ): List<String> =
                if (args.size == 1) {
                        val name = args[0]
                        commands.keys.filter { it.startsWith(name) }
                } else {
                        commands[args[0]]?.completeTab(args.subArray(1)) ?: emptyList()
                }
}

private object `sub-cmd curr` : ExeCCommand {
        override val name: String = "curr"
        
        override val usage: IChatComponent = buildChat {
                with(ExeCCommandChatComponentScope) {
                        of(
                                empty() // " - $name $name1 | Display name of current room"
                                .." "
                                .."-"()
                                .`--style split-mark`
                                .." "
                                ..CatacombsScanCommand.name()
                                .`--style command-name`
                                .." "
                                ..name()
                                .`--style command-text`
                                .." "
                                .."|"()
                                .`--style split-mark`
                                .." "
                                .."Display name of current room"
                        )
                }
        }
        
        override fun process(
                args: Array<String>
        ) {
                val (x, _, z) = player.position
                
                val info = CatacombsMap.roomEntryAtPos(x, z)?.info
                if (info === null) {
                        ExeClient.send {
                                "No information about current room"()
                                .`--color`(ChatColor.YELLOW)
                        }
                        return
                }
                ExeClient.send {
                        of(
                                empty()
                                .."Current Room: ["
                                ..info.data.name
                                .."] r="
                                ..info.rotation.name
                                ..", c=("
                                ..(info.corner?.let { (x, y, z) ->
                                        "$x, $y, $z"
                                } ?: "?, ?, ?")
                                ..")"
                        )
                }
        }
        
        override fun completeTab(
                args: Array<String>
        ): List<String> =
                emptyList()
}

private object `sub-cmd reset` : ExeCCommand {
        override val name: String = "reset"
        
        override val usage: IChatComponent = buildChat {
                with(ExeCCommandChatComponentScope) {
                        of(
                                empty() // " - $name $name1 | Reset scan result"
                                .." "
                                .."-"()
                                .`--style split-mark`
                                .." "
                                ..CatacombsScanCommand.name()
                                .`--style command-name`
                                .." "
                                ..name()
                                .`--style command-text`
                                .." "
                                .."|"()
                                .`--style split-mark`
                                .." "
                                .."Reset scan result"
                        )
                }
        }
        
        override fun process(
                args: Array<String>
        ) {
                CatacombsScanner.afterCurrentScan {
                        ExeClient.send {
                                "Reset scan result"()
                        }
                        CatacombsMap.reset()
                }
        }
        
        override fun completeTab(
                args: Array<String>
        ): List<String> =
                emptyList()
}

private object `sub-cmd rescan` : ExeCCommand {
        override val name: String = "rescan"
        
        override val usage: IChatComponent = buildChat {
                with(ExeCCommandChatComponentScope) {
                        of(
                                empty() // " - $name $name1 | Rescan catacombs map"
                                .." "
                                .."-"()
                                .`--style split-mark`
                                .." "
                                ..CatacombsScanCommand.name()
                                .`--style command-name`
                                .." "
                                ..name()
                                .`--style command-text`
                                .." "
                                .."|"()
                                .`--style split-mark`
                                .." "
                                .."Scan catacombs map"
                        )
                }
        }
        
        override fun process(
                args: Array<String>
        ) {
                val start = systemNanoTime
                if (CatacombsScanner.roomScanning) {
                        ExeClient.send {
                                "Previous scan hasn't finished yet"()
                        }
                        return
                }
                CatacombsMap.reset()
                val job = CatacombsScanner.scan()
                if (job === null) {
                        ExeClient.send {
                                "Previous scan hasn't finished yet"()
                        }
                        return
                }
                job.invokeOnCompletion c@{ e ->
                        val time = (systemNanoTime - start) / nanoSecondToMilliSecond
                        if (e !== null) {
                                if (e is CancellationException) ExeClient.send {
                                        "Catacombs scan has been cancelled"()
                                        .`--color`(ChatColor.YELLOW)
                                }
                                return@c
                        }
                        ExeClient.send {
                                of(
                                        empty()
                                        .."Catacombs scan finished in less than "
                                        .."$time"()
                                        .`--color`(ChatColor.GREEN)
                                        .`--bold`(true)
                                        .."ms"()
                                        .`--color`(ChatColor.YELLOW)
                                )
                        }
                }
        }
        
        override fun completeTab(
                args: Array<String>
        ): List<String> =
                emptyList()
}
