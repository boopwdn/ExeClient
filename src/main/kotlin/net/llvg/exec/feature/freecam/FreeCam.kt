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

import kotlinx.coroutines.Dispatchers
import net.llvg.exec.ExeClient
import net.llvg.exec.api.config.ExeCFeatureConfigEvent
import net.llvg.exec.api.config.ExeClientConfig
import net.llvg.exec.api.event.onEvent
import net.llvg.exec.api.feature.ExeCFeature
import net.llvg.exec.mixin.inject.InjectNetHandlerPlayClient
import net.llvg.exec.vanilla.event.EntityLivingBaseEvent
import net.llvg.exec.vanilla.event.PacketEvent
import net.llvg.exec.vanilla.event.WorldClientEvent
import net.llvg.exec.vanilla.utils.chat_component.buildChat
import net.llvg.exec.vanilla.utils.mc
import net.llvg.exec.vanilla.utils.player
import net.llvg.exec.vanilla.utils.world
import net.llvg.loliutils.exception.cast
import net.minecraft.entity.Entity
import net.minecraft.util.EnumChatFormatting
import net.minecraft.util.MovementInput
import net.minecraft.util.MovementInputFromOptions

object FreeCam : ExeCFeature<FreeCamConfig> {
        init {
                onEvent(Dispatchers.Default) { e: EntityLivingBaseEvent.HealthChange.Pre ->
                        if (e.entity !== player) return@onEvent
                        
                        if (FreeCamConfig.disableOnDamage && e.entity.health > e.health) {
                                if (FreeCamConfig.sendMessage) ExeClient.send {
                                        "You took damage!" {
                                                color = EnumChatFormatting.YELLOW
                                        }
                                }
                                
                                disable()
                        }
                }
                
                onEvent(Dispatchers.Default) { e: PacketEvent.Server.S43.Pre ->
                        if (FreeCamConfig.disableOnSeverCameraChange) {
                                if (FreeCamConfig.sendMessage) ExeClient.send {
                                        "Server is trying to change your camera entity!" {
                                                color = EnumChatFormatting.YELLOW
                                        }
                                }
                                
                                disable()
                        } else {
                                previousEntity = e.packet.getEntity(
                                        cast<InjectNetHandlerPlayClient>(e.handler)._clientWorldController_exec
                                )
                                e.cancel()
                        }
                }
                
                onEvent(Dispatchers.Default) { _: WorldClientEvent.Load.Pre ->
                        disable()
                }
                
                onEvent(Dispatchers.Default) { _: ExeCFeatureConfigEvent.Inactive<FreeCamConfig> ->
                        disable()
                }
        }
        
        override fun initialize() {}
        
        override val config: FreeCamConfig
                get() = ExeClientConfig.configFreeCamera
        
        val allowTogglePerspective: Boolean
                @[JvmStatic JvmName("allowTogglePerspective")]
                inline get() = FreeCamConfig.allowTogglePerspective
        
        val allowCameraInteract: Boolean
                @[JvmStatic JvmName("allowCameraInteract")]
                inline get() = FreeCamConfig.allowCameraInteract
        
        val allowPlayerInteract: Boolean
                @[JvmStatic JvmName("allowPlayerInteract")]
                inline get() = FreeCamConfig.allowPlayerInteract
        
        val enableWaterAndLavaOverlay: Boolean
                @[JvmStatic JvmName("enableWaterAndLavaOverlay")]
                inline get() = FreeCamConfig.enableWaterAndLavaOverlay
        
        @get:[JvmStatic JvmName("isEnabled")]
        var enabled: Boolean = false
                private set
        
        override val active: Boolean
                get() = config.active && enabled
        
        @get:JvmStatic
        var camera: FreeCamEntity? = null
                private set
        
        private var previousView: Int = 0
        
        @get:JvmStatic
        var previousEntity: Entity? = null
                private set
        
        @get:[JvmStatic JvmName("isControllingPlayer")]
        var controllingPlayer = true
                private set
        
        private val toggleControllerLock = Any()
        
        private fun setControlPlayer() {
                if (controllingPlayer) return
                
                synchronized(toggleControllerLock) {
                        if (controllingPlayer) return
                        controllingPlayer = true
                        
                        val camera = camera ?: return
                        
                        camera.movementInput = MovementInput()
                        player.movementInput = MovementInputFromOptions(mc.gameSettings)
                }
        }
        
        private fun setControlCamera() {
                if (!controllingPlayer) return
                
                synchronized(toggleControllerLock) {
                        if (!controllingPlayer) return
                        controllingPlayer = false
                        
                        val camera = camera ?: return
                        
                        player.movementInput = MovementInput()
                        camera.movementInput = FreeCamMovementInput(mc.gameSettings)
                }
        }
        
        @Synchronized
        fun toggleController() {
                if (!enabled) return
                if (!FreeCamConfig.allowToggleController) return
                // lock
                if (controllingPlayer) {
                        setControlCamera()
                } else {
                        setControlPlayer()
                }
        }
        
        private val toggleLock = Any()
        
        private val MESSAGE_ENABLED = buildChat {
                empty +
                "Free Camera " +
                ExeCFeature.MESSAGE_ENABLED
        }
        
        private val MESSAGE_DISABLED = buildChat {
                empty +
                "Free Camera " +
                ExeCFeature.MESSAGE_DISABLED
        }
        
        @Synchronized
        private fun enable() {
                if (enabled) return
                
                synchronized(toggleLock) {
                        if (enabled) return
                        enabled = true
                        
                        val camera = FreeCamEntity()
                        camera.copyLocationAndAnglesFrom(player)
                        
                        world.addEntityToWorld(-114514, camera)
                        FreeCam.camera = camera
                        
                        setControlCamera()
                        
                        previousView = mc.gameSettings.thirdPersonView
                        mc.gameSettings.thirdPersonView = 0
                        
                        previousEntity = mc.renderViewEntity
                        mc.renderViewEntity = camera
                        
                        mc.entityRenderer.loadEntityShader(camera)
                        mc.renderGlobal.setDisplayListEntitiesDirty()
                }
                
                if (FreeCamConfig.sendMessage) {
                        ExeClient.send(MESSAGE_ENABLED)
                }
        }
        
        @Synchronized
        private fun disable() {
                if (!enabled) return
                
                synchronized(toggleLock) {
                        if (!enabled) return
                        enabled = false
                        
                        setControlPlayer()
                        
                        world.removeEntity(camera)
                        camera = null
                        
                        mc.gameSettings.thirdPersonView = previousView
                        previousView = 0
                        
                        mc.renderViewEntity = previousEntity
                        previousEntity = null
                        
                        mc.entityRenderer.loadEntityShader(
                                if (previousView == 0) previousEntity else null
                        )
                        mc.renderGlobal.setDisplayListEntitiesDirty()
                }
                
                if (FreeCamConfig.sendMessage) {
                        ExeClient.send(MESSAGE_DISABLED)
                }
        }
        
        @Synchronized
        fun toggle() {
                if (!config.active) return
                
                if (enabled) {
                        disable()
                } else {
                        enable()
                }
        }
}