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

package net.llvg.exec.api.event

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

class ExeCEventListener<E : ExeCEvent>(
        private val owner: ExeCEventListenable,
        private val always: Boolean,
        private val priority: Int,
        val dispatcher: CoroutineDispatcher,
        val action: suspend CoroutineScope.(ExeCEvent) -> Unit
) : Comparable<ExeCEventListener<E>> {
        val active: Boolean
                get() = always || owner.active
        
        private val order = Companion.order++
        
        override fun compareTo(
                other: ExeCEventListener<E>
        ): Int = ExeCEventListener.compare(this, other)
        
        companion object : Comparator<ExeCEventListener<*>> by
                           Comparator
                           .comparingInt(ExeCEventListener<*>::priority)
                           .thenComparingInt(ExeCEventListener<*>::order) {
                private var order = 0
        }
}