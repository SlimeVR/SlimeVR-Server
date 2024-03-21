package dev.slimevr.filtering

import java.util.*

enum class TrackerFilters(val id: Int, val configKey: String) {
	NONE(0, "none"),
	SMOOTHING(1, "smoothing"),
	PREDICTION(2, "prediction"),
	;

	companion object {
		private val byConfigkey: MutableMap<String, TrackerFilters> = HashMap()

		init {
			for (configVal in values()) {
				byConfigkey[configVal.configKey.lowercase(Locale.getDefault())] =
					configVal
			}
		}

		val values = values()

		@JvmStatic
		fun fromId(id: Int): TrackerFilters? {
			for (filter in values) {
				if (filter.id == id) return filter
			}
			return null
		}

		@JvmStatic
		fun getByConfigkey(configKey: String?): TrackerFilters? = if (configKey == null) null else byConfigkey[configKey.lowercase(Locale.getDefault())]
	}
}
