package dev.slimevr.vrcosc

import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsActions
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class VRCOSCSettingsBehaviour(
	private val settings: Settings,
) : VRCOSCBehaviour {
	override fun reduce(state: VRCOSCState, action: VRCOSCActions) = when (action) {
		is VRCOSCActions.UpdateConfig -> state.copy(config = action.config)
		else -> state
	}

	override fun observe(receiver: VRCOSCManager) {
		receiver.context.state
			.map { state -> state.config }
			.distinctUntilChanged()
			.onEach { config ->
				settings.context.dispatch(SettingsActions.Update { copy(vrcOscConfig = config) })
			}.launchIn(receiver.context.scope)
	}
}
