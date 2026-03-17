package dev.slimevr.tracking.videocalibration.steps

import dev.slimevr.tracking.trackers.TrackerPosition
import dev.slimevr.tracking.videocalibration.data.Camera
import dev.slimevr.tracking.videocalibration.data.CameraExtrinsic
import dev.slimevr.tracking.videocalibration.data.CameraIntrinsic
import dev.slimevr.tracking.videocalibration.data.CocoWholeBodyKeypoint
import dev.slimevr.tracking.videocalibration.snapshots.HumanPoseSnapshot
import dev.slimevr.tracking.videocalibration.snapshots.TrackersSnapshot
import dev.slimevr.tracking.videocalibration.sources.SnapshotsDatabase
import dev.slimevr.tracking.videocalibration.util.DebugOutput
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
import org.slf4j.ext.LoggerWrapper
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class SolveCamera(
	private val debugOutput: DebugOutput,
) {

	class Solution(
		val camera: Camera,
		val cameraDelay: Duration,
	)

	private val minDistanceInWorld = 0.05
	private val minMatches = 100

	fun solve(database: SnapshotsDatabase): Solution? {
		val zeroFrames = database.matchRecent(Duration.ZERO)
		val zeroMatches = mutableListOf<Correspondence>()
		zeroMatches += extractPath(TrackerPosition.RIGHT_HAND, CocoWholeBodyKeypoint.RIGHT_WRIST, zeroFrames)
		if (zeroMatches.size < minMatches) {
			return null
		}

		var centroid = Vector3D.NULL
		for (match in zeroMatches) {
			centroid += match.trackerOriginInWorld
		}
		centroid /= zeroMatches.size.toDouble()
		LogManager.debug("Tracker centroid: $centroid")

		var bestRMS = Double.POSITIVE_INFINITY
		var bestCameraDelay: Duration? = null
		var bestCamera: Camera? = null
		var bestWristInTracker: Vector3D? = null

		for (cameraDelayInMs in -300..300 step 20) {
			val cameraDelay = cameraDelayInMs.milliseconds
			val frames = database.matchRecent(cameraDelay)
			if (frames.isEmpty()) {
				continue
			}

			val initialCamera = frames.first().second.camera

			val matches = mutableListOf<Correspondence>()
// 		matches += extractPath(TrackerPosition.LEFT_HAND, CocoWholeBodyKeypoint.LEFT_WRIST, frames)
			matches += extractPath(TrackerPosition.RIGHT_HAND, CocoWholeBodyKeypoint.RIGHT_WRIST, frames)

			// TODO: Check coverage across image

// 			if (matches.size < minMatches) {
// 				continue
// 			}

// 			LogManager.debug("Solving camera with ${matches.size} correspondences...")
			for (angleDeg in 0..360 step 20) {
				val (initialExtraYaw, initialCameraOriginInWorld) = placeCamera(initialCamera, FastMath.toRadians(angleDeg.toDouble()), 3.0)

				val (camera, wristInTracker, rms) = solveCamera(initialCamera, initialExtraYaw, initialCameraOriginInWorld, matches) ?: continue

// 				LogManager.debug("Solved camera $angleDeg deg: rms=$rms camera=$camera")

				if (rms >= bestRMS) {
					continue
				}

				val projectedMatches =
					matches.map { match ->
						projectWrist(
							camera,
							match.trackerToWorld,
							match.trackerOriginInWorld,
							wristInTracker,
						) to match.joint
					}

				debugOutput.saveHandToControllerMatches(
					projectedMatches,
					initialCamera.imageSize,
				)

				bestRMS = rms
				bestCameraDelay = cameraDelay
				bestCamera = camera
				bestWristInTracker = wristInTracker
			}
		}

		if (bestCamera == null || bestCameraDelay == null) {
			return null
		}

		LogManager.info("Solved camera: $bestCamera")
		LogManager.info("  delay=$bestCameraDelay")
		LogManager.info("  wristInTracker=$bestWristInTracker")

		return Solution(bestCamera, bestCameraDelay)
	}

	private fun projectWrist(camera: Camera, trackerRotation: QuaternionD, trackerOriginInWorld: Vector3D, wristInTracker: Vector3D): Vector2D? {
		// TODO: Select the right constant
		val wrist = trackerOriginInWorld + trackerRotation.sandwich(wristInTracker)
		return camera.project(wrist)
	}

	private data class Correspondence(
		val trackerToWorld: QuaternionD,
		val trackerOriginInWorld: Vector3D,
		val joint: Vector2D,
	)

	private fun extractPath(
		trackerPosition: TrackerPosition,
		jointPosition: CocoWholeBodyKeypoint,
		frames: List<Pair<TrackersSnapshot, HumanPoseSnapshot>>,
	): List<Correspondence> {
		val matches = mutableListOf<Correspondence>()

		for ((trackersFrame, humanFrame) in frames.reversed()) {
			val tracker = trackersFrame.trackers[trackerPosition]
			val joint = humanFrame.joints[jointPosition]
			if (tracker == null || joint == null) {
				continue
			}

			val originInWorld = tracker.trackerOriginInWorld
			if (originInWorld == null) {
				continue
			}

			val lastMatch = matches.lastOrNull()
			if (lastMatch == null || (originInWorld - lastMatch.trackerOriginInWorld).len() > minDistanceInWorld) {
				matches += Correspondence(tracker.rawTrackerToWorld, originInWorld, joint)
			}
		}

		return matches
	}

	private fun placeCamera(
		initialCamera: Camera,
		angleRad: Double,
		distanceFromOrigin: Double,
	): Pair<Double, Vector3D> {
		val cameraOriginInWorld = Vector3D(cos(angleRad), 0.0, sin(angleRad)) * distanceFromOrigin

		val cameraZ = initialCamera.extrinsic.cameraToWorld.sandwichUnitZ()
		val cameraYaw = atan2(cameraZ.z, cameraZ.x)
		val targetYaw = atan2(-cameraOriginInWorld.z, -cameraOriginInWorld.x)
		val extraYaw = -(targetYaw - cameraYaw) // In an RHS coordinate system, we have to rotate in the opposite direction

		return Pair(extraYaw, cameraOriginInWorld)
	}

	private fun solveCamera(
		initialCamera: Camera,
		initialExtraYaw: Double,
		initialCameraOriginInWorld: Vector3D,
		matches: List<Correspondence>,
	): Triple<Camera, Vector3D, Double>? {
		val n = matches.size * 2

		val costFn = MultivariateVectorFunction { params ->
			val camera = buildCamera(params, initialCamera)
			val wristInTracker = Vector3D(params[4], params[5], params[6])

			val residuals = DoubleArray(n) { 0.0 }
			for ((i, match) in matches.withIndex()) {
				val projected = projectWrist(camera, match.trackerToWorld, match.trackerOriginInWorld, wristInTracker)
				if (projected != null) {
					val dx = projected.x - match.joint.x
					val dy = projected.y - match.joint.y
					residuals[i * 2 + 0] = dx
					residuals[i * 2 + 1] = dy
				} else {
					residuals[i * 2 + 0] = 1.0e6
					residuals[i * 2 + 1] = 1.0e6
				}
			}

			residuals
		}

		val model = numericalJacobian(costFn)

		// TODO: Should initialize relative to HEAD
		val initial = doubleArrayOf(
			initialExtraYaw,
			initialCameraOriginInWorld.x,
			initialCameraOriginInWorld.y,
			initialCameraOriginInWorld.z,
			0.0, // Tracker to wrist x
			0.0, // Tracker to wrist y
			0.0, // Tracker to wrist z
		)

		val problem = LeastSquaresBuilder()
			.start(initial)
			.model(model)
			.target(DoubleArray(n) { 0.0 })
			.maxEvaluations(10000)
			.maxIterations(10000)
			.build()

		val optimizer = LevenbergMarquardtOptimizer()

		val result: LeastSquaresOptimizer.Optimum
		try {
			result = optimizer.optimize(problem)
		} catch (e: Exception) {
			return null
		}

		val bestCamera = buildCamera(result.point.toArray(), initialCamera)
		val wristInTracker = Vector3D(result.point.getEntry(4), result.point.getEntry(5), result.point.getEntry(6))

		return Triple(bestCamera, wristInTracker, result.rms)
	}

	private fun buildCamera(params: DoubleArray, initialCamera: Camera): Camera {
		val cameraToWorld = QuaternionD.rotationAroundYAxis(params[0]) * initialCamera.extrinsic.cameraToWorld
		val cameraOriginInWorld = Vector3D(params[1], params[2], params[3])

		return Camera(
			CameraExtrinsic.fromCameraPose(cameraToWorld, cameraOriginInWorld),
			CameraIntrinsic(initialCamera.intrinsic.fx, initialCamera.intrinsic.fy, initialCamera.intrinsic.tx, initialCamera.intrinsic.ty),
			initialCamera.imageSize,
		)
	}
}
