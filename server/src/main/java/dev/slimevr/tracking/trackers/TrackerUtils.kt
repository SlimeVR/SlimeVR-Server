package dev.slimevr.tracking.trackers

object TrackerUtils {

	/**
	 * Finds the first non-internal tracker from allTrackers
	 * matching the position, that is not DISCONNECTED
	 * These can be IMU (physical/SlimeVR) trackers, trackers from SteamVR (Vive),
	 * or even trackers from third party applications (joycons)
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
	 * Finds the first non-internal Computed tracker from allTrackers
	 * matching the position, that is not DISCONNECTED.
	 * This should return the HMD or any SteamVR tracker assigned
	 * to the head position.
	 *
	 * @return A non-internal computed tracker (e.g. HMD)
	 */
	@JvmStatic
	fun getNonInternalComputedTrackerForBodyPosition(
		allTrackers: List<Tracker>,
		position: TrackerPosition,
	): Tracker? = allTrackers.firstOrNull {
		it.trackerPosition === position &&
			it.isComputed &&
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
