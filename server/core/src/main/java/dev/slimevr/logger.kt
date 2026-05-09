package dev.slimevr

import io.klogging.Level
import io.klogging.config.loggingConfiguration
import io.klogging.logger
import io.klogging.rendering.RENDER_SIMPLE
import io.klogging.sending.STDOUT

object AppLogger {
	val tracker = logger("Tracker")
	val device = logger("Device")
	val udp = logger("UDPConnection")
	val solarxr = logger("SolarXR")
	val steamvr = logger("SteamVR")
	val hid = logger("HID")
	val serial = logger("Serial")
	val firmware = logger("Firmware")
	val vrc = logger("VRChat")
	val bvh = logger("BVH")
	val vmc = logger("VMC")
	val oscQuery = logger("OSCQuery")

	init {
		loggingConfiguration {
			sink("stdout", RENDER_SIMPLE, STDOUT)
			logging {
				fromMinLevel(Level.INFO) {
					toSink("stdout")
				}
			}
		}
	}
}
