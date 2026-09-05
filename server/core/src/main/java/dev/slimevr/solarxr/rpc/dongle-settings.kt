package dev.slimevr.solarxr.rpc

import dev.slimevr.VRServer
import dev.slimevr.hid.HIDReceiverActions
import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.SolarXRBridgeBehaviour
import solarxr_protocol.rpc.ChangeDongleSettingsRequest

class DongleSettingsBehaviour(
	private val server: VRServer,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		receiver.rpcDispatcher.on<ChangeDongleSettingsRequest> { req ->
			val id = req.dongleId.toInt()
			if (id == 0) return@on
			val dongle = server.context.state.value.dongles[id]
				?: return@on

			dongle.context.dispatch(
				HIDReceiverActions.SetCustomName(req.displayName),
			)
		}.launchIn(receiver.context.scope)
	}
}
