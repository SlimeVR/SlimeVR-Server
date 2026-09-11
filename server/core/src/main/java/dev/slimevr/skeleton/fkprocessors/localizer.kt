package dev.slimevr.skeleton.fkprocessors

import dev.slimevr.config.Settings
import dev.slimevr.skeleton.BoneState
import dev.slimevr.skeleton.ComputedSkeleton
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.ResettableSkeletonProcessor
import dev.slimevr.skeleton.SkeletonFkProcessor
import dev.slimevr.skeleton.centreOfMass
import dev.slimevr.skeleton.targetprocessors.FLOOR_CALIBRATION_OFFSET
import dev.slimevr.skeleton.targetprocessors.SKATING_ACCELERATION_THRESHOLD
import dev.slimevr.skeleton.targetprocessors.SKATING_LOCK_ENGAGE_PERCENT
import dev.slimevr.skeleton.targetprocessors.SKATING_VELOCITY_THRESHOLD
import dev.slimevr.skeleton.targetprocessors.shouldLock
import dev.slimevr.util.MonotonicValueTimeMark
import dev.slimevr.util.inFloatingSeconds
import dev.slimevr.util.timeSource
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.ResetType
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

private const val MAX_FOOT_PERCENTAGE = 50.0f
private const val MAX_ACCEL_UP = 2.0f
private const val SITTING_KNEE_THRESHOLD = 1.1f
private const val CONSTANT_ACCELERATION: Float = 2.0f
private val WARMUP_DELAY = 160.milliseconds
private val SITTING_THRESHOLD = 1.6.seconds
private val VELOCITY_SAMPLE_RATE = 16.milliseconds

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
private fun isFootOnGround(fk: ComputedSkeleton): Boolean {
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
	// The user is neither sitting nor has a foot on the ground. Use Centre Of Mass.
	FollowSource.COM
}

private fun getActiveBodyParts(inputs: InputSkeleton) = inputs.filter { it.value.isRotationActive }.map { it.key }

/** Returns the active bone closest to or furthest inside the ground */
fun getLowestBone(inputs: InputSkeleton, fk: ComputedSkeleton): BoneState? {
	val activeBodyParts = getActiveBodyParts(inputs)
	return fk.filter { it.key in activeBodyParts }.minByOrNull { it.value.tailPosition.y }?.value
}

object FootLocalizer {
	enum class PlantedFoot {
		LEFT,
		RIGHT,
		NONE,
	}

	private fun isFootLocked(bone: BoneState, lastPlantedFoot: PlantedFoot) = shouldLock(
		bone.tailPosition,
		bone.tailPosition,
		bone.acceleration,
		bone.velocity,
		if (lastPlantedFoot == PlantedFoot.LEFT) SKATING_LOCK_ENGAGE_PERCENT else 1f,
		0f,
	)

	/**
	 * Returns the average percentage the real velocity and acceleration are of
	 * the scaled thresholds for velocity and acceleration
	 */
	private fun velocityAccelRatio(bone: BoneState): Float {
		val velocityPercentage = bone.velocity.linear.lenSq() / SKATING_VELOCITY_THRESHOLD
		val accelerationPercentage = bone.acceleration.lenSq() / SKATING_ACCELERATION_THRESHOLD
		return (velocityPercentage + accelerationPercentage) / 2f
	}

	fun getPlantedFoot(fk: ComputedSkeleton, lastPlantedFoot: PlantedFoot): PlantedFoot {
		val leftFoot = fk[BodyPart.LEFT_FOOT] ?: return PlantedFoot.NONE
		val rightFoot = fk[BodyPart.RIGHT_FOOT] ?: return PlantedFoot.NONE

		// If foot is locked, use that
		if (isFootLocked(leftFoot, lastPlantedFoot)) return PlantedFoot.LEFT
		if (isFootLocked(rightFoot, lastPlantedFoot)) return PlantedFoot.RIGHT

		// Else, use velocity and accel to pick the foot who moves the least
		val leftVelocityAccelRatio = velocityAccelRatio(leftFoot)
		val rightVelocityAccelRatio = velocityAccelRatio(rightFoot)
		return if (leftVelocityAccelRatio < rightVelocityAccelRatio &&
			leftVelocityAccelRatio < MAX_FOOT_PERCENTAGE &&
			leftFoot.acceleration.y < MAX_ACCEL_UP
		) {
			PlantedFoot.LEFT
		} else if (rightVelocityAccelRatio < leftVelocityAccelRatio &&
			rightVelocityAccelRatio < MAX_FOOT_PERCENTAGE &&
			rightFoot.acceleration.y < MAX_ACCEL_UP
		) {
			PlantedFoot.RIGHT
		} else {
			PlantedFoot.NONE
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
	fun getAdjustedTargetHip(inputs: InputSkeleton, fk: ComputedSkeleton, targetHip: Vector3): Vector3 {
		getLowestBone(inputs, fk)?.let {
			if (it.tailPosition.y < FLOOR_CALIBRATION_OFFSET) {
				return Vector3(targetHip.x, targetHip.y + (FLOOR_CALIBRATION_OFFSET - it.tailPosition.y), targetHip.z)
			}
		}
		return targetHip
	}

	fun computeSittingTravel(hip: Vector3, targetHip: Vector3) = hip - targetHip
}

object COMLocalizer {
	private val TORSO_TRACKERS = setOf(BodyPart.UPPER_CHEST, BodyPart.LOWER_CHEST, BodyPart.UPPER_WAIST, BodyPart.LOWER_WAIST, BodyPart.HIP)

	/** Get the average accel of the torso bones */
	fun getTorsoAccel(inputs: InputSkeleton): Vector3 {
		var i = 0f
		return TORSO_TRACKERS.fold(Vector3.ZERO) { acc, part ->
			inputs[part]?.let {
				if (it.isAccelerationActive) {
					i++
					acc + it.acceleration
				}
			}
			acc
		} /
			i.coerceAtLeast(1f)
	}

	/** Get the velocity of the COM */
	fun getCOMVelocity(currentCOM: Vector3, previousCOMs: List<Pair<MonotonicValueTimeMark, Vector3>>, lastCOMVelocity: Vector3, currentTime: MonotonicValueTimeMark, comAccel: Vector3, deltaTime: Duration): Vector3 {
		// Calculate the velocity
		val sampleTimeTarget = currentTime - VELOCITY_SAMPLE_RATE
		val previousCOM = previousCOMs.firstOrNull { it.first <= sampleTimeTarget } ?: return Vector3.ZERO
		val comVelocity = (previousCOM.second - currentCOM) / ((previousCOM.first - currentTime).inFloatingSeconds)

		// Constantly pull the skeleton down a little to account for acceleration inaccuracy
		val gravity = (comAccel.y - CONSTANT_ACCELERATION) / deltaTime.inFloatingSeconds

		// Add the acceleration of gravity
		return Vector3(
			comVelocity.x,
			lastCOMVelocity.y + gravity,
			comVelocity.z,
		)
	}

	fun getTargetCOM(inputs: InputSkeleton, fk: ComputedSkeleton, targetCOM: Vector3, comVelocity: Vector3, deltaTime: Duration): Vector3 {
		val currentCOM = targetCOM + (comVelocity / deltaTime.inFloatingSeconds)

		// Update the target COM and velocity to reflect this new distance
		getLowestBone(inputs, fk)?.let {
			if (it.tailPosition.y < FLOOR_CALIBRATION_OFFSET) {
				return Vector3(currentCOM.x, currentCOM.y + (FLOOR_CALIBRATION_OFFSET - it.tailPosition.y), currentCOM.z)
			}
		}
		return currentCOM
	}

	fun computeCOMTravel(currentCOM: Vector3, targetCOM: Vector3) = currentCOM - targetCOM
}

class LocalizerFkProcessor(val settings: Settings) :
	SkeletonFkProcessor,
	ResettableSkeletonProcessor {
	private var plantedFoot = FootLocalizer.PlantedFoot.NONE
	private var targetFoot = Vector3.ZERO

	private var sittingTime = Duration.ZERO
	private var targetHip = Vector3.ZERO

	private val previousCOMs = mutableListOf<Pair<MonotonicValueTimeMark, Vector3>>()
	private var comVelocity = Vector3.ZERO
	private var targetCOM = Vector3.ZERO

	private var lastProcessTime = timeSource.markNow()

	override fun process(mutableInputSkeleton: InputSkeleton, fk: ComputedSkeleton, floorLevel: Float) {
		if (!settings.context.state.value.data.skeletonConfig.toggles.mocapMode) return
		val headInput = mutableInputSkeleton[BodyPart.HEAD] ?: return
		if (headInput.isPositionActive) return

		val now = timeSource.markNow()
		val deltaTime = now - lastProcessTime
		lastProcessTime = now

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

		// Compute comVelocity and targetCom
		val currentCom = centreOfMass(fk)
		if (followSource != FollowSource.COM) targetCOM = currentCom
		val comAccel = COMLocalizer.getTorsoAccel(mutableInputSkeleton)
		comVelocity = COMLocalizer.getCOMVelocity(currentCom, previousCOMs, comVelocity, now, comAccel, deltaTime)
		targetCOM = COMLocalizer.getTargetCOM(mutableInputSkeleton, fk, targetCOM, comVelocity, deltaTime)
		previousCOMs.add(now to centreOfMass(fk))

		val currentHip = fk[BodyPart.HIP]?.tailPosition ?: Vector3.ZERO

		val travel = when (followSource) {
			FollowSource.FOOT -> {
				// Set the hip target to the current hip
				targetHip = currentHip
				// Get foot travel
				plantedFoot = FootLocalizer.getPlantedFoot(fk, plantedFoot)
				val footTravel = if (plantedFoot == FootLocalizer.PlantedFoot.NONE) {
					targetFoot = Vector3.ZERO
					Vector3.ZERO
				} else {
					val currentFoot = FootLocalizer.getCurrentFootPosition(fk, plantedFoot)
					val travel = FootLocalizer.computeFootTravel(currentFoot, targetFoot)
					targetFoot = currentFoot
					travel
				}

				// Get COM travel
				val comTravel = COMLocalizer.computeCOMTravel(targetCOM, targetCOM)
				// Return horizontal foot travel and vertical COM travel
				Vector3(footTravel.x, comTravel.y, footTravel.z)

				Vector3.ZERO // TODO
			}

			FollowSource.COM -> {
				// Set the hip target to the current hip
				targetHip = currentHip
				// Return COM travel
				COMLocalizer.computeCOMTravel(targetCOM, targetCOM)

				Vector3.ZERO // TODO
			}

			FollowSource.HIP -> {
				// Adjust the target hip
				targetHip = HipLocalizer.getAdjustedTargetHip(mutableInputSkeleton, fk, targetHip)
				// Return the sitting travel
				HipLocalizer.computeSittingTravel(currentHip, targetHip)

				Vector3.ZERO // TODO
			}
		}

		val newHeadPosition = (headInput.position ?: Vector3.ZERO) + travel
		mutableInputSkeleton[BodyPart.HEAD] = headInput.copy(position = newHeadPosition)
	}

	override fun reset(resetType: ResetType) {
		if (resetType == ResetType.FULL) {
			plantedFoot = FootLocalizer.PlantedFoot.NONE
			targetFoot = Vector3.ZERO
			sittingTime = Duration.ZERO
			targetHip = Vector3.ZERO
			previousCOMs.clear()
			comVelocity = Vector3.ZERO
			targetCOM = Vector3.ZERO
			lastProcessTime = timeSource.markNow()
		}
	}
}
