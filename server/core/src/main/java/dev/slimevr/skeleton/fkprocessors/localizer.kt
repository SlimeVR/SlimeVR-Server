package dev.slimevr.skeleton.fkprocessors

import dev.slimevr.config.Settings
import dev.slimevr.skeleton.BODY_PART_MASSES
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

// Constant translations
const val FLOOR_DOWNWARDS_GROUNDING = -1.5f
const val FLOOR_UPWARDS_GROUNDING = 2.75f
const val DEAD_RECKONING_DOWNWARDS_GROUNDING = -1.95f

const val MAX_PLANTED_ACCEL = 9f
const val MAX_PLANTED_VERTICAL_ACCEL = 7.8f
const val MAX_TORSO_VERTICAL_ACCEL = 8f

// In meters
const val FLOOR_TOUCH_DISTANCE = 0.03f

// 1 = up, -1 = down
const val SITTING_KNEE_MIN = -0.4f

const val SITTING_THRESHOLD_SECONDS = 0.8f

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
	// Returns true if a bone is considered as touching the floor
	fun isBoneOnFloor(lowestBone: BoneState?) = lowestBone?.let {
		it.tailPosition.y <= FLOOR_TOUCH_DISTANCE &&
			it.acceleration.y < MAX_PLANTED_VERTICAL_ACCEL &&
			it.acceleration.len() < MAX_PLANTED_ACCEL
	} ?: false

	// Not necessarily the lowest bone, but always a bone touching the floor
	fun getPlantedBone(inputs: InputSkeleton, fk: ComputedSkeleton): BodyPart? {
		val bonesTouchingFloor = fk.filter { it.key in getActiveBodyParts(inputs) }.filter { isBoneOnFloor(it.value) }.values
		return bonesTouchingFloor.minByOrNull { it.acceleration.lenSq() }?.bodyPart
	}

	const val GROUNDING_DAMPENING_MULTIPLIER = 15f
	const val GROUNDING_DAMPENING_MIN = 0.6f
	fun getGroundingForce(lowestBone: BoneState?, deltaTime: Float): Vector3 = lowestBone?.let {
		val forceDampening = abs(it.tailPosition.y * GROUNDING_DAMPENING_MULTIPLIER).coerceAtLeast(GROUNDING_DAMPENING_MIN)
		if (it.tailPosition.y > 0f) {
			Vector3(0f, (FLOOR_DOWNWARDS_GROUNDING * deltaTime).coerceAtLeast(-it.tailPosition.y) * forceDampening, 0f)
		} else {
			Vector3(0f, (FLOOR_UPWARDS_GROUNDING * deltaTime).coerceAtMost(-it.tailPosition.y) * forceDampening, 0f)
		}
	} ?: Vector3.ZERO
}

// Locks the hip in place
object SittingLocalizer {
	// Returns true if the user is likely sitting
	fun isUserSitting(fk: ComputedSkeleton): Boolean {
		// Using the local knee positions, decide if the user is sitting or
		// standing (if the user is sitting the vector will be pointing off
		// to the side for both knees)
		val leftKnee = fk[BodyPart.LEFT_UPPER_LEG] ?: return false
		val rightKnee = fk[BodyPart.RIGHT_UPPER_LEG] ?: return false

		// if the y component of the vectors is small then the user is probably sitting
		val leftKneeVertical = leftKnee.localTailPosition.unit().y
		val rightKneeVertical = rightKnee.localTailPosition.unit().y
		val sittingLeft = leftKneeVertical > SITTING_KNEE_MIN
		val sittingRight = rightKneeVertical > SITTING_KNEE_MIN

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
	// Only use these body parts for getting vertical velocity and acceleration.
	val VERTICAL_BODY_PARTS = setOf(
		BodyPart.HEAD,
		BodyPart.NECK,
		BodyPart.UPPER_CHEST,
		BodyPart.LOWER_CHEST,
		BodyPart.UPPER_WAIST,
		BodyPart.LOWER_WAIST,
		BodyPart.HIP,
	)

	fun getVerticalVelocity(fk: ComputedSkeleton): Vector3 {
		val velocitySum = VERTICAL_BODY_PARTS.fold(Vector3.ZERO) { acc, part ->
			fk[part]?.let {
				acc + it.velocity.linear
			} ?: acc
		}
		return Vector3(0f, (velocitySum / VERTICAL_BODY_PARTS.count().toFloat()).y, 0f)
	}

	fun getHorizontalVelocity(fk: ComputedSkeleton) = BODY_PART_MASSES.keys.fold(Vector3.ZERO) { acc, part ->
		fk[part]?.let { bone ->
			BODY_PART_MASSES[part]?.let { mass ->
				acc + (bone.velocity.linear * mass)
			}
		} ?: acc
	}.let { Vector3(it.x, 0f, it.z) }

	fun getVerticalAcceleration(inputSkeleton: InputSkeleton): Vector3 {
		var count = 0f
		val accelSum = VERTICAL_BODY_PARTS.fold(Vector3.ZERO) { acc, part ->
			inputSkeleton[part]?.let {
				if (it.isAccelerationActive) {
					count++
					acc + it.acceleration
				} else {
					null
				}
			} ?: acc
		}
		return Vector3(0f, (accelSum / count.coerceAtLeast(1f)).y, 0f)
	}

	fun getHorizontalAcceleration(inputSkeleton: InputSkeleton) = BODY_PART_MASSES.keys.fold(Vector3.ZERO) { acc, part ->
		inputSkeleton[part]?.let { bone ->
			BODY_PART_MASSES[part]?.let { mass ->
				acc + (bone.acceleration * mass)
			}
		} ?: acc
	}.let { Vector3(it.x, 0f, it.z) }

	const val VELOCITY_DAMPING = 3.8f
	fun computeNewVelocity(velocity: Vector3, acceleration: Vector3, deltaTime: Float): Vector3 {
		val dampenedVelocity = velocity * exp(-VELOCITY_DAMPING * deltaTime)
		return dampenedVelocity + (acceleration * deltaTime)
	}

	fun computeDisplacement(velocity: Vector3, acceleration: Vector3, deltaTime: Float): Vector3 = (velocity * deltaTime) + (acceleration * 0.5f * deltaTime * deltaTime)

	fun getGroundingForce(lowestBone: BoneState?, deltaTime: Float): Vector3 = lowestBone?.let {
		if (it.tailPosition.y > 0f) {
			Vector3(0f, (DEAD_RECKONING_DOWNWARDS_GROUNDING * deltaTime).coerceAtLeast(-it.tailPosition.y), 0f)
		} else {
			// Snap to floor if below it
			Vector3(0f, -it.tailPosition.y, 0f)
		}
	} ?: Vector3.ZERO
}

fun computeTravel(current: Vector3, target: Vector3) = target - current

fun getLowestBone(inputs: InputSkeleton, fk: ComputedSkeleton) = fk.filter { it.key in getActiveBodyParts(inputs) }.minByOrNull { it.value.tailPosition.y }?.value

enum class FollowSource {
	FLOOR,
	DEAD_RECKONING,
	SITTING,
}
fun getSourceToFollow(fk: ComputedSkeleton, lowestBone: BoneState?, bodyAcceleration: Vector3): FollowSource = if (SittingLocalizer.isUserSitting(fk)) {
	// The user is sitting down
	FollowSource.SITTING
} else if (bodyAcceleration.y < MAX_TORSO_VERTICAL_ACCEL && FloorLocalizer.isBoneOnFloor(lowestBone)) {
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

	private var velocity = Vector3.ZERO

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

		// Average body acceleration
		val bodyAcceleration = DeadReckoningLocalizer.getVerticalAcceleration(mutableInputSkeleton) + DeadReckoningLocalizer.getHorizontalAcceleration(mutableInputSkeleton)

		// Only follow the hip if the user's been sitting for enough time, else follow the bone on the floor
		val followSource = getSourceToFollow(fk, lowestBone, bodyAcceleration).let {
			if (it == FollowSource.SITTING) {
				sittingTime += deltaTime
				if (sittingTime < SITTING_THRESHOLD_SECONDS) {
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
				Vector3(floorTravel.x, 0f, floorTravel.z) + FloorLocalizer.getGroundingForce(lowestBone, deltaTime)
			}

			FollowSource.DEAD_RECKONING ->
				DeadReckoningLocalizer.computeDisplacement(velocity, bodyAcceleration, deltaTime) + DeadReckoningLocalizer.getGroundingForce(lowestBone, deltaTime)

			FollowSource.SITTING ->
				computeTravel(currentHip, hipTarget)
		}

		// Update velocity for dead reckoning
		if (followSource != FollowSource.DEAD_RECKONING) {
			// Reset to reliable velocity from velocity processor.
			velocity = DeadReckoningLocalizer.getVerticalVelocity(fk) + DeadReckoningLocalizer.getHorizontalVelocity(fk)
		}
		// Integrate acceleration into running velocity.
		velocity = DeadReckoningLocalizer.computeNewVelocity(velocity, bodyAcceleration, deltaTime)

		// Update head position from travel
		val newHeadPosition = (headInput.position ?: Vector3.ZERO) + travel
		mutableInputSkeleton[BodyPart.HEAD] = headInput.copy(position = newHeadPosition)
	}

	override fun reset(resetType: ResetType) {
		if (resetType == ResetType.FULL) {
			floorTarget = null
			lastPlantedBone = null
			velocity = Vector3.ZERO
			hipTarget = Vector3.ZERO
			sittingTime = 0f
			lastProcessTime = timeSource.markNow()
		}
	}
}
