package dev.slimevr.bvh

fun reduce(state: BVHState, action: BVHActions): BVHState = when (action) {
	is BVHActions.StartRecording -> state.copy(recording = true, recordingPath = action.path)
	is BVHActions.StopRecording -> state.copy(recording = false, recordingPath = null)
}
