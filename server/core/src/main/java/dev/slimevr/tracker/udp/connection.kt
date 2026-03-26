package dev.slimevr.tracker.udp

import dev.llelievr.espflashkotlin.Packet
import dev.slimevr.AppLogger
import dev.slimevr.EventDispatcher
import dev.slimevr.VRServer
import dev.slimevr.VRServerActions
import dev.slimevr.context.Context
import dev.slimevr.context.CustomBehaviour
import dev.slimevr.context.createContext
import dev.slimevr.tracker.Device
import dev.slimevr.tracker.DeviceActions
import dev.slimevr.tracker.DeviceOrigin
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import dev.slimevr.tracker.TrackerIdNum
import dev.slimevr.tracker.createDevice
import dev.slimevr.tracker.createTracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
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
}

typealias UDPConnectionContext = Context<UDPConnectionState, UDPConnectionActions>
typealias UDPConnectionBehaviour = CustomBehaviour<UDPConnectionState, UDPConnectionActions, UDPConnection>

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
			if (now - state.lastPacket > 5000 && packet.packetNumber == 0L) {
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

			else -> s
		}
	},
	observer = {
		it.packetEvents.onPacket<Handshake> { packet ->
			val state = it.context.state.value

			if (state.deviceId == null) {
				val deviceId = it.serverContext.nextHandle()

				val newDevice = createDevice(
					id = deviceId,
					scope = it.serverContext.context.scope,
					address = it.context.state.value.address,
					macAddress = packet.data.macString,
					boardType = packet.data.boardType,
					protocolVersion = packet.data.protocolVersion,
					mcuType = packet.data.mcuType,
					firmware = packet.data.firmware,
					origin = DeviceOrigin.UDP,
					serverContext = it.serverContext,
				)

				it.serverContext.context.dispatch(
					VRServerActions.NewDevice(
						deviceId = deviceId,
						context = newDevice,
					),
				)
				it.context.dispatch(UDPConnectionActions.Handshake(deviceId))
				it.send(Handshake())
			} else {
				it.send(Handshake())
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
					status = event.data.status,
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
					dispatcher.emit(event)
				}
			}

			return conn
		}
	}
}
