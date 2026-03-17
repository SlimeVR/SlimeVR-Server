package dev.slimevr.tracker

import dev.slimevr.context.Context
import dev.slimevr.context.BasicModule
import io.github.axisangles.ktmath.Quaternion

data class TrackerState(
	val id: Int,
	val name: String,
	val rawRotation: Quaternion,
)

sealed interface TrackerActions {
	data class UpdateRotation(val rotation: Quaternion)
}

typealias TrackerContext = Context<TrackerState, TrackerActions>
typealias TrackerModule = BasicModule<TrackerState, TrackerActions>
