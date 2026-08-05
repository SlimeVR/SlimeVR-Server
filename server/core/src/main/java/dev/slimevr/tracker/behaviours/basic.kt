package dev.slimevr.tracker.behaviours

import dev.slimevr.logging.AppLogger
import dev.slimevr.stayaligned.StayAlignedManager
import dev.slimevr.tracker.CalibratedAcceleration
import dev.slimevr.tracker.CalibratedRotation
import dev.slimevr.tracker.Motion
import dev.slimevr.tracker.RawAcceleration
import dev.slimevr.tracker.RawRotation
import dev.slimevr.tracker.SessionCalibration
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import dev.slimevr.tracker.TrackerBehaviour
import dev.slimevr.tracker.TrackerState
import dev.slimevr.tracker.YawResetSmoothing
import dev.slimevr.tracker.applyCalibration
import dev.slimevr.tracker.estimateAttitudeAlign
import dev.slimevr.tracker.estimateHeadingAlign
import dev.slimevr.tracker.estimateHeadingCorrect
import dev.slimevr.util.timeSource
import io.github.axisangles.ktmath.Quaternion
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import solarxr_protocol.datatypes.MountingMethod
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.incrementAndFetch
import kotlin.time.Duration

@OptIn(ExperimentalAtomicApi::class)
class TrackerBasicBehaviour(private val stayAlignedManager: StayAlignedManager) : TrackerBehaviour {
	val tpsCount = AtomicInt(0)

	override fun reduce(state: TrackerState, action: TrackerActions) = when (action) {
		is TrackerActions.Update -> action.transform(state)

		is TrackerActions.SetMagStatus -> state.copy(magStatus = action.status)

		is TrackerActions.SetStatus -> state.copy(status = action.status)

		is TrackerActions.SetRotation -> {
			// This action counts as a tick towards TPS if the data is new.
			if (action.newData) tpsCount.incrementAndFetch()

			val rawRotation: RawRotation = action.rotation ?: state.rawRotation
			val rawAcceleration: RawAcceleration = action.acceleration ?: state.rawAcceleration
			val rawMagnetometer = action.magnetometer ?: state.rawMagnetometer
			val position = action.position ?: state.position

			val cal = state.sessionCalibration

			val hideYawCorrection = stayAlignedManager.context.state.value.hideCorrection
			val yawCorrectedRawRotation = Quaternion.rotationAroundYAxis(state.stayAlignedData.yawCorrection.toRad()) * rawRotation

			// TODO non-IMU trackers still want some form of calibration applied
			// Rotation calibration
			val (rotation: CalibratedRotation, forceStayAlignedRotation: CalibratedRotation) = when {
				state.imuType == null -> rawRotation to rawRotation

				cal != null && action.rotation != null -> {
					fun calibrate(rot: RawRotation) = applyCalibration(
						rot,
						cal.headingCorrection,
						cal.attitudeAlignment,
						cal.headingAlignment * state.mountingOrientation,
					)
					val yawCorrectCalibratedRotation = calibrate(yawCorrectedRawRotation).twinNearest(state.stayAlignedData.forceStayAlignedRotation)
					(if (hideYawCorrection) calibrate(rawRotation).twinNearest(state.rotation) else yawCorrectCalibratedRotation) to yawCorrectCalibratedRotation
				}

				cal != null -> state.rotation to state.stayAlignedData.forceStayAlignedRotation

				else -> rawRotation to rawRotation
			}

			// Accel calibration
			val acceleration: CalibratedAcceleration = when {
				state.imuType == null -> rawAcceleration

				cal != null && action.acceleration != null -> applyCalibration(
					rawAcceleration,
					if (hideYawCorrection) rawRotation else yawCorrectedRawRotation,
					cal.headingCorrection,
					cal.headingAlignment,
				)

				cal != null -> state.acceleration

				else -> rawAcceleration
			}

			state.copy(
				rawRotation = rawRotation,
				rotation = rotation,
				stayAlignedData = state.stayAlignedData.copy(forceStayAlignedRotation = forceStayAlignedRotation),
				rawAcceleration = rawAcceleration,
				acceleration = acceleration,
				rawMagnetometer = rawMagnetometer,
				position = position,
			)
		}

		is TrackerActions.SetMountingOrientation -> {
			state.copy(
				mountingOrientation = action.mountingOrientation,
				sessionCalibration = state.sessionCalibration?.copy(headingAlignment = Quaternion.IDENTITY),
				lastMountingMethod = MountingMethod.MANUAL,
			)
		}

		is TrackerActions.SetRestOrientation -> state.copy(restOrientation = action.restOrientation)

		is TrackerActions.FullReset -> {
			val headingCorrection = estimateHeadingCorrect(
				state.rawRotation,
				action.referenceRotation,
			)
			val attitudeAlignment = estimateAttitudeAlign(
				state.rawRotation,
				headingCorrection,
				action.referenceRotation,
			)

			val sessionCalibration = state.sessionCalibration?.copy(
				headingCorrection = headingCorrection,
				attitudeAlignment = attitudeAlignment,
			) ?: SessionCalibration(
				headingCorrection = headingCorrection,
				attitudeAlignment = attitudeAlignment,
			)

			// Reset polarity tracking
			val defaultPolarityRotation = state.rotation.twinNearest(Quaternion.IDENTITY)

			state.copy(
				sessionCalibration = sessionCalibration,
				rotation = defaultPolarityRotation,
				// Full reset snaps: cancel any in-progress yaw smoothing.
				yawResetSmoothing = null,
			)
		}

		is TrackerActions.YawReset -> {
			val newHeading = estimateHeadingCorrect(
				state.rawRotation,
				action.referenceRotation,
			)
			val cal = state.sessionCalibration

			if (action.smoothTime > Duration.ZERO && cal != null && cal.headingCorrection != newHeading) {
				// Smooth: only set the target. Leave the applied heading where it is
				// TrackerYawResetSmoothingBehaviour eases sessionCalibration.headingCorrection
				// to newHeading over smoothTime. A reset mid-ease just replaces the seed.
				state.copy(
					yawResetSmoothing = YawResetSmoothing(
						from = cal.headingCorrection,
						to = newHeading,
						duration = action.smoothTime,
					),
				)
			} else {
				// Snap: apply the new heading immediately (default, no smoothing configured).
				state.copy(
					sessionCalibration = cal?.copy(headingCorrection = newHeading)
						?: SessionCalibration(headingCorrection = newHeading),
					yawResetSmoothing = null,
				)
			}
		}

		is TrackerActions.MountingReset -> {
			val cal = state.sessionCalibration

			val headingAlignment = estimateHeadingAlign(
				state.rawRotation,
				action.referenceRotation,
				cal?.headingCorrection ?: Quaternion.IDENTITY,
				cal?.attitudeAlignment ?: Quaternion.IDENTITY,
				state.mountingOrientation,
				action.yawOffset,
			)

			val sessionCalibration = state.sessionCalibration?.copy(
				headingAlignment = headingAlignment,
			) ?: SessionCalibration(
				headingAlignment = headingAlignment,
			)

			state.copy(
				sessionCalibration = sessionCalibration,
				lastMountingMethod = MountingMethod.POSE,
			)
		}

		is TrackerActions.ClearMountingReset -> {
			state.copy(
				sessionCalibration = state.sessionCalibration?.copy(headingAlignment = Quaternion.IDENTITY),
				lastMountingMethod = MountingMethod.MANUAL,
			)
		}

		else -> state
	}

	override fun observe(receiver: Tracker) {
		observeCalibration(receiver)
		observeTps(receiver)
	}

	/**
	 * Refreshes the tracker's rotation whenever calibration gets updated
	 */
	private fun observeCalibration(receiver: Tracker) {
		receiver.context.state
			.distinctUntilChanged { old, new ->
				old.sessionCalibration == new.sessionCalibration &&
					old.restOrientation == new.restOrientation &&
					old.mountingOrientation == new.mountingOrientation
			}
			.onEach {
				// Make sure to send the raw data to have calibration re-apply
				receiver.context.dispatch(TrackerActions.SetRotation(it.rawRotation, it.rawAcceleration, it.rawMagnetometer, newData = false))
			}.launchIn(receiver.context.scope)
	}

	/**
	 * Sets the tracker's Ticks Per Second (TPS) every second.
	 *
	 * One tick = one new rotation data
	 */
	private fun observeTps(receiver: Tracker) {
		receiver.context.scope.launch {
			var mark = timeSource.markNow()
			while (isActive) {
				try {
					delay(1000)
					val elapsed = mark.elapsedNow()
					val tps = tpsCount.exchange(0) * 1000L / elapsed.inWholeMilliseconds

					// Tracker is at rest if it hasn't been updated in the last second
					val updateMotionAction = if (tps == 0L && receiver.context.state.value.motion != Motion.RESTING) TrackerActions.SetMotion(Motion.RESTING) else null
					receiver.context.dispatchAll(
						listOfNotNull(
							TrackerActions.Update { copy(tps = tps.toUShort()) },
							updateMotionAction,
						),
					)

					mark = timeSource.markNow()
				} catch (e: Exception) {
					AppLogger.coroutines.error(e, "Error in TrackerTPSBehaviour")
				}
			}
		}
	}
}
