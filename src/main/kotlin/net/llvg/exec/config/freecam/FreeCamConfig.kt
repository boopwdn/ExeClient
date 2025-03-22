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

package net.llvg.exec.config.freecam

import cc.polyfrost.oneconfig.config.annotations.Checkbox
import cc.polyfrost.oneconfig.config.annotations.KeyBind
import cc.polyfrost.oneconfig.config.annotations.Number
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import net.llvg.exec.config.ExeClientConfig
import net.llvg.exec.config.ExeFeatureConfig
import net.llvg.exec.features.freecam.FreeCam

object FreeCamConfig : ExeFeatureConfig(
        ExeClientConfig::active,
        FreeCam,
        "Free Camera",
        "exec-free_camera-config.json"
) {
        @KeyBind(
                name = "Free Camera Toggle Key",
                size = 2
        )
        var keyToggle = OneKeyBind()
        
        @Checkbox(
                name = "Allow Toggle Controller"
        )
        var allowToggleController = false
        
        @KeyBind(
                name = "Toggle Controller Key"
        )
        var keyToggleController = OneKeyBind()
        
        @Checkbox(
                name = "Allow Toggle Perspective",
                size = 2
        )
        var allowTogglePerspective = false
        
        @Checkbox(
                name = "Disable on Damage"
        )
        var disableOnDamage = false
        
        @Checkbox(
                name = "Disable on Server Camera Change"
        )
        var disableOnSeverCameraChange = false
        
        @Checkbox(
                name = "Allow Camera Interact"
        )
        var allowCameraInteract = false
        
        @Checkbox(
                name = "Allow Player Interact"
        )
        var allowPlayerInteract = false
        
        @Number(
                name = "Horizontal Speed",
                min = 0F,
                max = Float.MAX_VALUE
        )
        var hSpeed = 1F
        
        
        @Number(
                name = "Vertical Speed",
                min = 0F,
                max = Float.MAX_VALUE
        )
        var vSpeed = .5F
        
        @Number(
                name = "Horizontal Sprint Speed",
                min = 0F,
                max = Float.MAX_VALUE
        )
        var hSprintSpeed = 2F
        
        @Number(
                name = "Vertical Sprint Speed",
                min = 0F,
                max = Float.MAX_VALUE
        )
        var vSprintSpeed = .5F
        
        override fun initialize() {
                super.initialize()
                registerKeyBind(
                        keyToggle,
                        FreeCam::toggle
                )
                registerKeyBind(
                        keyToggleController,
                        FreeCam::toggleController
                )
        }
}