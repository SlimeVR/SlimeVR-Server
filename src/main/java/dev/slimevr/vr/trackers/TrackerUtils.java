package dev.slimevr.vr.trackers;

import java.util.List;

import dev.slimevr.vr.processor.ComputedHumanPoseTracker;


public class TrackerUtils {

	private TrackerUtils() {
	}

	/**
	 * Finds the first tracker from allTrackers matching the position
	 * 
	 * @return The tracker as a Tracker
	 */
	public static <T extends Tracker> T findTrackerForBodyPosition(
		List<T> allTrackers,
		TrackerPosition position
	) {
		if (position == null)
			return null;
		for (T t : allTrackers) {
			if (t != null && t.getBodyPosition() == position)
				return t;
		}
		return null;
	}

	/**
	 * Finds the first non ComputedHumanPoseTracker tracker from allTrackers
	 * matching the position
	 * 
	 * @return The non ComputedHumanPoseTracker as a Tracker
	 */
	public static <T extends Tracker> T findNonComputedHumanPoseTrackerForBodyPosition(
		List<T> allTrackers,
		TrackerPosition position
	) {
		if (position == null)
			return null;
		for (T t : allTrackers) {
			if (
				t != null
					&& t.getBodyPosition() == position
					&& !(t instanceof ComputedHumanPoseTracker)
			)
				return t;
		}
		return null;
	}

	/**
	 * Returns the first tracker that isn't null out of the 3 trackers maximum
	 * passed as arguments.
	 * 
	 * 
	 * @return The first non null tracker.
	 */
	public static <T extends Tracker> T getFirstAvailableTracker(
		T firstTracker,
		T secondTracker,
		T thirdTracker
	) {
		if (firstTracker != null)
			return firstTracker;
		if (secondTracker != null)
			return secondTracker;
		if (thirdTracker != null)
			return thirdTracker;

		return null;
	}
}
