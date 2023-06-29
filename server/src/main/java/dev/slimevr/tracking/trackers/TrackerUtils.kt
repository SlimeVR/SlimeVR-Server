package dev.slimevr.tracking.trackers

object TrackerUtils {

	/**
	 * Finds the first non-internal tracker from allTrackers
	 * matching the position, that is not DISCONNECTED
	 *
	 * @return A non-internal tracker
	 */
	@JvmStatic
	fun getNonInternalTrackerForBodyPosition(
		allTrackers: List<Tracker>,
		position: TrackerPosition,
	): Tracker? = allTrackers.firstOrNull {
		it.trackerPosition === position &&
			!it.isInternal &&
			it.status != TrackerStatus.DISCONNECTED &&
			it.status != TrackerStatus.ERROR
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
}
