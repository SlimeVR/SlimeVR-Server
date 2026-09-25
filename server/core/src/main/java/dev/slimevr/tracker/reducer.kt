package dev.slimevr.tracker

import io.github.axisangles.ktmath.Quaternion
import solarxr_protocol.datatypes.MountingMethod
import solarxr_protocol.rpc.ResetType
import kotlin.time.Duration

fun reduce(
	state: TrackerState,
	action: TrackerActions,
): TrackerState = when (action) {
	is TrackerActions.Update -> action.transform(state)

	is TrackerActions.SetMagStatus -> state.copy(magStatus = action.status)

	is TrackerActions.SetStatus -> state.copy(status = action.status)

	is TrackerActions.SetDriverName -> state.copy(driverName = action.driverName)

	is TrackerActions.SetRotation -> {
		val accumulatedTicks = if (action.increaseTps && action.rotation != null) (state.accumulatedTicks + 1u).toUShort() else state.accumulatedTicks

		// Rotation
		val rawRotation: RawRotation = action.rotation ?: state.rawRotation
		val correctedRawRotation = if (state.stayAlignedData.enabled) {
			// Apply stay aligned yaw correction
			Quaternion.rotationAroundYAxis(state.stayAlignedData.yawCorrection.toRad()) * rawRotation
		} else {
			rawRotation
		}
		val polarityAlign = if (state.rotationDirty) {
			// Reset polarity according to last reference rotation.
			state.lastReference
		} else {
			state.rotation
		}

		// Other inputs
		val rawAcceleration: RawAcceleration = action.acceleration ?: state.rawAcceleration
		val rawMagnetometer = action.magnetometer ?: state.rawMagnetometer
		val position = action.position ?: state.position

		// Rotation calibration
		val rotation: CalibratedRotation =
			if (action.rotation != null) {
				applyFullCalibration(correctedRawRotation, state)
					.twinNearest(polarityAlign)
			} else {
				state.rotation
			}

		// Accel calibration
		val acceleration: CalibratedAcceleration =
			if (action.acceleration != null) {
				applyFullCalibration(rawAcceleration, correctedRawRotation, state)
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
			rotationDirty = false,
		)
	}

	is TrackerActions.SetMountingOrientation -> {
		state.copy(
			mountingOrientation = action.mountingOrientation,
			sessionCalibration = state.sessionCalibration.copy(
				headingCorrection = if (state.position != null) action.mountingOrientation else state.sessionCalibration.headingCorrection,
				headingAlignment = action.mountingOrientation,
			),
			lastMountingMethod = MountingMethod.MANUAL,
			rotationDirty = true,
		)
	}

	is TrackerActions.SetRestOrientation -> state.copy(
		restOrientation = action.restOrientation,
		rotationDirty = true,
	)

	is TrackerActions.FullReset -> {
		val alignAttitude = !state.isAssignedReliableReference || action.resetReliableReferenceAttitude
		val correctHeading = action.referenceRotation != null
		val alignHeading = state.position != null && action.referenceRotation != null

		val referenceRotation = action.referenceRotation ?: state.rawRotation

		// Always compute headingCorrection since we need it to compute attitudeAlignment
		val headingCorrection = estimateHeadingCorrect(
			state.rawRotation,
			referenceRotation,
		)
		val attitudeAlignment =
			if (alignAttitude) {
				estimateAttitudeAlign(
					state.rawRotation,
					headingCorrection,
					referenceRotation,
				)
			} else {
				Quaternion.IDENTITY
			}
		val headingAlignment =
			if (alignHeading) {
				headingCorrection
			} else {
				state.sessionCalibration.headingAlignment
			}

		state.copy(
			sessionCalibration = state.sessionCalibration.copy(
				headingCorrection = if (correctHeading) headingCorrection else state.sessionCalibration.headingCorrection,
				attitudeAlignment = attitudeAlignment,
				headingAlignment = headingAlignment,
			),
			lastReference = referenceRotation,
			// Full reset snaps: cancel any in-progress yaw smoothing.
			yawResetSmoothing = null,
			pendingSkeletonResets = state.pendingSkeletonResets + ResetType.FULL,
			rotationDirty = true,
		)
	}

	is TrackerActions.YawReset -> {
		val baseReturnState = state.copy(
			lastReference = action.referenceRotation ?: state.rotation,
			pendingSkeletonResets = state.pendingSkeletonResets + ResetType.YAW,
			rotationDirty = true,
		)

		// Only reset polarity for positional trackers and references
		val correctHeading = state.position == null && action.referenceRotation != null
		if (!correctHeading) return baseReturnState

		// Compute the heading (yaw) correction
		val newHeadingCorrection = estimateHeadingCorrect(
			applyCalibration(state.rawRotation, attitudeAlign = state.sessionCalibration.attitudeAlignment, headingAlign = state.sessionCalibration.headingAlignment),
			action.referenceRotation,
		)

		val lastHeadingCorrection = state.sessionCalibration.headingCorrection
		if (action.smoothTime > Duration.ZERO && lastHeadingCorrection != Quaternion.IDENTITY && lastHeadingCorrection != newHeadingCorrection) {
			// Yaw Reset Smoothing: only set the target. Leave the applied heading where it is
			// TrackerYawResetSmoothingBehaviour eases sessionCalibration.headingCorrection
			// to newHeading over smoothTime. A reset mid-ease just replaces the seed.
			baseReturnState.copy(
				yawResetSmoothing = YawResetSmoothing(
					from = lastHeadingCorrection,
					to = newHeadingCorrection,
					duration = action.smoothTime,
				),
			)
		} else {
			// Snap: apply the new heading immediately (default, no smoothing configured).
			baseReturnState.copy(
				sessionCalibration = state.sessionCalibration.copy(headingCorrection = newHeadingCorrection),
				yawResetSmoothing = null,
			)
		}
	}

	is TrackerActions.PoseMountingReset -> {
		// Positional trackers' heading is aligned on full reset, not on mounting reset, except for a reference.
		val alignHeading = state.position == null || action.referenceRotation == null
		// A positional reference tracker needs to correct its heading on mounting reset.
		val correctHeading = state.position != null && action.referenceRotation == null

		val referenceRotation = action.referenceRotation ?: state.rotation

		val headingAlignment = if (alignHeading) {
			estimateHeadingAlign(
				state.rawRotation,
				referenceRotation,
				state.sessionCalibration.headingCorrection,
				state.sessionCalibration.attitudeAlignment,
				action.yawOffset,
			)
		} else {
			state.sessionCalibration.headingAlignment
		}

		// Heading correction for positional tracker is the heading alignment
		val headingCorrection = if (correctHeading) {
			headingAlignment
		} else {
			state.sessionCalibration.headingCorrection
		}

		state.copy(
			sessionCalibration = state.sessionCalibration.copy(headingCorrection = headingCorrection, headingAlignment = headingAlignment),
			lastMountingMethod = if (alignHeading) MountingMethod.POSE else state.lastMountingMethod,
			lastReference = referenceRotation,
			rotationDirty = true,
			pendingSkeletonResets = state.pendingSkeletonResets + ResetType.POSE_MOUNTING,
		)
	}

	is TrackerActions.ClearMountingReset -> {
		state.copy(
			sessionCalibration = state.sessionCalibration.copy(headingAlignment = state.mountingOrientation),
			lastMountingMethod = MountingMethod.MANUAL,
			rotationDirty = true,
		)
	}

	is TrackerActions.ClearPendingSkeletonResets -> {
		state.copy(pendingSkeletonResets = state.pendingSkeletonResets.drop(action.count))
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
		if (state.yawResetSmoothing == null) {
			// Nothing to advance.
			state.copy(yawResetSmoothing = null)
		} else {
			// The behaviour computed the interpolated heading; store it in the session
			// calibration. TrackerCalibrationRefreshBehaviour.observe re-applies it to the rotation
			// (using the last raw rotation), so this progresses even with no new IMU
			// data. On `done` the seed is cleared, leaving the target heading in place.
			state.copy(
				sessionCalibration = state.sessionCalibration.copy(headingCorrection = action.heading),
				yawResetSmoothing = if (action.done) null else state.yawResetSmoothing,
				rotationDirty = true,
			)
		}
	}
}
