package dev.slimevr.udp

import dev.slimevr.AppContextProvider
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.logging.AppLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.io.Source
import kotlin.time.measureTime

data class UdpServerState(
	val connections: Map<String, UDPConnection>,
	val unknownDevices: Map<String, UnknownDeviceState> = emptyMap(),
	val ignoredHardwareIds: Set<String> = emptySet(),
)

sealed interface UdpServerActions {
	data class ConnectionAdded(val address: String, val conn: UDPConnection) : UdpServerActions
	data class ConnectionRemoved(val address: String) : UdpServerActions
	data class UnknownDeviceSeen(val device: UnknownDeviceState) : UdpServerActions
	data class UnknownDeviceRemoved(val hardwareId: String) : UdpServerActions
	data class UnknownDevicesExpired(val cutoff: Long) : UdpServerActions
	data class IgnoreUnknownDevice(val hardwareId: String) : UdpServerActions
}

typealias UdpServerContext = Context<UdpServerState, UdpServerActions>
typealias UdpServerBehaviour = Behaviour<UdpServer>

class UdpServer(
	val context: UdpServerContext,
	// Wall-clock ms, replaceable so tests can run on virtual time
	val clock: () -> Long = System::currentTimeMillis,
	private val bind: UdpBind,
) {
	private var receiveJob: Job? = null

	fun startObserving() = context.observeAll(this)

	fun addConnection(address: String, conn: UDPConnection) {
		val oldConn = context.state.value.connections[address]
		context.dispatch(UdpServerActions.ConnectionAdded(address, conn))
		oldConn?.dispose()
	}

	fun removeConnection(address: String) {
		val oldConn = context.state.value.connections[address]
		if (oldConn != null) {
			context.dispatch(UdpServerActions.ConnectionRemoved(address))
			oldConn.dispose()
		}
	}

	fun findConnectionForDevice(deviceId: Int): UDPConnection? = context.state.value.connections.values.find { conn ->
		conn.context.state.value.deviceId == deviceId
	}

	/** Listens on the configured tracker port and follows it when the settings profile changes it. */
	fun startReceiving(appContext: AppContextProvider, scope: CoroutineScope) {
		if (receiveJob != null) return
		receiveJob = scope.launch(Dispatchers.IO) {
			appContext.config.settings.context.state
				.map { it.data.trackersConfig.trackerPort }
				.distinctUntilChanged()
				.collectLatest { port ->
					connections().forEach { removeConnection(it) }
					receiveOn(port, appContext, scope)
				}
		}
	}

	private fun connections() = context.state.value.connections.keys.toList()

	private suspend fun receiveOn(port: Int, appContext: AppContextProvider, scope: CoroutineScope) {
		val socket = try {
			bind(port)
		} catch (e: Exception) {
			AppLogger.udp.error(e, "Failed to bind the tracker port $port")
			return
		}
		AppLogger.udp.info("Listening for trackers on port $port")
		try {
			socket.listen { from, payload -> handleDatagram(socket, from, payload, appContext, scope) }
		} finally {
			release(socket)
		}
	}

	private fun handleDatagram(socket: UdpSocket, from: UdpPeer, src: Source, appContext: AppContextProvider, scope: CoroutineScope) {
		try {
			val took = measureTime {
				val packetId = src.readInt()
				val packetNumber = src.readLong()
				val type = PacketType.fromId(packetId) ?: return@measureTime
				val packetData = readPacket(type, src)

				val conn = context.state.value.connections[from.address]

				val event = PacketEvent(data = packetData, packetNumber = packetNumber)

				if (conn != null) {
					conn.packetChannel.trySend(event)
				} else {
					val newConn = UDPConnection.create(
						peer = from,
						socket = socket,
						appContext = appContext,
						scope = scope,
					)
					addConnection(from.address, newConn)
					newConn.packetChannel.trySend(event)
				}
			}
			if (took.inWholeMilliseconds > 2) {
				AppLogger.udpReceive.warn("Packet processing took too long (${took.inWholeMilliseconds}ms)")
			}
		} catch (e: UnsupportedOperationException) {
			AppLogger.udpReceive.warn("Unsupported operation when processing UDP packet: ${e.message}")
		} catch (e: Exception) {
			AppLogger.udpReceive.error(e, "Error processing UDP packet")
		}
	}

	// Must also run when cancelled, and the close call can block
	private suspend fun release(socket: UdpSocket) = withContext(NonCancellable + Dispatchers.IO) {
		socket.close()
	}

	suspend fun dispose() {
		receiveJob?.cancelAndJoin()
		receiveJob = null
	}

	companion object {
		val INITIAL_STATE = UdpServerState(connections = emptyMap())

		fun create(
			scope: CoroutineScope,
			clock: () -> Long = System::currentTimeMillis,
			bind: UdpBind,
		): UdpServer {
			val context = Context.create(
				initialState = INITIAL_STATE,
				scope = scope,
				reducer = ::reduce,
				behaviours = listOf(UnknownDeviceExpiryBehaviour()),
				name = "UdpServer",
			)
			val server = UdpServer(context, clock, bind)
			server.startObserving()
			return server
		}
	}
}
