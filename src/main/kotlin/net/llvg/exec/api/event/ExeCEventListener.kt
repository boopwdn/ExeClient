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

package net.llvg.exec.api.event

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

sealed class ExeCEventListener<E : ExeCEvent>(
        val owner: ExeCEventListenable,
        val always: Boolean,
        private val priority: Int
) : Comparable<ExeCEventListener<E>> {
        private val order = Companion.order++
        
        override fun compareTo(
                other: ExeCEventListener<E>
        ): Int =
                compare(this, other)
        
        class Block<E : ExeCEvent>(
                owner: ExeCEventListenable,
                always: Boolean,
                priority: Int,
                val action: (E) -> Unit
        ) : ExeCEventListener<E>(
                owner,
                always,
                priority
        )
        
        class Async<E : ExeCEvent>(
                owner: ExeCEventListenable,
                always: Boolean,
                priority: Int,
                val dispatcher: CoroutineDispatcher,
                val action: suspend CoroutineScope.(E) -> Unit
        ) : ExeCEventListener<E>(
                owner,
                always,
                priority
        )
        
        companion object : Comparator<ExeCEventListener<*>> by
                           Comparator
                           .comparingInt(ExeCEventListener<*>::priority)
                           .thenComparingInt(ExeCEventListener<*>::order) {
                private var order = 0
        }
}