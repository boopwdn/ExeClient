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

package net.llvg.exec.features.freecam

import com.mojang.authlib.GameProfile
import java.util.UUID
import kotlin.math.abs
import net.llvg.exec.config.freecam.FreeCamConfig
import net.llvg.exec.inject.EntityPlayer_super_onLivingUpdate
import net.llvg.exec.inject.activePotionsMap
import net.llvg.exec.inject.getPlayerInfo
import net.llvg.exec.inject.theInventoryEnderChest
import net.llvg.exec.utils.mc
import net.llvg.exec.utils.player
import net.llvg.exec.utils.world
import net.llvg.loliutils.exception.double
import net.llvg.loliutils.exception.float
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.network.NetHandlerPlayClient
import net.minecraft.client.network.NetworkPlayerInfo
import net.minecraft.entity.Entity
import net.minecraft.network.Packet
import net.minecraft.util.MathHelper

class FreeCamEntity(
) : EntityPlayerSP(
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
                capabilities.allowFlying = true
                capabilities.isFlying = true
                
                inventory =
                        player.inventory
                theInventoryEnderChest =
                        player.theInventoryEnderChest
                inventoryContainer =
                        player.inventoryContainer
                openContainer =
                        player.openContainer
                activePotionsMap =
                        player.activePotionsMap
        }
        
        override fun getPlayerInfo(): NetworkPlayerInfo? {
                return player.getPlayerInfo()
        }
        
        override fun canAttackWithItem(
        ): Boolean = false
        
        override fun hitByEntity(
                entityIn: Entity?
        ): Boolean = false
        
        private var forceSpectator = false
        
        override fun isSpectator(): Boolean {
                return forceSpectator || super.isSpectator()
        }
        
        override fun onUpdate() {
                forceSpectator = true
                super.onUpdate()
                forceSpectator = false
        }
        
        override fun applyEntityCollision(entityIn: Entity?) {}
        
        override fun setSprinting(sprinting: Boolean) {
                super.setSprinting(false)
        }
        
        override fun isSprinting(): Boolean {
                return false
        }
        
        override fun setSneaking(sneaking: Boolean) {
                super.setSneaking(false)
        }
        
        override fun isSneaking(): Boolean {
                return false
        }
        
        override fun onLivingUpdate() {
                movementInput.updatePlayerMoveState()
                
                val sprint = mc.gameSettings.keyBindSprint.isKeyDown
                
                motionY = 0.0
                if (movementInput.jump)
                        motionY += 1
                if (movementInput.sneak)
                        motionY -= 1
                
                motionY *= FreeCamConfig.run { if (sprint) vSprintSpeed else vSpeed }
                
                val hSpeed = FreeCamConfig.run { if (sprint) hSprintSpeed else hSpeed }
                val faceDirSpeed = hSpeed * movementInput.moveForward
                val sideDirSpeed = hSpeed * movementInput.moveStrafe
                
                val sinV = MathHelper.sin(rotationYaw * Math.PI.float / 180F)
                val cosV = MathHelper.cos(rotationYaw * Math.PI.float / 180F)
                
                motionX = (sideDirSpeed * cosV - faceDirSpeed * sinV).double
                motionZ = (sideDirSpeed * sinV + faceDirSpeed * cosV).double
                
                if (abs(motionX) < 0.005)
                        motionX = 0.0
                if (abs(motionY) < 0.005)
                        motionY = 0.0
                if (abs(motionZ) < 0.005)
                        motionZ = 0.0
                
                EntityPlayer_super_onLivingUpdate()
        }
}
