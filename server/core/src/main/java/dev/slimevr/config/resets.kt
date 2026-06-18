package dev.slimevr.config

import kotlinx.serialization.Serializable

enum class ArmsResetModes(val id: Int) {
	/** Upper arm going back and forearm going forward */
	BACK(0),
	/** Arms going forward */
	FORWARD(1),
	/** Arms going up to the sides into a T-pose */
	T_POSE_UP(2),
	/** Arms going down to the sides from a T-pose */
	T_POSE_DOWN(3),
	;

	companion object {
		val values = entries.toTypedArray()

		fun fromId(id: Int): ArmsResetModes? {
			for (filter in values) {
				if (filter.id == id) return filter
			}
			return null
		}
	}
}

enum class MountingMethods(val id: Int) {
	MANUAL(0),
	AUTOMATIC(1),
	;

	companion object {
		val values = MountingMethods.entries.toTypedArray()

		fun fromId(id: Int): MountingMethods? {
			for (filter in values) {
				if (filter.id == id) return filter
			}
			return null
		}
	}
}

@Serializable
data class ResetsConfig(
	/** Always reset mounting for feet */
	val resetMountingFeet: Boolean = false,
	/** Reset mode used for the arms */
	val mode: ArmsResetModes = ArmsResetModes.BACK,
	/** Yaw reset smoothing time in seconds */
	val yawResetSmoothTime: Float = 0.0f,
	/** Save automatic mounting reset calibration */
	val saveMountingReset: Boolean = false,
	/** Reset the HMD's pitch upon full reset */
	val resetHmdPitch: Boolean = false,
	val lastMountingMethod: MountingMethods = MountingMethods.AUTOMATIC,
	val yawResetDelay: Float = 0.0f,
	val fullResetDelay: Float = 3.0f,
	val mountingResetDelay: Float = 3.0f,
)
