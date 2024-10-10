package dev.slimevr.tracking.trackers

enum class TrackerDataType(val id: Int) {
	ROTATION(0),
	FLEX_RESISTANCE(1),
	FLEX_ANGLE(2),
	;

	companion object {
		private val byId = TrackerDataType.entries.associateBy { it.id }

		@JvmStatic
		fun getById(id: Int): TrackerDataType? = TrackerDataType.byId[id]
	}
}
