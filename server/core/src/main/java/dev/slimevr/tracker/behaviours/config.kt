package dev.slimevr.tracker.behaviours

import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsActions
import dev.slimevr.config.TrackerConfig
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import dev.slimevr.tracker.TrackerBehaviour
import dev.slimevr.tracker.TrackerState
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.datatypes.MagnetometerStatus
import solarxr_protocol.datatypes.MountingMethod

class TrackerConfigBehaviour(
	private val settings: Settings,
	private val hardwareId: String,
	/** State the tracker was created with, before any saved config was applied */
	private val baseState: TrackerState,
) : TrackerBehaviour {
	override fun observe(receiver: Tracker) {
		receiver.context.state
			.distinctUntilChangedBy {
				val saveMountingReset = receiver.settings.context.state.value.data.resetsConfig.saveMountingReset
				val headingAlignment = if (saveMountingReset) it.sessionCalibration.headingAlignment else null
				it.bodyPart to it.customName to it.mountingOrientation to it.magStatus to headingAlignment to it.boneOffsets
			}
			.drop(1)
			.onEach { state ->
				settings.context.dispatch(SettingsActions.UpdateTracker(hardwareId) { applyStateToConfig(this, state, receiver.settings.context.state.value.data.resetsConfig.saveMountingReset) })
			}
			.launchIn(receiver.context.scope)

		settings.context.state
			.map { it.name }
			.distinctUntilChanged()
			.drop(1)
			.onEach {
				val data = settings.context.state.value.data
				val config = data.trackers[hardwareId]
				receiver.context.dispatch(
					TrackerActions.Update { applyProfileConfig(this, baseState, config, data.resetsConfig.saveMountingReset) },
				)
			}
			.launchIn(receiver.context.scope)
	}

	companion object {
		/**
		 * Applies the config of a newly selected profile to a live tracker. A tracker the profile
		 * doesn't know goes back to the state it was created with.
		 */
		fun applyProfileConfig(state: TrackerState, baseState: TrackerState, config: TrackerConfig?, saveMountingReset: Boolean): TrackerState {
			val restored = if (config != null) restoreFromConfig(baseState, config, saveMountingReset) else baseState
			return state.copy(
				bodyPart = restored.bodyPart,
				customName = restored.customName,
				lastMountingMethod = restored.lastMountingMethod,
				mountingOrientation = restored.mountingOrientation,
				sessionCalibration = state.sessionCalibration.copy(headingAlignment = restored.sessionCalibration.headingAlignment),
				magStatus = if (config?.magEnabled != null) restored.magStatus else state.magStatus,
				boneOffsets = restored.boneOffsets,
			)
		}

		fun restoreFromConfig(state: TrackerState, config: TrackerConfig, saveMountingReset: Boolean): TrackerState = state.copy(
			bodyPart = config.bodyPart,
			customName = config.customName ?: state.customName,
			lastMountingMethod = if (saveMountingReset && config.mountingResetOrientation != null) MountingMethod.POSE else MountingMethod.MANUAL,
			mountingOrientation = config.mountingOrientation,
			sessionCalibration = if (saveMountingReset && config.mountingResetOrientation != null) {
				// Use mounting reset
				state.sessionCalibration.copy(headingAlignment = config.mountingResetOrientation)
			} else {
				// Use manual mounting
				state.sessionCalibration.copy(headingAlignment = config.mountingOrientation)
			},
			magStatus = when (config.magEnabled) {
				true -> MagnetometerStatus.ENABLED
				false -> MagnetometerStatus.DISABLED
				null -> state.magStatus
			},
			boneOffsets = state.boneOffsets + config.boneOffsets,
		)

		private fun applyStateToConfig(config: TrackerConfig, state: TrackerState, saveMountingReset: Boolean) = config.copy(
			bodyPart = state.bodyPart,
			customName = state.customName,
			mountingOrientation = state.mountingOrientation,
			mountingResetOrientation = if (saveMountingReset) state.sessionCalibration.headingAlignment else null,
			magEnabled = when (state.magStatus) {
				MagnetometerStatus.ENABLED -> true
				MagnetometerStatus.DISABLED -> false
				MagnetometerStatus.NOT_SUPPORTED -> config.magEnabled
			},
			boneOffsets = state.boneOffsets,
		)
	}
}
