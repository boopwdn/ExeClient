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

package net.llvg.exec.config

import cc.polyfrost.oneconfig.config.elements.SubConfig
import net.llvg.exec.event.post

abstract class ExeCFeatureConfig<C : ExeCFeatureConfig<C>>(
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
        protected abstract val self: C
        
        protected open fun active(
        ): Boolean = enabled
        
        @Transient
        @get:JvmName("isActive")
        var active = false
                private set
        
        override fun initialize() {
                super.initialize()
                active = active()
                
                initializeExeCFeatureConfigs(this)
        }
        
        override fun save() {
                super.save()
                val active = active()
                if (active != this.active) {
                        this.active = active
                        val event = if (active) {
                                ExeCFeatureConfigEvent.Reactive.Impl(self)
                        } else {
                                ExeCFeatureConfigEvent.Inactive.Impl(self)
                        }
                        event.post(wait = false)
                }
        }
}