package dev.slimevr.solarxr

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeout
import java.util.Collections
import java.util.IdentityHashMap
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass
import kotlin.time.Duration

class PendingReplies<T : Any> {
	private val pending = Collections.synchronizedMap(IdentityHashMap<T, UInt>())
	private val consumerTypes = Collections.newSetFromMap(ConcurrentHashMap<KClass<out T>, Boolean>())

	fun registerConsumer(type: KClass<out T>) {
		consumerTypes += type
	}

	fun record(message: T, txId: UInt) {
		if (txId == 0u) return
		if (message::class !in consumerTypes) return
		pending[message] = txId
	}

	fun consume(message: T): UInt? = pending.remove(message)
}

class PendingRequests<T : Any> {
	@PublishedApi
	internal val counter = AtomicInteger(1)

	@PublishedApi
	internal val pending = ConcurrentHashMap<UInt, CompletableDeferred<T>>()

	suspend inline fun <reified R : T> request(timeout: Duration, message: T, send: suspend (T, UInt) -> Unit): R {
		val txId = counter.getAndIncrement().toUInt()
		val deferred = CompletableDeferred<T>()
		pending[txId] = deferred
		try {
			send(message, txId)
			val response = withTimeout(timeout) { deferred.await() }
			return response as? R
				?: error("expected ${R::class.simpleName} but got ${response::class.simpleName}")
		} finally {
			pending.remove(txId)
		}
	}

	fun tryResolve(txId: UInt, message: T): Boolean {
		val waiter = pending.remove(txId) ?: return false
		waiter.complete(message)
		return true
	}
}
