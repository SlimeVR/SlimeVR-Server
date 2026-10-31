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
import solarxr_protocol.rpc.SerialDeviceType
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.time.Duration

private sealed interface Claim {
	class Got(val lease: SerialLease) : Claim
	data object Retry : Claim
	data object Failed : Claim
}

/** An open port and how many leases are keeping it open */
private class ConsoleHolder(val console: SerialConsole) {
	var leases = 0
}

/**
 * One holder's claim on a port. The port stays open while any lease on it is alive and closes once
 * the last one is released, so every holder must [release] what it took. Releasing never suspends
 * and repeats harmlessly
 */
@OptIn(ExperimentalAtomicApi::class)
class SerialLease internal constructor(
	val console: SerialConsole,
	private val onRelease: () -> Unit,
) {
	private val released = AtomicBoolean(false)

	fun release() {
		if (released.compareAndSet(false, true)) onRelease()
	}
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
	private val consoles = mutableMapOf<String, ConsoleHolder>()

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

	/** The caller owns the returned lease and must release it, or the port never closes */
	suspend fun awaitConsole(portLocation: String, timeout: Duration): SerialLease? = withTimeoutOrNull(timeout) {
		claimConsole(portLocation)
	}

	private suspend fun claimConsole(portLocation: String): SerialLease? {
		while (true) {
			context.state.map { isFree(it, portLocation) }.first { it }
			when (val claim = ownership.withLock { claimOnce(portLocation) }) {
				is Claim.Got -> return claim.lease
				is Claim.Failed -> return null
				is Claim.Retry -> {}
			}
		}
	}

	private suspend fun claimOnce(portLocation: String): Claim {
		if (!isFree(context.state.value, portLocation)) return Claim.Retry
		consoles[portLocation]?.let { existing ->
			if (existing.console.closed.isActive) return Claim.Got(lease(portLocation, existing))
			// this port failed and the cleanup in [watchForClose] hasn't run yet
			closeConsole(portLocation)
		}
		AppLogger.serial.info("Opening serial console on $portLocation")
		val clearResetLines = context.state.value.ports[portLocation]?.type == SerialDeviceType.ESP_TRACKER
		val console = SerialConsole.open(watcher, portLocation, clearResetLines) ?: return Claim.Failed
		val holder = ConsoleHolder(console)
		consoles[portLocation] = holder
		AppLogger.serial.info("Opened serial console on $portLocation")
		watchForClose(console)
		return Claim.Got(lease(portLocation, holder))
	}

	/**
	 * Hands out a lease on [holder]. Call under [ownership] and with no suspension before the lease
	 * reaches its holder, so a cancelled claim can't leave the count raised
	 */
	private fun lease(portLocation: String, holder: ConsoleHolder): SerialLease {
		holder.leases++
		return SerialLease(holder.console) {
			context.scope.launch {
				ownership.withLock {
					holder.leases--
					// A claim that landed while this release was queued keeps the port open
					if (holder.leases == 0 && consoles[portLocation] === holder) {
						AppLogger.serial.info("Last holder of $portLocation released it")
						closeConsole(portLocation)
					}
				}
			}
		}
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
		val holder = consoles.remove(portLocation) ?: return
		AppLogger.serial.info("Closing serial console on $portLocation")
		holder.console.close()
	}

	private fun watchForClose(console: SerialConsole) {
		context.scope.launch {
			console.closed.join()
			if (consoles[console.portLocation]?.console === console) AppLogger.serial.warn("Serial port ${console.portLocation} reported itself closed")
			ownership.withLock {
				if (consoles[console.portLocation]?.console === console) closeConsole(console.portLocation)
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
