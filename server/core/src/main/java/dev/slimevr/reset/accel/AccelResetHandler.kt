package dev.slimevr.reset.accel

import dev.slimevr.tracking.trackers.Tracker
import dev.slimevr.util.AccelAccumulator
import io.eiren.util.logging.LogManager
import io.github.axisangles.ktmath.Vector3
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.schedule
import kotlin.concurrent.thread
import kotlin.concurrent.withLock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.TimeSource

// Handles recording and processing of acceleration-based session calibration
class AccelResetHandler(val timeSource: TimeSource.WithComparableMarks = TimeSource.Monotonic) {
	var isRunning: Boolean = false
		private set
	var isDetecting: Boolean = false
		private set
	var isRecording: Boolean = false
		private set

	private val recordingLock = ReentrantLock()

	private var hmd: Tracker? = null
	private val trackers: MutableList<RecordingWrapper> = mutableListOf()

	private val timeoutTimer = Timer()
	private var timerTask: TimerTask? = null

	private var recStartTime = timeSource.markNow()

	/**
	 * Starts the accel reset process. performing rest detection on the trackers
	 * provided to automatically control the recording period.
	 */
	fun start(hmd: Tracker, trackers: Iterable<Tracker>) = recordingLock.withLock {
		// Maybe should throw IllegalStateException? Or just restart?
		if (isRunning) return

		// Nothing to do
		if (trackers.none()) return

		// Initialize our state
		isRunning = true
		this.hmd = hmd

		// Register our tracker event listener
		for (tracker in trackers) {
			val wrappedTracker = RecordingWrapper(tracker)
			this.trackers.add(wrappedTracker)
			tracker.accelTickCallback = {
				onAccelData(wrappedTracker)
			}
		}

		// Start waiting for movement
		isDetecting = true
		timerTask?.cancel()
		timerTask = timeoutTimer.schedule(START_TIMEOUT.inWholeMilliseconds) {
			timeout()
		}

		LogManager.info("[AccelResetHandler] Reset requested, detecting movement...")
	}

	/**
	 * Handles rest detection and data collection.
	 */
	private fun onAccelData(tracker: RecordingWrapper) {
		if (!isDetecting) return

		val sample = tracker.makeSample(timeSource.markNow(), hmd?.position ?: Vector3.NULL)

		// Rest detection
		tracker.updateRestState(sample)
		tracker.addRestSample(sample)
		// TODO: This shouldn't be done like this
		tracker.tracker.accelMountInProgress = isRecording && tracker.moving

		if (!isRecording) {
			// We haven't started moving yet, don't start recording
			if (!tracker.moving) return

			// Start recording
			recordingLock.withLock {
				// Race condition
				if (isRecording) return@withLock

				// Dump rest detection into the recording on tracker threads
				for (tracker in trackers) tracker.dumpRest = true

				isRecording = true
				recStartTime = timeSource.markNow()
				timerTask?.cancel()
				timerTask = timeoutTimer.schedule(RECORD_TIMEOUT.inWholeMilliseconds) {
					timeout()
				}

				LogManager.info("[AccelResetHandler] Movement detected, recording started!")
			}
		} else if (
			timeSource.markNow() - recStartTime > MINIMUM_DURATION &&
			trackers.none { it.moving }
		) {
			// We're recording, the minimum duration has passed, and no trackers are
			//  moving, therefore we can stop the recording and process it
			recordingLock.withLock {
				// Race condition
				if (!isRecording) return
				// Let's not block the tracker thread while processing
				thread {
					process()
				}
			}
			return
		}

		// Take the latest sample or dump the rest detection samples into the recording
		if (!tracker.dumpRest) {
			tracker.recording.add(sample)
		} else {
			tracker.recording.addAll(tracker.restDetect)
			tracker.dumpRest = false
		}
	}

	/**
	 * Stops recording, processes the recorded data, then resets this handler.
	 */
	private fun process() {
		stop()

		LogManager.info("[AccelResetHandler] Done recording, processing...")

		for (tracker in trackers) {
			val firstSample = tracker.recording.first()
			val lastSample = tracker.recording.last()

			// Compute the unbiased final velocity
			val calibAccum = AccelAccumulator()
			RecordingProcessor.processTimeline(calibAccum, tracker)

			// Assume the final velocity is zero (at rest), we can divide our unbiased
			//  final velocity (m/s) by the duration and get a static acceleration
			//  offset (m/s^2)
			val duration = lastSample.time - firstSample.time
			val bias = calibAccum.velocity / duration.toDouble(DurationUnit.SECONDS).toFloat()

			// Compute the biased final offset
			val finalAccum = AccelAccumulator()
			RecordingProcessor.processTimeline(finalAccum, tracker, accelBias = bias)

			// Compute the final offsets
			val trackerOffset = finalAccum.offset
			val trackerXZ = Vector3(trackerOffset.x, 0f, trackerOffset.z)
			val hmdOffset = lastSample.hmdPos - firstSample.hmdPos
			val hmdXZ = Vector3(hmdOffset.x, 0f, hmdOffset.z)

			// TODO: Fail on high error

			// Compute mounting to fix the yaw offset from tracker to HMD
			val mountRot = RecordingProcessor.angle(trackerXZ.unit()) *
				RecordingProcessor.angle(hmdXZ.unit()).inv()

			// Apply that mounting to the tracker
			val resetsHandler = tracker.tracker.resetsHandler
			val finalMounting = resetsHandler.mountingOrientation * resetsHandler.mountRotFix * mountRot
			resetsHandler.mountRotFix *= mountRot

			LogManager.info(
				"[Accel] Tracker ${tracker.tracker.id} (${tracker.tracker.trackerPosition?.designation}):\n" +
					"Tracker offset: $trackerOffset\n" +
					"HMD offset: $hmdOffset\n" +
					"Error value (meters): ${trackerXZ.len() - hmdXZ.len()}\n" +
					"Resulting mounting: $finalMounting",
			)
		}

		clean()
	}

	/**
	 * Stops recording without clearing the recorded data.
	 */
	private fun stop() = recordingLock.withLock {
		// Cancel any pending timeouts
		timerTask?.cancel()
		timerTask = null

		isDetecting = false
		isRecording = false

		// Unregister our tracker event listener
		for (tracker in trackers) {
			tracker.tracker.accelTickCallback = null
			tracker.tracker.accelMountInProgress = false
		}
	}

	/**
	 * Immediately stops execution and resets this handler.
	 */
	private fun clean() {
		stop()

		// Reset data storage
		hmd = null
		trackers.clear()

		isRunning = false
	}

	/**
	 * Stops the accel reset process and resets this handler.
	 */
	fun cancel() {
		clean()
	}

	/**
	 * Indicates that the process has timed out, then resets this handler.
	 */
	private fun timeout() {
		LogManager.warning("[AccelResetHandler] Reset timed out, aborting")
		clean()
	}

	companion object {
		val START_TIMEOUT = 8.seconds
		val MINIMUM_DURATION = 2.seconds
		val RECORD_TIMEOUT = 8.seconds
	}
}
