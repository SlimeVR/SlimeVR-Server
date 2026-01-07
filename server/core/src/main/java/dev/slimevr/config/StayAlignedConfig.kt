package dev.slimevr.config

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
@Serializable
class StayAlignedConfig {

	/**
	 * Apply yaw correction
	 */
	var enabled = false

	/**
	 * Temporarily hide the yaw correction from Stay Aligned.
	 *
	 * Players can enable this to compare to when Stay Aligned is not enabled. Useful to
	 * verify if Stay Aligned improved the situation. Also useful to prevent players
	 * from saying "Stay Aligned screwed up my trackers!!" when it's actually a tracker
	 * that is drifting extremely badly.
	 *
	 * Do not serialize to config so that when the server restarts, it is always false.
	 */
	@Transient
	var hideYawCorrection = false

	/**
	 * Standing relaxed pose
	 */
	val standingRelaxedPose = StayAlignedRelaxedPoseConfig()

	/**
	 * Sitting relaxed pose
	 */
	val sittingRelaxedPose = StayAlignedRelaxedPoseConfig()

	/**
	 * Flat relaxed pose
	 */
	val flatRelaxedPose = StayAlignedRelaxedPoseConfig()

	/**
	 * Whether setup has been completed
	 */
	var setupComplete = false
}
