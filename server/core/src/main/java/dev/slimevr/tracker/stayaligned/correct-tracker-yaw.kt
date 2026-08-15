package dev.slimevr.tracker.stayaligned

import dev.slimevr.config.StayAlignedConfig
import dev.slimevr.math.angle.Angle
import dev.slimevr.math.angle.AngleAverage
import dev.slimevr.math.angle.AngleErrors
import dev.slimevr.tracker.Motion
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import dev.slimevr.tracker.TrackerState
import dev.slimevr.tracker.applyCalibration
import dev.slimevr.tracker.stayaligned.YawUtils.hasTrackerYaw
import dev.slimevr.tracker.stayaligned.YawUtils.trackerYaw
import dev.slimevr.tracker.stayaligned.poses.PlayerPose
import dev.slimevr.tracker.stayaligned.poses.RelaxedPose
import dev.slimevr.tracker.stayaligned.visitors.CenterErrorVisitor
import dev.slimevr.tracker.stayaligned.visitors.NeighborErrorVisitor
import dev.slimevr.tracker.stayaligned.visitors.TrackerGroups
import io.github.axisangles.ktmath.Quaternion

object CorrectTrackerYaw {

	/**
	 * Aggregates the yaw errors from multiple forces.
	 */
	private data class YawErrors(
		var lockedError: AngleErrors = AngleErrors(),
		var centerError: AngleErrors = AngleErrors(),
		var neighborError: AngleErrors = AngleErrors(),
	)

	/**
	 * Adjusts the yaw of a tracker depending on its motion.
	 */
	fun adjust(
		tracker: Tracker,
		trackerStates: List<TrackerState>,
		yawCorrection: Angle,
		config: StayAlignedConfig,
	) {
		when (tracker.context.state.value.motion) {
			Motion.ROTATING -> adjustMovingTracker(tracker, trackerStates, yawCorrection, config)

			Motion.RESTING -> adjustLockedTracker(tracker, yawCorrection)

			// Do not adjust trackers that were recently resting
			// to support play styles that are primarily at rest
			Motion.STARTED_ROTATING -> {}
		}
	}

	/**
	 * Adjusts a locked tracker.
	 *
	 * After a tracker is at rest for a short time, we lock it and save its initial
	 * rotation. We assume that locked trackers really are at rest, and that any
	 * rotation is due to drift. We adjust the tracker's yaw towards its initial
	 * rotation. If the tracker rotates beyonds a certain angle, we unlock the
	 * tracker.
	 *
	 * This works very well when the player is still and the tracker is supported by
	 * some surface, e.g. sitting in a chair or lying in a bed. However, it does not
	 * work well when the player is standing or moving around because the trackers
	 * will never lock.
	 */
	private fun adjustLockedTracker(
		tracker: Tracker,
		yawCorrection: Angle,
	) {
		val trackerState = tracker.context.state.value
		val lockedRotation = trackerState.stayAlignedData.lockedRotation ?: return

		adjustByError(tracker, yawCorrection) {
			YawErrors().also {
				it.lockedError.add(YawUtils.yawDifference(getStayAlignedRotation(trackerState), lockedRotation))
			}
		}
	}

	/**
	 * Adjusts a tracker that is moving.
	 *
	 * When the player is moving around, we assume that the player will often be in
	 * a relaxed pose, or will eventually return to a relaxed pose. During setup, we
	 * collect the player's relaxed posed when standing, sitting and lying on their
	 * back.
	 *
	 * The centering force adjusts the tracker's yaw towards the relaxed pose.
	 * Upper body trackers are adjusted towards the average yaw of the body. Leg
	 * trackers are also adjusted towards the average yaw of the body, but with a
	 * yaw offset corresponding to their relaxed pose.
	 *
	 * This works well when the player is moving a lot. However, it doesn't work
	 * well when some of the trackers are locked, and others are moving. The locked
	 * trackers will stay in place, while the moving trackers will pull towards the
	 * relaxed pose, which can result in imbalanced poses.
	 */
	private fun adjustMovingTracker(
		tracker: Tracker,
		trackerStates: List<TrackerState>,
		yawCorrection: Angle,
		config: StayAlignedConfig,
	) {
		// Create groups from the tracker states
		val trackers = TrackerGroups(trackerStates)

		val trackerState = tracker.context.state.value

		val centerYaw = centerYawOfTrackers(trackers) ?: return

		val pose = PlayerPose.of(trackers)
		val relaxedPose = RelaxedPose.forPose(pose, config) ?: return

		adjustByError(tracker, yawCorrection) {
			YawErrors().also {
				trackers.visit(trackerState, CenterErrorVisitor(centerYaw, relaxedPose, it.centerError))
				trackers.visit(trackerState, NeighborErrorVisitor(relaxedPose, it.neighborError))
			}
		}
	}

	private fun centerYawOfTrackers(
		trackerGroups: TrackerGroups,
	): Angle? {
		val head = trackerGroups.head
		val upperBody = trackerGroups.upperBody
		val leftUpperLeg = trackerGroups.leftUpperLeg
		val rightUpperLeg = trackerGroups.rightUpperLeg
		val leftLowerLeg = trackerGroups.leftLowerLeg
		val rightLowerLeg = trackerGroups.rightLowerLeg

		if (
			// Head optional, because some mocap scenarios don't use one
			upperBody.isEmpty() ||
			leftUpperLeg == null ||
			rightUpperLeg == null ||
			leftLowerLeg == null ||
			rightLowerLeg == null
		) {
			return null
		}

		// Need a minimum set of trackers, and the trackers need to be oriented in a
		// way where we can actually calculate its yaw.
		val hasCenterYaw =
			upperBody.all(::hasTrackerYaw) &&
				hasTrackerYaw(leftUpperLeg) &&
				hasTrackerYaw(rightUpperLeg) &&
				hasTrackerYaw(leftLowerLeg) &&
				hasTrackerYaw(rightLowerLeg)
		if (!hasCenterYaw) {
			return null
		}

		// Calculate average yaw of the body
		val averageYaw = AngleAverage()

		if (head != null && hasTrackerYaw(head)) {
			averageYaw.add(trackerYaw(head), StayAlignedDefaults.CENTER_ERROR_HEAD_WEIGHT)
		}

		upperBody.forEach {
			averageYaw.add(trackerYaw(it), StayAlignedDefaults.CENTER_ERROR_UPPER_BODY_WEIGHT)
		}

		averageYaw.add(trackerYaw(leftUpperLeg), StayAlignedDefaults.CENTER_ERROR_UPPER_LEG_WEIGHT)
		averageYaw.add(trackerYaw(rightUpperLeg), StayAlignedDefaults.CENTER_ERROR_UPPER_LEG_WEIGHT)

		averageYaw.add(trackerYaw(leftLowerLeg), StayAlignedDefaults.CENTER_ERROR_LOWER_LEG_WEIGHT)
		averageYaw.add(trackerYaw(rightLowerLeg), StayAlignedDefaults.CENTER_ERROR_LOWER_LEG_WEIGHT)

		return averageYaw.toAngle()
	}

	/**
	 * Adjusts the yaw by applying gradient descent.
	 *
	 * The neighbouring force adjusts the tracker's yaw so that it is balanced
	 * between its neighbouring trackers. For example, if the player is standing in a
	 * very wide stance, the neighbouring force will push the upper leg tracker to a
	 * position that is proportional to their relaxed pose. This keeps the poses
	 * balanced.
	 *
	 * We use gradient descent to find the direction to apply a yaw correction. By
	 * applying this several times a second, the whole body is nudged into a reasonable
	 * alignment.
	 */
	private fun adjustByError(
		tracker: Tracker,
		yawCorrection: Angle,
		errorFn: (tracker: TrackerState) -> YawErrors,
	) {
		val trackerState = tracker.context.state.value
		val curYaw = trackerState.stayAlignedData.yawCorrection
		val curError = errorFn(trackerState)

		val posYaw = curYaw + yawCorrection
		tracker.context.dispatch(TrackerActions.SetYawCorrection(posYaw))
		val posError = errorFn(trackerState)

		val negYaw = curYaw - yawCorrection
		tracker.context.dispatch(TrackerActions.SetYawCorrection(negYaw))
		val negError = errorFn(trackerState)

		val posYawDelta = gradient(posError, curError)
		val negYawDelta = gradient(negError, curError)

		// Pick the yaw correction that minimizes the error
		val yawCorrectionResult = if ((posYawDelta < Angle.ZERO) && (posYawDelta < negYawDelta)) {
			posYaw
		} else if (negYawDelta < Angle.ZERO) {
			negYaw
		} else {
			curYaw
		}
		tracker.context.dispatch(TrackerActions.SetYawCorrection(yawCorrectionResult))
	}

	/**
	 * Calculates the gradient between two errors. A negative gradient means that there
	 * is less error in that direction.
	 */
	private fun gradient(errors: YawErrors, base: YawErrors) = (errors.lockedError.toL2Norm() - base.lockedError.toL2Norm()) *
		StayAlignedDefaults.YAW_ERRORS_LOCKED_ERROR_WEIGHT +
		(errors.centerError.toL2Norm() - base.centerError.toL2Norm()) *
		StayAlignedDefaults.YAW_ERRORS_CENTER_ERROR_WEIGHT +
		(errors.neighborError.toL2Norm() - base.neighborError.toL2Norm()) *
		StayAlignedDefaults.YAW_ERRORS_NEIGHBOR_ERROR_WEIGHT
}

fun getStayAlignedRotation(state: TrackerState) = applyCalibration(Quaternion.rotationAroundYAxis(state.stayAlignedData.yawCorrection.toRad()) * state.rawRotation, state)
