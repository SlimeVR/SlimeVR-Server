package dev.slimevr.tracking.trackers

/**
 * The tracker role classifies the position and the role of a tracker on user's
 * body or playspace (like CAMERA or BEACON). Tracker roles are hints for
 * interacting programs what the tracker means, and they do not correspond to
 * body's bones on purpose. Example: virtual vive trackers for SteamVR vs actual
 * SlimeVR trackers.
 */
enum class TrackerRole(val id: Int, val roleHint: String, val viveRole: String) {
	// @formatter:off
	NONE(0, "", ""),
	WAIST(1, "vive_tracker_waist", "TrackerRole_Waist"),
	LEFT_FOOT(2, "vive_tracker_left_foot", "TrackerRole_LeftFoot"),
	RIGHT_FOOT(3, "vive_tracker_right_foot", "TrackerRole_RightFoot"),
	CHEST(4, "vive_tracker_chest", "TrackerRole_Chest"),
	LEFT_KNEE(5, "vive_tracker_left_knee", "TrackerRole_LeftKnee"),
	RIGHT_KNEE(6, "vive_tracker_right_knee", "TrackerRole_RightKnee"),
	LEFT_ELBOW(7, "vive_tracker_left_elbow", "TrackerRole_LeftElbow"),
	RIGHT_ELBOW(8, "vive_tracker_right_elbow", "TrackerRole_RightElbow"),
	LEFT_SHOULDER(9, "vive_tracker_left_shoulder", "TrackerRole_LeftShoulder"),
	RIGHT_SHOULDER(10, "vive_tracker_right_shoulder", "TrackerRole_RightShoulder"),
	LEFT_HAND(11, "vive_tracker_handed", "TrackerRole_Handed"),
	RIGHT_HAND(12, "vive_tracker_handed", "TrackerRole_Handed"),
	LEFT_CONTROLLER(13, "vive_tracker_handed", "TrackerRole_Handed"),
	RIGHT_CONTROLLER(14, "vive_tracker_handed", "TrackerRole_Handed"),
	HEAD(15, "", ""),
	NECK(16, "", ""),
	CAMERA(17, "vive_tracker_camera", "TrackerRole_Camera"),
	KEYBOARD(18, "vive_tracker_keyboard", "TrackerRole_Keyboard"),
	HMD(19, "", ""),
	BEACON(20, "", ""),
	GENERIC_CONTROLLER(21, "vive_tracker_handed", "TrackerRole_Handed"),
	;

	companion object {
		private val byId = values().associateBy { it.id }

		@JvmStatic
		fun getById(id: Int): TrackerRole? = byId[id]
	}
}
