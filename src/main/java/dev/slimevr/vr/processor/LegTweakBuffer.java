package dev.slimevr.vr.processor;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;


// class that holds data related to the state and other variuse attributes of the legs
// such as the position of the foot, knee, and waist, after and before correction,
// the velocity of the foot and the computed state of the feet at that frame.
// mainly calculates the state of the legs per frame using these rules:
// The conditions for an unlock are as follows:
// 1. the foot is to far from its correct position
// 2. a velocity higher than a threashold is achived
// 3. a large acceleration is applied to the foot
// 4. angular velocity of the foot goes higher than a threashold
// The conditions for a lock are the opposite of the above


public class LegTweakBuffer {

	public static final int STATE_UNKNOWN = 0; // fall back state
	public static final int LOCKED = 1;
	public static final int UNLOCKED = 2;

	// states for the legs
	private int leftLegState = STATE_UNKNOWN;
	private int rightLegState = STATE_UNKNOWN;

	// positions and rotations
	private Vector3f leftFootPosition = new Vector3f();
	private Vector3f rightFootPosition = new Vector3f();
	private Vector3f leftKneePosition = new Vector3f();
	private Vector3f rightKneePosition = new Vector3f();
	private Vector3f waistPosition = new Vector3f();
	private Quaternion leftFootRotation = new Quaternion();
	private Quaternion rightFootRotation = new Quaternion();

	private Vector3f leftFootPositionCorrected = new Vector3f();
	private Vector3f rightFootPositionCorrected = new Vector3f();
	private Vector3f leftKneePositionCorrected = new Vector3f();
	private Vector3f rightKneePositionCorrected = new Vector3f();
	private Vector3f waistPositionCorrected = new Vector3f();

	// velocities
	private Vector3f leftFootVelocity = new Vector3f();
	private float leftFootVelocityMagnitude = 0;
	private Vector3f rightFootVelocity = new Vector3f();
	private float rightFootVelocityMagnitude = 0;
	private float leftFootAngleDiff = 0;
	private float rightFootAngleDiff = 0;

	// acceleration
	private Vector3f leftFootAcceleration = new Vector3f();
	private float leftFootAccelerationMagnitude = 0;
	private Vector3f rightFootAcceleration = new Vector3f();
	private float rightFootAccelerationMagnitude = 0;

	// other data
	private long timeOfFrame = System.nanoTime();
	private LegTweakBuffer parent = null; // frame before this one
	private int frameNumber = 0; // higher number is older frame
	private float leftFloorLevel;
	private float rightFloorLevel;

	// hyperparameters
	public static final float SKATING_CUTOFF = 0.275f;
	private static final float SKATING_VELOCITY_CUTOFF = 3.20f;
	private static final float SKATING_ACCELERATION_CUTOFF = 5.20f;
	private static final float SKATING_ROTATIONAL_VELOCITY_CUTOFF = 1.5f;
	private static final float SKATING_LOCK_ENGAGE_PERCENT = 0.5f;
	private static final float FLOOR_DIF_CUTOFF = 0.06f;

	private static final float SKATING_CUTOFF_ENGAGE = SKATING_CUTOFF
		* SKATING_LOCK_ENGAGE_PERCENT;
	private static final float SKATING_VELOCITY_CUTOFF_ENGAGE = SKATING_VELOCITY_CUTOFF
		* SKATING_LOCK_ENGAGE_PERCENT;
	private static final float SKATING_ACCELERATION_CUTOFF_ENGAGE = SKATING_ACCELERATION_CUTOFF
		* SKATING_LOCK_ENGAGE_PERCENT;
	private static final float SKATING_ROTATIONAL_VELOCITY_CUTOFF_ENGAGE = SKATING_ROTATIONAL_VELOCITY_CUTOFF
		* SKATING_LOCK_ENGAGE_PERCENT;

	// getters and setters
	public Vector3f getLeftFootPosition() {
		return leftFootPosition.clone();
	}

	public void setLeftFootPosition(Vector3f leftFootPosition) {
		this.leftFootPosition = leftFootPosition.clone();
	}

	public Vector3f getRightFootPosition() {
		return rightFootPosition.clone();
	}

	public void setRightFootPosition(Vector3f rightFootPosition) {
		this.rightFootPosition = rightFootPosition.clone();
	}

	public Vector3f getLeftKneePosition() {
		return leftKneePosition.clone();
	}

	public void setLeftKneePosition(Vector3f leftKneePosition) {
		this.leftKneePosition = leftKneePosition.clone();
	}

	public Vector3f getRightKneePosition() {
		return rightKneePosition.clone();
	}

	public void setRightKneePosition(Vector3f rightKneePosition) {
		this.rightKneePosition = rightKneePosition.clone();
	}

	public Vector3f getWaistPosition() {
		return waistPosition.clone();
	}

	public void setWaistPosition(Vector3f waistPosition) {
		this.waistPosition = waistPosition.clone();
	}

	public Quaternion getLeftFootRotation() {
		return leftFootRotation.clone();
	}

	public void setLeftFootRotation(Quaternion leftFootRotation) {
		this.leftFootRotation = leftFootRotation.clone();
	}

	public Quaternion getRightFootRotation() {
		return rightFootRotation.clone();
	}

	public void setRightFootRotation(Quaternion rightFootRotation) {
		this.rightFootRotation = rightFootRotation.clone();
	}

	public Vector3f getLeftFootPositionCorrected() {
		return leftFootPositionCorrected.clone();
	}

	public void setLeftFootPositionCorrected(Vector3f leftFootPositionCorrected) {
		this.leftFootPositionCorrected = leftFootPositionCorrected.clone();
	}

	public Vector3f getRightFootPositionCorrected() {
		return rightFootPositionCorrected.clone();
	}

	public void setRightFootPositionCorrected(Vector3f rightFootPositionCorrected) {
		this.rightFootPositionCorrected = rightFootPositionCorrected.clone();
	}

	public Vector3f getLeftKneePositionCorrected() {
		return leftKneePositionCorrected.clone();
	}

	public void setLeftKneePositionCorrected(Vector3f leftKneePositionCorrected) {
		this.leftKneePositionCorrected = leftKneePositionCorrected.clone();
	}

	public Vector3f getRightKneePositionCorrected() {
		return rightKneePositionCorrected.clone();
	}

	public void setRightKneePositionCorrected(Vector3f rightKneePositionCorrected) {
		this.rightKneePositionCorrected = rightKneePositionCorrected.clone();
	}

	public Vector3f getWaistPositionCorrected() {
		return waistPositionCorrected.clone();
	}

	public void setWaistPositionCorrected(Vector3f waistPositionCorrected) {
		this.waistPositionCorrected = waistPositionCorrected.clone();
	}

	public Vector3f getLeftFootVelocity() {
		return leftFootVelocity.clone();
	}

	public Vector3f getRightFootVelocity() {
		return rightFootVelocity.clone();
	}

	public void setLeftFloorLevel(float leftFloorLevel) {
		this.leftFloorLevel = leftFloorLevel;
	}

	public void setRightFloorLevel(float rightFloorLevel) {
		this.rightFloorLevel = rightFloorLevel;
	}

	public int getLeftLegState() {
		return leftLegState;
	}

	public int getRightLegState() {
		return rightLegState;
	}

	public void setParent(LegTweakBuffer parent) {
		this.parent = parent;
	}

	public LegTweakBuffer getParent() {
		return parent;
	}

	// calculate momvent attributes
	public void calculateFootAttributes(boolean active) {
		updateFrameNumber(0);

		// compute attributes of the legs
		computeVelocity();
		computeAcceleration();

		// if correction is inactive state is unknown and legs are both unlocked
		if (!active) {
			leftLegState = UNLOCKED;
			rightLegState = UNLOCKED;
		} else {
			computeState();
		}
	}

	// update the frame number of all the frames
	public void updateFrameNumber(int frameNumber) {
		this.frameNumber = frameNumber;
		if (this.frameNumber >= 2) {
			this.parent = null; // once a frame is 2 frames old, it is no longer
								// needed
		}
		if (parent != null) {
			parent.updateFrameNumber(frameNumber + 1);
		}
	}

	// compute the state of the legs
	private void computeState() {
		// based on the last state of the legs compute their state for this
		// individual frame
		leftLegState = checkStateLeft();

		rightLegState = checkStateRight();
	}

	// check if a locked foot should stay locked or be released
	private int checkStateLeft() {
		float timeStep = 1.0f / ((timeOfFrame - parent.timeOfFrame) / 1000000000.0f);
		if (parent.leftLegState == UNLOCKED) {
			if (
				parent.getLeftFootHorizantalDifference() > SKATING_CUTOFF_ENGAGE
					|| leftFootVelocityMagnitude * timeStep > SKATING_VELOCITY_CUTOFF_ENGAGE
					|| leftFootAccelerationMagnitude * timeStep > SKATING_ACCELERATION_CUTOFF_ENGAGE
					|| leftFootAngleDiff * timeStep > SKATING_ROTATIONAL_VELOCITY_CUTOFF_ENGAGE
					|| leftFootPosition.y > leftFloorLevel + FLOOR_DIF_CUTOFF
			) {
				return UNLOCKED;
			}
			return LOCKED;

		} else {
			if (
				parent.getLeftFootHorizantalDifference() > SKATING_CUTOFF
					|| leftFootVelocityMagnitude * timeStep > SKATING_VELOCITY_CUTOFF
					|| leftFootAccelerationMagnitude * timeStep > SKATING_ACCELERATION_CUTOFF
					|| leftFootAngleDiff * timeStep > SKATING_ROTATIONAL_VELOCITY_CUTOFF
					|| leftFootPosition.y > leftFloorLevel + FLOOR_DIF_CUTOFF
			) {
				return UNLOCKED;
			}
			return LOCKED;
		}
	}

	// check if a locked foot should stay locked or be released
	private int checkStateRight() {
		float timeStep = 1.0f / ((timeOfFrame - parent.timeOfFrame) / 1000000000.0f);
		if (parent.rightLegState == UNLOCKED) {
			if (
				parent.getRightFootHorizantalDifference() > SKATING_CUTOFF_ENGAGE
					|| rightFootVelocityMagnitude * timeStep > SKATING_VELOCITY_CUTOFF_ENGAGE
					|| rightFootAccelerationMagnitude * timeStep
						> SKATING_ACCELERATION_CUTOFF_ENGAGE
					|| rightFootAngleDiff * timeStep > SKATING_ROTATIONAL_VELOCITY_CUTOFF_ENGAGE
					|| rightFootPosition.y > rightFloorLevel + FLOOR_DIF_CUTOFF
			) {
				return UNLOCKED;
			}
			return LOCKED;
		} else {
			if (
				parent.getRightFootHorizantalDifference() > SKATING_CUTOFF
					|| rightFootVelocityMagnitude * timeStep > SKATING_VELOCITY_CUTOFF
					|| rightFootAccelerationMagnitude * timeStep > SKATING_ACCELERATION_CUTOFF
					|| rightFootAngleDiff * timeStep > SKATING_ROTATIONAL_VELOCITY_CUTOFF
					|| rightFootPosition.y > rightFloorLevel + FLOOR_DIF_CUTOFF
			) {
				return UNLOCKED;
			}
			return LOCKED;
		}
	}

	// get the difference in feet position between the kinematic and corrected
	// positions of the feet disregarding vertical displacment
	private float getLeftFootHorizantalDifference() {
		return leftFootPositionCorrected.subtract(leftFootPosition).setY(0).length();
	}

	// get the difference in feet position between the kinematic and corrected
	// positions of the feet
	private float getRightFootHorizantalDifference() {
		return rightFootPositionCorrected.subtract(rightFootPosition).setY(0).length();
	}

	// get the angular velocity of the left foot (kinda we just want a scalar)
	private float getLeftFootAngularVelocity() {
		return leftFootRotation
			.getRotationColumn(2)
			.distance(parent.leftFootRotation.getRotationColumn(2));
	}

	// get the angular velocity of the right foot (kinda we just want a scalar)
	private float getRightFootAngularVelocity() {
		return rightFootRotation
			.getRotationColumn(2)
			.distance(parent.rightFootRotation.getRotationColumn(2));
	}

	// compute acceleration of the feet from the velocity of the previous frame
	private void computeAcceleration() {
		if (parent != null) {
			leftFootAcceleration = leftFootVelocity.subtract(parent.leftFootVelocity);
			leftFootAccelerationMagnitude = leftFootAcceleration.length();
			rightFootAcceleration = rightFootVelocity.subtract(parent.rightFootVelocity);
			rightFootAccelerationMagnitude = rightFootAcceleration.length();
		}
	}

	// compute the velocity of the feet from the position in the last frame
	private void computeVelocity() {
		if (parent != null) {
			leftFootVelocity = leftFootPosition.subtract(parent.leftFootPosition);
			leftFootVelocityMagnitude = leftFootVelocity.length();
			rightFootVelocity = rightFootPosition.subtract(parent.rightFootPosition);
			rightFootVelocityMagnitude = rightFootVelocity.length();
			leftFootAngleDiff = getLeftFootAngularVelocity();
			rightFootAngleDiff = getRightFootAngularVelocity();
		}
	}

}
