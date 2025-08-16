package dev.slimevr.tracking.trackers

import com.jme3.system.NanoTimer
import io.eiren.math.FloatMath
import io.github.axisangles.ktmath.Vector3

class AccelAccumulator {
	var acceleration = Vector3.NULL
		private set
	var velocity = Vector3.NULL
		private set
	var offset = Vector3.NULL
		private set

	var dir = Vector3.NULL
		private set

	val timer = NanoTimer()

	fun dataTick(acceleration: Vector3) {
		timer.update()
		val deltaTime = timer.timePerFrame

		this.acceleration = acceleration
		offset += (velocity * deltaTime) + ((acceleration * deltaTime * deltaTime) / 2f)
		velocity += acceleration * deltaTime

		// Collect velocity direction over time
		dir += velocity.unit()
	}
}
