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

import net.llvg.exec.feature.freecam.FreeCam;
import net.llvg.exec.utils.JavaUtils;
import net.minecraft.entity.Entity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin (World.class)
public abstract class MixinWorld implements IBlockAccess {
        @ModifyVariable (method = "checkNoEntityCollision(Lnet/minecraft/util/AxisAlignedBB;Lnet/minecraft/entity/Entity;)Z", at = @At ("STORE"), index = 3)
        private List<Entity> checkNoEntityCollisionModifyVariable(List<Entity> list) {
                JavaUtils.onNotNull(FreeCam.getCamera(), it -> list.removeIf(it::isEntityEqual));
                return list;
        }
}
