package dev.slimevr.config

import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.skeleton.computeAllDefaultProportionsByBone
import dev.slimevr.skeleton.computeDefaultProportionsByBone
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import solarxr_protocol.rpc.StayAlignedRelaxedPose

private const val USER_CONFIG_VERSION = 1
private const val DEFAULT_USER_HEIGHT = 1.6f

/** A relaxed pose of the user, used by Stay Aligned to correct moving trackers. */
@Serializable
data class StayAlignedPoseConfig(
	/** The user recorded this pose. */
	val recorded: Boolean = false,
	/** Whether Stay Aligned corrects moving trackers with this pose. */
	val enabled: Boolean = false,
	/** Angle between the upper leg yaw and the centre yaw. */
	val upperLegAngleInDeg: Float = 0.0f,
	/** Angle between the lower leg yaw and the centre yaw. */
	val lowerLegAngleInDeg: Float = 0.0f,
	/** Angle between the foot yaw and the centre yaw. */
	val footAngleInDeg: Float = 0.0f,
)

@Serializable
data class UserConfigData(
	val userHeight: Float = DEFAULT_USER_HEIGHT,
	val proportions: Map<String, Float> = computeAllDefaultProportionsByBone(DEFAULT_USER_HEIGHT),
	/** Only the poses that differ from their default are stored. */
	val stayAlignedPoses: Map<StayAlignedRelaxedPose, StayAlignedPoseConfig> = emptyMap(),
	val version: Int = USER_CONFIG_VERSION,
)

private fun migrateUserConfig(json: JsonObject): JsonObject {
	val version = json["version"]?.jsonPrimitive?.intOrNull ?: 0
	return when {
		// add migration branches here as: version < N -> migrateUserConfig(...)
		else -> json
	}
}

private fun parseAndMigrateUserConfig(raw: String): UserConfigData {
	val json = jsonConfig.parseToJsonElement(raw).jsonObject
	val data = jsonConfig.decodeFromJsonElement<UserConfigData>(migrateUserConfig(json))
	// Bones missing from the file get the defaults for its height, not the ones for the default height
	val stored = if ("proportions" in json) data.proportions else emptyMap()
	return data.copy(proportions = computeAllDefaultProportionsByBone(data.userHeight) + stored)
}

/** Scales the height-driven bones to [height] and keeps the other bones as they are. */
fun withUserHeight(data: UserConfigData, height: Float): UserConfigData = data.copy(
	userHeight = height,
	proportions = data.proportions + computeDefaultProportionsByBone(height),
)

/** Every bone back to its default for the current height. */
fun withDefaultProportions(data: UserConfigData): UserConfigData = data.copy(proportions = computeAllDefaultProportionsByBone(data.userHeight))

data class UserConfigState(
	val data: UserConfigData,
	val name: String,
)

sealed interface UserConfigActions {
	data class Update(val transform: UserConfigData.() -> UserConfigData) : UserConfigActions
	data class LoadProfile(val newState: UserConfigState) : UserConfigActions
	data class Rename(val name: String) : UserConfigActions
}

typealias UserConfigContext = Context<UserConfigState, UserConfigActions>
typealias UserConfigBehaviour = Behaviour<UserConfig>

class UserConfig(
	val context: UserConfigContext,
	private val scope: CoroutineScope,
	private val storage: ConfigStorage,
	private val userConfigDir: String,
) {
	private var autosave: Autosave = startAutosave()

	private fun startAutosave() = launchAutosave(
		scope = scope,
		state = context.state,
		storage = storage,
		toPath = { state -> configPath(userConfigDir, "${state.name}.json") },
		serialize = { state -> jsonConfig.encodeToString(state.data) },
	)

	suspend fun flush() = autosave.flush()

	suspend fun rename(newName: String) {
		autosave.stop()
		try {
			storage.move(configPath(userConfigDir, profileFileName(context.state.value.name)), configPath(userConfigDir, profileFileName(newName)))
			context.dispatch(UserConfigActions.Rename(newName))
		} finally {
			autosave = startAutosave()
		}
	}

	suspend fun swap(newName: String) {
		autosave.stop()

		val newData = loadFileWithBackup(storage, configPath(userConfigDir, "$newName.json"), UserConfigData()) {
			parseAndMigrateUserConfig(it)
		}
		val newState = UserConfigState(name = newName, data = newData)
		context.dispatch(UserConfigActions.LoadProfile(newState))

		autosave = startAutosave()
	}

	companion object {
		suspend fun create(scope: CoroutineScope, storage: ConfigStorage, name: String): UserConfig {
			val userConfigDir = "user"

			val initialData = loadFileWithBackup(storage, configPath(userConfigDir, "$name.json"), UserConfigData()) {
				parseAndMigrateUserConfig(it)
			}
			val initialState = UserConfigState(name = name, data = initialData)

			val context = Context.create(
				initialState = initialState,
				scope = scope,
				reducer = ::reduce,
				name = "UserConfig[$name]",
			)
			val userConfig = UserConfig(context, scope = scope, storage = storage, userConfigDir = userConfigDir)
			context.observeAll(userConfig)
			return userConfig
		}
	}
}
