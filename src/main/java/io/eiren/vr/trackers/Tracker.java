package io.eiren.vr.trackers;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import io.eiren.vr.processor.TrackerBodyPosition;

public interface Tracker {
	
	public boolean getPosition(Vector3f store);
	
	public boolean getRotation(Quaternion store);
	
	public String getName();
	
	public TrackerStatus getStatus();
	
	public void loadConfig(TrackerConfig config);
	
	public void saveConfig(TrackerConfig config);
	
	public float getConfidenceLevel();
	
	public void resetFull(Quaternion reference);
	
	public void resetYaw(Quaternion reference);
	
	public void tick();
	
	public TrackerBodyPosition getBodyPosition();
	
	public void setBodyPosition(TrackerBodyPosition position);
	
	public boolean userEditable();
	
	public boolean hasRotation();
	
	public boolean hasPosition();

	public boolean isComputed();
	
	public default String getDescriptiveName() {
		return getName();
	}
}
