package dev.slimevr.tracker.stayaligned.poses

import dev.slimevr.config.UserConfigData
import dev.slimevr.math.angle.Angle
import dev.slimevr.tracker.TrackerState
import dev.slimevr.tracker.getFirstActiveFor
import dev.slimevr.tracker.stayaligned.StayAlignedBodyParts
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
			userConfig: UserConfigData,
		): RelaxedPose? {
			val poseConfig = stayAlignedPoseConfig(userConfig, playerPose.relaxedPose)
			if (!poseConfig.enabled) return null
			return RelaxedPose(
				Angle.ofDeg(poseConfig.upperLegAngleInDeg),
				Angle.ofDeg(poseConfig.lowerLegAngleInDeg),
				Angle.ofDeg(poseConfig.footAngleInDeg),
			)
		}

		/**
		 * Gets the relaxed angles from the trackers.
		 */
		fun fromTrackers(trackerStates: List<TrackerState>): RelaxedPose {
			val restingRotation = { state: TrackerState -> state.stayAlignedData.lockedRotation ?: state.rotation }
			val halfAngleBetween = { left: TrackerState, right: TrackerState ->
				(trackerYaw(restingRotation(left)) - trackerYaw(restingRotation(right))) * 0.5f
			}

			val upperLegAngle: Angle = trackerStates.getFirstActiveFor(StayAlignedBodyParts.leftUpperLeg)?.let { left ->
				trackerStates.getFirstActiveFor(StayAlignedBodyParts.rightUpperLeg)?.let { right ->
					halfAngleBetween(left, right)
				}
			} ?: Angle.ZERO

			val lowerLegAngle: Angle = trackerStates.getFirstActiveFor(StayAlignedBodyParts.leftLowerLeg)?.let { left ->
				trackerStates.getFirstActiveFor(StayAlignedBodyParts.rightLowerLeg)?.let { right ->
					halfAngleBetween(left, right)
				}
			} ?: Angle.ZERO

			val footAngle: Angle = trackerStates.getFirstActiveFor(StayAlignedBodyParts.leftFoot)?.let { left ->
				trackerStates.getFirstActiveFor(StayAlignedBodyParts.rightFoot)?.let { right ->
					halfAngleBetween(left, right)
				}
			} ?: Angle.ZERO

			return RelaxedPose(upperLegAngle, lowerLegAngle, footAngle)
		}
	}
}
