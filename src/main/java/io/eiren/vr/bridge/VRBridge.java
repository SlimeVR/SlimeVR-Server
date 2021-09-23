package io.eiren.vr.bridge;

import io.eiren.vr.trackers.Tracker;

public interface VRBridge {

	public void dataRead();
	
	public void dataWrite();
	
	/**
	 * Adds shared tracker to the bridge. Bridge should notify the
	 * other side of this tracker, if it's the type of tracker
	 * this bridge serves, and start sending data each update
	 * @param tracker
	 */
	public void addSharedTracker(Tracker tracker);
	
	/**
	 * Removes tracker from a bridge. If the other side supports
	 * tracker removal, bridge should notify it and stop sending
	 * new data. If it doesn't support tracker removal, the bridge
	 * can either stop sending new data, or keep sending it if it's
	 * available.
	 * @param tracker
	 */
	public void removeSharedTracker(Tracker tracker);
}
