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

package net.llvg.exec.feature.catacombs_scan

import net.llvg.exec.api.config.ExeCFeatureConfig
import net.llvg.exec.api.config.ExeClientConfig

object CatacombsScanConfig : ExeCFeatureConfig<CatacombsScanConfig>(
        "Catacombs Scan",
        "exec-catacombs_scan-config.json",
        canToggle = false
) {
        override val self: CatacombsScanConfig
                get() = this
        
        override fun active(): Boolean =
                ExeClientConfig.active() && super.active()
}