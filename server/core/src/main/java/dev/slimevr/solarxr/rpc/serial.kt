package dev.slimevr.solarxr.rpc

import dev.slimevr.logging.AppLogger
import dev.slimevr.serial.SerialConsole
import dev.slimevr.serial.SerialLease
import dev.slimevr.serial.SerialServer
import dev.slimevr.serial.SerialServerState
import dev.slimevr.serial.sortPorts
import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.SolarXRBridgeBehaviour
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import solarxr_protocol.rpc.CloseSerialRequest
import solarxr_protocol.rpc.OpenSerialRequest
import solarxr_protocol.rpc.SerialConsoleStatus
import solarxr_protocol.rpc.SerialDevice
import solarxr_protocol.rpc.SerialDevicesRequest
import solarxr_protocol.rpc.SerialDevicesResponse
import solarxr_protocol.rpc.SerialKeepaliveRequest
import solarxr_protocol.rpc.SerialTrackerCustomCommandRequest
import solarxr_protocol.rpc.SerialTrackerFactoryResetRequest
import solarxr_protocol.rpc.SerialTrackerGetInfoRequest
import solarxr_protocol.rpc.SerialTrackerGetWifiScanRequest
import solarxr_protocol.rpc.SerialTrackerRebootRequest
import solarxr_protocol.rpc.SerialUpdateResponse
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

// Wait before reopening a port whose console dropped while the port stayed plugged in
private val REOPEN_DELAY = 1.seconds

// How long the client has to renew its hold before the server takes the ports back
private val KEEPALIVE_GRACE = 15.seconds

private const val LOG_QUEUE_CAPACITY = 2000
private const val MAX_BATCH_CHARS = 16_000

/**
 * Reuses the lease this client already holds on the port, replacing it when the port died under it.
 * Leases stay in [leases] for as long as the client keeps its console open
 */
private suspend fun claimForClient(
	serialServer: SerialServer,
	leases: MutableMap<String, SerialLease>,
	portLocation: String,
): SerialLease? {
	leases[portLocation]?.let { held ->
		if (held.console.closed.isActive) return held
		held.release()
		leases -= portLocation
	}
	return serialServer.awaitConsole(portLocation, Duration.INFINITE)?.also { leases[portLocation] = it }
}

/**
 * Follows one port: streams its console while open, reports why it isn't otherwise, and opens it
 * again when it comes back or a flash releases it
 */
private suspend fun runConsoleSession(
	receiver: SolarXRBridge,
	serialServer: SerialServer,
	portLocation: String,
	activeConsole: MutableStateFlow<SerialConsole?>,
	leases: MutableMap<String, SerialLease>,
) = coroutineScope {
	launch {
		serialServer.context.state
			.map { blockedStatus(it, portLocation) }
			.distinctUntilChanged()
			.filterNotNull()
			.collect { status -> receiver.sendRpc(SerialUpdateResponse(status = status)) }
	}

	while (true) {
		AppLogger.solarxr.info("Serial session waiting for a console on $portLocation")
		if (isPortFree(serialServer.context.state.value, portLocation)) {
			receiver.sendRpc(SerialUpdateResponse(status = SerialConsoleStatus.OPENING))
		}
		val lease = claimForClient(serialServer, leases, portLocation)
		if (lease == null) {
			// The port refused to open, so there is nothing to do until something changes
			val before = serialServer.context.state.value
			receiver.sendRpc(SerialUpdateResponse(status = SerialConsoleStatus.OPEN_FAILED))
			serialServer.context.state.first { it != before }
			continue
		}

		val console = lease.console
		activeConsole.value = console
		val device = serialServer.context.state.value.ports[portLocation]?.toSerialDevice()
		receiver.sendRpc(SerialUpdateResponse(status = SerialConsoleStatus.OPEN, device = device))
		val stream = launch { streamLines(receiver, console, device) }
		console.closed.join()
		AppLogger.solarxr.info("Serial console on $portLocation ended")
		stream.cancel()
		activeConsole.value = null

		if (isPortFree(serialServer.context.state.value, portLocation)) delay(REOPEN_DELAY)
	}
}

private fun blockedStatus(state: SerialServerState, portLocation: String) = when {
	portLocation in state.flashing -> SerialConsoleStatus.BUSY
	portLocation !in state.ports -> SerialConsoleStatus.WAITING
	else -> null
}

private fun isPortFree(state: SerialServerState, portLocation: String) = blockedStatus(state, portLocation) == null

// Lines that arrive while a send is in flight go out together, so a burst costs a few messages
private suspend fun streamLines(
	receiver: SolarXRBridge,
	console: SerialConsole,
	device: SerialDevice?,
) = coroutineScope {
	val queue = Channel<String>(LOG_QUEUE_CAPACITY, BufferOverflow.DROP_OLDEST)
	launch { console.lines.collect { queue.trySend(it) } }

	for (first in queue) {
		val batch = StringBuilder().append(first).append('\n')
		while (batch.length < MAX_BATCH_CHARS) {
			val next = queue.tryReceive().getOrNull() ?: break
			batch.append(next).append('\n')
		}
		receiver.sendRpc(SerialUpdateResponse(log = batch.toString(), device = device, status = SerialConsoleStatus.OPEN))
	}
}

class SerialBehaviour(private val serialServer: SerialServer) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		val scope = receiver.context.scope

		var session: Job? = null
		var watchdog: Job? = null
		val activeConsole = MutableStateFlow<SerialConsole?>(null)
		val leases = mutableMapOf<String, SerialLease>()
		val renewals = Channel<Unit>(Channel.CONFLATED)

		fun releaseLeases() {
			leases.values.forEach { it.release() }
			leases.clear()
		}

		suspend fun endSession() {
			session?.cancelAndJoin()
			session = null
			watchdog?.cancel()
			watchdog = null
			activeConsole.value = null
			releaseLeases()
		}

		// A client that crashes, reloads or loses its socket never sends CloseSerialRequest, so the
		// scope dying has to drop its claims too
		scope.coroutineContext.job.invokeOnCompletion { releaseLeases() }

		// The full list goes out on every change. Clients replace their list, so removals need no event
		serialServer.context.state
			.map { sortPorts(it.ports.values) }
			.distinctUntilChanged()
			.drop(1)
			.onEach { ports -> receiver.sendRpc(SerialDevicesResponse(devices = ports.map { it.toSerialDevice() })) }
			.launchIn(scope)

		receiver.rpcDispatcher.on<SerialDevicesRequest> {
			receiver.sendRpc(
				SerialDevicesResponse(devices = sortPorts(serialServer.context.state.value.ports.values).map { it.toSerialDevice() }),
			)
		}.launchIn(scope)

		receiver.rpcDispatcher.on<OpenSerialRequest> { req ->
			val portLocation = req.port ?: return@on
			AppLogger.solarxr.info("Serial console requested on $portLocation")
			// Joining keeps the old session from adding a lease after this one took over the map
			session?.cancelAndJoin()
			activeConsole.value = null
			session = scope.launch { runConsoleSession(receiver, serialServer, portLocation, activeConsole, leases) }
			if (watchdog == null) {
				watchdog = scope.launch {
					while (withTimeoutOrNull(KEEPALIVE_GRACE) { renewals.receive() } != null) continue
					AppLogger.solarxr.warn("Serial keepalive stopped, taking the client's consoles back")
					watchdog = null
					endSession()
				}
			}
		}.launchIn(scope)

		receiver.rpcDispatcher.on<SerialKeepaliveRequest> {
			renewals.trySend(Unit)
		}.launchIn(scope)

		receiver.rpcDispatcher.on<CloseSerialRequest> {
			AppLogger.solarxr.info("Serial consoles released by the client")
			endSession()
		}.launchIn(scope)

		receiver.rpcDispatcher.on<SerialTrackerRebootRequest> {
			activeConsole.value?.write("REBOOT")
		}.launchIn(scope)

		receiver.rpcDispatcher.on<SerialTrackerGetInfoRequest> {
			activeConsole.value?.write("GET INFO")
		}.launchIn(scope)

		receiver.rpcDispatcher.on<SerialTrackerFactoryResetRequest> {
			activeConsole.value?.write("FRST")
		}.launchIn(scope)

		receiver.rpcDispatcher.on<SerialTrackerGetWifiScanRequest> {
			activeConsole.value?.write("GET WIFISCAN")
		}.launchIn(scope)

		receiver.rpcDispatcher.on<SerialTrackerCustomCommandRequest> { req ->
			val command = req.command ?: return@on
			activeConsole.value?.write(command)
		}.launchIn(scope)
	}
}
