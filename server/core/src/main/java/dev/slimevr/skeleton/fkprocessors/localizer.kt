package dev.slimevr.skeleton.fkprocessors

import dev.slimevr.config.Settings
import dev.slimevr.skeleton.BoneState
import dev.slimevr.skeleton.ComputedSkeleton
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonFkProcessor
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart

enum class MovementStates {
	LEFT_LOCKED,
	RIGHT_LOCKED,
	NONE_LOCKED,
	FOLLOW_FOOT,
	FOLLOW_COM,
	FOLLOW_SITTING,
}

private const val WARMUP_FRAMES = 100 // ~0.1 seconds
private const val MAX_FOOT_PERCENTAGE = 50.0f
private const val MAX_ACCEL_UP = 2.0f
private const val SITTING_KNEE_THRESHOLD = 1.1f
private const val SITTING_EARLY = 1000f
private const val VELOCITY_SAMPLE_RATE: Long = 100000000 // 10ms
private const val CONSTANT_ACCELERATION: Float = 2.0f

fun getPlantedFoot(fk: ComputedSkeleton): MovementStates {
	val leftFoot = fk[BodyPart.LEFT_FOOT] ?: return MovementStates.NONE_LOCKED
	val rightFoot = fk[BodyPart.RIGHT_FOOT] ?: return MovementStates.NONE_LOCKED

	return if (leftFoot.headPosition.y < rightFoot.headPosition.y) {
		MovementStates.LEFT_LOCKED
	} else {
		MovementStates.RIGHT_LOCKED
	}
}

// check if the foot that is planted is actually planted
private fun getWorldReference(fk: ComputedSkeleton): MovementStates = if (isUserSitting(fk)) {
	// User is sitting down
	MovementStates.FOLLOW_SITTING
} else if (!isFootOnGround(fk)) {
	// if the foot is not on the ground, use the COM
	MovementStates.FOLLOW_COM
} else {
	MovementStates.FOLLOW_FOOT
}

// Gets travel from a planted foot
private fun getPlantedFootTravel(fk: ComputedSkeleton): Vector3 {
	// Which foot is planted
	val plantedFoot = getPlantedFoot(fk)
	val footBone = when (plantedFoot) {
		MovementStates.LEFT_LOCKED -> fk[BodyPart.LEFT_FOOT]
		MovementStates.RIGHT_LOCKED -> fk[BodyPart.RIGHT_FOOT]
		else -> null
	}
	if (footBone == null) return Vector3.ZERO

	// Get foot travel
	val footPosition: Vector3 = footBone.headPosition
	return footPosition - Vector3.ZERO
}

// Gets the sitting travel (emulates hip lock)
private fun computeSittingTravel(fk: ComputedSkeleton): Vector3 {
	val hip = fk[BodyPart.HIP]?.headPosition ?: return Vector3.ZERO
	return Vector3.ZERO
}

// Returns true if either foot's position is below 0
fun isFootOnGround(fk: ComputedSkeleton): Boolean {
	val leftFoot = fk[BodyPart.LEFT_FOOT] ?: return false
	val rightFoot = fk[BodyPart.RIGHT_FOOT] ?: return false
	return leftFoot.headPosition.y <= 0f || rightFoot.headPosition.y <= 0f
}

// returns the bone closest to or furthest inside the ground
private fun getLowestBone(fk: ComputedSkeleton): BoneState = fk.minBy { it.value.headPosition.y }.value

// returns true if the user is likely sitting
// (assumes the floor is flat at 0.0)
private fun isUserSitting(fk: ComputedSkeleton): Boolean {
	// based on the waist to knee vector decide if the user is sitting or
	// standing (ie, if the user is sitting the vector will be pointing off
	// to the side for both feet)
	var leftKnee: Vector3 = fk[BodyPart.LEFT_UPPER_LEG]?.tailPosition ?: return false
	var rightKnee: Vector3 = fk[BodyPart.RIGHT_UPPER_LEG]?.tailPosition ?: return false
	val hip: Vector3 = fk[BodyPart.HIP]?.headPosition ?: return false
	leftKnee = hip - leftKnee
	rightKnee = hip - rightKnee

	// if the y component of the vectors is small then the user is probably sitting
	val sittingLeft = leftKnee.y * SITTING_KNEE_THRESHOLD < leftKnee.x + leftKnee.z
	val sittingRight = rightKnee.y * SITTING_KNEE_THRESHOLD < rightKnee.x + rightKnee.z

	return sittingLeft && sittingRight
}

class LocalizerFkProcessor(val settings: Settings) : SkeletonFkProcessor {
	override fun process(mutableInputSkeleton: InputSkeleton, fk: ComputedSkeleton, floorLevel: Float) {
		val headInput = mutableInputSkeleton[BodyPart.HEAD] ?: return
		if (headInput.isPositionActive || !settings.context.state.value.data.skeletonConfig.toggles.mocapMode) {
			return
		}

		val travel = when (getWorldReference(fk)) {
			MovementStates.FOLLOW_FOOT -> getPlantedFootTravel(fk)

// 			MovementStates.FOLLOW_COM -> getCOMTravel(fk) TODO

			MovementStates.FOLLOW_SITTING -> computeSittingTravel(fk)

			else -> Vector3.ZERO
		}

		val newHeadPosition = (headInput.position ?: Vector3.ZERO) - travel
		mutableInputSkeleton[BodyPart.HEAD] = headInput.copy(position = newHeadPosition)
	}
}
