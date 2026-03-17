package dev.slimevr.tracking.videocalibration.steps

import dev.slimevr.tracking.trackers.TrackerPosition
import dev.slimevr.tracking.videocalibration.snapshots.TrackersSnapshot
import dev.slimevr.tracking.videocalibration.util.toAngleAxisString
import io.eiren.util.logging.LogManager
import io.github.axisangles.ktmath.QuaternionD
import org.apache.commons.math3.util.FastMath
import kotlin.time.Duration.Companion.seconds

class CaptureBentOverPose {

	class Solution(
		val reference: QuaternionD,
		// Snapshot of rotations of all the trackers when the bent over pose was captured
		val trackerRotations: List<Map<TrackerPosition, QuaternionD>>,
	)

	private val minDuration = 1.seconds
	private val maxAngleDeviation = FastMath.toRadians(5.0)
	private val minStableSnapshots = 30

	// TODO: Same as SolveUpperBodyTrackerReset
	val trackerPositions = setOf(
		TrackerPosition.UPPER_CHEST,
		TrackerPosition.CHEST,
		TrackerPosition.WAIST,
		TrackerPosition.HIP,
	)

	val minBentOverAngle = FastMath.toRadians(30.0)

	fun capture(
		trackersSnapshots: List<TrackersSnapshot>,
		forwardPose: CaptureForwardPose.Solution,
	): Solution? {
		if (trackersSnapshots.isEmpty()) {
			return null
		}

		val headRotations = trackersSnapshots.mapNotNull {
			val head = it.trackers[TrackerPosition.HEAD] ?: return@mapNotNull null
			it.instant to head.rawTrackerToWorld
		}

		if (headRotations.isEmpty()) {
			return null
		}

		val (latestInstant, latestHeadRotation) = headRotations.last()

		// TODO: Upper body trackers must have at least 30 deg inclination from rotation at reference

		var numStableSnapshots = 0
		var hasMinDuration = false
		for ((instant, headRotation) in headRotations.reversed()) {
			if ((latestHeadRotation * headRotation.inv()).angleR() > maxAngleDeviation) {
				break
			}
			++numStableSnapshots

			if (latestInstant - instant > minDuration) {
				hasMinDuration = true
				break
			}
		}

		if (!hasMinDuration || numStableSnapshots < minStableSnapshots) {
			return null
		}

		for (trackerPosition in trackerPositions) {
			val bentOverTrackerRotation = trackersSnapshots.last().trackers[trackerPosition]
			val forwardPoseTrackerRotation = forwardPose.trackerRotations.last()[trackerPosition]
			if (bentOverTrackerRotation != null && forwardPoseTrackerRotation != null) {
				// TODO: Required tracker must have data
				if (bentOverTrackerRotation.rawTrackerToWorld.angleToR(
						forwardPoseTrackerRotation,
					) < minBentOverAngle
				) {
					LogManager.debug("Skipping because not bent over enough")
					return null
				}
			}
		}

		// TODO: Get average over duration instead

		LogManager.info("Found bent-over pose: ${latestHeadRotation.toAngleAxisString()}")

		val trackerRotations = trackersSnapshots.takeLast(50).map {
			it.trackers
				.mapNotNull { (trackerPosition, trackerSnapshot) ->
					trackerPosition to trackerSnapshot.rawTrackerToWorld
				}
				.toMap()
		}

		return Solution(latestHeadRotation, trackerRotations)
	}
}
