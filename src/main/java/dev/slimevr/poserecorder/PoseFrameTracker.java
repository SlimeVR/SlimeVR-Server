package dev.slimevr.poserecorder;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.slimevr.vr.trackers.Tracker;
import dev.slimevr.vr.trackers.TrackerConfig;
import dev.slimevr.vr.trackers.TrackerPosition;
import dev.slimevr.vr.trackers.TrackerStatus;
import dev.slimevr.vr.trackers.udp.UDPDevice;
import io.eiren.util.collections.FastList;

import java.util.Iterator;


public class PoseFrameTracker implements Tracker, Iterable<TrackerFrame> {

	public final String name;

	private final FastList<TrackerFrame> frames;
	private final int trackerId = Tracker.getNextLocalTrackerId();
	private int frameCursor = 0;

	public PoseFrameTracker(String name, FastList<TrackerFrame> frames) {
		if (frames == null) {
			throw new NullPointerException("frames must not be null");
		}

		this.name = name != null ? name : "";
		this.frames = frames;
	}

	public PoseFrameTracker(String name, int initialCapacity) {
		this(name, new FastList<TrackerFrame>(initialCapacity));
	}

	public PoseFrameTracker(Tracker parent, int initialCapacity) {
		this(parent.getName(), initialCapacity);
	}

	public PoseFrameTracker(String name) {
		this(name, 5);
	}

	public PoseFrameTracker(Tracker parent) {
		this(parent.getName());
	}

	private int limitCursor() {
		if (frameCursor < 0 || frames.isEmpty()) {
			frameCursor = 0;
		} else if (frameCursor >= frames.size()) {
			frameCursor = frames.size() - 1;
		}

		return frameCursor;
	}

	public int setCursor(int index) {
		frameCursor = index;
		return limitCursor();
	}

	public int incrementCursor(int increment) {
		frameCursor += increment;
		return limitCursor();
	}

	public int incrementCursor() {
		return incrementCursor(1);
	}

	public int getCursor() {
		return frameCursor;
	}

	public int getFrameCount() {
		return frames.size();
	}

	public TrackerFrame addFrame(int index, TrackerFrame trackerFrame) {
		frames.add(index, trackerFrame);
		return trackerFrame;
	}

	public TrackerFrame addFrame(int index, Tracker tracker) {
		return addFrame(index, TrackerFrame.fromTracker(tracker));
	}

	public TrackerFrame addFrame(TrackerFrame trackerFrame) {
		frames.add(trackerFrame);
		return trackerFrame;
	}

	public TrackerFrame addFrame(Tracker tracker) {
		return addFrame(TrackerFrame.fromTracker(tracker));
	}

	public TrackerFrame removeFrame(int index) {
		TrackerFrame trackerFrame = frames.remove(index);
		limitCursor();
		return trackerFrame;
	}

	public TrackerFrame removeFrame(TrackerFrame trackerFrame) {
		frames.remove(trackerFrame);
		limitCursor();
		return trackerFrame;
	}

	public void clearFrames() {
		frames.clear();
		limitCursor();
	}

	public void fakeClearFrames() {
		frames.fakeClear();
		limitCursor();
	}

	public TrackerFrame getFrame(int index) {
		return frames.get(index);
	}

	public TrackerFrame getFrame() {
		return getFrame(frameCursor);
	}

	public TrackerFrame safeGetFrame(int index) {
		try {
			return getFrame(index);
		} catch (Exception e) {
			return null;
		}
	}

	public TrackerFrame safeGetFrame() {
		return safeGetFrame(frameCursor);
	}

	// #region Tracker Interface Implementation
	@Override
	public boolean getRotation(Quaternion store) {
		TrackerFrame frame = safeGetFrame();
		if (frame != null && frame.hasData(TrackerFrameData.ROTATION)) {
			store.set(frame.rotation);
			return true;
		}

		store.set(Quaternion.IDENTITY);
		return false;
	}

	@Override
	public boolean getPosition(Vector3f store) {
		TrackerFrame frame = safeGetFrame();
		if (frame != null && frame.hasData(TrackerFrameData.POSITION)) {
			store.set(frame.position);
			return true;
		}

		store.set(Vector3f.ZERO);
		return false;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public TrackerStatus getStatus() {
		return TrackerStatus.OK;
	}

	@Override
	public void loadConfig(TrackerConfig config) {
		throw new UnsupportedOperationException(
			"PoseFrameTracker does not implement configuration"
		);
	}

	@Override
	public void saveConfig(TrackerConfig config) {
		throw new UnsupportedOperationException(
			"PoseFrameTracker does not implement configuration"
		);
	}

	@Override
	public float getConfidenceLevel() {
		return 1f;
	}

	@Override
	public void resetFull(Quaternion reference) {
		throw new UnsupportedOperationException("PoseFrameTracker does not implement calibration");
	}

	@Override
	public void resetYaw(Quaternion reference) {
		throw new UnsupportedOperationException("PoseFrameTracker does not implement calibration");
	}

	@Override
	public void tick() {
		throw new UnsupportedOperationException("PoseFrameTracker does not implement this method");
	}

	@Override
	public TrackerPosition getBodyPosition() {
		TrackerFrame frame = safeGetFrame();
		return frame == null ? null : frame.designation;
	}

	@Override
	public void setBodyPosition(TrackerPosition position) {
		throw new UnsupportedOperationException(
			"PoseFrameTracker does not allow setting the body position"
		);
	}

	@Override
	public boolean userEditable() {
		return false;
	}

	@Override
	public boolean hasRotation() {
		TrackerFrame frame = safeGetFrame();
		return frame != null && frame.hasData(TrackerFrameData.ROTATION);
	}

	@Override
	public boolean hasPosition() {
		TrackerFrame frame = safeGetFrame();
		return frame != null && frame.hasData(TrackerFrameData.POSITION);
	}

	@Override
	public boolean isComputed() {
		return true;
	}
	// #endregion

	@Override
	public Iterator<TrackerFrame> iterator() {
		return frames.iterator();
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
	public UDPDevice getDevice() {
		return null;
	}
}
