package dev.slimevr.tracking.processor.stayaligned.skeleton

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
) {
	override fun toString(): String =
		"upperLeg=$upperLeg lowerLeg=$lowerLeg foot=$foot"

	companion object {

		val ZERO = RelaxedPose(Angle.ZERO, Angle.ZERO, Angle.ZERO)

		/**
		 * Gets the relaxed angles for a particular pose. May provide defaults if the
		 * angles aren't configured for the pose.
		 */
		fun forPose(
			playerPose: PlayerPose,
			config: StayAlignedConfig,
		) =
			when (playerPose) {
				PlayerPose.STANDING ->
					RelaxedPose(
						Angle.ofDeg(config.standingRelaxedPose.upperLegAngleInDeg),
						Angle.ofDeg(config.standingRelaxedPose.lowerLegAngleInDeg),
						Angle.ofDeg(config.standingRelaxedPose.footAngleInDeg),
					)

				PlayerPose.SITTING_IN_CHAIR ->
					RelaxedPose(
						Angle.ofDeg(config.sittingRelaxedPose.upperLegAngleInDeg),
						Angle.ofDeg(config.sittingRelaxedPose.lowerLegAngleInDeg),
						Angle.ofDeg(config.sittingRelaxedPose.footAngleInDeg),
					)

				PlayerPose.SITTING_ON_GROUND,
				PlayerPose.LYING_ON_BACK,
				->
					RelaxedPose(
						Angle.ofDeg(config.flatRelaxedPose.upperLegAngleInDeg),
						Angle.ofDeg(config.flatRelaxedPose.lowerLegAngleInDeg),
						Angle.ofDeg(config.flatRelaxedPose.footAngleInDeg),
					)

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

			return RelaxedPose(upperLegAngle, lowerLegAngle, footAngle)
		}
	}
}
