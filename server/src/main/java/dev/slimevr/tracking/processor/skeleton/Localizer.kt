package dev.slimevr.tracking.processor.skeleton

import dev.slimevr.tracking.trackers.Tracker
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlin.math.*

/* Handles localizing the skeleton in 3d space when no 6dof device is present.
 * This is accomplished by using the foot state calculated by legtweaks. This of course
 * has the problem of the true position and predicted location drifting apart over time.
 * (in the comments COM means center of mass)
 */

enum class MovmentStates {
	LEFT_LOCKED,
	RIGHT_LOCKED,
	BOTH_LOCKED,
	NONE_LOCKED,
	FOLLOW_FOOT,
	FOLLOW_COM,
	FOLLOW_SITTING,
	FOLLOW_NONE,
}

class Localizer(humanSkeleton: HumanSkeleton) {
	// hyper parameters
	companion object {
		private const val WARMUP_FRAMES = 100
		private const val MAX_FOOT_PERCENTAGE = 50.0f
		private const val MAX_ACCEL_UP = 2.0f
		private const val SITTING_KNEE_THRESHOLD = 1.1f
		private const val SITTING_EARLY = 1000f
		private const val VELOCITY_SAMPLE_RATE: Long = 50000000 // 10ms
		private const val ACCEL_UP_THRESHOLD = 1.0f
		private const val DEAD_RECON_VELOCITY_THRESHOLD = 0.5f
		private const val MAX_DEAD_RECON_SAMPLES = 1000
		private val DEFAULT_DOWN_VELOCITY: Vector3 = Vector3(0.0f, -0.5f, 0.0f)
	}

	private val skeleton: HumanSkeleton
	private val legTweaks: LegTweaks
	private val  trackerList: ArrayList<Tracker> = ArrayList()
	private var bufCur: LegTweakBuffer
	private var bufPrev: LegTweakBuffer

	init {
		skeleton = humanSkeleton
		legTweaks = skeleton.legTweaks
		bufCur = legTweaks.buffer
		bufPrev = LegTweakBuffer()

		trackerList.add(skeleton.computedHeadTracker)
		trackerList.add(skeleton.computedChestTracker)
		trackerList.add(skeleton.computedChestTracker)
		trackerList.add(skeleton.computedHipTracker)
		trackerList.add(skeleton.computedLeftElbowTracker)
		trackerList.add(skeleton.computedRightElbowTracker)
		trackerList.add(skeleton.computedLeftHandTracker)
		trackerList.add(skeleton.computedRightHandTracker)
		trackerList.add(skeleton.computedLeftKneeTracker)
		trackerList.add(skeleton.computedRightKneeTracker)
		trackerList.add(skeleton.computedLeftFootTracker)
		trackerList.add(skeleton.computedRightFootTracker)
	}

	// state variables
	private var enabled: Boolean = false
	private var targetFoot: Vector3 = Vector3.NULL
	private var currentCOM: Vector3 = Vector3.NULL
	private var targetCOM: Vector3 = Vector3.NULL
	private var targetHip: Vector3 = Vector3.NULL
	private var comVelocity: Vector3 = Vector3.NULL
	private var lastComVelocity: Vector3 = Vector3.NULL
	private var comAccel: Vector3 = Vector3.NULL
	private var plantedFoot = MovmentStates.BOTH_LOCKED
	private var worldReference = MovmentStates.FOLLOW_FOOT
	private var uncorrectedFloor = 0.0f - LegTweaks.FLOOR_CALIBRATION_OFFSET
	private var floor = 0.0f
	private var warmupFrames = 0
	private var comFrames = 0
	private var footFrames = 0
	private var sittingFrames = 0

	// travel from different sources
	private var footTravel: Vector3 = Vector3.NULL
	private var comTravel: Vector3 = Vector3.NULL
	private var sittingTravel: Vector3 = Vector3.NULL

	fun getEnabled(): Boolean {
		return enabled
	}

	fun setEnabled(enabled: Boolean) {
		this.enabled = enabled
		legTweaks.setLocalizerMode(enabled)
	}

	fun update() {
		if (!enabled) {
			return
		}

		// if there is a 6dof device just use it
		if (skeleton.headTracker != null && !skeleton.headTracker.isImu()) {
			return
		}

		// check if the tracker list is populated
		updateTrackerList()

		// set the acceleration of the com for this frame
		comAccel = getTorsoAccel()

		if (warmupFrames < WARMUP_FRAMES) {
			comVelocity = Vector3.NULL
		}
		warmupFrames++

		// set the buffers for easy access
		bufCur = legTweaks.buffer
		bufPrev = bufCur.parent

		var finalTravel = Vector3.NULL

		// get the movement of the skeleton by foot travel
		footTravel = getPlantedFootTravel()

		// get the movement of the skeleton by the previous COM velocity
		comTravel = getCOMTravel()

		// sitting travel
		sittingTravel = getSittingTravel()

		// get the metric that this frame should rely on
		worldReference = getWorldReference()

		// update the final travel vector
		if (worldReference == MovmentStates.FOLLOW_FOOT || warmupFrames < WARMUP_FRAMES) {
			finalTravel = footTravel
		} else if (worldReference == MovmentStates.FOLLOW_COM) {
			finalTravel = comTravel
		} else if (worldReference == MovmentStates.FOLLOW_SITTING) {
			finalTravel = Vector3(
				sittingTravel.x,
				if (finalTravel.y < 0.0f) finalTravel.y else 0.0f,
				sittingTravel.z
			)
		} else {
			finalTravel = Vector3.NULL
		}

		// update the y value
		if (worldReference != MovmentStates.FOLLOW_SITTING || sittingFrames < SITTING_EARLY) {
			finalTravel = Vector3(
				finalTravel.x,
				comTravel.y,
				finalTravel.z
			)
		}

		// update the skeletons root position
		updateSkeletonPos(finalTravel)
	}

	// resets to the starting position
	fun reset() {
		skeleton.hmdNode.localTransform.translation = Vector3.NULL

		// right after reseting the localizer, the foot is not initialized
		plantedFoot = MovmentStates.NONE_LOCKED

		// reset the velocity
		comVelocity = Vector3.NULL

		// when localizing without a 6 dof device we choose the floor level
		// 0 happens to be an easy number to use
		legTweaks.setFloorLevel(0.0f)
		floor = 0.0f
		uncorrectedFloor = 0.0f - LegTweaks.FLOOR_CALIBRATION_OFFSET
		warmupFrames = 0
	}

	private fun getPlantedFoot(): MovmentStates {
		// return the foot planted if a certain state is active
		if (bufCur.leftLegState == LegTweakBuffer.LOCKED &&
			bufCur.rightLegState == LegTweakBuffer.LOCKED
		) {
			return MovmentStates.BOTH_LOCKED
		}

		// the integer value of the state has very low false positives so listen
		// to it
		if (bufCur.leftLegState == LegTweakBuffer.LOCKED) return MovmentStates.LEFT_LOCKED
		if (bufCur.rightLegState == LegTweakBuffer.LOCKED) return MovmentStates.RIGHT_LOCKED

		// if the state is not locked, use the numerical state to determine a
		// foot to follow
		return if (bufCur.leftLegNumericalState < bufCur.rightLegNumericalState &&
			bufCur.leftLegNumericalState < MAX_FOOT_PERCENTAGE &&
			bufCur.leftFootAcceleration.y < MAX_ACCEL_UP
		) {
			return MovmentStates.LEFT_LOCKED
		} else if (bufCur.rightLegNumericalState < bufCur.leftLegNumericalState &&
			bufCur.rightLegNumericalState < MAX_FOOT_PERCENTAGE &&
			bufCur.rightFootAcceleration.y < MAX_ACCEL_UP
		) {
			MovmentStates.RIGHT_LOCKED
		} else {
			MovmentStates.NONE_LOCKED
		}
	}

	// check if the foot that is planted is actually planted
	private fun getWorldReference(): MovmentStates {
		// check for sitting position
		if (isUserSitting()) {
			return MovmentStates.FOLLOW_SITTING
		}

		// if the foot is not on the ground, use the COM
		return if (!isFootOnGround()) {
			MovmentStates.FOLLOW_COM
		} else {
			MovmentStates.FOLLOW_FOOT
		}
	}

	// get the foot or feet that are planted
	// also sets the planted foot, foot init, and target pos variables
	private fun getPlantedFootTravel(): Vector3 {
		// get the foot that is planted
		var foot: MovmentStates = getPlantedFoot()

		// both feet being locked is treated as the locked foot not changing
		if (foot == MovmentStates.BOTH_LOCKED) {
			// if neither foot was locked, default to the left
			// this should be unlikely to happen
			if (plantedFoot === MovmentStates.NONE_LOCKED) plantedFoot = MovmentStates.LEFT_LOCKED
			foot = MovmentStates.LEFT_LOCKED
		}
		if (foot == MovmentStates.LEFT_LOCKED) {
			val footLoc: Vector3 = bufCur.leftFootPosition
			updateTargetPos(footLoc, foot)
			return getFootTravel(footLoc)
		} else if (foot == MovmentStates.RIGHT_LOCKED) {
			val footLoc: Vector3 = bufCur.rightFootPosition
			updateTargetPos(footLoc, foot)
			return getFootTravel(footLoc)
		}
		return Vector3.NULL
	}

	// get the travel of a foot over a frame
	private fun getFootTravel(loc: Vector3): Vector3 {
		return loc.minus(targetFoot)
	}

	// update the target position of the foot
	private fun updateTargetPos(loc: Vector3, foot: MovmentStates) {
		if (foot == plantedFoot) {
			if (worldReference === MovmentStates.FOLLOW_COM) {
				targetFoot = loc
			}
		} else {
			plantedFoot = foot
			targetFoot = loc
		}
	}

	// get the sitting travel (just prevents hip movement)
	private fun getSittingTravel(): Vector3 {
		// calculate the sitting travel by computing the movement to keep the
		// waist
		// at the same location
		val hip: Vector3 = skeleton.computedHipTracker.position

		// get the distance to move the waist to the target waist
		val dist: Vector3 = hip.minus(targetHip)

		// if the world reference is not sitting update the target waist
		if (worldReference !== MovmentStates.FOLLOW_SITTING || sittingFrames < SITTING_EARLY) {
			targetHip = hip
		}
		return dist
	}

	// returns the travel of the COM from its last position
	private fun getCOMTravel(): Vector3 {
		// update COM attributes
		updateCOMAttributes()

		var dist: Vector3 = bufCur.centerOfMass
		dist = dist.minus(targetCOM)

		return dist
	}

	// get the average velocity of the COM, the dead reckoned velocity, and
	// the acceleration of the COM assuming the COM is moving at the
	// average velocity
	private fun updateCOMAttributes() {
		deadReconCOMVelocity()
		getCOMVelocity()
		updateTargetCOM()

		// update how long the COM has been the reference and how long the foot
		// has been
		comFrames = if (worldReference === MovmentStates.FOLLOW_COM) comFrames + 1 else 0
		footFrames = if (worldReference === MovmentStates.FOLLOW_FOOT) footFrames + 1 else 0
		sittingFrames = if (worldReference === MovmentStates.FOLLOW_SITTING) sittingFrames + 1 else 0
	}

	// gets the position the COM should be at based on the velocity of the com and
	// the location of the floor
	private fun updateTargetCOM() {
		// if not in COM tracking mode, just use the current COM
		if (worldReference === MovmentStates.FOLLOW_FOOT || worldReference === MovmentStates.FOLLOW_SITTING) {
			targetCOM = bufCur.centerOfMass
		} else {
			// if the COM is the reference point, use the dead reckoned COM
			currentCOM = targetCOM
		}

		targetCOM = targetCOM.plus(comVelocity.div(bufCur.timeDelta))

		// correct any clipping through the floor
		val lowTracker = getLowestTracker()

		println(targetCOM)

		// update the target COM and velocity to reflect this new distance
		if (lowTracker.position.y < uncorrectedFloor) {
			targetCOM = Vector3(targetCOM.x, targetCOM.y + (uncorrectedFloor - lowTracker.position.y), targetCOM.z)
			comVelocity = Vector3(comVelocity.x, 0.0f, comVelocity.z)
		}
	}

	// get the velocity of the COM
	private fun getCOMVelocity(): Vector3 {
		// TODO make this more sophisticated
		val comY = comVelocity.y

		if (worldReference === MovmentStates.FOLLOW_FOOT || worldReference === MovmentStates.FOLLOW_SITTING) {
			// if the foot is the reference point use the in world velocity
			// get the average velocity over the last VELOCITY_SAMPLE_RATE (this
			// smooths out the velocity)
			var buf = bufCur
			val timeStart: Long = buf.timeOfFrame
			var timeEnd = timeStart - VELOCITY_SAMPLE_RATE
			val comPosStart: Vector3 = buf.centerOfMass

			// get the buffer that occurred VELOCITY_SAMPLE_RATE ago in time
			while (buf.timeOfFrame > timeEnd && buf.parent != null) {
				buf = buf.parent
			}

			val comPosEnd: Vector3 = buf.centerOfMass
			timeEnd = buf.timeOfFrame

			// calculate the velocity
			comVelocity = comPosStart.minus(comPosEnd).div((timeStart - timeEnd) / LegTweakBuffer.NS_CONVERT)
		}

		// add the acceleration of gravity
		comVelocity = Vector3(
			comVelocity.x,
			comY - (LegTweakBuffer.GRAVITY_MAGNITUDE / bufCur.timeDelta),
			comVelocity.z)

		return comVelocity
	}

	// dead reckon the COM velocity to determine the target COM
	private fun deadReconCOMVelocity(): Vector3 {
		// TODO use ml??
		return Vector3.NULL
	}

	// returns true if either foot is below 0.0
	private fun isFootOnGround(): Boolean {
		return (
			bufCur.leftFootPosition.y <= floor ||
				bufCur.rightFootPosition.y <= floor
			)
	}

	// returns the tracker closest to or the furthest in the ground
	// (a negative return value implies a tracker is in the ground)
	private fun getLowestTracker(): Tracker {
		var minVal = 9999f
		var tempVal: Float
		var retVal: Tracker = trackerList[0]
		for (tracker in trackerList) {
			if (tracker == null) // this is needed the IDE lies
				continue

			// get the max distance to the ground
			tempVal = tracker.position.y - uncorrectedFloor
			if (tempVal < minVal) {
				minVal = tempVal
				retVal = tracker
			}
		}

		return retVal
	}

	// returns true if the user is likely sitting
	// (assumes the floor is flat at 0.0)
	private fun isUserSitting(): Boolean {
		// based on the waist to knee vector decide if the user is sitting or
		// standing (ie, if the user is sitting the vector will be pointing off
		// to the side for both feet)
		var leftKnee: Vector3 = bufCur.leftKneePosition
		var rightKnee: Vector3 = bufCur.rightKneePosition
		val hip: Vector3 = skeleton.computedHipTracker.position
		leftKnee = hip.minus(leftKnee)
		rightKnee = hip.minus(rightKnee)

		// if the y component of the vectors is small then the user is probably
		// sitting
		var left = false
		var right = false
		if (leftKnee.y * SITTING_KNEE_THRESHOLD < leftKnee.x + leftKnee.z) {
			left = true
		}
		if (rightKnee.y * SITTING_KNEE_THRESHOLD < rightKnee.x + rightKnee.z) {
			right = true
		}
		return !bufCur.isStanding || left && right
	}

	// get the acceleration of the tracker that should be closest to the COM
	// TODO replace this with something better
	private fun getTorsoAccel(): Vector3 {
		var accel = Vector3.NULL
		if (skeleton.waistTracker != null) {
			accel = skeleton.waistTracker.acceleration
		} else if (skeleton.hipTracker != null) {
			accel = skeleton.hipTracker.acceleration
		} else if (skeleton.chestTracker != null) {
			accel = skeleton.chestTracker.acceleration
		}
		return accel
	}

	// updates the tracker list with new trackers if available
	private fun updateTrackerList() {
		if (skeleton.computedHeadTracker != null) {
			trackerList[0] = skeleton.computedHeadTracker
		}
		if (skeleton.computedChestTracker != null) {
			trackerList[1] = skeleton.computedChestTracker
		}
		if (skeleton.computedHipTracker != null) {
			trackerList[2] = skeleton.computedHipTracker
		}
		if (skeleton.computedLeftElbowTracker != null) {
			trackerList[3] = skeleton.computedLeftElbowTracker
		}
		if (skeleton.computedRightElbowTracker != null) {
			trackerList[4] = skeleton.computedRightElbowTracker
		}
		if (skeleton.computedLeftHandTracker != null) {
			trackerList[5] = skeleton.computedLeftHandTracker
		}
		if (skeleton.computedRightHandTracker != null) {
			trackerList[6] = skeleton.computedRightHandTracker
		}
		if (skeleton.computedLeftKneeTracker != null) {
			trackerList[7] = skeleton.computedLeftKneeTracker
		}
		if (skeleton.computedRightKneeTracker != null) {
			trackerList[8] = skeleton.computedRightKneeTracker
		}
		if (skeleton.computedLeftFootTracker != null) {
			trackerList[9] = skeleton.computedLeftFootTracker
		}
		if (skeleton.computedRightFootTracker != null) {
			trackerList[10] = skeleton.computedRightFootTracker
		}
	}

	// update the hmd position and rotation
	private fun updateSkeletonPos(travel: Vector3) {
		var rot = Quaternion.IDENTITY
		if (skeleton.headTracker != null) {
			rot = skeleton.headTracker.getRotation()
		}
		val temp = skeleton.hmdNode.localTransform.translation.minus(travel)

		skeleton.hmdNode.localTransform.translation = temp
		skeleton.hmdNode.localTransform.rotation = rot
	}
}
