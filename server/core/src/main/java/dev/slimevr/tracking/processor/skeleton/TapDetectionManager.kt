package dev.slimevr.tracking.processor.skeleton

import dev.slimevr.VRServer
import dev.slimevr.tracking.processor.HumanPoseManager
import dev.slimevr.tracking.trackers.Tracker
import dev.slimevr.tracking.trackers.TrackerPosition
import dev.slimevr.tracking.trackers.TrackerUtils
import io.eiren.util.logging.LogManager
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
		LogManager.info(yawResetTracker.toString())
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
			TrackerUtils.getTrackerForSkeleton(server.allTrackers, config.mountingResetTracker),
				if (config.yawResetTracker !== TrackerPosition.RIGHT_UPPER_LEG && config.fullResetTracker !== TrackerPosition.RIGHT_UPPER_LEG) skeleton.rightUpperLegTracker else null,
				if (config.yawResetTracker !== TrackerPosition.RIGHT_LOWER_LEG && config.fullResetTracker !== TrackerPosition.RIGHT_LOWER_LEG) skeleton.rightLowerLegTracker else null
			).firstNotNullOfOrNull { it }
		}

	private val fullResetTracker: Tracker?
		get() {
			return arrayOf(
				TrackerUtils.getTrackerForSkeleton(server.allTrackers, config.fullResetTracker),
				if (config.yawResetTracker !== TrackerPosition.LEFT_UPPER_LEG && config.mountingResetTracker !== TrackerPosition.LEFT_UPPER_LEG) skeleton.leftUpperLegTracker else null,
				if (config.yawResetTracker !== TrackerPosition.LEFT_LOWER_LEG && config.mountingResetTracker !== TrackerPosition.LEFT_LOWER_LEG) skeleton.leftLowerLegTracker else null
			).firstNotNullOfOrNull { it }
		}

	private val yawResetTracker: Tracker?
		get() {
			return arrayOf(
				TrackerUtils.getTrackerForSkeleton(server.allTrackers, config.yawResetTracker),
				if (config.fullResetTracker !== TrackerPosition.UPPER_CHEST && config.mountingResetTracker !== TrackerPosition.UPPER_CHEST) skeleton.upperChestTracker else null,
				if (config.fullResetTracker !== TrackerPosition.CHEST && config.mountingResetTracker !== TrackerPosition.CHEST) skeleton.chestTracker else null,
				if (config.fullResetTracker !== TrackerPosition.HIP && config.mountingResetTracker !== TrackerPosition.HIP) skeleton.hipTracker else null,
				if (config.fullResetTracker !== TrackerPosition.WAIST && config.mountingResetTracker !== TrackerPosition.WAIST) skeleton.waistTracker else null
			).firstNotNullOfOrNull { it }
		}

	companion object {
		const val RESET_SOURCE_NAME: String = "TapDetection"
	}
}
