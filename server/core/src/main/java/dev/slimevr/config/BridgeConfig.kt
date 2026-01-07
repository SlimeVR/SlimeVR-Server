package dev.slimevr.config

import dev.slimevr.tracking.trackers.TrackerRole
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
class BridgeConfig {
	var trackers: MutableMap<String, Boolean> = HashMap<String, Boolean>()
	var automaticSharedTrackersToggling: Boolean = true

	fun getBridgeTrackerRole(role: TrackerRole, def: Boolean): Boolean = trackers.getOrDefault(role.name.lowercase(Locale.getDefault()), def)!!

	fun setBridgeTrackerRole(role: TrackerRole, `val`: Boolean) {
		this.trackers[role.name.lowercase(Locale.getDefault())] = `val`
	}
}
