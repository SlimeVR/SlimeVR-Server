package dev.slimevr.unit

import dev.slimevr.tracking.trackers.Tracker
import dev.slimevr.tracking.trackers.TrackerPosition
import dev.slimevr.tracking.trackers.TrackerStatus

class TestTrackerSet(
	val computed: Boolean = false,
	val positional: Boolean = false,
	val resetHead: Boolean = false,
) {

	val head = mkTrack(0, TrackerPosition.HEAD, true)

	val chest = mkTrack(1, TrackerPosition.CHEST)
	val hip = mkTrack(2, TrackerPosition.HIP)

	val leftThigh = mkTrack(3, TrackerPosition.LEFT_UPPER_LEG)
	val leftCalf = mkTrack(4, TrackerPosition.LEFT_LOWER_LEG)

	val rightThigh = mkTrack(5, TrackerPosition.RIGHT_UPPER_LEG)
	val rightCalf = mkTrack(6, TrackerPosition.RIGHT_LOWER_LEG)

	/**
	 * All the trackers in the set.
	 */
	val set = arrayOf(
		chest,
		hip,
		leftThigh,
		leftCalf,
		rightThigh,
		rightCalf,
	)

	/**
	 * All the trackers in the set as a list.
	 */
	val setL = set.asList()

	/**
	 * All the trackers in the set plus the headset.
	 */
	val all = set + head

	/**
	 * All the trackers in the set plus the headset as a list.
	 */
	val allL = all.asList()

	fun mkTrack(id: Int, pos: TrackerPosition, isHmd: Boolean = false): Tracker {
		val tracker = Tracker(
			device = null,
			id = id,
			name = pos.name,
			trackerPosition = pos,
			trackerNum = 0,
			hasPosition = positional || isHmd,
			hasRotation = true,
			isComputed = computed || isHmd,
			allowReset = resetHead || !isHmd,
			allowMounting = resetHead || !isHmd,
			isHmd = isHmd,
			trackRotDirection = false,
		)
		tracker.status = TrackerStatus.OK
		return tracker
	}
}
