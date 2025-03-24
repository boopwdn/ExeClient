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

package net.llvg.exec.features.brush

import net.llvg.exec.config.ExeFeatureConfig
import net.llvg.exec.config.brush.BrushConfig
import net.llvg.exec.features.ExeFeature

object Brush : ExeFeature {
        override fun initialize() {}
        
        override fun reactive() {}
        
        override fun inactive() {}
        
        override val config: ExeFeatureConfig
                get() = BrushConfig
}