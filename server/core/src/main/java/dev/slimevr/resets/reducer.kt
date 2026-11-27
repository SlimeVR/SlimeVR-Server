package dev.slimevr.resets

import dev.slimevr.util.timeSource
import solarxr_protocol.rpc.ResetType

fun reduce(state: ResetsState, action: ResetsActions): ResetsState = when (action) {
	// Clear the states of the `canDoXReset`s to false
	is ResetsActions.ClearResets -> {
		state.copy(
			canDoYawReset = if (ResetType.YAW in action.resetTypes) false else state.canDoYawReset,
			canDoMountingReset = if (ResetType.MOUNTING in action.resetTypes) false else state.canDoMountingReset,
		)
	}

	// Whenever a reset is finished
	is ResetsActions.EndReset -> when (action.resetType) {
		ResetType.FULL -> state.copy(
			canDoYawReset = true,
			canDoMountingReset = true,
			lastFullResetTime = timeSource.markNow(),
		)

		ResetType.MOUNTING, ResetType.YAW -> state
	}

	is ResetsActions.SetStatus -> state.copy(status = action.status)
}
