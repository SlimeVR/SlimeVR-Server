package dev.slimevr.resets

import dev.slimevr.skeleton.boneId
import dev.slimevr.util.timeSource
import solarxr_protocol.rpc.ResetType

private val FEET_BONE_IDS = ResetBodyParts.FEET.mapTo(mutableSetOf()) { it.boneId }

fun reduce(state: ResetsState, action: ResetsActions): ResetsState = when (action) {
	// Clear the states of the `canDoXReset`s to false
	is ResetsActions.ClearResets -> {
		state.copy(
			canDoYawReset = if (ResetType.YAW in action.resetTypes) false else state.canDoYawReset,
			canDoMountingReset = if (ResetType.POSE_MOUNTING in action.resetTypes) false else state.canDoMountingReset,
		)
	}

	// Whenever a reset is finished
	is ResetsActions.EndReset -> when (action.resetType) {
		ResetType.FULL -> state.copy(
			canDoYawReset = true,
			canDoMountingReset = true,
			lastFullResetTime = timeSource.markNow(),
		)

		ResetType.POSE_MOUNTING -> {
			val boneIds = action.boneIds
			val feetOnly = !boneIds.isNullOrEmpty() && boneIds.all { it in FEET_BONE_IDS }
			when {
				feetOnly -> state.copy(feetMountingResetCompleted = true)

				boneIds.isNullOrEmpty() -> state.copy(
					mountingResetCompleted = true,
					feetMountingResetCompleted = action.resetMountingFeet || state.feetMountingResetCompleted,
				)

				else -> state
			}
		}

		ResetType.YAW -> state
	}

	// Mounting calibration was cleared, reset the session completion flags
	is ResetsActions.ClearMountingCompleted -> state.copy(
		mountingResetCompleted = false,
		feetMountingResetCompleted = false,
	)
}
