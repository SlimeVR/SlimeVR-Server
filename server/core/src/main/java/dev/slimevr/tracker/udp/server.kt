package dev.slimevr.tracker.udp

import dev.slimevr.VRServer
import dev.slimevr.VRServerContext
import dev.slimevr.config.ConfigContext
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.BoundDatagramSocket
import io.ktor.network.sockets.Datagram
import io.ktor.network.sockets.InetSocketAddress
import io.ktor.network.sockets.aSocket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

data class UDPTrackerServerState(
	val port: Int,
	val connections: MutableMap<String, UDPConnection>,
)

suspend fun createUDPTrackerServer(
	serverContext: VRServer,
	configContext: ConfigContext,
): UDPTrackerServerState {
	val state = UDPTrackerServerState(
		port = configContext.state.value.settingsConfig.trackerPort,
		connections = mutableMapOf(),
	)

	val selectorManager = SelectorManager(Dispatchers.IO)
	val serverSocket =
		aSocket(selectorManager).udp().bind(InetSocketAddress("0.0.0.0", state.port))

	supervisorScope {
		launch(Dispatchers.IO) {
			while (isActive) {
				val datagram = serverSocket.receive()
				val packetId = datagram.packet.readInt()
				val packetNumber = datagram.packet.readLong()
				val type = PacketType.fromId(packetId) ?: continue
				val packetData = readPacket(type, datagram.packet)

				val address = datagram.address as InetSocketAddress
				val connContext = state.connections[address.hostname]

				val event = PacketEvent(
					data = packetData,
					packetNumber = packetNumber,
				)

				if (connContext !== null) {
					connContext.packetEvents.emit(event)
				} else {
					val newContext = createUDPConnectionContext(
						id = address.hostname,
						remoteAddress = address,
						socket = serverSocket,
						serverContext = serverContext,
						scope = this,
					)

					state.connections[address.hostname] = newContext
					newContext.packetEvents.emit(event)
				}
			}
		}
	}

	return state
}
