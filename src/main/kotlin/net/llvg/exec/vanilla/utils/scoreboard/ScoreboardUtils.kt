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

@file:JvmName("ScoreboardUtils")

package net.llvg.exec.vanilla.utils.scoreboard

import com.google.common.collect.ImmutableList
import net.llvg.exec.utils.noMcFormatCode
import net.llvg.exec.vanilla.utils.mc
import net.llvg.exec.vanilla.utils.scoreboard.ScoreboardFlags.FLAG_LIMIT_TO_15
import net.llvg.exec.vanilla.utils.scoreboard.ScoreboardFlags.FLAG_NO_MC_FORMAT
import net.llvg.exec.vanilla.utils.scoreboard.ScoreboardFlags.FLAG_NO_STUPID_CHAR
import net.minecraft.scoreboard.ScorePlayerTeam

@JvmField
val STUPID_SCOREBOARD_CHARACTER_REGEX =
        Regex("[\\ud83c\\udf6b\\ud83d\\udca3\\ud83c\\udf89\\ud83c\\udf6d\\ud83d\\udc7d\\ud83d\\udd2e\\ud83d\\udc0d\\ud83d\\udc7e\\ud83c\\udf20⚽\\ud83c\\udfc0\\ud83d\\udc79\\ud83c\\udf81\\ud83c\\udf82]")

object ScoreboardFlags {
        const val FLAG_LIMIT_TO_15 = 0x01
        const val FLAG_NO_MC_FORMAT = 0x02
        const val FLAG_NO_STUPID_CHAR = 0x04
}

fun getScoreboardLines(
        flags: Int = 0
): List<String> {
        val world = mc.theWorld ?: return emptyList()
        val player = mc.thePlayer ?: return emptyList()
        
        val scoreboard = world.scoreboard
        val objective =
                scoreboard.getPlayersTeam(player.name)
                ?.run { chatFormat.colorIndex.takeIf { it >= 0 } }
                ?.let { scoreboard.getObjectiveInDisplaySlot(3 + it) }
                ?: scoreboard.getObjectiveInDisplaySlot(1)
                ?: return emptyList()
        
        val builder = ImmutableList.builder<String>()
        
        scoreboard
        .getSortedScores(objective)
        .filter { it.playerName?.startsWith('#') == false }
        .run { if (flags and FLAG_LIMIT_TO_15 != 0 && size > 15) subList(size - 15, size) else this }
        .forEach {
                val name = it.playerName
                builder.add(
                        ScorePlayerTeam.formatPlayerName(scoreboard.getPlayersTeam(name), name)
                        .run { if (flags and FLAG_NO_MC_FORMAT != 0) noMcFormatCode else this }
                        .run { if (flags and FLAG_NO_STUPID_CHAR != 0) noStupidScoreboardChar else this }
                )
        }
        
        return builder.build()
}

val String.noStupidScoreboardChar: String
        get() = STUPID_SCOREBOARD_CHARACTER_REGEX.replace(this, "")