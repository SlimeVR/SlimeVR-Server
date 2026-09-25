package dev.slimevr.skeleton.inputprocessors

import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonInputProcessor
import io.github.axisangles.ktmath.EulerAngles
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart
import kotlin.math.abs
import kotlin.math.exp

private const val ACCELERATION_SENSITIVITY = 0.6f
private const val SPRING_STRENGTH = 20.0f
private const val DAMPING = 6.0f
private const val ACCELERATION_DEADZONE = 0.10f
private const val MAX_PITCH_OFFSET = 0.2617994f
private const val MAX_DELTA_TIME = 0.05f
private const val VERTICAL_ACCEL_DEADZONE = 0.025f
private const val VERTICAL_ACCEL_GAIN = 18f
private const val VERTICAL_SPRING = 34f
private const val VERTICAL_DAMPING = 9f
private const val VERTICAL_BASELINE_TIME_CONSTANT = 2.5f
private const val MAX_VERTICAL_OFFSET = 0.01f
private const val SNAP_POSITION_EPSILON = 0.0001f
private const val SNAP_VELOCITY_EPSILON = 0.001f

class PosteriorInputProcessor : SkeletonInputProcessor {

	private data class PosteriorMotionState(
		var pitchOffset: Float = 0f,
		var pitchVelocity: Float = 0f,
		var verticalPosition: Float = 0f,
		var verticalVelocity: Float = 0f,
		var verticalBaselineY: Float = 0f,
		var baselineInitialized: Boolean = false,
	)

	private val states = mutableMapOf(
		BodyPart.LEFT_POSTERIOR to PosteriorMotionState(),
		BodyPart.RIGHT_POSTERIOR to PosteriorMotionState(),
	)

	private var lastUpdateNanos = System.nanoTime()

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

			updateRotationMotion(state, deltaTime)
			val verticalOffset = updateVerticalMotion(state, bone.acceleration.y, deltaTime)

			val localRotation =
				hipRotation.inv() * bone.rotation

			val correctedLocalRotation =
				applyPosteriorRotation(
					localRotation,
					state.pitchOffset,
				)

			val finalRotation =
				hipRotation * correctedLocalRotation

			val bouncedHeadOffset = bone.headOffset + Vector3(0f, verticalOffset, 0f)

			mutableInputSkeleton[bodyPart] =
				bone.copy(
					rotation = finalRotation,
					headOffset = bouncedHeadOffset,
				)
		}
	}

	private fun updateRotationMotion(
		state: PosteriorMotionState,
		deltaTime: Float,
	) {
		if (deltaTime <= 0f) return

		var acceleration = state.pitchOffset

		if (abs(acceleration) < ACCELERATION_DEADZONE) {
			acceleration = 0f
		}

		state.pitchVelocity -=
			acceleration *
				ACCELERATION_SENSITIVITY *
				deltaTime

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

		if (
			abs(state.pitchOffset) < 0.0001f &&
			abs(state.pitchVelocity) < 0.0001f
		) {
			state.pitchOffset = 0f
			state.pitchVelocity = 0f
		}
	}

	private fun updateVerticalMotion(
		state: PosteriorMotionState,
		rawAccelY: Float,
		dt: Float,
	): Float {
		if (!state.baselineInitialized) {
			state.verticalBaselineY = rawAccelY
			state.baselineInitialized = true
		}

		val baselineAlpha = 1f - exp(-dt / VERTICAL_BASELINE_TIME_CONSTANT)
		state.verticalBaselineY += (rawAccelY - state.verticalBaselineY) * baselineAlpha

		var dynamicAccel = rawAccelY - state.verticalBaselineY
		if (abs(dynamicAccel) < VERTICAL_ACCEL_DEADZONE) dynamicAccel = 0f

		val inertialInput = -dynamicAccel * VERTICAL_ACCEL_GAIN

		val restoring = -VERTICAL_SPRING * state.verticalPosition
		val damping = -VERTICAL_DAMPING * state.verticalVelocity
		state.verticalVelocity += (inertialInput + restoring + damping) * dt
		state.verticalPosition += state.verticalVelocity * dt
		state.verticalPosition = state.verticalPosition.coerceIn(-MAX_VERTICAL_OFFSET, MAX_VERTICAL_OFFSET)

		if (
			abs(state.verticalPosition) < SNAP_POSITION_EPSILON &&
			abs(state.verticalVelocity) < SNAP_VELOCITY_EPSILON
		) {
			state.verticalPosition = 0f
			state.verticalVelocity = 0f
		}

		return state.verticalPosition
	}

	private fun applyPosteriorRotation(
		rotation: Quaternion,
		pitchOffset: Float,
	): Quaternion {
		val euler = rotation.toEulerAngles(EulerOrder.XYZ)

		return EulerAngles(
			EulerOrder.XYZ,

			-euler.x + pitchOffset,

			euler.y,
			euler.z,
		).toQuaternion()
	}
}
