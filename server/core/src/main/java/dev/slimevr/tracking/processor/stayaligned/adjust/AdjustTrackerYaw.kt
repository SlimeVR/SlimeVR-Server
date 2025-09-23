package dev.slimevr.tracking.processor.stayaligned.adjust

import dev.slimevr.config.StayAlignedConfig
import dev.slimevr.math.Angle
import dev.slimevr.tracking.processor.stayaligned.StayAlignedDefaults.YAW_ERRORS_CENTER_ERROR_WEIGHT
import dev.slimevr.tracking.processor.stayaligned.StayAlignedDefaults.YAW_ERRORS_LOCKED_ERROR_WEIGHT
import dev.slimevr.tracking.processor.stayaligned.StayAlignedDefaults.YAW_ERRORS_NEIGHBOR_ERROR_WEIGHT
import dev.slimevr.tracking.processor.stayaligned.poses.PlayerPose
import dev.slimevr.tracking.processor.stayaligned.poses.RelaxedPose
import dev.slimevr.tracking.processor.stayaligned.trackers.RestDetector
import dev.slimevr.tracking.processor.stayaligned.trackers.TrackerSkeleton
import dev.slimevr.tracking.processor.stayaligned.trackers.YawErrors
import dev.slimevr.tracking.trackers.Tracker
import dev.slimevr.tracking.trackers.udp.MagnetometerStatus

object AdjustTrackerYaw {

	/**
	 * Adjusts the yaw of a tracker.
	 *
	 * Locked Trackers
	 * ---------------
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
	 *
	 * Centering Force
	 * ---------------
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
	 *
	 * Neighbor Trackers
	 * -----------------
	 * The neighboring force adjusts the tracker's yaw so that it is balanced
	 * between its neighboring trackers. For example, if the player is standing in a
	 * very wide stance, the neighboring force will push the upper leg tracker to a
	 * position that is proportional to their relaxed pose. This keeps the poses
	 * balanced.
	 *
	 * We use gradient descent to find the direction to apply a yaw correction. By
	 * applying this 50 times a second, the whole body is nudged into a reasonable
	 * alignment.
	 */
	fun adjust(
		tracker: Tracker,
		trackers: TrackerSkeleton,
		yawCorrection: Angle,
		config: StayAlignedConfig,
	) {
		// Only IMUs can drift
		if (!tracker.isImu()) {
			return
		}

		// Skip trackers that use magnetometer, because the magnetometer should know the
		// absolute yaw of the tracker
		if (tracker.magStatus == MagnetometerStatus.ENABLED) {
			return
		}

		// Clear errors, in case we don't adjust the tracker
		val state = tracker.stayAligned
		state.yawErrors = YawErrors()

		val restDetector = state.restDetector
		when (restDetector.state) {
			RestDetector.State.MOVING ->
				adjustMovingTracker(tracker, trackers, yawCorrection, config)

			RestDetector.State.AT_REST ->
				adjustLockedTracker(tracker, trackers, yawCorrection)

			RestDetector.State.RECENTLY_AT_REST -> {
				// Do not adjust trackers that were recently at rest, to support play
				// styles that are primarily at rest
			}
		}
	}

	/**
	 * Adjusts a locked tracker.
	 */
	private fun adjustLockedTracker(
		tracker: Tracker,
		trackers: TrackerSkeleton,
		yawCorrection: Angle,
	) {
		val lockedRotation = tracker.stayAligned.lockedRotation ?: return

		adjustByError(tracker, yawCorrection) {
			YawErrors().also {
				trackers.visit(tracker, LockedErrorVisitor(lockedRotation, it.lockedError))
			}
		}
	}

	/**
	 * Adjusts a tracker that is moving.
	 */
	private fun adjustMovingTracker(
		tracker: Tracker,
		trackers: TrackerSkeleton,
		yawCorrection: Angle,
		config: StayAlignedConfig,
	) {
		val centerYaw = CenterYaw.ofSkeleton(trackers) ?: return

		val pose = PlayerPose.ofTrackers(trackers)
		val relaxedPose = RelaxedPose.forPose(pose, config) ?: return

		adjustByError(tracker, yawCorrection) {
			YawErrors().also {
				trackers.visit(tracker, CenterErrorVisitor(centerYaw, relaxedPose, it.centerError))
				trackers.visit(tracker, NeighborErrorVisitor(relaxedPose, it.neighborError))
			}
		}
	}

	/**
	 * Adjusts the yaw by applying gradient descent.
	 */
	private fun adjustByError(
		tracker: Tracker,
		yawCorrection: Angle,
		errorFn: (tracker: Tracker) -> YawErrors,
	) {
		val state = tracker.stayAligned

		val curYaw = state.yawCorrection
		val curError = errorFn(tracker)

		val posYaw = curYaw + yawCorrection
		state.yawCorrection = posYaw
		val posError = errorFn(tracker)

		val negYaw = curYaw - yawCorrection
		state.yawCorrection = negYaw
		val negError = errorFn(tracker)

		val posYawDelta = gradient(posError, curError)
		val negYawDelta = gradient(negError, curError)

		// Pick the yaw correction that minimizes the error
		if ((posYawDelta < Angle.ZERO) && (posYawDelta < negYawDelta)) {
			state.yawCorrection = posYaw
			state.yawErrors = posError
		} else if (negYawDelta < Angle.ZERO) {
			state.yawCorrection = negYaw
			state.yawErrors = negError
		} else {
			state.yawCorrection = curYaw
			state.yawErrors = curError
		}
	}

	/**
	 * Calculates the gradient between two errors. A negative gradient means that there
	 * is less error in that direction.
	 */
	private fun gradient(errors: YawErrors, base: YawErrors) = (errors.lockedError.toL2Norm() - base.lockedError.toL2Norm()) *
		YAW_ERRORS_LOCKED_ERROR_WEIGHT +
		(errors.centerError.toL2Norm() - base.centerError.toL2Norm()) *
		YAW_ERRORS_CENTER_ERROR_WEIGHT +
		(errors.neighborError.toL2Norm() - base.neighborError.toL2Norm()) *
		YAW_ERRORS_NEIGHBOR_ERROR_WEIGHT
}
