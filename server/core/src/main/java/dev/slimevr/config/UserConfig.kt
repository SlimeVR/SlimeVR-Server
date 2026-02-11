package dev.slimevr.config

import kotlinx.serialization.Serializable

@Serializable
data class UserConfig(
	val skeleton: SkeletonConfig = SkeletonConfig(),
	private val trackers: Map<String, TrackerConfig> = HashMap(),
	val version: Int = CONFIG_VERSION,
) {
	companion object {
		const val CONFIG_VERSION = 1
	}
}
