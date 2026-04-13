package dev.slimevr.udp

import dev.slimevr.AppLogger
import dev.slimevr.VRServer
import dev.slimevr.config.AppConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import kotlinx.io.Buffer
import java.net.DatagramPacket
import java.net.DatagramSocket
import kotlin.time.measureTime

class UDPTrackerServerState(
	val port: Int,
	val connections: MutableMap<String, UDPConnection>,
)

suspend fun createUDPTrackerServer(
	serverContext: VRServer,
	configContext: AppConfig,
): UDPTrackerServerState {
	val state = UDPTrackerServerState(
		port = configContext.settings.context.state.value.data.trackerPort,
		connections = mutableMapOf(),
	)

	val socket = withContext(Dispatchers.IO) {
		DatagramSocket(state.port)
	}
	val recvBuffer = ByteArray(2048)
	val recvPacket = DatagramPacket(recvBuffer, recvBuffer.size)

	supervisorScope {
		val udpScope = this
		launch(Dispatchers.IO) {
			while (isActive) {
				socket.receive(recvPacket)
				val took = measureTime {
					val src = Buffer()
					src.write(recvBuffer, 0, recvPacket.length)

					val packetId = src.readInt()
					val packetNumber = src.readLong()
					val type = PacketType.fromId(packetId) ?: continue
					val packetData = readPacket(type, src)

					val ip = recvPacket.address.hostAddress
					val port = recvPacket.port
					val connContext = state.connections[ip]

					val event = PacketEvent(
						data = packetData,
						packetNumber = packetNumber,
					)

					if (connContext !== null) {
						connContext.packetChannel.trySend(event)
					} else {
						val newContext = UDPConnection.create(
							id = ip,
							remoteIp = ip,
							remotePort = port,
							socket = socket,
							serverContext = serverContext,
							settings = configContext.settings,
							scope = udpScope,
						)

						state.connections[ip] = newContext
						newContext.packetChannel.trySend(event)
					}
				}
				if (took.inWholeMilliseconds > 2) {
					AppLogger.udp.warn("Packet processing took too long ${took.inWholeMilliseconds}")
				}
			}
		}
	}

	return state
}
