package dev.slimevr.tracking.processor.skeleton

import dev.slimevr.tracking.trackers.Tracker
import java.util.*

// class that monitors the acceleration of the waist, hip, chest or upper chest trackers to detect taps
// and use this to trigger a variety of resets (if your wondering why no single tap class exists, it's because
// to many false positives)
class TapDetection(val skeleton: HumanSkeleton, val trackerToWatch: Tracker, val numberTrackersOverThreshold: Int, val tapToComplete: Int, val onTapCompleted: () -> Unit) {

	private val accelList = LinkedList<FloatArray>()
	private val tapTimestamps = LinkedList<Float>()
	private var timeWindowNS = 0.3f * tapToComplete * NS_CONVERTER
	private var waitForLowAccel = false

	// reset the lists for detecting taps
	fun reset() {
		tapTimestamps.clear()
		accelList.clear()
		waitForLowAccel = false
	}

	// main function for tap detection
	fun update() {
		// get the acceleration of the tracker and add it to the list
		val time = System.nanoTime().toFloat()
		accelList.add(floatArrayOf(trackerToWatch.getAcceleration().len(), time))

		// remove old values from the list (if they are too old)
		while (time - accelList.first()[1] > CLUMP_TIME_NS) {
			accelList.removeFirst()
		}

		// check for a tap
		val accelDelta = accelDelta
		if (accelDelta > NEEDED_ACCEL_DELTA && !waitForLowAccel) {
			// after a tap is added to the list, a lower acceleration
			// is needed before another tap can be added
			tapTimestamps.add(time)
			waitForLowAccel = true
		}

		// if waiting for low accel
		if (maxAccel < ALLOWED_BODY_ACCEL) {
			waitForLowAccel = false
		}

		// remove old taps from the list (if they are too old)
		if (!tapTimestamps.isEmpty()) {
			while (time - tapTimestamps.first() > timeWindowNS) {
				tapTimestamps.removeFirst()
				if (tapTimestamps.isEmpty()) return
			}
		}

		// if we have no taps within the timeframe or
		// if the user is moving their body too much, reset the tap detector
		if (!isUserStatic(trackerToWatch)) {
			reset()
			return
		}

		if (tapTimestamps.size >= tapToComplete) {
			onTapCompleted()
			reset()
		}
	}

	private val accelDelta: Float
		get() {
			var max = -999.9f
			var min = 999.9f
			for (`val` in accelList) {
				if (`val`[0] > max) max = `val`[0]
				if (`val`[0] < min) min = `val`[0]
			}
			return max - min
		}

	private val maxAccel: Float
		get() {
			var max = 0.0f
			for (`val` in accelList) {
				if (`val`[0] > max) {
					max = `val`[0]
				}
			}
			return max
		}

	// returns true if the user is not imparting more than allowedBodyAccel of
	// force on any of the torso or upper leg trackers (this sadly implies that
	// you need two or more trackers for this feature to be reliable)
	private fun isUserStatic(trackerToExclude: Tracker): Boolean {
		var num = 0
		if (skeleton.upperChestTracker != null &&
			skeleton.upperChestTracker != trackerToExclude
		) {
			if (skeleton.upperChestTracker!!.getAcceleration().lenSq()
				> ALLOWED_BODY_ACCEL_SQUARED
			) {
				num++
			}
		}
		if (skeleton.chestTracker != null &&
			skeleton.chestTracker != trackerToExclude
		) {
			if (skeleton.chestTracker!!.getAcceleration().lenSq() > ALLOWED_BODY_ACCEL_SQUARED) num++
		}
		if (skeleton.hipTracker != null && skeleton.hipTracker != trackerToExclude) {
			if (skeleton.hipTracker!!.getAcceleration().lenSq() > ALLOWED_BODY_ACCEL_SQUARED) num++
		}
		if (skeleton.waistTracker != null &&
			skeleton.waistTracker != trackerToExclude
		) {
			if (skeleton.waistTracker!!.getAcceleration().lenSq() > ALLOWED_BODY_ACCEL_SQUARED) num++
		}
		if (skeleton.leftUpperLegTracker != null &&
			skeleton.leftUpperLegTracker != trackerToExclude
		) {
			if (skeleton.leftUpperLegTracker!!.getAcceleration().lenSq()
				> ALLOWED_BODY_ACCEL_SQUARED
			) {
				num++
			}
		}
		if (skeleton.rightUpperLegTracker != null &&
			skeleton.rightUpperLegTracker != trackerToExclude
		) {
			if (skeleton.rightUpperLegTracker!!.getAcceleration().lenSq()
				> ALLOWED_BODY_ACCEL_SQUARED
			) {
				num++
			}
		}
		if (skeleton.leftFootTracker != null &&
			skeleton.leftFootTracker != trackerToExclude
		) {
			if (skeleton.leftFootTracker!!.getAcceleration().lenSq() > ALLOWED_BODY_ACCEL_SQUARED) {
				num++
			}
		}
		if (skeleton.rightFootTracker != null &&
			skeleton.rightFootTracker != trackerToExclude
		) {
			if (skeleton.rightFootTracker!!.getAcceleration().lenSq()
				> ALLOWED_BODY_ACCEL_SQUARED
			) {
				num++
			}
		}
		return num < numberTrackersOverThreshold
	}

	companion object {
		// hyperparameters
		private const val NS_CONVERTER = 1.0e9f
		private const val NEEDED_ACCEL_DELTA = 6.0f
		private const val ALLOWED_BODY_ACCEL = 2.5f
		private const val ALLOWED_BODY_ACCEL_SQUARED = ALLOWED_BODY_ACCEL * ALLOWED_BODY_ACCEL
		private const val CLUMP_TIME_NS = 0.06f * NS_CONVERTER
	}
}
