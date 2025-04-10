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

package net.llvg.exec.utils.registry

import com.google.common.collect.ImmutableList
import net.llvg.exec.api.event.post

abstract class Registry<T>(
        vararg initial: T
) {
        protected open fun event(
        ): InnerRegisterEvent<T> = object : InnerRegisterEvent<T> {
                override val elements: MutableList<T> = ArrayList()
        }
        
        protected val elements: List<T> = ImmutableList
        .builder<T>()
        .apply {
                add(*initial)
                
                val event = event()
                event.post(true)
                addAll(event.elements)
        }
        .build()
        
        protected interface InnerRegisterEvent<T> : RegisterEvent<T> {
                val elements: MutableList<T>
                
                override fun register(
                        element: T
                ) {
                        elements += element
                }
        }
}