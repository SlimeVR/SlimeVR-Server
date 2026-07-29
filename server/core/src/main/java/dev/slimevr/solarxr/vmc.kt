package dev.slimevr.solarxr

import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsActions
import kotlinx.coroutines.flow.launchIn
import solarxr_protocol.rpc.ChangeVMCOSCSettingsRequest
import solarxr_protocol.rpc.ChangeVRMSettingsRequest
import solarxr_protocol.rpc.VMCOSCSettingsRequest
import solarxr_protocol.rpc.VMCOSCSettingsResponse
import solarxr_protocol.rpc.VRMSettingsRequest
import solarxr_protocol.rpc.VRMSettingsResponse

class VmcBehaviour(
	private val settings: Settings,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		// Send VMC config
		receiver.rpcDispatcher.on<VMCOSCSettingsRequest> {
			val config = settings.context.state.value.data.vmcConfig
			receiver.sendRpc(
				VMCOSCSettingsResponse(
					enabled = config.enabled,
					portIn = config.portIn.toUShort(),
					portOut = config.portOut.toUShort(),
					address = config.address,
					anchorHip = config.anchorAtHips,
					mirrorTracking = config.mirrorTracking,
				),
			)
		}.launchIn(receiver.context.scope)

		// Send VRM json
		receiver.rpcDispatcher.on<VRMSettingsRequest> {
			val config = settings.context.state.value.data.vmcConfig
			receiver.sendRpc(
				VRMSettingsResponse(
					vrmJson = config.vrmJson,
				),
			)
		}.launchIn(receiver.context.scope)

		// Receive VMC config
		receiver.rpcDispatcher.on<ChangeVMCOSCSettingsRequest> { req ->
			settings.context.dispatch(
				SettingsActions.Update {
					copy(
						vmcConfig = vmcConfig.copy(
							enabled = req.enabled == true,
							portIn = req.portIn?.toInt() ?: vmcConfig.portIn,
							portOut = req.portOut?.toInt() ?: vmcConfig.portOut,
							address = req.address ?: vmcConfig.address,
							mirrorTracking = req.mirrorTracking == true,
							anchorAtHips = req.anchorHip == true,
						),
					)
				},
			)
		}.launchIn(receiver.context.scope)

		// Receive VRM json
		receiver.rpcDispatcher.on<ChangeVRMSettingsRequest> { req ->
			settings.context.dispatch(
				SettingsActions.Update {
					copy(
						vmcConfig = vmcConfig.copy(vrmJson = req.vrmJson),
					)
				},
			)
		}.launchIn(receiver.context.scope)
	}
}
