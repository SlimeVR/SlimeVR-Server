package io.eiren.vr.processor;

import java.util.HashMap;
import java.util.Map;

public enum TrackerBodyPosition {
	
	NONE(""),
	HMD("HMD"),
	CHEST("chest"),
	WAIST("waist"),
	LEFT_LEG("left_leg"),
	RIGHT_LEG("right_leg"),
	LEFT_ANKLE("left_ankle"),
	RIGHT_ANKLE("right_ankle"),
	LEFT_FOOT("left_foot"),
	RIGHT_FOOT("right_foot"),
	LEFT_CONTROLLER("left_controller"),
	RIGHT_CONTROLLER("right_conroller"),
	;
	
	public final String designation;
	
	public static final TrackerBodyPosition[] values = values();
	private static final Map<String, TrackerBodyPosition> byDesignation = new HashMap<>();
	
	private TrackerBodyPosition(String designation) {
		this.designation = designation;
	}
	
	public static TrackerBodyPosition getByDesignation(String designation) {
		return designation == null ? null : byDesignation.get(designation.toLowerCase());
	}
	
	static {
		for(TrackerBodyPosition tbp : values())
			byDesignation.put(tbp.designation.toLowerCase(), tbp);
	}
}