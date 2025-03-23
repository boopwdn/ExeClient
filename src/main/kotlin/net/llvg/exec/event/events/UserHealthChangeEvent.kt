package net.llvg.exec.event.events

import net.llvg.exec.event.ExeCEvent
import net.minecraft.entity.EntityLivingBase

data class UserHealthChangeEvent(
        val instance: EntityLivingBase,
        val health: Float
) : ExeCEvent