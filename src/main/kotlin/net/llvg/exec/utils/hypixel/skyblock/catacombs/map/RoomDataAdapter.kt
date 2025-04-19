/*
 * Copyright (C) 2025-2025 Water-OR
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

package net.llvg.exec.utils.hypixel.skyblock.catacombs.map

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import net.llvg.exec.utils.json.nextTyped
import net.llvg.exec.utils.json.value

object RoomDataAdapter : TypeAdapter<RoomData>() {
        override fun write(
                writer: JsonWriter,
                value: RoomData?
        ) {
                with(writer) {
                        if (value === null) {
                                nullValue()
                                return
                        }
                        
                        value.apply {
                                beginObject()
                                name("name").value(name)
                                name("type").value(type, RoomTypeAdapter)
                                name("cores").apply {
                                        beginArray()
                                        for (core in cores) {
                                                value(core)
                                        }
                                        endArray()
                                }
                                name("crypts").value(crypts)
                                name("secrets").value(secrets)
                                name("mimicPoses").value(mimicPoses)
                                endObject()
                        }
                }
        }
        
        override fun read(
                reader: JsonReader
        ): RoomData =
                with(reader) {
                        RoomData.builder().apply {
                                beginObject()
                                while (hasNext()) when (nextName()) {
                                        "name"          -> name(nextString())
                                        "type"          -> type(nextTyped(RoomTypeAdapter))
                                        "cores"         -> {
                                                beginArray()
                                                while (hasNext()) {
                                                        core(nextInt())
                                                }
                                                endArray()
                                        }
                                        
                                        "crypts"        -> crypts(nextInt())
                                        "secrets"       -> secrets(nextInt())
                                        "mimicPoses",
                                        "trappedChests" -> mimicPoses(nextInt())
                                        
                                        else            -> skipValue()
                                }
                                endObject()
                        }.build()
                }
}