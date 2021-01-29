package io.eiren.vr.trackers;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

public interface Tracker {
	
	public boolean getPosition(Vector3f store);
	
	public boolean getRotation(Quaternion store);
	
	public String getName();
	
	public TrackerStatus getStatus();
	
	public void loadConfig(TrackerConfig config);
	
	public void saveConfig(TrackerConfig config);
	
	public float getConfidenceLevel();
}
