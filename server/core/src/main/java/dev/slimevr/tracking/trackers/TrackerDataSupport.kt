package dev.slimevr.tracking.trackers

// Note: there is a -1 offset when receiving via UDP, thus ROTATION must be sent as 0 from firmware.
enum class TrackerDataSupport(val id: Int) {
	OTHER(0),
	ROTATION(1),
	FLEX_RESISTANCE(2),
	FLEX_ANGLE(3),
	;

	companion object {
		private val byId = TrackerDataSupport.entries.associateBy { it.id }

		@JvmStatic
		fun getById(id: Int): TrackerDataSupport? = TrackerDataSupport.byId[id]
	}
}
