package dev.slimevr.solarxr

import dev.slimevr.VRServer
import dev.slimevr.provisioning.ProvisioningManager
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.rpc.StartWifiProvisioningRequest
import solarxr_protocol.rpc.StopWifiProvisioningRequest
import solarxr_protocol.rpc.WifiProvisioningStatusResponse

class ProvisioningBehaviour(
	private val server: VRServer,
	private val provisioningManager: ProvisioningManager,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		receiver.rpcDispatcher.on<StartWifiProvisioningRequest> { event ->
			val ssid = event.ssid ?: return@on

			provisioningManager.startProvisioning(
				server,
				ssid,
				event.password,
				event.port,
			)
		}

		receiver.rpcDispatcher.on<StopWifiProvisioningRequest> {
			provisioningManager.stopProvisioning()
		}

		provisioningManager.context.state.drop(1).onEach { state ->
			receiver.sendRpc(
				WifiProvisioningStatusResponse(
					status = state.status,
				),
			)
		}.launchIn(receiver.context.scope)
	}
}
