package dev.slimevr.desktop.ipc

import dev.slimevr.VRServer
import dev.slimevr.VRServerActions
import dev.slimevr.desktop.platform.Position
import dev.slimevr.desktop.platform.ProtobufMessage
import dev.slimevr.desktop.platform.TrackerAdded
import dev.slimevr.desktop.platform.Version
import dev.slimevr.solarxr.createSolarXRConnection
import dev.slimevr.solarxr.onSolarXRMessage
import dev.slimevr.tracker.DeviceOrigin
import dev.slimevr.tracker.TrackerActions
import dev.slimevr.tracker.createDevice
import dev.slimevr.tracker.createTracker
import io.github.axisangles.ktmath.Quaternion
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import solarxr_protocol.datatypes.hardware_info.ImuType
import java.nio.ByteBuffer

const val PROTOCOL_VERSION = 5

suspend fun handleDriverConnection(
	server: VRServer,
	messages: Flow<ByteArray>,
	send: suspend (ByteArray) -> Unit,
) = coroutineScope {
	val sendMutex = Mutex()

	suspend fun sendMsg(msg: ProtobufMessage) = sendMutex.withLock {
		send(ProtobufMessage.ADAPTER.encode(msg))
	}

	sendMsg(ProtobufMessage(version = Version(protocol_version = PROTOCOL_VERSION)))

	val subscribedTrackers = mutableSetOf<Int>()

	val observerJob = launch {
		server.context.state.collect { state ->
			state.trackers.values.forEach { tracker ->
				val trackerState = tracker.context.state.value
				if (trackerState.origin == DeviceOrigin.DRIVER) return@forEach
				if (subscribedTrackers.add(trackerState.id)) {
					sendMsg(
						ProtobufMessage(
							tracker_added = TrackerAdded(
								tracker_id = trackerState.id,
								tracker_serial = trackerState.hardwareId,
								tracker_name = trackerState.customName ?: trackerState.name,
							),
						),
					)
					launch {
						tracker.context.state.collect { ts ->
							sendMsg(
								ProtobufMessage(
									position = Position(
										tracker_id = ts.id,
										qx = ts.rawRotation.x,
										qy = ts.rawRotation.y,
										qz = ts.rawRotation.z,
										qw = ts.rawRotation.w,
									),
								),
							)
						}
					}
				}
			}
		}
	}

	try {
		messages.collect { bytes ->
			val msg = ProtobufMessage.ADAPTER.decode(bytes)
			msg.user_action?.let {
				// TODO: dispatch user actions (reset, etc.) to VRServer
			}
			msg.version?.let {
				// TODO: store remote protocol version if needed
			}
		}
	} finally {
		observerJob.cancel()
	}
}

suspend fun handleFeederConnection(
	server: VRServer,
	messages: Flow<ByteArray>,
	send: suspend (ByteArray) -> Unit,
) = coroutineScope {
	send(ProtobufMessage.ADAPTER.encode(ProtobufMessage(version = Version(protocol_version = PROTOCOL_VERSION))))

	messages.collect { bytes ->
		val msg = ProtobufMessage.ADAPTER.decode(bytes)

		if (msg.tracker_added != null) {
			val deviceId = server.nextHandle()
			val device = createDevice(
				scope = this,
				id = deviceId,
				address = msg.tracker_added.tracker_serial,
				origin = DeviceOrigin.FEEDER,
				serverContext = server,
			)
			server.context.dispatch(VRServerActions.NewDevice(deviceId, device))

			val trackerId = server.nextHandle()
			val tracker = createTracker(
				scope = this,
				id = trackerId,
				deviceId = deviceId,
				sensorType = ImuType.MPU9250,
				hardwareId = msg.tracker_added.tracker_serial,
				origin = DeviceOrigin.FEEDER,
				serverContext = server,
			)
			server.context.dispatch(VRServerActions.NewTracker(trackerId, tracker))
		}

		if (msg.position != null) {
			server.getTracker(msg.position.tracker_id)?.context?.dispatch(
				TrackerActions.Update {
					copy(
						rawRotation = Quaternion(
							w = msg.position.qw,
							x = msg.position.qx,
							y = msg.position.qy,
							z = msg.position.qz,
						),
					)
				},
			)
		}
	}
}

suspend fun handleSolarXRConnection(
	server: VRServer,
	messages: Flow<ByteArray>,
	send: suspend (ByteArray) -> Unit,
) = coroutineScope {
	val connection = createSolarXRConnection(
		serverContext = server,
		scope = this,
		onSend = send,
	)

	messages.collect { bytes ->
		onSolarXRMessage(ByteBuffer.wrap(bytes), connection)
	}
}