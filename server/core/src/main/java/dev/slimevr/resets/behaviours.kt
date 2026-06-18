package dev.slimevr.resets

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import solarxr_protocol.rpc.ResetType

const val MOUNTING_RESET_TIMEOUT = 120 * 1000L // 120 seconds

class ResetsBasicBehaviour : ResetsBehaviour {
	override fun reduce(state: ResetsState, action: ResetsActions) = when (action) {
		is ResetsActions.UpdateConfig -> state.copy(config = action.config)

		// Clear the states of the `canDoXReset`s to false
		is ResetsActions.ClearResets -> {
			state.copy(
				canDoYawReset = if (ResetType.Yaw in action.resetType) false else state.canDoYawReset,
				canDoMountingReset = if (ResetType.Mounting in action.resetType) false else state.canDoMountingReset,
			)
		}

		// Whenever a reset is finished
		is ResetsActions.EndReset -> {
			if (action.resetType == ResetType.Full) {
				state.copy(canDoYawReset = true, canDoMountingReset = true, lastFullResetTime = System.nanoTime())
			}
			else state.copy()
		}
	}
}

class ResetsMountingTimeoutBehaviour : ResetsBehaviour {
	@OptIn(ExperimentalCoroutinesApi::class)
	override fun observe(receiver: ResetsManager) {
		receiver.context.state
			.distinctUntilChangedBy { it.lastFullResetTime }
			.mapLatest {
				delay(MOUNTING_RESET_TIMEOUT)
				receiver.context.dispatch(ResetsActions.ClearResets(listOf(ResetType.Mounting)))
			}
			.launchIn(receiver.context.scope)
	}
}
