package dev.slimevr.tracker.behaviours

import dev.slimevr.config.Settings
import dev.slimevr.config.UserConfig
import dev.slimevr.math.angle.Angle
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import dev.slimevr.tracker.TrackerBehaviour
import dev.slimevr.tracker.stayaligned.StayAlignedDefaults.IMU_TO_YAW_CORRECTION
import dev.slimevr.tracker.stayaligned.StayAlignedDefaults.YAW_CORRECTION_DEFAULT
import dev.slimevr.tracker.stayaligned.TrackerYawCorrection
import dev.slimevr.util.inFloatingSeconds
import dev.slimevr.util.timeSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.datatypes.MagnetometerStatus
import kotlin.time.Duration.Companion.seconds

// When a tracker comes back from sleep, prevents a disproportionate correction
private val MAX_DELTA_TIME = 0.5.seconds

class TrackerStayAlignedBehaviour(
	private val settings: Settings,
	private val userConfig: UserConfig,
) : TrackerBehaviour {

	override fun observe(receiver: Tracker) {
		observeConfig(receiver)
		observeReset(receiver)
		observeRun(receiver)
	}

	/**
	 * Sync stay aligned enabled state from settings
	 */
	private fun observeConfig(receiver: Tracker) {
		settings.context.state
			.map { it.data.stayAlignedConfig.enabled }
			.distinctUntilChanged()
			.onEach { enabled ->
				receiver.context.dispatch(TrackerActions.SetStayAlignedEnabled(enabled))
			}
			.launchIn(receiver.context.scope)
	}

	/**
	 * Reset Stay Aligned on calibration
	 */
	private fun observeReset(receiver: Tracker) {
		receiver.context.state
			.distinctUntilChanged { old, new -> old.sessionCalibration == new.sessionCalibration }
			.onEach {
				receiver.context.dispatch(TrackerActions.SetYawCorrection(Angle.ZERO))
			}.launchIn(receiver.context.scope)
	}

	/**
	 * Run StayAligned
	 */
	@OptIn(ExperimentalCoroutinesApi::class)
	private fun observeRun(receiver: Tracker) {
		val serverFlow = receiver.appContext.server.context.state

		val enabledFlow = settings.context.state.map { it.data.stayAlignedConfig.enabled }.distinctUntilChanged()
		val imuTypeFlow = receiver.context.state.map { it.imuType }.distinctUntilChanged()
		val magStatusFlow = receiver.context.state.map { it.magStatus }.distinctUntilChanged()
		combine(enabledFlow, imuTypeFlow, magStatusFlow, ::Triple)
			.flatMapLatest { (enabled, imuType, magStatus) ->
				if (magStatus == MagnetometerStatus.ENABLED || imuType == null || !enabled) return@flatMapLatest emptyFlow()

				var lastRotationTime = timeSource.markNow()
				receiver.context.state
					.distinctUntilChangedBy { it.rawRotation }
					.onEach { state ->
						val yawCorrectionPerSec = IMU_TO_YAW_CORRECTION.getOrDefault(imuType, YAW_CORRECTION_DEFAULT)
						if (yawCorrectionPerSec == Angle.ZERO) return@onEach

						val deltaTime = lastRotationTime.elapsedNow().coerceAtMost(MAX_DELTA_TIME).inFloatingSeconds
						lastRotationTime = timeSource.markNow()

						val applyYawCorrection = yawCorrectionPerSec * deltaTime

						val yawCorrectionResult = TrackerYawCorrection.computeYawCorrection(
							state,
							serverFlow.value.trackers.values.map { it.context.state.value },
							applyYawCorrection,
							userConfig.context.state.value.data,
						)
						if (yawCorrectionResult != null) {
							receiver.context.dispatch(TrackerActions.SetYawCorrection(yawCorrectionResult))
						}
					}
			}
			.launchIn(receiver.context.scope)
	}
}
