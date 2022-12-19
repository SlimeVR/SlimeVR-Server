package dev.slimevr.vr.processor.skeleton;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import java.util.Random;


// a class that emulates the oclusion issues of vive trackers
// this is done by randomly making the waist tracker fly away from the body
public class ViveEmulation {

	// skeleton
	private HumanSkeleton skeleton;

	// random number generator
	private Random random = new Random();

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
	private static final Vector3f ADJUSTER = new Vector3f(1.0f, 0.1f, 1.0f);

	// state variables
	private boolean enabled = false;
	private boolean flying = false;
	private boolean flyingBack = false;
	private boolean overShooting = false;
	private float flyTime = 0.0f;
	private float flyStartTime = 0.0f;
	private float flySpeed = 0.0f;
	private int ticksToFly = random.nextInt(CHANCE);
	private Vector3f flyDirection = new Vector3f();
	private Vector3f lastPosition = new Vector3f();
	private Vector3f targetPosition = new Vector3f();
	private Quaternion randomRotation = new Quaternion();
	private Quaternion lastRotation = new Quaternion();
	private long time = System.nanoTime();
	private float timeDelta = 0.0f;

	public ViveEmulation(HumanSkeleton skeleton) {
		this.skeleton = skeleton;
		if (skeleton.computedWaistTracker != null)
			this.lastPosition = skeleton.computedWaistTracker.position.clone();
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
		if (!enabled || skeleton.computedWaistTracker == null)
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
				getOvershootPosition(targetPosition);
				return;
			}
			skeleton.computedWaistTracker.position.set(getFlyingPos());
			skeleton.computedWaistTracker.rotation.set(getFlyingRotation());
		}
		// in this state the tracker is returning to its position but has not
		// yet overshot its target
		else if (flyingBack && overShooting) {
			if (targetPosition.distanceSquared(lastPosition) < NEARLY_ZERO * NEARLY_ZERO) {
				overShooting = false;
				flySpeed = FLY_BACK_OVERSHOOT;
				targetPosition.set(skeleton.computedWaistTracker.position);
				return;
			}
			skeleton.computedWaistTracker.position.set(getFlyingBackPos());
		}
		// in this state the tracker will return to its original position
		else if (flyingBack) {
			if (skeleton.computedWaistTracker.position.distance(lastPosition) < NEARLY_ZERO) {
				flyingBack = false;
				ticksToFly = random.nextInt(CHANCE);
				return;
			}
			targetPosition.set(skeleton.computedWaistTracker.position);
			skeleton.computedWaistTracker.position.set(getFlyingBackPos());
		}

		// if the tracker is not flying, we check if it should fly
		// if it should, we set the state variables accordingly
		if (ticksToFly <= 0 && !flying && !flyingBack) {
			flying = true;
			flyDirection
				.set(
					random.nextFloat() - 0.5f,
					random.nextFloat() - 0.5f,
					random.nextFloat() - 0.5f
				)
				.normalizeLocal();
			flyTime = (random.nextFloat() * (MAX_FLY_TIME - MIN_FLY_TIME) + MIN_FLY_TIME)
				* NS_CONVERTER;
			flySpeed = (random.nextFloat() * FLY_SPEED_VARIANCE + FLY_SPEED);
			flyStartTime = System.nanoTime();
			randomRotation
				.set(random.nextFloat(), random.nextFloat(), random.nextFloat(), random.nextFloat())
				.normalizeLocal();
			lastRotation.set(skeleton.computedWaistTracker.rotation);
			lastPosition.set(skeleton.computedWaistTracker.position);
		}
	}

	// returns the position of the waist tracker when it is flying
	// and update flySpeed and last position
	private Vector3f getFlyingPos() {
		lastPosition.addLocal(flyDirection.mult(flySpeed * timeDelta).mult(ADJUSTER));
		flySpeed -= FLY_SPEED_DRAG * timeDelta;

		if (flySpeed < 0.0f)
			flySpeed = 0.0f;

		return lastPosition;
	}

	// normally you would want to avoid any overshooting
	// but since vive trackers do it sometimes, we do it too!
	private Vector3f getFlyingBackPos() {
		lastPosition
			.subtractLocal(
				lastPosition
					.subtract(targetPosition)
					.normalize()
					.mult(flySpeed * timeDelta)
			);

		return lastPosition;
	}

	// slowly rotate the tracker as it flys away
	private Quaternion getFlyingRotation() {
		lastRotation.slerp(lastRotation, randomRotation, SLERP_AMOUNT * timeDelta);
		return lastRotation;
	}

	// get the position to fly back to (initially overshoot the actualy waist
	// position)
	private void getOvershootPosition(Vector3f store) {
		store
			.set(
				skeleton.computedWaistTracker.position
					.subtract(
						lastPosition
							.subtract(skeleton.computedWaistTracker.position)
							.normalize()
							.mult(
								random.nextFloat()
									* (MAX_OVERSHOOT_DISTANCE - MIN_OVERSHOOT_DISTANCE)
									+ MIN_OVERSHOOT_DISTANCE
							)
					)
			);
	}
}
