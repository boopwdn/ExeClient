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

@file:JvmName("ExeCFeatureConfigUtils")

package net.llvg.exec.api.config

import cc.polyfrost.oneconfig.config.annotations.SubConfig

fun initializeExeCFeatureConfigs(
        owner: Any
) {
        owner
        .javaClass
        .declaredFields
        .filter {
                it.getAnnotation(SubConfig::class.java) !== null &&
                ExeCFeatureConfig::class.java.isAssignableFrom(it.type)
        }
        .forEach {
                it.isAccessible = true
                (it[owner] as? ExeCFeatureConfig<*>)?.initialize()
        }
}