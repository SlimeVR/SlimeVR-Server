package dev.slimevr.tracking.videocalibration.sources

import dev.slimevr.tracking.trackers.Tracker
import dev.slimevr.tracking.trackers.TrackerPosition
import dev.slimevr.tracking.videocalibration.snapshots.TrackerSnapshot
import dev.slimevr.tracking.videocalibration.snapshots.TrackersSnapshot
import dev.slimevr.tracking.videocalibration.util.DebugOutput
import dev.slimevr.tracking.videocalibration.util.ScheduledInterval
import io.eiren.util.logging.LogManager
import io.github.axisangles.ktmath.Vector3D
import kotlinx.coroutines.channels.Channel
import kotlin.time.Duration
import kotlin.time.TimeSource

/**
 * Takes snapshots of the trackers at a fixed interval.
 */
class TrackersSource(
	private val trackersToRecord: Map<TrackerPosition, Tracker>,
	private val interval: Duration,
	private val debugOutput: DebugOutput,
) {

	enum class Status {
		NOT_STARTED,
		RUNNING,
		DONE,
	}

	var status = Status.NOT_STARTED
		private set

	val trackersSnapshots = Channel<TrackersSnapshot>(Channel.Factory.UNLIMITED)

	private val scheduler = ScheduledInterval(interval)
	private val allTrackerSnapshots = mutableListOf<TrackersSnapshot>()

	init {
// 		val missingTrackers = REQUIRED_TRACKERS_TO_RECORD.subtract(trackersToRecord.keys)
// 		require(missingTrackers.isEmpty()) { "Required trackers are missing: $missingTrackers" }
	}

	/**
	 * Starts taking snapshots. Snapshots will be published to [trackersSnapshots].
	 */
	fun start() {
		status = Status.RUNNING
	}

	/**
	 * Stops taking snapshots.
	 */
	fun requestStop() {
		status = Status.DONE
		debugOutput.saveTrackerSnapshots(trackersToRecord, allTrackerSnapshots, interval)
	}

	/**
	 * Must be called on each server tick, to take snapshots at appropriate times.
	 */
	fun onTick() {
		if (!scheduler.shouldInvoke()) {
			return
		}

		val snapshot: TrackersSnapshot
		try {
			snapshot = makeSnapshot()
		} catch (e: Exception) {
			LogManager.warning("Failed to create trackers snapshot", e)
			requestStop()
			return
		}

		allTrackerSnapshots.add(snapshot)
		trackersSnapshots.trySend(snapshot)
	}

	/**
	 * Creates a snapshot of the trackers to record.
	 */
	private fun makeSnapshot(): TrackersSnapshot {
		val now = TimeSource.Monotonic.markNow()

		val snapshots = mutableMapOf<TrackerPosition, TrackerSnapshot>()
		for ((trackerPosition, tracker) in trackersToRecord) {
			if (tracker.trackerPosition != trackerPosition) {
				error("Tracker ${tracker.name} position has changed")
			}

			val rawTrackerToWorld = tracker.getRawRotation().toDouble()
			val adjustedTrackerToWorld = tracker.getRotation().toDouble()

			var trackerOriginInWorld: Vector3D? = null
			if (TRACKERS_WITH_POSITION.contains(trackerPosition)) {
				if (tracker.hasPosition) {
					trackerOriginInWorld = tracker.position.toDouble()
				} else {
					error("Tracker $trackerPosition is missing required position")
				}
			}

			snapshots[trackerPosition] =
				TrackerSnapshot(
					rawTrackerToWorld,
					adjustedTrackerToWorld,
					trackerOriginInWorld,
				)
		}

		return TrackersSnapshot(now, snapshots)
	}

	companion object {

		private val REQUIRED_TRACKERS_TO_RECORD = setOf(
			TrackerPosition.HEAD,
			TrackerPosition.LEFT_HAND,
			TrackerPosition.RIGHT_HAND,
		)

		private val TRACKERS_WITH_POSITION = REQUIRED_TRACKERS_TO_RECORD
	}
}
