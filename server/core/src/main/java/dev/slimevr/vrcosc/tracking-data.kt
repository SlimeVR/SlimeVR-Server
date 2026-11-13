package dev.slimevr.vrcosc

import dev.slimevr.config.Settings
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import solarxr_protocol.rpc.VRCOSCTrackingDataState

/** How long after the last head/wrist pose we still consider tracking data "flowing". */
private const val TRACKING_TIMEOUT_MS = 3_000L

/** How long after the last inbound OSC message we still consider VRChat "present". */
private const val PRESENCE_TIMEOUT_MS = 5_000L

/**
 * Emits true whenever [source] gets a new timestamp, then false once [timeoutMs] pass
 * without a newer one. `debounce` already coalesces a fast arriving stream (poses can
 * arrive at up to 90 Hz) and re-fires its timeout on every new value, so this is just
 * the immediate-true half wired up next to it.
 */
@OptIn(FlowPreview::class)
private fun freshnessFlow(source: Flow<Long?>, timeoutMs: Long): Flow<Boolean> {
	val timestamps = source.filterNotNull().distinctUntilChanged()
	return merge(timestamps.map { true }, timestamps.debounce(timeoutMs).map { false })
		.onStart { emit(false) }
		.distinctUntilChanged()
}

class VRCOSCTrackingDataBehaviour(
	private val settings: Settings,
) : VRCOSCBehaviour {
	override fun observe(receiver: VRCOSCManager) {
		combine(
			settings.context.state.map { state -> state.data.vrcOscConfig.enabled }.distinctUntilChanged(),
			freshnessFlow(receiver.context.state.map { state -> state.status.lastReceivedTrackingMillis }, TRACKING_TIMEOUT_MS),
			freshnessFlow(receiver.context.state.map { state -> state.status.lastReceivedInputMillis }, PRESENCE_TIMEOUT_MS),
			receiver.context.state.map { state -> state.status.discoveredTargets.isNotEmpty() }.distinctUntilChanged(),
		) { enabled, trackingFresh, inputFresh, oscQueryFound ->
			when {
				!enabled -> VRCOSCTrackingDataState.UNKNOWN
				trackingFresh -> VRCOSCTrackingDataState.RECEIVED
				inputFresh || oscQueryFound -> VRCOSCTrackingDataState.DISABLED_IN_VRCHAT
				else -> VRCOSCTrackingDataState.UNKNOWN
			}
		}
			.distinctUntilChanged()
			.onEach { state -> receiver.context.dispatch(VRCOSCActions.SetTrackingDataState(state)) }
			.launchIn(receiver.context.scope)
	}
}
