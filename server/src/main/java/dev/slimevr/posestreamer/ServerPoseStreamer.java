package dev.slimevr.posestreamer;

import dev.slimevr.VRServer;
import dev.slimevr.util.ann.VRServerThread;
import dev.slimevr.tracking.processor.skeleton.HumanSkeleton;


public class ServerPoseStreamer extends TickPoseStreamer {

	protected final VRServer server;

	public ServerPoseStreamer(VRServer server) {
		super(null); // Skeleton is registered later
		this.server = server;

		// Register callbacks/events
		server.addSkeletonUpdatedCallback(this::onSkeletonUpdated);
		server.addOnTick(this::onTick);
	}

	@VRServerThread
	public void onSkeletonUpdated(HumanSkeleton skeleton) {
		this.skeleton = skeleton;
	}

	@VRServerThread
	public void onTick() {
		super.doTick();
	}
}
