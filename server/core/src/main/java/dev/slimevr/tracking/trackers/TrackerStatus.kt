package dev.slimevr.tracking.trackers

enum class TrackerStatus(val id: Int, val sendData: Boolean, val reset: Boolean) {

	DISCONNECTED(0, false, true),
	OK(1, true, false),
	BUSY(2, true, false),
	ERROR(3, false, true),
	OCCLUDED(4, false, false),
	TIMED_OUT(5, false, false),
	;

	companion object {

		private val byId = entries.associateBy { it.id }

		@JvmStatic
		fun getById(id: Int): TrackerStatus? = byId[id]
	}
}
