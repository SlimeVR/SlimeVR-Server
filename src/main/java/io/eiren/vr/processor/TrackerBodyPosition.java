package io.eiren.vr.processor;

import java.util.HashMap;
import java.util.Map;

import com.jme3.math.Quaternion;

public enum TrackerBodyPosition {
	
	CHEST(Quaternion.IDENTITY, "body:chest"),
	WAIST(Quaternion.IDENTITY, "body:waist"),
	LEFT_LEG(Quaternion.IDENTITY, "body:left_leg"),
	RIGHT_LEG(Quaternion.IDENTITY, "body:right_leg"),
	LEFT_ANKLE(Quaternion.IDENTITY, "body:left_ankle"),
	RIGHT_ANKLE(Quaternion.IDENTITY, "body:right_ankle"),
	;
	
	public final Quaternion baseRotation;
	public final String designation;
	
	private static final Map<String, TrackerBodyPosition> byDesignation = new HashMap<>();
	
	private TrackerBodyPosition(Quaternion base, String designation) {
		this.baseRotation = base;
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