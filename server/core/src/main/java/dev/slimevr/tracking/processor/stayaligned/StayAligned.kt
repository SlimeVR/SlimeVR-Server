package dev.slimevr.tracking.processor.stayaligned

import dev.slimevr.VRServer
import dev.slimevr.config.StayAlignedConfig
import dev.slimevr.math.Angle
import dev.slimevr.tracking.processor.stayaligned.StayAlignedDefaults.IMU_TO_YAW_CORRECTION
import dev.slimevr.tracking.processor.stayaligned.StayAlignedDefaults.YAW_CORRECTION_DEFAULT
import dev.slimevr.tracking.processor.stayaligned.adjust.AdjustTrackerYaw
import dev.slimevr.tracking.processor.stayaligned.trackers.TrackerSkeleton

/**
 * Manager to keep the trackers aligned.
 */
object StayAligned {

	private var nextTrackerIndex = 0

	/**
	 * Adjusts the yaw of the next tracker.
	 *
	 * We only adjust one tracker per tick to minimize CPU usage. When the server is
	 * running at 1000 Hz and there are 20 trackers, each tracker is still updated 50
	 * times a second.
	 */
	fun adjustNextTracker(trackers: TrackerSkeleton, config: StayAlignedConfig) {
		if (!config.enabled) {
			return
		}

		val numTrackers = trackers.allTrackers.size
		if (numTrackers == 0) {
			return
		}

		val trackerToAdjust = trackers.allTrackers[nextTrackerIndex % numTrackers]
		++nextTrackerIndex

		// Update hide correction since the config could have changed
		trackerToAdjust.stayAligned.hideCorrection = config.hideYawCorrection

		val yawCorrectionPerSec =
			IMU_TO_YAW_CORRECTION.getOrDefault(trackerToAdjust.imuType, YAW_CORRECTION_DEFAULT)
		if (yawCorrectionPerSec == Angle.ZERO) {
			return
		}

		// Scale yaw correction since we're only updating one tracker per tick
		val yawCorrection =
			yawCorrectionPerSec *
				VRServer.instance.fpsTimer.timePerFrame *
				numTrackers.toFloat()

		AdjustTrackerYaw.adjust(
			trackerToAdjust,
			trackers,
			yawCorrection,
			config,
		)
	}
}
