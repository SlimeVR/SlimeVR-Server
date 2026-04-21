package dev.slimevr.tracker

import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsActions
import dev.slimevr.config.TrackerConfig
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.MagnetometerStatus

fun restoreFromConfig(state: TrackerState, config: TrackerConfig): TrackerState = state.copy(
	bodyPart = config.bodyPart?.takeIf { it != BodyPart.NONE } ?: state.bodyPart,
	customName = config.customName ?: state.customName,
	mountingOrientation = config.mountingOrientation ?: state.mountingOrientation,
	magStatus = when (config.magEnabled) {
		true -> MagnetometerStatus.ENABLED
		false -> MagnetometerStatus.DISABLED
		null -> state.magStatus
	},
)

private fun applyStateToConfig(config: TrackerConfig, state: TrackerState) = config.copy(
	bodyPart = state.bodyPart,
	customName = state.customName,
	mountingOrientation = state.mountingOrientation,
	magEnabled = when (state.magStatus) {
		MagnetometerStatus.ENABLED -> true
		MagnetometerStatus.DISABLED -> false
		MagnetometerStatus.NOT_SUPPORTED -> config.magEnabled
	},
)

class TrackerConfigBehaviour(
	private val settings: Settings,
	private val hardwareId: String,
) : TrackerBehaviour {
	override fun observe(receiver: Tracker) {
		receiver.context.state
			.distinctUntilChangedBy { it.bodyPart to it.customName to it.mountingOrientation to it.magStatus }
			.drop(1)
			.onEach { state ->
				settings.context.dispatch(SettingsActions.UpdateTracker(hardwareId) { applyStateToConfig(this, state) })
			}
			.launchIn(receiver.context.scope)
	}
}
