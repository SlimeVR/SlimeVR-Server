package io.eiren.vr.trackers;

import java.util.List;

import io.eiren.vr.processor.TrackerBodyPosition;

public class TrackerUtils {
	
	private TrackerUtils() {
	}

	public static Tracker findTrackerForBodyPosition(List<? extends Tracker> allTrackers, TrackerBodyPosition position) {
		for(int i = 0; i < allTrackers.size(); ++i) {
			Tracker t = allTrackers.get(i);
			if(t.getBodyPosition() == position)
				return t;
		}
		return null;
	}

	public static Tracker findTrackerForBodyPosition(List<? extends Tracker> allTrackers, TrackerBodyPosition position, TrackerBodyPosition altPosition) {
		Tracker t = findTrackerForBodyPosition(allTrackers, position);
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
}
