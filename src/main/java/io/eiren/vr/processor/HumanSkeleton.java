package io.eiren.vr.processor;

import java.util.Map;

import io.eiren.util.ann.ThreadSafe;
import io.eiren.util.ann.VRServerThread;

public abstract class HumanSkeleton {

	@VRServerThread
	public abstract void updatePose();
	
	@ThreadSafe
	public abstract TransformNode getRootNode();

	@ThreadSafe
	public abstract Map<HumanJoint, Float> getJointsMap();

	@ThreadSafe
	public abstract void sentJointLength(HumanJoint joint, float newLength);
	
	public enum HumanJoint {
		
		HEAD("Head", ""),
		NECK("Neck", ""),
		WAIST("Waist", ""),
		WASIT_VIRTUAL("Virtual waist", ""),
		HIPS_WIDTH("Hips width", ""),
		HIPS_LENGTH("Hips length", ""),
		LEGS_LENGTH("Legs length", ""),
		;
		
		public final String name;
		public final String description;
		
		private HumanJoint(String name, String description) {
			this.name = name;
			this.description = description;
		}
	}
}
