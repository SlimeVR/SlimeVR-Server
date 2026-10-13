package dev.slimevr.hid.behaviours

import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsActions
import dev.slimevr.hid.HIDReceiver
import dev.slimevr.hid.HIDReceiverActions
import dev.slimevr.hid.HIDReceiverBehaviour
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class HIDReceiverConfigBehaviour(
	private val settings: Settings,
	private val serialNumber: String,
) : HIDReceiverBehaviour {
	override fun observe(receiver: HIDReceiver) {
		receiver.context.state
			.distinctUntilChangedBy { it.customName }
			.drop(1)
			.onEach { state ->
				settings.context.dispatch(SettingsActions.UpdateDongle(serialNumber) { copy(customName = state.customName) })
			}
			.launchIn(receiver.context.scope)

		settings.context.state
			.map { it.name }
			.distinctUntilChanged()
			.drop(1)
			.onEach {
				val customName = settings.context.state.value.data.dongles[serialNumber]?.customName
				receiver.context.dispatch(HIDReceiverActions.SetCustomName(customName))
			}
			.launchIn(receiver.context.scope)
	}
}
