package dev.slimevr.config

import java.util.EnumSet

/**
 * Data class to hold scaling values for all three axes.
 * Range: 0.00 - 1.00
 */
data class ScalingValues(
	var scaleX: Float = 1.0f,
	var scaleY: Float = 1.0f,
	var scaleZ: Float = 1.0f
)

/**
 * Sets a preset for which tracker roles are exposing derived velocity.
 * ALL - Enables All tracker roles from VelocityRoleGroup class.
 * HYBRID - Enables only Feet and Ankles, useful for NaLo + VRChat to reduce overprediction jitter.
 * CUSTOM - Allows custom selection of tracker role groups that will expose velocity.r
 */
enum class VelocityPreset {
	ALL,
	HYBRID,
	CUSTOM
}

/**
 * Sets tracker groups rather than setting per each individual tracker role.
 * Roles with trackers on two sides, like ANKLES, FEET, THIGHS, etc., are corresponding to both left and right trackers.
 * NECK and SHOULDERS are excluded due to being mostly static in terms of stand-in-place velocity and being too close to HMD.
 */

enum class VelocityRoleGroup {
	FEET,
	ANKLES,
	KNEES,
	CHEST,
	WAIST,
	ELBOWS
}

/**
 * Presets for velocity scaling factors.
 * UNSCALED - No scaling applied (1.0, 1.0, 1.0).
 * HYBRID - NaLo/Hybrid scaling, typically used with hybrid locomotion in VRChat.
 * CUSTOM_UNIFIED - Allows custom scaling with a single value applied to all axes.
 * CUSTOM_PER_AXIS - Allows custom scaling with individual values per axis.
 */
enum class VelocityScalingPreset {
	UNSCALED,
	HYBRID,
	CUSTOM_UNIFIED,
	CUSTOM_PER_AXIS
}

/**
 * Allows to enable/disable sending of optional derived velocity data via Protobuf.
 * Enables Natural Locomotion Support
 * May create overprediction in certain titles causing excessive jitter when moving upper body.
 */
class VelocityConfig {
	var sendDerivedVelocity: Boolean = false // Disables derived velocity for all trackers. Driver zeroes out velocity if nothing is returned in protobuf message.
	var preset: VelocityPreset = VelocityPreset.HYBRID
	var enabledGroups: EnumSet<VelocityRoleGroup> =
		EnumSet.noneOf(VelocityRoleGroup::class.java)

	// Velocity scaling preset and override flag. When override is enabled, the scaling preset is applied regardless of the selected velocity preset.
	var overrideScalingPreset: Boolean = false // Allows overriding the default scaling preset binding to role presets
	var scalingPreset: VelocityScalingPreset = VelocityScalingPreset.UNSCALED

	var enableUpscaling: Boolean = false // FBT position prediction will break, this is an accessibility feature for people with limited mobility.

	// Velocity scaling factors (1.0 = no scaling)
	var scale: ScalingValues = ScalingValues()
}
