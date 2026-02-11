package dev.slimevr.config

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
@Serializable
data class SkeletonConfig(
	val toggles: MutableMap<String, Boolean> = HashMap(),
	val values: MutableMap<String, Float> = HashMap(),
	val offsets: MutableMap<String, Float> = HashMap(),
	@JvmField val hmdHeight: Float = 0f,
	@JvmField val floorHeight: Float = 0f,
) {
	@Transient
	val userHeight: Float
		get() = hmdHeight - floorHeight
}
