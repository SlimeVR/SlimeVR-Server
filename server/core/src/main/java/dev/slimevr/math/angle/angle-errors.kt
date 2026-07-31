package dev.slimevr.math.angle

import kotlin.math.sqrt

class AngleErrors {
    private var sumSqrErrors = 0.0f

    fun add(error: Angle) {
        sumSqrErrors += error.toRad() * error.toRad()
    }

    fun toL2Norm() = Angle.ofRad(sqrt(sumSqrErrors))
}