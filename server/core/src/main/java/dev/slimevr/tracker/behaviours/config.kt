package dev.slimevr.tracker.behaviours

import dev.slimevr.bones.BoneId
import dev.slimevr.bones.BoneRegistry
import dev.slimevr.config.Settings
import dev.slimevr.config.SettingsActions
import dev.slimevr.config.TrackerConfig
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerBehaviour
import dev.slimevr.tracker.TrackerState
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.datatypes.MagnetometerStatus
import solarxr_protocol.datatypes.MountingMethod

class TrackerConfigBehaviour(
	private val settings: Settings,
	private val hardwareId: String,
	private val registry: BoneRegistry,
) : TrackerBehaviour {
	override fun observe(receiver: Tracker) {
		receiver.context.state
			.distinctUntilChangedBy {
				val saveMountingReset = receiver.settings.context.state.value.data.resetsConfig.saveMountingReset
				val headingAlignment = if (saveMountingReset) it.sessionCalibration.headingAlignment else null
				it.boneId to it.customName to it.mountingOrientation to it.magStatus to headingAlignment
			}
			.drop(1)
			.onEach { state ->
				settings.context.dispatch(SettingsActions.UpdateTracker(hardwareId) { applyStateToConfig(this, state, receiver.settings.context.state.value.data.resetsConfig.saveMountingReset, registry) })
			}
			.launchIn(receiver.context.scope)
	}

	companion object {
		fun restoreFromConfig(state: TrackerState, config: TrackerConfig, saveMountingReset: Boolean, registry: BoneRegistry): TrackerState = state.copy(
			boneId = config.bone?.let { registry.byKey(it) }?.let { BoneId(it.id) },
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
		)

		private fun applyStateToConfig(config: TrackerConfig, state: TrackerState, saveMountingReset: Boolean, registry: BoneRegistry) = config.copy(
			bone = state.boneId?.let { registry[it]?.key },
			customName = state.customName,
			mountingOrientation = state.mountingOrientation,
			mountingResetOrientation = if (saveMountingReset) state.sessionCalibration.headingAlignment else null,
			magEnabled = when (state.magStatus) {
				MagnetometerStatus.ENABLED -> true
				MagnetometerStatus.DISABLED -> false
				MagnetometerStatus.NOT_SUPPORTED -> config.magEnabled
			},
		)
	}
}
