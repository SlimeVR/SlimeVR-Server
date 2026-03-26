package dev.slimevr.udp

import dev.slimevr.AppLogger
import dev.slimevr.EventDispatcher
import dev.slimevr.VRServer
import dev.slimevr.VRServerActions
import dev.slimevr.context.Context
import dev.slimevr.context.CustomBehaviour
import dev.slimevr.context.createContext
import dev.slimevr.device.Device
import dev.slimevr.device.DeviceActions
import dev.slimevr.device.DeviceOrigin
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import dev.slimevr.tracker.TrackerIdNum
import dev.slimevr.device.createDevice
import dev.slimevr.tracker.createTracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import solarxr_protocol.datatypes.TrackerStatus
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
typealias UDPConnectionBehaviour = CustomBehaviour<UDPConnectionState, UDPConnectionActions, UDPConnection>

private const val CONNECTION_TIMEOUT_MS = 5000L

val PacketBehaviour = UDPConnectionBehaviour(
	reducer = { s, a ->
		when (a) {
			is UDPConnectionActions.LastPacket -> {
				var newState = s.copy(lastPacket = a.time)

				if (a.packetNum != null) {
					newState = newState.copy(lastPacketNum = a.packetNum)
				}

				newState
			}

			else -> s
		}
	},
	observer = {
		it.packetEvents.onAny { packet ->
			val state = it.context.state.value

			val now = System.currentTimeMillis()
			if (now - state.lastPacket > CONNECTION_TIMEOUT_MS && packet.packetNumber == 0L) {
				it.context.dispatch(
					UDPConnectionActions.LastPacket(
						packetNum = 0,
						time = now,
					),
				)
				AppLogger.udp.info("Reconnecting")
			} else if (packet.packetNumber < state.lastPacketNum) {
				AppLogger.udp.warn("WARN: Received packet with wrong packet number")
				return@onAny
			} else {
				it.context.dispatch(UDPConnectionActions.LastPacket(time = now))
			}
		}
	},
)

val PingBehaviour = UDPConnectionBehaviour(
	reducer = { s, a ->
		when (a) {
			is UDPConnectionActions.StartPing -> {
				s.copy(lastPing = s.lastPing.copy(startTime = a.startTime))
			}

			is UDPConnectionActions.ReceivedPong -> {
				s.copy(lastPing = s.lastPing.copy(duration = a.duration, id = a.id))
			}

			else -> s
		}
	},
	observer = {
		// Send the ping every 1s
		it.context.scope.launch {
			while (isActive) {
				val state = it.context.state.value
				if (state.didHandshake) {
					it.context.dispatch(UDPConnectionActions.StartPing(startTime = System.currentTimeMillis()))
					it.send(PingPong(state.lastPing.id + 1))
				}
				delay(1000)
			}
		}

		// listen for the pong
		it.packetEvents.onPacket<PingPong> { packet ->
			val state = it.context.state.value
			val deviceId = state.deviceId ?: return@onPacket

			if (packet.data.pingId != state.lastPing.id + 1) {
				AppLogger.udp.warn("Ping ID does not match, ignoring ${packet.data.pingId} != ${state.lastPing.id + 1}")
				return@onPacket
			}

			val ping = System.currentTimeMillis() - state.lastPing.startTime

			val device = it.serverContext.getDevice(deviceId) ?: return@onPacket

			it.context.dispatch(
				UDPConnectionActions.ReceivedPong(
					id = packet.data.pingId,
					duration = ping,
				),
			)
			device.context.dispatch(
				DeviceActions.Update {
					copy(ping = ping)
				},
			)
		}
	},
)

val HandshakeBehaviour = UDPConnectionBehaviour(
	reducer = { s, a ->
		when (a) {
			is UDPConnectionActions.Handshake -> s.copy(
				didHandshake = true,
				deviceId = a.deviceId,
			)

			is UDPConnectionActions.Disconnected -> s.copy(
				didHandshake = false,
			)

			else -> s
		}
	},
	observer = {
		it.packetEvents.onPacket<Handshake> { packet ->
			val state = it.context.state.value

			val device = if (state.deviceId == null) {
				val deviceId = it.serverContext.nextHandle()
				val newDevice = createDevice(
					id = deviceId,
					scope = it.serverContext.context.scope,
					address = state.address,
					macAddress = packet.data.macString,
					origin = DeviceOrigin.UDP,
					protocolVersion = packet.data.protocolVersion,
					serverContext = it.serverContext,
				)
				it.serverContext.context.dispatch(VRServerActions.NewDevice(deviceId = deviceId, context = newDevice))
				it.context.dispatch(UDPConnectionActions.Handshake(deviceId))
				newDevice
			} else {
				it.context.dispatch(UDPConnectionActions.Handshake(state.deviceId))
				it.getDevice() ?: run {
					AppLogger.udp.warn("Reconnect handshake but device ${state.deviceId} not found")
					it.send(Handshake())
					return@onPacket
				}
			}

			// Apply handshake fields to device, always, for both first connect and reconnect
			device.context.dispatch(
				DeviceActions.Update {
					copy(
						macAddress = packet.data.macString ?: macAddress,
						boardType = packet.data.boardType,
						mcuType = packet.data.mcuType,
						firmware = packet.data.firmware ?: firmware,
						protocolVersion = packet.data.protocolVersion,
					)
				},
			)

			it.send(Handshake())
		}
	},
)

val TimeoutBehaviour = UDPConnectionBehaviour(
	observer = {
		it.context.scope.launch {
			while (isActive) {
				val state = it.context.state.value
				if (!state.didHandshake) {
					delay(500)
					continue
				}
				val timeUntilTimeout = CONNECTION_TIMEOUT_MS - (System.currentTimeMillis() - state.lastPacket)
				if (timeUntilTimeout <= 0) {
					AppLogger.udp.info("Connection timed out for ${state.id}")
					it.context.dispatch(UDPConnectionActions.Disconnected)
					it.getDevice()?.context?.dispatch(
						DeviceActions.Update { copy(status = TrackerStatus.DISCONNECTED) },
					)
				} else {
					delay(timeUntilTimeout + 1)
				}
			}
		}
	},
)

val DeviceStatsBehaviour = UDPConnectionBehaviour(
	observer = {
		it.packetEvents.onPacket<BatteryLevel> { event ->
			val device = it.getDevice() ?: return@onPacket

			device.context.dispatch(
				DeviceActions.Update {
					copy(
						batteryLevel = event.data.level,
						batteryVoltage = event.data.voltage,
					)
				},
			)
		}

		it.packetEvents.onPacket<SignalStrength> { event ->
			val device = it.getDevice() ?: return@onPacket

			device.context.dispatch(
				DeviceActions.Update {
					copy(signalStrength = event.data.signal)
				},
			)
		}
	},
)

val SensorInfoBehaviour = UDPConnectionBehaviour(
	reducer = { s, a ->
		when (a) {
			is UDPConnectionActions.AssignTracker -> {
				s.copy(trackerIds = s.trackerIds + a.trackerId)
			}

			else -> s
		}
	},
	observer = { observerContext ->
		observerContext.packetEvents.onPacket<SensorInfo> { event ->
			val device = observerContext.getDevice()
				?: error("invalid state - a device should exist at this point")

			device.context.dispatch(
				DeviceActions.Update {
					copy(status = event.data.status)
				},
			)

			val tracker = observerContext.getTracker(event.data.sensorId)

			val action = TrackerActions.Update {
				copy(
					sensorType = event.data.imuType,
				)
			}

			if (tracker != null) {
				tracker.context.dispatch(action)
			} else {
				val deviceState = device.context.state.value
				val trackerId = observerContext.serverContext.nextHandle()
				val newTracker = createTracker(
					id = trackerId,
					hardwareId = "${deviceState.address}:${event.data.sensorId}",
					sensorType = event.data.imuType,
					deviceId = deviceState.id,
					origin = DeviceOrigin.UDP,
					serverContext = observerContext.serverContext,
					scope = observerContext.serverContext.context.scope,
				)

				observerContext.serverContext.context.dispatch(
					VRServerActions.NewTracker(
						trackerId = trackerId,
						context = newTracker,
					),
				)
				observerContext.context.dispatch(
					UDPConnectionActions.AssignTracker(
						trackerId = TrackerIdNum(
							id = trackerId,
							trackerNum = event.data.sensorId,
						),
					),
				)
				newTracker.context.dispatch(action)
			}
		}
	},
)

val SensorRotationBehaviour = UDPConnectionBehaviour(
	observer = { context ->
		context.packetEvents.onPacket<RotationData> { event ->
			val tracker = context.getTracker(event.data.sensorId) ?: return@onPacket
			tracker.context.dispatch(
				TrackerActions.Update {
					copy(rawRotation = event.data.rotation)
				},
			)
		}
	},
)

data class UDPConnection(
	val context: UDPConnectionContext,
	val serverContext: VRServer,
	val packetEvents: UDPPacketDispatcher,
	val packetChannel: Channel<PacketEvent<UDPPacket>>,
	val send: (UDPPacket) -> Unit,
) {
	fun getDevice(): Device? {
		val deviceId = context.state.value.deviceId
		return if (deviceId != null) {
			serverContext.getDevice(deviceId)
		} else {
			null
		}
	}

	fun getTracker(id: Int): Tracker? {
		val trackerId = context.state.value.trackerIds.find { it.trackerNum == id }
		return if (trackerId != null) {
			serverContext.getTracker(trackerId.id)
		} else {
			null
		}
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

			val context = createContext(
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
				reducers = behaviours.map { it.reducer },
				scope = scope,
			)

			val dispatcher = EventDispatcher<PacketEvent<UDPPacket>> { it.data::class }
			val packetChannel = Channel<PacketEvent<UDPPacket>>(capacity = 256)
			val remoteInetAddress = InetAddress.getByName(remoteIp)

			val conn = UDPConnection(
				context = context,
				serverContext = serverContext,
				dispatcher,
				packetChannel = packetChannel,
				send = { packet: UDPPacket ->
					scope.launch(Dispatchers.IO) {
						val buf = Buffer()
						writePacket(buf, packet)
						val bytes = buf.readByteArray()
						socket.send(DatagramPacket(bytes, bytes.size, remoteInetAddress, remotePort))
					}
				},
			)

			behaviours.map { it.observer }.forEach { it?.invoke(conn) }

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
