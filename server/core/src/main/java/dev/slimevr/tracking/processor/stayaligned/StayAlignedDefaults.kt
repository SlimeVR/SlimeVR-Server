package dev.slimevr.tracking.processor.stayaligned

import dev.slimevr.math.Angle
import dev.slimevr.tracking.processor.stayaligned.poses.RelaxedPose
import dev.slimevr.tracking.processor.stayaligned.trackers.RestDetector
import kotlin.time.Duration.Companion.seconds

/**
 * All non-user-configurable defaults used by Stay Aligned, so that we can tune the
 * algorithm from a single place.
 */
object StayAlignedDefaults {

	// Maximum yaw correction to apply
	val YAW_CORRECTION_PER_SEC = Angle.ofDeg(0.20f)

	// Extra yaw correction to apply to terrible IMUs
	val EXTRA_YAW_CORRECTION_PER_SEC = Angle.ofDeg(0.20f)

	// Rest detector for detecting when trackers are at rest
	fun makeRestDetector() =
		RestDetector(
			maxRotation = Angle.ofDeg(2.0f),
			enterRestTime = 1.seconds,
			enterMovingTime = 3.seconds,
		)

	// Relaxed pose for kneeling. This isn't that common, so we don't want to ask
	// players to provide this relaxed pose during setup.
	val RELAXED_POSE_KNEELING =
		RelaxedPose(
			upperLeg = Angle.ofDeg(0.0f),
			lowerLeg = Angle.ofDeg(0.0f),
			foot = Angle.ofDeg(0.0f),
		)

	// Weights to calculate the average yaw of the skeleton
	const val CENTER_ERROR_HEAD_WEIGHT = 0.5f
	const val CENTER_ERROR_UPPER_BODY_WEIGHT = 1.0f
	const val CENTER_ERROR_UPPER_LEG_WEIGHT = 0.4f
	const val CENTER_ERROR_LOWER_LEG_WEIGHT = 0.3f

	// Weight of each force
	const val YAW_ERRORS_LOCKED_ERROR_WEIGHT = 10.0f
	const val YAW_ERRORS_CENTER_ERROR_WEIGHT = 2.0f
	const val YAW_ERRORS_NEIGHBOR_ERROR_WEIGHT = 1.0f
}
