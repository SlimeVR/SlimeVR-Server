package dev.slimevr.desktop.ipc

import dev.slimevr.AppContextProvider
import dev.slimevr.desktop.platform.Position
import dev.slimevr.desktop.platform.ProtobufMessage
import dev.slimevr.desktop.platform.TrackerAdded
import dev.slimevr.desktop.platform.Version
import dev.slimevr.driver.DriverBridge
import dev.slimevr.driver.DriverBridgeInbound
import dev.slimevr.driver.DriverBridgeOutbound
import dev.slimevr.feeder.FeederBridge
import dev.slimevr.feeder.FeederBridgeInbound
import io.github.axisangles.ktmath.Quaternion
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

const val PROTOCOL_VERSION = 5

suspend fun handleDriverConnection(
	appContext: AppContextProvider,
	messages: Flow<ByteArray>,
	send: suspend (ByteArray) -> Unit,
) = coroutineScope {
	val sendMutex = Mutex()

	suspend fun sendMsg(msg: ProtobufMessage) = sendMutex.withLock {
		send(ProtobufMessage.ADAPTER.encode(msg))
	}

	val bridge = DriverBridge.create(id = appContext.server.nextHandle(), appContext = appContext, scope = this)

	bridge.outbound.on<DriverBridgeOutbound.TrackerAdded> { event ->
		sendMsg(ProtobufMessage(tracker_added = TrackerAdded(tracker_id = event.trackerId, tracker_serial = event.serial, tracker_name = event.name)))
	}
	bridge.outbound.on<DriverBridgeOutbound.TrackerPosition> { event ->
		sendMsg(ProtobufMessage(position = Position(tracker_id = event.trackerId, qx = event.rotation.x, qy = event.rotation.y, qz = event.rotation.z, qw = event.rotation.w)))
	}

	sendMsg(ProtobufMessage(version = Version(protocol_version = PROTOCOL_VERSION)))

	try {
		messages.collect { bytes ->
			val msg = ProtobufMessage.ADAPTER.decode(bytes)
			msg.version?.let { ver ->
				bridge.inbound.emit(DriverBridgeInbound.Version(ver.protocol_version))
			}
			msg.position?.let { pos ->
				bridge.inbound.emit(DriverBridgeInbound.TrackerPosition(trackerId = pos.tracker_id, rotation = Quaternion(w = pos.qw, x = pos.qx, y = pos.qy, z = pos.qz), position = null))
			}
		}
	} finally {
		bridge.disconnect()
	}
}

suspend fun handleFeederConnection(
	appContext: AppContextProvider,
	messages: Flow<ByteArray>,
	send: suspend (ByteArray) -> Unit,
) = coroutineScope {
	val bridge = FeederBridge.create(id = appContext.server.nextHandle(), appContext = appContext, scope = this)

	send(ProtobufMessage.ADAPTER.encode(ProtobufMessage(version = Version(protocol_version = PROTOCOL_VERSION))))

	try {
		messages.collect { bytes ->
			val msg = ProtobufMessage.ADAPTER.decode(bytes)
			msg.version?.let { ver ->
				bridge.inbound.emit(FeederBridgeInbound.Version(protocolVersion = ver.protocol_version, firmware = ver.toString()))
			}
			msg.tracker_added?.let { ta ->
				bridge.inbound.emit(FeederBridgeInbound.TrackerAdded(serial = ta.tracker_serial))
			}
			msg.position?.let { pos ->
				bridge.inbound.emit(FeederBridgeInbound.TrackerPosition(trackerId = pos.tracker_id, rotation = Quaternion(w = pos.qw, x = pos.qx, y = pos.qy, z = pos.qz), position = null))
			}
		}
	} finally {
		bridge.disconnect()
	}
}
