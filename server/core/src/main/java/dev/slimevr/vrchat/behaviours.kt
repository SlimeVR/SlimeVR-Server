package dev.slimevr.vrchat

import dev.slimevr.config.SettingsActions
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

object DefaultVRCConfigBehaviour : VRCConfigBehaviour {
	override fun reduce(state: VRCConfigState, action: VRCConfigActions) = when (action) {
		is VRCConfigActions.UpdateValues -> state.copy(currentValues = action.values)
		is VRCConfigActions.ToggleMutedWarning -> {
			if (action.key !in VRC_VALID_KEYS) state
			else if (action.key in state.mutedWarnings) state.copy(mutedWarnings = state.mutedWarnings - action.key)
			else state.copy(mutedWarnings = state.mutedWarnings + action.key)
		}
	}

	override fun observe(receiver: VRCConfigManager) {
		receiver.context.state.map { it.mutedWarnings }.distinctUntilChanged().onEach { warnings ->
			receiver.config.settings.context.dispatch(SettingsActions.Update {
				copy(mutedVRCWarnings = warnings)
			})
		}.launchIn(receiver.context.scope)
	}
}