package dev.slimevr.tracking.videocalibration

import dev.slimevr.VRServer
import dev.slimevr.protocol.GenericConnection
import dev.slimevr.tracking.trackers.TrackerStatus
import dev.slimevr.tracking.videocalibration.networking.MDNSRegistry
import dev.slimevr.tracking.videocalibration.sources.HumanPoseSource
import dev.slimevr.tracking.videocalibration.sources.PhoneWebcamSource
import dev.slimevr.tracking.videocalibration.sources.SnapshotsDatabase
import dev.slimevr.tracking.videocalibration.sources.TrackersSource
import dev.slimevr.tracking.videocalibration.steps.Step
import dev.slimevr.tracking.videocalibration.steps.VideoCalibrator
import dev.slimevr.tracking.videocalibration.util.DebugOutput
import io.eiren.util.logging.LogManager
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

class VideoCalibrationService(
	private val server: VRServer,
	webcamService: MDNSRegistry.Service,
	websocket: GenericConnection,
) {
	private val debugOutput = DebugOutput(DebugOutput.DEFAULT_DIR)

	private val webcamSource: PhoneWebcamSource
	private val humanPoseSource: HumanPoseSource
	private val trackersSource: TrackersSource
	private val snapshotsDatabase: SnapshotsDatabase
	private val videoCalibrator: VideoCalibrator

	private var timeoutInstant = TimeSource.Monotonic.markNow()

	init {
		webcamSource =
			PhoneWebcamSource(webcamService, debugOutput)

		humanPoseSource =
			HumanPoseSource(
				webcamSource.imageSnapshots,
				server.webRTCManager,
				debugOutput,
			)

		val trackersToRecord =
			server.allTrackers
				.filter {
					!it.isInternal &&
						it.trackerPosition != null &&
						it.status == TrackerStatus.OK
				}
				.associateBy { it.trackerPosition!! }

		trackersSource =
			TrackersSource(
				trackersToRecord,
				TRACKERS_SNAPSHOT_INTERVAL,
				debugOutput,
			)

		snapshotsDatabase =
			SnapshotsDatabase(
				TRACKERS_SNAPSHOT_INTERVAL * 2.0,
				humanPoseSource.humanPoseSnapshots,
				trackersSource.trackersSnapshots,
			)

		val trackersToReset = trackersToRecord.filter { it.value.isImu() }

		videoCalibrator =
			VideoCalibrator(
				trackersToReset,
				server.humanPoseManager.skeletonConfigManager,
				snapshotsDatabase,
				websocket,
				debugOutput,
			)
	}

	/**
	 * Starts calibration.
	 */
	fun start(timeout: Duration) {
		LogManager.info("Starting video calibration...")

		if (server.videoCalibrationService != null) {
			error("Video calibration is already started")
		}

		server.videoCalibrationService = this

		timeoutInstant = TimeSource.Monotonic.markNow() + timeout

		webcamSource.start()
		humanPoseSource.start()
		trackersSource.start()
		videoCalibrator.start()
	}

	/**
	 * Must be called on each server tick.
	 */
	fun onTick() {
		if (TimeSource.Monotonic.markNow() >= timeoutInstant) {
			LogManager.warning("Video calibration timed out")
			this.requestStop()
			return
		}

		if (webcamSource.status.get() == PhoneWebcamSource.Status.DONE ||
			humanPoseSource.status.get() == HumanPoseSource.Status.DONE ||
			trackersSource.status == TrackersSource.Status.DONE ||
			videoCalibrator.step.get() == Step.DONE
		) {
			this.requestStop()
			return
		}

		trackersSource.onTick()
	}

	/**
	 * Stops the calibration.
	 */
	fun requestStop() {
		LogManager.info("Stopping video calibration...")

		server.videoCalibrationService = null

		webcamSource.requestStop()
		trackersSource.requestStop()
		humanPoseSource.requestStop()
		videoCalibrator.requestStop()
	}

	companion object {
		private val TRACKERS_SNAPSHOT_INTERVAL = 1.seconds / 120.0
	}
}
