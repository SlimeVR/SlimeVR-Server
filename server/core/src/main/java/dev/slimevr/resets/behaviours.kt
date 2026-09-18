package dev.slimevr.resets

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import solarxr_protocol.rpc.ResetType
import kotlin.time.Duration.Companion.seconds

class ResetsMountingTimeoutBehaviour : ResetsBehaviour {
	val mountingResetTimeout = 120.seconds

	@OptIn(ExperimentalCoroutinesApi::class)
	override fun observe(receiver: ResetsManager) {
		receiver.context.state
			.distinctUntilChangedBy { it.lastFullResetTime }
			.mapLatest {
				delay(mountingResetTimeout)
				receiver.context.dispatch(ResetsActions.ClearResets(listOf(ResetType.POSE_MOUNTING)))
			}
			.launchIn(receiver.context.scope)
	}
}
