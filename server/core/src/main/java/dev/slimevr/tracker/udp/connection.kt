package dev.slimevr.tracker.udp

import dev.slimevr.AppLogger
import dev.slimevr.VRServer
import dev.slimevr.VRServerActions
import dev.slimevr.context.Context
import dev.slimevr.context.CustomModule
import dev.slimevr.context.createContext
import dev.slimevr.solarxr.SolarXRConnection
import dev.slimevr.solarxr.SolarXRConnectionActions
import dev.slimevr.solarxr.SolarXRConnectionState
import dev.slimevr.tracker.Device
import dev.slimevr.tracker.DeviceActions
import dev.slimevr.tracker.DeviceOrigin
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import dev.slimevr.tracker.TrackerIdNum
import dev.slimevr.tracker.createDevice
import dev.slimevr.tracker.createTracker
import io.ktor.network.sockets.BoundDatagramSocket
import io.ktor.network.sockets.Datagram
import io.ktor.network.sockets.InetSocketAddress
import io.ktor.utils.io.core.buildPacket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

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
	val trackerIds: List<TrackerIdNum>
)

sealed interface UDPConnectionActions {
	data class StartPing(val startTime: Long) : UDPConnectionActions
	data class ReceivedPong(val id: Int, val duration: Long) : UDPConnectionActions
	data class Handshake(val deviceId: Int) : UDPConnectionActions
	data class LastPacket(val packetNum: Long? = null, val time: Long) :
		UDPConnectionActions
	data class AssignTracker(val trackerId: TrackerIdNum) : UDPConnectionActions
}

typealias UDPConnectionContext = Context<UDPConnectionState, UDPConnectionActions>
typealias UDPConnectionModule = CustomModule<UDPConnectionState, UDPConnectionActions, UDPConnection>


data class UDPConnection(
	val context: UDPConnectionContext,
	val serverContext: VRServer,
	val packetEvents: PacketDispatcher,
	val send: (UDPPacket) -> Unit,
	val getDevice: () -> Device?,
	val getTracker: (sensorId: Int) -> Tracker?,
)

val PacketModule = UDPConnectionModule(
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
						time = now
					)
				)
				AppLogger.udp.info("Reconnecting")
			} else if (packet.packetNumber < state.lastPacketNum) {
				AppLogger.udp.warn("WARN: Received packet with wrong packet number")
				return@onAny
			} else {
				it.context.scope.launch {
					it.context.dispatch(UDPConnectionActions.LastPacket(time = now))
				}
			}
		}
	},
)

val PingModule = UDPConnectionModule(
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
		it.packetEvents.on<PingPong> { packet ->
			val state = it.context.state.value
			val deviceId = state.deviceId ?: return@on

			if (packet.data.pingId != state.lastPing.id + 1) {
				AppLogger.udp.warn("Ping ID does not match, ignoring ${packet.data.pingId} != ${state.lastPing.id + 1}")
				return@on
			}

			val ping = System.currentTimeMillis() - state.lastPing.startTime

			val device = it.serverContext.getDevice(deviceId) ?: return@on

			it.context.dispatch(
				UDPConnectionActions.ReceivedPong(
					id = packet.data.pingId,
					duration = ping
				)
			)
			device.context.dispatch(DeviceActions.Update {
				copy(ping = ping)
			})
		}
	},
)

val HandshakeModule = UDPConnectionModule(
	reducer = { s, a ->
		when (a) {
			is UDPConnectionActions.Handshake -> s.copy(
				didHandshake = true,
				deviceId = a.deviceId
			)
			else -> s
		}
	},
	observer = {
		it.packetEvents.on<Handshake> { packet ->
			val state = it.context.state.value

			if (state.deviceId == null) {
				val deviceId = it.serverContext.nextHandle()

				val newDevice = createDevice(
					id = deviceId,
					scope = it.serverContext.context.scope,
					address = packet.data.macString ?: error("no mac address?"),
					origin = DeviceOrigin.UDP,
					serverContext = it.serverContext,
				)

				it.serverContext.context.dispatch(
					VRServerActions.NewDevice(
						deviceId = deviceId,
						context = newDevice
					)
				)
				it.context.dispatch(UDPConnectionActions.Handshake(deviceId))
				it.send(Handshake())
			} else {
				it.send(Handshake())
			}
		}
	},
)

val DeviceStatsModule = UDPConnectionModule(
	observer = {
		it.packetEvents.on<BatteryLevel> { event ->
			val device = it.getDevice() ?: return@on

			device.context.dispatch(DeviceActions.Update {
				copy(
					batteryLevel = event.data.level,
					batteryVoltage = event.data.voltage
				)
			})
		}

		it.packetEvents.on<SignalStrength> { event ->
			val device = it.getDevice() ?: return@on

			device.context.dispatch(DeviceActions.Update {
				copy(signalStrength = event.data.signal)
			})
		}
	}
)

val SensorInfoModule = UDPConnectionModule(
	reducer = { s, a ->
		when (a) {
			is UDPConnectionActions.AssignTracker -> {
				s.copy(trackerIds = s.trackerIds + a.trackerId)
			}

			else -> s
		}
	},
	observer = { observerContext ->
		observerContext.packetEvents.on<SensorInfo> { event ->
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

				val device = observerContext.getDevice()
					?: error("invalid state - a device should exist at this point")
				val deviceState = device.context.state.value
				val trackerId = observerContext.serverContext.nextHandle()
				val newTracker = createTracker(
					id = trackerId,
					hardwareId = "${deviceState.address}:${event.data.sensorId}",
					sensorType = event.data.imuType,
					deviceId = deviceState.id,
					origin = DeviceOrigin.UDP,
					serverContext = observerContext.serverContext,
					scope = observerContext.serverContext.context.scope
				)

				observerContext.serverContext.context.dispatch(
					VRServerActions.NewTracker(
						trackerId = trackerId,
						context = newTracker
					)
				)
				observerContext.context.dispatch(
					UDPConnectionActions.AssignTracker(
						trackerId = TrackerIdNum(
							id = trackerId,
							trackerNum = event.data.sensorId
						)
					)
				)
				newTracker.context.dispatch(action)
			}

		}
	}
)

val SensorRotationModule = UDPConnectionModule(
	observer = { context ->
		context.packetEvents.on<RotationData> { event ->
			val tracker = context.getTracker(event.data.sensorId) ?: return@on
			tracker.context.scope.launch {
				tracker.context.dispatch(
					TrackerActions.Update {
						copy(rawRotation = event.data.rotation)
					}
				)
			}
		}
	}
)

fun createUDPConnectionContext(
	id: String,
	socket: BoundDatagramSocket,
	remoteAddress: InetSocketAddress,
	serverContext: VRServer,
	scope: CoroutineScope,
): UDPConnection {
	val modules = listOf(
		PacketModule,
		HandshakeModule,
		PingModule,
		DeviceStatsModule,
		SensorInfoModule,
		SensorRotationModule
	)

	val context = createContext(
		initialState = UDPConnectionState(
			id = id,
			lastPacket = System.currentTimeMillis(),
			lastPacketNum = 0,
			lastPing = LastPing(id = 0, duration = 0, startTime = 0),
			didHandshake = false,
			address = remoteAddress.hostname,
			port = remoteAddress.port,
			deviceId = null,
			trackerIds = listOf()
		),
		reducers = modules.map { it.reducer },
		scope = scope,
	)

	val dispatcher = PacketDispatcher()

	val conn = UDPConnection(
		context = context,
		serverContext = serverContext,
		dispatcher,
		send = { packet: UDPPacket ->
			scope.launch {
				val bytePacket = buildPacket {
					writePacket(this, packet)
				}
				socket.send(Datagram(bytePacket, remoteAddress))
			}
		},
		getDevice = {
			val deviceId = context.state.value.deviceId
			if (deviceId != null) serverContext.getDevice(deviceId)
			else null
		},
		getTracker = { id ->
			val trackerId = context.state.value.trackerIds.find { it.trackerNum == id }
			if (trackerId != null) serverContext.getTracker(trackerId.id)
			else null
		}
	)

	modules.map { it.observer }.forEach { it?.invoke(conn) }

	return conn
}
