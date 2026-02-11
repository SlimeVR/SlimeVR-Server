package dev.slimevr.config

import dev.slimevr.VRServer
import kotlinx.serialization.Serializable

@Serializable
data class FiltersConfig(
	// Type of filtering applied (none, smoothing or prediction)
	val type: String = "prediction",
	// Amount/Intensity of the specified filtering (0 to 1)
	val amount: Float = 0.2f,
) {
// 	fun updateTrackersFilters() {
// 		for (tracker in VRServer.instance.allTrackers) {
// 			if (tracker.allowFiltering) {
// 				tracker.filteringHandler.readFilteringConfig(this, tracker.getRotation())
// 			}
// 		}
// 	}
}
