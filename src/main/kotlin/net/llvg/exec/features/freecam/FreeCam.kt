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

import kotlinx.coroutines.Dispatchers
import net.llvg.exec.ExeClient
import net.llvg.exec.config.ExeFeatureConfig
import net.llvg.exec.config.freecam.FreeCamConfig
import net.llvg.exec.event.events.EntityLivingBaseEvent
import net.llvg.exec.event.events.PacketEvent
import net.llvg.exec.event.events.WorldClientEvent
import net.llvg.exec.event.onEvent
import net.llvg.exec.features.ExeFeature
import net.llvg.exec.mixin.inject.InjectNetHandlerPlayClient
import net.llvg.exec.utils.chat_component.buildChatComponent
import net.llvg.exec.utils.mc
import net.llvg.exec.utils.player
import net.llvg.exec.utils.world
import net.llvg.loliutils.exception.asNotNull
import net.llvg.loliutils.exception.cast
import net.minecraft.entity.Entity
import net.minecraft.util.EnumChatFormatting
import net.minecraft.util.MovementInput
import net.minecraft.util.MovementInputFromOptions

object FreeCam : ExeFeature {
        init {
                onEvent(dispatcher = Dispatchers.Default) { e: EntityLivingBaseEvent.HealthChange.Pre ->
                        if (e.entity !== player) return@onEvent
                        
                        if (FreeCamConfig.disableOnDamage && e.entity.health > e.health) {
                                if (FreeCamConfig.sendMessage) ExeClient.send {
                                        text("You took damage!") {
                                                color = EnumChatFormatting.YELLOW
                                        }
                                }
                                
                                disable()
                        }
                }
                
                onEvent(dispatcher = Dispatchers.Default) { e: PacketEvent.Server.S43.Pre ->
                        if (FreeCamConfig.disableOnSeverCameraChange) {
                                if (FreeCamConfig.sendMessage) ExeClient.send {
                                        text("Server is trying to change your camera entity!") {
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
                
                onEvent(dispatcher = Dispatchers.Default) { _: WorldClientEvent.Load.Pre ->
                        disable()
                }
        }
        
        override fun initialize() {}
        
        override fun reactive() {}
        
        override fun inactive() {
                disable()
        }
        
        override val config: ExeFeatureConfig
                get() = FreeCamConfig
        
        @get:[JvmStatic JvmName("isEnabled")]
        var enabled: Boolean = false
                private set
        
        override val active: Boolean
                get() = enabled
        
        @get:JvmStatic
        var camera: FreeCamEntity? = null
                private set
        
        private var previousView: Int = 0
        
        @get:JvmStatic
        var previousEntity: Entity? = null
                private set
        
        @get:[JvmStatic JvmName("isControllingPlayer")]
        var controllingPlayer = false
                private set
        
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
        
        private val toggleLock = Any()
        
        @Synchronized
        fun toggle() {
                if (!config.active()) return
                
                if (enabled) {
                        disable()
                } else {
                        enable()
                }
        }
        
        @Synchronized
        fun toggleController() {
                if (!config.active()) return
                if (!enabled) return
                val camera = camera ?: return
                if (!FreeCamConfig.allowToggleController) return
                // lock
                synchronized(toggleLock) {
                        if (controllingPlayer) {
                                player.movementInput = MovementInput()
                                camera.movementInput = FreeCamMovementInput(mc.gameSettings)
                                
                                controllingPlayer = false
                        } else {
                                camera.movementInput = MovementInput()
                                player.movementInput = MovementInputFromOptions(mc.gameSettings)
                                
                                controllingPlayer = true
                        }
                }
        }
        
        private val MESSAGE_ENABLED = buildChatComponent {
                empty +
                text("Free Camera") +
                space +
                ExeFeature.MESSAGE_ENABLED
        }
        
        @Synchronized
        private fun enable() {
                if (enabled) return
                // lock
                synchronized(toggleLock) {
                        // disable player movement
                        player.movementInput = MovementInput()
                        controllingPlayer = false
                        // create new camera
                        val newCameraEntity = FreeCamEntity()
                        newCameraEntity.copyLocationAndAnglesFrom(player)
                        // put camera to world
                        newCameraEntity.movementInput = FreeCamMovementInput(mc.gameSettings)
                        world.addEntityToWorld(-114514, newCameraEntity)
                        camera = newCameraEntity
                        // store previous view
                        previousView = mc.gameSettings.thirdPersonView
                        mc.gameSettings.thirdPersonView = 0
                        // store previous entity
                        previousEntity = mc.renderViewEntity
                        mc.renderViewEntity = camera
                        // reload shader
                        mc.entityRenderer.loadEntityShader(newCameraEntity)
                        mc.renderGlobal.setDisplayListEntitiesDirty()
                        // set enabled
                        enabled = true
                }
                // send message
                if (FreeCamConfig.sendMessage) {
                        ExeClient.send(MESSAGE_ENABLED)
                }
        }
        
        private val MESSAGE_DISABLED = buildChatComponent {
                empty +
                text("Free Camera") +
                space +
                ExeFeature.MESSAGE_DISABLED
        }
        
        @Synchronized
        private fun disable() {
                if (!enabled) return
                // lock
                synchronized(toggleLock) {
                        // remove camera from world
                        camera.asNotNull.movementInput = MovementInput()
                        world.removeEntity(camera)
                        camera = null
                        // enable player movement
                        player.movementInput = MovementInputFromOptions(mc.gameSettings)
                        controllingPlayer = true
                        // put previous view
                        mc.gameSettings.thirdPersonView = previousView
                        previousView = 0
                        // put previous entity
                        mc.renderViewEntity = previousEntity
                        previousEntity = null
                        // reload shader
                        mc.entityRenderer.loadEntityShader(
                                if (mc.gameSettings.thirdPersonView == 0) previousEntity else null
                        )
                        mc.renderGlobal.setDisplayListEntitiesDirty()
                        // set disabled
                        enabled = false
                }
                // send message
                if (FreeCamConfig.sendMessage) {
                        ExeClient.send(MESSAGE_DISABLED)
                }
        }
}