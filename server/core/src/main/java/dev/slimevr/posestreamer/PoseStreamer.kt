package dev.slimevr.posestreamer

import dev.slimevr.tracking.processor.skeleton.HumanSkeleton
import io.eiren.util.logging.LogManager
import java.io.IOException

open class PoseStreamer {
	// 60 FPS
	private var intervalInternal: Float = 1f / 60f
	private var stream: PoseDataStream? = null

	var skeleton: HumanSkeleton? = null
		protected set

	protected constructor()

	constructor(skeleton: HumanSkeleton?) {
		this.skeleton = skeleton
	}

	@Synchronized
	fun captureFrame() {
		// Make sure the stream is open before trying to write
		val skeleton = skeleton
		val stream = stream
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

	open var frameInterval: Float
		get() = intervalInternal
		set(interval) {
			require(interval > 0f) { "interval must be a value greater than 0" }
			this.intervalInternal = interval
		}

	@Synchronized
	@Throws(IOException::class)
	fun setOutput(poseFileStream: PoseDataStream, interval: Float) {
		this.frameInterval = interval
		this.output = poseFileStream
	}

	@set:Throws(IOException::class)
	@set:Synchronized
	open var output: PoseDataStream?
		get() = stream
		set(stream) {
			val skeleton = skeleton
			requireNotNull(skeleton) { "Unable to initialize stream, skeleton is null" }
			requireNotNull(stream) { "stream must not be null" }
			stream.writeHeader(skeleton, this)
			this.stream = stream
		}

	@Synchronized
	@Throws(IOException::class)
	fun closeOutput() {
		val stream = this.stream
		if (stream != null) {
			closeOutput(stream)
			this.stream = null
		}
	}

	@Synchronized
	@Throws(IOException::class)
	fun closeOutput(stream: PoseDataStream) {
		val skeleton = skeleton
		if (skeleton != null) {
			stream.writeFooter(skeleton)
		}
		stream.close()
	}
}
