package dev.slimevr.tracking.trackers

import io.github.axisangles.ktmath.Quaternion
import solarxr_protocol.datatypes.BodyPart

/**
 * Represents a position on the body that a tracker could be placed. Any bone is
 * a valid position.
 *
 * TrackerPosition intentionally lacks a numerical id to avoid breakage.
 */
enum class TrackerPosition(
	val designation: String,
	val trackerRole: TrackerRole?,
	val bodyPart: Int,
) {
	// If updating BodyPart of a TrackerRole,
	// please also update SteamVRBridge#updateShareSettingsAutomatically()
	HEAD("body:head", TrackerRole.HMD, BodyPart.HEAD),
	NECK("body:neck", TrackerRole.NECK, BodyPart.NECK),
	UPPER_CHEST("body:upper_chest", TrackerRole.CHEST, BodyPart.UPPER_CHEST),
	CHEST("body:chest", null, BodyPart.CHEST),
	WAIST("body:waist", null, BodyPart.WAIST),
	HIP("body:hip", TrackerRole.WAIST, BodyPart.HIP),
	LEFT_UPPER_LEG("body:left_upper_leg", TrackerRole.LEFT_KNEE, BodyPart.LEFT_UPPER_LEG),
	RIGHT_UPPER_LEG("body:right_upper_leg", TrackerRole.RIGHT_KNEE, BodyPart.RIGHT_UPPER_LEG),
	LEFT_LOWER_LEG("body:left_lower_leg", null, BodyPart.LEFT_LOWER_LEG),
	RIGHT_LOWER_LEG("body:right_lower_leg", null, BodyPart.RIGHT_LOWER_LEG),
	LEFT_FOOT("body:left_foot", TrackerRole.LEFT_FOOT, BodyPart.LEFT_FOOT),
	RIGHT_FOOT("body:right_foot", TrackerRole.RIGHT_FOOT, BodyPart.RIGHT_FOOT),
	LEFT_LOWER_ARM("body:left_lower_arm", null, BodyPart.LEFT_LOWER_ARM),
	RIGHT_LOWER_ARM("body:right_lower_arm", null, BodyPart.RIGHT_LOWER_ARM),
	LEFT_UPPER_ARM("body:left_upper_arm", TrackerRole.LEFT_ELBOW, BodyPart.LEFT_UPPER_ARM),
	RIGHT_UPPER_ARM("body:right_upper_arm", TrackerRole.RIGHT_ELBOW, BodyPart.RIGHT_UPPER_ARM),
	LEFT_HAND("body:left_hand", TrackerRole.LEFT_HAND, BodyPart.LEFT_HAND),
	RIGHT_HAND("body:right_hand", TrackerRole.RIGHT_HAND, BodyPart.RIGHT_HAND),
	LEFT_SHOULDER("body:left_shoulder", TrackerRole.LEFT_SHOULDER, BodyPart.LEFT_SHOULDER),
	RIGHT_SHOULDER("body:right_shoulder", TrackerRole.RIGHT_SHOULDER, BodyPart.RIGHT_SHOULDER),
	LEFT_THUMB_PROXIMAL("body:left_thumb_proximal", null, BodyPart.LEFT_THUMB_PROXIMAL),
	LEFT_THUMB_INTERMEDIATE("body:left_thumb_intermediate", null, BodyPart.LEFT_THUMB_INTERMEDIATE),
	LEFT_THUMB_DISTAL("body:left_thumb_distal", null, BodyPart.LEFT_THUMB_DISTAL),
	LEFT_INDEX_PROXIMAL("body:left_index_proximal", null, BodyPart.LEFT_INDEX_PROXIMAL),
	LEFT_INDEX_INTERMEDIATE("body:left_index_intermediate", null, BodyPart.LEFT_INDEX_INTERMEDIATE),
	LEFT_INDEX_DISTAL("body:left_index_distal", null, BodyPart.LEFT_INDEX_DISTAL),
	LEFT_MIDDLE_PROXIMAL("body:left_middle_proximal", null, BodyPart.LEFT_MIDDLE_PROXIMAL),
	LEFT_MIDDLE_INTERMEDIATE("body:left_middle_intermediate", null, BodyPart.LEFT_MIDDLE_INTERMEDIATE),
	LEFT_MIDDLE_DISTAL("body:left_middle_distal", null, BodyPart.LEFT_MIDDLE_DISTAL),
	LEFT_RING_PROXIMAL("body:left_ring_proximal", null, BodyPart.LEFT_RING_PROXIMAL),
	LEFT_RING_INTERMEDIATE("body:left_ring_intermediate", null, BodyPart.LEFT_RING_INTERMEDIATE),
	LEFT_RING_DISTAL("body:left_ring_distal", null, BodyPart.LEFT_RING_DISTAL),
	LEFT_LITTLE_PROXIMAL("body:left_little_proximal", null, BodyPart.LEFT_LITTLE_PROXIMAL),
	LEFT_LITTLE_INTERMEDIATE("body:left_little_intermediate", null, BodyPart.LEFT_LITTLE_INTERMEDIATE),
	LEFT_LITTLE_DISTAL("body:left_little_distal", null, BodyPart.LEFT_LITTLE_DISTAL),
	RIGHT_THUMB_PROXIMAL("body:right_thumb_proximal", null, BodyPart.RIGHT_THUMB_PROXIMAL),
	RIGHT_THUMB_INTERMEDIATE("body:right_thumb_intermediate", null, BodyPart.RIGHT_THUMB_INTERMEDIATE),
	RIGHT_THUMB_DISTAL("body:right_thumb_distal", null, BodyPart.RIGHT_THUMB_DISTAL),
	RIGHT_INDEX_PROXIMAL("body:right_index_proximal", null, BodyPart.RIGHT_INDEX_PROXIMAL),
	RIGHT_INDEX_INTERMEDIATE("body:right_index_intermediate", null, BodyPart.RIGHT_INDEX_INTERMEDIATE),
	RIGHT_INDEX_DISTAL("body:right_index_distal", null, BodyPart.RIGHT_INDEX_DISTAL),
	RIGHT_MIDDLE_PROXIMAL("body:right_middle_proximal", null, BodyPart.RIGHT_MIDDLE_PROXIMAL),
	RIGHT_MIDDLE_INTERMEDIATE("body:right_middle_intermediate", null, BodyPart.RIGHT_MIDDLE_INTERMEDIATE),
	RIGHT_MIDDLE_DISTAL("body:right_middle_distal", null, BodyPart.RIGHT_MIDDLE_DISTAL),
	RIGHT_RING_PROXIMAL("body:right_ring_proximal", null, BodyPart.RIGHT_RING_PROXIMAL),
	RIGHT_RING_INTERMEDIATE("body:right_ring_intermediate", null, BodyPart.RIGHT_RING_INTERMEDIATE),
	RIGHT_RING_DISTAL("body:right_ring_distal", null, BodyPart.RIGHT_RING_DISTAL),
	RIGHT_LITTLE_PROXIMAL("body:right_little_proximal", null, BodyPart.RIGHT_LITTLE_PROXIMAL),
	RIGHT_LITTLE_INTERMEDIATE("body:right_little_intermediate", null, BodyPart.RIGHT_LITTLE_INTERMEDIATE),
	RIGHT_LITTLE_DISTAL("body:right_little_distal", null, BodyPart.RIGHT_LITTLE_DISTAL),
	;

	/**
	 * Returns the default mounting orientation for the body part
	 */
	fun defaultMounting(): Quaternion = when (this) {
		LEFT_LOWER_ARM, LEFT_HAND,
		LEFT_INDEX_PROXIMAL, LEFT_INDEX_INTERMEDIATE,
		LEFT_INDEX_DISTAL, LEFT_MIDDLE_PROXIMAL,
		LEFT_MIDDLE_INTERMEDIATE, LEFT_MIDDLE_DISTAL,
		LEFT_RING_PROXIMAL, LEFT_RING_INTERMEDIATE,
		LEFT_RING_DISTAL, LEFT_LITTLE_PROXIMAL,
		LEFT_LITTLE_INTERMEDIATE, LEFT_LITTLE_DISTAL,
		-> Quaternion.SLIMEVR.LEFT

		RIGHT_LOWER_ARM, RIGHT_HAND,
		RIGHT_INDEX_PROXIMAL, RIGHT_INDEX_INTERMEDIATE,
		RIGHT_INDEX_DISTAL, RIGHT_MIDDLE_PROXIMAL,
		RIGHT_MIDDLE_INTERMEDIATE, RIGHT_MIDDLE_DISTAL,
		RIGHT_RING_PROXIMAL, RIGHT_RING_INTERMEDIATE,
		RIGHT_RING_DISTAL, RIGHT_LITTLE_PROXIMAL,
		RIGHT_LITTLE_INTERMEDIATE, RIGHT_LITTLE_DISTAL,
		-> Quaternion.SLIMEVR.RIGHT

		LEFT_UPPER_ARM, LEFT_LOWER_LEG -> Quaternion.SLIMEVR.FRONT_LEFT

		RIGHT_UPPER_ARM, RIGHT_LOWER_LEG -> Quaternion.SLIMEVR.FRONT_RIGHT

		else -> Quaternion.SLIMEVR.FRONT
	}

	companion object {
		/** Indexed by `BodyPart` int value. EFFICIENCY FTW  */
		private val byBodyPart: Array<out TrackerPosition?> = arrayOfNulls<TrackerPosition>(BodyPart.names.size).apply {
			for (position in entries) {
				this[position.bodyPart] = position
			}
		}
		private val byDesignation = entries.associateBy { it.designation.lowercase() }
		private val byTrackerRole = entries.filter { it.trackerRole != null }.associateBy { it.trackerRole!! }

		/**
		 * Gets the `TrackerPosition` by its string designation.
		 *
		 * @return Returns an optional as not all strings are valid designators.
		 */
		@JvmStatic
		fun getByDesignation(designation: String): TrackerPosition? = byDesignation[designation.lowercase()]

		@JvmStatic
		fun getByTrackerRole(role: TrackerRole): TrackerPosition? {
			// Hands TrackerPositions are bound to the hands TrackerRoles,
			// so we hardcode getting those.
			if (role == TrackerRole.LEFT_CONTROLLER) {
				return LEFT_HAND
			}
			if (role == TrackerRole.RIGHT_CONTROLLER) {
				return RIGHT_HAND
			}
			return byTrackerRole[role]
		}

		@JvmStatic
		fun getByBodyPart(bodyPart: Int): TrackerPosition? = byBodyPart[bodyPart]
	}
}
