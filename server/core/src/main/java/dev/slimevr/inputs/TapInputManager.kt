package dev.slimevr.inputs

import dev.slimevr.VRServer
import dev.slimevr.tracking.processor.skeleton.HumanSkeleton
import dev.slimevr.tracking.processor.skeleton.TapDetection
import dev.slimevr.tracking.trackers.Tracker
import dev.slimevr.util.ann.VRServerThread

/**
 * Handles tap detection for SteamVR virtual controller input
 */
class TapInputManager(
	private val skeleton: HumanSkeleton,
	private val vrServer: VRServer,
) {

	// tap detectors
	private lateinit var leftDoubleDetector: TapDetection
	private lateinit var leftTripleDetector: TapDetection
	private lateinit var rightDoubleDetector: TapDetection
	private lateinit var rightTripleDetector: TapDetection

	val NS_CONVERTER: Float = 1.0e9f
	private var doubleTapDelay = 0.25f * NS_CONVERTER

	init {
		reinit()
	}

	fun reinit() {
		// Create tap detectors for both hands
		leftDoubleDetector = TapDetection(skeleton, trackerToWatchLeftHand)
		leftDoubleDetector.enabled = true
		leftDoubleDetector.setMaxTaps(2)

		leftTripleDetector = TapDetection(skeleton, trackerToWatchLeftHand)
		leftTripleDetector.enabled = true
		leftTripleDetector.setMaxTaps(3)

		rightDoubleDetector = TapDetection(skeleton, trackerToWatchRightHand)
		rightDoubleDetector.enabled = true
		rightDoubleDetector.setMaxTaps(2)

		rightTripleDetector = TapDetection(skeleton, trackerToWatchRightHand)
		rightTripleDetector.enabled = true
		rightTripleDetector.setMaxTaps(3)
	}

	@VRServerThread
	fun update() {
		// update the tap detectors
		leftDoubleDetector.update()
		leftTripleDetector.update()
		rightDoubleDetector.update()
		rightTripleDetector.update()

		// check if any tap detectors have detected taps
		if (3 <= leftTripleDetector.taps) {
			leftTripleDetector.resetDetector()
			leftDoubleDetector.resetDetector()
			vrServer.inputs.add(Input(false, InputType.TRIPLE_TAP))
		} else if (2 <= leftDoubleDetector.taps && System.nanoTime() - rightDoubleDetector.detectionTime > doubleTapDelay) {
			leftTripleDetector.resetDetector()
			leftDoubleDetector.resetDetector()
			vrServer.inputs.add(Input(false, InputType.DOUBLE_TAP))
		}

		if (3 <= rightTripleDetector.taps) {
			rightTripleDetector.resetDetector()
			rightDoubleDetector.resetDetector()
			vrServer.inputs.add(Input(true, InputType.TRIPLE_TAP))
		} else if (2 <= rightDoubleDetector.taps && System.nanoTime() - rightDoubleDetector.detectionTime > doubleTapDelay) {
			rightTripleDetector.resetDetector()
			rightDoubleDetector.resetDetector()
			vrServer.inputs.add(Input(true, InputType.DOUBLE_TAP))
		}
	}

	private val trackerToWatchLeftHand: Tracker?
		get() = listOfNotNull(
			skeleton.leftHandTracker,
			skeleton.leftLowerArmTracker,
			skeleton.leftUpperArmTracker,
			skeleton.leftThumbDistalTracker,
			skeleton.leftThumbProximalTracker,
			skeleton.leftThumbMetacarpalTracker,
		).firstOrNull()

	private val trackerToWatchRightHand: Tracker?
		get() = listOfNotNull(
			skeleton.rightHandTracker,
			skeleton.rightLowerArmTracker,
			skeleton.rightUpperArmTracker,
			skeleton.rightThumbDistalTracker,
			skeleton.rightThumbProximalTracker,
			skeleton.rightThumbMetacarpalTracker,
		).firstOrNull()
}
