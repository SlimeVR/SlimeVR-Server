package dev.slimevr.serial.reducers

import dev.slimevr.serial.SerialConnectionActions
import dev.slimevr.serial.SerialConnectionState
import dev.slimevr.serial.SerialLogBehaviour
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class SerialConnectionReducerTest {
	private val reducer = SerialLogBehaviour.reducer!!

	private fun state(lines: List<String> = emptyList(), connected: Boolean = true) = SerialConnectionState(
		portLocation = "COM1",
		descriptivePortName = "Test Port",
		connected = connected,
		logLines = lines,
	)

	@Test
	fun `LogLine appends to empty log`() {
		val result = reducer(state(), SerialConnectionActions.LogLine("hello"))
		assertEquals(listOf("hello"), result.logLines)
	}

	@Test
	fun `LogLine appends to existing log`() {
		val result = reducer(state(listOf("a", "b")), SerialConnectionActions.LogLine("c"))
		assertEquals(listOf("a", "b", "c"), result.logLines)
	}

	@Test
	fun `LogLine drops oldest line when at capacity`() {
		val full = state(lines = List(500) { "line $it" })
		val result = reducer(full, SerialConnectionActions.LogLine("new"))
		assertEquals(500, result.logLines.size)
		assertEquals("line 1", result.logLines.first())
		assertEquals("new", result.logLines.last())
	}

	@Test
	fun `LogLine does not drop below capacity`() {
		val almostFull = state(lines = List(499) { "line $it" })
		val result = reducer(almostFull, SerialConnectionActions.LogLine("new"))
		assertEquals(500, result.logLines.size)
		assertEquals("line 0", result.logLines.first())
	}

	@Test
	fun `Disconnected sets connected to false`() {
		val result = reducer(state(connected = true), SerialConnectionActions.Disconnected)
		assertFalse(result.connected)
	}
}
