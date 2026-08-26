package dev.slimevr.tracker.stayaligned

import dev.slimevr.config.StayAlignedConfig
import dev.slimevr.math.angle.Angle
import dev.slimevr.math.angle.AngleErrors
import dev.slimevr.tracker.Motion
import dev.slimevr.tracker.TrackerState
import dev.slimevr.tracker.applyCalibration
import dev.slimevr.tracker.getAllFineFor
import dev.slimevr.tracker.getFirstFineFor
import dev.slimevr.tracker.stayaligned.YawUtils.sideYaw
import dev.slimevr.tracker.stayaligned.YawUtils.trackerYaw
import dev.slimevr.tracker.stayaligned.poses.PlayerPose
import dev.slimevr.tracker.stayaligned.poses.RelaxedPose
import dev.slimevr.util.Side
import dev.slimevr.util.side
import io.github.axisangles.ktmath.Quaternion

/**
 * Entry point for Stay Aligned. More specifically, computeYawCorrection is.
 */
object TrackerYawCorrection {

	/**
	 * Aggregates the yaw errors from multiple forces.
	 */
	private data class YawErrors(
		var lockedError: AngleErrors = AngleErrors(),
		var centerError: AngleErrors = AngleErrors(),
		var neighbourError: AngleErrors = AngleErrors(),
	)

	/**
	 * Adjusts the yaw of a tracker depending on its motion.
	 */
	fun computeYawCorrection(
		trackerState: TrackerState,
		trackerStates: List<TrackerState>,
		applyYawCorrection: Angle,
		config: StayAlignedConfig,
	): Angle? = when (trackerState.motion) {
		Motion.ROTATING -> adjustMovingTracker(trackerState, trackerStates, applyYawCorrection, config)

		Motion.RESTING -> adjustLockedTracker(trackerState, applyYawCorrection)

		// Do not adjust trackers that were recently resting
		// to support play styles that are primarily at rest
		Motion.STARTED_ROTATING -> null
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
		trackerState: TrackerState,
		applyYawCorrection: Angle,
	): Angle? {
		val lockedRotation = trackerState.stayAlignedData.lockedRotation ?: return null

		return adjustByError(trackerState, applyYawCorrection) { yawCorrection ->
			YawErrors().also {
				it.lockedError.add(YawUtils.yawDifference(computeYawCorrectedRotation(yawCorrection, trackerState), lockedRotation))
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
		trackerState: TrackerState,
		trackerStates: List<TrackerState>,
		applyYawCorrection: Angle,
		config: StayAlignedConfig,
	): Angle? {
		val centerYaw = YawUtils.centerYawOfTrackers(trackerStates) ?: return null
		val relaxedPose = RelaxedPose.forPose(PlayerPose.of(trackerStates), config) ?: return null

		return adjustByError(trackerState, applyYawCorrection) { yawCorrection ->
			YawErrors().also {
				val yawCorrectedRotation = computeYawCorrectedRotation(yawCorrection, trackerState)
				it.centerError.add(getCenterError(yawCorrectedRotation, trackerState, centerYaw, relaxedPose))
				it.neighbourError.add(getNeighbourError(yawCorrectedRotation, trackerState, relaxedPose, trackerStates))
			}
		}
	}

	/**
	 * Returns an error based off the yaw difference from a tracker and the centre yaw.
	 */
	private fun getCenterError(yawCorrectedRotation: Quaternion, trackerState: TrackerState, centerYaw: Angle, relaxedPose: RelaxedPose): Angle {
		val bodyPart = trackerState.bodyPart
		val side = bodyPart?.side ?: Side.LEFT

		val poseYaw = when (bodyPart) {
			StayAlignedBodyParts.head,
			in StayAlignedBodyParts.upperBodyGroup,
			-> Angle.ZERO

			StayAlignedBodyParts.leftUpperLeg,
			StayAlignedBodyParts.rightUpperLeg,
			-> sideYaw(side, relaxedPose.upperLeg)

			StayAlignedBodyParts.leftLowerLeg,
			StayAlignedBodyParts.rightLowerLeg,
			-> sideYaw(side, relaxedPose.lowerLeg)

			StayAlignedBodyParts.leftFoot,
			StayAlignedBodyParts.rightFoot,
			-> sideYaw(side, relaxedPose.foot)

			// No error for others
			else -> return Angle.ZERO
		}

		return centerYaw + poseYaw - trackerYaw(yawCorrectedRotation)
	}

	/**
	 * Returns an error based off the yaw difference from a tracker and the next upper and lower trackers (neighbours).
	 */
	private fun getNeighbourError(yawCorrectedRotation: Quaternion, trackerState: TrackerState, relaxedPose: RelaxedPose, trackerStates: List<TrackerState>): Angle {
		fun neighbourError(rotation: Quaternion) = trackerYaw(rotation) - trackerYaw(yawCorrectedRotation)
		val upperBodyTrackers = trackerStates.getAllFineFor(StayAlignedBodyParts.upperBodyGroup).sortedBy { StayAlignedBodyParts.upperBodyOrder[it.bodyPart] }
		val bodyPart = trackerState.bodyPart
		val side = bodyPart?.side ?: Side.LEFT

		return when (bodyPart) {
			StayAlignedBodyParts.head if (upperBodyTrackers.isNotEmpty()) -> neighbourError(upperBodyTrackers.first().rotation)

			in StayAlignedBodyParts.upperBodyGroup -> {
				// Index of this tracker in upperBodyTrackers. 0 = highest on body.
				val trackerUpperBodyIndex = upperBodyTrackers.map { it.bodyPart }.indexOf(trackerState.bodyPart)

				// Compute upper legs error
				val leftUpperLeg = trackerStates.getFirstFineFor(StayAlignedBodyParts.leftUpperLeg)
				val rightUpperLeg = trackerStates.getFirstFineFor(StayAlignedBodyParts.rightUpperLeg)
				val upperLegsError = if (leftUpperLeg != null && rightUpperLeg != null) {
					neighbourError(leftUpperLeg.rotation) -
						sideYaw(Side.LEFT, relaxedPose.upperLeg) +
						neighbourError(rightUpperLeg.rotation) -
						sideYaw(Side.RIGHT, relaxedPose.upperLeg)
				} else {
					Angle.ZERO
				}

				when {
					// Only upper body tracker (error from the upper legs)
					upperBodyTrackers.size == 1 -> upperLegsError

					// First upper body tracker (error from the second upper body tracker)
					trackerUpperBodyIndex == 0 -> neighbourError(upperBodyTrackers[1].rotation)

					// Last upper body tracker (error from next upper tracker and upper legs)
					trackerUpperBodyIndex == upperBodyTrackers.size - 1 -> neighbourError(upperBodyTrackers[trackerUpperBodyIndex - 1].rotation) + upperLegsError

					// Middle upper body tracker (error from the next upper and lower trackers)
					else -> neighbourError(upperBodyTrackers[trackerUpperBodyIndex - 1].rotation) +
						neighbourError(upperBodyTrackers[trackerUpperBodyIndex + 1].rotation)
				}
			}

			StayAlignedBodyParts.leftUpperLeg,
			StayAlignedBodyParts.rightUpperLeg,
			-> {
				val lastUpperBodyTracker = upperBodyTrackers.lastOrNull()
				val upperBodyError = if (lastUpperBodyTracker != null) {
					neighbourError(lastUpperBodyTracker.rotation) + sideYaw(Side.LEFT, relaxedPose.upperLeg)
				} else {
					Angle.ZERO
				}

				val lowerLeg = trackerStates.getFirstFineFor(StayAlignedBodyParts.lowerLeg(side))
				val lowerLegError = if (lowerLeg != null) {
					neighbourError(lowerLeg.rotation) - sideYaw(side, relaxedPose.lowerLeg) + sideYaw(side, relaxedPose.upperLeg)
				} else {
					Angle.ZERO
				}

				upperBodyError + lowerLegError
			}

			StayAlignedBodyParts.leftLowerLeg,
			StayAlignedBodyParts.rightLowerLeg,
			-> {
				val upperLeg = trackerStates.getFirstFineFor(StayAlignedBodyParts.upperLeg(side))
				val upperLegError = if (upperLeg != null) {
					neighbourError(upperLeg.rotation) - sideYaw(side, relaxedPose.upperLeg) + sideYaw(side, relaxedPose.lowerLeg)
				} else {
					Angle.ZERO
				}

				val foot = trackerStates.getFirstFineFor(StayAlignedBodyParts.foot(side))
				val footError = if (foot != null) {
					neighbourError(foot.rotation) - sideYaw(side, relaxedPose.foot) + sideYaw(side, relaxedPose.lowerLeg)
				} else {
					Angle.ZERO
				}

				upperLegError + footError
			}

			StayAlignedBodyParts.leftFoot,
			StayAlignedBodyParts.rightFoot,
			-> {
				val lowerLeg = trackerStates.getFirstFineFor(StayAlignedBodyParts.lowerLeg(side))
				if (lowerLeg != null) {
					neighbourError(lowerLeg.rotation) - sideYaw(side, relaxedPose.lowerLeg) + sideYaw(side, relaxedPose.foot)
				} else {
					Angle.ZERO
				}
			}

			// No error for others
			else -> Angle.ZERO
		}
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
		trackerState: TrackerState,
		applyYawCorrection: Angle,
		errorFn: (yawCorrection: Angle) -> YawErrors,
	): Angle {
		val curYaw = trackerState.stayAlignedData.yawCorrection
		val curError = errorFn(curYaw)

		val posYaw = curYaw + applyYawCorrection
		val posError = errorFn(posYaw)

		val negYaw = curYaw - applyYawCorrection
		val negError = errorFn(negYaw)

		val posYawDelta = gradient(posError, curError)
		val negYawDelta = gradient(negError, curError)

		// Pick the yaw correction that minimizes the error
		return if ((posYawDelta < Angle.ZERO) && (posYawDelta < negYawDelta)) {
			posYaw
		} else if (negYawDelta < Angle.ZERO) {
			negYaw
		} else {
			curYaw
		}
	}

	/**
	 * Calculates the gradient between two errors. A negative gradient means that there
	 * is less error in that direction.
	 */
	private fun gradient(errors: YawErrors, base: YawErrors) = (errors.lockedError.toL2Norm() - base.lockedError.toL2Norm()) *
		StayAlignedDefaults.YAW_ERRORS_LOCKED_ERROR_WEIGHT +
		(errors.centerError.toL2Norm() - base.centerError.toL2Norm()) *
		StayAlignedDefaults.YAW_ERRORS_CENTER_ERROR_WEIGHT +
		(errors.neighbourError.toL2Norm() - base.neighbourError.toL2Norm()) *
		StayAlignedDefaults.YAW_ERRORS_NEIGHBOUR_ERROR_WEIGHT

	/**
	 * Returns the calibrated tracker rotation with the yawCorrection applied.
	 */
	private fun computeYawCorrectedRotation(yawCorrection: Angle, state: TrackerState): Quaternion {
		val yawCorrectedRawRotation = Quaternion.rotationAroundYAxis(yawCorrection.toRad()) * state.rawRotation
		val cal = state.sessionCalibration
		return applyCalibration(yawCorrectedRawRotation, cal.headingCorrection, cal.attitudeAlignment, cal.headingAlignment, state.restOrientation)
	}
}
