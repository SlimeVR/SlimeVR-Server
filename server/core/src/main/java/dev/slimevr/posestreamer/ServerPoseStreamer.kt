package dev.slimevr.posestreamer

import dev.slimevr.VRServer
import dev.slimevr.tracking.processor.skeleton.HumanSkeleton
import dev.slimevr.util.ann.VRServerThread
import java.util.function.Consumer

class ServerPoseStreamer(val server: VRServer) : TickPoseStreamer(null) {

	init {
		// Register callbacks/events
		server.addSkeletonUpdatedCallback(Consumer { skeleton: HumanSkeleton? -> this.onSkeletonUpdated(skeleton) })
		server.addOnTick(Runnable { this.tick() })
	}

	@VRServerThread
	fun onSkeletonUpdated(skeleton: HumanSkeleton?) {
		this.skeleton = skeleton
	}

	@VRServerThread
	fun tick() {
		super.tick(server.fpsTimer.timePerFrame)
	}
}
