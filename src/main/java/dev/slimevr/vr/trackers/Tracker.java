package dev.slimevr.vr.trackers;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import java.util.concurrent.atomic.AtomicInteger;


public interface Tracker {

	AtomicInteger nextLocalTrackerId = new AtomicInteger();

	static int getNextLocalTrackerId() {
		return nextLocalTrackerId.incrementAndGet();
	}

	boolean getPosition(Vector3f store);

	boolean getRotation(Quaternion store);

	String getName();

	TrackerStatus getStatus();

	void loadConfig(TrackerConfig config);

	void saveConfig(TrackerConfig config);

	float getConfidenceLevel();

	void resetFull(Quaternion reference);

	void resetYaw(Quaternion reference);

	void tick();

	TrackerPosition getBodyPosition();

	void setBodyPosition(TrackerPosition position);

	boolean userEditable();

	boolean hasRotation();

	boolean hasPosition();

	boolean isComputed();

	int getTrackerId();

	int getTrackerNum();

	Device getDevice();

	default String getDescriptiveName() {
		return getName();
	}
}
