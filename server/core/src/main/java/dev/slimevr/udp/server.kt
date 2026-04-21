package dev.slimevr.udp

import dev.slimevr.AppContextProvider
import dev.slimevr.AppLogger
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.io.Buffer
import java.net.DatagramPacket
import java.net.DatagramSocket
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
	fun startObserving() = context.observeAll(this)

	fun findConnectionForDevice(deviceId: Int): UDPConnection? = context.state.value.connections.values.find { conn ->
		conn.context.state.value.deviceId == deviceId
	}

	fun startReceiving(appContext: AppContextProvider, scope: CoroutineScope) {
		scope.launch {
			val port = appContext.config.settings.context.state.value.data.trackerPort
			val socket = withContext(Dispatchers.IO) { DatagramSocket(port) }
			val recvBuffer = ByteArray(2048)
			val recvPacket = DatagramPacket(recvBuffer, recvBuffer.size)

			launch(Dispatchers.IO) {
				while (isActive) {
					socket.receive(recvPacket)
					val took = measureTime {
						val src = Buffer()
						src.write(recvBuffer, 0, recvPacket.length)

						val packetId = src.readInt()
						val packetNumber = src.readLong()
						val type = PacketType.fromId(packetId) ?: return@measureTime
						val packetData = readPacket(type, src)

						val ip = recvPacket.address.hostAddress
						val port = recvPacket.port
						val conn = context.state.value.connections[ip]

						val event = PacketEvent(data = packetData, packetNumber = packetNumber)

						if (conn != null) {
							conn.packetChannel.trySend(event)
						} else {
							val newConn = UDPConnection.create(
								id = ip,
								remoteIp = ip,
								remotePort = port,
								socket = socket,
								appContext = appContext,
								scope = scope,
							)
							context.dispatch(UdpServerActions.ConnectionAdded(ip, newConn))
							newConn.packetChannel.trySend(event)
						}
					}
					if (took.inWholeMilliseconds > 2) {
						AppLogger.udp.warn("Packet processing took too long ${took.inWholeMilliseconds}")
					}
				}
			}
		}
	}

	companion object {
		val INITIAL_STATE = UdpServerState(connections = emptyMap())

		fun create(scope: CoroutineScope): UdpServer {
			val context = Context.create(
				initialState = INITIAL_STATE,
				scope = scope,
				behaviours = listOf(UdpServerBaseBehaviour),
			)
			val server = UdpServer(context)
			server.startObserving()
			return server
		}
	}
}
