package dev.slimevr.vr.processor;


import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;


public class ClipCorrection {
	private float floorLevel;
	private float maxDynamicDisplacement = 0.065f;
	private boolean initialized = true;

	private final Vector3f normal = new Vector3f(0, 0, -1);

	// variables for holding relavant leg data
	// TODO make these private
	public Vector3f leftFootPosition = new Vector3f();
	public Vector3f rightFootPosition = new Vector3f();
	public Vector3f leftKneePosition = new Vector3f();
	public Vector3f rightKneePosition = new Vector3f();
	public Vector3f waistPosition = new Vector3f();
	public Quaternion leftFootRotation = new Quaternion();
	public Quaternion rightFootRotation = new Quaternion();

	public ClipCorrection(float floorLevel) {
		this.floorLevel = floorLevel;
	}

	public double getFloorLevel() {
		return floorLevel;
	}

	public void setFloorLevel(float floorLevel) {
		this.floorLevel = floorLevel;
	}

	public void resetFloorLevel() {
		this.initialized = false;
	}

	// returns true if the foot is clipped and false if it is not
	public boolean isClipped(float leftOffset, float rightOffset) {
		return (leftFootPosition.y < floorLevel + leftOffset
			|| rightFootPosition.y < floorLevel + rightOffset);
	}

	// returns true if the tracker positions should be corrected with the values
	// stored in the class
	public boolean correctClipping() {
		// if we are not initialized, we need to initialize the floor level
		if (!this.initialized) {
			this.floorLevel = (leftFootPosition.y + rightFootPosition.y) / 2f + 0.025f;
			this.initialized = true;
		}
		// calculate how angled down the feet are as a scalar value between 0
		// and 1 (0 = flat, 1 = max angle)
		float leftOffset = getLeftFootOffset();
		float rightOffset = getRightFootOffset();

		// if there is no clipping, return false
		if (!isClipped(leftOffset, rightOffset)) {
			return false;
		}

		// calculate the current triangles of waist knees and feet


		if (this.leftFootPosition.y < floorLevel + (maxDynamicDisplacement * leftOffset)) {
			this.leftFootPosition.y = floorLevel + (maxDynamicDisplacement * leftOffset);
		}
		if (this.rightFootPosition.y < floorLevel + (maxDynamicDisplacement * rightOffset)) {
			this.rightFootPosition.y = floorLevel + (maxDynamicDisplacement * rightOffset);
		}

		// calculate the new triangles of waist knees and feet (calculate new
		// knee position)


		return true;
	}

	private float getLeftFootOffset() {
		float offset = computeUnitVector(this.leftFootRotation).y;
		if (offset > 0) {
			return 0;
		}
		return offset * -1;
	}

	private float getRightFootOffset() {
		float offset = computeUnitVector(this.rightFootRotation).y;
		if (offset > 0) {
			return 0;
		}
		return offset * -1;
	}

	// get the z component of the unit vector of the given quaternion
	private Vector3f computeUnitVector(Quaternion quaternion) {
		return quaternion.mult(normal).normalize();
	}
}
