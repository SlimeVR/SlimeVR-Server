package dev.slimevr.bridge

import dev.slimevr.tracking.trackers.Tracker
import dev.slimevr.tracking.trackers.TrackerRole
import dev.slimevr.util.ann.VRServerThread

/**
 * Bridge handles sending and receiving tracker data between SlimeVR and other
 * systems like VR APIs (SteamVR, OpenXR, etc), apps and protocols (VMC,
 * WebSocket, TIP). It can create and manage tracker received from the **remote
 * side** or send shared **local trackers** to the other side.
 */
interface Bridge {
	@VRServerThread
	fun dataRead()

	@VRServerThread
	fun dataWrite()

	/**
	 * Adds shared tracker to the bridge. Bridge should notify the other side of
	 * this tracker, if it's the type of tracker this bridge serves, and start
	 * sending data each update
	 *
	 * @param tracker
	 */
	@VRServerThread
	fun addSharedTracker(tracker: Tracker?)

	/**
	 * Removes tracker from a bridge. If the other side supports tracker
	 * removal, bridge should notify it and stop sending new data. If it doesn't
	 * support tracker removal, the bridge can either stop sending new data, or
	 * keep sending it if it's available.
	 *
	 * @param tracker
	 */
	@VRServerThread
	fun removeSharedTracker(tracker: Tracker?)

	@VRServerThread
	fun startBridge()

	fun isConnected(): Boolean
}

interface ISteamVRBridge : Bridge {
	fun getShareSetting(role: TrackerRole): Boolean

	fun changeShareSettings(role: TrackerRole?, share: Boolean)

	fun updateShareSettingsAutomatically(): Boolean

	fun getAutomaticSharedTrackers(): Boolean
	fun setAutomaticSharedTrackers(value: Boolean)
}
