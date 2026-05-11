package dev.slimevr.desktop.ipc

import dev.slimevr.AppContextProvider
import dev.slimevr.AppLogger
import dev.slimevr.CURRENT_PLATFORM
import dev.slimevr.Platform
import dev.slimevr.desktop.platform.Position
import dev.slimevr.desktop.platform.ProtobufMessage
import dev.slimevr.desktop.platform.TrackerAdded
import dev.slimevr.desktop.platform.Version
import dev.slimevr.driver.DriverBridge
import dev.slimevr.driver.DriverBridgeInbound
import dev.slimevr.driver.DriverBridgeOutbound
import io.github.axisangles.ktmath.Quaternion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.exists

const val PROTOCOL_VERSION = 2

private fun getBindingsProviderPath(): Path? {
	val executableName = when (CURRENT_PLATFORM) {
		Platform.WINDOWS -> "SlimeVR-Bindings-Provider.exe"
		Platform.LINUX -> "slimevr-bindings-provider"
		else -> return null
	}

	// First we want to try to find it in the working directory, its location on
	// Steam/Windows/portable.
	val workingDir = System.getProperty("user.dir")
	val binaryPath = Path(workingDir, executableName)
	if (binaryPath.exists()) return binaryPath

	// Then look through PATH to find the binary.
	// PATH shouldn't be null, but if it is just gracefully fail
	val path = System.getenv("PATH") ?: return null
	val separator = System.getProperty("path.separator")
	for (path in path.split(separator)) {
		val binaryPath = Path(path, executableName)
		if (binaryPath.exists()) return binaryPath
	}

	// :(
	return null
}

suspend fun startBindingProvider() = withContext(Dispatchers.IO) {
	val path = getBindingsProviderPath()
	if (path == null) {
		AppLogger.steamvr.warn("Failed to find bindings provider")
		return@withContext
	}

	val proc = try {
		ProcessBuilder(path.toString()).start()
	} catch (e: Exception) {
		AppLogger.steamvr.error(e, "Failed to start bindings provider")
		return@withContext
	}
	AppLogger.steamvr.info("Started bindings provider (PID ${proc.pid()})")
	proc.waitFor()

	AppLogger.steamvr.info("Bindings provider exited with code ${proc.exitValue()}")
}

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
				if (ver.protocol_version >= 2) {
					launch {
						startBindingProvider()
					}
				}
			}
			msg.tracker_added?.let { ta ->
				bridge.inbound.emit(DriverBridgeInbound.TrackerAdded(id = ta.tracker_id, name = ta.tracker_name, manufacturer = ta.manufacturer.ifEmpty { "SlimeVR" }, serial = ta.tracker_serial))
			}
			msg.battery?.let { bat ->
				bridge.inbound.emit(DriverBridgeInbound.TrackerBattery(id = bat.tracker_id, batteryLevel = bat.battery_level, charging = bat.is_charging))
			}
			msg.position?.let { pos ->
				bridge.inbound.emit(DriverBridgeInbound.TrackerPosition(trackerId = pos.tracker_id, rotation = Quaternion(w = pos.qw, x = pos.qx, y = pos.qy, z = pos.qz), position = null))
			}
		}
	} finally {
		bridge.disconnect()
	}
}
