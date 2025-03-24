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
import net.llvg.exec.event.events.UserHealthChangeEvent;
import net.llvg.exec.inject.EntityLivingBaseInject;
import net.llvg.exec.utils.MinecraftUtils;
import net.llvg.loliutils.exception.TypeCast;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin (EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends Entity implements EntityLivingBaseInject {
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
                        UserHealthChangeEvent event = new UserHealthChangeEvent(TypeCast.uncheckedCast(this), health);
                        ExeCEventManager.post(UserHealthChangeEvent.class, event, true);
                }
        }
        
        @Unique
        @Override
        @NotNull
        public Map<Integer, PotionEffect> getExec_activePotionsMap() {
                return activePotionsMap;
        }
        
        @Unique
        @Override
        public void setExec_activePotionsMap(@NotNull Map<Integer, PotionEffect> activePotionsMap) {
                this.activePotionsMap = activePotionsMap;
        }
}
