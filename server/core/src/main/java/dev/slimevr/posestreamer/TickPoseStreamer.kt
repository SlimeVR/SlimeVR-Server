package dev.slimevr.posestreamer

import dev.slimevr.tracking.processor.skeleton.HumanSkeleton
import java.io.IOException

open class TickPoseStreamer(skeleton: HumanSkeleton?) : PoseStreamer(skeleton) {
	protected var nextFrameTimeMs: Long = -1L

	@get:Synchronized
	@set:Throws(IOException::class)
	@set:Synchronized
	override var output: PoseDataStream?
		get() = super.output
		set(value) {
			super.output = value
			nextFrameTimeMs = -1L // Reset the frame timing
		}

	fun doTick() {
		val poseFileStream = this.poseFileStream
		if (poseFileStream == null) {
			return
		}

		val skeleton = this.skeleton
		if (skeleton == null) {
			return
		}

		val curTime = System.currentTimeMillis()
		if (curTime < nextFrameTimeMs) {
			return
		}

		nextFrameTimeMs += frameRecordingInterval

		// To prevent duplicate frames, make sure the frame time is always in
		// the future
		if (nextFrameTimeMs <= curTime) {
			nextFrameTimeMs = curTime + frameRecordingInterval
		}

		captureFrame()
	}
}
