package dev.slimevr

import dev.slimevr.logging.AppLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import kotlin.reflect.KClass
import kotlin.time.Duration.Companion.seconds

private val SATURATION_REPORT_INTERVAL = 5.seconds

/**
 * A handler that has been described but not yet attached. [launchIn] attaches it and ties it to the
 * lifetime of the given scope.
 *
 * FIXME: Keep this until we are sure the current event dispatcher is good. Instead of redoing all [EventDispatcher.on] calls in all the behaviours
 */
class Subscription<T : Any, P : Any> @PublishedApi internal constructor(
	private val dispatcher: EventDispatcher<T>,
	private val type: KClass<P>,
	private val action: suspend (P) -> Unit,
) {
	fun launchIn(scope: CoroutineScope): DisposableHandle = dispatcher.subscribe(type, scope, action)
}

/**
 * Single-consumer event bus.
 *
 * One coroutine drains the mailbox and runs every handler for an event before taking the next one,
 * so handlers still observe events in emission order. Several behaviours depend on that: a
 * `HIDDeviceRegister` has to be handled before the `HIDDeviceInfo` that follows it, and the driver
 * needs `TrackerAdded` before `TrackerPosition`.
 *
 * Producers only wait for mailbox space, never for handlers, so a slow handler cannot stall the
 * thread reading from a device.
 *
 * [onBufferOverflow] is a decision per dispatcher, not a default to accept:
 * - [BufferOverflow.SUSPEND] for anything that must not be lost (registrations, RPC).
 * - [BufferOverflow.DROP_OLDEST] for state-like samples where a stale value is worthless anyway.
 *
 * Saturation is reported on three signals: how deep the mailbox got, events dropped under
 * DROP_OLDEST, and producers forced to wait under SUSPEND. Depth is the one that fires first, at a
 * quarter of [capacity], so a dispatcher that is starting to fall behind says so while it is still
 * keeping up. Capacity stays large enough to absorb a GC pause without dropping; the warning
 * threshold is what makes it early, not a small buffer.
 */
class EventDispatcher<T : Any>(
	private val name: String,
	scope: CoroutineScope,
	private val capacity: Int = 64,
	onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND,
) {
	private class Sub<T : Any>(val type: KClass<*>, val action: suspend (T) -> Unit)

	// enqueueDepth is what the producer saw in the mailbox at send time. Depth 0 means nothing was
	// ahead of this event, so its whole queue wait is the scheduler getting the parked drain coroutine
	// back onto a thread, not a backlog. Splitting the wait on it is what separates the two.
	private class Envelope<T>(val payload: T, val enqueuedAtNanos: Long, val enqueueDepth: Int)

	private val subs = CopyOnWriteArrayList<Sub<T>>()

	// Which handlers a concrete event class resolves to. Cleared whenever the subscriber list moves,
	// which only happens at wiring time, so the per-event cost is one map lookup.
	private val resolved = ConcurrentHashMap<KClass<*>, List<Sub<T>>>()

	private val dropped = AtomicLong()
	private val stalled = AtomicLong()
	private val depth = AtomicInteger()
	private val peakDepth = AtomicInteger()

	// Warn on how full the mailbox gets, not on it overflowing. Overflow is the failure; depth is the
	// leading indicator, and separating the two means the buffer can stay big enough to ride out a GC
	// pause without that also delaying the warning.
	private val warnDepth = (capacity / 4).coerceAtLeast(1)

	private val dispatcherMetrics = DispatcherMetrics(name)
	private val metrics get() = if (AppLogger.ShouldDebug.eventDispatcher) dispatcherMetrics else null

	private val mailbox = Channel<Envelope<T>>(capacity, onBufferOverflow) {
		dropped.incrementAndGet()
		depth.decrementAndGet()
	}

	init {
		scope.launch {
			for (envelope in mailbox) {
				depth.decrementAndGet()
				val type = envelope.payload::class

				if (metrics == null) {
					for (sub in handlersFor(type)) sub.action(envelope.payload)
					continue
				}

				metrics?.recordQueueWait(System.nanoTime() - envelope.enqueuedAtNanos, envelope.enqueueDepth)
				for (sub in handlersFor(type)) {
					val start = System.nanoTime()
					sub.action(envelope.payload)
					metrics?.recordHandler(System.nanoTime() - start, type)
				}
			}
		}

		scope.launch {
			while (true) {
				delay(SATURATION_REPORT_INTERVAL)
				saturationReport()?.let { AppLogger.events.warn(it) }
				metrics?.report()?.let { AppLogger.events.info(it) }
			}
		}

		// Once the owner is gone nothing drains the mailbox, so it has to stop accepting. Without this
		// a SUSPEND producer would fill the buffer and then block forever on a queue nobody reads.
		scope.coroutineContext.job.invokeOnCompletion { mailbox.close() }
	}

	suspend fun emit(event: T) {
		val now = System.nanoTime()
		metrics?.recordArrival(now)

		val envelope = Envelope(event, now, if (metrics != null) depth.get() else 0)
		val result = mailbox.trySend(envelope)
		if (result.isSuccess) return recordDepth()
		if (result.isClosed) return

		stalled.incrementAndGet()
		try {
			mailbox.send(envelope)
			recordDepth()
		} catch (_: ClosedSendChannelException) {
			// The owner can go away between the trySend above and here.
		}
	}

	private fun recordDepth() {
		val current = depth.incrementAndGet()
		peakDepth.updateAndGet { seen -> if (current > seen) current else seen }
	}

	inline fun <reified P : Any> on(noinline action: suspend (P) -> Unit): Subscription<T, P> = Subscription(this, P::class, action)

	fun <P : Any> subscribe(type: KClass<P>, scope: CoroutineScope, action: suspend (P) -> Unit): DisposableHandle {
		@Suppress("UNCHECKED_CAST")
		val sub = Sub<T>(type) { event -> action(event as P) }
		subs += sub
		resolved.clear()

		// A job in the listener's scope, so a cancelled scope and an explicit dispose both unsubscribe
		// through the same path. ATOMIC because a scope that is already dead would otherwise skip the
		// body entirely and leave the handler registered.
		val job = scope.launch(start = CoroutineStart.ATOMIC) {
			try {
				awaitCancellation()
			} finally {
				subs -= sub
				resolved.clear()
			}
		}
		return DisposableHandle { job.cancel() }
	}

	private fun handlersFor(cls: KClass<*>): List<Sub<T>> = resolved.getOrPut(cls) { subs.filter { sub -> sub.type.java.isAssignableFrom(cls.java) } }

	internal fun saturationReport(): String? {
		val peak = peakDepth.getAndSet(depth.get().coerceAtLeast(0))
		val drops = dropped.getAndSet(0)
		val stalls = stalled.getAndSet(0)
		if (drops == 0L && stalls == 0L && peak < warnDepth) return null

		val outcome = when {
			drops > 0L -> "$drops events dropped"
			stalls > 0L -> "$stalls producer stalls"
			else -> "nothing lost yet"
		}
		return "$name mailbox peak $peak/$capacity in $SATURATION_REPORT_INTERVAL -> $outcome"
	}
}

private class Stat {
	private val sumNanos = AtomicLong(0L)
	private val maxNanos = AtomicLong(0L)
	private val count = AtomicLong(0L)

	fun record(nanos: Long) {
		sumNanos.addAndGet(nanos)
		maxNanos.updateAndGet { seen -> if (nanos > seen) nanos else seen }
		count.incrementAndGet()
	}

	fun drain() = Snapshot(count.getAndSet(0), sumNanos.getAndSet(0), maxNanos.getAndSet(0))

	class Snapshot(val count: Long, val sumNanos: Long, val maxNanos: Long) {
		override fun toString(): String {
			val averageNanos = if (count > 0) sumNanos / count else 0L
			return "avg ${averageNanos / 1000}us max ${maxNanos / 1000}us"
		}
	}
}

/**
 * Timing for one [EventDispatcher], attached only when it is asked for.
 *
 * - [queueWait] is the whole enqueue-to-handled delay.
 * - [wakeupWait] is the part of it spent by events that arrived to an empty mailbox
 * - [handlerByType] keeps timing stats per handler. Used to find the most likely culprit of slow
 *   handlers, and summed for the overall handler time.
 * - [arrivalGap] is measured where events are emitted, so it describes the producer.
 */
private class DispatcherMetrics(private val name: String) {
	private val arrivalGap = Stat()
	private val queueWait = Stat()
	private val wakeupWait = Stat()
	private val handlerByType = ConcurrentHashMap<String, Stat>()

	private val lastEmitAtNanos = AtomicLong(0L)

	fun recordArrival(nowNanos: Long) {
		val previous = lastEmitAtNanos.getAndSet(nowNanos)
		if (previous != 0L) arrivalGap.record(nowNanos - previous)
	}

	fun recordQueueWait(waitNanos: Long, enqueueDepth: Int) {
		queueWait.record(waitNanos)
		if (enqueueDepth <= 0) wakeupWait.record(waitNanos)
	}

	fun recordHandler(durationNanos: Long, type: KClass<*>) {
		handlerByType.getOrPut(type.simpleName ?: type.toString()) { Stat() }.record(durationNanos)
	}

	fun report(): String? {
		val arrivals = arrivalGap.drain()
		val waits = queueWait.drain()
		val wakeup = wakeupWait.drain()
		val byType = handlerByType
			.map { (type, stat) -> type to stat.drain() }
			.filter { (_, stat) -> stat.count > 0 }
			.sortedByDescending { (_, stat) -> stat.maxNanos }

		val handled = Stat.Snapshot(
			count = byType.sumOf { (_, stat) -> stat.count },
			sumNanos = byType.sumOf { (_, stat) -> stat.sumNanos },
			maxNanos = byType.maxOfOrNull { (_, stat) -> stat.maxNanos } ?: 0L,
		)

		if (waits.count == 0L) return null

		return "$name ${waits.count} events/$SATURATION_REPORT_INTERVAL" +
			" | gap between arrivals $arrivals" +
			" | waited in mailbox $waits" +
			" | of that, wakeup only $wakeup over ${wakeup.count} events" +
			" | time in handlers $handled" +
			" | slowest handlers: " +
			byType.joinToString(", ") { (type, stat) -> "$type ${stat.count} calls $stat" }
				.ifEmpty { "no handlers" }
	}
}
