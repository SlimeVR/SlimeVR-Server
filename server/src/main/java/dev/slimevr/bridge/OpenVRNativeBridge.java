package dev.slimevr.bridge;

import dev.slimevr.util.ann.VRServerThread;
import dev.slimevr.vr.trackers.ShareableTracker;


public class OpenVRNativeBridge implements Bridge {

	public OpenVRNativeBridge() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void dataRead() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dataWrite() {
		// TODO Auto-generated method stub

	}

	@Override
	public void addSharedTracker(ShareableTracker tracker) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeSharedTracker(ShareableTracker tracker) {
		// TODO Auto-generated method stub

	}

	@Override
	@VRServerThread
	public void startBridge() {

	}
}
