package dev.slimevr.config

import dev.slimevr.vrServer

class DriftCompensationConfig {

	// Is drift compensation enabled
	var enabled = true // TODO

	// Amount of drift compensation applied
	var amount = 0.8f // TODO

	// Max resets for the calculated average drift
	var maxResets = 6 // TODO
	fun updateTrackersDriftCompensation() {
		for (t in vrServer.allTrackers) {
			if (t.allowFiltering) {
				t.resetsHandler.readDriftCompensationConfig(this)
			}
		}
	}
}
