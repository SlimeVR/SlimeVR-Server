package dev.slimevr.tracking.processor.stayaligned.poses

import dev.slimevr.config.StayAlignedConfig
import dev.slimevr.math.Angle
import dev.slimevr.tracking.processor.skeleton.HumanSkeleton
import dev.slimevr.tracking.processor.stayaligned.StayAlignedDefaults
import dev.slimevr.tracking.processor.stayaligned.adjust.TrackerYaw.trackerYaw
import dev.slimevr.tracking.trackers.Tracker

class RelaxedPose(
	val upperLeg: Angle,
	val lowerLeg: Angle,
	val foot: Angle,
	val toe1: Angle,
	val toe2: Angle,
	val toe3: Angle,
) {
	override fun toString(): String = "upperLeg=$upperLeg lowerLeg=$lowerLeg foot=$foot"

	companion object {

		val ZERO = RelaxedPose(Angle.ZERO, Angle.ZERO, Angle.ZERO, Angle.ZERO, toe2 = Angle.ZERO, toe3 = Angle.ZERO)

		/**
		 * Gets the relaxed angles for a particular pose. May provide defaults if the
		 * angles aren't configured for the pose.
		 */
		fun forPose(
			playerPose: PlayerPose,
			config: StayAlignedConfig,
		) =
			when (playerPose) {
				PlayerPose.STANDING -> {
					val poseConfig = config.standingRelaxedPose
					if (poseConfig.enabled) {
						RelaxedPose(
							Angle.ofDeg(poseConfig.upperLegAngleInDeg),
							Angle.ofDeg(poseConfig.lowerLegAngleInDeg),
							Angle.ofDeg(poseConfig.footAngleInDeg),
							Angle.ofDeg(poseConfig.toe1AngleInDeg),
							Angle.ofDeg(poseConfig.toe2AngleInDeg),
							Angle.ofDeg(poseConfig.toe3AngleInDeg),
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
							Angle.ofDeg(poseConfig.toe1AngleInDeg),
							Angle.ofDeg(poseConfig.toe2AngleInDeg),
							Angle.ofDeg(poseConfig.toe3AngleInDeg),
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
							Angle.ofDeg(poseConfig.toe1AngleInDeg),
							Angle.ofDeg(poseConfig.toe2AngleInDeg),
							Angle.ofDeg(poseConfig.toe3AngleInDeg),
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
		fun fromTrackers(humanSkeleton: HumanSkeleton): RelaxedPose {
			val halfAngleBetween = { left: Tracker, right: Tracker ->
				(trackerYaw(left) - trackerYaw(right)) * 0.5f
			}

			var upperLegAngle = Angle.ZERO
			humanSkeleton.leftUpperLegTracker?.let { left ->
				humanSkeleton.rightUpperLegTracker?.let { right ->
					upperLegAngle = halfAngleBetween(left, right)
				}
			}

			var lowerLegAngle = Angle.ZERO
			humanSkeleton.leftLowerLegTracker?.let { left ->
				humanSkeleton.rightLowerLegTracker?.let { right ->
					lowerLegAngle = halfAngleBetween(left, right)
				}
			}

			var footAngle = Angle.ZERO
			humanSkeleton.leftFootTracker?.let { left ->
				humanSkeleton.rightFootTracker?.let { right ->
					footAngle = halfAngleBetween(left, right)
				}
			}

			var toe1Angle = Angle.ZERO
			humanSkeleton.leftToe1Tracker?.let { left ->
				humanSkeleton.rightToe1Tracker?.let { right ->
					toe1Angle = halfAngleBetween(left, right)
				}
			}
			var toe2Angle = Angle.ZERO
			humanSkeleton.leftToe2Tracker?.let { left ->
				humanSkeleton.rightToe2Tracker?.let { right ->
					toe2Angle = halfAngleBetween(left, right)
				}
			}
			var toe3Angle = Angle.ZERO
			humanSkeleton.leftToe3Tracker?.let { left ->
				humanSkeleton.rightToe3Tracker?.let { right ->
					toe3Angle = halfAngleBetween(left, right)
				}
			}

			return RelaxedPose(upperLegAngle, lowerLegAngle, footAngle,toe1Angle, toe2Angle, toe3Angle)
		}
	}
}
