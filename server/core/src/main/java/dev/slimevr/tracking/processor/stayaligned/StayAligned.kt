package dev.slimevr.tracking.processor.stayaligned

import dev.slimevr.VRServer
import dev.slimevr.config.StayAlignedConfig
import dev.slimevr.tracking.processor.stayaligned.StayAlignedDefaults.EXTRA_YAW_CORRECTION_PER_SEC
import dev.slimevr.tracking.processor.stayaligned.StayAlignedDefaults.YAW_CORRECTION_PER_SEC
import dev.slimevr.tracking.processor.stayaligned.adjust.AdjustTrackerYaw
import dev.slimevr.tracking.processor.stayaligned.skeleton.RelaxedPose
import dev.slimevr.tracking.processor.stayaligned.skeleton.TrackerSkeleton

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
	fun adjustNextTracker(
		trackers: TrackerSkeleton,
		config: StayAlignedConfig,
		relaxedPose: RelaxedPose?,
	) {
		if (!config.enabled) {
			return
		}

		val numTrackers = trackers.allTrackers.size
		if (numTrackers == 0) {
			return
		}

		val tracker = trackers.allTrackers[nextTrackerIndex % numTrackers]

		var yawCorrectionPerSec = YAW_CORRECTION_PER_SEC
		if (config.extraYawCorrection) {
			yawCorrectionPerSec += EXTRA_YAW_CORRECTION_PER_SEC
		}

		AdjustTrackerYaw.adjust(
			tracker,
			trackers,
			// Scale yaw correction since we're only updating one tracker per tick
			yawCorrectionPerSec
				* VRServer.instance.fpsTimer.timePerFrame
				* numTrackers.toFloat(),
			relaxedPose,
		)

		++nextTrackerIndex
	}
}
