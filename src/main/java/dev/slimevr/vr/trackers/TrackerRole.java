package dev.slimevr.vr.trackers;

/**
 * The tracker role classifies the position and the role of a tracker on user's
 * body or playspace (like CAMERA or BEACON). Tracker roles are hints for
 * interacting programs what the tracker means, and they do not correspond to
 * body's bones on purpose. Example: virtual vive trackers for SteamVR vs actual
 * SlimeVR trackers.
 */
public enum TrackerRole {

	// @formatter:off
	NONE(0, "", "", null),
	WAIST(1, "vive_tracker_waist", "TrackerRole_Waist", DeviceType.TRACKER),
	LEFT_FOOT(2, "vive_tracker_left_foot", "TrackerRole_LeftFoot", DeviceType.TRACKER),
	RIGHT_FOOT(3, "vive_tracker_right_foot", "TrackerRole_RightFoot", DeviceType.TRACKER),
	CHEST(4, "vive_tracker_chest", "TrackerRole_Chest", DeviceType.TRACKER),
	LEFT_KNEE(5, "vive_tracker_left_knee", "TrackerRole_LeftKnee", DeviceType.TRACKER),
	RIGHT_KNEE(6, "vive_tracker_right_knee", "TrackerRole_RightKnee", DeviceType.TRACKER),
	LEFT_ELBOW(7, "vive_tracker_left_elbow", "TrackerRole_LeftElbow", DeviceType.TRACKER),
	RIGHT_ELBOW(8, "vive_tracker_right_elbow", "TrackerRole_RightElbow", DeviceType.TRACKER),
	LEFT_SHOULDER(9, "vive_tracker_left_shoulder", "TrackerRole_LeftShoulder", DeviceType.TRACKER),
	RIGHT_SHOULDER(10, "vive_tracker_right_shoulder", "TrackerRole_RightShoulder", DeviceType.TRACKER),
	LEFT_HAND(11, "vive_tracker_handed", "TrackerRole_Handed", DeviceType.TRACKER),
	RIGHT_HAND(12, "vive_tracker_handed", "TrackerRole_Handed", DeviceType.TRACKER),
	LEFT_CONTROLLER(13, "vive_tracker_handed", "TrackerRole_Handed", DeviceType.CONTROLLER),
	RIGHT_CONTROLLER(14, "vive_tracker_handed", "TrackerRole_Handed", DeviceType.CONTROLLER),
	HEAD(15, "", "", DeviceType.TRACKER),
	NECK(16, "", "", DeviceType.TRACKER),
	CAMERA(17, "vive_tracker_camera", "TrackerRole_Camera", DeviceType.TRACKER),
	KEYBOARD(18, "vive_tracker_keyboard", "TrackerRole_Keyboard", DeviceType.TRACKER),
	HMD(19, "", "", DeviceType.HMD),
	BEACON(20, "", "", DeviceType.TRACKING_REFERENCE),
	GENERIC_CONTROLLER(21, "vive_tracker_handed", "TrackerRole_Handed", DeviceType.CONTROLLER);
	// @formatter:on

	public static final TrackerRole[] values = values();
	private static final TrackerRole[] byId = new TrackerRole[22];

	static {
		for (TrackerRole tr : values) {
			if (byId[tr.id] != null)
				throw new AssertionError(
					"Tracker role id "
						+ tr.id
						+ " occupied by "
						+ byId[tr.id]
						+ " when adding "
						+ tr
				);
			byId[tr.id] = tr;
		}
	}

	public final int id;
	public final String roleHint;
	public final String viveRole;
	public final DeviceType deviceType;

	private TrackerRole(int id, String roleHint, String viveRole, DeviceType deviceType) {
		this.id = id;
		this.roleHint = roleHint;
		this.viveRole = viveRole;
		this.deviceType = deviceType;
	}

	public static TrackerRole getById(int id) {
		return id < 0 || id >= byId.length ? null : byId[id];
	}
}
