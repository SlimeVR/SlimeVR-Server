package dev.slimevr.tracking.trackers

enum class TrackerStatus(val id: UByte, val sendData: Boolean, val reset: Boolean) {

	DISCONNECTED(0u, false, true),
	OK(1u, true, false),
	BUSY(2u, true, false),
	ERROR(3u, false, true),
	OCCLUDED(4u, false, false),
	TIMED_OUT(5u, false, false),
	;

	companion object {

		private val byId = entries.associateBy { it.id }

		@JvmStatic
		fun getById(id: UByte): TrackerStatus? = byId[id]
	}
}
