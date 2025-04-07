package dev.slimevr.tracking.trackers

class ResetParams(
	val source: String,
	val bodyPose: ResetBodyPose,
	val referenceTrackerPosition: TrackerPosition,
	val trackerPositionsToReset: Set<TrackerPosition>,
) {
	fun shouldReset(tracker: Tracker) =
		trackerPositionsToReset.isEmpty() || trackerPositionsToReset.contains(tracker.trackerPosition)

	companion object {

		fun makeDefault(source: String?) =
			ResetParams(source ?: "UNKNOWN", ResetBodyPose.SKIING, TrackerPosition.HEAD, setOf())
	}
}
