package dev.slimevr.tracking.processor.skeleton

import dev.slimevr.tracking.trackers.Tracker
import java.util.*

// class that monitors the acceleration of the waist, hip, chest or upper chest trackers to detect taps
// and use this to trigger a varaity of resets (if your wondering why no single tap class exists, it's because
// to many false positives)
class TapDetection {
	// server and related classes
	private val skeleton: HumanSkeleton?

	// tap detection
	@JvmField
	var enabled: Boolean = false
	private val accelList = LinkedList<FloatArray>()
	private val tapTimes = LinkedList<Float>()
	var tracker: Tracker? = null
		private set
	private var numberTrackersOverThreshold = 1

	private var timeWindowNS = 0.6f * NS_CONVERTER

	// state
	var detectionTime: Float = -1.0f
		private set
	var taps: Int = 0
		private set
	private var waitForLowAccel = false

	constructor(skeleton: HumanSkeleton?) {
		this.skeleton = skeleton
	}

	constructor(
		skeleton: HumanSkeleton?,
		trackerToWatch: Tracker?,
	) {
		this.skeleton = skeleton
		this.tracker = trackerToWatch
	}

	// set the tracker to watch and detect taps on
	fun setTrackerToWatch(tracker: Tracker?) {
		this.tracker = tracker
	}

	fun setNumberTrackersOverThreshold(numberTrackersOverThreshold: Int) {
		this.numberTrackersOverThreshold = numberTrackersOverThreshold
	}

	// reset the lists for detecting taps
	fun resetDetector() {
		tapTimes.clear()
		accelList.clear()
		taps = 0
	}

	// set the max taps this detector is configured to detect
	fun setMaxTaps(maxTaps: Int) {
		timeWindowNS = 0.3f * maxTaps * NS_CONVERTER
	}

	// main function for tap detection
	fun update() {
		if (skeleton == null || !enabled) return

		if (tracker == null) return

		// get the acceleration of the tracker and add it to the list
		val time = System.nanoTime().toFloat()
		val listval = floatArrayOf(tracker!!.getAcceleration().len(), time)
		accelList.add(listval)

		// remove old values from the list (if they are too old)
		while (time - accelList.first()[1] > CLUMP_TIME_NS) {
			accelList.removeFirst()
		}

		// check for a tap
		val accelDelta = accelDelta
		if (accelDelta > NEEDED_ACCEL_DELTA && !waitForLowAccel) {
			// after a tap is added to the list, a lower acceleration
			// is needed before another tap can be added
			tapTimes.add(time)
			waitForLowAccel = true
		}

		// if waiting for low accel
		if (maxAccel < ALLOWED_BODY_ACCEL) {
			waitForLowAccel = false
		}

		// remove old taps from the list (if they are too old)
		if (!tapTimes.isEmpty()) {
			while (time - tapTimes.first() > timeWindowNS) {
				tapTimes.removeFirst()
				if (tapTimes.isEmpty()) return
			}
		}

		// if the user is moving their body too much, reset the tap list
		if (!isUserStatic(tracker!!)) {
			tapTimes.clear()
			accelList.clear()
		}

		// get the amount of taps in the list
		// and set the detection time
		val newTaps = tapTimes.size
		if (newTaps > taps) {
			taps = newTaps
			detectionTime = time
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
		if (skeleton!!.upperChestTracker != null &&
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
		if (skeleton.hipTracker != null && skeleton.hipTracker != trackerToExclude
		) {
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
			if (skeleton.leftFootTracker!!.getAcceleration().lenSq() > ALLOWED_BODY_ACCEL_SQUARED
			) {
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
