package dev.slimevr.tracking.trackers;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.slimevr.config.TrackerConfig;
import dev.slimevr.tracking.Device;

import java.util.Optional;


public class ComputedTracker implements Tracker, TrackerWithTPS {

	public final Vector3f position = new Vector3f();
	public final Quaternion rotation = new Quaternion();
	protected final String name;
	protected final String serial;
	protected final boolean hasRotation;
	protected final boolean hasPosition;
	protected final int trackerId;
	private final Device device;
	private final boolean useTimeout;
	private String customName;
	public TrackerPosition bodyPosition = null;
	protected TrackerStatus status = TrackerStatus.DISCONNECTED;
	private long timeAtLastUpdate;
	private final static Long TIMEOUT_MS = 2000L;

	public ComputedTracker(
		int trackerId,
		String serial,
		String name,
		boolean hasRotation,
		boolean hasPosition,
		Device device,
		boolean useTimeout
	) {
		this.name = name;
		this.serial = serial;
		this.hasRotation = hasRotation;
		this.hasPosition = hasPosition;
		this.trackerId = trackerId;
		this.device = device;
		this.useTimeout = useTimeout;
	}

	public ComputedTracker(int trackerId, String name, boolean hasRotation, boolean hasPosition) {
		this(trackerId, name, name, hasRotation, hasPosition, null, false);
	}

	@Override
	public void writeConfig(TrackerConfig config) {
		config.setDesignation(bodyPosition == null ? null : bodyPosition.designation);
		config.setCustomName(customName);
	}

	@Override
	public void readConfig(TrackerConfig config) {
		// Loading a config is an act of user editing, therefore it shouldn't
		// be allowed if editing is not allowed
		if (userEditable()) {
			setCustomName(config.getCustomName());
			Optional<TrackerPosition> trackerPosition = TrackerPosition
				.getByDesignation(config.getDesignation());
			if (trackerPosition.isEmpty()) {
				bodyPosition = null;
			} else {
				bodyPosition = trackerPosition.get();
			}
		}
	}

	@Override
	public String getName() {
		return this.serial;
	}


	@Override
	public boolean getPosition(Vector3f store) {
		store.set(position);
		return true;
	}

	@Override
	public boolean getRotation(Quaternion store) {
		store.set(rotation);
		return true;
	}

	@Override
	public boolean getRawRotation(Quaternion store) {
		store.set(rotation);
		return true;
	}

	@Override
	public boolean getAcceleration(Vector3f store) {
		store.set(0, 0, 0);
		return false;
	}

	@Override
	public TrackerStatus getStatus() {
		return status;
	}

	public void setStatus(TrackerStatus status) {
		this.status = status;
	}

	@Override
	public float getConfidenceLevel() {
		return 1.0f;
	}

	@Override
	public void resetFull(Quaternion reference) {
	}

	@Override
	public void resetYaw(Quaternion reference) {
	}

	@Override
	public void resetMounting(boolean reverseYaw) {
	}

	@Override
	public TrackerPosition getBodyPosition() {
		return bodyPosition;
	}

	@Override
	public void setBodyPosition(TrackerPosition position) {
		this.bodyPosition = position;
	}

	@Override
	public boolean userEditable() {
		return false;
	}

	@Override
	public void dataTick() {
		timeAtLastUpdate = System.currentTimeMillis();
	}

	@Override
	public void tick() {
		if (useTimeout) {
			if (System.currentTimeMillis() - timeAtLastUpdate < TIMEOUT_MS)
				setStatus(TrackerStatus.OK);
			else
				setStatus(TrackerStatus.DISCONNECTED);
		}
	}

	@Override
	public boolean hasRotation() {
		return hasRotation;
	}

	@Override
	public boolean hasPosition() {
		return hasPosition;
	}

	@Override
	public boolean hasAcceleration() {
		return false;
	}

	@Override
	public boolean isComputed() {
		return true;
	}

	@Override
	public float getTPS() {
		return -1;
	}

	@Override
	public int getTrackerId() {
		return this.trackerId;
	}

	@Override
	public int getTrackerNum() {
		return this.getTrackerId();
	}

	@Override
	public Device getDevice() {
		return device;
	}

	@Override
	public Tracker get() {
		return this;
	}

	@Override
	public String getDisplayName() {
		return getName();
	}

	@Override
	public String getCustomName() {
		return customName;
	}

	@Override
	public void setCustomName(String customName) {
		this.customName = customName;
	}
}
