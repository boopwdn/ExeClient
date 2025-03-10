package net.llvg.exec.config.freecam

import net.llvg.exec.config.ExeClientConfig
import net.llvg.exec.config.ExeFeatureConfig
import net.llvg.exec.features.freecam.FreeCam

object FreeCamConfig : ExeFeatureConfig(
        ExeClientConfig::active,
        FreeCam,
        "Free Camera",
        "exec-free_camera-config.json"
) {
}