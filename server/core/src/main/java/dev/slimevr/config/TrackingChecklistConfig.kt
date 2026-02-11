package dev.slimevr.config

import kotlinx.serialization.Serializable

@Serializable
data class TrackingChecklistConfig(
	val ignoredStepsIds: List<Int> = listOf(),
)
