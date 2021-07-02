package io.eiren.unity;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;

import io.eiren.math.FloatMath;
import io.eiren.util.StringUtils;
import io.eiren.vr.trackers.ComputedTracker;
import io.eiren.vr.trackers.ReferenceAdjustedTracker;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests {@link ReferenceAdjustedTracker#resetYaw(Quaternion)}
 */
public class ReferenceAdjustmentsYawTests {
	
	@Test
	public void check0to0() {
		yawTest(0, 0);
	}

	@Test
	public void check45to0() {
		yawTest(0, 45);
	}

	@Test
	public void check90to0() {
		yawTest(0, 90);
	}

	@Test
	public void check180to0() {
		yawTest(0, 180);
	}

	@Test
	public void check270to0() {
		yawTest(0, 270);
	}
	
	@Test
	public void check0to45() {
		yawTest(45, 0);
	}

	@Test
	public void check45to45() {
		yawTest(45, 45);
	}

	@Test
	public void check90to45() {
		yawTest(45, 90);
	}

	@Test
	public void check180to45() {
		yawTest(45, 180);
	}

	@Test
	public void check270to45() {
		yawTest(45, 270);
	}
	
	@Test
	public void check0to90() {
		yawTest(90, 0);
	}

	@Test
	public void check45to90() {
		yawTest(90, 45);
	}

	@Test
	public void check90to90() {
		yawTest(90, 90);
	}

	@Test
	public void check180to90() {
		yawTest(90, 180);
	}

	@Test
	public void check270to90() {
		yawTest(90, 270);
	}
	
	@Test
	public void check0to180() {
		yawTest(180, 0);
	}

	@Test
	public void check45to180() {
		yawTest(180, 45);
	}

	@Test
	public void check90to180() {
		yawTest(180, 90);
	}

	@Test
	public void check180to180() {
		yawTest(180, 180);
	}

	@Test
	public void check270to180() {
		yawTest(180, 270);
	}
	
	private void yawTest(float refYaw, float trackerTaw) {
		checkReferenceAdjustmentYaw(q(0, refYaw, 0), q(0, trackerTaw, 0));
		checkReferenceAdjustmentYaw(q(0, refYaw, 15), q(0, trackerTaw, 0));
		checkReferenceAdjustmentYaw(q(15, refYaw, 0), q(0, trackerTaw, 0));
		checkReferenceAdjustmentYaw(q(15, refYaw, 15), q(0, trackerTaw, 0));
		checkReferenceAdjustmentYaw(q(0, refYaw, 0), q(15, trackerTaw, 0));
		checkReferenceAdjustmentYaw(q(0, refYaw, 15), q(0, trackerTaw, 15));
		checkReferenceAdjustmentYaw(q(15, refYaw, 0), q(15, trackerTaw, 15));
		checkReferenceAdjustmentYaw(q(15, refYaw, 15), q(0, trackerTaw, 15));
	}
	
	public static void checkReferenceAdjustmentYaw(Quaternion referenceQuat, Quaternion trackerQuat) {
		ComputedTracker tracker = new ComputedTracker("test");
		tracker.rotation.set(trackerQuat);
		ReferenceAdjustedTracker adj = new ReferenceAdjustedTracker(tracker);
		adj.resetYaw(referenceQuat);
		Quaternion read = new Quaternion();
		assertTrue("Adjusted tracker didn't return rotation", adj.getRotation(read));
		assertEquals("Adjusted quat is not equal to reference quat (" + toDegs(referenceQuat) + " vs " + toDegs(read) + ")", new QuatEqualYawWithEpsilon(referenceQuat), new QuatEqualYawWithEpsilon(read));
	}
	
	public static String toDegs(Quaternion q) {
		float[] degs = new float[3];
		q.toAngles(degs);
		return StringUtils.prettyNumber(degs[0] * FastMath.RAD_TO_DEG, 0) + "," + StringUtils.prettyNumber(degs[1] * FastMath.RAD_TO_DEG, 0) + "," + StringUtils.prettyNumber(degs[2] * FastMath.RAD_TO_DEG, 0);
	}
	
	public static Quaternion q(float pitch, float yaw, float roll) {
		return new Quaternion().fromAngles(pitch * FastMath.DEG_TO_RAD, yaw * FastMath.DEG_TO_RAD, roll * FastMath.DEG_TO_RAD);
	}
	
	private static class QuatEqualYawWithEpsilon {
		
		private final Quaternion q;
		
		public QuatEqualYawWithEpsilon(Quaternion q) {
			this.q = q;
		}
		
		@Override
		public String toString() {
			return String.valueOf(q);
		}
		
		@Override
		public int hashCode() {
			return q.hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof Quaternion)
				obj = new QuatEqualYawWithEpsilon((Quaternion) obj);
			if(!(obj instanceof QuatEqualYawWithEpsilon))
				return false;
			Quaternion q2 = ((QuatEqualYawWithEpsilon) obj).q;
			float[] degs1 = new float[3];
			q.toAngles(degs1);
			float[] degs2 = new float[3];
			q2.toAngles(degs2);
			if(degs1[1] < -FloatMath.ANGLE_EPSILON_RAD)
				degs1[1] += FastMath.TWO_PI;
			if(degs2[1] < -FloatMath.ANGLE_EPSILON_RAD)
				degs2[1] += FastMath.TWO_PI;
			return FloatMath.equalsWithEpsilon(degs1[1], degs2[1]);
		}
	}
}
