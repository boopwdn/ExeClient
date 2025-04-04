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

import net.llvg.exec.mixin.callback.CallbackEntityLivingBase;
import net.llvg.exec.mixin.inject.InjectEntityLivingBase;
import net.llvg.exec.utils.MinecraftUtils;
import net.llvg.loliutils.exception.TypeCast;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin (EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends Entity implements InjectEntityLivingBase {
        @Final
        @Shadow
        @Mutable
        private Map<Integer, PotionEffect> activePotionsMap;
        
        private MixinEntityLivingBase() {
                super(null);
        }
        
        @Inject (method = "setHealth", at = @At ("HEAD"))
        private void setHealthInject(float health, CallbackInfo ci) {
                if (isEntityEqual(MinecraftUtils.mc().thePlayer)) {
                        CallbackEntityLivingBase.postUserHealthChangeEvent(TypeCast.cast(this), health);
                }
        }
        
        @Override
        public Map<Integer, PotionEffect> getActivePotionsMap$exec() {
                return activePotionsMap;
        }
        
        @Override
        public void setActivePotionsMap$exec(Map<Integer, PotionEffect> o) {
                activePotionsMap = o;
        }
}
