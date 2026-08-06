package dev.slimevr.desktop.ipc

import com.squareup.wire.ProtoWriter
import dev.slimevr.AppContextProvider
import dev.slimevr.CURRENT_PLATFORM
import dev.slimevr.Platform
import dev.slimevr.desktop.platform.Battery
import dev.slimevr.desktop.platform.Position
import dev.slimevr.desktop.platform.ProtobufMessage
import dev.slimevr.desktop.platform.TrackerAdded
import dev.slimevr.desktop.platform.TrackerStatus
import dev.slimevr.desktop.platform.Version
import dev.slimevr.driver.DriverBridge
import dev.slimevr.driver.DriverBridgeInbound
import dev.slimevr.driver.DriverBridgeOutbound
import dev.slimevr.driver.DriverBridgeSource
import dev.slimevr.driver.TrackerRole
import dev.slimevr.driver.bodyPartToRole
import dev.slimevr.driver.roleToBodyPart
import dev.slimevr.logging.AppLogger
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okio.Buffer
import java.io.File
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.exists

val solarxrToProtoStatus = mapOf(
	solarxr_protocol.datatypes.TrackerStatus.OK to TrackerStatus.Status.OK,
	solarxr_protocol.datatypes.TrackerStatus.SLEEPING to TrackerStatus.Status.OK,
	solarxr_protocol.datatypes.TrackerStatus.ERROR to TrackerStatus.Status.ERROR,
	solarxr_protocol.datatypes.TrackerStatus.OCCLUDED to TrackerStatus.Status.OCCLUDED,
	solarxr_protocol.datatypes.TrackerStatus.TIMED_OUT to TrackerStatus.Status.DISCONNECTED,
	solarxr_protocol.datatypes.TrackerStatus.DISCONNECTED to TrackerStatus.Status.DISCONNECTED,
	solarxr_protocol.datatypes.TrackerStatus.BUSY to TrackerStatus.Status.BUSY,
)
val protoToSolarxrStatus = solarxrToProtoStatus.entries.associate { (k, v) -> v to k }

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
	for (path in path.split(File.pathSeparator)) {
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

	AppLogger.steamvr.info("Found bindings provider at $path")
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
	source: DriverBridgeSource,
	messages: Flow<ByteArray>,
	send: suspend (ByteArray) -> Unit,
) = coroutineScope {
	val sendMutex = Mutex()

	val encodeBuffer = Buffer()
	val encodeWriter = ProtoWriter(encodeBuffer)

	suspend fun sendMsg(msg: ProtobufMessage) = sendMutex.withLock {
		ProtobufMessage.ADAPTER.encode(encodeWriter, msg)
		send(encodeBuffer.readByteArray())
	}

	val bridge = DriverBridge.create(
		id = appContext.server.nextHandle(),
		source = source,
		appContext = appContext,
		scope = this,
	)

	bridge.outbound.on<DriverBridgeOutbound.TrackerAdded> { event ->
		val trackerRole = bodyPartToRole.getOrDefault(event.part, TrackerRole.NONE)
		sendMsg(
			ProtobufMessage(
				tracker_added = TrackerAdded(
					tracker_id = event.trackerId,
					tracker_serial = "human://$trackerRole",
					tracker_name = "SlimeVR $trackerRole Virtual Tracker",
					tracker_role = trackerRole.value.toInt(),
					manufacturer = "SlimeVR",
				),
			),
		)
	}.launchIn(this)

	bridge.outbound.on<DriverBridgeOutbound.TrackerPosition> { event ->
		sendMsg(
			ProtobufMessage(
				position = Position(
					tracker_id = event.trackerId,
					qx = event.rotation.x,
					qy = event.rotation.y,
					qz = event.rotation.z,
					qw = event.rotation.w,
					x = event.position?.x,
					y = event.position?.y,
					z = event.position?.z,
				),
			),
		)
	}.launchIn(this)

	bridge.outbound.on<DriverBridgeOutbound.TrackerStatus> { event ->
		sendMsg(
			ProtobufMessage(
				tracker_status = TrackerStatus(
					tracker_id = event.trackerId,
					status = solarxrToProtoStatus.getOrDefault(event.status, TrackerStatus.Status.ERROR),
				),
			),
		)

		sendMsg(
			ProtobufMessage(
				battery = Battery(
					tracker_id = event.trackerId,
					battery_level = event.battery ?: 0f,
					is_charging = event.charging,
				),
			),
		)
	}.launchIn(this)

	sendMsg(ProtobufMessage(version = Version(protocol_version = PROTOCOL_VERSION)))

	try {
		messages.collect { bytes ->
			val msg = ProtobufMessage.ADAPTER.decode(bytes)
			msg.version?.let { ver ->
				bridge.inbound.emit(DriverBridgeInbound.Version(ver.protocol_version))
				if (ver.protocol_version >= 2) {
					// FIXME: multiple launch could be created here bc nothing prevent protocol from changing or getting called again during runtime
					// causing a memory leak
					this@coroutineScope.launch {
						startBindingProvider()
					}
				}
			}
			msg.tracker_added?.let { ta ->
				bridge.inbound.emit(
					DriverBridgeInbound.TrackerAdded(
						id = ta.tracker_id,
						name = ta.tracker_name,
						manufacturer = ta.manufacturer.ifEmpty { "OpenVR" },
						serial = ta.tracker_serial,
						bodyPart = TrackerRole.fromValue(ta.tracker_role.toUByte())?.let { role -> roleToBodyPart[role] },
					),
				)
			}
			msg.tracker_status?.let { status ->
				bridge.inbound.emit(DriverBridgeInbound.TrackerStatus(id = status.tracker_id, status = protoToSolarxrStatus.getOrDefault(status.status, solarxr_protocol.datatypes.TrackerStatus.ERROR)))
			}
			msg.battery?.let { bat ->
				bridge.inbound.emit(
					DriverBridgeInbound.TrackerBattery(
						id = bat.tracker_id,
						batteryLevel = bat.battery_level * 100,
						charging = bat.is_charging,
					),
				)
			}
			msg.position?.let { pos ->
				bridge.inbound.emit(
					DriverBridgeInbound.TrackerPosition(
						id = pos.tracker_id,
						rotation = Quaternion(
							w = pos.qw,
							x = pos.qx,
							y = pos.qy,
							z = pos.qz,
						),
						position = if (pos.x != null && pos.y != null && pos.z != null) {
							Vector3(
								pos.x,
								pos.y,
								pos.z,
							)
						} else {
							null
						},
					),
				)
			}
		}
	} finally {
		bridge.disconnect()
		// The outbound listeners are launched into this scope and never complete on their own
		// (they observe a SharedFlow), so coroutineScope would wait on them forever and the
		// connection would never be released for the next client -- e.g. a SteamVR restart
		coroutineContext.cancelChildren()
	}
}
