package dev.slimevr.tracking.trackers

enum class FlexSupport(val id: Int) {
	NONE(0),
	RESISTANCE(1),
	ANGLE(2),
	;

	companion object {
		private val byId = FlexSupport.entries.associateBy { it.id }

		@JvmStatic
		fun getById(id: Int): FlexSupport? = FlexSupport.byId[id]
	}
}
