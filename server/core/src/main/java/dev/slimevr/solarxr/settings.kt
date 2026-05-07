package dev.slimevr.solarxr

import dev.slimevr.vmc.VMCManager
import solarxr_protocol.rpc.SettingsRequest
import solarxr_protocol.rpc.SettingsResponse

// Central handler for SettingsRequest. Aggregates per-feature settings into one SettingsResponse
// so the GUI receives a single response. Each feature builder lives next to its behaviour
// (e.g. buildVmcOscSettings in vmc.kt).
// TODO: Would be nice to split settings based on their solarxr behaviour.
// TODO: Almost all build functions could be their own request. making it easier to work with
class SettingsBehaviour(
	private val vmcManager: VMCManager,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		receiver.rpcDispatcher.on<SettingsRequest> {
			receiver.sendRpc(
				SettingsResponse(
					vmcOsc = buildVmcOscSettings(vmcManager.context.state.value.config),
				),
			)
		}
	}
}
