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

package net.llvg.exec.feature.freecam

import net.minecraft.client.settings.GameSettings
import net.minecraft.util.MovementInput

class FreeCamMovementInput(
        private val gameSettings: GameSettings
) : MovementInput() {
        override fun updatePlayerMoveState() {
                jump = false
                if (gameSettings.keyBindJump.isKeyDown) {
                        jump = true
                }
                
                sneak = false
                if (gameSettings.keyBindSneak.isKeyDown) {
                        sneak = true
                }
                
                moveForward = 0F
                if (gameSettings.keyBindForward.isKeyDown) {
                        moveForward += 1
                }
                if (gameSettings.keyBindBack.isKeyDown) {
                        moveForward -= 1
                }
                
                moveStrafe = 0F
                if (gameSettings.keyBindLeft.isKeyDown) {
                        moveStrafe += 1
                }
                if (gameSettings.keyBindRight.isKeyDown) {
                        moveStrafe -= 1
                }
        }
}