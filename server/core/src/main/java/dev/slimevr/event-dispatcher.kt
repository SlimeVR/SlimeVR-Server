package dev.slimevr

import kotlin.reflect.KClass

class EventDispatcher<T : Any>(private val keyOf: (T) -> KClass<*> = { it::class }) {
	@Volatile var listeners: Map<KClass<*>, List<suspend (T) -> Unit>> = emptyMap()
	@Volatile private var globalListeners: List<suspend (T) -> Unit> = emptyList()

	fun register(key: KClass<*>, callback: suspend (T) -> Unit) {
		synchronized(this) {
			val updated = listeners.toMutableMap()
			updated[key] = (updated[key] ?: emptyList()) + callback
			listeners = updated
		}
	}

	@Suppress("UNCHECKED_CAST")
	inline fun <reified P : T> on(crossinline callback: suspend (P) -> Unit) {
		register(P::class) { callback(it as P) }
	}

	fun onAny(callback: suspend (T) -> Unit) {
		synchronized(this) {
			globalListeners = globalListeners + callback
		}
	}

	suspend fun emit(event: T) {
		globalListeners.forEach { it(event) }
		listeners[keyOf(event)]?.forEach { it(event) }
	}
}
