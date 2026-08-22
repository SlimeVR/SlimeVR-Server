package dev.slimevr.tracker.stayaligned.poses

import dev.slimevr.config.StayAlignedConfig
import dev.slimevr.math.angle.Angle
import dev.slimevr.tracker.TrackerState
import dev.slimevr.tracker.getFirstFineFor
import dev.slimevr.tracker.stayaligned.StayAlignedBodyParts
import dev.slimevr.tracker.stayaligned.StayAlignedDefaults
import dev.slimevr.tracker.stayaligned.YawUtils.trackerYaw

class RelaxedPose(
	val upperLeg: Angle,
	val lowerLeg: Angle,
	val foot: Angle,
) {
	override fun toString(): String = "upperLeg=$upperLeg lowerLeg=$lowerLeg foot=$foot"

	companion object {

		val ZERO = RelaxedPose(Angle.ZERO, Angle.ZERO, Angle.ZERO)

		/**
		 * Gets the relaxed angles for a particular pose. May provide defaults if the
		 * angles aren't configured for the pose.
		 */
		fun forPose(
			playerPose: PlayerPose,
			config: StayAlignedConfig,
		) = when (playerPose) {
			PlayerPose.STANDING -> {
				val poseConfig = config.standingRelaxedPose
				if (poseConfig.enabled) {
					RelaxedPose(
						Angle.ofDeg(poseConfig.upperLegAngleInDeg),
						Angle.ofDeg(poseConfig.lowerLegAngleInDeg),
						Angle.ofDeg(poseConfig.footAngleInDeg),
					)
				} else {
					null
				}
			}

			PlayerPose.SITTING_IN_CHAIR -> {
				val poseConfig = config.sittingRelaxedPose
				if (poseConfig.enabled) {
					RelaxedPose(
						Angle.ofDeg(poseConfig.upperLegAngleInDeg),
						Angle.ofDeg(poseConfig.lowerLegAngleInDeg),
						Angle.ofDeg(poseConfig.footAngleInDeg),
					)
				} else {
					null
				}
			}

			PlayerPose.SITTING_ON_GROUND,
			PlayerPose.LYING_ON_BACK,
			-> {
				val poseConfig = config.flatRelaxedPose
				if (poseConfig.enabled) {
					RelaxedPose(
						Angle.ofDeg(poseConfig.upperLegAngleInDeg),
						Angle.ofDeg(poseConfig.lowerLegAngleInDeg),
						Angle.ofDeg(poseConfig.footAngleInDeg),
					)
				} else {
					null
				}
			}

			PlayerPose.KNEELING ->
				StayAlignedDefaults.RELAXED_POSE_KNEELING

			else ->
				null
		}

		/**
		 * Gets the relaxed angles from the trackers.
		 */
		fun fromTrackers(trackerStates: List<TrackerState>): RelaxedPose {
			val halfAngleBetween = { left: TrackerState, right: TrackerState ->
				(trackerYaw(left.rotation) - trackerYaw(right.rotation)) * 0.5f
			}

			val upperLegAngle: Angle = trackerStates.getFirstFineFor(StayAlignedBodyParts.leftUpperLeg)?.let { left ->
				trackerStates.getFirstFineFor(StayAlignedBodyParts.rightUpperLeg)?.let { right ->
					halfAngleBetween(left, right)
				}
			} ?: Angle.ZERO

			val lowerLegAngle: Angle = trackerStates.getFirstFineFor(StayAlignedBodyParts.leftLowerLeg)?.let { left ->
				trackerStates.getFirstFineFor(StayAlignedBodyParts.rightLowerLeg)?.let { right ->
					halfAngleBetween(left, right)
				}
			} ?: Angle.ZERO

			val footAngle: Angle = trackerStates.getFirstFineFor(StayAlignedBodyParts.leftFoot)?.let { left ->
				trackerStates.getFirstFineFor(StayAlignedBodyParts.rightFoot)?.let { right ->
					halfAngleBetween(left, right)
				}
			} ?: Angle.ZERO

			return RelaxedPose(upperLegAngle, lowerLegAngle, footAngle)
		}
	}
}
