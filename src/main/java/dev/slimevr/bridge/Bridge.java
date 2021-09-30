package dev.slimevr.bridge;

import io.eiren.util.ann.VRServerThread;
import io.eiren.vr.trackers.Tracker;

/**
 * Bridge handles sending and recieving tracker data
 * between SlimeVR and other systems like VR APIs (SteamVR, OpenXR, etc),
 * apps and protocols (VMC, WebSocket, TIP). It can create and manage
 * tracker recieved from the <b>remote side</b> or send shared <b>local
 * trackers</b> to the other side.
 */
public interface Bridge {

	@VRServerThread
	public void dataRead();

	@VRServerThread
	public void dataWrite();
	
	/**
	 * Adds shared tracker to the bridge. Bridge should notify the
	 * other side of this tracker, if it's the type of tracker
	 * this bridge serves, and start sending data each update
	 * @param tracker
	 */
	@VRServerThread
	public void addSharedTracker(Tracker tracker);
	
	/**
	 * Removes tracker from a bridge. If the other side supports
	 * tracker removal, bridge should notify it and stop sending
	 * new data. If it doesn't support tracker removal, the bridge
	 * can either stop sending new data, or keep sending it if it's
	 * available.
	 * @param tracker
	 */
	@VRServerThread
	public void removeSharedTracker(Tracker tracker);

	@VRServerThread
	public void startBridge();
}
