package net.llvg.exec.features

import net.llvg.exec.config.ExeFeatureConfig
import net.llvg.exec.event.ExeCEventListenable

interface ExeFeature : ExeCEventListenable {
        fun initialize()
        
        fun reactive()
        
        fun inactive()
        
        val config: ExeFeatureConfig
        
        override val active: Boolean
                get() = config.active()
}