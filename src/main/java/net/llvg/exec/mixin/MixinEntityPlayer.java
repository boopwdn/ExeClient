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

import net.llvg.exec.inject.EntityPlayerInject;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryEnderChest;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin (EntityPlayer.class)
public abstract class MixinEntityPlayer extends EntityLivingBase implements EntityPlayerInject {
        @Shadow
        private InventoryEnderChest theInventoryEnderChest;
        
        private MixinEntityPlayer() {
                super(null);
        }
        
        @Unique
        @Override
        @NotNull
        public InventoryEnderChest getExec_theInventoryEnderChest() {
                return theInventoryEnderChest;
        }
        
        @Unique
        @Override
        public void setExec_theInventoryEnderChest(@NotNull InventoryEnderChest theInventoryEnderChest) {
                this.theInventoryEnderChest = theInventoryEnderChest;
        }
        
        @Unique
        @Override
        public void exec_EntityPlayer_super_onLivingUpdate() {
                super.onLivingUpdate();
        }
}
