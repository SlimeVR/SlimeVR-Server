package dev.slimevr.config

import kotlinx.serialization.Serializable

@Serializable
data class StayAlignedRelaxedPoseConfig(
	/**
	 * Whether Stay Aligned should adjust the tracker yaws when the player is in this
	 * pose.
	 */
	val enabled: Boolean = false,

	/**
	 * Angle between the upper leg yaw and the center yaw.
	 */
	val upperLegAngleInDeg: Float = 0.0f,

	/**
	 * Angle between the lower leg yaw and the center yaw.
	 */
	val lowerLegAngleInDeg: Float = 0.0f,

	/**
	 * Angle between the foot and the center yaw.
	 */
	val footAngleInDeg: Float = 0.0f,
)
