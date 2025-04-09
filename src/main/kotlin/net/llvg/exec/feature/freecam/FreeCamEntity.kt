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

import com.mojang.authlib.GameProfile
import java.util.UUID
import kotlin.math.abs
import net.llvg.exec.mixin.inject.InjectAbstractClientPlayer
import net.llvg.exec.mixin.inject.InjectEntityLivingBase
import net.llvg.exec.mixin.inject.InjectEntityPlayer
import net.llvg.exec.vanilla.utils.mc
import net.llvg.exec.vanilla.utils.player
import net.llvg.exec.vanilla.utils.world
import net.llvg.loliutils.exception.cast
import net.llvg.loliutils.exception.double
import net.llvg.loliutils.exception.float
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.network.NetHandlerPlayClient
import net.minecraft.client.network.NetworkPlayerInfo
import net.minecraft.entity.Entity
import net.minecraft.network.Packet
import net.minecraft.util.MathHelper
import net.minecraft.util.MovementInput

class FreeCamEntity : EntityPlayerSP(
        mc,
        world,
        object : NetHandlerPlayClient(
                mc,
                null,
                mc.netHandler.networkManager,
                GameProfile(UUID.randomUUID(), "FreeCamEntity"),
        ) {
                override fun addToSendQueue(ignored: Packet<*>?) {}
        },
        player.statFileWriter,
) {
        init {
                movementInput = MovementInput()
                
                capabilities.allowFlying = true
                capabilities.isFlying = true
                
                inventory =
                        player.inventory
                cast<InjectEntityPlayer>(this)._theInventoryEnderChest_exec =
                        cast<InjectEntityPlayer>(player)._theInventoryEnderChest_exec
                inventoryContainer =
                        player.inventoryContainer
                openContainer =
                        player.openContainer
                cast<InjectEntityLivingBase>(this)._activePotionsMap_exec =
                        cast<InjectEntityLivingBase>(player)._activePotionsMap_exec
        }
        
        override fun getPlayerInfo(
        ): NetworkPlayerInfo? = cast<InjectAbstractClientPlayer>(player).playerInfo_exec
        
        override fun canAttackWithItem(
        ): Boolean = false
        
        override fun hitByEntity(
                entityIn: Entity?
        ): Boolean = false
        
        private var forceSpectator = false
        
        override fun isSpectator(
        ): Boolean = forceSpectator || super.isSpectator()
        
        override fun onUpdate() {
                forceSpectator = true
                super.onUpdate()
                forceSpectator = false
        }
        
        override fun applyEntityCollision(
                entityIn: Entity?
        ) {
        }
        
        override fun setSprinting(
                sprinting: Boolean
        ) {
                super.setSprinting(false)
        }
        
        override fun isSprinting(
        ): Boolean = false
        
        override fun setSneaking(
                sneaking: Boolean
        ) {
                super.setSneaking(false)
        }
        
        override fun isSneaking(
        ): Boolean = false
        
        override fun updateEntityActionState() {
                super.updateEntityActionState()
                isJumping = false
        }
        
        override fun onLivingUpdate() {
                movementInput.updatePlayerMoveState()
                
                val sprint = mc.gameSettings.keyBindSprint.isKeyDown
                
                motionY = 0.0
                if (movementInput.jump) {
                        motionY += 1
                }
                if (movementInput.sneak) {
                        motionY -= 1
                }
                
                motionY *= FreeCam.config.run {
                        if (sprint) vSprintSpeed else vSpeed
                }
                
                val hSpeed = FreeCam.config.run {
                        if (sprint) hSprintSpeed else hSpeed
                }
                val faceDirSpeed = hSpeed * movementInput.moveForward
                val sideDirSpeed = hSpeed * movementInput.moveStrafe
                
                val sinV = MathHelper.sin(rotationYaw * Math.PI.float / 180F)
                val cosV = MathHelper.cos(rotationYaw * Math.PI.float / 180F)
                
                motionX = (sideDirSpeed * cosV - faceDirSpeed * sinV).double
                motionZ = (sideDirSpeed * sinV + faceDirSpeed * cosV).double
                
                if (abs(motionX) < 0.005) {
                        motionX = 0.0
                }
                if (abs(motionY) < 0.005) {
                        motionY = 0.0
                }
                if (abs(motionZ) < 0.005) {
                        motionZ = 0.0
                }
                
                cast<InjectEntityPlayer>(this)._super_onLivingUpdate_exec()
        }
}
