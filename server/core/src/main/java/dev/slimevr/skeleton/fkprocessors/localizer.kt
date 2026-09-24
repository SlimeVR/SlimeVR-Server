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
import dev.slimevr.skeleton.targetprocessors.SKATING_LINEAR_VELOCITY_THRESHOLD
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
private const val SITTING_KNEE_THRESHOLD = -0.1f
private const val CONSTANT_ACCELERATION: Float = 2.0f
private val SITTING_THRESHOLD = 1.6.seconds
private val VELOCITY_SAMPLE_RATE = 16.milliseconds

enum class FollowSource {
	FOOT,
	COM,
	HIP,
}

// Returns true if the user is likely sitting
private fun isUserSitting(fk: ComputedSkeleton): Boolean {
	// Using the local knee positions, decide if the user is sitting or
	// standing (if the user is sitting the vector will be pointing off
	// to the side for both knees)
	val leftKnee = fk[BodyPart.LEFT_UPPER_LEG] ?: return false
	val rightKnee = fk[BodyPart.RIGHT_UPPER_LEG] ?: return false

	// if the y component of the vectors is small then the user is probably sitting
	val sittingLeft = leftKnee.localTailPosition.y / leftKnee.offset.len() > SITTING_KNEE_THRESHOLD
	val sittingRight = rightKnee.localTailPosition.y / rightKnee.offset.len() > SITTING_KNEE_THRESHOLD

	return sittingLeft && sittingRight
}

// Returns true if either foot's position is below 0
private fun isFootOnGround(fk: ComputedSkeleton): Boolean {
	val leftFoot = fk[BodyPart.LEFT_FOOT] ?: return false
	val rightFoot = fk[BodyPart.RIGHT_FOOT] ?: return false
	// TODO should use FLOOR_CALIBRATION_OFFSET?
	return leftFoot.headPosition.y <= 0f ||
		rightFoot.headPosition.y <= 0f ||
		leftFoot.tailPosition.y <= 0f ||
		rightFoot.tailPosition.y <= 0f
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

// Feet are always considered even if inactive
private val alwaysActiveBodyParts = arrayOf(BodyPart.LEFT_FOOT, BodyPart.RIGHT_FOOT, BodyPart.LEFT_LOWER_LEG, BodyPart.RIGHT_LOWER_LEG)
private fun getActiveBodyParts(inputs: InputSkeleton) = inputs.filter { it.value.isRotationActive }.map { it.key } + alwaysActiveBodyParts

/** Returns the active bone closest to or furthest inside the ground */
fun getLowestBone(inputs: InputSkeleton, fk: ComputedSkeleton) = fk.filter { it.key in getActiveBodyParts(inputs) }.minByOrNull { it.value.tailPosition.y }?.value

object FootLocalizer {
	enum class PlantedFoot {
		LEFT,
		RIGHT,
		NONE,
	}

	fun getCurrentFootPosition(fk: ComputedSkeleton, plantedFoot: PlantedFoot) = when (plantedFoot) {
		PlantedFoot.LEFT -> fk[BodyPart.LEFT_FOOT]?.tailPosition
		PlantedFoot.RIGHT -> fk[BodyPart.RIGHT_FOOT]?.tailPosition
		else -> null
	}

	private fun whichPlantedFoot(bodyPart: BodyPart) = when (bodyPart) {
		BodyPart.LEFT_LOWER_LEG, BodyPart.LEFT_FOOT -> PlantedFoot.LEFT
		BodyPart.RIGHT_LOWER_LEG, BodyPart.RIGHT_FOOT -> PlantedFoot.RIGHT
		else -> PlantedFoot.NONE
	}

	private fun isFootLocked(bone: BoneState, lastPlantedFoot: PlantedFoot): Boolean = shouldLock(
		bone.tailPosition,
		bone.tailPosition,
		bone.acceleration,
		bone.velocity,
		lastPlantedFoot == whichPlantedFoot(bone.bodyPart),
	)

	/**
	 * Returns the average percentage the real velocity and acceleration are of
	 * the scaled thresholds for velocity and acceleration
	 */
	private fun velocityAccelRatio(bone: BoneState): Float {
		val velocityPercentage = bone.velocity.linear.len() / SKATING_LINEAR_VELOCITY_THRESHOLD
		val accelerationPercentage = bone.acceleration.len() / SKATING_ACCELERATION_THRESHOLD
		return (velocityPercentage + accelerationPercentage) / 2f
	}

	fun getPlantedFoot(fk: ComputedSkeleton, lastPlantedFoot: PlantedFoot): PlantedFoot {
		// TODO start with foot, fallback to ankles, else nil
		val leftLowerLeg = fk[BodyPart.LEFT_LOWER_LEG] ?: return PlantedFoot.NONE
		val rightLowerLeg = fk[BodyPart.RIGHT_LOWER_LEG] ?: return PlantedFoot.NONE

		// If foot is locked, use that TODO should maybe not use that and just check y position instead
// 		if (isFootLocked(leftLowerLeg, lastPlantedFoot)) return PlantedFoot.LEFT
// 		if (isFootLocked(rightLowerLeg, lastPlantedFoot)) return PlantedFoot.RIGHT

		// Else, use velocity and accel to pick the foot who moves the least
		val leftVelocityAccelRatio = velocityAccelRatio(leftLowerLeg)
		val rightVelocityAccelRatio = velocityAccelRatio(rightLowerLeg)
		return if (leftVelocityAccelRatio < rightVelocityAccelRatio &&
			leftVelocityAccelRatio < MAX_FOOT_PERCENTAGE &&
			leftLowerLeg.acceleration.y < MAX_ACCEL_UP
		) {
			PlantedFoot.LEFT
		} else if (rightVelocityAccelRatio < leftVelocityAccelRatio &&
			rightVelocityAccelRatio < MAX_FOOT_PERCENTAGE &&
			rightLowerLeg.acceleration.y < MAX_ACCEL_UP
		) {
			PlantedFoot.RIGHT
		} else {
			PlantedFoot.NONE
		}
	}

	fun computeFootTravel(currentFoot: Vector3, targetFoot: Vector3) = targetFoot - currentFoot
}

object HipLocalizer {
	fun computeAdjustedTargetHip(targetHip: Vector3, lowestBone: BoneState?): Vector3 {
		lowestBone?.let {
			if (it.tailPosition.y < FLOOR_CALIBRATION_OFFSET) {
				return Vector3(targetHip.x, targetHip.y + (FLOOR_CALIBRATION_OFFSET - it.tailPosition.y), targetHip.z)
			}
		}
		return targetHip
	}

	fun computeSittingTravel(hip: Vector3, targetHip: Vector3) = targetHip - hip
}

object COMLocalizer {
	private val TORSO_TRACKERS = setOf(BodyPart.UPPER_CHEST, BodyPart.LOWER_CHEST, BodyPart.UPPER_WAIST, BodyPart.LOWER_WAIST, BodyPart.HIP)

	/** Get the average accel of the torso bones */
	fun computeTorsoAccel(inputs: InputSkeleton): Vector3 {
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
	fun computeCOMVelocity(currentCOM: Vector3, previousCOMs: List<Pair<MonotonicValueTimeMark, Vector3>>, lastCOMVelocity: Vector3, currentTime: MonotonicValueTimeMark, comAccel: Vector3, deltaTime: Duration): Vector3 {
		// Calculate the velocity
		val sampleTimeTarget = currentTime - VELOCITY_SAMPLE_RATE
		val previousCOM = previousCOMs.firstOrNull { it.first <= sampleTimeTarget } ?: return Vector3.ZERO
		val comVelocity = (previousCOM.second - currentCOM) / ((previousCOM.first - currentTime).inFloatingSeconds)

		// Constantly pull the skeleton down a little to account for acceleration inaccuracy
		val gravity = (comAccel.y - CONSTANT_ACCELERATION) * deltaTime.inFloatingSeconds

		// Add the acceleration of gravity
		return Vector3(
			comVelocity.x,
			lastCOMVelocity.y + gravity,
			comVelocity.z,
		)
	}

	fun computeTargetCOM(targetCOM: Vector3, comVelocity: Vector3, deltaTime: Duration) = targetCOM + (comVelocity * deltaTime.inFloatingSeconds)

	fun floorAdjustTargetCOM(targetCOM: Vector3, lowestBone: BoneState?) = lowestBone?.let {
		if (it.tailPosition.y <= FLOOR_CALIBRATION_OFFSET) {
			Vector3(targetCOM.x, targetCOM.y - it.tailPosition.y, targetCOM.z)
		} else {
			targetCOM
		}
	} ?: targetCOM

	fun floorAdjustCOMVelocity(comVelocity: Vector3, lowestBone: BoneState?) = lowestBone?.let {
		if (it.tailPosition.y <= FLOOR_CALIBRATION_OFFSET) {
			Vector3(comVelocity.x, 0f, comVelocity.z)
		} else {
			comVelocity
		}
	} ?: comVelocity

	fun computeCOMTravel(currentCOM: Vector3, targetCOM: Vector3) = targetCOM - currentCOM
}

class LocalizerFkProcessor(val settings: Settings) :
	SkeletonFkProcessor,
	ResettableSkeletonProcessor {
	private var lastPlantedFoot = FootLocalizer.PlantedFoot.NONE
	private var targetFoot: Vector3? = null

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

		val lowestBone = getLowestBone(mutableInputSkeleton, fk)

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

		// Get current foot info and update target foot if planted foot changed
		val currentPlantedFoot = FootLocalizer.getPlantedFoot(fk, lastPlantedFoot)
		val currentFoot = FootLocalizer.getCurrentFootPosition(fk, currentPlantedFoot)
		// Target planted foot changed, reset target to current
		if (currentPlantedFoot != lastPlantedFoot) targetFoot = currentFoot

		// Compute comVelocity and targetCom
		val currentCom = centreOfMass(fk)
		if (followSource != FollowSource.COM) targetCOM = currentCom
		previousCOMs.add(now to centreOfMass(fk))

		// Compute COM stuff
		val comAccel = COMLocalizer.computeTorsoAccel(mutableInputSkeleton)
		comVelocity = COMLocalizer.computeCOMVelocity(currentCom, previousCOMs, comVelocity, now, comAccel, deltaTime)
		targetCOM = COMLocalizer.computeTargetCOM(targetCOM, comVelocity, deltaTime)
		targetCOM = COMLocalizer.floorAdjustTargetCOM(targetCOM, lowestBone)
		comVelocity = COMLocalizer.floorAdjustCOMVelocity(comVelocity, lowestBone)

		// Get current hip position
		val currentHip = fk[BodyPart.HIP]?.tailPosition ?: Vector3.ZERO

		val travel = when (followSource) {
			FollowSource.FOOT -> {
				// Set the hip target to the current hip
				targetHip = currentHip

				// Get foot travel
				val footTravel = currentFoot?.let { current ->
					targetFoot?.let { target ->
						FootLocalizer.computeFootTravel(current, target)
					}
				} ?: Vector3.ZERO

				// Get COM travel
				val comTravel = COMLocalizer.computeCOMTravel(currentCom, targetCOM)

				// Return horizontal foot travel and vertical COM travel
				Vector3(footTravel.x, comTravel.y, footTravel.z)
			}

			FollowSource.COM -> {
				// Set the hip target to the current hip
				targetHip = currentHip

				// Set target foot
				targetFoot = currentFoot

				// Return COM travel
				COMLocalizer.computeCOMTravel(currentCom, targetCOM)
			}

			FollowSource.HIP -> {
				// Adjust the target hip
				targetHip = HipLocalizer.computeAdjustedTargetHip(targetHip, lowestBone)

				// Return the sitting travel
				HipLocalizer.computeSittingTravel(currentHip, targetHip)
			}
		}

		// Set last foot info for next frame
		lastPlantedFoot = currentPlantedFoot

		// Update head position from travel
		val newHeadPosition = (headInput.position ?: Vector3.ZERO) + travel
		mutableInputSkeleton[BodyPart.HEAD] = headInput.copy(position = newHeadPosition)
	}

	override fun reset(resetType: ResetType) {
		if (resetType == ResetType.FULL) {
			lastPlantedFoot = FootLocalizer.PlantedFoot.NONE
			targetFoot = null
			sittingTime = Duration.ZERO
			targetHip = Vector3.ZERO
			previousCOMs.clear()
			comVelocity = Vector3.ZERO
			targetCOM = Vector3.ZERO
			lastProcessTime = timeSource.markNow()
		}
	}
}
