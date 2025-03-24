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

package net.llvg.exec.event

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
import net.llvg.loliutils.exception.ValueWrapper
import org.apache.logging.log4j.LogManager

object ExeCEventManager : CoroutineScope by CoroutineScope(SupervisorJob()) {
        private val logger = LogManager.getLogger(ExeCEventManager::class.java.simpleName)
        
        private val normalStorage: MutableMap<Class<out ExeCEvent>, MutableSet<ExeCEventListener<out ExeCEvent>>> =
                HashMap()
        
        private val forcedStorage: MutableMap<Class<out ExeCEvent>, MutableSet<ExeCEventListener<out ExeCEvent>>> =
                HashMap()
        
        private val cachedStorages: MutableMap<Class<out ExeCEvent>, List<MutableSet<ExeCEventListener<out ExeCEvent>>>> =
                HashMap()
        
        private fun <E : ExeCEvent> MutableMap<Class<out ExeCEvent>, MutableSet<ExeCEventListener<out ExeCEvent>>>.getSafe(
                key: Class<E>
        ): MutableSet<ExeCEventListener<out ExeCEvent>> = synchronized(this) {
                getOrPut(key) { TreeSet() }
        }
        
        @JvmStatic
        fun <E : ExeCEvent> register(
                type: Class<E>,
                forced: Boolean,
                listener: ExeCEventListener<E>
        ) {
                val storage = if (forced) forcedStorage else normalStorage
                val listeners = storage.getSafe(type)
                
                synchronized(listeners) {
                        listeners.add(listener)
                }
        }
        
        @OptIn(ExperimentalCoroutinesApi::class)
        @JvmStatic
        fun <E : ExeCEvent> post(
                type: Class<E>,
                event: ExeCEvent,
                wait: Boolean
        ) {
                if (!type.isInstance(event)) {
                        val exception = IllegalArgumentException(
                                "event $event(loader=" + event.javaClass.classLoader + ") is not in type $type(loader=" + type.classLoader + ")"
                        )
                        logger.error(exception)
                        throw exception
                }
                
                val jobs: MutableList<Job> = LinkedList()
                val wrappedEvent = ValueWrapper(event)
                
                val cachedStorage = cachedStorages.getOrPut(type) {
                        val builder = ImmutableList.builder<MutableSet<ExeCEventListener<out ExeCEvent>>>()
                        builder.add(synchronized(forcedStorage) {
                                forcedStorage.getSafe(type)
                        })
                        var clazz: Class<out ExeCEvent>? = type
                        while (clazz != null) {
                                builder.add(synchronized(normalStorage) {
                                        normalStorage.getSafe(clazz)
                                })
                                clazz = clazz.superclass as? Class<out ExeCEvent>
                        }
                        
                        builder.build()
                }
                
                val methodTread = Dispatchers.Default.limitedParallelism(1)
                
                val collect = collector@{ listener: ExeCEventListener<out ExeCEvent> ->
                        with(listener) {
                                if (!active) return@collector
                                if (dispatcher === Dispatchers.Unconfined)
                                        jobs.add(launch(methodTread) {
                                                action(wrappedEvent)
                                        })
                                else
                                        jobs.add(launch(dispatcher) {
                                                action(wrappedEvent)
                                        })
                        }
                }
                
                cachedStorage.forEach {
                        synchronized(it) {
                                it.forEach(collect)
                        }
                }
                
                if (wait) runBlocking(methodTread) {
                        jobs.joinAll()
                }
        }
}