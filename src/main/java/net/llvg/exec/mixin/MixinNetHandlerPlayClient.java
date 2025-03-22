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
import net.llvg.exec.event.events.ServerCameraChangeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin (NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient {
        @Redirect (method = "handleCamera", at = @At (value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;setRenderViewEntity(Lnet/minecraft/entity/Entity;)V"))
        private void handleCameraRedirect(Minecraft instance, Entity viewingEntity) {
                ServerCameraChangeEvent event = new ServerCameraChangeEvent(instance, viewingEntity);
                ExeCEventManager.post(ServerCameraChangeEvent.class, event, true);
                viewingEntity = event.getEntity();
                if (viewingEntity != null && viewingEntity != instance.getRenderViewEntity()) {
                        instance.setRenderViewEntity(viewingEntity);
                }
        }
}
