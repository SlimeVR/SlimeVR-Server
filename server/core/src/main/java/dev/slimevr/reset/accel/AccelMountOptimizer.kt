package dev.slimevr.reset.accel

import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import org.apache.commons.math3.analysis.MultivariateFunction
import org.apache.commons.math3.optim.InitialGuess
import org.apache.commons.math3.optim.MaxEval
import org.apache.commons.math3.optim.PointValuePair
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.PowellOptimizer
import kotlin.math.abs

data class AccelFrame(
	val trackerAccel: Vector3,
	val trackerRot: Quaternion,
	val hmdAccel: Vector3,
)

data class AccelMountResult(
	val attachmentFix: Quaternion,
	val yawFix: Quaternion,
	val error: Double,
)

// Finds optimal attachment and yaw fix using Powell optimization
class AccelMountOptimizer(
	private val mountingOrientation: Quaternion = Quaternion.IDENTITY,
	private val mountRotFix: Quaternion = Quaternion.IDENTITY,
) {
	companion object {
		private const val REL_TOLERANCE = 1e-8
		private const val ABS_TOLERANCE = 1e-10
		private const val MAX_EVAL = 10000
	}

	// Computes world-space accel from tracker data given calibration params
	fun computeWorldAccel(
		trackerRot: Quaternion,
		trackerAccel: Vector3,
		attachmentFix: Quaternion,
		yawFix: Quaternion,
	): Vector3 {
		var rot = trackerRot
		rot *= attachmentFix
		rot = mountingOrientation.inv() * rot * mountingOrientation
		rot = mountRotFix.inv() * rot * mountRotFix
		rot = yawFix * rot

		val correctionInv = (attachmentFix * mountingOrientation * mountRotFix).inv()
		val fullRot = rot * correctionInv

		return fullRot.sandwich(trackerAccel)
	}

	// Sum of squared error between tracker and HMD accelerations
	fun sumAccelError(
		frames: List<AccelFrame>,
		attachmentFix: Quaternion,
		yawFix: Quaternion,
	): Double {
		var totalError = 0.0
		for (frame in frames) {
			val worldAccel = computeWorldAccel(
				frame.trackerRot,
				frame.trackerAccel,
				attachmentFix,
				yawFix,
			)
			val diff = worldAccel - frame.hmdAccel
			totalError += (diff.x * diff.x + diff.y * diff.y + diff.z * diff.z).toDouble()
		}
		return totalError
	}

	// Params: [qx, qy, qz, qw, yawVal]
	private fun paramsToQuaternions(x: DoubleArray): Pair<Quaternion, Quaternion> {
		val attachmentFix = Quaternion(
			x[0].toFloat(),
			x[1].toFloat(),
			x[2].toFloat(),
			x[3].toFloat(),
		).unit()

		val yawVal = x[4].toFloat()
		val yawFix = Quaternion(1f - abs(yawVal), 0f, yawVal, 0f).unit()

		return Pair(attachmentFix, yawFix)
	}

	private fun createObjectiveFunction(frames: List<AccelFrame>): MultivariateFunction = MultivariateFunction { x ->
		val (attachmentFix, yawFix) = paramsToQuaternions(x)
		sumAccelError(frames, attachmentFix, yawFix)
	}

	fun optimize(
		frames: List<AccelFrame>,
		initialAttachment: Quaternion = Quaternion.IDENTITY,
		initialYaw: Float = 0f,
	): AccelMountResult {
		require(frames.isNotEmpty()) { "At least one frame is required" }

		val optimizer = PowellOptimizer(REL_TOLERANCE, ABS_TOLERANCE)

		val initialGuess = doubleArrayOf(
			initialAttachment.x.toDouble(),
			initialAttachment.y.toDouble(),
			initialAttachment.z.toDouble(),
			initialAttachment.w.toDouble(),
			initialYaw.toDouble(),
		)

		val result: PointValuePair = optimizer.optimize(
			MaxEval(MAX_EVAL),
			ObjectiveFunction(createObjectiveFunction(frames)),
			GoalType.MINIMIZE,
			InitialGuess(initialGuess),
		)

		val (attachmentFix, yawFix) = paramsToQuaternions(result.point)

		return AccelMountResult(
			attachmentFix = attachmentFix,
			yawFix = yawFix,
			error = result.value,
		)
	}
}
