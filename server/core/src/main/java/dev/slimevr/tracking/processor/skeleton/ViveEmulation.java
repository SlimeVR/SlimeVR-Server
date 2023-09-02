package dev.slimevr.tracking.processor.skeleton;

import io.github.axisangles.ktmath.Quaternion;
import io.github.axisangles.ktmath.Vector3;

import java.util.Random;


// a class that emulates the oclusion issues of vive trackers
// this is done by randomly making the waist tracker fly away from the body
public class ViveEmulation {

	// skeleton
	private final HumanSkeleton skeleton;

	// random number generator
	private final Random random = new Random();

	// hyperparameters
	private static final int CHANCE = (int) 1e5;
	private static final float MAX_FLY_TIME = 3.5f;
	private static final float MIN_FLY_TIME = 0.5f;
	private static final float FLY_SPEED = 0.5f;
	private static final float FLY_SPEED_VARIANCE = 0.4f;
	private static final float FLY_SPEED_DRAG = 0.1f;
	private static final float FLY_BACK_SPEED = 20.0f;
	private static final float FLY_BACK_OVERSHOOT = 8.0f;
	private static final float MIN_OVERSHOOT_DISTANCE = 0.1f;
	private static final float MAX_OVERSHOOT_DISTANCE = 1.5f;
	private static final float SLERP_AMOUNT = 0.2f;
	private static final float NS_CONVERTER = 1e9f;
	private static final float NEARLY_ZERO = 0.01f;
	private static final Vector3 ADJUSTER = new Vector3(1.0f, 0.1f, 1.0f);

	// state variables
	private boolean enabled = false;
	private boolean flying = false;
	private boolean flyingBack = false;
	private boolean overShooting = false;
	private float flyTime = 0.0f;
	private float flyStartTime = 0.0f;
	private float flySpeed = 0.0f;
	private int ticksToFly = random.nextInt(CHANCE);
	private Vector3 flyDirection = Vector3.Companion.getNULL();
	private Vector3 lastPosition = Vector3.Companion.getNULL();
	private Vector3 targetPosition = Vector3.Companion.getNULL();
	private Quaternion randomRotation = Quaternion.Companion.getIDENTITY();
	private Quaternion lastRotation = Quaternion.Companion.getIDENTITY();
	private long time = System.nanoTime();
	private float timeDelta = 0.0f;

	public ViveEmulation(HumanSkeleton skeleton) {
		this.skeleton = skeleton;
		if (skeleton.getComputedHipTracker() != null)
			this.lastPosition = skeleton.getComputedHipTracker().getPosition();
	}

	public boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	// this method is called every frame
	// it will randomly make the waist tracker fly away
	public void update() {
		// if the skeleton is not enabled or the waist tracker is not present,
		// we do nothing
		if (!enabled || skeleton.getComputedHipTracker() == null)
			return;

		// update state
		ticksToFly--;
		long newTime = System.nanoTime();
		timeDelta = (System.nanoTime() - time) / NS_CONVERTER;
		time = newTime;

		// in the flying state update the tracker each frame
		if (flying) {
			if (System.nanoTime() - flyStartTime > flyTime) {
				flying = false;
				flyingBack = true;
				overShooting = true;
				flySpeed = FLY_BACK_SPEED;
				targetPosition = getOvershootPosition();
				return;
			}
			skeleton.getComputedHipTracker().setPosition(getFlyingPos());
			skeleton.getComputedHipTracker().setRotation(getFlyingRotation());
		}
		// in this state the tracker is returning to its position but has not
		// yet overshot its target
		else if (flyingBack && overShooting) {
			if (lastPosition.minus(targetPosition).lenSq() < NEARLY_ZERO * NEARLY_ZERO) {
				overShooting = false;
				flySpeed = FLY_BACK_OVERSHOOT;
				targetPosition = skeleton.getComputedHipTracker().getPosition();
				return;
			}
			skeleton.getComputedHipTracker().setPosition(getFlyingBackPos());
		}
		// in this state the tracker will return to its original position
		else if (flyingBack) {
			if (
				lastPosition.minus(skeleton.getComputedHipTracker().getPosition()).len()
					< NEARLY_ZERO
			) {
				flyingBack = false;
				ticksToFly = random.nextInt(CHANCE);
				return;
			}
			targetPosition = skeleton.getComputedHipTracker().getPosition();
			skeleton.getComputedHipTracker().setPosition(getFlyingBackPos());
		}

		// if the tracker is not flying, we check if it should fly
		// if it should, we set the state variables accordingly
		if (ticksToFly <= 0 && !flying && !flyingBack) {
			flying = true;
			flyDirection = new Vector3(
				random.nextFloat() - 0.5f,
				random.nextFloat() - 0.5f,
				random.nextFloat() - 0.5f
			)
				.unit();
			flyTime = (random.nextFloat() * (MAX_FLY_TIME - MIN_FLY_TIME) + MIN_FLY_TIME)
				* NS_CONVERTER;
			flySpeed = (random.nextFloat() * FLY_SPEED_VARIANCE + FLY_SPEED);
			flyStartTime = System.nanoTime();
			randomRotation = new Quaternion(
				random.nextFloat(),
				random.nextFloat(),
				random.nextFloat(),
				random.nextFloat()
			).unit();
			lastRotation = skeleton.getComputedHipTracker().getRotation();
			lastPosition = skeleton.getComputedHipTracker().getPosition();
		}
	}

	// returns the position of the waist tracker when it is flying
	// and update flySpeed and last position
	private Vector3 getFlyingPos() {
		lastPosition = lastPosition.plus(flyDirection.times(flySpeed * timeDelta).cross(ADJUSTER));
		flySpeed -= FLY_SPEED_DRAG * timeDelta;

		if (flySpeed < 0.0f)
			flySpeed = 0.0f;

		return lastPosition;
	}

	// normally you would want to avoid any overshooting
	// but since vive trackers do it sometimes, we do it too!
	private Vector3 getFlyingBackPos() {
		lastPosition = lastPosition
			.minus(
				lastPosition
					.minus(targetPosition)
					.unit()
					.times(flySpeed * timeDelta)
			);

		return lastPosition;
	}

	// slowly rotate the tracker as it flies away
	private Quaternion getFlyingRotation() {
		return lastRotation.interpR(randomRotation, SLERP_AMOUNT * timeDelta);
	}

	// get the position to fly back to (initially overshoot the actualy waist
	// position)
	private Vector3 getOvershootPosition() {
		return skeleton
			.getComputedHipTracker()
			.getPosition()
			.minus(
				lastPosition
					.cross(skeleton.getComputedHipTracker().getPosition())
					.unit()
					.times(
						random.nextFloat()
							* (MAX_OVERSHOOT_DISTANCE - MIN_OVERSHOOT_DISTANCE)
							+ MIN_OVERSHOOT_DISTANCE
					)
			);
	}
}
