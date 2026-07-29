package dev.slimevr.resets

import dev.slimevr.timeSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import solarxr_protocol.rpc.ResetType
import kotlin.time.Duration.Companion.seconds

class ResetsBasicBehaviour : ResetsBehaviour {
	override fun reduce(state: ResetsState, action: ResetsActions) = when (action) {
		// Clear the states of the `canDoXReset`s to false
		is ResetsActions.ClearResets -> {
			state.copy(
				canDoYawReset = if (ResetType.YAW in action.resetType) false else state.canDoYawReset,
				canDoMountingReset = if (ResetType.MOUNTING in action.resetType) false else state.canDoMountingReset,
			)
		}

		// Whenever a reset is finished
		is ResetsActions.EndReset -> when (action.resetType) {
			ResetType.FULL -> state.copy(
				canDoYawReset = true,
				canDoMountingReset = true,
				lastFullResetTime = timeSource.markNow(),
			)

			ResetType.MOUNTING -> {
				val bodyParts = action.bodyParts
				val feetOnly = !bodyParts.isNullOrEmpty() && bodyParts.all { it in FOOT_PARTS }
				when {
					feetOnly -> state.copy(feetMountingResetCompleted = true)

					bodyParts.isNullOrEmpty() -> state.copy(
						mountingResetCompleted = true,
						feetMountingResetCompleted = action.resetMountingFeet || state.feetMountingResetCompleted,
					)

					else -> state
				}
			}

			else -> state
		}

		// Mounting calibration was cleared, reset the session completion flags
		is ResetsActions.ClearMountingCompleted -> state.copy(
			mountingResetCompleted = false,
			feetMountingResetCompleted = false,
		)
	}
}

class ResetsMountingTimeoutBehaviour : ResetsBehaviour {
	val mountingResetTimeout = 120.seconds

	@OptIn(ExperimentalCoroutinesApi::class)
	override fun observe(receiver: ResetsManager) {
		receiver.context.state
			.distinctUntilChangedBy { it.lastFullResetTime }
			.mapLatest {
				delay(mountingResetTimeout)
				receiver.context.dispatch(ResetsActions.ClearResets(listOf(ResetType.MOUNTING)))
			}
			.launchIn(receiver.context.scope)
	}
}
