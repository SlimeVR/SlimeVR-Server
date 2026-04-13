package dev.slimevr.tracker

import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsActions
import dev.slimevr.config.TrackerConfig
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.datatypes.BodyPart

private fun restoreFromConfig(state: TrackerState, config: TrackerConfig): TrackerState = state.copy(
	bodyPart = config.bodyPart?.takeIf { it != BodyPart.NONE } ?: state.bodyPart,
	customName = config.customName ?: state.customName,
	mountingOrientation = config.mountingOrientation ?: state.mountingOrientation,
)

private fun stateToConfig(state: TrackerState): TrackerConfig = TrackerConfig(
	bodyPart = state.bodyPart,
	customName = state.customName,
	mountingOrientation = state.mountingOrientation,
)

class TrackerConfigBehaviour(
	private val settings: Settings,
	private val hardwareId: String,
) : TrackerBehaviour {
	override fun observe(receiver: Tracker) {
		val config = settings.context.state.value.data.trackers[hardwareId]
		if (config != null) {
			receiver.context.dispatch(TrackerActions.Update { restoreFromConfig(this, config) })
		}

		receiver.context.state
			.map { stateToConfig(it) }
			.distinctUntilChanged()
			.onEach { config ->
				settings.context.dispatch(SettingsActions.UpdateTracker(hardwareId) { config })
			}
			.launchIn(receiver.context.scope)
	}
}