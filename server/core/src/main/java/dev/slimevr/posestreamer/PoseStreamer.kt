package dev.slimevr.posestreamer

import dev.slimevr.tracking.processor.skeleton.HumanSkeleton
import io.eiren.util.logging.LogManager
import java.io.IOException

open class PoseStreamer {
	protected var frameRecordingInterval: Long = 60L

	@get:Synchronized
	var skeleton: HumanSkeleton? = null
		protected set
	protected var poseFileStream: PoseDataStream? = null

	protected constructor()

	constructor(skeleton: HumanSkeleton?) {
		this.skeleton = skeleton
	}

	@Synchronized
	fun captureFrame() {
		// Make sure the stream is open before trying to write
		val skeleton = skeleton
		val stream = poseFileStream
		if (skeleton == null || stream == null || stream.isClosed) {
			return
		}

		try {
			stream.writeFrame(skeleton)
		} catch (e: Exception) {
			// Handle any exceptions without crashing the program
			LogManager.severe("[PoseStreamer] Exception while saving frame", e)
		}
	}

	@get:Synchronized
	@set:Synchronized
	var frameInterval: Long
		get() = frameRecordingInterval
		set(intervalMs) {
			require(intervalMs >= 1) { "intervalMs must at least have a value of 1" }
			this.frameRecordingInterval = intervalMs
		}

	@Synchronized
	@Throws(IOException::class)
	fun setOutput(poseFileStream: PoseDataStream, intervalMs: Long) {
		this.frameInterval = intervalMs
		this.output = poseFileStream
	}

	@get:Synchronized
	@set:Throws(IOException::class)
	@set:Synchronized
	open var output: PoseDataStream?
		get() = poseFileStream
		set(poseFileStream) {
			val skeleton = skeleton
			requireNotNull(skeleton) { "Unable to initialize stream, skeleton is null" }
			requireNotNull(poseFileStream) { "poseFileStream must not be null" }
			poseFileStream.writeHeader(skeleton, this)
			this.poseFileStream = poseFileStream
		}

	@Synchronized
	@Throws(IOException::class)
	fun closeOutput() {
		val poseFileStream = this.poseFileStream
		if (poseFileStream != null) {
			closeOutput(poseFileStream)
			this.poseFileStream = null
		}
	}

	@Synchronized
	@Throws(IOException::class)
	fun closeOutput(poseFileStream: PoseDataStream) {
		val skeleton = skeleton
		if (skeleton != null) {
			poseFileStream.writeFooter(skeleton)
		}
		poseFileStream.close()
	}
}
