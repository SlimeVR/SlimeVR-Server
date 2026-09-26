package dev.slimevr.serial

import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

val MAC_REGEX = Regex("mac: (([0-9A-Fa-f]{2}[:-]){5}[0-9A-Fa-f]{2})", RegexOption.IGNORE_CASE)

internal const val MAX_LOG_LINES = 500
private const val LOG_BURST_CAPACITY = 2000

/**
 * A line stream from one open port. Emitting never suspends and a slow collector loses its oldest
 * lines, so a reader thread can't stall. The last [MAX_LOG_LINES] lines replay to new collectors
 */
class SerialConsole private constructor(
	private val handle: SerialPortHandle,
	private val log: MutableSharedFlow<String>,
	private val closedSignal: CompletableJob,
) {
	val portLocation get() = handle.portLocation
	val descriptivePortName get() = handle.descriptivePortName
	val lines: SharedFlow<String> = log.asSharedFlow()

	val recent: List<String> get() = log.replayCache

	val closed: Job get() = closedSignal

	@OptIn(ExperimentalCoroutinesApi::class)
	fun clearLog() = log.resetReplayCache()

	suspend fun write(command: String) = handle.writeCommand(command)

	suspend fun close() {
		closedSignal.complete()
		handle.close()
	}

	companion object {
		suspend fun open(watcher: SerialPortWatcher, portLocation: String): SerialConsole? {
			val log = MutableSharedFlow<String>(
				replay = MAX_LOG_LINES,
				extraBufferCapacity = LOG_BURST_CAPACITY,
				onBufferOverflow = BufferOverflow.DROP_OLDEST,
			)
			val closed = Job()
			val handle = watcher.open(portLocation, { line -> log.tryEmit(line) }, { closed.complete() })
				?: return null
			return SerialConsole(handle, log, closed)
		}
	}
}
