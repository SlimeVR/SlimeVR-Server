package dev.slimevr.skeleton.fkprocessors

import dev.slimevr.config.Settings
import dev.slimevr.skeleton.BoneState
import dev.slimevr.skeleton.ComputedSkeleton
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.SkeletonFkProcessor
import dev.slimevr.skeleton.centreOfMass
import dev.slimevr.util.timeSource
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

private const val WARMUP_FRAMES = 100 // ~0.1 seconds
private const val MAX_FOOT_PERCENTAGE = 50.0f
private const val MAX_ACCEL_UP = 2.0f
private const val SITTING_KNEE_THRESHOLD = 1.1f
private const val VELOCITY_SAMPLE_RATE: Long = 100000000 // 10ms
private const val CONSTANT_ACCELERATION: Float = 2.0f
private val SITTING_THRESHOLD = 1.2.seconds

enum class FollowSource {
	FOOT,
	COM,
	HIP,
}

// Returns true if the user is likely sitting
private fun isUserSitting(fk: ComputedSkeleton): Boolean {
	// based on the waist to knee vector decide if the user is sitting or
	// standing (ie, if the user is sitting the vector will be pointing off
	// to the side for both feet)
	var leftKnee: Vector3 = fk[BodyPart.LEFT_UPPER_LEG]?.tailPosition ?: return false
	var rightKnee: Vector3 = fk[BodyPart.RIGHT_UPPER_LEG]?.tailPosition ?: return false
	val hip: Vector3 = fk[BodyPart.HIP]?.tailPosition ?: return false
	leftKnee = hip - leftKnee
	rightKnee = hip - rightKnee

	// if the y component of the vectors is small then the user is probably sitting
	val sittingLeft = leftKnee.y * SITTING_KNEE_THRESHOLD < leftKnee.x + leftKnee.z
	val sittingRight = rightKnee.y * SITTING_KNEE_THRESHOLD < rightKnee.x + rightKnee.z

	return sittingLeft && sittingRight
}

// Returns true if either foot's position is below 0
fun isFootOnGround(fk: ComputedSkeleton): Boolean {
	val leftFoot = fk[BodyPart.LEFT_FOOT] ?: return false
	val rightFoot = fk[BodyPart.RIGHT_FOOT] ?: return false
	return leftFoot.tailPosition.y <= 0f || rightFoot.tailPosition.y <= 0f
}

private fun getSourceToFollow(fk: ComputedSkeleton): FollowSource = if (isUserSitting(fk)) {
	// The user is sitting down
	FollowSource.HIP
} else if (isFootOnGround(fk)) {
	// One of the user's foot is on the ground
	FollowSource.FOOT
} else {
	// The user is neither sitting nor has a foot on the ground. Use Center Of Mass.
	FollowSource.COM
}

// returns the bone closest to or furthest inside the ground
fun getLowestBone(fk: ComputedSkeleton): BoneState = fk.minBy { it.value.tailPosition.y }.value

object FootLocalizer {
	enum class PlantedFoot {
		LEFT,
		RIGHT,
		NONE,
	}

	fun getPlantedFoot(fk: ComputedSkeleton): PlantedFoot {
		val leftFoot = fk[BodyPart.LEFT_FOOT] ?: return PlantedFoot.NONE
		val rightFoot = fk[BodyPart.RIGHT_FOOT] ?: return PlantedFoot.NONE

		// TODO
		return if (leftFoot.tailPosition.y < rightFoot.tailPosition.y) {
			PlantedFoot.LEFT
		} else {
			PlantedFoot.RIGHT
		}
	}

	fun getCurrentFootPosition(fk: ComputedSkeleton, plantedFoot: PlantedFoot) = when (plantedFoot) {
		PlantedFoot.LEFT -> fk[BodyPart.LEFT_FOOT]
		PlantedFoot.RIGHT -> fk[BodyPart.RIGHT_FOOT]
		else -> null
	}?.tailPosition ?: Vector3.ZERO

	fun computeFootTravel(currentFoot: Vector3, targetFoot: Vector3) = currentFoot - targetFoot
}

object HipLocalizer {
	fun getAdjustedTargetHip(fk: ComputedSkeleton, targetHip: Vector3): Vector3 {
		val lowestBone = getLowestBone(fk)
		if (lowestBone.tailPosition.y < 0f) {
			return Vector3(targetHip.x, targetHip.y - lowestBone.tailPosition.y, targetHip.z)
		}
		return targetHip
	}

	fun computeSittingTravel(fk: ComputedSkeleton, hip: Vector3, targetHip: Vector3) = hip - targetHip
}

object COMLocalizer {
	// get the velocity of the COM
// 	private fun getCOMVelocity(): Vector3 {
// 		val comY = comVelocity.y
//
// 		var buf = bufCur
// 		val timeStart: Long = buf.timeOfFrame
// 		var timeEnd = timeStart - VELOCITY_SAMPLE_RATE
// 		val comPosStart: Vector3 = buf.centerOfMass
//
// 		// get the buffer that occurred VELOCITY_SAMPLE_RATE ago in time
// 		while (buf.timeOfFrame > timeEnd && buf.parent != null) {
// 			buf = buf.parent!!
// 		}
//
// 		val comPosEnd: Vector3 = buf.centerOfMass
// 		timeEnd = buf.timeOfFrame
//
// 		// calculate the velocity
// 		comVelocity = (comPosEnd - comPosStart) / ((timeEnd - timeStart) / LegTweaksBuffer.NS_CONVERT)
//
// 		// if the feet have been the reference for a short amount of time nullify any upwards acceleration to prevent flying away
// 		if (footFrames < WARMUP_FRAMES) {
// 			comAccel = Vector3(
// 				comAccel.x,
// 				FastMath.clamp(comAccel.y, -9999.0f, 0.0f),
// 				comAccel.z,
// 			)
// 		}
//
// 		// constantly pull the skeleton down a little to account for acceleration
// 		// inaccuracy
// 		val gravity = comAccel.y - CONSTANT_ACCELERATION
//
// 		// add the acceleration of gravity
// 		comVelocity = Vector3(
// 			comVelocity.x,
// 			comY + (gravity / bufCur.getTimeDelta()),
// 			comVelocity.z,
// 		)
//
// 		return comVelocity
// 	}
//
// 	private fun getTargetCOM() {
// 		// if not in COM tracking mode, just use the current COM
// 		var targetCOM = Vector3.ZERO
// 		var currentCOM = Vector3.ZERO
//
// 		if (worldReference == MovementStates.FOLLOW_FOOT || worldReference == MovementStates.FOLLOW_SITTING) {
// 			targetCOM = centreOfMass(fk)
// 		} else {
// 			currentCOM = targetCOM
// 		}
//
// 		targetCOM += (comVelocity / bufCur.getTimeDelta())
//
// 		val lowTracker = getLowestTracker()
//
// 		// update the target COM and velocity to reflect this new distance
// 		if (lowTracker != null) {
// 			if (lowTracker.position.y < uncorrectedFloor) {
// 				targetCOM = Vector3(targetCOM.x, targetCOM.y + (uncorrectedFloor - lowTracker.position.y), targetCOM.z)
// 				comVelocity = Vector3(comVelocity.x, 0.0f, comVelocity.z)
// 			}
// 		}
// 	}

	fun computeCOMTravel(currentCOM: Vector3, targetCOM: Vector3) = currentCOM - targetCOM
}

class LocalizerFkProcessor(val settings: Settings) : SkeletonFkProcessor {
	private var plantedFoot = FootLocalizer.PlantedFoot.LEFT
	private var targetFoot = Vector3.ZERO

	private var sittingTime = Duration.ZERO
	private var targetHip = Vector3.ZERO

	private var targetCOM = Vector3.ZERO

	private var lastProcessTime = timeSource.markNow()

	override fun process(mutableInputSkeleton: InputSkeleton, fk: ComputedSkeleton, floorLevel: Float) {
		val headInput = mutableInputSkeleton[BodyPart.HEAD] ?: return
		if (headInput.isPositionActive || !settings.context.state.value.data.skeletonConfig.toggles.mocapMode) {
			return
		}

		val now = timeSource.markNow()
		val deltaTime = now - lastProcessTime
		lastProcessTime = now

		val currentCOM = centreOfMass(fk)
		val currentHip = fk[BodyPart.HIP]?.tailPosition ?: Vector3.ZERO

		// Only follow the hip if the user's been sitting for enough time, else follow the foot
		val followSource = getSourceToFollow(fk).let {
			if (it == FollowSource.HIP) {
				sittingTime += deltaTime
				if (sittingTime < SITTING_THRESHOLD) {
					FollowSource.FOOT
				} else {
					FollowSource.HIP
				}
			} else {
				sittingTime = Duration.ZERO
				it
			}
		}

		val travel = when (followSource) {
			FollowSource.FOOT -> {
				targetHip = currentHip
				plantedFoot = FootLocalizer.getPlantedFoot(fk)
				val currentFoot = FootLocalizer.getCurrentFootPosition(fk, plantedFoot)
				targetFoot = FootLocalizer.computeFootTravel(currentFoot, targetFoot)
				val comTravel = COMLocalizer.computeCOMTravel(currentCOM, targetCOM)
				Vector3(targetFoot.x, comTravel.y, targetFoot.z)
			}

			FollowSource.COM -> {
				targetHip = currentHip
				COMLocalizer.computeCOMTravel(currentCOM, targetCOM)
			}

			FollowSource.HIP -> {
				targetHip = HipLocalizer.getAdjustedTargetHip(fk, targetHip)
				HipLocalizer.computeSittingTravel(fk, currentHip, targetHip)
			}
		}

		val newHeadPosition = (headInput.position ?: Vector3.ZERO) + travel
		mutableInputSkeleton[BodyPart.HEAD] = headInput.copy(position = newHeadPosition)
	}
}
