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
	LEFT_KNEE(6, "body:left_knee", TrackerRole.LEFT_KNEE),
	RIGHT_KNEE(7, "body:right_knee", TrackerRole.RIGHT_KNEE),
	LEFT_ANKLE(8, "body:left_ankle", null),
	RIGHT_ANKLE(9, "body:right_ankle", null),
	LEFT_FOOT(10, "body:left_foot", TrackerRole.LEFT_FOOT),
	RIGHT_FOOT(11, "body:right_foot", TrackerRole.RIGHT_FOOT),
	LEFT_CONTROLLER(12, "body:left_controller", TrackerRole.LEFT_CONTROLLER),
	RIGHT_CONTROLLER(13, "body:right_controller", TrackerRole.RIGHT_CONTROLLER),
	LEFT_FOREARM(14, "body:left_forearm", TrackerRole.LEFT_ELBOW),
	RIGHT_FOREARM(15, "body:right_forearm", TrackerRole.RIGHT_ELBOW),
	LEFT_UPPER_ARM(16, "body:left_upperarm", null),
	RIGHT_UPPER_ARM(17, "body:right_upperarm", null),
	LEFT_HAND(18, "body:left_hand", TrackerRole.LEFT_HAND),
	RIGHT_HAND(19, "body:right_hand", TrackerRole.RIGHT_HAND),
	;

	public final int id;
	public final String designation;
	public final TrackerRole trackerRole;

	public static final TrackerPosition[] values = values();
	private static final Map<Integer, TrackerPosition> byId = new HashMap<>();
	private static final Map<String, TrackerPosition> byDesignation = new HashMap<>();
	private static final EnumMap<TrackerRole, TrackerPosition> byRole = new EnumMap<>(TrackerRole.class);

	private TrackerPosition(int id, String designation, TrackerRole trackerRole) {
		this.id = id;
		this.designation = designation;
		this.trackerRole = trackerRole;
	}

	public static TrackerPosition getByDesignation(String designation) {
		// Support old configs. leg was renamed to knee.
		if (designation != null) {
			if (designation.equals("body:left_leg"))
				designation = "body:left_knee";
			if (designation.equals("body:right_leg"))
				designation = "body:right_knee";
		}

		return designation == null ? null : byDesignation.get(designation.toLowerCase());
	}

	public static TrackerPosition getByRole(TrackerRole role) {
		return byRole.get(role);
	}

	public static TrackerPosition getById(int id) {
		return byId.get(id);
	}

	static {
		for (TrackerPosition tbp : values()) {
			byDesignation.put(tbp.designation.toLowerCase(), tbp);
			byId.put(tbp.id, tbp);
			if (tbp.trackerRole != null) {
				TrackerPosition old = byRole.get(tbp.trackerRole);
				if (old != null)
					throw new AssertionError("Only one tracker position can match tracker role. " + tbp.trackerRole + " is occupied by " + old + " when adding " + tbp);
				byRole.put(tbp.trackerRole, tbp);
			}
		}
	}
}
