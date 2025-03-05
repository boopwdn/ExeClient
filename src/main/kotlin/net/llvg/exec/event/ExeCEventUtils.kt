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

@file:JvmName("ExeCEventUtils")

package net.llvg.exec.event

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import net.llvg.loliutils.exception.cast

inline fun <reified E : ExeCEvent> ExeCEventListenable.onEvent(
        forced: Boolean = false,
        always: Boolean = true,
        priority: Int = 0,
        dispatcher: CoroutineDispatcher = Dispatchers.Unconfined,
        crossinline action: suspend CoroutineScope.(E) -> Unit
): Unit = ExeCEventManager.register(
        E::class.java,
        forced,
        ExeCEventListener<E>(
                this,
                always,
                priority,
                dispatcher
        ) { action(it.cast()) }
)

inline fun <reified E : ExeCEvent> E.post(
        wait: Boolean
): Unit = ExeCEventManager.post(E::class.java, this, wait)