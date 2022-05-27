package dev.slimevr.vr.trackers;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;


public enum TrackerPosition {

	NONE(0, "", TrackerRole.NONE),
	HMD(1, "HMD", TrackerRole.HMD),
	NECK(2, "body:neck", null),
	CHEST(3, "body:chest", TrackerRole.CHEST),
	WAIST(4, "body:waist", TrackerRole.WAIST),
	HIP(5, "body:hip", null),
	LEFT_UPPER_LEG(6, "body:left_upper_leg", TrackerRole.LEFT_KNEE),
	RIGHT_UPPER_LEG(7, "body:right_upper_leg", TrackerRole.RIGHT_KNEE),
	LEFT_LOWER_LEG(8, "body:left_lower_leg", null),
	RIGHT_LOWER_LEG(9, "body:right_lower_leg", null),
	LEFT_FOOT(10, "body:left_foot", TrackerRole.LEFT_FOOT),
	RIGHT_FOOT(11, "body:right_foot", TrackerRole.RIGHT_FOOT),
	LEFT_CONTROLLER(12, "body:left_controller", TrackerRole.LEFT_CONTROLLER),
	RIGHT_CONTROLLER(13, "body:right_controller", TrackerRole.RIGHT_CONTROLLER),
	LEFT_LOWER_ARM(14, "body:left_lower_arm", TrackerRole.LEFT_ELBOW),
	RIGHT_LOWER_ARM(15, "body:right_lower_arm", TrackerRole.RIGHT_ELBOW),
	LEFT_UPPER_ARM(16, "body:left_upper_arm", TrackerRole.LEFT_SHOULDER),
	RIGHT_UPPER_ARM(17, "body:right_upper_arm", TrackerRole.RIGHT_SHOULDER),
	LEFT_HAND(18, "body:left_hand", TrackerRole.LEFT_HAND),
	RIGHT_HAND(19, "body:right_hand", TrackerRole.RIGHT_HAND),;

	public static final TrackerPosition[] values = values();
	private static final Map<Integer, TrackerPosition> byId = new HashMap<>();
	private static final Map<String, TrackerPosition> byDesignation = new HashMap<>();
	private static final EnumMap<TrackerRole, TrackerPosition> byRole = new EnumMap<>(
		TrackerRole.class
	);

	static {
		for (TrackerPosition tbp : values()) {
			byDesignation.put(tbp.designation.toLowerCase(), tbp);
			byId.put(tbp.id, tbp);
			if (tbp.trackerRole != null) {
				TrackerPosition old = byRole.get(tbp.trackerRole);
				if (old != null)
					throw new AssertionError(
						"Only one tracker position can match tracker role. "
							+ tbp.trackerRole
							+ " is occupied by "
							+ old
							+ " when adding "
							+ tbp
					);
				byRole.put(tbp.trackerRole, tbp);
			}
		}
	}

	public final int id;
	public final String designation;
	public final TrackerRole trackerRole;

	TrackerPosition(int id, String designation, TrackerRole trackerRole) {
		this.id = id;
		this.designation = designation;
		this.trackerRole = trackerRole;
	}

	public static TrackerPosition getByDesignation(String designation) {
		// Support old configs
		if (designation != null) {
			if (
				designation.equalsIgnoreCase("body:left_leg")
					|| designation.equalsIgnoreCase("body:left_knee")
			) {
				designation = "body:left_upper_leg";
			} else if (
				designation.equalsIgnoreCase("body:right_leg")
					|| designation.equalsIgnoreCase("body:right_knee")
			) {
				designation = "body:right_upper_leg";
			} else if (designation.equalsIgnoreCase("body:left_ankle")) {
				designation = "body:left_lower_leg";
			} else if (designation.equalsIgnoreCase("body:right_ankle")) {
				designation = "body:right_lower_leg";
			} else if (designation.equalsIgnoreCase("body:left_forearm")) {
				designation = "body:left_lower_arm";
			} else if (designation.equalsIgnoreCase("body:right_forearm")) {
				designation = "body:right_lower_arm";
			}
		}

		return designation == null ? null : byDesignation.get(designation.toLowerCase());
	}

	public static TrackerPosition getByRole(TrackerRole role) {
		return byRole.get(role);
	}

	public static TrackerPosition getById(int id) {
		return byId.get(id);
	}
}
