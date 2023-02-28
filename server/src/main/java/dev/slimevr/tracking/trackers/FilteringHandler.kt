package dev.slimevr.tracking.trackers

import dev.slimevr.filtering.QuaternionMovingAverage
import dev.slimevr.filtering.TrackerFilters

/**
 * Class taking care of filtering logic
 * (smoothing and prediction)
 */
class FilteringHandler(val tracker: Tracker){
	private var movingAverage: QuaternionMovingAverage? = null

	fun setFiltering(type: TrackerFilters?, amount: Float) {
		movingAverage = if (type != null) {
			when (type) {
				TrackerFilters.SMOOTHING, TrackerFilters.PREDICTION -> QuaternionMovingAverage(
					type,
					amount,
					tracker.getRawRotation()
				)
				TrackerFilters.NONE -> null
				else -> null
			}
		} else {
			null
		}
	}
}
