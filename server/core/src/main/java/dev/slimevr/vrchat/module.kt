package dev.slimevr.vrchat

import dev.slimevr.VRServer
import dev.slimevr.config.AppConfig
import dev.slimevr.config.SettingsActions
import dev.slimevr.context.BasicBehaviour
import dev.slimevr.context.Context
import dev.slimevr.context.CustomBehaviour
import dev.slimevr.context.createContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.VRCAvatarMeasurementType
import solarxr_protocol.rpc.VRCConfigRecommendedValues
import solarxr_protocol.rpc.VRCConfigValidity
import solarxr_protocol.rpc.VRCConfigValues
import solarxr_protocol.rpc.VRCSpineMode
import solarxr_protocol.rpc.VRCTrackerModel
import kotlin.math.abs

val VRC_VALID_KEYS = setOf(
	"legacyModeOk",
	"shoulderTrackingOk",
	"shoulderWidthCompensationOk",
	"userHeightOk",
	"calibrationRangeOk",
	"calibrationVisualsOk",
	"trackerModelOk",
	"spineModeOk",
	"avatarMeasurementTypeOk",
)

data class VRCConfigState(
	val currentValues: VRCConfigValues?,
	val isSupported: Boolean,
	val mutedWarnings: List<String>,
)

sealed interface VRCConfigActions {
	data class UpdateValues(val values: VRCConfigValues?) : VRCConfigActions
	data class ToggleMutedWarning(val key: String) : VRCConfigActions
}

typealias VRCConfigContext = Context<VRCConfigState, VRCConfigActions>
typealias VRCConfigBehaviour = CustomBehaviour<VRCConfigState, VRCConfigActions, VRCConfigManager>

data class VRCConfigManager(
	val context: VRCConfigContext,
	val config: AppConfig,
	val userHeight: () -> Double,
)

val DefaultVRCConfigBehaviour = VRCConfigBehaviour(
	reducer = { s, a ->
		when (a) {
			is VRCConfigActions.UpdateValues -> s.copy(currentValues = a.values)
			is VRCConfigActions.ToggleMutedWarning -> {
				if (a.key !in VRC_VALID_KEYS) s
				else if (a.key in s.mutedWarnings) s.copy(mutedWarnings = s.mutedWarnings - a.key)
				else s.copy(mutedWarnings = s.mutedWarnings + a.key)
			}
		}
	},
	observer = { context ->

		context.context.state.map { it.mutedWarnings }.distinctUntilChanged().onEach { warnings ->
			context.config.settings.context.dispatch(SettingsActions.Update {
				copy(mutedVRCWarnings = warnings)
			})
		}.launchIn(scope = context.context.scope)
	}
)

fun computeRecommendedValues(server: VRServer, userHeight: Double): VRCConfigRecommendedValues {
	val trackers = server.context.state.value.trackers.values

	fun hasTracker(bodyPart: BodyPart) = trackers.any { it.context.state.value.bodyPart == bodyPart }

	val hasLeftHandWithPosition = hasTracker(BodyPart.LEFT_HAND)
	val hasRightHandWithPosition = hasTracker(BodyPart.RIGHT_HAND)

	val isMissingAnArmTracker = !hasTracker(BodyPart.LEFT_LOWER_ARM) ||
		!hasTracker(BodyPart.RIGHT_LOWER_ARM) ||
		!hasTracker(BodyPart.LEFT_UPPER_ARM) ||
		!hasTracker(BodyPart.RIGHT_UPPER_ARM)
	val isMissingAShoulderTracker = !hasTracker(BodyPart.LEFT_SHOULDER) ||
		!hasTracker(BodyPart.RIGHT_SHOULDER)

	return VRCConfigRecommendedValues(
		legacyMode = false,
		shoulderTrackingDisabled =
			(!hasLeftHandWithPosition || !hasRightHandWithPosition || isMissingAnArmTracker) &&
				((hasLeftHandWithPosition && hasRightHandWithPosition) || isMissingAShoulderTracker),
		userHeight = userHeight.toFloat(),
		calibrationRange = 0.2f,
		trackerModel = VRCTrackerModel.AXIS,
		spineMode = listOf(VRCSpineMode.LOCK_HIP, VRCSpineMode.LOCK_HEAD),
		calibrationVisuals = true,
		avatarMeasurementType = VRCAvatarMeasurementType.HEIGHT,
		shoulderWidthCompensation = true,
	)
}

fun computeValidity(values: VRCConfigValues, recommended: VRCConfigRecommendedValues): VRCConfigValidity =
	VRCConfigValidity(
		legacyModeOk = values.legacyMode == recommended.legacyMode,
		shoulderTrackingOk = values.shoulderTrackingDisabled == recommended.shoulderTrackingDisabled,
		spineModeOk = recommended.spineMode?.contains(values.spineMode) == true,
		trackerModelOk = values.trackerModel == recommended.trackerModel,
		calibrationRangeOk = abs(values.calibrationRange - recommended.calibrationRange) < 0.1f,
		userHeightOk = abs(recommended.userHeight - values.userHeight) < 0.1f,
		calibrationVisualsOk = values.calibrationVisuals == recommended.calibrationVisuals,
		avatarMeasurementTypeOk = values.avatarMeasurementType == recommended.avatarMeasurementType,
		shoulderWidthCompensationOk = values.shoulderWidthCompensation == recommended.shoulderWidthCompensation,
	)

fun createVRCConfigManager(
	config: AppConfig,
	scope: CoroutineScope,
	userHeight: () -> Double,
	isSupported: Boolean,
	values: Flow<VRCConfigValues?>,
): VRCConfigManager {
	val modules = listOf(DefaultVRCConfigBehaviour)

	val initialState = VRCConfigState(
		currentValues = null,
		isSupported = isSupported,
		mutedWarnings = listOf(),
	)

	val context = createContext(
		initialState = initialState,
		reducers = modules.map { it.reducer },
		scope = scope,
	)

	scope.launch {
		values.collect { context.dispatch(VRCConfigActions.UpdateValues(it)) }
	}

	val manager =  VRCConfigManager(context = context, userHeight = userHeight, config = config)
	modules.map { it.observer }.forEach { it?.invoke(manager) }

	return manager
}
