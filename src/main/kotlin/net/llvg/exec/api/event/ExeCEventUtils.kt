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

@file:JvmName("ExeCEventUtils")

package net.llvg.exec.api.event

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

@Suppress("UNUSED")
inline fun <reified E : ExeCEvent> ExeCEventListenable.onEvent(
        dispatcher: CoroutineDispatcher,
        forced: Boolean = false,
        priority: Int = 0,
        always: Boolean = false,
        noinline action: suspend CoroutineScope.(E) -> Unit
) {
        ExeCEventManager.register(
                E::class.java,
                forced,
                ExeCEventListener.Async(
                        this,
                        always,
                        priority,
                        dispatcher,
                        action
                )
        )
}

@Suppress("UNUSED")
inline fun <reified E : ExeCEvent> ExeCEventListenable.onEvent(
        forced: Boolean = false,
        priority: Int = 0,
        always: Boolean = false,
        noinline action: (E) -> Unit
) {
        ExeCEventManager.register(
                E::class.java,
                forced,
                ExeCEventListener.Block(
                        this,
                        always,
                        priority,
                        action
                )
        )
}

@Suppress("UNUSED")
inline fun <reified E : ExeCEvent> E.post(
        wait: Boolean
) {
        post(E::class.java, wait)
}

@Suppress("UNUSED")
fun <E : ExeCEvent> E.post(
        type: Class<out ExeCEvent>,
        wait: Boolean
) {
        ExeCEventManager.post(
                type,
                this,
                wait
        )
}

@Suppress("UNUSED")
inline val <reified E : ExeCEventCancellable> E.postAndCheckCancel: Boolean
        get() = postAndCheckCancel(E::class.java)

@Suppress("UNUSED")
fun <E : ExeCEventCancellable> E.postAndCheckCancel(
        type: Class<out ExeCEvent>
): Boolean {
        ExeCEventManager.post(
                type,
                this,
                true
        )
        return cancelled
}