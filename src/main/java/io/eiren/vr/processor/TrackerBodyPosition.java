package io.eiren.vr.processor;

import java.util.HashMap;
import java.util.Map;

public enum TrackerBodyPosition {
	
	NONE(""),
	CHEST("body:chest"),
	WAIST("body:waist"),
	LEFT_LEG("body:left_leg"),
	RIGHT_LEG("body:right_leg"),
	LEFT_ANKLE("body:left_ankle"),
	RIGHT_ANKLE("body:right_ankle"),
	LEFT_FOOT("body:left_foot"),
	RIGHT_FOOT("body:right_foot"),
	;
	
	public final String designation;
	
	private static final Map<String, TrackerBodyPosition> byDesignation = new HashMap<>();
	
	private TrackerBodyPosition(String designation) {
		this.designation = designation;
	}
	
	public static TrackerBodyPosition getByDesignation(String designation) {
		return byDesignation.get(designation.toLowerCase());
	}
	
	static {
		for(TrackerBodyPosition tbp : values())
			byDesignation.put(tbp.designation, tbp);
	}
}