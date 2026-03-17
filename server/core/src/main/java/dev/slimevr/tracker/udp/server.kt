package dev.slimevr.tracker.udp

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
	val connections: MutableMap<String, UDPConnection>
)

suspend fun processPacket(
	socket: BoundDatagramSocket,
	datagram: Datagram,
	workerId: Int
) {


}

const val PACKET_WORKERS = 4

suspend fun createUDPTrackerServer(
	serverContext: VRServerContext,
	configContext: ConfigContext
): UDPTrackerServerState {
	val state = UDPTrackerServerState(
		port = configContext.state.value.settingsConfig.trackerPort,
		connections = mutableMapOf()
	)

	val selectorManager = SelectorManager(Dispatchers.IO)
	val serverSocket =
		aSocket(selectorManager).udp().bind(InetSocketAddress("0.0.0.0", state.port))
	val packetChannel = Channel<Datagram>(capacity = Channel.BUFFERED)

	supervisorScope {
		launch(Dispatchers.IO) {
			while (isActive) {
				val datagram = serverSocket.receive()
				packetChannel.send(datagram)
			}
		}

		repeat(PACKET_WORKERS) { workerId ->
			launch(Dispatchers.Default) {
				for (datagram in packetChannel) {
					val packet = readPacket(datagram.packet)
					if (packet == null) {
						println("null packet")
						continue
					}

					val address = datagram.address as InetSocketAddress
					val connContext = state.connections[address.hostname]

					if (connContext !== null)
						connContext.packetEvents.emit(packet = packet)
					else {
						val newContext = createUDPConnectionContext(
							id = address.hostname,
							remoteAddress = address,
							socket = serverSocket,
							scope = this
						)

						state.connections[address.hostname] = newContext
						newContext.packetEvents.emit(packet = packet)
					}

				}
			}
		}
	}

	return state
}
