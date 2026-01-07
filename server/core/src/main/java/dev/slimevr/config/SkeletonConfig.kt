package dev.slimevr.config

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
@Serializable
class SkeletonConfig {
	var toggles: MutableMap<String, Boolean> = HashMap()

	var values: MutableMap<String, Float> = HashMap()

	var offsets: MutableMap<String, Float> = HashMap()

	@JvmField
	var hmdHeight: Float = 0f

	@JvmField
	var floorHeight: Float = 0f

	@Transient
	val userHeight: Float
		get() = hmdHeight - floorHeight
}
