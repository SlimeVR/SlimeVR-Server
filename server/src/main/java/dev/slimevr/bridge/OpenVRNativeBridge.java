package dev.slimevr.bridge;

import dev.slimevr.tracking.trackers.Tracker;
import dev.slimevr.util.ann.VRServerThread;


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
	public void addSharedTracker(Tracker tracker) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeSharedTracker(Tracker tracker) {
		// TODO Auto-generated method stub

	}

	@Override
	@VRServerThread
	public void startBridge() {

	}
}
