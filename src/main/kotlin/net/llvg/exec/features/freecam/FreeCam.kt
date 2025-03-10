package net.llvg.exec.features.freecam

import net.llvg.exec.config.ExeFeatureConfig
import net.llvg.exec.config.freecam.FreeCamConfig
import net.llvg.exec.features.ExeFeature

object FreeCam : ExeFeature {
        override fun initialize() {}
        
        override fun reactive() {}
        
        override fun inactive() {}
        
        override val config: ExeFeatureConfig
                get() = FreeCamConfig
}