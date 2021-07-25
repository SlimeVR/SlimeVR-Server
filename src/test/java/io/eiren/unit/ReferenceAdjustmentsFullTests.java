package io.eiren.unit;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;

import io.eiren.math.FloatMath;
import io.eiren.util.StringUtils;
import io.eiren.vr.processor.TransformNode;
import io.eiren.vr.trackers.ComputedTracker;
import io.eiren.vr.trackers.ReferenceAdjustedTracker;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link ReferenceAdjustedTracker#resetFull(Quaternion)}
 */
public class ReferenceAdjustmentsFullTests {
	
	private Set<String> testedTrackerNames = new HashSet<>();

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
	
	private void yawTest(int refYaw, int trackerYaw) {
		checkReferenceAdjustmentFull(q(0, refYaw, 0), q(0, trackerYaw, 0), refYaw, "Tracker(0," + trackerYaw + ",0/" + refYaw + ")");
		checkReferenceAdjustmentFull(q(0, refYaw, 15), q(0, trackerYaw, 0), refYaw, "Tracker(0," + trackerYaw + ",0/" + refYaw + ")");
		checkReferenceAdjustmentFull(q(15, refYaw, 0), q(0, trackerYaw, 0), refYaw, "Tracker(0," + trackerYaw + ",0/" + refYaw + ")");
		checkReferenceAdjustmentFull(q(15, refYaw, 15), q(0, trackerYaw, 0), refYaw, "Tracker(0," + trackerYaw + ",0/" + refYaw + ")");
		checkReferenceAdjustmentFull(q(0, refYaw, 0), q(15, trackerYaw, 0), refYaw, "Tracker(15," + trackerYaw + ",0/" + refYaw + ")");
		checkReferenceAdjustmentFull(q(0, refYaw, 15), q(0, trackerYaw, 15), refYaw, "Tracker(0," + trackerYaw + ",0/" + refYaw + ")");
		checkReferenceAdjustmentFull(q(15, refYaw, 0), q(15, trackerYaw, 15), refYaw, "Tracker(15," + trackerYaw + ",0/" + refYaw + ")");
		checkReferenceAdjustmentFull(q(15, refYaw, 15), q(0, trackerYaw, 15), refYaw, "Tracker(0," + trackerYaw + ",0/" + refYaw + ")");
	}
	
	public void checkReferenceAdjustmentFull(Quaternion referenceQuat, Quaternion trackerQuat, int refYaw, String name) {
		ComputedTracker tracker = new ComputedTracker("test");
		tracker.rotation.set(trackerQuat);
		ReferenceAdjustedTracker<ComputedTracker> adj = new ReferenceAdjustedTracker<>(tracker);
		adj.resetFull(referenceQuat);
		Quaternion read = new Quaternion();
		assertTrue(adj.getRotation(read), "Adjusted tracker didn't return rotation");

		// Use only yaw HMD rotation
		Quaternion targetTrackerRotation = new Quaternion(referenceQuat);
		float[] angles = new float[3];
		targetTrackerRotation.toAngles(angles);
		targetTrackerRotation.fromAngles(0, angles[1], 0);
		
		assertEquals(new QuatEqualFullWithEpsilon(read), new QuatEqualFullWithEpsilon(targetTrackerRotation),
				"Adjusted quat is not equal to reference quat (" + toDegs(targetTrackerRotation) + " vs " + toDegs(read) + ")");
		testAdjustedTracker(tracker, adj, name, refYaw);
	}
	
	private static final boolean PRINT_TEST_RESULTS = false;
	
	private static int errors = 0;
	private static int successes = 0;
	
	private void testAdjustedTracker(ComputedTracker tracker, ReferenceAdjustedTracker<ComputedTracker> adj, String name, int refYaw) {
		if(!testedTrackerNames.add(name))
			return;
		
		final Quaternion trackerBase = new Quaternion();
		trackerBase.set(tracker.rotation);
		
		Quaternion rotation = new Quaternion();
		Quaternion rotationCompare = new Quaternion();
		Quaternion read = new Quaternion();
		Quaternion diff = new Quaternion();
		float[] angles = new float[3];
		float[] anglesAdj = new float[3];
		float[] anglesDiff = new float[3];
		
		TransformNode trackerNode = new TransformNode(name, true);
		TransformNode rotationNode = new TransformNode("Rot", true);
		rotationNode.attachChild(trackerNode);
		
		trackerNode.localTransform.setRotation(trackerBase);
		
		for(int yaw = 0; yaw <= 360; yaw += 30) {
			for(int pitch = -90; pitch <= 90; pitch += 15) {
				for(int roll = -90; roll <= 90; roll += 15) {
					rotation.fromAngles(pitch * FastMath.DEG_TO_RAD, yaw * FastMath.DEG_TO_RAD, roll * FastMath.DEG_TO_RAD);
					rotationCompare.fromAngles(pitch * FastMath.DEG_TO_RAD, (yaw + refYaw) * FastMath.DEG_TO_RAD, roll * FastMath.DEG_TO_RAD);
					rotationNode.localTransform.setRotation(rotation);
					rotationNode.update();
					tracker.rotation.set(trackerNode.worldTransform.getRotation());
					tracker.rotation.toAngles(angles);
					
					adj.getRotation(read);
					read.toAngles(anglesAdj);
					
					diff.set(read).inverseLocal().multLocal(rotationCompare);
					diff.toAngles(anglesDiff);
					
					if(!PRINT_TEST_RESULTS) {
						assertTrue(FloatMath.equalsToZero(anglesDiff[0]) && FloatMath.equalsToZero(anglesDiff[1]) && FloatMath.equalsToZero(anglesDiff[2]),
								name(name, yaw, pitch, roll, angles, anglesAdj, anglesDiff));
					} else {
						if(FloatMath.equalsToZero(anglesDiff[0]) && FloatMath.equalsToZero(anglesDiff[1]) && FloatMath.equalsToZero(anglesDiff[2]))
							successes++;
						else
							errors++;
						System.out.println(name(name, yaw, pitch, roll, angles, anglesAdj, anglesDiff));
					}
				}
			}
		}
		if(PRINT_TEST_RESULTS)
			System.out.println("Errors: " + errors + ", successes: " + successes);
	}
	
	private static String name(String name, int yaw, int pitch, int roll, float[] angles, float[] anglesAdj, float[] anglesDiff) {
		return name + ". Rot: " + yaw + "/" + pitch + ". "
				+ "Angles: " + StringUtils.prettyNumber(angles[0] * FastMath.RAD_TO_DEG, 1) + "/" + StringUtils.prettyNumber(anglesAdj[0] * FastMath.RAD_TO_DEG, 1) + ", "
				+ StringUtils.prettyNumber(angles[1] * FastMath.RAD_TO_DEG, 1) + "/" + StringUtils.prettyNumber(anglesAdj[1] * FastMath.RAD_TO_DEG, 1) + ", "
				+ StringUtils.prettyNumber(angles[2] * FastMath.RAD_TO_DEG, 1) + "/" + StringUtils.prettyNumber(anglesAdj[2] * FastMath.RAD_TO_DEG, 1) + ". Diff: "
				+ StringUtils.prettyNumber(anglesDiff[0] * FastMath.RAD_TO_DEG, 1) + ", "
				+ StringUtils.prettyNumber(anglesDiff[1] * FastMath.RAD_TO_DEG, 1) + ", "
				+ StringUtils.prettyNumber(anglesDiff[2] * FastMath.RAD_TO_DEG, 1);
	}
	
	public static String toDegs(Quaternion q) {
		float[] degs = new float[3];
		q.toAngles(degs);
		return StringUtils.prettyNumber(degs[0] * FastMath.RAD_TO_DEG, 0) + "," + StringUtils.prettyNumber(degs[1] * FastMath.RAD_TO_DEG, 0) + "," + StringUtils.prettyNumber(degs[2] * FastMath.RAD_TO_DEG, 0);
	}
	
	public static Quaternion q(float pitch, float yaw, float roll) {
		return new Quaternion().fromAngles(pitch * FastMath.DEG_TO_RAD, yaw * FastMath.DEG_TO_RAD, roll * FastMath.DEG_TO_RAD);
	}
	
	public static class QuatEqualFullWithEpsilon {
		
		private final Quaternion q;
		
		public QuatEqualFullWithEpsilon(Quaternion q) {
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
				obj = new QuatEqualFullWithEpsilon((Quaternion) obj);
			if(!(obj instanceof QuatEqualFullWithEpsilon))
				return false;
			Quaternion q2 = ((QuatEqualFullWithEpsilon) obj).q;
			float[] degs1 = new float[3];
			q.toAngles(degs1);
			float[] degs2 = new float[3];
			q2.toAngles(degs2);
			if(degs1[1] < -FloatMath.ANGLE_EPSILON_RAD)
				degs1[1] += FastMath.TWO_PI;
			if(degs2[1] < -FloatMath.ANGLE_EPSILON_RAD)
				degs2[1] += FastMath.TWO_PI;
			return FloatMath.equalsWithEpsilon(degs1[0], degs2[0])
					&& FloatMath.equalsWithEpsilon(degs1[1], degs2[1])
					&& FloatMath.equalsWithEpsilon(degs1[2], degs2[2]);
		}
	}
}
