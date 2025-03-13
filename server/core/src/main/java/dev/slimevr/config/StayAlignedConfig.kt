package dev.slimevr.config

import com.fasterxml.jackson.annotation.JsonIgnore

class StayAlignedConfig {

	// Apply yaw correction
	var enabled = false

	// Applies extra yaw correction to support worse IMUs
	//
	// We could let players choose a yaw correction amount instead, but this lead to
	// players agonizing about choosing the "right" yaw correction amount. In practice,
	// we only need 2 yaw correction amounts - a default one for most IMUs, and an extra
	// one for terrible IMUs.
	var extraYawCorrection = false

	// Temporarily hide the yaw correction that Stay Aligned is applying, so that the
	// player can compare to if Stay Aligned was disabled. Do not serialize to config so
	// that when the server restarts, it is always false.
	@JsonIgnore
	var hideYawCorrection = false

	// Relaxed poses
	val standingRelaxedPose = StayAlignedRelaxedPoseConfig()
	val sittingRelaxedPose = StayAlignedRelaxedPoseConfig()
	val flatRelaxedPose = StayAlignedRelaxedPoseConfig()
}
