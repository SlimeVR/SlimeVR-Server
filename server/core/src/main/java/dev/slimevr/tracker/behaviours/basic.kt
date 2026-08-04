package dev.slimevr.tracker.behaviours

import dev.slimevr.stayaligned.StayAlignedManager
import dev.slimevr.tracker.CalibratedAcceleration
import dev.slimevr.tracker.CalibratedRotation
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
import io.github.axisangles.ktmath.Quaternion
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.time.Duration

class TrackerBasicBehaviour(private val stayAlignedManager: StayAlignedManager) : TrackerBehaviour {
	override fun reduce(state: TrackerState, action: TrackerActions) = when (action) {
		is TrackerActions.Update -> action.transform(state)

		is TrackerActions.SetMagStatus -> state.copy(magStatus = action.status)

		is TrackerActions.SetStatus -> state.copy(status = action.status)

		is TrackerActions.SetRotation -> {
			val rawRotation: RawRotation = action.rotation ?: state.rawRotation
			val rawAcceleration: RawAcceleration = action.acceleration ?: state.rawAcceleration
			val rawMagnetometer = action.magnetometer ?: state.rawMagnetometer
			val position = action.position ?: state.position

			val cal = state.sessionCalibration

			val hideYawCorrection = stayAlignedManager.context.state.value.hideYawCorrection
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

			state.copy(sessionCalibration = sessionCalibration)
		}

		is TrackerActions.ClearMountingReset -> {
			state.copy(sessionCalibration = state.sessionCalibration?.copy(headingAlignment = Quaternion.IDENTITY))
		}

		else -> state
	}

	override fun observe(receiver: Tracker) {
		// Refresh the tracker's rotation whenever calibration gets updated
		receiver.context.state
			.distinctUntilChanged { old, new ->
				old.sessionCalibration == new.sessionCalibration &&
					old.restOrientation == new.restOrientation &&
					old.mountingOrientation == new.mountingOrientation
			}
			.onEach {
				// Make sure to send the raw data to have calibration re-apply
				receiver.context.dispatch(TrackerActions.SetRotation(it.rawRotation, it.rawAcceleration, it.rawMagnetometer))
			}.launchIn(receiver.context.scope)
	}
}
