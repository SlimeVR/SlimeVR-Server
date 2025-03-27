package dev.slimevr.games.vrchat

import dev.slimevr.VRServer
import dev.slimevr.tracking.processor.config.SkeletonConfigToggles
import dev.slimevr.tracking.trackers.TrackerPosition
import dev.slimevr.tracking.trackers.TrackerUtils
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.*

enum class VRCTrackerModel(val value: Int, val id: Int) {
	UNKNOWN(-1, solarxr_protocol.rpc.VRCTrackerModel.UNKNOWN),
	SPHERE(0, solarxr_protocol.rpc.VRCTrackerModel.SPHERE),
	SYSTEM(1, solarxr_protocol.rpc.VRCTrackerModel.SYSTEM),
	BOX(2, solarxr_protocol.rpc.VRCTrackerModel.BOX),
	AXIS(3, solarxr_protocol.rpc.VRCTrackerModel.AXIS),
	;

	companion object {
		private val byValue = VRCTrackerModel.entries.associateBy { it.value }

		fun getByValue(value: Int): VRCTrackerModel? = byValue[value]
	}
}

enum class VRCSpineMode(val value: Int, val id: Int) {
	UNKNOWN(-1, solarxr_protocol.rpc.VRCSpineMode.UNKNOWN),
	LOCK_HIP(0, solarxr_protocol.rpc.VRCSpineMode.LOCK_HIP),
	LOCK_HEAD(1, solarxr_protocol.rpc.VRCSpineMode.LOCK_HEAD),
	LOCK_BOTH(2, solarxr_protocol.rpc.VRCSpineMode.LOCK_BOTH),
	;

	companion object {
		private val byValue = VRCSpineMode.entries.associateBy { it.value }

		fun getByValue(value: Int): VRCSpineMode? = byValue[value]
	}
}

enum class VRCAvatarMeasurementType(val value: Int, val id: Int) {
	UNKNOWN(-1, solarxr_protocol.rpc.VRCAvatarMeasurementType.UNKNOWN),
	ARM_SPAN(0, solarxr_protocol.rpc.VRCAvatarMeasurementType.ARM_SPAN),
	HEIGHT(1, solarxr_protocol.rpc.VRCAvatarMeasurementType.HEIGHT),
	;

	companion object {
		private val byValue = VRCAvatarMeasurementType.entries.associateBy { it.value }

		fun getByValue(value: Int): VRCAvatarMeasurementType? = byValue[value]
	}
}

data class VRCConfigValues(
	val legacyMode: Boolean,
	val shoulderTrackingDisabled: Boolean,
	val shoulderWidthCompensation: Boolean,
	val userHeight: Double,
	val calibrationRange: Double,
	val calibrationVisuals: Boolean,
	val trackerModel: VRCTrackerModel,
	val spineMode: VRCSpineMode,
	val avatarMeasurementType: VRCAvatarMeasurementType,
)

data class VRCConfigRecommendedValues(
	val legacyMode: Boolean,
	val shoulderTrackingDisabled: Boolean,
	val shoulderWidthCompensation: Boolean,
	val userHeight: Double,
	val calibrationRange: Double,
	val calibrationVisuals: Boolean,
	val trackerModel: VRCTrackerModel,
	val spineMode: Array<VRCSpineMode>,
	val avatarMeasurementType: VRCAvatarMeasurementType,
)

data class VRCConfigValidity(
	val legacyModeOk: Boolean,
	val shoulderTrackingOk: Boolean,
	val shoulderWidthCompensationOk: Boolean,
	val userHeightOk: Boolean,
	val calibrationOk: Boolean,
	val calibrationVisualsOk: Boolean,
	val tackerModelOk: Boolean,
	val spineModeOk: Boolean,
	val avatarMeasurementOk: Boolean,
)

abstract class VRCConfigHandler {
	abstract val isSupported: Boolean
	abstract fun initHandler(onChange: (config: VRCConfigValues) -> Unit)
}

class VRCConfigHandlerStub : VRCConfigHandler() {
	override val isSupported: Boolean
		get() = false

	override fun initHandler(onChange: (config: VRCConfigValues) -> Unit) {}
}

interface VRCConfigListener {
	fun onChange(validity: VRCConfigValidity, values: VRCConfigValues, recommended: VRCConfigRecommendedValues)
}

class VRChatConfigManager(val server: VRServer, private val handler: VRCConfigHandler) {

	private val listeners: MutableList<VRCConfigListener> = CopyOnWriteArrayList()
	var currentValues: VRCConfigValues? = null

	val isSupported: Boolean
		get() = handler.isSupported

	init {
		handler.initHandler(::onChange)
	}

	/**
	 * shoulderTrackingDisabled should be true if:
	 * The user isn't tracking their whole arms from their controllers:
	 * forceArmsFromHMD is enabled || the user doesn't have hand trackers with position || the user doesn't have lower arms trackers || the user doesn't have upper arm trackers
	 * And the user isn't tracking their arms from their HMD or doesn't have both shoulders:
	 * (forceArmsFromHMD is disabled && user has hand trackers with position) || user is missing a shoulder tracker
	 */
	fun recommendedValues(): VRCConfigRecommendedValues {
		val forceArmsFromHMD = server.humanPoseManager.getToggle(SkeletonConfigToggles.FORCE_ARMS_FROM_HMD)

		val hasLeftHandWithPosition = TrackerUtils.getTrackerForSkeleton(server.allTrackers, TrackerPosition.LEFT_HAND)?.hasPosition ?: false
		val hasRightHandWithPosition = TrackerUtils.getTrackerForSkeleton(server.allTrackers, TrackerPosition.RIGHT_HAND)?.hasPosition ?: false

		val isMissingAnArmTracker = TrackerUtils.getTrackerForSkeleton(server.allTrackers, TrackerPosition.LEFT_LOWER_ARM) == null ||
			TrackerUtils.getTrackerForSkeleton(server.allTrackers, TrackerPosition.RIGHT_LOWER_ARM) == null ||
			TrackerUtils.getTrackerForSkeleton(server.allTrackers, TrackerPosition.LEFT_UPPER_ARM) == null ||
			TrackerUtils.getTrackerForSkeleton(server.allTrackers, TrackerPosition.RIGHT_UPPER_ARM) == null
		val isMissingAShoulderTracker = TrackerUtils.getTrackerForSkeleton(server.allTrackers, TrackerPosition.LEFT_SHOULDER) == null ||
			TrackerUtils.getTrackerForSkeleton(server.allTrackers, TrackerPosition.RIGHT_SHOULDER) == null

		return VRCConfigRecommendedValues(
			legacyMode = false,
			shoulderTrackingDisabled =
			((forceArmsFromHMD || !hasLeftHandWithPosition || !hasRightHandWithPosition) || isMissingAnArmTracker) && // Not tracking shoulders from hands
				((!forceArmsFromHMD && hasLeftHandWithPosition && hasRightHandWithPosition) || isMissingAShoulderTracker), // Not tracking shoulders from HMD
			userHeight = server.humanPoseManager.userHeightFromConfig / 0.936,
			calibrationRange = 0.2,
			trackerModel = VRCTrackerModel.AXIS,
			spineMode = arrayOf(VRCSpineMode.LOCK_HIP, VRCSpineMode.LOCK_HEAD),
			calibrationVisuals = true,
			avatarMeasurementType = VRCAvatarMeasurementType.HEIGHT,
			shoulderWidthCompensation = true
		)
	}

	fun addListener(listener: VRCConfigListener) {
		listeners.add(listener)
	}

	fun removeListener(listener: VRCConfigListener) {
		listeners.removeIf { l -> l === listener }
	}

	fun checkValidity(values: VRCConfigValues, recommended: VRCConfigRecommendedValues): VRCConfigValidity = VRCConfigValidity(
		legacyModeOk = values.legacyMode == recommended.legacyMode,
		shoulderTrackingOk = values.shoulderTrackingDisabled == recommended.shoulderTrackingDisabled,
		spineModeOk = recommended.spineMode.contains(values.spineMode),
		tackerModelOk = values.trackerModel == recommended.trackerModel,
		calibrationOk = abs(values.calibrationRange - recommended.calibrationRange) < 0.1,
		userHeightOk = abs(server.humanPoseManager.userHeightFromConfig / 0.936 - values.userHeight) < 0.1,
		calibrationVisualsOk = values.calibrationVisuals == recommended.calibrationVisuals,
		avatarMeasurementOk = values.avatarMeasurementType == recommended.avatarMeasurementType,
		shoulderWidthCompensationOk = values.shoulderWidthCompensation == recommended.shoulderWidthCompensation
	)

	fun onChange(values: VRCConfigValues) {
		val recommended = recommendedValues()
		val validity = checkValidity(values, recommended)
		currentValues = values
		listeners.forEach {
			it.onChange(validity, values, recommended)
		}
	}
}
