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
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin (EntityRenderer.class)
public class MixinEntityRenderer {
        @Redirect (method = "getMouseOver", at = @At (value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getRenderViewEntity()Lnet/minecraft/entity/Entity;"))
        private Entity getMouseOverRedirect(Minecraft instance) {
                if (FreeCam.isEnabled()) {
                        return FreeCam.isControllingPlayer() ? FreeCam.getPreviousEntity() : FreeCam.getCamera();
                }
                return instance.getRenderViewEntity();
        }
        
        @ModifyVariable (method = "getMouseOver", at = @At ("STORE"))
        private List<Entity> getMouseOverModifyVariable(List<Entity> entities) {
                entities.removeIf(EntityPlayerSP.class::isInstance);
                return entities;
        }
        
        @ModifyArg (method = "renderWorldPass", at = @At (value = "INVOKE", target = "Lnet/minecraft/client/renderer/ActiveRenderInfo;updateRenderInfo(Lnet/minecraft/entity/player/EntityPlayer;Z)V"), index = 0)
        private EntityPlayer renderWorldPassModifyArg(EntityPlayer entityplayerIn) {
                if (FreeCam.isEnabled()) {
                        return FreeCam.getCamera();
                }
                return entityplayerIn;
        }
        
        @ModifyVariable (method = { "setupFog", "updateFogColor" }, at = @At ("STORE"))
        private Block setupFog_updateFogColorModifyVariable(Block block) {
                if (FreeCam.isEnabled() && !FreeCam.enableWaterAndLavaOverlay()) {
                        if (block.getMaterial() == Material.water || block.getMaterial() == Material.lava) {
                                return Blocks.air;
                        }
                }
                return block;
        }
}
