package dev.slimevr.hid.behaviours

import dev.slimevr.config.DevicesConfig
import dev.slimevr.config.DevicesConfigActions
import dev.slimevr.hid.HIDReceiver
import dev.slimevr.hid.HIDReceiverBehaviour
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class HIDReceiverConfigBehaviour(
	private val devices: DevicesConfig,
	private val serialNumber: String,
) : HIDReceiverBehaviour {
	override fun observe(receiver: HIDReceiver) {
		receiver.context.state
			.distinctUntilChangedBy { it.customName }
			.drop(1)
			.onEach { state ->
				devices.context.dispatch(DevicesConfigActions.UpdateDongle(serialNumber) { copy(customName = state.customName) })
			}
			.launchIn(receiver.context.scope)
	}
}
