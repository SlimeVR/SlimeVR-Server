package dev.slimevr

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.onEach

// For now, it blocks all the time, no queue. Maybe it is a bad idea IDK. Future us problem
class EventDispatcher<T : Any>(
	extraBufferCapacity: Int = 0,
	onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND,
) {
	private val flow = MutableSharedFlow<T>(extraBufferCapacity = extraBufferCapacity, onBufferOverflow = onBufferOverflow)
	val events: SharedFlow<T> = flow.asSharedFlow()

	suspend fun emit(event: T) = flow.emit(event)

	inline fun <reified P : Any> on(crossinline action: suspend (P) -> Unit): Flow<P> =
		events.filterIsInstance<P>().onEach { action(it) }
}
