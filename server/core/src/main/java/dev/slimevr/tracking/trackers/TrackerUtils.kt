package dev.slimevr.tracking.trackers

import solarxr_protocol.datatypes.BodyPart

object TrackerUtils {

	/**
	 * Finds a suitable tracker for use in the SlimeVR skeleton
	 * in allTrackers matching the position.
	 *
	 * This won't return disconnected, errored or internal trackers,
	 * but it will return timed out trackers, and they have a lower
	 * priority than normal trackers.
	 *
	 * @return A tracker for use in the SlimeVR skeleton
	 */
	@JvmStatic
	fun getTrackerForSkeleton(
		allTrackers: List<Tracker>,
		position: TrackerPosition,
	): Tracker? = getNonInternalTrackerForBodyPosition(allTrackers, position)

	/**
	 * Finds the first non-internal tracker from allTrackers
	 * matching the position, that is not `TrackerStatus.reset`.
	 * It will also choose timed out trackers, but they have a lower
	 * priority than the rest of the trackers
	 *
	 * @return A non-internal tracker
	 */
	private fun getNonInternalTrackerForBodyPosition(
		allTrackers: List<Tracker>,
		position: TrackerPosition,
	): Tracker? {
		val resetTrackers = allTrackers.filter {
			it.trackerPosition == position &&
				!it.isInternal &&
				!it.status.reset
		}
		return resetTrackers.firstOrNull { it.status != TrackerStatus.TIMED_OUT } ?: resetTrackers.firstOrNull()
	}

	/**
	 * Finds the first non-internal non-computed tracker from allTrackers
	 * matching the position, that is not `TrackerStatus.reset`.
	 * It will also choose timed out trackers, but they have a lower
	 * priority than the rest of the trackers
	 *
	 * @return A non-internal non-computed tracker
	 */
	private fun getNonInternalNonComputedTrackerForBodyPosition(
		allTrackers: List<Tracker>,
		position: TrackerPosition,
	): Tracker? {
		val resetTrackers = allTrackers.filter {
			it.trackerPosition == position &&
				!it.isComputed &&
				!it.isInternal &&
				!it.status.reset
		}
		return resetTrackers.firstOrNull { it.status != TrackerStatus.TIMED_OUT } ?: resetTrackers.firstOrNull()
	}

	/**
	 * Finds the first non-internal and non-imu tracker from allTrackers
	 * matching the position, that is not `TrackerStatus.reset`.
	 * It will also choose timed out trackers, but they have a lower
	 * priority than the rest of the trackers
	 * @return A non-internal non-imu tracker
	 */
	@JvmStatic
	fun getNonInternalNonImuTrackerForBodyPosition(
		allTrackers: List<Tracker>,
		position: TrackerPosition,
	): Tracker? {
		val resetTrackers = allTrackers.filter {
			it.trackerPosition == position &&
				!it.isImu() &&
				!it.isInternal &&
				!it.status.reset
		}
		return resetTrackers.firstOrNull { it.status != TrackerStatus.TIMED_OUT } ?: resetTrackers.firstOrNull()
	}

	/**
	 * Returns the first tracker that isn't null out of the n trackers passed as
	 * arguments.
	 *
	 * @return The first non-null tracker or null
	 */
	@JvmStatic
	fun getFirstAvailableTracker(
		vararg trackers: Tracker?,
	): Tracker? = trackers.firstOrNull { it != null }

	val allBodyPartsButFingers = listOf(
		BodyPart.HEAD, BodyPart.NECK, BodyPart.UPPER_CHEST,
		BodyPart.CHEST, BodyPart.WAIST, BodyPart.HIP,
		BodyPart.LEFT_UPPER_LEG, BodyPart.RIGHT_UPPER_LEG, BodyPart.LEFT_LOWER_LEG,
		BodyPart.RIGHT_LOWER_LEG, BodyPart.LEFT_LOWER_ARM, BodyPart.RIGHT_LOWER_ARM,
		BodyPart.LEFT_UPPER_ARM, BodyPart.RIGHT_UPPER_ARM, BodyPart.LEFT_HAND,
		BodyPart.RIGHT_HAND, BodyPart.LEFT_SHOULDER, BodyPart.RIGHT_SHOULDER,
		BodyPart.LEFT_FOOT, BodyPart.RIGHT_FOOT,
	)

	val allBodyPartsButFingersAndFeets = listOf(
		BodyPart.HEAD, BodyPart.NECK, BodyPart.UPPER_CHEST,
		BodyPart.CHEST, BodyPart.WAIST, BodyPart.HIP,
		BodyPart.LEFT_UPPER_LEG, BodyPart.RIGHT_UPPER_LEG, BodyPart.LEFT_LOWER_LEG,
		BodyPart.RIGHT_LOWER_LEG, BodyPart.LEFT_LOWER_ARM, BodyPart.RIGHT_LOWER_ARM,
		BodyPart.LEFT_UPPER_ARM, BodyPart.RIGHT_UPPER_ARM, BodyPart.LEFT_HAND,
		BodyPart.RIGHT_HAND, BodyPart.LEFT_SHOULDER, BodyPart.RIGHT_SHOULDER,
	)

	val feetsBodyParts = listOf(
		BodyPart.LEFT_FOOT,
		BodyPart.RIGHT_FOOT,
	)
}
