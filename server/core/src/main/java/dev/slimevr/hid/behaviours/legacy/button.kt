@file:Suppress("DEPRECATION")

package dev.slimevr.hid.behaviours.legacy

import dev.slimevr.hid.HIDDataLegacy
import dev.slimevr.hid.HIDReceiver
import dev.slimevr.hid.HIDReceiverBehaviour
import dev.slimevr.hid.HIDRotationButtonLegacy
import solarxr_protocol.rpc.TapDetectionSetupNotification

class HIDButtonLegacyBehaviour : HIDReceiverBehaviour {
	private val lastButton = mutableMapOf<Int, Int>()

	override fun observe(receiver: HIDReceiver) {
		receiver.packetEvents.on<HIDDataLegacy> { onButton(receiver, it.hidId, it.button) }
			.launchIn(receiver.context.scope)
		receiver.packetEvents.on<HIDRotationButtonLegacy> { onButton(receiver, it.hidId, it.button) }
			.launchIn(receiver.context.scope)
	}

	private suspend fun onButton(receiver: HIDReceiver, hidId: Int, button: Int) {
		val fired = (button and (lastButton[hidId] ?: 0).inv()) and BUTTON_ACTION_MASK != 0
		lastButton[hidId] = button
		if (!fired) return

		if (!receiver.appContext.tapDetectionManager.context.state.value.setupMode) return
		val tracker = receiver.getTracker(hidId) ?: return
		receiver.appContext.server.sendSolarxrRpc(
			TapDetectionSetupNotification(tracker.context.state.value.id.toUShort()),
		)
	}

	companion object {
		private const val BUTTON_ACTION_MASK = 0b11
	}
}
