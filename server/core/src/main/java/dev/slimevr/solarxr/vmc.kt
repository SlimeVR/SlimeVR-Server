package dev.slimevr.solarxr

import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsActions
import dev.slimevr.config.VMCConfig
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
		}

		// Send VRM json
		receiver.rpcDispatcher.on<VRMSettingsRequest> {
			val config = settings.context.state.value.data.vmcConfig
			receiver.sendRpc(
				VRMSettingsResponse(
					vrmJson = config.vrmJson,
				),
			)
		}

		// Receive VMC config
		receiver.rpcDispatcher.on<ChangeVMCOSCSettingsRequest> { req ->
			val oldConfig = settings.context.state.value.data.vmcConfig
			settings.context.dispatch(
				SettingsActions.Update {
					copy(
						vmcConfig = VMCConfig(
							enabled = req.enabled == true,
							portIn = req.portIn?.toInt() ?: oldConfig.portIn,
							portOut = req.portOut?.toInt() ?: oldConfig.portOut,
							address = req.address ?: oldConfig.address,
							mirrorTracking = req.mirrorTracking == true,
							anchorAtHips = req.anchorHip == true,
						),
					)
				},
			)
		}

		// Receive VRM json
		receiver.rpcDispatcher.on<ChangeVRMSettingsRequest> { req ->
			settings.context.dispatch(
				SettingsActions.Update {
					copy(
						vmcConfig = VMCConfig(
							vrmJson = req.vrmJson,
						),
					)
				},
			)
		}
	}
}
