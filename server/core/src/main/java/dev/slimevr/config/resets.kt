package dev.slimevr.config

import kotlinx.serialization.Serializable
import solarxr_protocol.rpc.ArmsMountingResetMode
import solarxr_protocol.rpc.ArmsResetMode

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
	val armsResetMode: ArmsResetMode = ArmsResetMode.BACK,
	/** Yaw reset smoothing time in seconds */
	val yawResetSmoothTime: Float = 0.0f,
	/** Save automatic mounting reset calibration */
	val saveMountingReset: Boolean = false,
	/** Reset the HMD's pitch upon full reset */
	val resetHmdPitch: Boolean = false,
	val lastMountingMethod: MountingMethods = MountingMethods.AUTOMATIC,
)
