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

import java.util.TreeSet
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.llvg.loliutils.exception.cast
import net.llvg.loliutils.exception.tryExtend
import org.apache.logging.log4j.LogManager

private typealias EventType = Class<out ExeCEvent>

private typealias BlockListener<E> = ExeCEventListener.Block<E>
private typealias AsyncListener<E> = ExeCEventListener.Async<E>

object ExeCEventManager {
        private val logger = LogManager.getLogger(ExeCEventManager::class.java.simpleName)
        
        private val blockStorage = TypedStorage<BlockListener<*>>()
        private val asyncStorage = TypedStorage<AsyncListener<*>>()
        
        @JvmStatic
        fun register(
                type: EventType,
                forced: Boolean,
                listener: ExeCEventListener<*>
        ) {
                when (listener) {
                        is BlockListener -> blockStorage.put(type, listener, forced)
                        is AsyncListener -> asyncStorage.put(type, listener, forced)
                }
        }
        
        private val scope: CoroutineScope = CoroutineScope(SupervisorJob())
        
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
                
                val asyncListeners = asyncStorage[type]
                val blockListeners = blockStorage[type]
                
                val job = scope.launch(Dispatchers.Default) {
                        val jobs: MutableList<Job> = ArrayList()
                        
                        asyncListeners.traverse {
                                if (it.always || it.owner.active) {
                                        jobs += trigger(it, event)
                                }
                        }
                        
                        blockListeners.traverse {
                                if (it.always || it.owner.active) {
                                        trigger(it, event)
                                }
                        }
                        
                        jobs.joinAll()
                }
                
                if (wait) runBlocking {
                        job.join()
                }
        }
        
        private fun CoroutineScope.trigger(
                listener: AsyncListener<*>,
                event: ExeCEvent
        ): Job = launch(listener.dispatcher) {
                try {
                        cast<AsyncListener<ExeCEvent>>(listener).run {
                                action(event)
                        }
                } catch (e: Throwable) {
                        logger.info(
                                "An error occur during active async listener own by {} of event {}",
                                listener.owner,
                                event,
                                e
                        )
                }
        }
        
        private fun trigger(
                listener: BlockListener<*>,
                event: ExeCEvent
        ) {
                try {
                        cast<BlockListener<ExeCEvent>>(listener).run {
                                action(event)
                        }
                } catch (e: Throwable) {
                        logger.info(
                                "An error occur during active block listener own by {} of event {}",
                                listener.owner,
                                event,
                                e
                        )
                }
        }
}

private interface Traversable<T> {
        fun traverse(
                action: (T) -> Unit
        )
}

private class CompactTraversable<T>(
        vararg val traversables: Traversable<out T>
) : Traversable<T> {
        override fun traverse(
                action: (T) -> Unit
        ) {
                for (it in traversables) {
                        it.traverse(action)
                }
        }
}

private class IterableWithLock<T>(
        private val lock: Lock,
        private val iterable: Iterable<T>
) : Traversable<T> {
        override fun traverse(
                action: (T) -> Unit
        ) {
                lock.withLock {
                        for (it in iterable) {
                                action(it)
                        }
                }
        }
}

private class TypedStorage<T> {
        private val forced: MutableMap<EventType, Pair<ReadWriteLock, MutableSet<T>>> = HashMap()
        private val normal: MutableMap<EventType, Pair<ReadWriteLock, MutableSet<T>>> = HashMap()
        
        private val cacheVals: MutableMap<EventType, Traversable<T>> = HashMap()
        private val cacheLock: MutableMap<EventType, Mutex> = HashMap()
        
        private infix fun MutableMap<EventType, Pair<ReadWriteLock, MutableSet<T>>>.query(
                key: EventType
        ): Pair<ReadWriteLock, MutableSet<T>> =
                get(key) ?: synchronized(this) {
                        getOrPut(key) { ReentrantReadWriteLock() to TreeSet() }
                }
        
        fun put(
                key: EventType,
                value: T,
                force: Boolean
        ) {
                val (lock, listeners) = (if (force) forced else normal) query key
                lock.writeLock().withLock {
                        listeners += value
                }
        }
        
        operator fun get(
                key: EventType
        ): Traversable<T> {
                val cache: Traversable<T>
                
                runBlocking(Dispatchers.Default) {
                        cache = cacheOrBuild(key)
                }
                
                val (forceLock, forceListener) = forced[key] ?: return cache
                
                return CompactTraversable(
                        IterableWithLock(
                                forceLock.readLock(),
                                forceListener
                        ),
                        cache
                )
        }
        
        private fun cacheLock(
                key: EventType
        ): Mutex =
                cacheLock[key] ?: synchronized(cacheLock) {
                        cacheLock.getOrPut(key) { Mutex() }
                }
        
        private suspend fun cacheOrBuild(
                key: EventType
        ): Traversable<T> =
                cacheVals[key] ?: cacheLock(key).withLock {
                        cacheVals[key]?.let {
                                return it
                        }
                        
                        val deferredList: MutableList<Deferred<Traversable<T>>> = ArrayList()
                        coroutineScope {
                                deferredList += async {
                                        val (lock, listeners) = normal query key
                                        IterableWithLock(lock.readLock(), listeners)
                                }
                                
                                key.superclass?.tryExtend<ExeCEvent>()?.let {
                                        deferredList += async {
                                                cacheOrBuild(it)
                                        }
                                }
                                
                                for (i in key.interfaces) {
                                        val it = i.tryExtend<ExeCEvent>() ?: continue
                                        
                                        deferredList += async {
                                                cacheOrBuild(it)
                                        }
                                }
                        }
                        
                        val result = CompactTraversable(*deferredList.awaitAll().toTypedArray())
                        cacheVals.putIfAbsent(key, result)
                        return result
                }
}