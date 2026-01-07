package dev.slimevr.config

import kotlinx.serialization.Serializable

@Serializable
class TrackingChecklistConfig {
	val ignoredStepsIds: MutableList<Int> = mutableListOf()
}
