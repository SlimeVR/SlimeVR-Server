package dev.slimevr.tracking.trackers

import java.util.EnumSet
import dev.slimevr.config.ScalingValues
import dev.slimevr.config.VelocityConfig
import dev.slimevr.config.VelocityPreset
import dev.slimevr.config.VelocityRoleGroup
import dev.slimevr.config.VelocityScalingPreset

object VelocityRolePolicy {

	/**
	 * Default scaling values for the HYBRID preset.
	 * These values are used when the HYBRID scaling preset is active.
	 */
	val HYBRID_DEFAULT_SCALE = ScalingValues(0.25f, 0.25f, 0.25f)

	/**
	 * Maps SlimeVR tracker designation to velocity groups.
	 *
	 * A physical tracker is allowed to emit derived velocity if its
	 * logical group is enabled in the config.
	 *
	 * Velocity filtering operates at the tracker level.
	 */
	private val ROLE_TO_GROUPS = mapOf(
		TrackerPosition.LEFT_FOOT to setOf(
			VelocityRoleGroup.FEET
		),
		TrackerPosition.RIGHT_FOOT to setOf(
			VelocityRoleGroup.FEET
		),

		TrackerPosition.LEFT_LOWER_LEG to setOf(
			VelocityRoleGroup.ANKLES
		),
		TrackerPosition.RIGHT_LOWER_LEG to setOf(
			VelocityRoleGroup.ANKLES
		),

		TrackerPosition.LEFT_UPPER_LEG to setOf(
			VelocityRoleGroup.KNEES
		),
		TrackerPosition.RIGHT_UPPER_LEG to setOf(
			VelocityRoleGroup.KNEES
		),

		TrackerPosition.WAIST to setOf(
			VelocityRoleGroup.WAIST
		),
		TrackerPosition.HIP to setOf(
			VelocityRoleGroup.WAIST
		),

		TrackerPosition.CHEST to setOf(
			VelocityRoleGroup.CHEST
		),
		TrackerPosition.UPPER_CHEST to setOf(
			VelocityRoleGroup.CHEST
		),

		TrackerPosition.LEFT_UPPER_ARM to setOf(
			VelocityRoleGroup.ELBOWS
		),
		TrackerPosition.RIGHT_UPPER_ARM to setOf(
			VelocityRoleGroup.ELBOWS
		),
	)


	private fun enabledGroups(config: VelocityConfig): Set<VelocityRoleGroup> =
		when (config.preset) {
			VelocityPreset.ALL ->
				EnumSet.allOf(VelocityRoleGroup::class.java)

			VelocityPreset.HYBRID ->
				setOf(
					VelocityRoleGroup.WAIST,
					VelocityRoleGroup.KNEES,
					VelocityRoleGroup.FEET,
					VelocityRoleGroup.ANKLES
				)

			VelocityPreset.CUSTOM ->
				config.enabledGroups
		}

	private fun trackerPositionFromDesignation(
		designation: String?
	): TrackerPosition? {
		if (designation.isNullOrBlank()) return null

		return TrackerPosition.entries.firstOrNull {
			it.designation.equals(designation, ignoreCase = true)
		}
	}

	fun isVelocityAllowed(
		trackerDesignation: String?,
		velocityConfig: VelocityConfig
	): Boolean {
		if (!velocityConfig.sendDerivedVelocity) return false

		val position = trackerPositionFromDesignation(trackerDesignation) ?: return false
		val positionGroups = ROLE_TO_GROUPS[position] ?: return false
		val allowedGroups = enabledGroups(velocityConfig)

		return positionGroups.any { it in allowedGroups }
	}

	/**
	 * Returns the default scaling preset associated with the given velocity role group preset.
	 * - ALL and CUSTOM role presets default to UNSCALED scaling.
	 * - HYBRID role preset defaults to HYBRID scaling.
	 */
	fun getDefaultScalingPreset(velocityPreset: VelocityPreset): VelocityScalingPreset =
		when (velocityPreset) {
			VelocityPreset.ALL -> VelocityScalingPreset.UNSCALED
			VelocityPreset.HYBRID -> VelocityScalingPreset.HYBRID
			VelocityPreset.CUSTOM -> VelocityScalingPreset.UNSCALED
		}

	/**
	 * Resolves the effective scaling preset based on the override toggle.
	 * - If overrideScalingPreset is true, use the scalingPreset from config.
	 * - If overrideScalingPreset is false, use the default based on role group preset.
	 */
	fun getEffectiveScalingPreset(config: VelocityConfig): VelocityScalingPreset =
		if (config.overrideScalingPreset) {
			config.scalingPreset
		} else {
			getDefaultScalingPreset(config.preset)
		}

	/**
	 * Returns the effective scaling values based on the resolved scaling preset.
	 * - UNSCALED: returns (1.00, 1.00, 1.00) - no scaling applied.
	 * - HYBRID: returns the HYBRID default scaling values.
	 * - CUSTOM_UNIFIED/CUSTOM_PER_AXIS: returns values from config (UI handles the logic).
	 */
	fun getScalingValues(config: VelocityConfig): ScalingValues {
		val effectivePreset = getEffectiveScalingPreset(config)

		return when (effectivePreset) {
			VelocityScalingPreset.UNSCALED -> ScalingValues(1.00f, 1.00f, 1.00f)
			VelocityScalingPreset.HYBRID -> HYBRID_DEFAULT_SCALE
			VelocityScalingPreset.CUSTOM_UNIFIED,
			VelocityScalingPreset.CUSTOM_PER_AXIS -> config.scale
		}
	}
}
