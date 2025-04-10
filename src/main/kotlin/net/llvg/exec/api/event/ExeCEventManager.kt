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

package net.llvg.exec.api.event

import com.google.common.collect.ImmutableList
import java.util.TreeSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.llvg.loliutils.exception.cast
import org.apache.logging.log4j.LogManager

private typealias EventType = Class<out ExeCEvent>

private typealias BlockListener<E> = ExeCEventListener.Block<E>
private typealias AsyncListener<E> = ExeCEventListener.Async<E>
private typealias BlockListenerSet = MutableSet<ExeCEventListener.Block<*>>
private typealias AsyncListenerSet = MutableSet<ExeCEventListener.Async<*>>

object ExeCEventManager {
        private val logger = LogManager.getLogger(ExeCEventManager::class.java.simpleName)
        
        private val blockNormalStorage: MutableMap<EventType, BlockListenerSet> = HashMap()
        private val blockForcedStorage: MutableMap<EventType, BlockListenerSet> = HashMap()
        private val asyncNormalStorage: MutableMap<EventType, AsyncListenerSet> = HashMap()
        private val asyncForcedStorage: MutableMap<EventType, AsyncListenerSet> = HashMap()
        
        private val cache: MutableMap<EventType, Pair<List<BlockListenerSet>, List<AsyncListenerSet>>> = HashMap()
        
        private operator fun <L : ExeCEventListener<*>> MutableMap<EventType, MutableSet<L>>.invoke(
                key: EventType
        ): MutableSet<L> = synchronized(this) {
                getOrPut(key) { TreeSet() }
        }
        
        @JvmStatic
        fun register(
                type: EventType,
                forced: Boolean,
                listener: ExeCEventListener<*>
        ) {
                when (listener) {
                        is BlockListener -> {
                                val storage = if (forced) blockForcedStorage else blockNormalStorage
                                val listeners = storage(type)
                                
                                synchronized(listeners) {
                                        listeners.add(listener)
                                }
                        }
                        
                        is AsyncListener -> {
                                val storage = if (forced) asyncForcedStorage else asyncNormalStorage
                                val listeners = storage(type)
                                
                                synchronized(listeners) {
                                        listeners.add(listener)
                                }
                        }
                }
        }
        
        private val scope: CoroutineScope = CoroutineScope(SupervisorJob())
        
        @JvmStatic
        fun post(
                type: EventType,
                event: ExeCEvent,
                wait: Boolean
        ) {
                val job = scope.launch {
                        post(type, event)
                }
                
                if (wait) runBlocking {
                        job.join()
                }
        }
        
        private fun CoroutineScope.post(
                type: EventType,
                event: ExeCEvent
        ): Job {
                if (!type.isInstance(event)) {
                        val exception = IllegalArgumentException(
                                "event $event(loader=${event.javaClass.classLoader}) is not in type $type(loader=${type.classLoader})"
                        )
                        logger.error(exception)
                        throw exception
                }
                val (blockCache, asyncCache) = cache.getOrPut(type) {
                        val blockBuilder = ImmutableList.builder<BlockListenerSet>()
                        val asyncBuilder = ImmutableList.builder<AsyncListenerSet>()
                        
                        blockBuilder.add(blockForcedStorage(type))
                        asyncBuilder.add(asyncForcedStorage(type))
                        
                        forEachSuperClass(type) {
                                blockBuilder.add(blockNormalStorage(it))
                                asyncBuilder.add(asyncNormalStorage(it))
                        }
                        
                        blockBuilder.build() to asyncBuilder.build()
                }
                
                val curr = Dispatchers.Default
                return launch(curr) {
                        val jobs: MutableList<Job> = ArrayList()
                        asyncCache.forEachFlat {
                                launch(it.dispatcher) {
                                        try {
                                                cast<AsyncListener<ExeCEvent>>(it).run {
                                                        action(event)
                                                }
                                        } catch (e: Throwable) {
                                                logger.info(
                                                        "An error occur during active async listener {} of event {}",
                                                        it,
                                                        type,
                                                        e
                                                )
                                        }
                                }.let(jobs::add)
                        }
                        
                        blockCache.forEachFlat {
                                try {
                                        cast<BlockListener<ExeCEvent>>(it).action(event)
                                } catch (e: Throwable) {
                                        logger.info(
                                                "An error occur during active block listener {} of event {}",
                                                it,
                                                type,
                                                e
                                        )
                                }
                        }
                        
                        jobs.joinAll()
                }
        }
}

private fun forEachSuperClass(
        clazz: Class<out ExeCEvent>,
        action: (Class<out ExeCEvent>) -> Unit
) {
        fun CoroutineScope.forEachInterface(
                visited: MutableSet<Class<out ExeCEvent>>,
                clazz: Class<out ExeCEvent>,
                action: (Class<out ExeCEvent>) -> Unit
        ): Job? {
                if (clazz in visited) return null
                return launch {
                        val jobs: MutableList<Job> = ArrayList()
                        
                        launch {
                                action(clazz)
                        }.let(jobs::add)
                        
                        clazz.interfaces.forEach {
                                launch {
                                        if (ExeCEvent::class.java.isAssignableFrom(it)) forEachInterface(
                                                visited,
                                                it.asSubclass(ExeCEvent::class.java),
                                                action
                                        )?.join()
                                }.let(jobs::add)
                        }
                        
                        jobs.joinAll()
                }
        }
        
        runBlocking {
                val visited: MutableSet<Class<out ExeCEvent>> = HashSet()
                
                var type: Class<out ExeCEvent> = clazz
                val jobs: MutableList<Job> = ArrayList()
                
                while (true) {
                        launch {
                                forEachInterface(visited, type, action)?.join()
                        }.let(jobs::add)
                        
                        val supT = type.superclass ?: break
                        if (ExeCEvent::class.java.isAssignableFrom(supT)) {
                                type = supT.asSubclass(ExeCEvent::class.java)
                        } else break
                }
                
                jobs.joinAll()
        }
}

private inline fun <L : ExeCEventListener<*>> List<MutableSet<L>>.forEachFlat(
        action: (L) -> Unit
) {
        for (it in this) for (listener in it) action(listener)
}
