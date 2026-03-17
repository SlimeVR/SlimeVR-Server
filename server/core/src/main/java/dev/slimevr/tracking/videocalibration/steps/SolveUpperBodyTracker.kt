package dev.slimevr.tracking.videocalibration.steps

import dev.slimevr.tracking.trackers.TrackerPosition
import dev.slimevr.tracking.videocalibration.data.TrackerResetOverride
import dev.slimevr.tracking.videocalibration.util.numericalJacobian
import io.eiren.util.logging.LogManager
import io.github.axisangles.ktmath.QuaternionD
import org.apache.commons.math3.analysis.MultivariateVectorFunction
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresBuilder
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer
import org.apache.commons.math3.util.FastMath
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.max

class SolveUpperBodyTracker {

	fun solve(
		trackerPosition: TrackerPosition,
		forwardPose: CaptureForwardPose.Solution,
		bentOverPose: CaptureBentOverPose.Solution,
	): TrackerResetOverride? {
		val n = forwardPose.trackerRotations.size + bentOverPose.trackerRotations.size * 2

		val costFn = MultivariateVectorFunction { p ->
			val reset = buildTrackerReset(p)

			val residual = DoubleArray(n) { 0.0 }

			var i = 0
			for (frame in forwardPose.trackerRotations) {
				val trackerRotation = frame[trackerPosition]
				if (trackerRotation != null) {
					val trackerBone = reset.toBoneRotation(trackerRotation)
					residual[i++] = trackerBone.angleToR(forwardPose.reference)
				}
			}
			for (frame in bentOverPose.trackerRotations) {
				val trackerRotation = frame[trackerPosition]
				if (trackerRotation != null) {
					val trackerBone = reset.toBoneRotation(trackerRotation)
					residual[i++] = trackerBone.sandwichUnitX().angleTo(forwardPose.reference.sandwichUnitX())

					// y-axis should be facing forward, instead of backwards
					// Rotate forwardPose forward instead of using bentOverPose because the standing forwardPose is usually more balanced
					val bentOverYAxis = (forwardPose.reference * QuaternionD.rotationAroundXAxis(-PI / 4.0)).sandwichUnitY()
					val bentOverAngle = trackerBone.sandwichUnitY().angleTo(bentOverYAxis)
					residual[i++] = max(bentOverAngle - FastMath.toRadians(30.0), 0.0)
				}
			}

			residual
		}

		val model = numericalJacobian(costFn)

		var bestRMS = Double.POSITIVE_INFINITY
		var bestInitialParams: DoubleArray? = null
		for (initialParams in startingParams()) {
			val rms = calcError(initialParams, trackerPosition, forwardPose, bentOverPose)
			if (rms < bestRMS) {
				bestRMS = rms
				bestInitialParams = initialParams
			}
		}

		if (bestInitialParams == null) {
			LogManager.warning("Failed to find best initial params")
			return null
		}

		LogManager.info("Best initial params: ${bestInitialParams.toList()} rms=$bestRMS")

		val problem = LeastSquaresBuilder()
			.start(bestInitialParams)
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

		val trackerReset = buildTrackerReset(result.point.toArray())

		LogManager.info("Found tracker resets for $trackerPosition: $trackerReset")

		return trackerReset
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

	private fun calcError(
		params: DoubleArray,
		trackerPosition: TrackerPosition,
		forwardPose: CaptureForwardPose.Solution,
		bentOverPose: CaptureBentOverPose.Solution,
	): Double {
		val reset = buildTrackerReset(params)

		var rms = 0.0
		for (frame in forwardPose.trackerRotations) {
			val trackerRotation = frame[trackerPosition]
			if (trackerRotation != null) {
				val trackerBone = reset.toBoneRotation(trackerRotation)
				val error = trackerBone.angleToR(forwardPose.reference)
				rms += error * error
			}
		}
		for (frame in bentOverPose.trackerRotations) {
			val trackerRotation = frame[trackerPosition]
			if (trackerRotation != null) {
				val trackerBone = reset.toBoneRotation(trackerRotation)
				val error1 = trackerBone.sandwichUnitX().angleTo(forwardPose.reference.sandwichUnitX())

				// y-axis should be facing forward, instead of backwards
				// Rotate forwardPose forward instead of using bentOverPose because the standing forwardPose is usually more balanced
				val bentOverYAxis = (forwardPose.reference * QuaternionD.rotationAroundXAxis(-PI / 4.0)).sandwichUnitY()
				val bentOverAngle = trackerBone.sandwichUnitY().angleTo(bentOverYAxis)
				val error2 = max(bentOverAngle - FastMath.toRadians(30.0), 0.0)

				rms += error1 * error1 + error2 * error2
			}
		}

		return rms
	}
}
