package dev.slimevr.tracker.behaviours

import dev.slimevr.config.Settings
import dev.slimevr.math.angle.Angle
import dev.slimevr.stayaligned.CorrectTrackerYaw
import dev.slimevr.stayaligned.StayAlignedDefaults.IMU_TO_YAW_CORRECTION
import dev.slimevr.stayaligned.StayAlignedDefaults.YAW_CORRECTION_DEFAULT
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import dev.slimevr.tracker.TrackerBehaviour
import dev.slimevr.tracker.TrackerState
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

class TrackerStayAlignedBehaviour(
	private val settings: Settings,
) : TrackerBehaviour {
	private var lastRotationTime = timeSource.markNow()

	@OptIn(ExperimentalCoroutinesApi::class)
	override fun observe(receiver: Tracker) {
		receiver.context.state
			.distinctUntilChanged { old, new -> old.sessionCalibration == new.sessionCalibration }
			.onEach {
				receiver.context.dispatch(TrackerActions.SetYawCorrection(Angle.ZERO))
			}.launchIn(receiver.context.scope)

		val serverFlow = receiver.appContext.server.context.state

		val stayAlignedConfigFlow = settings.context.state.map { it.data.stayAlignedConfig }.distinctUntilChanged()
		val imuTypeFlow = receiver.context.state.map { it.imuType }.distinctUntilChanged()
		val magStatusFlow = receiver.context.state.map { it.magStatus }.distinctUntilChanged()
		combine(stayAlignedConfigFlow, imuTypeFlow, magStatusFlow, ::Triple)
			.flatMapLatest { (stayAlignedConfig, imuType, magStatus) ->
				if (magStatus == MagnetometerStatus.ENABLED || imuType == null || !stayAlignedConfig.enabled) return@flatMapLatest emptyFlow()

				// Ignore every other emission for performance (50FPS instead of 100FPS at peak)
				var index = 0
				receiver.context.state
					.distinctUntilChangedBy { it.rawRotation }
					.filter { index++ % 2 == 0 }
					.onEach { state ->
						val yawCorrectionPerSec = IMU_TO_YAW_CORRECTION.getOrDefault(state.imuType, YAW_CORRECTION_DEFAULT)
						if (yawCorrectionPerSec == Angle.ZERO) return@onEach

						val lastFrameTimeSeconds = lastRotationTime.elapsedNow().inFloatingSeconds
						lastRotationTime = timeSource.markNow()

						val normalizedYawCorrection = yawCorrectionPerSec * lastFrameTimeSeconds
						val yawCorrectionResult = CorrectTrackerYaw.computeYawCorrection(
							state,
							serverFlow.value.trackers.values.map { it.context.state.value },
							normalizedYawCorrection,
							stayAlignedConfig,
						)

						if (yawCorrectionResult != null) {
							receiver.context.dispatch(TrackerActions.SetYawCorrection(yawCorrectionResult))
						}
					}
			}
			.launchIn(receiver.context.scope)
	}

	override fun reduce(state: TrackerState, action: TrackerActions): TrackerState = when (action) {
		is TrackerActions.SetYawCorrection -> state.copy(stayAlignedData = state.stayAlignedData.copy(yawCorrection = action.yawCorrection))
		else -> state
	}
}
