@file:Suppress("DEPRECATION")

package dev.slimevr.hid.behaviours.legacy

import dev.slimevr.AppContextProvider
import dev.slimevr.hid.HIDDataLegacy
import dev.slimevr.hid.HIDReceiver
import dev.slimevr.hid.HIDReceiverBehaviour
import dev.slimevr.hid.HIDRotationButtonLegacy

class HIDButtonLegacyBehaviour(private val appContext: AppContextProvider) : HIDReceiverBehaviour {
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

		val tracker = receiver.getTracker(hidId) ?: return
		appContext.tapDetectionManager.actionTriggered(tracker.context.state.value.id)
	}

	companion object {
		private const val BUTTON_ACTION_MASK = 0b11
	}
}
