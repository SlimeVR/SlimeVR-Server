package dev.slimevr.skeleton.fkprocessors

import dev.slimevr.config.Settings
import dev.slimevr.skeleton.BoneState
import dev.slimevr.skeleton.ComputedSkeleton
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.ResettableSkeletonProcessor
import dev.slimevr.skeleton.SkeletonFkProcessor
import dev.slimevr.util.inFloatingSeconds
import dev.slimevr.util.timeSource
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.ResetType
import kotlin.math.abs
import kotlin.math.exp

const val DOWNWARDS_GROUNDING = -1f
const val UPWARDS_GROUNDING = 2.5f

const val MAX_PLANTED_VERTICAL_ACCEL = 10f
const val MAX_PLANTED_HORIZONTAL_ACCEL = 25f
const val FLOOR_TOUCH_THRESHOLD = 0.03f // 3cm

const val SITTING_KNEE_THRESHOLD = -0.2f
const val SITTING_THRESHOLD_TIME = 1f // 1 second

// TODO add thickness for all bones
// Legs, spine and head are always used even if inactive
val alwaysActiveBodyParts = arrayOf(
	BodyPart.LEFT_LOWER_LEG,
	BodyPart.RIGHT_LOWER_LEG,
	BodyPart.LEFT_UPPER_LEG,
	BodyPart.RIGHT_UPPER_LEG,
	BodyPart.HIP,
	BodyPart.LOWER_WAIST,
	BodyPart.UPPER_WAIST,
	BodyPart.LOWER_CHEST,
	BodyPart.UPPER_CHEST,
	BodyPart.NECK,
	BodyPart.HEAD,
)
fun getActiveBodyParts(inputs: InputSkeleton) = inputs.filter { it.value.isRotationActive }.map { it.key } + alwaysActiveBodyParts

// Follows a bone touching that floor that is considered planted
object FloorLocalizer {
	fun horizontalLength(vec: Vector3) = Vector3(vec.x, 0f, vec.z).len()
	fun verticalLength(vec: Vector3) = Vector3(0f, vec.y, 0f).len()

	// Returns true if a bone is considered as touching the floor
	fun isBoneOnFloor(lowestBone: BoneState?) = lowestBone?.let {
		it.tailPosition.y <= FLOOR_TOUCH_THRESHOLD &&
			horizontalLength(it.acceleration) < MAX_PLANTED_HORIZONTAL_ACCEL &&
			verticalLength(it.acceleration) < MAX_PLANTED_VERTICAL_ACCEL
	} ?: false

	// Not necessarily the lowest bone, but always a bone touching the floor
	fun getPlantedBone(inputs: InputSkeleton, fk: ComputedSkeleton): BodyPart? {
		val bonesTouchingFloor = fk.filter { it.key in getActiveBodyParts(inputs) }.filter { isBoneOnFloor(it.value) }.values
		return bonesTouchingFloor.minByOrNull { it.acceleration.lenSq() }?.bodyPart
	}
}

// Locks the hip in place
object SittingLocalizer {
	// Returns true if the user is likely sitting
	// TODO this could be improved
	fun isUserSitting(fk: ComputedSkeleton): Boolean {
		// Using the local knee positions, decide if the user is sitting or
		// standing (if the user is sitting the vector will be pointing off
		// to the side for both knees)
		val leftKnee = fk[BodyPart.LEFT_UPPER_LEG] ?: return false
		val rightKnee = fk[BodyPart.RIGHT_UPPER_LEG] ?: return false

		// if the y component of the vectors is small then the user is probably sitting
		val sittingLeft = leftKnee.localTailPosition.unit().y > SITTING_KNEE_THRESHOLD
		val sittingRight = rightKnee.localTailPosition.unit().y > SITTING_KNEE_THRESHOLD

		return sittingLeft && sittingRight
	}

	fun computeAdjustedTargetHip(targetHip: Vector3, lowestBone: BoneState?): Vector3 {
		lowestBone?.let {
			if (it.tailPosition.y < 0f) {
				return Vector3(targetHip.x, targetHip.y - it.tailPosition.y, targetHip.z)
			}
		}
		return targetHip
	}
}

// Uses acceleration to guess localization
object DeadReckoningLocalizer {
	// TODO: Maybe only take feet or only torso
	//  Also maybe have different parts for linear velocity and accel
	val DEAD_RECKONING_BODY_PARTS = setOf(
		BodyPart.HEAD,
		BodyPart.NECK,
		BodyPart.UPPER_CHEST,
		BodyPart.LOWER_CHEST,
		BodyPart.UPPER_WAIST,
		BodyPart.LOWER_WAIST,
		BodyPart.HIP,
		BodyPart.LEFT_UPPER_LEG,
		BodyPart.RIGHT_UPPER_LEG,
		BodyPart.LEFT_LOWER_LEG,
		BodyPart.RIGHT_LOWER_LEG,
		BodyPart.LEFT_FOOT,
		BodyPart.RIGHT_FOOT,
	)

	fun getAverageLinearVelocity(fk: ComputedSkeleton): Vector3 = DEAD_RECKONING_BODY_PARTS.fold(Vector3.ZERO) { acc, part ->
		fk[part]?.let {
			acc + it.velocity.linear
		} ?: acc
	} /
		DEAD_RECKONING_BODY_PARTS.count().toFloat()

	fun getAverageAcceleration(inputSkeleton: InputSkeleton): Vector3 {
		var count = 0f
		val accelSum = DEAD_RECKONING_BODY_PARTS.fold(Vector3.ZERO) { acc, part ->
			inputSkeleton[part]?.let {
				if (it.isAccelerationActive) {
					count++
					acc + it.acceleration
				} else {
					null
				}
			} ?: acc
		}
		return accelSum / count.coerceAtLeast(1f)
	}

	const val VELOCITY_DAMPING = 3f
	fun computeNewVelocity(velocity: Vector3, acceleration: Vector3, deltaTime: Float) = (velocity * exp(-VELOCITY_DAMPING * deltaTime)) + (acceleration * deltaTime)

	fun computeDisplacement(velocity: Vector3, acceleration: Vector3, deltaTime: Float) = -((velocity * deltaTime) + (acceleration * 0.5f * deltaTime * deltaTime))
}

const val GROUNDING_DAMPENING_MULTIPLIER = 15f
const val GROUNDING_DAMPENING_MIN = 0.7f
fun getGroundingForce(lowestBone: BoneState?, deltaTime: Float): Vector3 = lowestBone?.let {
	val forceDampening = abs(it.tailPosition.y * GROUNDING_DAMPENING_MULTIPLIER).coerceAtLeast(GROUNDING_DAMPENING_MIN)
	if (it.tailPosition.y > 0f) {
		Vector3(0f, (DOWNWARDS_GROUNDING * deltaTime).coerceAtLeast(-it.tailPosition.y) * forceDampening, 0f)
	} else {
		Vector3(0f, (UPWARDS_GROUNDING * deltaTime).coerceAtMost(-it.tailPosition.y) * forceDampening, 0f)
	}
} ?: Vector3.ZERO

fun computeTravel(current: Vector3, target: Vector3) = target - current

fun getLowestBone(inputs: InputSkeleton, fk: ComputedSkeleton) = fk.filter { it.key in getActiveBodyParts(inputs) }.minByOrNull { it.value.tailPosition.y }?.value

enum class FollowSource {
	FLOOR,
	DEAD_RECKONING,
	SITTING,
}
fun getSourceToFollow(fk: ComputedSkeleton, lowestBone: BoneState?): FollowSource = if (SittingLocalizer.isUserSitting(fk)) {
	// The user is sitting down
	FollowSource.SITTING
} else if (FloorLocalizer.isBoneOnFloor(lowestBone)) {
	// One of the user's bone is on the ground
	FollowSource.FLOOR
} else {
	// The user is neither sitting nor has a bone on the ground. Use Centre Of Mass.
	FollowSource.DEAD_RECKONING
}

class LocalizerFkProcessor(val settings: Settings) :
	SkeletonFkProcessor,
	ResettableSkeletonProcessor {
	private var floorTarget: Vector3? = null
	private var lastPlantedBone: BodyPart? = null

	private var accelVelocity = Vector3.ZERO

	private var hipTarget = Vector3.ZERO
	private var sittingTime: Float = 0f

	private var lastProcessTime = timeSource.markNow()

	override fun process(mutableInputSkeleton: InputSkeleton, fk: ComputedSkeleton, floorLevel: Float) {
		if (!settings.context.state.value.data.skeletonConfig.toggles.mocapMode) return
		val headInput = mutableInputSkeleton[BodyPart.HEAD] ?: return
		if (headInput.isPositionActive) return

		val now = timeSource.markNow()
		val deltaTime = (now - lastProcessTime).inFloatingSeconds
		lastProcessTime = now

		// The absolute lowest bone
		val lowestBone = getLowestBone(mutableInputSkeleton, fk)

		// Only follow the hip if the user's been sitting for enough time, else follow the bone on the floor
		val followSource = getSourceToFollow(fk, lowestBone).let {
			if (it == FollowSource.SITTING) {
				sittingTime += deltaTime
				if (sittingTime < SITTING_THRESHOLD_TIME) {
					FollowSource.FLOOR
				} else {
					FollowSource.SITTING
				}
			} else {
				sittingTime = 0f
				it
			}
		}

		// Floor
		val currentPlantedBone = FloorLocalizer.getPlantedBone(mutableInputSkeleton, fk)
		val currentFloor = fk[currentPlantedBone]?.tailPosition
		// Target floor bone changed, or we're in acceleration tracking, reset target to current
		if (currentPlantedBone != lastPlantedBone || followSource == FollowSource.DEAD_RECKONING) {
			lastPlantedBone = currentPlantedBone
			floorTarget = currentFloor
		}

		// Dead reckoning
		val currentAcceleration = DeadReckoningLocalizer.getAverageAcceleration(mutableInputSkeleton)

		// Sitting
		val currentHip = fk[BodyPart.HIP]?.tailPosition ?: Vector3.ZERO
		hipTarget = if (followSource == FollowSource.SITTING) {
			SittingLocalizer.computeAdjustedTargetHip(hipTarget, lowestBone)
		} else {
			currentHip
		}

		// Compute head travel for this frame depending on the source to follow
		val travel = when (followSource) {
			FollowSource.FLOOR -> {
				val floorTravel = currentFloor?.let { current ->
					floorTarget?.let { target ->
						computeTravel(current, target)
					}
				} ?: Vector3.ZERO
				Vector3(floorTravel.x, 0f, floorTravel.z) + getGroundingForce(lowestBone, deltaTime)
			}

			FollowSource.DEAD_RECKONING -> {
				val deadReckoningTravel = DeadReckoningLocalizer.computeDisplacement(accelVelocity, currentAcceleration, deltaTime)
				deadReckoningTravel + getGroundingForce(lowestBone, deltaTime)
			}

			FollowSource.SITTING -> computeTravel(currentHip, hipTarget)
		}

		// Update velocity for dead reckoning
		accelVelocity = if (followSource != FollowSource.DEAD_RECKONING) {
			-DeadReckoningLocalizer.getAverageLinearVelocity(fk)
		} else {
			DeadReckoningLocalizer.computeNewVelocity(accelVelocity, currentAcceleration, deltaTime)
		}

		// Update head position from travel
		val newHeadPosition = (headInput.position ?: Vector3.ZERO) + travel
		mutableInputSkeleton[BodyPart.HEAD] = headInput.copy(position = newHeadPosition)
	}

	override fun reset(resetType: ResetType) {
		if (resetType == ResetType.FULL) {
			floorTarget = null
			lastPlantedBone = null
			accelVelocity = Vector3.ZERO
			hipTarget = Vector3.ZERO
			sittingTime = 0f
			lastProcessTime = timeSource.markNow()
		}
	}
}
