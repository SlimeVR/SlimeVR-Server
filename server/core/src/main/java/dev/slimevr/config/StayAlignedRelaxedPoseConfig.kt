package dev.slimevr.config

class StayAlignedRelaxedPoseConfig {

	/**
	 * Whether Stay Aligned should adjust the tracker yaws when the player is in this
	 * pose.
	 */
	var enabled = false

	/**
	 * Angle between the upper leg yaw and the center yaw.
	 */
	var upperLegAngleInDeg = 0.0f

	/**
	 * Angle between the lower leg yaw and the center yaw.
	 */
	var lowerLegAngleInDeg = 0.0f

	/**
	 * Angle between the foot and the center yaw.
	 */
	var footAngleInDeg = 0.0f
}
