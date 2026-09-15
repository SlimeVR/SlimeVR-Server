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

// The HMD height is taken from the HMD pose, which is between the eyes.
// This compensates for the offset to the full height (top of the head).
// From Drillis and Contini (1966)
const val EYE_HEIGHT_TO_HEIGHT_RATIO = 0.936f

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
typealias VRCConfigBehaviourType = Behaviour<VRCConfigManager>

class VRCConfigManager(
	val context: VRCConfigContext,
	val config: AppConfig,
) {
	fun startObserving() = context.observeAll(this)

	companion object {
		fun create(
			config: AppConfig,
			scope: CoroutineScope,
			isSupported: Boolean,
			values: Flow<VRCConfigValues?>,
		): VRCConfigManager {
			val context = Context.create(
				initialState = VRCConfigState(
					currentValues = null,
					isSupported = isSupported,
					mutedWarnings = listOf(),
				),
				scope = scope,
				reducer = ::reduce,
				behaviours = listOf(DefaultVRCConfigBehaviour()),
				name = "VRCConfig",
			)

			scope.launch {
				values.collect { context.dispatch(VRCConfigActions.UpdateValues(it)) }
			}

			return VRCConfigManager(context = context, config = config)
		}
	}
}

fun computeRecommendedValues(server: VRServer, userHeight: Float): VRCConfigRecommendedValues {
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
		userHeight = userHeight / EYE_HEIGHT_TO_HEIGHT_RATIO,
		calibrationRange = 0.2f,
		trackerModel = VRCTrackerModel.AXIS,
		spineMode = listOf(VRCSpineMode.LOCK_HIP, VRCSpineMode.LOCK_HEAD),
		calibrationVisuals = true,
		avatarMeasurementType = VRCAvatarMeasurementType.HEIGHT,
		shoulderWidthCompensation = true,
	)
}

fun isVRCConfigValid(validity: VRCConfigValidity, mutedWarnings: List<String>): Boolean = (validity.legacyModeOk || "legacyModeOk" in mutedWarnings) &&
	(validity.shoulderTrackingOk || "shoulderTrackingOk" in mutedWarnings) &&
	(validity.shoulderWidthCompensationOk || "shoulderWidthCompensationOk" in mutedWarnings) &&
	(validity.userHeightOk || "userHeightOk" in mutedWarnings) &&
	(validity.calibrationRangeOk || "calibrationRangeOk" in mutedWarnings) &&
	(validity.calibrationVisualsOk || "calibrationVisualsOk" in mutedWarnings) &&
	(validity.trackerModelOk || "trackerModelOk" in mutedWarnings) &&
	(validity.spineModeOk || "spineModeOk" in mutedWarnings) &&
	(validity.avatarMeasurementTypeOk || "avatarMeasurementTypeOk" in mutedWarnings)

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
