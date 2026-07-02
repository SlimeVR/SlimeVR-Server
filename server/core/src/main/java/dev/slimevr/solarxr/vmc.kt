package dev.slimevr.solarxr

import dev.slimevr.config.VMCConfig
import dev.slimevr.vmc.VMCActions
import dev.slimevr.vmc.VMCManager
import solarxr_protocol.rpc.ChangeVMCOSCSettingsRequest
import solarxr_protocol.rpc.VMCOSCSettingsRequest
import solarxr_protocol.rpc.VMCOSCSettingsResponse

class VmcBehaviour(
	private val vmcManager: VMCManager,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		receiver.rpcDispatcher.on<VMCOSCSettingsRequest> {
			receiver.sendRpc(buildVmcOscSettings(vmcManager.context.state.value.config))
		}

		val oldConfig = vmcManager.context.state.value.config
		receiver.rpcDispatcher.on<ChangeVMCOSCSettingsRequest> { req ->
			vmcManager.context.dispatch(
				VMCActions.UpdateConfig(
					VMCConfig(
						enabled = req.enabled == true,
						portIn = req.portIn?.toInt() ?: oldConfig.portIn,
						portOut = req.portOut?.toInt() ?: oldConfig.portOut,
						address = req.address ?: oldConfig.address,
						mirrorTracking = req.mirrorTracking == true,
						anchorAtHips = req.anchorHip == true,
					),
				),
			)
		}
	}

	private fun buildVmcOscSettings(config: VMCConfig): VMCOSCSettingsResponse = VMCOSCSettingsResponse(
		enabled = config.enabled,
		portIn = config.portIn.toUShort(),
		portOut = config.portOut.toUShort(),
		address = config.address,
		anchorHip = config.anchorAtHips,
		mirrorTracking = config.mirrorTracking,
	)
}
