package dev.slimevr.tracking.trackers

enum class TrackerStatus(val id: Int, val sendData: Boolean) {

	DISCONNECTED(0, false),
	OK(1, true),
	BUSY(2, true),
	ERROR(3, false),
	OCCLUDED(4, false),
	;

	companion object {

		private val byId = values().associateBy { it.id }

		@JvmStatic
		fun getById(id: Int): TrackerStatus? = byId[id]
	}
}
