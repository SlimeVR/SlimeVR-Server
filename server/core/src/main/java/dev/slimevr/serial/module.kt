package dev.slimevr.serial

import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.logging.AppLogger
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration

private sealed interface Claim {
	class Got(val console: SerialConsole) : Claim
	data object Retry : Claim
	data object Failed : Claim
}

data class SerialServerState(
	val ports: Map<String, SerialPortInfo>,
	/** Ports a firmware flash holds. A claim outlasts the port re-enumerating mid-flash */
	val flashing: Set<String>,
)

sealed interface SerialServerActions {
	data class PortsChanged(val ports: Map<String, SerialPortInfo>) : SerialServerActions
	data class FlashingStarted(val portLocation: String) : SerialServerActions
	data class FlashingEnded(val portLocation: String) : SerialServerActions
}

typealias SerialServerContext = Context<SerialServerState, SerialServerActions>
typealias SerialServerBehaviour = Behaviour<SerialServer>

class SerialServer(
	val context: SerialServerContext,
	private val watcher: SerialPortWatcher,
) {
	private val ownership = Mutex()
	private val consoles = mutableMapOf<String, SerialConsole>()

	fun startObserving() = context.observeAll(this)

	suspend fun refresh() {
		val found = try {
			watcher.enumerate()
		} catch (e: CancellationException) {
			throw e
		} catch (e: Exception) {
			AppLogger.serial.error(e, "Serial port enumeration failed")
			return
		}
		ownership.withLock {
			val known = context.state.value.ports.keys
			if (known != found.keys) {
				AppLogger.serial.info("Serial ports changed, added ${(found.keys - known).joinToString()} removed ${(known - found.keys).joinToString()}")
			}
			for (location in consoles.keys.filter { it !in found }) closeConsole(location)
			context.dispatch(SerialServerActions.PortsChanged(found))
		}
	}

	suspend fun awaitConsole(portLocation: String, timeout: Duration): SerialConsole? = withTimeoutOrNull(timeout) {
		claimConsole(portLocation)
	}

	private suspend fun claimConsole(portLocation: String): SerialConsole? {
		while (true) {
			context.state.map { isFree(it, portLocation) }.first { it }
			when (val claim = ownership.withLock { claimOnce(portLocation) }) {
				is Claim.Got -> return claim.console
				is Claim.Failed -> return null
				is Claim.Retry -> {}
			}
		}
	}

	private suspend fun claimOnce(portLocation: String): Claim {
		if (!isFree(context.state.value, portLocation)) return Claim.Retry
		consoles[portLocation]?.let { existing ->
			if (existing.closed.isActive) return Claim.Got(existing)
			// this port failed and the cleanup in [watchForClose] hasn't run yet
			closeConsole(portLocation)
		}
		AppLogger.serial.info("Opening serial console on $portLocation")
		val console = SerialConsole.open(watcher, portLocation) ?: return Claim.Failed
		consoles[portLocation] = console
		AppLogger.serial.info("Opened serial console on $portLocation")
		watchForClose(console)
		return Claim.Got(console)
	}

	suspend fun openForFlashing(portLocation: String): FlashingHandler? = ownership.withLock {
		val state = context.state.value
		if (portLocation !in state.ports || portLocation in state.flashing) return@withLock null
		closeConsole(portLocation)
		context.dispatch(SerialServerActions.FlashingStarted(portLocation))
		val handler = watcher.openForFlashing()
		object : FlashingHandler by handler {
			override fun closeSerial() {
				try {
					handler.closeSerial()
				} finally {
					context.dispatch(SerialServerActions.FlashingEnded(portLocation))
				}
			}
		}
	}

	private suspend fun closeConsole(portLocation: String) {
		val console = consoles.remove(portLocation) ?: return
		AppLogger.serial.info("Closing serial console on $portLocation")
		console.close()
	}

	private fun watchForClose(console: SerialConsole) {
		context.scope.launch {
			console.closed.join()
			if (consoles[console.portLocation] === console) AppLogger.serial.warn("Serial port ${console.portLocation} reported itself closed")
			ownership.withLock {
				if (consoles[console.portLocation] === console) closeConsole(console.portLocation)
			}
		}
	}

	companion object {
		fun create(watcher: SerialPortWatcher, scope: CoroutineScope): SerialServer {
			val context = Context.create(
				initialState = SerialServerState(ports = mapOf(), flashing = setOf()),
				scope = scope,
				reducer = ::reduce,
				behaviours = listOf(PortDetectionBehaviour(watcher)),
				name = "SerialServer",
			)
			val server = SerialServer(context = context, watcher = watcher)
			server.startObserving()
			return server
		}
	}
}

private fun isFree(state: SerialServerState, portLocation: String) = portLocation in state.ports && portLocation !in state.flashing
