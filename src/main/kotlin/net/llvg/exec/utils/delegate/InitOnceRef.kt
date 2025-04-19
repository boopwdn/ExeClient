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

package net.llvg.exec.utils.delegate

import net.llvg.loliutils.delegate.ValRef
import net.llvg.loliutils.delegate.VarRef
import net.llvg.loliutils.delegate.getValue
import net.llvg.loliutils.delegate.wrapBox

class InitOnceRef<T> : VarRef<T> {
        private var container: ValRef<T>? = null
        private val lock = Any()
        
        override fun get(): T {
                val container by container ?: synchronized(lock) {
                        container ?: throw IllegalArgumentException("Reference hasn't been initialized!")
                }
                return container
        }
        
        override fun set(
                o: T
        ) {
                if (container !== null) {
                        throw IllegalArgumentException("Reference has been initialized!")
                }
                synchronized(lock) {
                        if (container !== null) {
                                throw IllegalArgumentException("Reference has been initialized!")
                        }
                        container = o.wrapBox
                }
        }
}