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

import net.llvg.exec.mixin.inject.InjectNetHandlerPlayClient;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S43PacketCamera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.llvg.exec.mixin.callback.CallbackNetHandlerPlayClient.*;
import static net.llvg.loliutils.exception.TypeCast.cast;

@Mixin (NetHandlerPlayClient.class)
public abstract class MixinNetHandlerPlayClient implements INetHandlerPlayClient, InjectNetHandlerPlayClient {
        @Shadow
        private WorldClient clientWorldController;
        
        @Inject (method = "handleMultiBlockChange", at = @At (value = "INVOKE_ASSIGN", target = "Lnet/minecraft/network/PacketThreadUtil;checkThreadAndEnqueue(Lnet/minecraft/network/Packet;Lnet/minecraft/network/INetHandler;Lnet/minecraft/util/IThreadListener;)V"), cancellable = true)
        private void handleMultiBlockChangeInject(S22PacketMultiBlockChange packetIn, CallbackInfo ci) {
                if (postPacketEventServerS22Pre(cast(this), packetIn)) ci.cancel();
        }
        
        @Inject (method = "handleBlockChange", at = @At (value = "INVOKE_ASSIGN", target = "Lnet/minecraft/network/PacketThreadUtil;checkThreadAndEnqueue(Lnet/minecraft/network/Packet;Lnet/minecraft/network/INetHandler;Lnet/minecraft/util/IThreadListener;)V"), cancellable = true)
        private void handleBlockChangeInject(S23PacketBlockChange packetIn, CallbackInfo ci) {
                if (postPacketEventServerS23Pre(cast(this), packetIn)) ci.cancel();
        }
        
        @Inject (method = "handleCamera", at = @At (value = "INVOKE_ASSIGN", target = "Lnet/minecraft/network/PacketThreadUtil;checkThreadAndEnqueue(Lnet/minecraft/network/Packet;Lnet/minecraft/network/INetHandler;Lnet/minecraft/util/IThreadListener;)V"), cancellable = true)
        private void handleCameraInject(S43PacketCamera packetIn, CallbackInfo ci) {
                if (postPacketEventServerS43Pre(cast(this), packetIn)) ci.cancel();
        }
        
        @Unique
        @Override
        public WorldClient get_clientWorldController_exec() {
                return clientWorldController;
        }
}
