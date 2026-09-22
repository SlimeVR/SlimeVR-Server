package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import io.github.axisangles.ktmath.EulerAngles
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import solarxr_protocol.datatypes.BodyPart
import kotlin.math.abs

class PosteriorInputProcessor : SkeletonInputProcessor {

	private data class PosteriorMotionState(
		var pitchOffset: Float = 0f,
		var pitchVelocity: Float = 0f,
		var verticalAcceleration: Float = 0f,
	)

	private val states = mutableMapOf(
		BodyPart.LEFT_POSTERIOR to PosteriorMotionState(),
		BodyPart.RIGHT_POSTERIOR to PosteriorMotionState(),
	)

	private var lastUpdateNanos = System.nanoTime()

	companion object {
		private const val ACCELERATION_SENSITIVITY = 0.6f
		private const val SPRING_STRENGTH = 20.0f
		private const val DAMPING = 6.0f
		private const val ACCELERATION_DEADZONE = 0.10f
		private const val MAX_PITCH_OFFSET = 0.2617994f
		private const val MAX_DELTA_TIME = 0.05f
	}

	fun setVerticalAcceleration(
		bodyPart: BodyPart,
		acceleration: Float,
	) {
		val state = states[bodyPart] ?: return
		state.verticalAcceleration = acceleration
	}

	override fun process(
		mutableInputSkeleton: InputSkeleton,
		skeletonHeight: Float,
	) {
		val now = System.nanoTime()

		var deltaTime =
			(now - lastUpdateNanos).toFloat() / 1_000_000_000f

		lastUpdateNanos = now
		deltaTime = deltaTime.coerceIn(0f, MAX_DELTA_TIME)

		val hip =
			mutableInputSkeleton[BodyPart.HIP]
				?: return

		val hipRotation = hip.rotation

		for (bodyPart in arrayOf(BodyPart.LEFT_POSTERIOR, BodyPart.RIGHT_POSTERIOR)) {
			val bone = mutableInputSkeleton[bodyPart] ?: continue
			if (!bone.isRotationActive) {
				mutableInputSkeleton[bodyPart] = bone.copy(rotation = hipRotation)
				continue
			}
			val state = states.getValue(bodyPart)

			updateMotion(state, deltaTime)

			// Convert absolute posterior rotation into hip-local rotation.
			val localRotation =
				hipRotation.inv() * bone.rotation

			// Modify only the rotation relative to the hip.
			val correctedLocalRotation =
				applyPosteriorRotation(
					localRotation,
					state.pitchOffset,
				)

			// Convert back to absolute/skeleton rotation.
			val finalRotation =
				hipRotation * correctedLocalRotation

			mutableInputSkeleton[bodyPart] =
				bone.copy(rotation = finalRotation)
		}
	}

	private fun updateMotion(
		state: PosteriorMotionState,
		deltaTime: Float,
	) {
		if (deltaTime <= 0f) {
			return
		}

		var acceleration = state.verticalAcceleration

		if (abs(acceleration) < ACCELERATION_DEADZONE) {
			acceleration = 0f
		}

		state.pitchVelocity -=
			acceleration *
				ACCELERATION_SENSITIVITY *
				deltaTime

		// Damped spring pulling the offset back toward zero.
		val springAcceleration =
			(SPRING_STRENGTH * state.pitchOffset) -
				(DAMPING * state.pitchVelocity)

		state.pitchVelocity += springAcceleration * deltaTime
		state.pitchOffset += state.pitchVelocity * deltaTime

		state.pitchOffset =
			state.pitchOffset.coerceIn(
				-MAX_PITCH_OFFSET,
				MAX_PITCH_OFFSET,
			)

		// If we've basically settled, remove tiny residual motion.
		if (
			abs(state.pitchOffset) < 0.0001f &&
			abs(state.pitchVelocity) < 0.0001f
		) {
			state.pitchOffset = 0f
			state.pitchVelocity = 0f
		}
	}

	private fun applyPosteriorRotation(
		rotation: Quaternion,
		pitchOffset: Float,
	): Quaternion {
		val euler = rotation.toEulerAngles(EulerOrder.XYZ)

		return EulerAngles(
			EulerOrder.XYZ,

			// Inverted actual pitch + temporary inertial motion
			-euler.x + pitchOffset,

			euler.y,
			euler.z,
		).toQuaternion()
	}
}
