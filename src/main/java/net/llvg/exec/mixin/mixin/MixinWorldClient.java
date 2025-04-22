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

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.llvg.exec.mixin.callback.CallBackWorldClient.postWorldClientEventBlockChangeByServerPre;
import static net.llvg.loliutils.exception.TypeCast.cast;

@Mixin (WorldClient.class)
public abstract class MixinWorldClient extends World {
        @SuppressWarnings ("DataFlowIssue")
        private MixinWorldClient() {
                super(null, null, null, null, false);
        }
        
        @Inject (method = "invalidateRegionAndSetBlock", at = @At ("HEAD"), cancellable = true)
        private void invalidateRegionAndSetBlockInject(BlockPos pos, IBlockState state, CallbackInfoReturnable<Boolean> cir) {
                if (postWorldClientEventBlockChangeByServerPre(cast(this), pos, state)) {
                        cir.setReturnValue(false);
                }
        }
}
