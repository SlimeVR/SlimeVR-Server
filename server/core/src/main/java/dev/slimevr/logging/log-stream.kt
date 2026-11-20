package dev.slimevr.logging

import io.klogging.Level
import io.klogging.rendering.evalTemplate
import io.klogging.sending.EventSender
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onSubscription
import kotlinx.datetime.toStdlibInstant
import solarxr_protocol.rpc.LogEvent
import solarxr_protocol.rpc.LogLevel
import solarxr_protocol.rpc.LogSource
import io.klogging.events.LogEvent as KloggingEvent

const val BACKLOG_SIZE = 2000

/**
 * Room for events between a subscriber's reads. Without it, tryEmit from the logging thread
 * fails whenever a subscriber is not waiting at that instant, and the event is lost.
 */
private const val SUBSCRIBER_BUFFER = 1024

internal val ANSI_ESCAPE = Regex("\u001B\\[[0-9;]*m")

private class Entry(val seq: Long, val event: LogEvent)

/**
 * Klogging sink keeping the last [BACKLOG_SIZE] events of [source] and passing new ones to
 * subscribers of [events].
 */
class LogEventBuffer(private val source: LogSource) : EventSender {
	private val lock = Any()
	private val backlog = ArrayDeque<Entry>(BACKLOG_SIZE)
	private var nextSeq = 0L
	private val live = MutableSharedFlow<Entry>(
		extraBufferCapacity = SUBSCRIBER_BUFFER,
		onBufferOverflow = BufferOverflow.DROP_OLDEST,
	)

	override fun invoke(batch: List<KloggingEvent>) = synchronized(lock) {
		for (event in batch) {
			val entry = Entry(nextSeq++, toLogEvent(source, event))
			if (backlog.size == BACKLOG_SIZE) backlog.removeFirst()
			backlog.addLast(entry)
			live.tryEmit(entry)
		}
	}

	/**
	 * An event added between the subscription and the backlog snapshot reaches the subscriber
	 * twice, once from each; the sequence number drops the second copy.
	 */
	val events: Flow<LogEvent> = flow {
		var lastSeq = -1L
		live
			.onSubscription { synchronized(lock) { backlog.toList() }.forEach { emit(it) } }
			.filter { it.seq > lastSeq }
			.collect {
				lastSeq = it.seq
				emit(it.event)
			}
	}
}

private fun toLogEvent(source: LogSource, event: KloggingEvent): LogEvent {
	val items = if (event.items.isNotEmpty()) " : ${event.items}" else ""
	return LogEvent(
		source = source,
		timeMs = event.timestamp.toStdlibInstant().toEpochMilliseconds().toULong(),
		level = toLogLevel(event.level),
		logger = event.logger,
		message = (event.evalTemplate() + items).replace(ANSI_ESCAPE, ""),
		stackTrace = event.stackTrace,
	)
}

private fun toLogLevel(level: Level) = when (level) {
	Level.TRACE -> LogLevel.TRACE
	Level.DEBUG -> LogLevel.DEBUG
	Level.INFO, Level.NONE -> LogLevel.INFO
	Level.WARN -> LogLevel.WARN
	Level.ERROR -> LogLevel.ERROR
	Level.FATAL -> LogLevel.FATAL
}
