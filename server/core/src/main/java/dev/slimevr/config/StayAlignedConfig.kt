package dev.slimevr.config

import kotlinx.serialization.Serializable

@Serializable
data class StayAlignedConfig(
	/**
	 * Apply yaw correction
	 */
	val enabled: Boolean = false,

// 	/**
// 	 * Temporarily hide the yaw correction from Stay Aligned.
// 	 *
// 	 * Players can enable this to compare to when Stay Aligned is not enabled. Useful to
// 	 * verify if Stay Aligned improved the situation. Also useful to prevent players
// 	 * from saying "Stay Aligned screwed up my trackers!!" when it's actually a tracker
// 	 * that is drifting extremely badly.
// 	 *
// 	 * Do not serialize to config so that when the server restarts, it is always false.
// 	 */
// 	@Transient var hideYawCorrection: Boolean = false,
	/**
	 * Standing relaxed pose
	 */
	val standingRelaxedPose: StayAlignedRelaxedPoseConfig = StayAlignedRelaxedPoseConfig(),

	/**
	 * Sitting relaxed pose
	 */
	val sittingRelaxedPose: StayAlignedRelaxedPoseConfig = StayAlignedRelaxedPoseConfig(),

	/**
	 * Flat relaxed pose
	 */
	val flatRelaxedPose: StayAlignedRelaxedPoseConfig = StayAlignedRelaxedPoseConfig(),

	/**
	 * Whether setup has been completed
	 */
	val setupComplete: Boolean = false,
)
