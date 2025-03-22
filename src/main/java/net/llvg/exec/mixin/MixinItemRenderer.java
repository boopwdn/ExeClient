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
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.ItemRenderer;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin (ItemRenderer.class)
public class MixinItemRenderer {
        @Redirect (method = "renderItemInFirstPerson", at = @At (value = "FIELD", target = "Lnet/minecraft/client/Minecraft;thePlayer:Lnet/minecraft/client/entity/EntityPlayerSP;", opcode = Opcodes.GETFIELD))
        private EntityPlayerSP renderItemInFirstPersonRedirect(Minecraft instance) {
                if (FreeCam.isEnabled()) {
                        return FreeCam.getCamera();
                }
                return instance.thePlayer;
        }
}
