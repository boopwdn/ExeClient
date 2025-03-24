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

package net.llvg.exec.config.brush

import cc.polyfrost.oneconfig.config.annotations.Checkbox
import cc.polyfrost.oneconfig.config.annotations.KeyBind
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import net.llvg.exec.config.ExeClientConfig
import net.llvg.exec.config.ExeFeatureConfig
import net.llvg.exec.features.brush.Brush

object BrushConfig : ExeFeatureConfig(
        ExeClientConfig::active,
        Brush,
        "Brush",
        "exec-brush-config.json"
) {
        @KeyBind(
                name = "Brush Toggle Key"
        )
        var keyToggle = OneKeyBind()
        
        @Checkbox(
                name = "Send Message"
        )
        var sendMessage = false
        
        override fun initialize() {
                super.initialize()
                registerKeyBind(
                        keyToggle,
                        Brush::toggle
                )
        }
}