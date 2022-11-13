package dev.slimevr.posestreamer;

import dev.slimevr.VRServer;
import dev.slimevr.util.ann.VRServerThread;
import dev.slimevr.vr.processor.skeleton.Skeleton;


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
	public void onSkeletonUpdated(Skeleton skeleton) {
		this.skeleton = skeleton;
	}

	@VRServerThread
	public void onTick() {
		super.doTick();
	}
}
