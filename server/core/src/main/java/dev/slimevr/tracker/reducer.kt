package dev.slimevr.tracker

import com.jme3.math.FastMath
import io.github.axisangles.ktmath.Quaternion
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.MountingMethod
import kotlin.time.Duration

private const val UNRELIABLE_ANGLE_CHANGE_THRESHOLD = 135 * FastMath.DEG_TO_RAD

private fun trackPolarity(
	newRotation: Quaternion,
	oldRotation: Quaternion,
	bodyPart: BodyPart?,
	headTrackerRotation: Quaternion? = null,
): Quaternion {
	val absAngleChange = newRotation.angleToR(oldRotation)
	if (absAngleChange > UNRELIABLE_ANGLE_CHANGE_THRESHOLD && bodyPart != BodyPart.HEAD && headTrackerRotation != null) {
		// Rotation rotated too much since last SetRotation; we're not sure which direction the tracker rotated.
		// Solution: align polarity with the head tracker since the user is likely to be spinning their whole body.
		return newRotation.twinNearest(headTrackerRotation)
	}

	// Track polarity compared to the last rotation
	return newRotation.twinNearest(oldRotation)
}

fun reduce(
	state: TrackerState,
	action: TrackerActions,
): TrackerState = when (action) {
	is TrackerActions.Update -> action.transform(state)

	is TrackerActions.SetMagStatus -> state.copy(magStatus = action.status)

	is TrackerActions.SetStatus -> state.copy(status = action.status)

	is TrackerActions.SetDriverName -> state.copy(driverName = action.driverName)

	is TrackerActions.SetRotation -> {
		val accumulatedTicks = if (action.newData && action.rotation != null) (state.accumulatedTicks + 1u).toUShort() else state.accumulatedTicks

		// Rotation
		val rawRotation: RawRotation = action.rotation ?: state.rawRotation
		val yawCorrectedRawRotation = Quaternion.rotationAroundYAxis(state.stayAlignedData.yawCorrection.toRad()) * rawRotation
		val stayAlignedEnabled = state.stayAlignedData.enabled
		val correctedRawRotation = if (stayAlignedEnabled) yawCorrectedRawRotation else rawRotation

		// Other inputs
		val rawAcceleration: RawAcceleration = action.acceleration ?: state.rawAcceleration
		val rawMagnetometer = action.magnetometer ?: state.rawMagnetometer
		val position = action.position ?: state.position

		val cal = state.sessionCalibration

		// Rotation calibration
		val rotation: CalibratedRotation =
			if (action.rotation != null) {
				trackPolarity(
					applyCalibration(correctedRawRotation, cal.headingCorrection, cal.attitudeAlignment, cal.headingAlignment, state.restOrientation),
					state.rotation,
					state.bodyPart,
					action.headTrackerRotation,
				)
			} else {
				state.rotation
			}

		// Accel calibration
		val acceleration: CalibratedAcceleration =
			if (action.acceleration != null) {
				applyCalibration(rawAcceleration, correctedRawRotation, cal.headingCorrection, cal.headingAlignment)
			} else {
				state.acceleration
			}

		state.copy(
			rawRotation = rawRotation,
			rotation = rotation,
			rawAcceleration = rawAcceleration,
			acceleration = acceleration,
			rawMagnetometer = rawMagnetometer,
			position = position,
			accumulatedTicks = accumulatedTicks,
		)
	}

	is TrackerActions.SetMountingOrientation -> {
		if (state.position != null) {
			// Don't set mounting orientation for positional trackers
			state
		} else {
			state.copy(
				mountingOrientation = action.mountingOrientation,
				sessionCalibration = state.sessionCalibration.copy(headingAlignment = action.mountingOrientation),
				lastMountingMethod = MountingMethod.MANUAL,
			)
		}
	}

	is TrackerActions.SetRestOrientation -> state.copy(restOrientation = action.restOrientation)

	is TrackerActions.FullReset -> {
		val isPositional = state.position != null
		val isHead = state.bodyPart == BodyPart.HEAD
		val shouldAlignAttitude = !isHead || !isPositional || action.resetPositionalHeadAttitude
		val shouldAlignHeadingWithReference = !isHead && isPositional

		val headingCorrection =
			if (shouldAlignAttitude) {
				estimateHeadingCorrect(
					state.rawRotation,
					action.referenceRotation,
				)
			} else {
				state.sessionCalibration.attitudeAlignment
			}
		val attitudeAlignment =
			if (shouldAlignAttitude) {
				estimateAttitudeAlign(
					state.rawRotation,
					headingCorrection,
					action.referenceRotation,
				)
			} else {
				state.sessionCalibration.attitudeAlignment
			}
		val headingAlignment =
			if (shouldAlignHeadingWithReference) {
				headingCorrection
			} else {
				state.sessionCalibration.headingAlignment
			}

		state.copy(
			sessionCalibration = state.sessionCalibration.copy(
				headingCorrection = headingCorrection,
				attitudeAlignment = attitudeAlignment,
				headingAlignment = headingAlignment,
			),
			// Reset polarity tracking
			rotation = state.rotation.twinNearest(Quaternion.IDENTITY),
			// Full reset snaps: cancel any in-progress yaw smoothing.
			yawResetSmoothing = null,
		)
	}

	is TrackerActions.YawReset -> {
		val cal = state.sessionCalibration

		val newHeading = estimateHeadingCorrect(
			applyCalibration(state.rawRotation, attitudeAlign = cal.attitudeAlignment, headingAlign = cal.headingAlignment),
			action.referenceRotation,
		)

		if (action.smoothTime > Duration.ZERO && cal.headingCorrection != Quaternion.IDENTITY && cal.headingCorrection != newHeading) {
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
				sessionCalibration = cal.copy(headingCorrection = newHeading),
				yawResetSmoothing = null,
			)
		}
	}

	is TrackerActions.PoseMountingReset -> {
		val cal = state.sessionCalibration

		val headingAlignment = estimateHeadingAlign(
			state.rawRotation,
			action.referenceRotation,
			cal.headingCorrection,
			cal.attitudeAlignment,
			state.mountingOrientation,
			action.yawOffset,
		) *
			state.mountingOrientation

		state.copy(
			sessionCalibration = state.sessionCalibration.copy(headingAlignment = headingAlignment),
			lastMountingMethod = MountingMethod.POSE,
		)
	}

	is TrackerActions.ClearMountingReset -> {
		state.copy(
			sessionCalibration = state.sessionCalibration.copy(headingAlignment = state.mountingOrientation),
			lastMountingMethod = MountingMethod.MANUAL,
		)
	}

	is TrackerActions.SetMotion -> {
		state.copy(
			motion = action.motion,
			stayAlignedData = state.stayAlignedData.copy(lockedRotation = if (action.motion == Motion.RESTING) state.rotation else null),
		)
	}

	is TrackerActions.SetYawCorrection -> state.copy(stayAlignedData = state.stayAlignedData.copy(yawCorrection = action.yawCorrection))

	is TrackerActions.SetStayAlignedEnabled -> state.copy(stayAlignedData = state.stayAlignedData.copy(enabled = action.enabled))

	is TrackerActions.TickYawResetSmoothing -> {
		val cal = state.sessionCalibration
		if (state.yawResetSmoothing == null) {
			// Nothing to advance.
			state.copy(yawResetSmoothing = null)
		} else {
			// The behaviour computed the interpolated heading; store it in the session
			// calibration. TrackerCalibrationRefreshBehaviour.observe re-applies it to the rotation
			// (using the last raw rotation), so this progresses even with no new IMU
			// data. On `done` the seed is cleared, leaving the target heading in place.
			state.copy(
				sessionCalibration = cal.copy(headingCorrection = action.heading),
				yawResetSmoothing = if (action.done) null else state.yawResetSmoothing,
			)
		}
	}
}
