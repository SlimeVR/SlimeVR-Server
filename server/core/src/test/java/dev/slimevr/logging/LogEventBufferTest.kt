package dev.slimevr.logging

import io.klogging.Level
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.toDeprecatedInstant
import solarxr_protocol.rpc.LogLevel
import solarxr_protocol.rpc.LogSource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant
import io.klogging.events.LogEvent as KloggingEvent

class LogEventBufferTest {

	private fun event(message: String, level: Level = Level.INFO) = KloggingEvent(
		id = message,
		timestamp = Instant.fromEpochMilliseconds(1_000).toDeprecatedInstant(),
		host = "test",
		logger = "Test",
		context = null,
		level = level,
		template = null,
		message = message,
		stackTrace = null,
		items = emptyMap(),
	)

	@Test
	fun `a new subscriber gets the backlog first, then new events`() = runTest {
		val buffer = LogEventBuffer(LogSource.SLIMEVR_SERVER)
		buffer(listOf(event("before")))

		val received = async(start = CoroutineStart.UNDISPATCHED) { buffer.events.take(2).toList() }
		buffer(listOf(event("after")))

		assertEquals(listOf("before", "after"), received.await().map { it.message })
	}

	@Test
	fun `the backlog keeps only the most recent events`() = runTest {
		val buffer = LogEventBuffer(LogSource.SLIMEVR_SERVER)
		buffer((0 until BACKLOG_SIZE + 5).map { event("event $it") })

		val backlog = buffer.events.take(BACKLOG_SIZE).toList()

		assertEquals("event 5", backlog.first().message)
		assertEquals("event ${BACKLOG_SIZE + 4}", backlog.last().message)
	}

	@Test
	fun `events are tagged with the source and converted`() = runTest {
		val buffer = LogEventBuffer(LogSource.SLIMEVR_SERVER)
		buffer(listOf(event("\u001B[33mcoloured\u001B[0m", Level.WARN)))

		val received = buffer.events.take(1).toList().single()

		assertEquals(LogSource.SLIMEVR_SERVER, received.source)
		assertEquals(LogLevel.WARN, received.level)
		assertEquals(1_000uL, received.timeMs)
		assertEquals("coloured", received.message)
	}
}
