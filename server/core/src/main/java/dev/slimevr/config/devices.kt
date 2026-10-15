package dev.slimevr.config

import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import solarxr_protocol.datatypes.BodyPart

private const val DEVICES_CONFIG_VERSION = 1
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
	return when {
		else -> json
	}
}

private fun parseAndMigrateDevicesConfig(raw: String): DevicesConfigState {
	val json = jsonConfig.parseToJsonElement(raw).jsonObject
	return jsonConfig.decodeFromJsonElement(migrateDevicesConfig(json))
}
