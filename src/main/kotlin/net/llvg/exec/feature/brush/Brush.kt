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

package net.llvg.exec.feature.brush

import net.llvg.exec.api.config.ExeClientConfig
import net.llvg.exec.api.feature.ExeCFeature

object Brush : ExeCFeature<BrushConfig> {
        override fun initialize() {}
        
        override val config: BrushConfig
                get() = ExeClientConfig.configBrush
        
        override val active: Boolean
                get() = config.active
}