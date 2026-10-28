package dev.slimevr.hid.behaviours.v3

import dev.slimevr.hid.HIDButton
import dev.slimevr.hid.HIDReceiver
import dev.slimevr.hid.HIDReceiverBehaviour
import dev.slimevr.tapdetection.TapDetectionManager
import solarxr_protocol.rpc.TapDetectionSetupNotification

class HIDButtonV3Behaviour(private val tapDetectionManager: TapDetectionManager) : HIDReceiverBehaviour {
	override fun observe(receiver: HIDReceiver) {
		receiver.packetEvents.on<HIDButton> { packet ->
			val tracker = receiver.getTracker(packet.hidId) ?: return@on
			tapDetectionManager.actionTriggered(tracker.context.state.value.id)
		}.launchIn(receiver.context.scope)
	}
}
