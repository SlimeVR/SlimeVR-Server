package dev.slimevr

import dev.slimevr.serial.FlashingHandler
import dev.slimevr.serial.SerialPortHandle
import dev.slimevr.serial.SerialPortInfo
import dev.slimevr.serial.SerialPortWatcher
import dev.slimevr.serial.SerialServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

fun noopFlashingHandler() = object : FlashingHandler {
	override fun openSerial(port: Any) = Unit
	override fun closeSerial() = Unit
	override fun write(data: ByteArray) = Unit
	override fun read(length: Int) = ByteArray(length)
	override fun setDTR(value: Boolean) = Unit
	override fun setRTS(value: Boolean) = Unit
	override fun changeBaud(baud: Int) = Unit
	override fun setReadTimeout(timeout: Long) = Unit
	override fun availableBytes() = 0
	override fun flushIOBuffers() = Unit
}

/** A watcher whose ports and received lines are driven by the test */
class FakeSerialPortWatcher(
	override val changes: Flow<Unit> = emptyFlow(),
	private val flashingHandler: () -> FlashingHandler = ::noopFlashingHandler,
) : SerialPortWatcher {
	val ports = mutableMapOf<String, SerialPortInfo>()
	val written = mutableListOf<String>()
	private val lineSinks = mutableMapOf<String, (String) -> Unit>()
	private val closeSinks = mutableMapOf<String, () -> Unit>()
	val openCount = mutableMapOf<String, Int>()
	var failOpen = false

	override suspend fun enumerate(): Map<String, SerialPortInfo> = ports.toMap()

	override suspend fun open(portLocation: String, onLine: (String) -> Unit, onClosed: () -> Unit): SerialPortHandle? {
		if (failOpen) return null
		openCount[portLocation] = (openCount[portLocation] ?: 0) + 1
		lineSinks[portLocation] = onLine
		closeSinks[portLocation] = onClosed
		return SerialPortHandle(portLocation, "Fake $portLocation", { written += it }, {})
	}

	override fun openForFlashing() = flashingHandler()

	fun emitLine(portLocation: String, line: String) = lineSinks[portLocation]?.invoke(line)

	fun failPort(portLocation: String) = closeSinks[portLocation]?.invoke()
}

class TestSerial(val server: SerialServer, val watcher: FakeSerialPortWatcher) {
	suspend fun plug(info: SerialPortInfo) {
		watcher.ports[info.portLocation] = info
		server.refresh()
	}

	suspend fun unplug(portLocation: String) {
		watcher.ports.remove(portLocation)
		server.refresh()
	}

	fun emitLine(portLocation: String, line: String) = watcher.emitLine(portLocation, line)
}

fun buildTestSerial(
	scope: CoroutineScope,
	watcher: FakeSerialPortWatcher = FakeSerialPortWatcher(),
) = TestSerial(SerialServer.create(watcher, scope), watcher)
