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

@file:[JvmName("CallbackMinecraft") Suppress("FunctionName")]

package net.llvg.exec.mixin.callback

import net.llvg.exec.event.events.WorldClientEvent
import net.llvg.exec.event.post
import net.minecraft.client.multiplayer.WorldClient

fun `post$WorldClientEvent$Load$Pre`(
        worldClient: WorldClient?
) {
        val event = WorldClientEvent.Load.Pre.Impl(
                worldClient
        )
        event.post(wait = true)
}