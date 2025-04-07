package dev.slimevr.posestreamer

import dev.slimevr.TickReducer
import dev.slimevr.tracking.processor.skeleton.HumanSkeleton
import java.io.IOException

open class TickPoseStreamer(skeleton: HumanSkeleton) : PoseStreamer(skeleton) {
	private val ticker = TickReducer({ captureFrame() }, frameInterval)

	@set:Throws(IOException::class)
	@set:Synchronized
	override var output: PoseDataStream?
		get() = super.output
		set(value) {
			super.output = value
			ticker.reset()
		}

	@set:Synchronized
	override var frameInterval: Float
		get() = super.frameInterval
		set(value) {
			super.frameInterval = value
			ticker.interval = value
		}

	fun tick(tickDelta: Float) {
		ticker.tick(tickDelta)
	}
}
