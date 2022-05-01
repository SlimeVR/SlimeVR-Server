package dev.slimevr.vr.trackers;

import java.util.List;

public class TrackerUtils {

	private TrackerUtils() {
	}

	public static <T extends Tracker> T findTrackerForBodyPosition(T[] allTrackers, TrackerPosition position) {
		if (position == null)
			return null;
		for (int i = 0; i < allTrackers.length; ++i) {
			T t = allTrackers[i];
			if (t != null && t.getBodyPosition() == position)
				return t;
		}
		return null;
	}

	public static <T extends Tracker> T findTrackerForBodyPosition(List<T> allTrackers, TrackerPosition position) {
		if (position == null)
			return null;
		for (T t : allTrackers) {
			if (t != null && t.getBodyPosition() == position)
				return t;
		}
		return null;
	}

	public static <T extends Tracker> T findTrackerForBodyPosition(List<T> allTrackers, TrackerPosition position, TrackerPosition altPosition) {
		T t = findTrackerForBodyPosition(allTrackers, position);
		if (t != null)
			return t;
		return findTrackerForBodyPosition(allTrackers, altPosition);
	}

	public static <T extends Tracker> T findTrackerForBodyPosition(T[] allTrackers, TrackerPosition position, TrackerPosition altPosition, TrackerPosition secondAltPosition) {
		T t = findTrackerForBodyPosition(allTrackers, position);
		if (t != null)
			return t;
		t = findTrackerForBodyPosition(allTrackers, altPosition);
		if (t != null)
			return t;
		return findTrackerForBodyPosition(allTrackers, secondAltPosition);
	}

	public static Tracker findTrackerForBodyPositionOrEmpty(List<? extends Tracker> allTrackers, TrackerPosition position, TrackerPosition altPosition, TrackerPosition secondAltPosition) {
		Tracker t = findTrackerForBodyPosition(allTrackers, position);
		if (t != null)
			return t;
		t = findTrackerForBodyPosition(allTrackers, altPosition);
		if (t != null)
			return t;
		t = findTrackerForBodyPosition(allTrackers, secondAltPosition);
		if (t != null)
			return t;
		return new ComputedTracker(Tracker.getNextLocalTrackerId(), "Empty tracker", false, false);
	}

	public static Tracker findTrackerForBodyPositionOrEmpty(Tracker[] allTrackers, TrackerPosition position, TrackerPosition altPosition) {
		Tracker t = findTrackerForBodyPosition(allTrackers, position);
		if (t != null)
			return t;
		t = findTrackerForBodyPosition(allTrackers, altPosition);
		if (t != null)
			return t;
		return new ComputedTracker(Tracker.getNextLocalTrackerId(), "Empty tracker", false, false);
	}
}
