package dev.slimevr.tracking.trackers;

import java.util.List;


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
		for (T t : allTrackers) {
			if (t.getBodyPosition() == position)
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
		for (T t : allTrackers) {
			if (
				t.getBodyPosition() == position
					&& !(t instanceof ComputedHumanPoseTracker)
					&& !(t instanceof HMDTracker)
			)
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
	public static <T extends Tracker> T getHMDTracker(
		List<T> allTrackers
	) {
		for (T t : allTrackers) {
			if (t instanceof HMDTracker && t.getStatus() != TrackerStatus.DISCONNECTED)
				return t;
		}
		return null;
	}

	/**
	 * Returns the first tracker that isn't null out of the 2 trackers passed as
	 * arguments.
	 *
	 * @return The first non-null tracker or null
	 */
	public static <T extends Tracker> T getFirstAvailableTracker(
		T firstTracker,
		T secondTracker
	) {
		if (firstTracker != null)
			return firstTracker;
		return secondTracker;
	}

	/**
	 * Returns the first tracker that isn't null out of the 3 trackers passed as
	 * arguments.
	 *
	 * @return The first non-null tracker or null
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
		return thirdTracker;
	}
}
