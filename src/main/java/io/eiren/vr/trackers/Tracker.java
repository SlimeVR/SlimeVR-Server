package io.eiren.vr.trackers;

import java.util.concurrent.atomic.AtomicInteger;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

public interface Tracker {
	
	public static final AtomicInteger nextLocalTrackerId = new AtomicInteger();
	
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
	
	public TrackerPosition getBodyPosition();
	
	public void setBodyPosition(TrackerPosition position);
	
	public boolean userEditable();
	
	public boolean hasRotation();
	
	public boolean hasPosition();

	public boolean isComputed();
	
	public int getTrackerId();
	
	public default String getDescriptiveName() {
		return getName();
	}
	
	public static int getNextLocalTrackerId() {
		return nextLocalTrackerId.incrementAndGet();
	}
}
