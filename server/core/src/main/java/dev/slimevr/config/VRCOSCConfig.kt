package dev.slimevr.config

import dev.slimevr.tracking.trackers.TrackerRole
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
class VRCOSCConfig : OSCConfig() {

	// Which trackers' data to send
// 	@JsonDeserialize(using = BooleanMapDeserializer::class)
// 	@JsonSerialize(keyUsing = StdKeySerializers.StringKeySerializer::class)
	var trackers: MutableMap<String, Boolean> = HashMap()

	var oscqueryEnabled: Boolean = true

	fun getOSCTrackerRole(role: TrackerRole, def: Boolean): Boolean = trackers.getOrDefault(role.name.lowercase(Locale.getDefault()), def)

	fun setOSCTrackerRole(role: TrackerRole, value: Boolean) {
		trackers[role.name.lowercase(Locale.getDefault())] = value
	}

	init {
		portIn = 9001
		portOut = 9000

		setOSCTrackerRole(
			TrackerRole.WAIST,
			getOSCTrackerRole(TrackerRole.WAIST, true),
		)
		setOSCTrackerRole(
			TrackerRole.LEFT_FOOT,
			getOSCTrackerRole(TrackerRole.WAIST, true),
		)
		setOSCTrackerRole(
			TrackerRole.RIGHT_FOOT,
			getOSCTrackerRole(TrackerRole.WAIST, true),
		)
	}
}
