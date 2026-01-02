package dev.slimevr.util

import com.jme3.system.NanoTimer
import io.github.axisangles.ktmath.Vector3

class AccelAccumulator {
	var acceleration = Vector3.NULL
		private set
	var velocity = Vector3.NULL
		private set
	var offset = Vector3.NULL
		private set

	val timer = NanoTimer()

	fun dataTick(acceleration: Vector3, time: Float? = null) {
		timer.update()
		val deltaTime = time ?: timer.timePerFrame

		this.acceleration = acceleration
		offset += (velocity * deltaTime) + ((acceleration * deltaTime * deltaTime) / 2f)
		velocity += acceleration * deltaTime
	}
}
