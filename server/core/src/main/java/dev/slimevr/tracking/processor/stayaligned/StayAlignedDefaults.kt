package dev.slimevr.tracking.processor.stayaligned

import dev.slimevr.math.Angle
import dev.slimevr.tracking.processor.stayaligned.poses.RelaxedPose
import dev.slimevr.tracking.processor.stayaligned.trackers.RestDetector
import dev.slimevr.tracking.trackers.udp.IMUType
import kotlin.time.Duration.Companion.seconds

/**
 * All non-user-configurable defaults used by Stay Aligned, so that we can tune the
 * algorithm from a single place.
 */
object StayAlignedDefaults {

	// Rest detector for detecting when trackers are at rest
	fun makeRestDetector() = RestDetector(
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

	// Yaw correction for each type of IMU
	val YAW_CORRECTION_IMU_GOOD = Angle.ofDeg(0.15f)
	val YAW_CORRECTION_IMU_OK = Angle.ofDeg(0.20f)
	val YAW_CORRECTION_IMU_BAD = Angle.ofDeg(0.40f)
	val YAW_CORRECTION_IMU_DISABLED = Angle.ZERO

	val IMU_TO_YAW_CORRECTION = buildMap {
		// Mag is enabled on MPU9250 but server doesn't know about it
		set(IMUType.MPU9250, YAW_CORRECTION_IMU_DISABLED)
		set(IMUType.MPU6500, YAW_CORRECTION_IMU_BAD)
		set(IMUType.BNO080, YAW_CORRECTION_IMU_GOOD)
		set(IMUType.BNO085, YAW_CORRECTION_IMU_GOOD)
		set(IMUType.BNO055, YAW_CORRECTION_IMU_BAD)
		set(IMUType.MPU6050, YAW_CORRECTION_IMU_BAD)
		set(IMUType.BNO086, YAW_CORRECTION_IMU_GOOD)
		set(IMUType.BMI160, YAW_CORRECTION_IMU_BAD)
		set(IMUType.ICM20948, YAW_CORRECTION_IMU_BAD)
		set(IMUType.ICM42688, YAW_CORRECTION_IMU_OK)
		set(IMUType.BMI270, YAW_CORRECTION_IMU_OK)
		set(IMUType.LSM6DS3TRC, YAW_CORRECTION_IMU_BAD)
		set(IMUType.LSM6DSV, YAW_CORRECTION_IMU_GOOD)
		set(IMUType.LSM6DSO, YAW_CORRECTION_IMU_OK)
		set(IMUType.LSM6DSR, YAW_CORRECTION_IMU_GOOD)
		set(IMUType.ICM45686, YAW_CORRECTION_IMU_GOOD)
		set(IMUType.ICM45605, YAW_CORRECTION_IMU_GOOD)
	}

	// Assume any new IMUs are at least OK, or else we wouldn't be writing firmware to
	// support it. Please classify and add new IMUs to the map above!
	val YAW_CORRECTION_DEFAULT = YAW_CORRECTION_IMU_OK
}
