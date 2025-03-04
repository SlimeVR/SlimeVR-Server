package dev.slimevr.config

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.StdKeySerializers
import dev.slimevr.config.serializers.BooleanMapDeserializer
import dev.slimevr.tracking.trackers.TrackerRole
import java.util.*

class VRCOSCConfig : OSCConfig() {

	// Which trackers' data to send
	@JsonDeserialize(using = BooleanMapDeserializer::class)
	@JsonSerialize(keyUsing = StdKeySerializers.StringKeySerializer::class)
	var trackers: MutableMap<String, Boolean> = HashMap()

	var oscqueryEnabled: Boolean = true

	fun getOSCTrackerRole(role: TrackerRole, def: Boolean): Boolean = trackers.getOrDefault(role.name.lowercase(Locale.getDefault()), def)

	fun setOSCTrackerRole(role: TrackerRole, `val`: Boolean) {
		trackers[role.name.lowercase(Locale.getDefault())] = `val`
	}
}
