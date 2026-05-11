package dev.slimevr.udp

import dev.slimevr.AppContextProvider
import dev.slimevr.AppLogger
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.util.safeLaunch
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.BoundDatagramSocket
import io.ktor.network.sockets.InetSocketAddress
import io.ktor.network.sockets.aSocket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.isActive
import kotlin.time.measureTime

data class UdpServerState(
	val connections: Map<String, UDPConnection>,
)

sealed interface UdpServerActions {
	data class ConnectionAdded(val ip: String, val conn: UDPConnection) : UdpServerActions
	data class ConnectionRemoved(val ip: String) : UdpServerActions
}

typealias UdpServerContext = Context<UdpServerState, UdpServerActions>
typealias UdpServerBehaviour = Behaviour<UdpServerState, UdpServerActions, UdpServer>

object UdpServerBaseBehaviour : UdpServerBehaviour {
	override fun reduce(state: UdpServerState, action: UdpServerActions) = when (action) {
		is UdpServerActions.ConnectionAdded -> state.copy(connections = state.connections + (action.ip to action.conn))
		is UdpServerActions.ConnectionRemoved -> state.copy(connections = state.connections - action.ip)
	}
}

class UdpServer(val context: UdpServerContext) {
	private var receiveJob: Job? = null
	private var socket: BoundDatagramSocket? = null
	private var selectorManager: SelectorManager? = null

	fun startObserving() = context.observeAll(this)

	fun findConnectionForDevice(deviceId: Int): UDPConnection? = context.state.value.connections.values.find { conn ->
		conn.context.state.value.deviceId == deviceId
	}

	fun startReceiving(appContext: AppContextProvider, scope: CoroutineScope) {
		if (receiveJob != null) return
		receiveJob = scope.safeLaunch {
			val port = appContext.config.settings.context.state.value.data.trackerPort
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
							val id = addressKey(remoteAddress)
							val conn = context.state.value.connections[id]

							val event = PacketEvent(data = packetData, packetNumber = packetNumber)

							if (conn != null) {
								conn.packetChannel.trySend(event)
							} else {
								val newConn = UDPConnection.create(
									id = id,
									remoteAddress = remoteAddress,
									socket = socket,
									appContext = appContext,
									scope = scope,
								)
								context.dispatch(UdpServerActions.ConnectionAdded(id, newConn))
								newConn.packetChannel.trySend(event)
							}
						}
						if (took.inWholeMilliseconds > 2) {
							AppLogger.udp.warn("Packet processing took too long ${took.inWholeMilliseconds}")
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

		private fun addressKey(address: InetSocketAddress): String {
			val raw = address.resolveAddress()
			return if (raw != null && raw.size == 4) {
				raw.joinToString(".") { byte -> (byte.toInt() and 0xFF).toString() }
			} else {
				address.hostname
			}
		}

		fun create(scope: CoroutineScope): UdpServer {
			val context = Context.create(
				initialState = INITIAL_STATE,
				scope = scope,
				behaviours = listOf(UdpServerBaseBehaviour),
				name = "UdpServer",
			)
			val server = UdpServer(context)
			server.startObserving()
			return server
		}
	}
}
