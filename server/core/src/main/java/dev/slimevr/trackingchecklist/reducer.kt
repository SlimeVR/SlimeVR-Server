package dev.slimevr.trackingchecklist

fun reduce(state: TrackingChecklistState, action: TrackingChecklistActions): TrackingChecklistState = when (action) {
	is TrackingChecklistActions.UpdateStep -> state.copy(steps = state.steps + (action.id to action.step))
}
