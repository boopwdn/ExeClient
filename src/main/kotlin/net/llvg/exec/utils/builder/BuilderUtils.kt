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

@file:JvmName("BuilderUtils")

package net.llvg.exec.utils.builder

@Suppress("UnusedReceiverParameter")
fun <T> BuilderBase<*>.notNull(
        value: T?,
        name: String
): T {
        if (value === null) {
                throw IllegalArgumentException("value [$name] should not be null!")
        }
        
        return value
}