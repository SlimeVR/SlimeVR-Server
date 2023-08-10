package dev.slimevr.tracking.trackers

object TrackerUtils {

	/**
	 * Finds a suitable tracker for use in the SlimeVR skeleton
	 * in allTrackers matching the position.
	 * This won't return disconnected, errored or internal trackers.
	 *
	 * @return A tracker for use in the SlimeVR skeleton
	 */
	@JvmStatic
	fun getTrackerForSkeleton(
		allTrackers: List<Tracker>,
		position: TrackerPosition,
	): Tracker? {
		return getNonInternalTrackerForBodyPosition(allTrackers, position)
	}

	/**
	 * Finds the first non-internal tracker from allTrackers
	 * matching the position, that is not DISCONNECTED
	 *
	 * @return A non-internal tracker
	 */
	private fun getNonInternalTrackerForBodyPosition(
		allTrackers: List<Tracker>,
		position: TrackerPosition,
	): Tracker? = allTrackers.firstOrNull {
		it.trackerPosition === position &&
			!it.isInternal &&
			!it.status.reset
	}

	/**
	 * Finds the first non-internal non-computed tracker from allTrackers
	 * matching the position, that is not DISCONNECTED.
	 *
	 * @return A non-internal non-computed tracker
	 */
	private fun getNonInternalNonComputedTrackerForBodyPosition(
		allTrackers: List<Tracker>,
		position: TrackerPosition,
	): Tracker? = allTrackers.firstOrNull {
		it.trackerPosition === position &&
			!it.isComputed &&
			!it.isInternal &&
			!it.status.reset
	}

	/**
	 * Finds the first non-internal and non-imu tracker from allTrackers
	 * matching the position, that is not DISCONNECTED.
	 *
	 * @return A non-internal non-imu tracker
	 */
	@JvmStatic
	fun getNonInternalNonImuTrackerForBodyPosition(
		allTrackers: List<Tracker>,
		position: TrackerPosition,
	): Tracker? = allTrackers.firstOrNull {
		it.trackerPosition === position &&
			!it.isImu() &&
			!it.isInternal &&
			!it.status.reset
	}

	/**
	 * Returns the first tracker that isn't null out of the 2 trackers passed as
	 * arguments.
	 *
	 * @return The first non-null tracker or null
	 */
	@JvmStatic
	fun getFirstAvailableTracker(
		firstTracker: Tracker?,
		secondTracker: Tracker?,
	): Tracker? = firstTracker ?: secondTracker

	/**
	 * Returns the first tracker that isn't null out of the 3 trackers passed as
	 * arguments.
	 *
	 * @return The first non-null tracker or null
	 */
	@JvmStatic
	fun getFirstAvailableTracker(
		firstTracker: Tracker?,
		secondTracker: Tracker?,
		thirdTracker: Tracker?,
	): Tracker? = firstTracker ?: (secondTracker ?: thirdTracker)

	/**
	 * Returns the first tracker that isn't null out of the 4 trackers passed as
	 * arguments.
	 *
	 * @return The first non-null tracker or null
	 */
	@JvmStatic
	fun getFirstAvailableTracker(
		firstTracker: Tracker?,
		secondTracker: Tracker?,
		thirdTracker: Tracker?,
		fourthTracker: Tracker?,
	): Tracker? = firstTracker ?: (secondTracker ?: (thirdTracker ?: fourthTracker))
}
