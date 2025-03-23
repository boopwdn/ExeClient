package net.llvg.exec.config

import cc.polyfrost.oneconfig.config.elements.SubConfig
import net.llvg.exec.features.ExeFeature

abstract class ExeFeatureConfig(
        @Transient private val isOwnerActive: () -> Boolean,
        @Transient private val feature: ExeFeature,
        name: String,
        configFile: String,
        icon: String? = null,
        enabled: Boolean = false,
        canToggle: Boolean = true
) : SubConfig(name, configFile, icon, enabled, canToggle) {
        @Transient
        private var active = isOwnerActive() && enabled
        
        fun active(
        ): Boolean = active
        
        override fun initialize() {
                super.initialize()
                feature.initialize()
        }
        
        override fun save() {
                super.save()
                val wasActive = active
                active = isOwnerActive() && enabled
                if (wasActive != active) with(feature) {
                        if (active)
                                reactive()
                        else
                                inactive()
                }
        }
}