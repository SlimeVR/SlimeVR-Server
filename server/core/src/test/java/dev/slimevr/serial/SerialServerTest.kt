package dev.slimevr.serial

import dev.slimevr.FakeSerialPortWatcher
import dev.slimevr.buildTestSerial
import dev.slimevr.noopFlashingHandler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import solarxr_protocol.rpc.SerialDeviceType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.time.Duration.Companion.seconds

private fun fakePort(loc: String = "COM1") = SerialPortInfo(loc, "Fake $loc", 0x1A86, 0x7523)

private fun unknownPort(loc: String) = SerialPortInfo(loc, "Unknown $loc", 0x1234, 0x5678)

@OptIn(ExperimentalCoroutinesApi::class)
class SerialServerTest {
	@Test
	fun `refresh publishes ports and replaces the previous set`() = runTest {
		val serial = buildTestSerial(backgroundScope)

		serial.plug(fakePort("COM1"))
		serial.plug(fakePort("COM2"))
		assertEquals(setOf("COM1", "COM2"), serial.server.context.state.value.ports.keys)

		serial.unplug("COM1")
		assertEquals(setOf("COM2"), serial.server.context.state.value.ports.keys)
	}

	@Test
	fun `hotplug event triggers enumeration after the settle delay`() = runTest {
		val changes = MutableSharedFlow<Unit>()
		val watcher = FakeSerialPortWatcher(changes = changes)
		val serial = buildTestSerial(backgroundScope, watcher)
		advanceTimeBy(1)

		watcher.ports["COM1"] = fakePort()
		changes.emit(Unit)
		advanceTimeBy(1_000)

		assertEquals(setOf("COM1"), serial.server.context.state.value.ports.keys)
	}

	@Test
	fun `unrecognized usb ports are listed as UNKNOWN and sort last`() = runTest {
		val serial = buildTestSerial(backgroundScope)
		serial.plug(unknownPort("A"))
		serial.plug(fakePort("B"))

		val sorted = sortPorts(serial.server.context.state.value.ports.values)

		assertEquals(listOf("B", "A"), sorted.map { it.portLocation })
		assertEquals(SerialDeviceType.UNKNOWN, sorted.last().type)
	}

	@Test
	fun `awaitConsole opens the port once and shares the console`() = runTest {
		val serial = buildTestSerial(backgroundScope)
		serial.plug(fakePort())

		val first = serial.server.awaitConsole("COM1", 5.seconds)
		val second = serial.server.awaitConsole("COM1", 5.seconds)

		assertNotNull(first)
		assertSame(first, second)
		assertEquals(1, serial.watcher.openCount["COM1"])
	}

	@Test
	fun `awaitConsole waits for the port to appear`() = runTest {
		val serial = buildTestSerial(backgroundScope)
		var console: SerialConsole? = null
		val job = launch { console = serial.server.awaitConsole("COM1", 30.seconds) }

		advanceTimeBy(1_000)
		assertNull(console)
		serial.plug(fakePort())
		job.join()

		assertNotNull(console)
	}

	@Test
	fun `awaitConsole gives up after the timeout`() = runTest {
		val serial = buildTestSerial(backgroundScope)

		assertNull(serial.server.awaitConsole("COM1", 5.seconds))
	}

	@Test
	fun `awaitConsole returns null when the port refuses to open`() = runTest {
		val serial = buildTestSerial(backgroundScope)
		serial.watcher.failOpen = true
		serial.plug(fakePort())

		assertNull(serial.server.awaitConsole("COM1", 5.seconds))
	}

	@Test
	fun `console keeps receiving after more lines than the log holds`() = runTest {
		val serial = buildTestSerial(backgroundScope)
		serial.plug(fakePort())
		val console = serial.server.awaitConsole("COM1", 5.seconds)!!
		val last = backgroundScope.launch { console.lines.first { it == "line 1200" } }
		advanceTimeBy(1)

		repeat(1_200) { serial.emitLine("COM1", "line ${it + 1}") }
		advanceTimeBy(1)

		assertEquals(true, last.isCompleted)
		assertEquals(MAX_LOG_LINES, console.recent.size)
		assertEquals("line 1200", console.recent.last())
	}

	@Test
	fun `clearLog drops the replayed lines`() = runTest {
		val serial = buildTestSerial(backgroundScope)
		serial.plug(fakePort())
		val console = serial.server.awaitConsole("COM1", 5.seconds)!!
		serial.emitLine("COM1", "a")

		console.clearLog()

		assertEquals(emptyList(), console.recent)
	}

	@Test
	fun `unplugging closes the console and a replug opens a new one`() = runTest {
		val serial = buildTestSerial(backgroundScope)
		serial.plug(fakePort())
		val first = serial.server.awaitConsole("COM1", 5.seconds)!!

		serial.unplug("COM1")
		assertEquals(true, first.closed.isCompleted)

		serial.plug(fakePort())
		val second = serial.server.awaitConsole("COM1", 5.seconds)
		assertNotNull(second)
		assertNotSame(first, second)
	}

	@Test
	fun `a port that reports itself closed drops its console and can reopen`() = runTest {
		val serial = buildTestSerial(backgroundScope)
		serial.plug(fakePort())
		val first = serial.server.awaitConsole("COM1", 5.seconds)!!

		serial.watcher.failPort("COM1")
		advanceTimeBy(1)
		assertEquals(true, first.closed.isCompleted)

		val second = serial.server.awaitConsole("COM1", 5.seconds)
		assertNotNull(second)
		assertNotSame(first, second)
	}

	@Test
	fun `awaitConsole does not hand out a console whose port already failed`() = runTest {
		val serial = buildTestSerial(backgroundScope)
		serial.plug(fakePort())
		val first = serial.server.awaitConsole("COM1", 5.seconds)!!

		serial.watcher.failPort("COM1")
		val second = serial.server.awaitConsole("COM1", 5.seconds)

		assertNotNull(second)
		assertNotSame(first, second)
	}

	@Test
	fun `openForFlashing takes the port from its console`() = runTest {
		val serial = buildTestSerial(backgroundScope)
		serial.plug(fakePort())
		val console = serial.server.awaitConsole("COM1", 5.seconds)!!

		val handler = serial.server.openForFlashing("COM1")

		assertNotNull(handler)
		assertEquals(true, console.closed.isCompleted)
		assertEquals(setOf("COM1"), serial.server.context.state.value.flashing)
	}

	@Test
	fun `openForFlashing returns null for an unknown port`() = runTest {
		val serial = buildTestSerial(backgroundScope)

		assertNull(serial.server.openForFlashing("COM1"))
	}

	@Test
	fun `openForFlashing returns null while already flashing`() = runTest {
		val serial = buildTestSerial(backgroundScope)
		serial.plug(fakePort())
		serial.server.openForFlashing("COM1")

		assertNull(serial.server.openForFlashing("COM1"))
	}

	@Test
	fun `closeSerial releases the flashing claim`() = runTest {
		val serial = buildTestSerial(backgroundScope)
		serial.plug(fakePort())
		val handler = serial.server.openForFlashing("COM1")!!

		handler.closeSerial()

		assertEquals(emptySet(), serial.server.context.state.value.flashing)
		assertNotNull(serial.server.openForFlashing("COM1"))
	}

	@Test
	fun `closeSerial releases the flashing claim when the platform close fails`() = runTest {
		val watcher = FakeSerialPortWatcher(
			flashingHandler = {
				object : FlashingHandler by noopFlashingHandler() {
					override fun closeSerial() = error("close failed")
				}
			},
		)
		val serial = buildTestSerial(backgroundScope, watcher)
		serial.plug(fakePort())
		val handler = serial.server.openForFlashing("COM1")!!

		assertFailsWith<IllegalStateException> { handler.closeSerial() }

		assertEquals(emptySet(), serial.server.context.state.value.flashing)
	}

	@Test
	fun `awaitConsole waits out a flash and opens once it ends`() = runTest {
		val serial = buildTestSerial(backgroundScope)
		serial.plug(fakePort())
		val handler = serial.server.openForFlashing("COM1")!!
		var console: SerialConsole? = null
		val job = launch { console = serial.server.awaitConsole("COM1", 30.seconds) }

		advanceTimeBy(1_000)
		assertNull(console)
		handler.closeSerial()
		job.join()

		assertNotNull(console)
	}

	@Test
	fun `flashing claim survives the port re-enumerating mid flash`() = runTest {
		val serial = buildTestSerial(backgroundScope)
		serial.plug(fakePort())
		serial.server.openForFlashing("COM1")

		serial.unplug("COM1")
		serial.plug(fakePort())

		assertEquals(setOf("COM1"), serial.server.context.state.value.flashing)
	}
}
