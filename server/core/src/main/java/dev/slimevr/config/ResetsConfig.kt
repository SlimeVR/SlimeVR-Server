package dev.slimevr.config

import kotlinx.serialization.Serializable

enum class ArmsResetMode(val id: Int) {
	// Upper arm going back and forearm going forward
	BACK(0),

	// Arms going forward
	FORWARD(1),

	// Arms going up to the sides into a tpose
	TPOSE_UP(2),

	// Arms going down to the sides from a tpose
	TPOSE_DOWN(3),
	;

	companion object {
		val values = entries.toTypedArray()

		@JvmStatic
		fun fromId(id: Int): ArmsResetMode? {
			for (filter in values) {
				if (filter.id == id) return filter
			}
			return null
		}
	}
}

enum class MountingMethod(val id: Int) {
	MANUAL(0),
	AUTOMATIC(1),
	;

	companion object {
		val values = MountingMethod.entries.toTypedArray()

		@JvmStatic
		fun fromId(id: Int): MountingMethod? {
			for (filter in values) {
				if (filter.id == id) return filter
			}
			return null
		}
	}
}

@Serializable
data class ResetsConfig(
	// Always reset mounting for feet
	val resetMountingFeet: Boolean = false,

	// Reset mode used for the arms
	val mode: ArmsResetMode = ArmsResetMode.BACK,

	// Yaw reset smoothing time in seconds
	val yawResetSmoothTime: Float = 0.0f,

	// Save automatic mounting reset calibration
	val saveMountingReset: Boolean = false,

	// Reset the HMD's pitch upon full reset
	val resetHmdPitch: Boolean = false,

	val lastMountingMethod: MountingMethod = MountingMethod.AUTOMATIC,

	val yawResetDelay: Float = 0.0f,
	val fullResetDelay: Float = 3.0f,
	val mountingResetDelay: Float = 3.0f,
) {
// 	fun updateTrackersResetsSettings() {
// 		for (t in VRServer.instance.allTrackers) {
// 			t.resetsHandler.readResetConfig(this)
// 		}
// 	}
}
