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

package net.llvg.exec.hypixel.skyblock.catacombs.map.scan

import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import net.llvg.exec.ExeClient
import net.llvg.exec.api.event.ExeCEventListenable
import net.llvg.exec.api.event.onEvent
import net.llvg.exec.feature.catacombs_scan.CatacombsScan
import net.llvg.exec.utils.classNameLogger
import net.llvg.exec.hypixel.skyblock.catacombs.map.RoomData
import net.llvg.exec.hypixel.skyblock.catacombs.map.RoomType
import net.llvg.exec.utils.vector.Vec2I
import net.llvg.exec.vanilla.block.id
import net.llvg.exec.vanilla.event.WorldClientEvent
import net.llvg.exec.vanilla.utils.chat_component.ChatColor
import net.llvg.exec.vanilla.utils.mc
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.world.chunk.Chunk

@Suppress("MemberVisibilityCanBePrivate")
object CatacombsScanner : ExeCEventListenable {
        init {
                onEvent(Dispatchers.Default) { _: WorldClientEvent.Load.Pre ->
                        roomScanJob?.run {
                                cancel()
                        }
                }
                
                initialize()
        }
        
        private val scope: CoroutineScope = CoroutineScope(SupervisorJob())
        
        val roomScanning: Boolean
                get() = roomScanJob !== null
        
        fun scan(): Job? {
                if (!CatacombsScan.checkInCatacombs() || !CatacombsScan.checkNotInBoss()) return null
                
                roomScanLock.readLock().withLock {
                        if (roomScanning) return null
                }
                val job: Job
                roomScanLock.writeLock().withLock {
                        if (roomScanning) return null
                        job = scope.launch(Dispatchers.Default) c@{
                                try {
                                        scanRoomInternalSuspend()
                                } catch (e: Throwable) {
                                        ExeClient.send {
                                                "An error occur during scanning catacombs rooms, see logs for more information"()
                                                .`--color`(ChatColor.DARK_RED)
                                        }
                                        logger.error("An error occur during scanning catacombs rooms", e)
                                        throw CatacombsScanFailedException(e)
                                }
                        }
                        roomScanJob = job
                }
                job.invokeOnCompletion {
                        roomScanLock.writeLock().withLock {
                                roomScanJob = null
                        }
                }
                return job
        }
        
        fun afterCurrentScan(
                action: (Throwable?) -> Unit
        ) {
                if (!CatacombsScan.checkInCatacombs() || !CatacombsScan.checkNotInBoss()) return
                
                roomScanLock.readLock().withLock {
                        roomScanJob
                        ?.invokeOnCompletion {
                                action(it)
                        }
                        ?: action(null)
                }
        }
        
        fun scanAndHashPos(
                pos: Vec2I
        ): Int? {
                return StringBuilder(150)
                .apply {
                        val (x, z) = pos
                        val world = mc.theWorld ?: return null
                        val chunk = world.getChunkFromChunkCoords(x shr 4, z shr 4)
                        if (!chunk.isLoaded) return null
                        
                        val height = chunk.getHeightValue(x and 15, z and 15).coerceIn(11..140)
                        append(CharArray(140 - height) { '0' })
                        var bedrock = 0
                        for (y in height downTo 12) {
                                val id = chunk.getBlock(x, y, z).id
                                if (id == 0 && bedrock > 1 && y < 69) {
                                        append(CharArray(y - 11) { '0' })
                                        break
                                }
                                
                                if (id == 7) {
                                        ++bedrock
                                } else {
                                        bedrock = 0
                                        if (id in hashBlockIdException) continue
                                }
                                append(id)
                        }
                }
                .toString()
                .hashCode()
        }
}

private fun initialize() {}

private val logger = classNameLogger<CatacombsScan>()

private val hashBlockIdException = intArrayOf(
        5, // planks
        54, // chest
        146, // trapped_chest
)

private val cornerSideBlockIdInclusion = intArrayOf(
        0, // air
        159, // stained_hardened_clay
)
private var roomScanJob: Job? = null
private var roomScanLock: ReadWriteLock = ReentrantReadWriteLock()

// do not inline this or the code will be much more stupid
private fun Chunk.getTopYOf(
        pos: Vec2I
): Int {
        val (x, z) = pos
        val y = getHeightValue(x and 15, z and 15) - 1
        
        return if (getBlock(x, y, z) == Blocks.gold_block) y - 1 else y
}

private suspend fun scanRoomInternalSuspend() = coroutineScope {
        val dataToEntries: MutableMap<RoomData, MutableList<RoomEntry>> = HashMap()
        val jobs = mutableListOf<Job>()
        
        // scan new rooms
        for (entry in CatacombsMap.roomEntries) {
                if (entry.info !== null) continue
                jobs += launch {
                        CatacombsScanner.scanAndHashPos(entry.pos)?.let(RoomData::get)?.let { data ->
                                val entries = dataToEntries[data] ?: synchronized(dataToEntries) {
                                        dataToEntries.getOrPut(data) { ArrayList() }
                                }
                                
                                entries += entry
                        }
                }
        }
        jobs.joinAll()
        jobs.clear()
        
        // process known room-info
        for (info in CatacombsMap.roomInfoSet) {
                val entries = dataToEntries.remove(info.data) ?: continue
                jobs += launch {
                        for (entry in entries) {
                                if (entry in info.entries) continue
                                entry.info = info
                                info.entries += entry
                        }
                        calcRotation(info)
                }
        }
        jobs.joinAll()
        jobs.clear()
        
        // process new room-info
        for ((data, entries) in dataToEntries) {
                jobs += launch {
                        val info = RoomInfo(data)
                        for (entry in entries) {
                                entry.info = info
                                info.entries += entry
                        }
                        CatacombsMap.roomInfoSet += info
                        calcRotation(info)
                }
        }
        jobs.joinAll()
        jobs.clear()
}

private fun calcRotation(
        info: RoomInfo
) {
        if (info.rotation !== Rotation.UNKNOWN) return
        
        val world = mc.theWorld ?: return
        val y: Int
        
        // actually it won't be null, but who knows :D
        val entry1: RoomEntry = info.entries.firstOrNull() ?: return
        
        entry1.pos.let {
                val chunk: Chunk = world.getChunkFromChunkCoords(it.x shr 4, it.y shr 4) ?: return
                y = chunk.getTopYOf(it)
        }
        
        if (info.data.type === RoomType.FAIRY) {
                val (x, z) = entry1.pos - Vec2I(15, 15)
                info.corner = BlockPos(x, y, z)
                info.rotation = Rotation.SOUTH
                return
        }
        
        if (info.entries.size == 1) {
                for (rotation in Rotation.directions) {
                        val (x, z) = entry1.pos + rotation.offset * 15
                        
                        val chunk: Chunk = world.getChunkFromChunkCoords(x shr 4, z shr 4) ?: continue
                        if (chunk.getBlock(x, y, z).id != 159) continue
                        
                        info.corner = BlockPos(x, y, z)
                        info.rotation = rotation
                        return
                }
        } else for (entry in info.entries) {
                for (rotation in Rotation.directions) {
                        val (x, z) = entry.pos + (rotation.offset * 15)
                        
                        val chunk: Chunk = world.getChunkFromChunkCoords(x shr 4, z shr 4) ?: continue
                        if (
                                chunk.getBlock(x, y, z).id != 159 ||
                                EnumFacing.HORIZONTALS.any {
                                        chunk.getBlock(
                                                x + it.frontOffsetX,
                                                y,
                                                z + it.frontOffsetZ
                                        ).id !in cornerSideBlockIdInclusion
                                }
                        ) continue
                        
                        info.corner = BlockPos(x, y, z)
                        info.rotation = rotation
                        return
                }
        }
}

