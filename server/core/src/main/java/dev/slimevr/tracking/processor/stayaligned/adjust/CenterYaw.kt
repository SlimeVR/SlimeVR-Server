package dev.slimevr.tracking.processor.stayaligned.adjust

import dev.slimevr.math.Angle
import dev.slimevr.math.AngleAverage
import dev.slimevr.tracking.processor.stayaligned.StayAlignedDefaults.CENTER_ERROR_HEAD_WEIGHT
import dev.slimevr.tracking.processor.stayaligned.StayAlignedDefaults.CENTER_ERROR_LOWER_LEG_WEIGHT
import dev.slimevr.tracking.processor.stayaligned.StayAlignedDefaults.CENTER_ERROR_UPPER_BODY_WEIGHT
import dev.slimevr.tracking.processor.stayaligned.StayAlignedDefaults.CENTER_ERROR_UPPER_LEG_WEIGHT
import dev.slimevr.tracking.processor.stayaligned.adjust.TrackerYaw.hasTrackerYaw
import dev.slimevr.tracking.processor.stayaligned.adjust.TrackerYaw.trackerYaw
import dev.slimevr.tracking.processor.stayaligned.trackers.TrackerSkeleton

object CenterYaw {

	fun ofSkeleton(
		trackers: TrackerSkeleton,
	): Angle? {
		val head = trackers.head
		val upperBody = trackers.upperBody
		val leftUpperLeg = trackers.leftUpperLeg
		val rightUpperLeg = trackers.rightUpperLeg
		val leftLowerLeg = trackers.leftLowerLeg
		val rightLowerLeg = trackers.rightLowerLeg

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
			averageYaw.add(trackerYaw(head), CENTER_ERROR_HEAD_WEIGHT)
		}

		upperBody.forEach {
			averageYaw.add(trackerYaw(it), CENTER_ERROR_UPPER_BODY_WEIGHT)
		}

		averageYaw.add(trackerYaw(leftUpperLeg), CENTER_ERROR_UPPER_LEG_WEIGHT)
		averageYaw.add(trackerYaw(rightUpperLeg), CENTER_ERROR_UPPER_LEG_WEIGHT)

		averageYaw.add(trackerYaw(leftLowerLeg), CENTER_ERROR_LOWER_LEG_WEIGHT)
		averageYaw.add(trackerYaw(rightLowerLeg), CENTER_ERROR_LOWER_LEG_WEIGHT)

		return averageYaw.toAngle()
	}
}
