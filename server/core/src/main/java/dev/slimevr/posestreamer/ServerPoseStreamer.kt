package dev.slimevr.posestreamer

import dev.slimevr.VRServer
import dev.slimevr.tracking.processor.skeleton.HumanSkeleton
import dev.slimevr.util.ann.VRServerThread

class ServerPoseStreamer(val server: VRServer) : TickPoseStreamer(server.humanPoseManager.skeleton) {

	init {
		// Register callbacks/events
		server.addSkeletonUpdatedCallback { skeleton: HumanSkeleton? ->
			this.onSkeletonUpdated(
				skeleton,
			)
		}
		server.addOnTick { this.tick() }
	}

	@VRServerThread
	fun onSkeletonUpdated(skeleton: HumanSkeleton?) {
		if (skeleton != null) {
			this.skeleton = skeleton
		}
	}

	@VRServerThread
	fun tick() {
		super.tick(server.fpsTimer.timePerFrame)
	}
}
