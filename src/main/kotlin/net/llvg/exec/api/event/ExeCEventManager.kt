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
import java.util.LinkedList
import java.util.TreeSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager

private typealias EventType = Class<out ExeCEvent>
private typealias ListenerSet = MutableSet<ExeCEventListener<*>>

object ExeCEventManager : CoroutineScope by CoroutineScope(SupervisorJob()) {
        private val logger = LogManager.getLogger(ExeCEventManager::class.java.simpleName)
        
        private val normalStorage: MutableMap<EventType, ListenerSet> = HashMap()
        private val forcedStorage: MutableMap<EventType, ListenerSet> = HashMap()
        
        private val cachedStorages: MutableMap<EventType, List<ListenerSet>> = HashMap()
        
        private operator fun MutableMap<EventType, ListenerSet>.invoke(
                key: EventType
        ): ListenerSet = synchronized(this) {
                getOrPut(key, ::TreeSet)
        }
        
        @JvmStatic
        fun register(
                type: EventType,
                forced: Boolean,
                listener: ExeCEventListener<*>
        ) {
                val storage = if (forced) forcedStorage else normalStorage
                val listeners = storage(type)
                
                synchronized(listeners) {
                        listeners.add(listener)
                }
        }
        
        @OptIn(ExperimentalCoroutinesApi::class)
        @JvmStatic
        fun post(
                type: EventType,
                event: ExeCEvent,
                wait: Boolean
        ) {
                if (!type.isInstance(event)) {
                        val exception = IllegalArgumentException(
                                "event $event(loader=${event.javaClass.classLoader}) is not in type $type(loader=${type.classLoader})"
                        )
                        logger.error(exception)
                        throw exception
                }
                
                val jobs: MutableList<Job> = LinkedList()
                
                val cache = cachedStorages.getOrPut(type) {
                        ImmutableList
                        .builder<ListenerSet>()
                        .apply {
                                add(forcedStorage(type))
                                var clazz: EventType? = type
                                while (clazz !== null) {
                                        add(normalStorage(clazz))
                                        @Suppress("UNCHECKED_CAST")
                                        clazz = clazz.superclass as? EventType
                                }
                        }
                        .build()
                }
                
                val methodThread = Dispatchers.Default.limitedParallelism(1)
                
                val collect = fun ExeCEventListener<*>.() {
                        if (!active) return
                        launch(
                                if (dispatcher === Dispatchers.Unconfined) {
                                        methodThread
                                } else {
                                        dispatcher
                                }
                        ) {
                                action(event)
                        }.let(jobs::add)
                }
                
                cache.forEach {
                        synchronized(it) {
                                it.forEach(collect)
                        }
                }
                
                if (wait) runBlocking(methodThread) {
                        jobs.joinAll()
                }
        }
}