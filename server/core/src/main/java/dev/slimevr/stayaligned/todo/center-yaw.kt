package dev.slimevr.stayaligned.todo

import dev.slimevr.math.angle.Angle
import dev.slimevr.math.angle.AngleAverage
import dev.slimevr.stayaligned.StayAlignedDefaults
import dev.slimevr.stayaligned.todo.TrackerYaw.hasTrackerYaw
import dev.slimevr.stayaligned.todo.TrackerYaw.trackerYaw

object CenterYaw {
	fun ofTrackerGroup(
		trackerGroup: TrackerGroup,
	): Angle? {
		val head = trackerGroup.head
		val upperBody = trackerGroup.upperBody
		val leftUpperLeg = trackerGroup.leftUpperLeg
		val rightUpperLeg = trackerGroup.rightUpperLeg
		val leftLowerLeg = trackerGroup.leftLowerLeg
		val rightLowerLeg = trackerGroup.rightLowerLeg

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
}
