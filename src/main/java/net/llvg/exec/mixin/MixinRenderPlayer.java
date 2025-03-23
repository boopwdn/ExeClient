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

import net.llvg.exec.features.freecam.FreeCam;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin (RenderPlayer.class)
public abstract class MixinRenderPlayer {
        @Redirect (method = "doRender(Lnet/minecraft/client/entity/AbstractClientPlayer;DDDFF)V", at = @At (value = "INVOKE", target = "Lnet/minecraft/client/entity/AbstractClientPlayer;isUser()Z"))
        private boolean doRenderRedirect(AbstractClientPlayer instance) {
                if (FreeCam.isEnabled()) {
                        return instance == FreeCam.getCamera();
                }
                return instance.isUser();
        }
}
