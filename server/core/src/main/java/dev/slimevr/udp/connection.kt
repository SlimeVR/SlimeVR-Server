package dev.slimevr.udp

import dev.slimevr.AppContextProvider
import dev.slimevr.EventDispatcher
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.device.Device
import dev.slimevr.tracker.Tracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import solarxr_protocol.datatypes.MagnetometerStatus

data class LastPing(
	val id: Int,
	val startTime: Long,
)

data class SensorConfigFlags(
	val magStatus: MagnetometerStatus,
)

data class TrackerSensorIds(val trackerId: Int, val sensorId: Int)

data class UDPConnectionState(
	val address: String,
	val lastPacket: Long,
	val lastPacketNum: Long,
	val lastHandshake: Long = 0L,
	val lastPing: LastPing,
	val didHandshake: Boolean,
	val deviceId: Int?,
	val trackerIds: List<TrackerSensorIds>,
	val features: FirmwareFeatures?,
	val sensorConfigFlags: Map<Int, SensorConfigFlags>,
)

sealed interface UDPConnectionActions {
	data class StartPing(val startTime: Long, val pingId: Int) : UDPConnectionActions
	data class Handshake(val deviceId: Int) : UDPConnectionActions
	data class LastPacket(val packetNum: Long? = null, val time: Long) : UDPConnectionActions
	data class AssignTracker(val trackerId: TrackerSensorIds) : UDPConnectionActions
	data class FirmwareFeatures(val features: dev.slimevr.udp.FirmwareFeatures) : UDPConnectionActions
	data class SetSensorConfig(val sensorId: Int, val flags: SensorConfigFlags) : UDPConnectionActions
	data object TimedOut : UDPConnectionActions
}

typealias UDPConnectionContext = Context<UDPConnectionState, UDPConnectionActions>
typealias UDPConnectionBehaviour = Behaviour<UDPConnection>

class UDPConnection(
	val context: UDPConnectionContext,
	val appContext: AppContextProvider,
	val packetEvents: UDPPacketDispatcher,
	val packetChannel: Channel<PacketEvent<UDPPacket>>,
	private val socket: UdpSocket,
	private val peer: UdpPeer,
	private val scope: CoroutineScope,
) {
	fun send(packet: UDPPacket) {
		scope.launch {
			val buf = Buffer()
			writePacket(buf, packet)
			socket.send(peer, buf.readByteArray())
		}
	}

	fun startObserving() = context.observeAll(this)

	fun dispose() {
		context.scope.coroutineContext[kotlinx.coroutines.Job]?.cancel()
		packetChannel.close()
	}

	fun getDevice(): Device? = context.state.value.deviceId?.let { id -> appContext.server.getDevice(id) }

	fun getTracker(id: Int): Tracker? = context.state.value.trackerIds.find { it.sensorId == id }?.let { trackerId ->
		appContext.server.getTracker(trackerId.trackerId)
	}

	companion object {
		fun create(
			peer: UdpPeer,
			socket: UdpSocket,
			appContext: AppContextProvider,
			scope: CoroutineScope,
		): UDPConnection {
			val address = peer.address
			val behaviours = listOf(
				PacketBehaviour(),
				PacketLossBehaviour(),
				HandshakeBehaviour(),
				TimeoutBehaviour(),
				DisconnectBehaviour(),
				PingBehaviour(),
				DeviceStatsBehaviour(),
				SensorInfoBehaviour(),
				SensorRotationBehaviour(),
				BundledPacketBehaviour(),
				FlagsBehaviour(),
				TemperatureBehaviour(),
				SensorConfigBehaviour(),
				AckConfigBehaviour(),
			)

			val connJob = kotlinx.coroutines.SupervisorJob(scope.coroutineContext[kotlinx.coroutines.Job])
			val connScope = CoroutineScope(scope.coroutineContext + connJob)

			val context = Context.create(
				initialState = UDPConnectionState(
					address = address,
					lastPacket = System.currentTimeMillis(),
					lastPacketNum = 0,
					lastPing = LastPing(id = 0, startTime = 0),
					didHandshake = false,
					deviceId = null,
					trackerIds = listOf(),
					features = null,
					sensorConfigFlags = emptyMap(),
				),
				scope = connScope,
				reducer = ::reduce,
				behaviours = behaviours,
				name = "UDPConnection[$address]",
			)

			// Same reasoning as the HID dispatcher: these are state samples, the newest one wins.
			val dispatcher = EventDispatcher<PacketEvent<UDPPacket>>(
				name = "UDP[$address]",
				scope = context.scope,
				capacity = 64,
				onBufferOverflow = BufferOverflow.DROP_OLDEST,
			)
			val packetChannel = Channel<PacketEvent<UDPPacket>>(capacity = 256)

			val conn = UDPConnection(
				context = context,
				appContext = appContext,
				packetEvents = dispatcher,
				packetChannel = packetChannel,
				socket = socket,
				peer = peer,
				scope = connScope,
			)
			conn.startObserving()

			// Dedicated coroutine per connection so the reception loop is never blocked by packet processing
			connScope.launch {
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
