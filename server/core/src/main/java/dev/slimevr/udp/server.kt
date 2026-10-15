package dev.slimevr.udp

import dev.slimevr.AppContextProvider
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.logging.AppLogger
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.BoundDatagramSocket
import io.ktor.network.sockets.InetSocketAddress
import io.ktor.network.sockets.aSocket
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.measureTime

data class UdpServerState(
	val connections: Map<String, UDPConnection>,
	val unknownDevices: Map<String, UnknownDeviceState> = emptyMap(),
	val ignoredMacs: Set<String> = emptySet(),
)

sealed interface UdpServerActions {
	data class ConnectionAdded(val address: String, val conn: UDPConnection) : UdpServerActions
	data class ConnectionRemoved(val address: String) : UdpServerActions
	data class UnknownDeviceSeen(val device: UnknownDeviceState) : UdpServerActions
	data class UnknownDeviceRemoved(val macAddress: String) : UdpServerActions
	data class UnknownDevicesExpired(val cutoff: Long) : UdpServerActions
	data class IgnoreUnknownDevice(val macAddress: String) : UdpServerActions
}

typealias UdpServerContext = Context<UdpServerState, UdpServerActions>
typealias UdpServerBehaviour = Behaviour<UdpServer>

class UdpServer(
	val context: UdpServerContext,
	// Wall-clock ms, replaceable so tests can run on virtual time
	val clock: () -> Long = System::currentTimeMillis,
	private val addressResolver: (InetSocketAddress) -> String,
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
		val selectorManager = SelectorManager(Dispatchers.IO)
		val socket = try {
			aSocket(selectorManager).udp().bind(port = port)
		} catch (e: Exception) {
			AppLogger.udp.error(e, "Failed to bind the tracker port $port")
			release(null, selectorManager)
			return
		}
		AppLogger.udp.info("Listening for trackers on port $port")
		try {
			while (currentCoroutineContext().isActive) {
				try {
					val recvPacket = socket.receive()
					val took = measureTime {
						val src = recvPacket.packet
						val packetId = src.readInt()
						val packetNumber = src.readLong()
						val type = PacketType.fromId(packetId) ?: return@measureTime
						val packetData = readPacket(type, src)

						val remoteAddress = recvPacket.address as? InetSocketAddress ?: return@measureTime
						val address = addressResolver(remoteAddress)
						val conn = context.state.value.connections[address]

						val event = PacketEvent(data = packetData, packetNumber = packetNumber)

						if (conn != null) {
							conn.packetChannel.trySend(event)
						} else {
							val newConn = UDPConnection.create(
								address = address,
								remoteAddress = remoteAddress,
								socket = socket,
								appContext = appContext,
								scope = scope,
							)
							addConnection(address, newConn)
							newConn.packetChannel.trySend(event)
						}
					}
					if (took.inWholeMilliseconds > 2) {
						AppLogger.udp.warn("Packet processing took too long (${took.inWholeMilliseconds}ms)")
					}
				} catch (e: CancellationException) {
					throw e
				} catch (e: UnsupportedOperationException) {
					AppLogger.udp.warn("Unsupported operation when processing UDP packet: ${e.message}")
				} catch (e: Exception) {
					AppLogger.udp.error(e, "Error processing UDP packet")
				}
			}
		} finally {
			release(socket, selectorManager)
		}
	}

	// Must also run when cancelled, and the close calls can block
	private suspend fun release(socket: BoundDatagramSocket?, selectorManager: SelectorManager) = withContext(NonCancellable + Dispatchers.IO) {
		socket?.close()
		selectorManager.close()
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
			addressResolver: (InetSocketAddress) -> String,
		): UdpServer {
			val context = Context.create(
				initialState = INITIAL_STATE,
				scope = scope,
				reducer = ::reduce,
				behaviours = listOf(UnknownDeviceExpiryBehaviour()),
				name = "UdpServer",
			)
			val server = UdpServer(context, clock, addressResolver)
			server.startObserving()
			return server
		}
	}
}
