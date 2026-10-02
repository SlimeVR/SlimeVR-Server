package dev.slimevr.hid.behaviours.v3

import dev.slimevr.hid.HIDButton
import dev.slimevr.hid.HIDReceiver
import dev.slimevr.hid.HIDReceiverBehaviour
import solarxr_protocol.rpc.TapDetectionSetupNotification

class HIDButtonV3Behaviour : HIDReceiverBehaviour {
	override fun observe(receiver: HIDReceiver) {
		receiver.packetEvents.on<HIDButton> { packet ->
			if (!receiver.appContext.tapDetectionManager.context.state.value.setupMode) return@on
			val tracker = receiver.getTracker(packet.hidId) ?: return@on
			receiver.appContext.server.sendSolarxrRpc(
				TapDetectionSetupNotification(tracker.context.state.value.id.toUShort()),
			)
		}.launchIn(receiver.context.scope)
	}
}
