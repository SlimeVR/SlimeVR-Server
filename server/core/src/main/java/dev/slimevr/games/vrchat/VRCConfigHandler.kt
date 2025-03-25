package dev.slimevr.games.vrchat

import dev.slimevr.VRServer
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.*

enum class VRCTrackerModel(val value: Int, val id: Int) {
	UNKNOWN(-1, solarxr_protocol.rpc.VRCTrackerModel.UNKNOWN),
	SPHERE(0, solarxr_protocol.rpc.VRCTrackerModel.SPHERE),
	SYSTEM(1, solarxr_protocol.rpc.VRCTrackerModel.SYSTEM),
	BOX(2, solarxr_protocol.rpc.VRCTrackerModel.BOX),
	AXIS(3, solarxr_protocol.rpc.VRCTrackerModel.AXIS);

	companion object {
		private val byValue = VRCTrackerModel.entries.associateBy { it.value }

		fun getByValue(value: Int): VRCTrackerModel? = byValue[value]
	}
}

enum class VRCSpineModel(val value: Int, val id: Int) {
	UNKNOWN(-1, solarxr_protocol.rpc.VRCSpineMode.UNKNOWN),
	LOCK_HIP(0, solarxr_protocol.rpc.VRCSpineMode.LOCK_HIP),
	LOCK_HEAD(1, solarxr_protocol.rpc.VRCSpineMode.LOCK_HEAD),
	LOCK_BOTH(2, solarxr_protocol.rpc.VRCSpineMode.LOCK_BOTH);


	companion object {
		private val byValue = VRCSpineModel.entries.associateBy { it.value }

		fun getByValue(value: Int): VRCSpineModel? = byValue[value]
	}
}

data class VRCConfigValues(
	val legacyMode: Boolean,
	val shoulderTrackingDisabled: Boolean,
	val userHeight: Double,
	val calibrationRange: Double,
	val calibrationVisuals: Boolean,
	val trackerModel: VRCTrackerModel,
	val spineMode: VRCSpineModel,
)

data class VRCConfigRecommendedValues(
	val legacyMode: Boolean,
	val shoulderTrackingDisabled: Boolean,
	val userHeight: Double,
	val calibrationRange: Double,
	val calibrationVisuals: Boolean,
	val trackerModel: VRCTrackerModel,
	val spineMode: Array<VRCSpineModel>,
)

data class VRCConfigValidity(
	val legacyModeOk: Boolean,
	val shoulderTrackingOk: Boolean,
	val userHeightOk: Boolean,
	val calibrationOk: Boolean,
	val calibrationVisualsOk: Boolean,
	val tackerModelOk: Boolean,
	val spineModeOk: Boolean,
)

abstract class VRCConfigHandler {
	abstract val isSupported: Boolean
	abstract fun initHandler(onChange: (config: VRCConfigValues) -> Unit);
}

class VRCConfigHandlerStub: VRCConfigHandler() {
	override val isSupported: Boolean
		get() = false

	override fun initHandler(onChange: (config: VRCConfigValues) -> Unit) {}
}

interface VRCConfigListener {
	fun onChange(validity: VRCConfigValidity, values: VRCConfigValues, recommended: VRCConfigRecommendedValues)
}

class VRChatConfigManager(val server: VRServer, private val handler: VRCConfigHandler) {

	private val listeners: MutableList<VRCConfigListener> = CopyOnWriteArrayList()
	var currentValues: VRCConfigValues? = null;

	val isSupported: Boolean
		get() = handler.isSupported

	init {
		handler.initHandler(::onChange)
	}

	fun recommendedValues(): VRCConfigRecommendedValues {
		return VRCConfigRecommendedValues(
			legacyMode = false,
			shoulderTrackingDisabled = !server.allTrackers
				.any { //FIXME: do better condition for shoulder tracking disabled recommendation
					if (it.isComputed) return@any false;
					val position = it.trackerPosition ?: return@any false
					position.isLeftArmTracker() || position.isRightArmTracker()
				},
			userHeight = server.humanPoseManager.userHeightFromConfig / 0.936,
			calibrationRange = 0.2,
			trackerModel = VRCTrackerModel.AXIS,
			spineMode = arrayOf(VRCSpineModel.LOCK_HIP, VRCSpineModel.LOCK_HEAD),
			calibrationVisuals = true,
		)
	}

	fun addListener(listener: VRCConfigListener) {
		listeners.add(listener);
	}

	fun removeListener(listener: VRCConfigListener) {
		listeners.removeIf { l -> l === listener }
	}

	fun checkValidity(values: VRCConfigValues, recommended: VRCConfigRecommendedValues): VRCConfigValidity {
		return VRCConfigValidity(
			legacyModeOk = values.legacyMode == recommended.legacyMode,
			shoulderTrackingOk = values.shoulderTrackingDisabled == recommended.shoulderTrackingDisabled,
			spineModeOk = recommended.spineMode.contains(values.spineMode),
			tackerModelOk = values.trackerModel == recommended.trackerModel,
			calibrationOk = abs(values.calibrationRange - recommended.calibrationRange) < 0.1,
			userHeightOk = abs(server.humanPoseManager.userHeightFromConfig / 0.936 - values.userHeight) < 0.1,
			calibrationVisualsOk = values.calibrationVisuals == recommended.calibrationVisuals
		)
	}

	fun onChange(values: VRCConfigValues) {
		val recommended = recommendedValues();
		val validity = checkValidity(values, recommended)
		currentValues = values;
		listeners.forEach {
			it.onChange(validity, values, recommended)
		}
	}

}
