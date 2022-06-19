package dev.slimevr.vr.processor.skeleton;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;


// class that holds data related to the state and other variuse attributes of the legs
// such as the position of the foot, knee, and waist, after and before correction,
// the velocity of the foot and the computed state of the feet at that frame.
public class LegTweakBuffer {
	// define different states to be in
	public static final int STATE_UNKNOWN = 0; // fall back state
	private final int WALKING = 1; // one foot locked and the other unlocked
	private final int JUMPING = 2; // Both feet off the ground
	private final int STANDING = 3; // both feet locked and both on the ground
	private final int OTHER = 4; // any pose that does not involve the feet
									// suporting the user

	// define states for each individual leg
	private final int LOCKED_CONFIDENT = 5; // foot is locked and very near to
											// the kinamatic position
	private final int LOCKED_UNCONFIDENT = 6; // foot is locked and likly about
												// to disengage
	private final int UNLOCKED_CONFIDENT = 7; // foot is unlocked and very near
												// to the kinamatic position
												// (could lock soon)
	private final int UNLOCKED_UNCONFIDENT = 8; // foot is unlocked and
												// correcting to the kinamatic
												// position

	// states for the legs
	private int state = STATE_UNKNOWN;
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
	private Quaternion leftFootRotationCorrected = new Quaternion();
	private Quaternion rightFootRotationCorrected = new Quaternion();

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
	private LegTweakBuffer parent = null; // frame before this one
	private LegTweakBuffer child = null; // frame after this one
	private int frameNumber = 0; // frame number of this frame

	// hyperparameters
	public float distanceCutoffHorizantal = 0.1f;
	public float distanceCutoffVertical = 0.1f;
	public float velocityCutoff = 0.1f;
	public float accelerationCutoff = 0.1f;


	// constructors
	public LegTweakBuffer() {
		return;
	}

	public LegTweakBuffer(
		Vector3f leftFootPosition,
		Vector3f rightFootPosition,
		Vector3f leftKneePosition,
		Vector3f rightKneePosition,
		Vector3f waistPosition,
		Quaternion leftFootRotation,
		Quaternion rightFootRotation,
		LegTweakBuffer parent
	) {
		this.leftFootPosition = leftFootPosition;
		this.rightFootPosition = rightFootPosition;
		this.leftKneePosition = leftKneePosition;
		this.rightKneePosition = rightKneePosition;
		this.waistPosition = waistPosition;
		this.leftFootRotation = leftFootRotation;
		this.rightFootRotation = rightFootRotation;
		this.parent = parent;
		this.parent.setChild(this);
		updateFrameNumber(0);

		// compute attributes of the legs
		computeVelocity();
		computeAcceleration();
		computeState();

	}

	// set the child
	private void setChild(LegTweakBuffer child) {
		this.child = child;
	}

	// update the frame number of all the frames
	public void updateFrameNumber(int frameNumber) {
		this.frameNumber = frameNumber;
		if (this.frameNumber >= 15) {
			this.parent = null; // once a frame is 15 frames old, it is no
								// longer needed
		}
		if (parent != null) {
			parent.updateFrameNumber(frameNumber + 1);
		}
	}

	// populate the corrected positions and rotations
	public void populateCorrectedPositions(
		Vector3f leftFootPosition,
		Vector3f rightFootPosition,
		Vector3f leftKneePosition,
		Vector3f rightKneePosition,
		Vector3f waistPosition,
		Quaternion leftFootRotation,
		Quaternion rightFootRotation
	) {
		this.leftFootPositionCorrected = leftFootPosition;
		this.rightFootPositionCorrected = rightFootPosition;
		this.leftKneePositionCorrected = leftKneePosition;
		this.rightKneePositionCorrected = rightKneePosition;
		this.waistPositionCorrected = waistPosition;
		this.leftFootRotationCorrected = leftFootRotation;
		this.rightFootRotationCorrected = rightFootRotation;
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

	// compute the state of the legs
	private void computeState() {
		// if the foot was locked in the last frame compute the state

		// if the foor is not locked in the last frame, compute the state

		// if the foot just recently unlocked in the last frame, compute the
		// state

	}

	// get the difference in feet position between the kinematic and corrected
	// positions of the feet
	private Vector3f getLeftFootPositionDifference() {
		return leftFootPositionCorrected.subtract(leftFootPosition);
	}

	// get the difference in feet position between the kinematic and corrected
	// positions of the feet
	private Vector3f getRightFootPositionDifference() {
		return rightFootPositionCorrected.subtract(rightFootPosition);
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

}
