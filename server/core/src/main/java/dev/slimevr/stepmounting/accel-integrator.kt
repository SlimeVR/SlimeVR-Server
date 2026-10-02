package dev.slimevr.stepmounting

import io.github.axisangles.ktmath.Vector3

class AccelerationIntegrator {
	var acceleration = Vector3.ZERO
		private set
	var velocity = Vector3.ZERO
		private set
	var offset = Vector3.ZERO
		private set

	fun integrate(acceleration: Vector3, time: Float) {
		this.acceleration = acceleration
		offset += (velocity * time) + ((acceleration * time * time) / 2f)
		velocity += acceleration * time
	}
}
