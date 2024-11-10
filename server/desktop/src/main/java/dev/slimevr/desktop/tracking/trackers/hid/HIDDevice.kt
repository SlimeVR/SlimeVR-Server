package dev.slimevr.desktop.tracking.trackers.hid

import dev.slimevr.tracking.trackers.Device
import dev.slimevr.tracking.trackers.Tracker
import dev.slimevr.tracking.trackers.udp.BoardType
import dev.slimevr.tracking.trackers.udp.MCUType

class HIDDevice(val hidId: Int) : Device() {
	override var hardwareIdentifier: String = "Unknown"
	override var boardType: BoardType = BoardType.UNKNOWN
	override var mcuType: MCUType = MCUType.UNKNOWN
	fun getTracker(id: Int): Tracker? = trackers[id]
}
