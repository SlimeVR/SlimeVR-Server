package dev.slimevr.solarxr

import dev.slimevr.VRServer
import dev.slimevr.vrchat.VRCConfigActions
import dev.slimevr.vrchat.VRCConfigManager
import dev.slimevr.vrchat.computeRecommendedValues
import dev.slimevr.vrchat.computeValidity
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.rpc.VRCConfigSettingToggleMute
import solarxr_protocol.rpc.VRCConfigStateChangeResponse
import solarxr_protocol.rpc.VRCConfigStateRequest

class VrcBehaviour(
	private val vrcManager: VRCConfigManager,
	private val server: VRServer,
	private val userHeight: () -> Double,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		fun buildCurrentResponse(): VRCConfigStateChangeResponse {
			val state = vrcManager.context.state.value
			val values = state.currentValues
			if (!state.isSupported || values == null) return VRCConfigStateChangeResponse(isSupported = false)
			val recommended = computeRecommendedValues(server, userHeight())
			return VRCConfigStateChangeResponse(
				isSupported = true,
				validity = computeValidity(values, recommended),
				state = values,
				recommended = recommended,
				muted = state.mutedWarnings.toList(),
			)
		}

		// Drop the initial value, we only want to push updates when the config changes
		vrcManager.context.state.drop(1).onEach {
			receiver.sendRpc(buildCurrentResponse())
		}.launchIn(receiver.context.scope)

		receiver.rpcDispatcher.on<VRCConfigStateRequest> {
			receiver.sendRpc(buildCurrentResponse())
		}

		receiver.rpcDispatcher.on<VRCConfigSettingToggleMute> { req ->
			val key = req.key ?: return@on
			vrcManager.context.dispatch(VRCConfigActions.ToggleMutedWarning(key))
		}
	}
}
