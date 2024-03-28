package dev.slimevr.tracking.processor.skeleton

import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Quaternion.Companion.IDENTITY
import io.github.axisangles.ktmath.Vector3
import io.github.axisangles.ktmath.Vector3.Companion.NULL
import java.util.*

/**
 * this is done by randomly making the waist tracker fly away from the body
 */
class ViveEmulation(
	private val skeleton: HumanSkeleton,
) {
	// random number generator
	private val random = Random()

	// state variables
	var enabled: Boolean = false
	private var flying = false
	private var flyingBack = false
	private var overShooting = false
	private var flyTime = 0.0f
	private var flyStartTime = 0.0f
	private var flySpeed = 0.0f
	private var ticksToFly = random.nextInt(CHANCE)
	private var flyDirection = NULL
	private var lastPosition = NULL
	private var targetPosition = NULL
	private var randomRotation = IDENTITY
	private var lastRotation = IDENTITY
	private var time = System.nanoTime()
	private var timeDelta = 0.0f

	init {
		if (skeleton.computedHipTracker != null) {
			this.lastPosition = skeleton.computedHipTracker!!.position
		}
	}

	// this method is called every frame
	// it will randomly make the waist tracker fly away
	fun update() {
		// if the skeleton is not enabled or the waist tracker is not present,
		// we do nothing
		if (!enabled || skeleton.computedHipTracker == null) return

		// update state
		ticksToFly--
		val newTime = System.nanoTime()
		timeDelta = (System.nanoTime() - time) / NS_CONVERTER
		time = newTime

		// in the flying state update the tracker each frame
		if (flying) {
			if (System.nanoTime() - flyStartTime > flyTime) {
				flying = false
				flyingBack = true
				overShooting = true
				flySpeed = FLY_BACK_SPEED
				targetPosition = overshootPosition
				return
			}
			skeleton.computedHipTracker!!.position = flyingPos
			skeleton.computedHipTracker!!.setRotation(flyingRotation)
		} else if (flyingBack && overShooting) {
			if ((lastPosition - targetPosition).lenSq() < NEARLY_ZERO * NEARLY_ZERO) {
				overShooting = false
				flySpeed = FLY_BACK_OVERSHOOT
				targetPosition = skeleton.computedHipTracker!!.position
				return
			}
			skeleton.computedHipTracker!!.position = flyingBackPos
		} else if (flyingBack) {
			if ((lastPosition - skeleton.computedHipTracker!!.position).len()
				< NEARLY_ZERO
			) {
				flyingBack = false
				ticksToFly = random.nextInt(CHANCE)
				return
			}
			targetPosition = skeleton.computedHipTracker!!.position
			skeleton.computedHipTracker!!.position = flyingBackPos
		}

		// if the tracker is not flying, we check if it should fly
		// if it should, we set the state variables accordingly
		if (ticksToFly <= 0 && !flying && !flyingBack) {
			flying = true
			flyDirection = Vector3(
				random.nextFloat() - 0.5f,
				random.nextFloat() - 0.5f,
				random.nextFloat() - 0.5f,
			)
				.unit()
			flyTime = (
				(random.nextFloat() * (MAX_FLY_TIME - MIN_FLY_TIME) + MIN_FLY_TIME) *
					NS_CONVERTER
				)
			flySpeed = (random.nextFloat() * FLY_SPEED_VARIANCE + FLY_SPEED)
			flyStartTime = System.nanoTime().toFloat()
			randomRotation = Quaternion(
				random.nextFloat(),
				random.nextFloat(),
				random.nextFloat(),
				random.nextFloat(),
			).unit()
			lastRotation = skeleton.computedHipTracker!!.getRotation()
			lastPosition = skeleton.computedHipTracker!!.position
		}
	}

	/**
	 * @returns the position of the waist tracker when it is flying
	 */
	private val flyingPos: Vector3
		get() {
			lastPosition += flyDirection * (flySpeed * timeDelta) cross ADJUSTER
			flySpeed -= FLY_SPEED_DRAG * timeDelta

			if (flySpeed < 0.0f) flySpeed = 0.0f

			return lastPosition
		}

	private val flyingBackPos: Vector3
		// normally you would want to avoid any overshooting
		get() {
			lastPosition -= (lastPosition - targetPosition).unit() * (flySpeed * timeDelta)
			return lastPosition
		}

	private val flyingRotation: Quaternion
		// slowly rotate the tracker as it flies away
		get() = lastRotation.interpR(randomRotation, SLERP_AMOUNT * timeDelta)

	private val overshootPosition: Vector3
		// get the position to fly back to (initially overshoot the actualy waist
		get() = skeleton
			.computedHipTracker!!
			.position -
			(
				(lastPosition cross skeleton.computedHipTracker!!.position).unit() *
					(random.nextFloat() * (MAX_OVERSHOOT_DISTANCE - MIN_OVERSHOOT_DISTANCE) + MIN_OVERSHOOT_DISTANCE)
				)

	companion object {
		// hyperparameters
		private const val CHANCE = 1e5.toInt()
		private const val MAX_FLY_TIME = 3.5f
		private const val MIN_FLY_TIME = 0.5f
		private const val FLY_SPEED = 0.5f
		private const val FLY_SPEED_VARIANCE = 0.4f
		private const val FLY_SPEED_DRAG = 0.1f
		private const val FLY_BACK_SPEED = 20.0f
		private const val FLY_BACK_OVERSHOOT = 8.0f
		private const val MIN_OVERSHOOT_DISTANCE = 0.1f
		private const val MAX_OVERSHOOT_DISTANCE = 1.5f
		private const val SLERP_AMOUNT = 0.2f
		private const val NS_CONVERTER = 1e9f
		private const val NEARLY_ZERO = 0.01f
		private val ADJUSTER = Vector3(1.0f, 0.1f, 1.0f)
	}
}
