package dev.slimevr.vr.trackers;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.slimevr.vr.IDevice;

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

	IDevice getDevice();

	/**
	 * Returns the real tracker behind this tracker. In case this tracker
	 * transforms another tracker like {@link ReferenceAdjustedTracker}, this
	 * will return the tracker it modifies. Otherwise, it will return itself.
	 */
	Tracker get();

	default String getDescriptiveName() {
		return getName();
	}
}
