package dev.slimevr.solarxr

import dev.slimevr.vmc.VMCActions
import dev.slimevr.vmc.VMCConfig
import dev.slimevr.vmc.VMCManager
import solarxr_protocol.rpc.ChangeSettingsRequest
import solarxr_protocol.rpc.OSCSettings
import solarxr_protocol.rpc.VMCOSCSettings

class VmcBehaviour(
	private val vmcManager: VMCManager,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		receiver.rpcDispatcher.on<ChangeSettingsRequest> { req ->
			val vmc = req.vmcOsc ?: return@on
			val osc = vmc.oscSettings ?: return@on
			vmcManager.context.dispatch(
				VMCActions.UpdateConfig(
					VMCConfig(
						enabled = osc.enabled,
						portIn = osc.portIn.toInt(),
						portOut = osc.portOut.toInt(),
						address = osc.address.orEmpty(),
						mirrorTracking = vmc.mirrorTracking,
						anchorAtHips = vmc.anchorHip,
						vrmJson = vmc.vrmJson?.ifEmpty { null },
					),
				),
			)
		}
	}
}

fun buildVmcOscSettings(config: VMCConfig): VMCOSCSettings = VMCOSCSettings(
	oscSettings = OSCSettings(
		enabled = config.enabled,
		portIn = config.portIn.toUShort(),
		portOut = config.portOut.toUShort(),
		address = config.address,
	),
	vrmJson = config.vrmJson,
	anchorHip = config.anchorAtHips,
	mirrorTracking = config.mirrorTracking,
)
