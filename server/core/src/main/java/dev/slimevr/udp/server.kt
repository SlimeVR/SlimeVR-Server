package dev.slimevr.udp

import dev.slimevr.AppContextProvider
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.logging.AppLogger
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.BoundDatagramSocket
import io.ktor.network.sockets.InetSocketAddress
import io.ktor.network.sockets.aSocket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.measureTime

data class UdpServerState(
	val connections: Map<String, UDPConnection>,
)

sealed interface UdpServerActions {
	data class ConnectionAdded(val address: String, val conn: UDPConnection) : UdpServerActions
	data class ConnectionRemoved(val address: String) : UdpServerActions
}

typealias UdpServerContext = Context<UdpServerState, UdpServerActions>
typealias UdpServerBehaviour = Behaviour<UdpServer>

class UdpServer(val context: UdpServerContext, private val addressResolver: (InetSocketAddress) -> String) {
	private var receiveJob: Job? = null
	private var socket: BoundDatagramSocket? = null
	private var selectorManager: SelectorManager? = null

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

	fun startReceiving(appContext: AppContextProvider, scope: CoroutineScope) {
		if (receiveJob != null) return
		receiveJob = scope.launch(Dispatchers.IO) {
			val port = appContext.config.settings.context.state.value.data.trackersConfig.trackerPort
			val selectorManager = SelectorManager(Dispatchers.IO)
			val socket = aSocket(selectorManager).udp().bind(port = port)
			this@UdpServer.selectorManager = selectorManager
			this@UdpServer.socket = socket
			try {
				while (isActive) {
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
					} catch (e: Exception) {
						AppLogger.udp.error(e, "Error processing UDP packet")
					}
				}
			} finally {
				this@UdpServer.receiveJob = null
				this@UdpServer.socket = null
				this@UdpServer.selectorManager = null
				socket.close()
				selectorManager.close()
			}
		}
	}

	suspend fun dispose() {
		socket?.close()
		selectorManager?.close()
		receiveJob?.cancelAndJoin()
		receiveJob = null
	}

	companion object {
		val INITIAL_STATE = UdpServerState(connections = emptyMap())

		fun create(scope: CoroutineScope, addressResolver: (InetSocketAddress) -> String): UdpServer {
			val context = Context.create(
				initialState = INITIAL_STATE,
				scope = scope,
				reducer = ::reduce,
				name = "UdpServer",
			)
			val server = UdpServer(context, addressResolver)
			server.startObserving()
			return server
		}
	}
}
