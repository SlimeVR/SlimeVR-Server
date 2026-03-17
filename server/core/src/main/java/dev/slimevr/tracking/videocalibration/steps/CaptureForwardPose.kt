package dev.slimevr.tracking.videocalibration.steps

import dev.slimevr.tracking.trackers.TrackerPosition
import dev.slimevr.tracking.videocalibration.snapshots.TrackersSnapshot
import dev.slimevr.tracking.videocalibration.util.toAngleAxisString
import io.eiren.util.logging.LogManager
import io.github.axisangles.ktmath.Matrix3D
import io.github.axisangles.ktmath.QuaternionD
import io.github.axisangles.ktmath.Vector3D
import org.apache.commons.math3.util.FastMath
import kotlin.math.*
import kotlin.time.Duration.Companion.seconds

class CaptureForwardPose {

	class Solution(
		// Reference yaw (to align the Z-axes of all the trackers, i.e. backwards)
		val reference: QuaternionD,
		// Snapshot of rotations of all the trackers when the reference yaw was captured
		val trackerRotations: List<Map<TrackerPosition, QuaternionD>>,
	)

	private val minDuration = 2.seconds
	private val maxAngleDeviation = FastMath.toRadians(5.0)
	private val minAngleFromVertical = FastMath.toRadians(20.0)
	private val minStableSnapshots = 30

	fun capture(trackersSnapshots: List<TrackersSnapshot>): Solution? {
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

		// HEAD tracker must be level
		if (
			abs(latestHeadRotation.sandwichUnitY().dot(Vector3D.POS_Y)) < cos(minAngleFromVertical)
		) {
			return null
		}

		var numStableSnapshots = 0
		var hasMinDuration = false
		for ((instant, headRotation) in headRotations.reversed()) {
			if (latestHeadRotation.angleToR(headRotation) > maxAngleDeviation) {
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

		// TODO: Get average over duration instead

		val headZAxis = latestHeadRotation.sandwichUnitZ()
		val zAxis = Vector3D(headZAxis.x, 0.0, headZAxis.z).unit()
		val yAxis = Vector3D.POS_Y
		val xAxis = yAxis.cross(zAxis)
		val reference = Matrix3D(xAxis, yAxis, zAxis).toQuaternionAssumingOrthonormal()

		LogManager.info("Found forward pose: ${reference.toAngleAxisString()}")

		val trackerRotations = trackersSnapshots.takeLast(50).map {
			it.trackers
				.mapNotNull { (trackerPosition, trackerSnapshot) ->
					trackerPosition to trackerSnapshot.rawTrackerToWorld
				}
				.toMap()
		}

		return Solution(reference, trackerRotations)
	}
}
