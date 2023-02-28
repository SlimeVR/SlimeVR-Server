package dev.slimevr.tracking.trackers

import dev.slimevr.config.FiltersConfig
import dev.slimevr.filtering.QuaternionMovingAverage
import dev.slimevr.filtering.TrackerFilters
import io.github.axisangles.ktmath.Quaternion

/**
 * Class taking care of filtering logic
 * (smoothing and prediction)
 */
class TrackerFilteringHandler(val tracker: Tracker) {
	private var movingAverage: QuaternionMovingAverage? = null
	var enabled = false

	fun readFilteringConfig(config: FiltersConfig) {
		val type = TrackerFilters.getByConfigkey(config.type)
		if (type == TrackerFilters.SMOOTHING || type == TrackerFilters.PREDICTION) {
			movingAverage = QuaternionMovingAverage(
				type,
				config.amount,
				tracker.getRawRotation()
			)
			enabled = true
		} else {
			movingAverage = null
			enabled = false
		}
	}

	fun tick() {
		movingAverage?.update()
	}

	fun getFilteredRotation(): Quaternion {
		return movingAverage?.filteredQuaternion ?: Quaternion.IDENTITY
	}
}
