package dev.slimevr.stayaligned.poses

import dev.slimevr.config.StayAlignedConfig
import dev.slimevr.math.angle.Angle
import dev.slimevr.stayaligned.StayAlignedDefaults
import dev.slimevr.stayaligned.YawUtils.trackerYaw
import dev.slimevr.tracker.TrackerState
import dev.slimevr.tracker.getFineFor
import solarxr_protocol.datatypes.BodyPart

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

		// TODO only used for this module, move it?

		/**
		 * Gets the relaxed angles from the trackers.
		 */
		fun fromTrackers(trackerStates: List<TrackerState>): RelaxedPose {
			val halfAngleBetween = { left: TrackerState, right: TrackerState ->
				(trackerYaw(left) - trackerYaw(right)) * 0.5f
			}

			val upperLegAngle: Angle = trackerStates.getFineFor(BodyPart.LEFT_UPPER_LEG)?.let { left ->
				trackerStates.getFineFor(BodyPart.RIGHT_UPPER_LEG)?.let { right ->
					halfAngleBetween(left, right)
				}
			} ?: Angle.ZERO

			val lowerLegAngle: Angle = trackerStates.getFineFor(BodyPart.LEFT_LOWER_LEG)?.let { left ->
				trackerStates.getFineFor(BodyPart.RIGHT_LOWER_LEG)?.let { right ->
					halfAngleBetween(left, right)
				}
			} ?: Angle.ZERO

			val footAngle: Angle = trackerStates.getFineFor(BodyPart.LEFT_FOOT)?.let { left ->
				trackerStates.getFineFor(BodyPart.RIGHT_FOOT)?.let { right ->
					halfAngleBetween(left, right)
				}
			} ?: Angle.ZERO

			return RelaxedPose(upperLegAngle, lowerLegAngle, footAngle)
		}
	}
}
