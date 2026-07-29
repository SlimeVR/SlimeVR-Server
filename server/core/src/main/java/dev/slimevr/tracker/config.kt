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

fun restoreFromConfig(state: TrackerState, config: TrackerConfig, saveMountingReset: Boolean): TrackerState = state.copy(
	bodyPart = config.bodyPart?.takeIf { it != BodyPart.NONE } ?: state.bodyPart,
	customName = config.customName ?: state.customName,
	mountingOrientation = config.mountingOrientation,
	sessionCalibration = if (saveMountingReset) {
		config.mountingResetOrientation?.let {
			SessionCalibration(
				headingAlignment = it,
			)
		}
	} else {
		null
	},
	magStatus = when (config.magEnabled) {
		true -> MagnetometerStatus.ENABLED
		false -> MagnetometerStatus.DISABLED
		null -> state.magStatus
	},
)

private fun applyStateToConfig(config: TrackerConfig, state: TrackerState, saveMountingReset: Boolean) = config.copy(
	bodyPart = state.bodyPart,
	customName = state.customName,
	mountingOrientation = state.mountingOrientation,
	mountingResetOrientation = if (saveMountingReset) state.sessionCalibration?.headingAlignment else null,
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
			.distinctUntilChangedBy {
				val saveMountingReset = receiver.settings.context.state.value.data.resetsConfig.saveMountingReset
				val headingAlignment = if (saveMountingReset) it.sessionCalibration?.headingAlignment else null
				it.bodyPart to it.customName to it.mountingOrientation to it.magStatus to headingAlignment
			}
			.drop(1)
			.onEach { state ->
				settings.context.dispatch(SettingsActions.UpdateTracker(hardwareId) { applyStateToConfig(this, state, receiver.settings.context.state.value.data.resetsConfig.saveMountingReset) })
			}
			.launchIn(receiver.context.scope)
	}
}
