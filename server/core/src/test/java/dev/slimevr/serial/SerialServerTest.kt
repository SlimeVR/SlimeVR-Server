package dev.slimevr.serial

import dev.llelievr.espflashkotlin.FlasherSerialInterface
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

private fun fakePortHandle(portLocation: String) = SerialPortHandle(
	portLocation = portLocation,
	descriptivePortName = "Fake $portLocation",
	writeCommand = {},
	close = {},
)

private fun fakeFlashingHandler() = object : FlasherSerialInterface {
	override fun openSerial(port: Any) {}
	override fun closeSerial() {}
	override fun write(data: ByteArray) {}
	override fun read(length: Int) = ByteArray(length)
	override fun setDTR(value: Boolean) {}
	override fun setRTS(value: Boolean) {}
	override fun changeBaud(baud: Int) {}
	override fun setReadTimeout(timeout: Long) {}
	override fun availableBytes() = 0
	override fun flushIOBuffers() {}
}

private fun fakePort() = SerialPortInfo("COM1", "Fake COM1", 0x1A86, 0x7523)

class SerialServerTest {
	@Test
	fun `openForFlashing registers Flashing connection`() = runTest {
		val server = SerialServer.create(
			openPort = { loc, _, _, _ -> fakePortHandle(loc) },
			openFlashingPort = ::fakeFlashingHandler,
			scope = this,
		)
		server.onPortDetected(fakePort())

		val handler = server.openForFlashing("COM1")

		assertNotNull(handler)
		assertIs<SerialConnection.Flashing>(server.context.state.value.connections["COM1"])
	}

	@Test
	fun `openForFlashing returns null when port has an existing connection`() = runTest {
		val server = SerialServer.create(
			openPort = { loc, _, _, _ -> fakePortHandle(loc) },
			openFlashingPort = ::fakeFlashingHandler,
			scope = this,
		)
		server.onPortDetected(fakePort())
		server.openConnection("COM1")

		val handler = server.openForFlashing("COM1")

		assertNull(handler)
	}

	@Test
	fun `openForFlashing returns null for unknown port`() = runTest {
		val server = SerialServer.create(
			openPort = { loc, _, _, _ -> fakePortHandle(loc) },
			openFlashingPort = ::fakeFlashingHandler,
			scope = this,
		)

		// No onPortDetected call, port is not in availablePorts
		val handler = server.openForFlashing("COM1")

		assertNull(handler)
	}

	@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
	@Test
	fun `closeSerial removes Flashing connection asynchronously`() = runTest {
		val server = SerialServer.create(
			openPort = { loc, _, _, _ -> fakePortHandle(loc) },
			openFlashingPort = ::fakeFlashingHandler,
			scope = this,
		)
		server.onPortDetected(fakePort())
		val handler = server.openForFlashing("COM1")!!

		handler.closeSerial()

		// The scope.launch inside closeSerial has not run yet
		assertIs<SerialConnection.Flashing>(server.context.state.value.connections["COM1"])

		advanceUntilIdle()

		// Now the dispatched RemoveConnection has run
		assertNull(server.context.state.value.connections["COM1"])
	}

	@Test
	fun `openConnection registers Console connection`() = runTest {
		val server = SerialServer.create(
			openPort = { loc, _, _, _ -> fakePortHandle(loc) },
			openFlashingPort = ::fakeFlashingHandler,
			scope = this,
		)
		server.onPortDetected(fakePort())

		server.openConnection("COM1")

		assertIs<SerialConnection.Console>(server.context.state.value.connections["COM1"])
	}

	@Test
	fun `onPortLost closes Console and removes connection`() = runTest {
		val server = SerialServer.create(
			openPort = { loc, _, _, _ -> fakePortHandle(loc) },
			openFlashingPort = ::fakeFlashingHandler,
			scope = this,
		)
		server.onPortDetected(fakePort())
		server.openConnection("COM1")

		server.onPortLost("COM1")

		assertNull(server.context.state.value.connections["COM1"])
		assertNull(server.context.state.value.availablePorts["COM1"])
	}

	@Test
	fun `openConnection while flashing is a no-op`() = runTest {
		val server = SerialServer.create(
			openPort = { loc, _, _, _ -> fakePortHandle(loc) },
			openFlashingPort = ::fakeFlashingHandler,
			scope = this,
		)
		server.onPortDetected(fakePort())
		server.openForFlashing("COM1")

		server.openConnection("COM1")

		// Still Flashing, openConnection must not have replaced it
		assertIs<SerialConnection.Flashing>(server.context.state.value.connections["COM1"])
	}

	@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
	@Test
	fun `port can be flashed again after previous flash completes`() = runTest {
		val server = SerialServer.create(
			openPort = { loc, _, _, _ -> fakePortHandle(loc) },
			openFlashingPort = ::fakeFlashingHandler,
			scope = this,
		)
		server.onPortDetected(fakePort())
		val firstHandler = server.openForFlashing("COM1")!!
		firstHandler.closeSerial()
		advanceUntilIdle()

		// Connection is gone, port is still available, can flash again
		val secondHandler = server.openForFlashing("COM1")

		assertNotNull(secondHandler)
		assertIs<SerialConnection.Flashing>(server.context.state.value.connections["COM1"])
	}

	@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
	@Test
	fun `openConnection succeeds after flash completes`() = runTest {
		val server = SerialServer.create(
			openPort = { loc, _, _, _ -> fakePortHandle(loc) },
			openFlashingPort = ::fakeFlashingHandler,
			scope = this,
		)
		server.onPortDetected(fakePort())
		val handler = server.openForFlashing("COM1")!!
		handler.closeSerial()
		advanceUntilIdle()

		server.openConnection("COM1")

		assertIs<SerialConnection.Console>(server.context.state.value.connections["COM1"])
	}

	@Test
	fun `onPortLost during flash removes Flashing connection`() = runTest {
		val server = SerialServer.create(
			openPort = { loc, _, _, _ -> fakePortHandle(loc) },
			openFlashingPort = ::fakeFlashingHandler,
			scope = this,
		)
		server.onPortDetected(fakePort())
		server.openForFlashing("COM1")

		server.onPortLost("COM1")

		assertNull(server.context.state.value.connections["COM1"])
		assertNull(server.context.state.value.availablePorts["COM1"])
	}
}
