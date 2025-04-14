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

package net.llvg.exec.mixin.mixin;

import net.llvg.exec.feature.freecam.FreeCam;
import net.llvg.exec.utils.JavaUtils;
import net.llvg.exec.vanilla.utils.MinecraftUtils;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin (EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer {
        @SuppressWarnings ("DataFlowIssue")
        private MixinEntityPlayerSP() {
                super(null, null);
        }
        
        @Inject (method = "swingItem", at = @At ("HEAD"))
        private void swingItemInject(CallbackInfo ci) {
                if (FreeCam.isEnabled() && isEntityEqual(MinecraftUtils.player())) {
                        JavaUtils.onNotNull(FreeCam.getCamera(), EntityPlayerSP::swingItem);
                }
        }
        
        @Inject (method = "isCurrentViewEntity", at = @At ("HEAD"), cancellable = true)
        private void isCurrentViewEntityInject(CallbackInfoReturnable<Boolean> cir) {
                if (FreeCam.isEnabled() && isEntityEqual(FreeCam.getPreviousEntity())) {
                        cir.setReturnValue(true);
                }
        }
        
        @Inject(method = "isUser", at = @At("HEAD"), cancellable = true)
        private void isUserInject(CallbackInfoReturnable<Boolean> cir) {
                if (FreeCam.isEnabled()) {
                        cir.setReturnValue(isEntityEqual(FreeCam.getCamera()));
                }
        }
}
