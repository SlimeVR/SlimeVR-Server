package dev.slimevr.tracking.trackers

enum class TrackerDataSupport(val id: Int) {
	ROTATION(0),
	FLEX_RESISTANCE(1),
	FLEX_ANGLE(2),
	;

	companion object {
		private val byId = TrackerDataSupport.entries.associateBy { it.id }

		@JvmStatic
		fun getById(id: Int): TrackerDataSupport? = TrackerDataSupport.byId[id]
	}
}
