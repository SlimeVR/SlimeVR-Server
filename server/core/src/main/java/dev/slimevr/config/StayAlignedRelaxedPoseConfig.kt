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

	/**
	 * Angle between the abductorHallucis and the center yaw.
	 */
	var abductorHallucisAngleInDeg = 0.0f
	/**
	 * Angle between the digitorumBrevis and the center yaw.
	 */
	var digitorumBrevisAngleInDeg = 0.0f

	/**
	 * Angle between the abductorDigitiMinimi and the center yaw.
	 */
	var abductorDigitiMinimiAngleInDeg = 0.0f
}
