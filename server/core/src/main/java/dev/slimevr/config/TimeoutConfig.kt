package dev.slimevr.config

import dev.slimevr.VRServer
import dev.slimevr.tracking.trackers.Tracker

class TimeoutConfig {
	// Timeout duration in seconds
	var duration = 3.0f

	fun updateTimeoutDuration() {
		if (this.duration.isNaN() || this.duration.isInfinite()) {
			this.duration = 3.0f
		}
		if (this.duration < 1f) {
			this.duration = 1f
		}
		if (this.duration > 1000000000000000f) {
			this.duration = 1000000000000000f
		}
		println("Updated timeout duration to ${this.duration} seconds")
		Tracker.DISCONNECT_MS = (this.duration * 1000L).toLong() + Tracker.TIMEOUT_MS
	}
}
