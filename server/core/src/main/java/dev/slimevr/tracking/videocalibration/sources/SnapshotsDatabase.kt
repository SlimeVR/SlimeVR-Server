package dev.slimevr.tracking.videocalibration.sources

import dev.slimevr.tracking.videocalibration.snapshots.HumanPoseSnapshot
import dev.slimevr.tracking.videocalibration.snapshots.TrackersSnapshot
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.onSuccess
import kotlin.time.Duration

/**
 * Database of snapshots.
 */
class SnapshotsDatabase(
	private val maxJitter: Duration,
	private val humanPoseFramesSource: Channel<HumanPoseSnapshot>,
	private val trackersFramesSource: Channel<TrackersSnapshot>,
) {

	val allHumanPoseSnapshots = mutableListOf<HumanPoseSnapshot>()
	val allTrackersSnapshots = mutableListOf<TrackersSnapshot>()

	val recentHumanPoseSnapshots = mutableListOf<HumanPoseSnapshot>()
	val recentTrackersSnapshots = mutableListOf<TrackersSnapshot>()

	/**
	 * Inserts all pending snapshots into the database.
	 */
	fun update() {
		while (true) {
			humanPoseFramesSource.tryReceive()
				.onSuccess {
					allHumanPoseSnapshots.add(it)
					recentHumanPoseSnapshots.add(it)
				}
				.onFailure { break }
		}

		while (true) {
			trackersFramesSource.tryReceive()
				.onSuccess {
					allTrackersSnapshots.add(it)
					recentTrackersSnapshots.add(it)
				}
				.onFailure { break }
		}
	}

	fun matchAll(poseDelay: Duration) = match(allHumanPoseSnapshots, allTrackersSnapshots, poseDelay)

	fun matchRecent(poseDelay: Duration) = match(recentHumanPoseSnapshots, recentTrackersSnapshots, poseDelay)

	/**
	 * Matches human pose snapshots with tracker snapshots, after applying a delay to
	 * the human pose snapshots since they may be captured at different times.
	 */
	private fun match(
		humanPoseSnapshots: List<HumanPoseSnapshot>,
		trackersSnapshots: List<TrackersSnapshot>,
		humanPoseDelay: Duration,
	): List<Pair<TrackersSnapshot, HumanPoseSnapshot>> {
		val result = mutableListOf<Pair<TrackersSnapshot, HumanPoseSnapshot>>()

		var j = 0
		for (pose in humanPoseSnapshots) {
			val poseInstant = pose.instant + humanPoseDelay

			while (j + 1 < trackersSnapshots.size) {
				val current = trackersSnapshots[j]
				val currentDiff = (poseInstant - current.instant).absoluteValue

				val next = trackersSnapshots[j + 1]
				val nextDiff = (poseInstant - next.instant).absoluteValue

				if (nextDiff < currentDiff) {
					j++
				} else {
					break
				}
			}

			if ((trackersSnapshots[j].instant - poseInstant).absoluteValue < maxJitter) {
				result += Pair(trackersSnapshots[j], pose)
			}
		}

		return result
	}

	/**
	 * Clears the recent snapshots.
	 */
	fun clearRecent() {
		recentHumanPoseSnapshots.clear()
		recentTrackersSnapshots.clear()
	}
}
