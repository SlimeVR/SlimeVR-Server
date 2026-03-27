package dev.slimevr.vrchat

import dev.slimevr.VRServer
import dev.slimevr.config.AppConfig
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
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
typealias VRCConfigBehaviour = Behaviour<VRCConfigState, VRCConfigActions, VRCConfigManager>

class VRCConfigManager(
	val context: VRCConfigContext,
	val config: AppConfig,
) {
	companion object {
		fun create(
			config: AppConfig,
			scope: CoroutineScope,
			isSupported: Boolean,
			values: Flow<VRCConfigValues?>,
		): VRCConfigManager {
			val behaviours = listOf(DefaultVRCConfigBehaviour)

			val context = Context.create(
				initialState = VRCConfigState(
					currentValues = null,
					isSupported = isSupported,
					mutedWarnings = listOf(),
				),
				scope = scope,
				behaviours = behaviours,
			)

			scope.launch {
				values.collect { context.dispatch(VRCConfigActions.UpdateValues(it)) }
			}

			val manager = VRCConfigManager(context = context, config = config)
			behaviours.forEach { it.observe(manager) }
			return manager
		}
	}
}

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

fun computeValidity(values: VRCConfigValues, recommended: VRCConfigRecommendedValues): VRCConfigValidity = VRCConfigValidity(
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
