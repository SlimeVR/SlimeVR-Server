package dev.slimevr.desktop.tracking.trackers.hid

import dev.slimevr.tracking.trackers.Device
import dev.slimevr.tracking.trackers.Tracker

class HIDDevice(val hidId: Int) : Device() {
	fun getTracker(id: Int): Tracker? = trackers[id]
}
