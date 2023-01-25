package io.github.axisangles.ktmath

import kotlin.math.*
import kotlin.system.measureTimeMillis

var randSeed = 0
fun randInt(): Int {
    randSeed = (1103515245*randSeed + 12345).mod(2147483648).toInt()
    return randSeed
}

fun randFloat(): Float {
    return randInt().toFloat()/2147483648f
}

fun randGaussian(): Float {
    var thing = 1f - randFloat()
    while (thing == 0f) {
        // no 0s allowed
        thing = 1f - randFloat()
    }
    return sqrt(-2f*ln(thing))*cos(PI.toFloat()*randFloat())
}

fun randMatrix(): Matrix3 {
    return Matrix3(
        randGaussian(), randGaussian(), randGaussian(),
        randGaussian(), randGaussian(), randGaussian(),
        randGaussian(), randGaussian(), randGaussian()
    )
}

fun randQuaternion(): Quaternion {
    return Quaternion(randGaussian(), randGaussian(), randGaussian(), randGaussian())
}

fun randRotMatrix(): Matrix3 {
    return randQuaternion().toMatrix()
}

fun randVector(): Vector3 {
    return Vector3(randGaussian(), randGaussian(), randGaussian())
}

fun testEulerMatrix(order: EulerOrder, M: Matrix3, exception: String) {
    // We convert to euler angles and back and see if they are reasonably similar
    val N = M.toEulerAngles(order).toMatrix()
    if ((N - M).norm() > 1e-6) {
        println("norm error: " + (N - M).norm().toString())
        throw Exception(exception)
    }
}

fun testEulerConversion(order: EulerOrder, exception: String) {
    for (i in 1..1000) {
        testEulerMatrix(order, randRotMatrix(), exception)
    }
}

fun testMatrixOrthonormalize() {
    for (i in 1..1000) {
        val M = randMatrix()

        val N = M.invTranspose().orthonormalize()
        val O = M.orthonormalize()
        if ((N - O).norm() > 1e-5) {
            println("norm error: " + (N - O).norm().toString())
            throw Exception("Matrix orthonormalization accuracy test failed")
        }
    }
}

fun testQuatMatrixConversion() {
    for (i in 1..1000) {
        val M = randRotMatrix()
        val N = (randGaussian()*M.toQuaternion()).toMatrix()
        if ((N - M).norm() > 1e-6) {
            println("norm error: " + (N - M).norm().toString())
            throw Exception("Quaternion Matrix conversion accuracy test failed")
        }
    }
}

fun relError(a: Matrix3, b: Matrix3): Float {
    val combinedLen = sqrt((a.normSq() + b.normSq())/2f)
    if (combinedLen == 0f) return 0f

    return (b - a).norm()/combinedLen
}

fun relError(a: Vector3, b: Vector3): Float {
    val combinedLen = sqrt((a.lenSq() + b.lenSq())/2f)
    if (combinedLen == 0f) return 0f

    return (b - a).len()/combinedLen
}

fun relError(a: Quaternion, b: Quaternion): Float {
    val combinedLen = sqrt((a.lenSq() + b.lenSq())/2f)
    if (combinedLen == 0f) return 0f

    return (b - a).len()/combinedLen
}

fun checkError(eta: Float, a: Matrix3, b: Matrix3): Boolean {
    return (b - a).normSq() <= eta*eta*(a.normSq() + b.normSq())
}

fun checkError(eta: Float, a: Quaternion, b: Quaternion): Boolean {
    return (b - a).lenSq() <= eta*eta*(a.lenSq() + b.lenSq())
}

fun checkError(eta: Float, a: Vector3, b: Vector3): Boolean {
    return (b - a).lenSq() <= eta*eta*(a.lenSq() + b.lenSq())
}

fun checkError(eta: Float, A: Quaternion): Boolean {
    return A.lenSq() <= eta*eta
}

fun testQuaternionInv() {
    for (i in 1..1000) {
        val Q = randQuaternion()

        if (relError(Q*Q.inv(), Quaternion.ONE) > 1e-6f)
            throw Exception("Quaternion inv accuracy test failed")
    }
}

fun testQuaternionDiv() {
    for (i in 1..1000) {
        val Q = randQuaternion()

        if (!checkError(1e-6f, Q/Q, Quaternion.ONE))
            throw Exception("Quaternion div accuracy test failed")
        if (!checkError(1e-6f, 2f/Q, 2f*Q.inv()))
            throw Exception("Float/Quaternion accuracy test failed")
        if (!checkError(1e-6f, Q/2f, 0.5f*Q))
            throw Exception("Quaternion/Float accuracy test failed")
    }
}

// 19 binary digits of accuracy
fun testQuaternionPow() {
    for (i in 1..1000) {
        val Q = randQuaternion()

        if (!checkError(2e-6f, Q.pow(-1f), Q.inv()))
            throw Exception("Quaternion pow -1 accuracy test failed")
        if (!checkError(2e-6f, Q.pow(0f), Quaternion.ONE))
            throw Exception("Quaternion pow 0 accuracy test failed")
        if (!checkError(2e-6f, Q.pow(1f), Q))
            throw Exception("Quaternion pow 1 accuracy test failed")
        if (!checkError(2e-6f, Q.pow(2f), Q*Q))
            throw Exception("Quaternion pow 2 accuracy test failed")
    }
}

fun testQuaternionSandwich() {
    for (i in 1..1000) {
        val Q = randQuaternion()
        val v = randVector()

        if (!checkError(5e-7f, Q.toMatrix()*v, Q.sandwich(v)))
            throw Exception("Quaternion sandwich accuracy test failed")
    }
}

// projection and alignment are expected to be less accurate in some extreme cases
// so we expect to see some cases in which half the bits are lost
fun testQuaternionProjectAlign() {
    for (i in 1..1000) {
        val Q = randQuaternion()
        val v = randVector()

        if (!checkError(1e-4f, Q.align(v, v), Q.project(v))) {
            println(Q.align(v, v) - Q.project(v))
            println(Q.align(v, v))
            println(Q.project(v))
            println(Q)
            throw Exception("Quaternion project/align accuracy test failed")
        }
    }
}

fun testQuaternionRotationVector() {
    for (i in 1..1000) {
        val Q = randQuaternion().unit()
        val P = Quaternion.fromRotationVector(Q.toRotationVector())

        if (!checkError(5e-7f, Q, P)) {
            throw Exception("Quaternion toRotationVector fromRotationVector accuracy test failed")
        }
    }
}

fun testQuaternionEulerAngles(order: EulerOrder, exception: String) {
    for (i in 1..1000) {
        val Q = randQuaternion().unit()
        val P = Q.toEulerAngles(order).toQuaternion().twinNearest(Q)

        if (!checkError(2e-7f, Q, P)) {
            println(relError(Q, P))
            throw Exception(exception)
        }
    }
}

fun testEulerSingularity(order: EulerOrder, M: Matrix3, exception: String) {
    for (i in 1..1000) {
        val R = 1e-6f*randMatrix()
        val S = M + R
        if (S.det() <= 0f) return

        val error = (S.toEulerAnglesAssumingOrthonormal(order).toMatrix() - S).norm()
        if (error > 2f*R.norm() + 1e-6f) {
            throw Exception(exception)
        }
    }
}

fun testEulerConversions(order: EulerOrder, exception: String) {
    for (i in 1..1000) {
        val e = EulerAngles(order, 6.28318f*randFloat(), 6.28318f*randFloat(), 6.28318f*randFloat())
        val N = e.toMatrix()
        val M = e.toQuaternion().toMatrix()
        if ((N - M).norm() > 1e-6) {
            throw Exception(exception)
        }
    }
}


fun main() {
    val X90 = Matrix3(
        1f, 0f, 0f,
        0f, 0f, -1f,
        0f, 1f, 0f
    )
    val Y90 = Matrix3(
        0f, 0f, 1f,
        0f, 1f, 0f,
        -1f, 0f, 0f
    )
    val Z90 = Matrix3(
        0f, -1f, 0f,
        1f, 0f, 0f,
        0f, 0f, 1f
    )

    testMatrixOrthonormalize()
    testQuatMatrixConversion()
    testQuaternionEulerAngles(EulerOrder.XYZ, "Quaternion EulerAnglesXYZ accuracy test failed")
    testQuaternionEulerAngles(EulerOrder.YZX, "Quaternion EulerAnglesYZX accuracy test failed")
    testQuaternionEulerAngles(EulerOrder.ZXY, "Quaternion EulerAnglesZXY accuracy test failed")
    testQuaternionEulerAngles(EulerOrder.ZYX, "Quaternion EulerAnglesZYX accuracy test failed")
    testQuaternionEulerAngles(EulerOrder.YXZ, "Quaternion EulerAnglesYXZ accuracy test failed")
    testQuaternionEulerAngles(EulerOrder.XZY, "Quaternion EulerAnglesXZY accuracy test failed")

    testQuaternionInv()
    testQuaternionDiv()
    testQuaternionPow()
    testQuaternionSandwich()
    testQuaternionProjectAlign()
    testQuaternionRotationVector()

    println(Matrix3.IDENTITY.average(Y90))

    testEulerConversions(EulerOrder.XYZ, "fromEulerAnglesXYZ Quaternion or Matrix3 accuracy test failed")
    testEulerConversions(EulerOrder.YZX, "fromEulerAnglesYZX Quaternion or Matrix3 accuracy test failed")
    testEulerConversions(EulerOrder.ZXY, "fromEulerAnglesZXY Quaternion or Matrix3 accuracy test failed")
    testEulerConversions(EulerOrder.ZYX, "fromEulerAnglesZYX Quaternion or Matrix3 accuracy test failed")
    testEulerConversions(EulerOrder.YXZ, "fromEulerAnglesYXZ Quaternion or Matrix3 accuracy test failed")
    testEulerConversions(EulerOrder.XZY, "fromEulerAnglesXZY Quaternion or Matrix3 accuracy test failed")

    // EULER ANGLE TESTS
    testEulerConversion(EulerOrder.XYZ, "toEulerAnglesXYZ accuracy test failed")
    testEulerConversion(EulerOrder.YZX, "toEulerAnglesYZX accuracy test failed")
    testEulerConversion(EulerOrder.ZXY, "toEulerAnglesZXY accuracy test failed")
    testEulerConversion(EulerOrder.ZYX, "toEulerAnglesZYX accuracy test failed")
    testEulerConversion(EulerOrder.YXZ, "toEulerAnglesYXZ accuracy test failed")
    testEulerConversion(EulerOrder.XZY, "toEulerAnglesXZY accuracy test failed")

    // test robustness to noise
    testEulerSingularity(EulerOrder.XYZ, Y90, "toEulerAnglesXYZ singularity accuracy test failed")
    testEulerSingularity(EulerOrder.YZX, Z90, "toEulerAnglesYZX singularity accuracy test failed")
    testEulerSingularity(EulerOrder.ZXY, X90, "toEulerAnglesZXY singularity accuracy test failed")
    testEulerSingularity(EulerOrder.ZYX, Y90, "toEulerAnglesZYX singularity accuracy test failed")
    testEulerSingularity(EulerOrder.YXZ, X90, "toEulerAnglesYXZ singularity accuracy test failed")
    testEulerSingularity(EulerOrder.XZY, Z90, "toEulerAnglesXZY singularity accuracy test failed")



    // speed test a linear (align) method against some standard math functions
//    var x = Quaternion(1f, 2f, 3f, 4f)
//
//    var dtAlignTotal: Long = 0
//    var dtOrthonormalizeTotal: Long = 0
//    var dtAtan2Total: Long = 0
//    var dtAsinTotal: Long = 0
//
//    for (i in 1..10) {
//        val dtAlign = measureTimeMillis {
//            for (i in 1..1_000_000) {
//                val u = Vector3(1f, 0f, 0f)
//                val v = Vector3(0f, 1f, 0f)
//                // to make sure it is not optimized away
//                x = x.align(u, v)
//                //            internally, x.align is:
//                //            val U = Quaternion(0f, u)
//                //            val V = Quaternion(0f, v)
//                //            x = (V*x/U + (V/U).len()*x)/2f
//            }
//        }
//
//        var y = x.toMatrix()
//        val dtOrthonormalize = measureTimeMillis {
//            for (i in 1..1_000_000) {
//                // to make sure it is not optimized away
//                y = y.orthonormalize()
//                //            internally, x.align is:
//                //            val U = Quaternion(0f, u)
//                //            val V = Quaternion(0f, v)
//                //            x = (V*x/U + (V/U).len()*x)/2f
//            }
//        }
//
//        var z = 0f;
//        val dtAtan2 = measureTimeMillis {
//            for (i in 1..1_000_000) {
//                z+= atan2(i.toFloat(), i.toFloat()) // 45 degrees
//            }
//        }
//
//        var w = 0f;
//        val dtAsin = measureTimeMillis {
//            for (i in 1..1_000_000) {
//                w+= asin(i.toFloat()*0.7071f/i.toFloat()) // 45 degrees
//            }
//        }
//
//        dtAlignTotal += dtAlign
//        dtOrthonormalizeTotal += dtOrthonormalize
//        dtAtan2Total += dtAtan2
//        dtAsinTotal += dtAsin
//    }
//
//    println(x)
//
//    println(dtAlignTotal) // 213
//    println(dtOrthonormalizeTotal) // 244
//    println(dtAtan2Total) // 610
//    println(dtAsinTotal) // 3558


//    var x = Quaternion(2f, 1f, 4f, 3f)
//    val dtPow = measureTimeMillis {
//        for (i in 1..10_000_000) {
//            x = x.pow(1f)
//        }
//    }
//
//    println(dtPow)
}