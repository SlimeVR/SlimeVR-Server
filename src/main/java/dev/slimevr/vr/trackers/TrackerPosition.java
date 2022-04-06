package dev.slimevr.vr.trackers;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public enum TrackerPosition {
	
	NONE("", TrackerRole.NONE),
	HMD("HMD", TrackerRole.HMD),
	CHEST("body:chest", TrackerRole.CHEST),
	WAIST("body:waist", TrackerRole.WAIST),
	HIP("body:hip", null),
	LEFT_KNEE("body:left_knee", TrackerRole.LEFT_KNEE),
	RIGHT_KNEE("body:right_knee", TrackerRole.RIGHT_KNEE),
	LEFT_ANKLE("body:left_ankle", null),
	RIGHT_ANKLE("body:right_ankle", null),
	LEFT_FOOT("body:left_foot", TrackerRole.LEFT_FOOT),
	RIGHT_FOOT("body:right_foot", TrackerRole.RIGHT_FOOT),
	LEFT_CONTROLLER("body:left_controller", TrackerRole.LEFT_CONTROLLER),
	RIGHT_CONTROLLER("body:right_controller", TrackerRole.RIGHT_CONTROLLER),
	LEFT_FOREARM("body:left_forearm", TrackerRole.LEFT_ELBOW),
	RIGHT_FOREARM("body:right_forearm", TrackerRole.RIGHT_ELBOW),
	LEFT_UPPER_ARM("body:left_upperarm", null),
	RIGHT_UPPER_ARM("body:right_upperarm", null),
	;
	
	public final String designation;
	public final TrackerRole trackerRole;
	
	public static final TrackerPosition[] values = values();
	private static final Map<String, TrackerPosition> byDesignation = new HashMap<>();
	private static final EnumMap<TrackerRole, TrackerPosition> byRole = new EnumMap<>(TrackerRole.class);
	
	private TrackerPosition(String designation, TrackerRole trackerRole) {
		this.designation = designation;
		this.trackerRole = trackerRole;
	}
	
	public static TrackerPosition getByDesignation(String designation) {
		// Support old configs. leg was renamed to knee.
		if(designation != null) {
			if(designation.equals("body:left_leg"))
				designation = "body:left_knee";
			if(designation.equals("body:right_leg"))
				designation = "body:right_knee";	
		}
		
		return designation == null ? null : byDesignation.get(designation.toLowerCase());
	}
	
	public static TrackerPosition getByRole(TrackerRole role) {
		return byRole.get(role);
	}
	
	static {
		for(TrackerPosition tbp : values()) {
			byDesignation.put(tbp.designation.toLowerCase(), tbp);
			if(tbp.trackerRole != null) {
				TrackerPosition old = byRole.get(tbp.trackerRole);
				if(old != null)
					throw new AssertionError("Only one tracker position can match tracker role. " + tbp.trackerRole + " is occupied by " + old + " when adding " + tbp);
				byRole.put(tbp.trackerRole, tbp);
			}
		}
	}
}
