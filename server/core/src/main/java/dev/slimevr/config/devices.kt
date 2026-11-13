package dev.slimevr.config

import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.tracker.driverTrackerHardwareId
import dev.slimevr.tracker.hidTrackerHardwareId
import dev.slimevr.tracker.udpTrackerHardwareId
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import solarxr_protocol.datatypes.BodyPart

private const val DEVICES_CONFIG_VERSION = 2
const val DEVICES_CONFIG_PATH = "devices.json"

@Serializable
data class TrackerConfig(
	@Serializable(with = BodyPartSerializer::class)
	val bodyPart: BodyPart? = null,
	val customName: String? = null,
	@Serializable(with = QuaternionSerializer::class)
	val mountingOrientation: Quaternion = Quaternion.IDENTITY,
	@Serializable(with = QuaternionSerializer::class)
	val mountingResetOrientation: Quaternion? = null,
	val magEnabled: Boolean? = null,
	val boneOffsets: Map<BodyPart, Vector3> = emptyMap(),
)

@Serializable
data class DongleConfig(
	val customName: String? = null,
)

@Serializable
data class DevicesConfigState(
	val version: Int = DEVICES_CONFIG_VERSION,
	val allowedUdpDevices: Set<String> = emptySet(),
	val trackers: Map<String, TrackerConfig> = emptyMap(),
	val dongles: Map<String, DongleConfig> = emptyMap(),
)

sealed interface DevicesConfigActions {
	data class Update(val transform: DevicesConfigState.() -> DevicesConfigState) : DevicesConfigActions
	data class UpdateTracker(val hardwareId: String, val transform: TrackerConfig.() -> TrackerConfig) : DevicesConfigActions
	data class UpdateDongle(val serialNumber: String, val transform: DongleConfig.() -> DongleConfig) : DevicesConfigActions
	data class AddAllowedUdpDevice(val mac: String) : DevicesConfigActions
	data class RemoveAllowedUdpDevice(val mac: String) : DevicesConfigActions
}

typealias DevicesConfigContext = Context<DevicesConfigState, DevicesConfigActions>
typealias DevicesConfigBehaviour = Behaviour<DevicesConfig>

class DevicesConfig(
	val context: DevicesConfigContext,
	private val autosave: Autosave,
) {
	fun startObserving() = context.observeAll(this)

	suspend fun flush() = autosave.flush()

	companion object {
		suspend fun create(scope: CoroutineScope, storage: ConfigStorage): DevicesConfig {
			val initialState = loadFileWithBackup(storage, DEVICES_CONFIG_PATH, DevicesConfigState()) {
				parseAndMigrateDevicesConfig(it)
			}
			val context = Context.create(
				initialState = initialState,
				scope = scope,
				reducer = ::reduce,
				name = "DevicesConfig",
			)
			val autosave = launchAutosave(
				scope = scope,
				state = context.state,
				storage = storage,
				toPath = { DEVICES_CONFIG_PATH },
				serialize = { jsonConfig.encodeToString(it) },
			)
			val devices = DevicesConfig(context, autosave)
			devices.startObserving()
			return devices
		}
	}
}

private fun migrateDevicesConfig(json: JsonObject): JsonObject {
	val version = json["version"]?.jsonPrimitive?.intOrNull ?: 0
	if (version >= DEVICES_CONFIG_VERSION) return json
	val migrated = (version + 1..DEVICES_CONFIG_VERSION).fold(json) { current, to -> migrateDevicesConfigTo(to, current) }
	return JsonObject(migrated + ("version" to JsonPrimitive(DEVICES_CONFIG_VERSION)))
}

/** Brings a devices config up to version [to] from the one before. */
private fun migrateDevicesConfigTo(to: Int, json: JsonObject): JsonObject = when (to) {
	1 -> json
	2 -> migrateDevicesConfigV2(json)
	else -> error("No devices config migration to version $to")
}

/** Version 1 keyed trackers by their bare hardware id, version 2 prefixes it with the tracker origin. */
private fun migrateDevicesConfigV2(json: JsonObject): JsonObject {
	val udpTrackerId = Regex("((?:[0-9A-F]{2}:){5}[0-9A-F]{2}|\\d{1,3}(?:\\.\\d{1,3}){3}):(\\d{1,9})")
	val hidTrackerId = Regex("[0-9A-F]{12}|[0-9A-F]{16}")

	fun prefixed(id: String): String {
		if (id.startsWith("vmc:") || id.startsWith("vrcosc:")) return id
		udpTrackerId.matchEntire(id)?.let { return udpTrackerHardwareId(it.groupValues[1], it.groupValues[2].toInt()) }
		if (hidTrackerId.matches(id)) return hidTrackerHardwareId(id)
		return driverTrackerHardwareId(id)
	}

	val trackers = json["trackers"] as? JsonObject ?: return json
	return JsonObject(json + ("trackers" to JsonObject(trackers.mapKeys { (id, _) -> prefixed(id) })))
}

private fun parseAndMigrateDevicesConfig(raw: String): DevicesConfigState {
	val json = jsonConfig.parseToJsonElement(raw).jsonObject
	return jsonConfig.decodeFromJsonElement(migrateDevicesConfig(json))
}
