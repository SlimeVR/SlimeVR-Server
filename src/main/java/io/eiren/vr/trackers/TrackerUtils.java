package io.eiren.vr.trackers;

import java.util.List;

import io.eiren.vr.processor.TrackerBodyPosition;

public class TrackerUtils {
	
	private TrackerUtils() {
	}

	public static <T extends Tracker> T findTrackerForBodyPosition(T[] allTrackers, TrackerBodyPosition position) {
		for(int i = 0; i < allTrackers.length; ++i) {
			T t = allTrackers[i];
			if(t != null && t.getBodyPosition() == position)
				return t;
		}
		return null;
	}

	public static <T extends Tracker> T findTrackerForBodyPosition(List<T> allTrackers, TrackerBodyPosition position) {
		for(int i = 0; i < allTrackers.size(); ++i) {
			T t = allTrackers.get(i);
			if(t != null && t.getBodyPosition() == position)
				return t;
		}
		return null;
	}

	public static <T extends Tracker> T findTrackerForBodyPosition(List<T> allTrackers, TrackerBodyPosition position, TrackerBodyPosition altPosition) {
		T t = findTrackerForBodyPosition(allTrackers, position);
		if(t != null)
			return t;
		return findTrackerForBodyPosition(allTrackers, altPosition);
	}

	public static <T extends Tracker> T findTrackerForBodyPosition(T[] allTrackers, TrackerBodyPosition position, TrackerBodyPosition altPosition) {
		T t = findTrackerForBodyPosition(allTrackers, position);
		if(t != null)
			return t;
		return findTrackerForBodyPosition(allTrackers, altPosition);
	}

	public static Tracker findTrackerForBodyPositionOrEmpty(List<? extends Tracker> allTrackers, TrackerBodyPosition position, TrackerBodyPosition altPosition) {
		Tracker t = findTrackerForBodyPosition(allTrackers, position);
		if(t != null)
			return t;
		t = findTrackerForBodyPosition(allTrackers, altPosition);
		if(t != null)
			return t;
		return new ComputedTracker("Empty tracker", false, false);
	}

	public static Tracker findTrackerForBodyPositionOrEmpty(Tracker[] allTrackers, TrackerBodyPosition position, TrackerBodyPosition altPosition) {
		Tracker t = findTrackerForBodyPosition(allTrackers, position);
		if(t != null)
			return t;
		t = findTrackerForBodyPosition(allTrackers, altPosition);
		if(t != null)
			return t;
		return new ComputedTracker("Empty tracker", false, false);
	}
}
