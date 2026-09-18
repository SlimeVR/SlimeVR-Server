package dev.slimevr.vrchat

import dev.slimevr.config.SettingsActions
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class DefaultVRCConfigBehaviour : VRCConfigBehaviourType {
	override fun observe(receiver: VRCConfigManager) {
		receiver.context.state.map { it.mutedWarnings }.distinctUntilChanged().onEach { warnings ->
			receiver.config.settings.context.dispatch(
				SettingsActions.Update {
					copy(mutedVRCWarnings = warnings)
				},
			)
		}.launchIn(receiver.context.scope)
	}
}
