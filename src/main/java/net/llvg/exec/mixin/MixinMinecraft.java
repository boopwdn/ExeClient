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

package net.llvg.exec.mixin;

import net.llvg.exec.event.ExeCEventManager;
import net.llvg.exec.event.events.WorldLoadEvent;
import net.llvg.exec.features.freecam.FreeCam;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin (Minecraft.class)
public class MixinMinecraft {
        @Redirect (method = "runTick", slice = @Slice (from = @At (value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;keyBindTogglePerspective:Lnet/minecraft/client/settings/KeyBinding;")), at = @At (value = "INVOKE", target = "Lnet/minecraft/client/settings/KeyBinding;isPressed()Z", ordinal = 0))
        private boolean runTickRedirect(KeyBinding instance) {
                if (FreeCam.isEnabled()) {
                        return FreeCam.allowTogglePerspective() && instance.isPressed();
                }
                return instance.isPressed();
        }
        
        @Inject (method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At ("HEAD"))
        private void loadWorldInject(WorldClient worldClientIn, String loadingMessage, CallbackInfo ci) {
                WorldLoadEvent event = new WorldLoadEvent(worldClientIn);
                ExeCEventManager.post(WorldLoadEvent.class, event, true);
        }
        
        @Inject (method = "clickMouse", at = @At ("HEAD"), cancellable = true)
        private void clickMouseInject(CallbackInfo ci) {
                if (FreeCam.isEnabled()) {
                        if (exec$allowInteract()) { return; }
                        ci.cancel();
                }
        }
        
        @Inject (method = "rightClickMouse", at = @At ("HEAD"), cancellable = true)
        private void rightClickMouseInject(CallbackInfo ci) {
                if (FreeCam.isEnabled()) {
                        if (exec$allowInteract()) { return; }
                        ci.cancel();
                }
        }
        
        @Inject (method = "middleClickMouse", at = @At ("HEAD"), cancellable = true)
        private void middleClickMouseInject(CallbackInfo ci) {
                if (FreeCam.isEnabled()) {
                        if (exec$allowInteract()) { return; }
                        ci.cancel();
                }
        }
        
        @ModifyVariable (method = "sendClickBlockToController", at = @At ("HEAD"), index = 1, argsOnly = true)
        private boolean sendClickBlockToControllerModifyVariable(boolean leftClick) {
                return leftClick && exec$allowInteract();
        }
        
        @Unique
        private static boolean exec$allowInteract() {
                return FreeCam.isControllingPlayer() ? FreeCam.allowPlayerInteract() : FreeCam.allowCameraInteract();
        }
}
