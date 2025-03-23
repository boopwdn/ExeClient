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
import net.llvg.exec.utils.MinecraftUtils;
import net.llvg.exec.utils.NullUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin (Entity.class)
public abstract class MixinEntity implements ICommandSender, ICapabilitySerializable<NBTTagCompound> {
        @Inject (method = "setAngles", at = @At ("HEAD"), cancellable = true)
        private void setAnglesInject(float yaw, float pitch, CallbackInfo ci) {
                if (FreeCam.isEnabled() && isEntityEqual(MinecraftUtils.player()) && !FreeCam.isControllingPlayer()) {
                        NullUtils.onNotNull(FreeCam.getCamera(), it -> it.setAngles(yaw, pitch));
                        ci.cancel();
                }
        }
        
        @Shadow
        public abstract boolean isEntityEqual(Entity entityIn);
        
        @Inject (method = "applyEntityCollision", at = @At ("HEAD"), cancellable = true)
        private void applyEntityCollisionInject(Entity entityIn, CallbackInfo ci) {
                if (FreeCam.isEnabled() && entityIn == FreeCam.getCamera()) {
                        ci.cancel();
                }
        }
}
