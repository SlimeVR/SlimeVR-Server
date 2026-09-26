package dev.slimevr.skeleton.fkprocessors

import dev.slimevr.config.Settings
import dev.slimevr.skeleton.BoneState
import dev.slimevr.skeleton.ComputedSkeleton
import dev.slimevr.skeleton.InputSkeleton
import dev.slimevr.skeleton.ResettableSkeletonProcessor
import dev.slimevr.skeleton.SkeletonFkProcessor
import dev.slimevr.skeleton.centreOfMass
import dev.slimevr.util.MonotonicValueTimeMark
import dev.slimevr.util.inFloatingSeconds
import dev.slimevr.util.timeSource
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.ResetType
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

const val SITTING_KNEE_THRESHOLD = -0.2f
const val FLOOR_TOUCH_THRESHOLD = 0.01f // 1cm
const val GRAVITY: Float = 2.0f
val SITTING_THRESHOLD = 1.seconds
val VELOCITY_SAMPLE_RATE = 16.milliseconds

// Legs are always used even if inactive
val alwaysActiveBodyParts = arrayOf(
	BodyPart.LEFT_FOOT,
	BodyPart.RIGHT_FOOT,
	BodyPart.LEFT_LOWER_LEG,
	BodyPart.RIGHT_LOWER_LEG,
	BodyPart.LEFT_UPPER_LEG,
	BodyPart.RIGHT_UPPER_LEG,
)
fun getActiveBodyParts(inputs: InputSkeleton) = inputs.filter { it.value.isRotationActive }.map { it.key } + alwaysActiveBodyParts

/** Returns the active bone closest to or furthest inside the ground */
fun getLowestBone(inputs: InputSkeleton, fk: ComputedSkeleton) = fk.filter { it.key in getActiveBodyParts(inputs) }.minByOrNull { it.value.tailPosition.y }?.value

enum class FollowSource {
	FLOOR,
	COM,
	SITTING,
}

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

// Returns true if a bone is considered touching the floor
fun isBoneOnFloor(lowestBone: BoneState?) = lowestBone?.let { it.tailPosition.y <= FLOOR_TOUCH_THRESHOLD } ?: false

fun getSourceToFollow(fk: ComputedSkeleton, lowestBone: BoneState?): FollowSource = if (isUserSitting(fk)) {
	// The user is sitting down
	FollowSource.SITTING
} else if (isBoneOnFloor(lowestBone)) {
	// One of the user's bone is on the ground
	FollowSource.FLOOR
} else {
	// The user is neither sitting nor has a bone on the ground. Use Centre Of Mass.
	FollowSource.COM
}

// Follows a bone touching that floor that is considered planted
object FloorLocalizer {
	fun getPlantedBone(inputs: InputSkeleton, fk: ComputedSkeleton): BodyPart? {
		val bonesTouchingFloor = fk.filter { it.key in getActiveBodyParts(inputs) }.filter { it.value.tailPosition.y <= FLOOR_TOUCH_THRESHOLD }.values
		return bonesTouchingFloor.minByOrNull { it.acceleration.lenSq() }?.bodyPart
	}
}

// Locks the hip in place
object SittingLocalizer {
	fun computeAdjustedTargetHip(targetHip: Vector3, lowestBone: BoneState?): Vector3 {
		lowestBone?.let {
			if (it.tailPosition.y < 0f) {
				return Vector3(targetHip.x, targetHip.y - it.tailPosition.y, targetHip.z)
			}
		}
		return targetHip
	}
}

// Uses Centre Of Mass to guess localization
object COMLocalizer {
	val TORSO_TRACKERS = setOf(BodyPart.UPPER_CHEST, BodyPart.LOWER_CHEST, BodyPart.UPPER_WAIST, BodyPart.LOWER_WAIST, BodyPart.HIP)

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
		val gravity = (comAccel.y - GRAVITY) * deltaTime.inFloatingSeconds

		// Add the acceleration of gravity
		return Vector3(
			comVelocity.x,
			lastCOMVelocity.y + gravity,
			comVelocity.z,
		)
	}

	fun computeTargetCOM(targetCOM: Vector3, comVelocity: Vector3, deltaTime: Duration) = targetCOM + (comVelocity * deltaTime.inFloatingSeconds)

	fun floorAdjustTargetCOM(targetCOM: Vector3, lowestBone: BoneState?) = lowestBone?.let {
		if (it.tailPosition.y < 0f) {
			Vector3(targetCOM.x, targetCOM.y - it.tailPosition.y, targetCOM.z)
		} else {
			targetCOM
		}
	} ?: targetCOM

	fun floorAdjustCOMVelocity(comVelocity: Vector3, lowestBone: BoneState?) = lowestBone?.let {
		if (it.tailPosition.y <= 0f) {
			Vector3(comVelocity.x, 0f, comVelocity.z)
		} else {
			comVelocity
		}
	} ?: comVelocity
}

fun computeTravel(current: Vector3, target: Vector3) = target - current

class LocalizerFkProcessor(val settings: Settings) :
	SkeletonFkProcessor,
	ResettableSkeletonProcessor {
	private var lastPlantedBone: BodyPart? = null
	private var targetFloor: Vector3? = null
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

		// The lowest active bone (with some exception for always-active bones)
		val lowestBone = getLowestBone(mutableInputSkeleton, fk)

		// Only follow the hip if the user's been sitting for enough time, else follow the bone on the floor
		val followSource = getSourceToFollow(fk, lowestBone).let {
			if (it == FollowSource.SITTING) {
				sittingTime += deltaTime
				if (sittingTime < SITTING_THRESHOLD) {
					FollowSource.FLOOR
				} else {
					FollowSource.SITTING
				}
			} else {
				sittingTime = Duration.ZERO
				it
			}
		}

		// Get current planted bone info and update target floor if planted bone changed
		val currentPlantedBone = FloorLocalizer.getPlantedBone(mutableInputSkeleton, fk)
		val currentFloor = fk[currentPlantedBone]?.tailPosition
		// Target floor bone changed, or we're in COM tracking, reset target to current
		if (currentPlantedBone != lastPlantedBone || followSource == FollowSource.COM) {
			lastPlantedBone = currentPlantedBone
			targetFloor = currentFloor
		}

		// Compute comVelocity and targetCom
		val currentCom = centreOfMass(fk)
		if (followSource != FollowSource.COM) targetCOM = currentCom
		previousCOMs.add(now to centreOfMass(fk))

		// Compute COM stuff
		val comAccel = COMLocalizer.computeTorsoAccel(mutableInputSkeleton)
		comVelocity = COMLocalizer.computeCOMVelocity(currentCom, previousCOMs, comVelocity, now, comAccel, deltaTime)
		targetCOM = COMLocalizer.computeTargetCOM(targetCOM, comVelocity, deltaTime)
		comVelocity = COMLocalizer.floorAdjustCOMVelocity(comVelocity, lowestBone)
		targetCOM = COMLocalizer.floorAdjustTargetCOM(targetCOM, lowestBone)

		// Get current hip position
		val currentHip = fk[BodyPart.HIP]?.tailPosition ?: Vector3.ZERO
		targetHip = if (followSource == FollowSource.SITTING) {
			SittingLocalizer.computeAdjustedTargetHip(targetHip, lowestBone)
		} else {
			currentHip
		}

		// Compute head travel for this frame depending on the source to follow
		val travel = when (followSource) {
			FollowSource.FLOOR -> {
				// Horizontal floor travel + vertical COM travel
				val floorTravel = currentFloor?.let { current ->
					targetFloor?.let { target ->
						computeTravel(current, target)
					}
				} ?: Vector3.ZERO
				val comTravel = computeTravel(currentCom, targetCOM)
				Vector3(floorTravel.x, comTravel.y, floorTravel.z)
			}

			FollowSource.COM -> computeTravel(currentCom, targetCOM)

			FollowSource.SITTING -> computeTravel(currentHip, targetHip)
		}

		// Update head position from travel
		val newHeadPosition = (headInput.position ?: Vector3.ZERO) + travel
		mutableInputSkeleton[BodyPart.HEAD] = headInput.copy(position = newHeadPosition)
	}

	override fun reset(resetType: ResetType) {
		if (resetType == ResetType.FULL) {
			lastPlantedBone = null
			targetFloor = null
			sittingTime = Duration.ZERO
			targetHip = Vector3.ZERO
			previousCOMs.clear()
			comVelocity = Vector3.ZERO
			targetCOM = Vector3.ZERO
			lastProcessTime = timeSource.markNow()
		}
	}
}
