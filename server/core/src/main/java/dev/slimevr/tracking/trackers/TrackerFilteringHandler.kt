package dev.slimevr.tracking.trackers

import dev.slimevr.config.FiltersConfig
import dev.slimevr.filtering.QuaternionMovingAverage
import dev.slimevr.filtering.TrackerFilters
import io.github.axisangles.ktmath.Quaternion

/**
 * Class taking care of filtering logic
 * (smoothing and prediction)
 * See QuaternionMovingAverage.kt for the quaternion math.
 */
class TrackerFilteringHandler {
	// Instantiated by default in case config doesn't get read (if tracker doesn't support filtering)
	private var movingAverage = QuaternionMovingAverage(TrackerFilters.NONE)
	var filteringEnabled = false

	/**
	 * Reads/loads filtering settings from given config
	 */
	fun readFilteringConfig(config: FiltersConfig, currentRotation: Quaternion) {
		val type = TrackerFilters.getByConfigkey(config.type)
		if (type == TrackerFilters.SMOOTHING || type == TrackerFilters.PREDICTION) {
			movingAverage = QuaternionMovingAverage(
				type,
				config.amount,
				currentRotation,
			)
			filteringEnabled = true
		} else {
			movingAverage = QuaternionMovingAverage(
				TrackerFilters.NONE,
				initialRotation = currentRotation,
			)
			filteringEnabled = false
		}
	}

	/**
	 * Update the moving average to make it smooth
	 */
	fun update() {
		movingAverage.update()
	}

	/**
	 * Updates the latest rotation
	 */
	fun dataTick(currentRawRotation: Quaternion) {
		movingAverage.addQuaternion(currentRawRotation)
	}

	/**
	 * Call when doing a full reset to reset the tracking of rotations >180 degrees
	 */
	fun resetMovingAverage(currentRotation: Quaternion, reference: Quaternion) {
		movingAverage.resetQuats(currentRotation, reference)
	}

	/**
	 * Get the filtered rotation from the moving average (either prediction/smoothing or just >180 degs)
	 */
	fun getFilteredRotation() = movingAverage.filteredQuaternion

	/**
	 * Get the impact filtering has on the rotation
	 */
	fun getFilteringImpact(): Float = movingAverage.filteringImpact
}
