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

import net.llvg.exec.mixin.inject.InjectAbstractClientPlayer;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin (AbstractClientPlayer.class)
public abstract class MixinAbstractClientPlayer extends EntityPlayer implements InjectAbstractClientPlayer {
        @Shadow
        protected abstract NetworkPlayerInfo getPlayerInfo();
        
        @SuppressWarnings ("DataFlowIssue")
        private MixinAbstractClientPlayer() {
                super(null, null);
        }
        
        @Override
        public NetworkPlayerInfo getPlayerInfo_exec() {
                return getPlayerInfo();
        }
}
