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

package net.llvg.exec.utils.skyblock.catacombs.map

import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.InputStreamReader
import java.io.Reader
import net.llvg.exec.ExeClient
import net.llvg.exec.utils.builder.BuilderBase
import net.llvg.exec.utils.builder.notNull
import net.llvg.exec.utils.classNameLogger
import net.llvg.exec.utils.json.nextTyped
import net.llvg.exec.utils.json.value

data class RoomData(
        val name: String,
        var type: RoomType,
        val cores: Set<Int>,
        val crypts: Int,
        val secrets: Int,
        val mimicPoses: Int,
) {
        class Builder private constructor() : BuilderBase<RoomData> {
                private var name: String? = null
                private var type: RoomType? = null
                private val cores: MutableSet<Int> = LinkedHashSet()
                private var crypts: Int = 0
                private var secrets: Int = 0
                private var mimicPoses: Int = 0
                
                fun name(name: String): Builder =
                        apply { this.name = name }
                
                fun type(type: RoomType): Builder =
                        apply { this.type = type }
                
                fun core(core: Int): Builder =
                        apply { this.cores += core }
                
                fun crypts(crypts: Int): Builder =
                        apply { this.crypts = crypts }
                
                fun secrets(secrets: Int): Builder =
                        apply { this.secrets = secrets }
                
                fun mimicPoses(mimicPoses: Int): Builder =
                        apply { this.mimicPoses = mimicPoses }
                
                override fun build(): RoomData = RoomData(
                        notNull(name, "name"),
                        notNull(type, "type"),
                        cores,
                        crypts,
                        secrets,
                        mimicPoses
                )
                
                companion object {
                        fun create(): Builder =
                                Builder()
                }
        }
        
        companion object {
                init {
                        rooms
                }
                
                @JvmStatic
                fun builder(): Builder =
                        Builder.create()
                
                @JvmStatic
                operator fun get(
                        hash: Int
                ): RoomData? =
                        rooms.find { hash in it.cores }
        }
}

private val logger = classNameLogger<RoomData>()
private const val roomDataFileName = "catacombs-room-data.json"
private val roomDataFile: File = File(ExeClient.directory, roomDataFileName)
private val rooms: List<RoomData> = run {
        val internalPath = "/${ExeClient.MIXIN_ID}/$roomDataFileName"
        val internalUrl = RoomData::class.java.getResource(internalPath)
        requireNotNull(internalUrl) { "resource '$internalPath' is missing" }
        
        val input: Reader =
                if (roomDataFile.isFile) {
                        FileReader(roomDataFile)
                } else {
                        roomDataFile.parentFile.mkdirs()
                        roomDataFile.delete()
                        
                        InputStreamReader(internalUrl.openStream())
                }
        
        
        val result: MutableList<RoomData> = ArrayList()
        
        JsonReader(input).use {
                with(it) {
                        beginArray()
                        while (hasNext()) try {
                                result += nextTyped(RoomDataAdapter)
                        } catch (e: Throwable) {
                                logger.info("An error occur during reading room data from {}", input, e)
                        }
                        endArray()
                }
        }
        
        result.sortBy(RoomData::name)
        
        JsonWriter(FileWriter(roomDataFile)).use {
                with(it) {
                        isLenient = true
                        serializeNulls = false
                        setIndent("\t")
                        beginArray()
                        for (i in result) {
                                value(i, RoomDataAdapter)
                        }
                        endArray()
                }
        }
        
        result
}