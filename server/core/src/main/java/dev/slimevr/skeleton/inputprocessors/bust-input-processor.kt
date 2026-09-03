package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import io.github.axisangles.ktmath.EulerAngles
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import solarxr_protocol.datatypes.BodyPart
import kotlin.math.abs

/**
 * Handles bust-specific rotation behavior:
 *
 * - Inverts the normal bust pitch.
 * - Adds temporary pitch motion from vertical acceleration.
 * - Uses a damped spring so acceleration-induced motion settles
 *   back to the actual tracked rotation.
 */
class BustInputProcessor : SkeletonInputProcessor {

	private data class BustMotionState(
		var pitchOffset: Float = 0f,
		var pitchVelocity: Float = 0f,
		var verticalAcceleration: Float = 0f,
	)

	private val states = mutableMapOf(
		BodyPart.LEFT_BUST to BustMotionState(),
		BodyPart.RIGHT_BUST to BustMotionState(),
	)

	private var lastUpdateNanos = System.nanoTime()

	companion object {
		/**
		 * How strongly vertical acceleration affects pitch.
		 */
		private const val ACCELERATION_SENSITIVITY = 0.12f

		/**
		 * How strongly the temporary pitch offset is pulled back toward zero.
		 *
		 * Higher = snaps back faster.
		 */
		private const val SPRING_STRENGTH = 20.0f

		/**
		 * Resistance to oscillation.
		 *
		 * Higher = less bouncing.
		 * Lower = more secondary motion / jiggle.
		 */
		private const val DAMPING = 6.0f

		/**
		 * Ignore tiny accelerometer fluctuations.
		 */
		private const val ACCELERATION_DEADZONE = 0.10f

		/**
		 * Maximum temporary pitch offset, in radians.
		 *
		 * ~15 degrees.
		 */
		private const val MAX_PITCH_OFFSET = 0.2617994f

		/**
		 * Avoid giant simulation steps after pauses/debugger stops.
		 */
		private const val MAX_DELTA_TIME = 0.05f
	}

	/**
	 * Feed vertical/linear acceleration for a bust tracker into this processor.
	 *
	 * Positive/negative direction may need to be flipped depending on
	 * SlimeVR's acceleration coordinate system.
	 */
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

		val chest =
			mutableInputSkeleton[BodyPart.UPPER_CHEST]
				?: return

		val chestRotation = chest.rotation

		for (bodyPart in arrayOf(BodyPart.LEFT_BUST, BodyPart.RIGHT_BUST)) {
			val bone = mutableInputSkeleton[bodyPart] ?: continue
			val state = states.getValue(bodyPart)

			updateMotion(state, deltaTime)

			// Convert absolute bust rotation into chest-local rotation.
			val localRotation =
				chestRotation.inverse() * bone.rotation

			// Modify only the rotation relative to the chest.
			val correctedLocalRotation =
				applyBustRotation(
					localRotation,
					state.pitchOffset,
				)

			// Convert back to absolute/skeleton rotation.
			val finalRotation =
				chestRotation * correctedLocalRotation

			mutableInputSkeleton[bodyPart] =
				bone.copy(rotation = finalRotation)
		}
	}

	/**
	 * Updates the temporary acceleration-driven pitch offset.
	 *
	 * Acceleration pushes the velocity, while a damped spring pulls
	 * the pitch back toward the actual tracker rotation.
	 */
	private fun updateMotion(
		state: BustMotionState,
		deltaTime: Float,
	) {
		if (deltaTime <= 0f) {
			return
		}

		var acceleration = state.verticalAcceleration

		if (abs(acceleration) < ACCELERATION_DEADZONE) {
			acceleration = 0f
		}

		// Acceleration creates temporary pitch velocity.
		//
		// Flip this sign if upward acceleration produces motion
		// in the wrong direction:
		//
		// state.pitchVelocity -= ...
		state.pitchVelocity +=
			acceleration *
				ACCELERATION_SENSITIVITY *
				deltaTime

		// Damped spring pulling the offset back toward zero.
		val springAcceleration =
			(-SPRING_STRENGTH * state.pitchOffset) -
				(DAMPING * state.pitchVelocity)

		state.pitchVelocity += springAcceleration * deltaTime
		state.pitchOffset += state.pitchVelocity * deltaTime

		state.pitchOffset =
			state.pitchOffset.coerceIn(
				-MAX_PITCH_OFFSET,
				MAX_PITCH_OFFSET,
			)

		// If we've basically settled, kill tiny residual motion.
		if (
			abs(state.pitchOffset) < 0.0001f &&
			abs(state.pitchVelocity) < 0.0001f
		) {
			state.pitchOffset = 0f
			state.pitchVelocity = 0f
		}
	}

	/**
	 * Inverts the normal pitch and adds the temporary
	 * acceleration-induced pitch offset.
	 */
	private fun applyBustRotation(
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
