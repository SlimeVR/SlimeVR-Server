package dev.slimevr.poseframeformat

import dev.slimevr.VRServer
import dev.slimevr.poseframeformat.trackerdata.TrackerFrames
import dev.slimevr.tracking.trackers.Tracker
import dev.slimevr.util.TickReducer
import dev.slimevr.util.ann.VRServerThread
import io.eiren.util.collections.FastList
import io.eiren.util.logging.LogManager
import org.apache.commons.lang3.tuple.Pair
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.Future
import java.util.function.Consumer

class PoseRecorder(private val server: VRServer) {
	inner class RecordingProgress(val frame: Int, val totalFrames: Int)

	private var poseFrame: PoseFrames? = null
	private var numFrames = -1
	private var frameCursor = 0

	// Default 50 TPS
	private val ticker = TickReducer({ onTick() }, 0.02f)

	private var recordingFuture: CompletableFuture<PoseFrames>? = null
	private var frameCallback: Consumer<RecordingProgress>? = null
	var trackers = FastList<Pair<Tracker, TrackerFrames>>()

	init {
		server.addOnTick {
			if (numFrames > 0) {
				ticker.tick(server.fpsTimer.timePerFrame)
			}
		}
	}

	// Make sure it's synchronized since this is the server thread interacting with
	// an unknown outside thread controlling this class
	@Synchronized
	@VRServerThread
	fun onTick() {
		if (frameCursor >= numFrames) {
			// If done and hasn't yet, send finished recording
			stopFrameRecording()
			return
		}

		// A stopped recording will be accounted for by an empty "trackers" list
		val cursor = frameCursor++
		for (tracker in trackers) {
			// Add a frame for each tracker
			tracker.right.addFrameFromTracker(cursor, tracker.left)
		}

		// Send the number of finished frames
		frameCallback?.accept(RecordingProgress(frameCursor, numFrames))
		// If done, send finished recording
		if (frameCursor >= numFrames) {
			stopFrameRecording()
		}
	}

	@Synchronized
	fun startFrameRecording(
		numFrames: Int,
		interval: Float,
		trackers: List<Tracker?> = server.allTrackers,
		frameCallback: Consumer<RecordingProgress>? = null,
	): Future<PoseFrames> {
		require(numFrames >= 1) { "numFrames must at least have a value of 1." }
		require(interval > 0) { "interval must be greater than 0." }
		require(trackers.isNotEmpty()) { "trackers must have at least one entry." }

		cancelFrameRecording()
		val poseFrame = PoseFrames(trackers.size)
		poseFrame.frameInterval = interval

		// Update tracker list
		this.trackers.ensureCapacity(trackers.size)
		for (tracker in trackers) {
			// Ignore null and internal trackers
			if (tracker == null || tracker.isInternal) {
				continue
			}

			// Create a tracker recording
			val trackerFrames = TrackerFrames(tracker, numFrames)
			poseFrame.frameHolders.add(trackerFrames)

			// Pair tracker with recording
			this.trackers.add(Pair.of(tracker, trackerFrames))
		}
		require(this.trackers.isNotEmpty()) { "trackers must have at least one valid tracker." }

		// Ticking setup
		ticker.interval = interval
		ticker.reset()

		val recordingFuture = CompletableFuture<PoseFrames>()
		this.recordingFuture = recordingFuture
		this.frameCallback = frameCallback

		// Recording setup
		this.poseFrame = poseFrame
		frameCursor = 0
		this.numFrames = numFrames

		LogManager.info(
			"[PoseRecorder] Recording $numFrames samples at a $interval s frame interval",
		)

		return recordingFuture
	}

	@Synchronized
	private fun internalStopFrameRecording(cancel: Boolean) {
		val currentRecording = recordingFuture
		if (currentRecording != null && !currentRecording.isDone) {
			val currentFrames = poseFrame
			if (cancel || currentFrames == null) {
				// If it's supposed to be cancelled or there's actually no recording,
				// then cancel the recording and return nothing
				currentRecording.cancel(true)
			} else {
				// Stop the recording, returning the frames recorded
				currentRecording.complete(currentFrames)
			}
		}
		numFrames = -1
		frameCursor = 0
		trackers.clear()
		poseFrame = null
	}

	@Synchronized
	fun stopFrameRecording() {
		internalStopFrameRecording(false)
	}

	@Synchronized
	fun cancelFrameRecording() {
		internalStopFrameRecording(true)
	}

	val isReadyToRecord: Boolean
		get() = server.trackersCount > 0

	val isRecording: Boolean
		get() = numFrames > frameCursor

	fun hasRecording(): Boolean = recordingFuture != null

	val framesAsync: Future<PoseFrames>?
		get() = recordingFuture

	@get:Throws(ExecutionException::class, InterruptedException::class)
	val frames: PoseFrames?
		get() {
			return recordingFuture?.get()
		}
}
