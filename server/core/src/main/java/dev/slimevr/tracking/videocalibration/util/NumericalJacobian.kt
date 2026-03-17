package dev.slimevr.tracking.videocalibration.util

import org.apache.commons.math3.analysis.MultivariateVectorFunction
import org.apache.commons.math3.fitting.leastsquares.MultivariateJacobianFunction
import org.apache.commons.math3.linear.Array2DRowRealMatrix
import org.apache.commons.math3.linear.ArrayRealVector
import org.apache.commons.math3.util.Pair

fun numericalJacobian(
	f: MultivariateVectorFunction,
	eps: Double = 1e-6,
): MultivariateJacobianFunction = MultivariateJacobianFunction { point ->
	val x = point.toArray()
	val y = f.value(x)

	val m = y.size
	val n = x.size

	val jacobian = Array2DRowRealMatrix(m, n)

	for (j in 0 until n) {
		val x2 = x.clone()
		x2[j] += eps

		val y2 = f.value(x2)

		for (i in 0 until m) {
			jacobian.setEntry(i, j, (y2[i] - y[i]) / eps)
		}
	}

	Pair(ArrayRealVector(y), jacobian)
}
