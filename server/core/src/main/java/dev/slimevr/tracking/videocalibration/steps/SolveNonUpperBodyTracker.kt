package dev.slimevr.tracking.videocalibration.steps

import dev.slimevr.tracking.trackers.TrackerPosition
import dev.slimevr.tracking.videocalibration.data.Camera
import dev.slimevr.tracking.videocalibration.data.CocoWholeBodyKeypoint
import dev.slimevr.tracking.videocalibration.data.TrackerResetOverride
import dev.slimevr.tracking.videocalibration.snapshots.HumanPoseSnapshot
import dev.slimevr.tracking.videocalibration.snapshots.TrackersSnapshot
import dev.slimevr.tracking.videocalibration.sources.SnapshotsDatabase
import dev.slimevr.tracking.videocalibration.util.numericalJacobian
import io.eiren.util.logging.LogManager
import io.github.axisangles.ktmath.QuaternionD
import io.github.axisangles.ktmath.Vector2D
import io.github.axisangles.ktmath.Vector3D
import org.apache.commons.math3.analysis.MultivariateVectorFunction
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresBuilder
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer
import org.apache.commons.math3.util.FastMath
import kotlin.math.PI
import kotlin.math.abs
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class SolveNonUpperBodyTracker {

	data class Solution(
		val trackerPosition: TrackerPosition,
		val trackerReset: TrackerResetOverride,
		val cameraDelay: Duration,
	)

	val trackerPositionToJoints = mapOf(
		TrackerPosition.LEFT_UPPER_LEG to Pair(CocoWholeBodyKeypoint.LEFT_HIP, CocoWholeBodyKeypoint.LEFT_KNEE),
		TrackerPosition.LEFT_LOWER_LEG to Pair(CocoWholeBodyKeypoint.LEFT_KNEE, CocoWholeBodyKeypoint.LEFT_ANKLE),
		TrackerPosition.RIGHT_UPPER_LEG to Pair(CocoWholeBodyKeypoint.RIGHT_HIP, CocoWholeBodyKeypoint.RIGHT_KNEE),
		TrackerPosition.RIGHT_LOWER_LEG to Pair(CocoWholeBodyKeypoint.RIGHT_KNEE, CocoWholeBodyKeypoint.RIGHT_ANKLE),
		TrackerPosition.LEFT_UPPER_ARM to Pair(CocoWholeBodyKeypoint.LEFT_SHOULDER, CocoWholeBodyKeypoint.LEFT_ELBOW),
		TrackerPosition.RIGHT_UPPER_ARM to Pair(CocoWholeBodyKeypoint.RIGHT_SHOULDER, CocoWholeBodyKeypoint.RIGHT_ELBOW),
	)

	private val minMatches = 120

	fun solve(
		trackerPosition: TrackerPosition,
		camera: Camera,
		forwardPose: CaptureForwardPose.Solution,
		snapshotsDatabase: SnapshotsDatabase,
		cameraDelay: Duration,
	): Solution? {
		val frames = snapshotsDatabase.matchRecent(cameraDelay)
		if (frames.isEmpty()) {
			return null
		}

		val joints = trackerPositionToJoints[trackerPosition]
		if (joints == null) {
			return null
		}

		val matches = buildMatches(trackerPosition, frames, joints.first, joints.second)

		val filteredMatches = filterMatches(matches)
		if (filteredMatches.size < minMatches) {
			return null
		}

		if (!enoughRotation(filteredMatches)) {
			return null
		}

		LogManager.info("Trying to solve $trackerPosition...")

		var bestRMS = Double.POSITIVE_INFINITY
		var bestTotalDelay: Duration? = null
		var bestTrackerReset: TrackerResetOverride? = null

		for (extraDelay in -500..500 step 10) {
			val totalDelay = cameraDelay + extraDelay.milliseconds
			val shiftedFrames = snapshotsDatabase.matchRecent(totalDelay)
			val shiftedMatches = buildMatches(trackerPosition, shiftedFrames, joints.first, joints.second)
			val filteredShiftedMatches = filterMatches(shiftedMatches)
			if (filteredShiftedMatches.size < minMatches) {
				return null
			}

			var bestInitialRMS = Double.POSITIVE_INFINITY
			var bestInitialParams: DoubleArray? = null
			for (initialParams in startingParams()) {
				val rms = calcError(
					initialParams,
					trackerPosition,
					filteredShiftedMatches,
					camera,
					forwardPose,
				)
				if (rms < bestInitialRMS) {
					bestInitialRMS = rms
					bestInitialParams = initialParams
				}
			}

			if (bestInitialParams == null) {
				LogManager.warning("Failed to find best initial params")
				return null
			}

			val result = solveLM(
				trackerPosition,
				filteredShiftedMatches,
				camera,
				forwardPose,
				bestInitialParams,
			)
			if (result == null) {
				LogManager.warning("Failed to optimize tracker")
				return null
			}

			val (trackerReset, rms) = result
			if (rms < bestRMS) {
				bestRMS = rms
				bestTotalDelay = totalDelay
				bestTrackerReset = trackerReset
			}
		}

		if (bestTotalDelay == null || bestTrackerReset == null) {
			return null
		}

		LogManager.info("Found tracker resets for $trackerPosition: $bestTrackerReset delay=$bestTotalDelay")

		return Solution(trackerPosition, bestTrackerReset, bestTotalDelay)
	}

	class Match(
		val trackerRotation: QuaternionD,
		val lowerJoint: Vector2D,
		val upperJoint: Vector2D,
		val boneDir: Vector2D,
	)

	private fun buildMatches(
		trackerPosition: TrackerPosition,
		frames: List<Pair<TrackersSnapshot, HumanPoseSnapshot>>,
		upperJoint: CocoWholeBodyKeypoint,
		lowerJoint: CocoWholeBodyKeypoint,
	): List<Match> {
		val matches = mutableListOf<Match>()
		for ((trackersSnapshot, humanPoseSnapshot) in frames) {
			val tracker = trackersSnapshot.trackers[trackerPosition] ?: continue

			val r = tracker.rawTrackerToWorld

			// Tracker Y-axis points from lower joint to upper joint
			val upperJoint = humanPoseSnapshot.joints[upperJoint] ?: continue
			val lowerJoint = humanPoseSnapshot.joints[lowerJoint] ?: continue
			val boneDir = (upperJoint - lowerJoint).unit()

			matches += Match(r, lowerJoint, upperJoint, boneDir)
		}

		return matches
	}

	private val minBoneLength = 40.0
	private val minAngleDeviation = FastMath.toRadians(5.0)

	private fun filterMatches(matches: List<Match>): List<Match> {
		val filtered = mutableListOf<Match>()
		for (match in matches.asReversed()) {
			if ((match.upperJoint - match.lowerJoint).len() < minBoneLength) {
				continue
			}

			val lastMatch = filtered.lastOrNull()
			if (
				lastMatch == null ||
				lastMatch.trackerRotation.angleToR(match.trackerRotation) >= minAngleDeviation
			) {
				filtered += match
			}
		}

		return filtered
	}

	private val minMaxRotation = FastMath.toRadians(60.0)

	private fun enoughRotation(matches: List<Match>): Boolean {
		for (i in matches) {
			for (j in matches) {
				// TODO: Should look at non-yaw rotation
				if (i.trackerRotation.angleToR(j.trackerRotation) >= minMaxRotation) {
					return true
				}
			}
		}
		return false
	}

	private fun solveLM(
		trackerPosition: TrackerPosition,
		matches: List<Match>,
		camera: Camera,
		forwardPose: CaptureForwardPose.Solution,
		initialParams: DoubleArray,
	): Pair<TrackerResetOverride, Double>? {
		val n = forwardPose.trackerRotations.size + matches.size

		val costFn = MultivariateVectorFunction { p ->
			val reset = buildTrackerReset(p)

			val residual = DoubleArray(n) { 0.0 }

			var i = 0

			// Ensure that the bone is aligned to the reference direction
			for (frame in forwardPose.trackerRotations) {
				val trackerRotation = frame[trackerPosition]
				if (trackerRotation != null) {
					val trackerBone = reset.toBoneRotation(trackerRotation)
// 					residual[i++] = trackerBone.angleToR(forwardPose.reference)
					val z = trackerBone.sandwichUnitZ()
					val z2 = Vector3D(z.x, 0.0, z.z).unit()
					residual[i++] = z2.angleTo(forwardPose.reference.sandwichUnitZ())
				}
			}

			// Ensure that the after-reset tracker Y-axis is aligned to the bone
			for (match in matches) {
				val trackerBone = reset.toBoneRotation(match.trackerRotation)
				val projectedBoneDir =
					camera.project(
						trackerBone.sandwichUnitY(),
						(match.lowerJoint + match.upperJoint) * 0.5,
						1.0,
					)
				if (projectedBoneDir != null) {
					residual[i++] = projectedBoneDir.angleTo(match.boneDir)
				} else {
					residual[i++] = 1.0e6
				}
			}

			residual
		}

		val model = numericalJacobian(costFn)

		val problem = LeastSquaresBuilder()
			.start(initialParams)
			.model(model)
			.target(DoubleArray(n) { 0.0 })
			.maxEvaluations(10000)
			.maxIterations(10000)
			.checker { iter, prev, current ->
// 				LogManager.info("$iter ${prev.rms} ${current.rms}")
				abs(current.rms - prev.rms) < 1e-7
			}
			.build()

		val optimizer = LevenbergMarquardtOptimizer()

		val result: LeastSquaresOptimizer.Optimum
		try {
			result = optimizer.optimize(problem)
		} catch (e: Exception) {
			LogManager.warning("Failed to optimize tracker: $e")
			return null
		}

		val (preRot, postRot) = buildTrackerReset(result.point.toArray())

		val trackerReset = TrackerResetOverride(preRot, postRot)

		return Pair(trackerReset, result.rms)
	}

	private fun buildTrackerReset(p: DoubleArray): TrackerResetOverride {
		val override = TrackerResetOverride(p[0], QuaternionD(p[1], p[2], p[3], p[4]).unit())
		return override
	}

	private fun startingParams() = iterator {
		val count = 4
		for (y1 in 0 until count) {
			val globalYaw = 2.0 * PI / count * y1
			for (y2 in 0 until count) {
				val localYaw = 2.0 * PI / count * y2
				for (z in 0 until count) {
					val localRoll = 2.0 * PI / count * z
					val localRotation = QuaternionD.rotationAroundYAxis(localYaw) * QuaternionD.rotationAroundZAxis(localRoll)
					yield(doubleArrayOf(globalYaw, localRotation.w, localRotation.x, localRotation.y, localRotation.z))
				}
			}
		}
	}

	fun calcError(
		params: DoubleArray,
		trackerPosition: TrackerPosition,
		matches: List<Match>,
		camera: Camera,
		forwardPose: CaptureForwardPose.Solution,
	): Double {
		val reset = buildTrackerReset(params)

		var rms = 0.0

		// Ensure that the bone is aligned to the reference direction
		for (frame in forwardPose.trackerRotations) {
			val trackerRotation = frame[trackerPosition]
			if (trackerRotation != null) {
				val trackerBone = reset.toBoneRotation(trackerRotation)
				val error = trackerBone.angleToR(forwardPose.reference)
				rms += error * error
			}
		}

		// Ensure that the after-reset tracker Y-axis is aligned to the bone
		for (match in matches) {
			val trackerBone = reset.toBoneRotation(match.trackerRotation)
			val projectedBoneDir =
				camera.project(
					trackerBone.sandwichUnitY(),
					(match.lowerJoint + match.upperJoint) * 0.5,
					1.0,
				)
			if (projectedBoneDir != null) {
				val error = projectedBoneDir.angleTo(match.boneDir)
				rms += error * error
			} else {
				val error = 1.0e6
				rms += error * error
			}
		}

		return rms
	}
}
