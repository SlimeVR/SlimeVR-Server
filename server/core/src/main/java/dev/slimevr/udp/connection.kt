package dev.slimevr.udp

import dev.slimevr.EventDispatcher
import dev.slimevr.VRServer
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.device.Device
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerIdNum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

data class LastPing(
	val id: Int,
	val startTime: Long,
	val duration: Long,
)

data class UDPConnectionState(
	val id: String,
	val lastPacket: Long,
	val lastPacketNum: Long,
	val lastPing: LastPing,
	val didHandshake: Boolean,
	val address: String,
	val port: Int,
	val deviceId: Int?,
	val trackerIds: List<TrackerIdNum>,
)

sealed interface UDPConnectionActions {
	data class StartPing(val startTime: Long) : UDPConnectionActions
	data class ReceivedPong(val id: Int, val duration: Long) : UDPConnectionActions
	data class Handshake(val deviceId: Int) : UDPConnectionActions
	data class LastPacket(val packetNum: Long? = null, val time: Long) : UDPConnectionActions
	data class AssignTracker(val trackerId: TrackerIdNum) : UDPConnectionActions
	data object Disconnected : UDPConnectionActions
}

typealias UDPConnectionContext = Context<UDPConnectionState, UDPConnectionActions>
typealias UDPConnectionBehaviour = Behaviour<UDPConnectionState, UDPConnectionActions, UDPConnection>

class UDPConnection(
	val context: UDPConnectionContext,
	val serverContext: VRServer,
	val packetEvents: UDPPacketDispatcher,
	val packetChannel: Channel<PacketEvent<UDPPacket>>,
	private val socket: DatagramSocket,
	private val remoteInetAddress: InetAddress,
	private val remotePort: Int,
	private val scope: CoroutineScope,
) {
	fun send(packet: UDPPacket) {
		scope.launch(Dispatchers.IO) {
			val buf = Buffer()
			writePacket(buf, packet)
			val bytes = buf.readByteArray()
			socket.send(DatagramPacket(bytes, bytes.size, remoteInetAddress, remotePort))
		}
	}

	fun getDevice(): Device? {
		val deviceId = context.state.value.deviceId
		return if (deviceId != null) serverContext.getDevice(deviceId) else null
	}

	fun getTracker(id: Int): Tracker? {
		val trackerId = context.state.value.trackerIds.find { it.trackerNum == id }
		return if (trackerId != null) serverContext.getTracker(trackerId.id) else null
	}

	companion object {
		fun create(
			id: String,
			socket: DatagramSocket,
			remoteIp: String,
			remotePort: Int,
			serverContext: VRServer,
			scope: CoroutineScope,
		): UDPConnection {
			val behaviours = listOf(
				PacketBehaviour,
				HandshakeBehaviour,
				TimeoutBehaviour,
				PingBehaviour,
				DeviceStatsBehaviour,
				SensorInfoBehaviour,
				SensorRotationBehaviour,
			)

			val context = Context.create(
				initialState = UDPConnectionState(
					id = id,
					lastPacket = System.currentTimeMillis(),
					lastPacketNum = 0,
					lastPing = LastPing(id = 0, duration = 0, startTime = 0),
					didHandshake = false,
					address = remoteIp,
					port = remotePort,
					deviceId = null,
					trackerIds = listOf(),
				),
				scope = scope,
				behaviours = behaviours,
			)

			val dispatcher = EventDispatcher<PacketEvent<UDPPacket>> { it.data::class }
			val packetChannel = Channel<PacketEvent<UDPPacket>>(capacity = 256)
			val remoteInetAddress = InetAddress.getByName(remoteIp)

			val conn = UDPConnection(
				context = context,
				serverContext = serverContext,
				packetEvents = dispatcher,
				packetChannel = packetChannel,
				socket = socket,
				remoteInetAddress = remoteInetAddress,
				remotePort = remotePort,
				scope = scope,
			)

			behaviours.forEach { it.observe(conn) }

			// Dedicated coroutine per connection so the receive loop is never blocked by packet processing
			scope.launch {
				for (event in packetChannel) {
					// We skip any packet from the tracker that are not handshake packets
					// if we didn't do a handshake with the server
					// this prevents from receiving packets if the server does not know about the
					// tracker yet. This usually happen when you restart the server with already
					// connected trackers
					if (!context.state.value.didHandshake && event.data !is PreHandshakePacket) continue
					dispatcher.emit(event)
				}
			}

			return conn
		}
	}
}