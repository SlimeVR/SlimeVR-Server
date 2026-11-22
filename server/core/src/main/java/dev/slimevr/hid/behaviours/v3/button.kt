package dev.slimevr.hid.behaviours.v3

import dev.slimevr.AppContextProvider
import dev.slimevr.hid.HIDButton
import dev.slimevr.hid.HIDReceiver
import dev.slimevr.hid.HIDReceiverBehaviour

class HIDButtonV3Behaviour(private val appContext: AppContextProvider) : HIDReceiverBehaviour {
	override fun observe(receiver: HIDReceiver) {
		receiver.packetEvents.on<HIDButton> { packet ->
			val tracker = receiver.getTracker(packet.hidId) ?: return@on
			appContext.tapDetectionManager.actionTriggered(tracker.context.state.value.id)
		}.launchIn(receiver.context.scope)
	}
}
