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

package net.llvg.exec.api.config

import cc.polyfrost.oneconfig.config.elements.SubConfig

abstract class ExeCFeatureConfig(
        name: String,
        configFile: String,
        icon: String? = null,
        enabled: Boolean = false,
        canToggle: Boolean = true
) : SubConfig(
        name,
        configFile,
        icon,
        enabled,
        canToggle
) {
        protected open fun active(): Boolean =
                enabled
        
        open fun reactive() {}
        
        open fun inactive() {}
        
        @Transient
        @get:JvmName("isActive")
        var active = false
                private set(o) {
                        if (o == field) return
                        
                        if (o) reactive() else inactive()
                        field = o
                }
        
        override fun initialize() {
                super.initialize()
                active = active()
                
                initializeExeCFeatureConfigs(this)
        }
        
        override fun save() {
                super.save()
                active = active()
        }
}