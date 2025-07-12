package dev.slimevr.inputs

import dev.slimevr.tracking.processor.HumanPoseManager
import dev.slimevr.tracking.processor.skeleton.HumanSkeleton
import dev.slimevr.tracking.processor.skeleton.TapDetection
import dev.slimevr.tracking.trackers.Tracker

/**
 * Handles tap detection for SteamVR virtual controller input
 */
class TapInputManager(
	private val skeleton: HumanSkeleton,
	private val humanPoseManager: HumanPoseManager?,
) {

	// tap detectors
	private var leftDoubleDetector: TapDetection? = null
	private var leftTripleDetector: TapDetection? = null
	private var rightDoubleDetector: TapDetection? = null
	private var rightTripleDetector: TapDetection? = null

	init {
		// Create tap detectors for both hands
		leftDoubleDetector = TapDetection(skeleton, trackerToWatchLeftHand)
		leftTripleDetector = TapDetection(skeleton, trackerToWatchLeftHand)
		rightDoubleDetector = TapDetection(skeleton, trackerToWatchRightHand)
		rightTripleDetector = TapDetection(skeleton, trackerToWatchRightHand)
	}

	fun update() {
		if (leftDoubleDetector == null ||
			leftTripleDetector == null ||
			rightDoubleDetector == null ||
			rightTripleDetector == null
		) {
			return
		}

		// update the tap detectors
		leftDoubleDetector!!.update()
		leftTripleDetector!!.update()
		rightDoubleDetector!!.update()
		rightTripleDetector!!.update()

		// check if any tap detectors have detected taps
		// TODO
	}

	private val trackerToWatchLeftHand: Tracker?
		get() = listOf(
			skeleton.leftHandTracker,
			skeleton.leftLowerArmTracker,
			skeleton.leftUpperArmTracker,
			skeleton.leftThumbDistalTracker,
			skeleton.leftThumbProximalTracker,
			skeleton.leftThumbMetacarpalTracker,
		).firstOrNull()

	private val trackerToWatchRightHand: Tracker?
		get() = listOf(
			skeleton.rightHandTracker,
			skeleton.rightLowerArmTracker,
			skeleton.rightUpperArmTracker,
			skeleton.rightThumbDistalTracker,
			skeleton.rightThumbProximalTracker,
			skeleton.rightThumbMetacarpalTracker,
		).firstOrNull()
}
