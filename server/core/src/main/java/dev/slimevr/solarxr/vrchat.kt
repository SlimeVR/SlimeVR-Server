package dev.slimevr.solarxr

import dev.slimevr.vrchat.VRCConfigActions
import dev.slimevr.vrchat.computeRecommendedValues
import dev.slimevr.vrchat.computeValidity
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.rpc.VRCConfigSettingToggleMute
import solarxr_protocol.rpc.VRCConfigStateChangeResponse
import solarxr_protocol.rpc.VRCConfigStateRequest

val VRCBehaviour = SolarXRConnectionBehaviour(
	observer = { conn ->
		val vrcManager = conn.serverContext.vrcConfigManager

		fun buildCurrentResponse(): VRCConfigStateChangeResponse {
			val state = vrcManager.context.state.value
			val values = state.currentValues
			if (!state.isSupported || values == null) return VRCConfigStateChangeResponse(isSupported = false)
			val recommended = computeRecommendedValues(conn.serverContext, vrcManager.userHeight())
			return VRCConfigStateChangeResponse(
				isSupported = true,
				validity = computeValidity(values, recommended),
				state = values,
				recommended = recommended,
				muted = state.mutedWarnings.toList(),
			)
		}

		// Note here that we drop the first one here
		// that is because we don't need the initial value
		// we just want to send new response when the vrch config change
		vrcManager.context.state.drop(1).onEach {
			conn.sendRpc(buildCurrentResponse())
		}.launchIn(conn.context.scope)

		conn.rpcDispatcher.on<VRCConfigStateRequest> {
			conn.sendRpc(buildCurrentResponse())
		}

		conn.rpcDispatcher.on<VRCConfigSettingToggleMute> { req ->
			val key = req.key ?: return@on
			vrcManager.context.dispatch(VRCConfigActions.ToggleMutedWarning(key))
		}
	},
)
