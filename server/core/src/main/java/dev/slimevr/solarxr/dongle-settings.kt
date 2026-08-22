package dev.slimevr.solarxr

import dev.slimevr.VRServer
import dev.slimevr.hid.HIDReceiverActions
import kotlinx.coroutines.flow.launchIn
import solarxr_protocol.rpc.ChangeDongleSettingsRequest

class DongleSettingsBehaviour(
	private val server: VRServer,
) : SolarXRBridgeBehaviour {
	override fun observe(receiver: SolarXRBridge) {
		receiver.rpcDispatcher.on<ChangeDongleSettingsRequest> { req ->
			val id = req.dongleId ?: return@on
			val dongle = server.context.state.value.dongles[id.toInt()]
				?: return@on

			dongle.context.dispatch(
				HIDReceiverActions.SetCustomName(req.displayName),
			)
		}.launchIn(receiver.context.scope)
	}
}
