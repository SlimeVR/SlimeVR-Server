package dev.slimevr.tracking.processor.skeleton

import dev.slimevr.VRServer
import dev.slimevr.tracking.processor.HumanPoseManager
import dev.slimevr.tracking.trackers.Tracker
import java.util.concurrent.CopyOnWriteArrayList

class TapDetectionManager(
	val server: VRServer,
	val skeleton: HumanSkeleton,
	val humanPoseManager: HumanPoseManager,
) {
	private var tapDetectors: MutableList<TapDetection> = CopyOnWriteArrayList()
	var yawResetDetector: TapDetection? = null
	var fullResetDetector: TapDetection? = null
	var mountingResetDetector: TapDetection? = null

	var config = server.configManager.vrConfig.tapDetection

	init {
		refresh()
	}

	fun registerSingleTapDetectors() {
		for (tracker in server.allTrackers) {
			tapDetectors.add(
				TapDetection(skeleton, tracker, config.numberTrackersOverThreshold, 2) {
					server.tapSetupHandler.sendTap(tracker)
				},
			)
		}
	}

	fun registerResetsDetectors() {
		val yawTracker = yawResetTracker
		yawResetDetector = if (yawTracker != null && config.yawResetEnabled) {
			TapDetection(skeleton, yawTracker, config.numberTrackersOverThreshold, config.yawResetTaps) {
				server.scheduleResetTrackersYaw(RESET_SOURCE_NAME, (config.yawResetDelay * 1000).toLong())
			}
		} else {
			null
		}

		val fullTracker = fullResetTracker
		fullResetDetector = if (fullTracker != null && config.fullResetEnabled) {
			TapDetection(skeleton, fullTracker, config.numberTrackersOverThreshold, config.fullResetTaps) {
				server.scheduleResetTrackersFull(RESET_SOURCE_NAME, (config.fullResetDelay * 1000).toLong())
			}
		} else {
			null
		}

		val mountingTracker = mountingResetTracker
		mountingResetDetector = if (mountingTracker != null && config.mountingResetEnabled) {
			TapDetection(skeleton, mountingTracker, config.numberTrackersOverThreshold, config.mountingResetTaps) {
				server.scheduleResetTrackersMounting(RESET_SOURCE_NAME, (config.mountingResetDelay * 1000).toLong())
			}
		} else {
			null
		}
	}

	/**
	 * Called when the list of available trackers gets updated
	 * or when the tap settings get changed
	 * it re-create the tap detectors according to the configs and available trackers
	 */
	fun refresh() {
		tapDetectors.clear()
		registerSingleTapDetectors()
		registerResetsDetectors()
	}

	fun update() {
		// We disable the resets detectors during the assignment phase so you cant
		// trigger a reset while assigning
		if (config.setupMode) {
			for (detector in tapDetectors) {
				detector.update()
			}
		} else {
			yawResetDetector?.update()
			fullResetDetector?.update()
			mountingResetDetector?.update()
		}
	}

	private val mountingResetTracker: Tracker?
		get() {
			return arrayOf(
				skeleton.rightUpperLegTracker,
				skeleton.rightLowerLegTracker,
			).firstNotNullOfOrNull { it }
		}

	private val fullResetTracker: Tracker?
		get() {
			return arrayOf(
				skeleton.leftUpperLegTracker,
				skeleton.leftLowerLegTracker,
			).firstNotNullOfOrNull { it }
		}

	private val yawResetTracker: Tracker?
		get() {
			return arrayOf(
				skeleton.upperChestTracker,
				skeleton.chestTracker,
				skeleton.hipTracker,
				skeleton.waistTracker,
			).firstNotNullOfOrNull { it }
		}

	companion object {
		const val RESET_SOURCE_NAME: String = "TapDetection"
	}
}
