package dev.slimevr.unit;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import dev.slimevr.VRServer;
import dev.slimevr.tracking.processor.BoneType;
import dev.slimevr.tracking.processor.TransformNode;
import dev.slimevr.tracking.trackers.IMUTracker;
import io.eiren.math.FloatMath;
import io.eiren.util.StringUtils;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;


/**
 * Tests {@link IMUTracker#resetFull(Quaternion)}
 */
public class ReferenceAdjustmentsTests {

	private static final int[] yaws = { 0, 45, 90, 180, 270 };
	private static final int[] pitches = { 0, 15, 35, -15, -35 };
	private static final int[] rolls = { 0, 15, 35, -15, -35 };
	private static final boolean PRINT_TEST_RESULTS = false;
	private static int errors = 0;
	private static int successes = 0;

	public static Stream<AnglesSet> getAnglesSet() {
		return IntStream
			.of(yaws)
			.mapToObj(
				(yaw) -> IntStream
					.of(pitches)
					.mapToObj(
						(
							pitch
						) -> IntStream.of(rolls).mapToObj((roll) -> new AnglesSet(pitch, yaw, roll))
					)
			)
			.flatMap(Function.identity())
			.flatMap(Function.identity());
	}

	private static String name(
		int yaw,
		int pitch,
		int roll,
		float[] angles,
		float[] anglesAdj,
		float[] anglesDiff
	) {
		return "Rot: "
			+ yaw
			+ "/"
			+ pitch
			+ "/"
			+ roll
			+ ". "
			+ "Angles: "
			+ StringUtils.prettyNumber(angles[0] * FastMath.RAD_TO_DEG, 1)
			+ "/"
			+ StringUtils.prettyNumber(anglesAdj[0] * FastMath.RAD_TO_DEG, 1)
			+ ", "
			+ StringUtils.prettyNumber(angles[1] * FastMath.RAD_TO_DEG, 1)
			+ "/"
			+ StringUtils.prettyNumber(anglesAdj[1] * FastMath.RAD_TO_DEG, 1)
			+ ", "
			+ StringUtils.prettyNumber(angles[2] * FastMath.RAD_TO_DEG, 1)
			+ "/"
			+ StringUtils.prettyNumber(anglesAdj[2] * FastMath.RAD_TO_DEG, 1)
			+ ". Diff: "
			+ StringUtils.prettyNumber(anglesDiff[0] * FastMath.RAD_TO_DEG, 1)
			+ ", "
			+ StringUtils.prettyNumber(anglesDiff[1] * FastMath.RAD_TO_DEG, 1)
			+ ", "
			+ StringUtils.prettyNumber(anglesDiff[2] * FastMath.RAD_TO_DEG, 1);
	}

	public static Quaternion q(float pitch, float yaw, float roll) {
		return new Quaternion()
			.fromAngles(
				pitch * FastMath.DEG_TO_RAD,
				yaw * FastMath.DEG_TO_RAD,
				roll * FastMath.DEG_TO_RAD
			);
	}

	public static String toDegs(Quaternion q) {
		float[] degs = new float[3];
		q.toAngles(degs);
		return StringUtils.prettyNumber(degs[0] * FastMath.RAD_TO_DEG, 0)
			+ ","
			+ StringUtils.prettyNumber(degs[1] * FastMath.RAD_TO_DEG, 0)
			+ ","
			+ StringUtils.prettyNumber(degs[2] * FastMath.RAD_TO_DEG, 0);
	}

	@TestFactory
	Stream<DynamicTest> getTestsYaw() {
		return getAnglesSet()
			.map(
				(p) -> dynamicTest(
					"Adjustment Yaw Test of Tracker(" + p.pitch + "," + p.yaw + "," + p.roll + ")",
					() -> IntStream
						.of(yaws)
						.forEach(
							(refYaw) -> checkReferenceAdjustmentYaw(
								q(p.pitch, p.yaw, p.roll),
								0,
								refYaw,
								0
							)
						)
				)
			);
	}

	@TestFactory
	Stream<DynamicTest> getTestsFull() {
		return getAnglesSet()
			.map(
				(p) -> dynamicTest(
					"Adjustment Full Test of Tracker(" + p.pitch + "," + p.yaw + "," + p.roll + ")",
					() -> getAnglesSet()
						.forEach(
							(ref) -> checkReferenceAdjustmentFull(
								q(p.pitch, p.yaw, p.roll),
								ref.pitch,
								ref.yaw,
								ref.roll
							)
						)
				)
			);
	}

	// TODO : Test is not passing because the test is wrong
	// See issue https://github.com/SlimeVR/SlimeVR-Server/issues/55
	// @TestFactory
	Stream<DynamicTest> getTestsForRotation() {
		return getAnglesSet()
			.flatMap(
				(p) -> IntStream
					.of(yaws)
					.mapToObj(
						(refYaw) -> dynamicTest(
							"Adjustment Rotation Test of Tracker("
								+ p.pitch
								+ ","
								+ p.yaw
								+ ","
								+ p.roll
								+ "), Ref "
								+ refYaw,
							() -> testAdjustedTrackerRotation(
								q(p.pitch, p.yaw, p.roll),
								0,
								refYaw,
								0
							)
						)
					)
			);
	}

	public void checkReferenceAdjustmentFull(
		Quaternion trackerQuat,
		int refPitch,
		int refYaw,
		int refRoll
	) {
		Quaternion referenceQuat = q(refPitch, refYaw, refRoll);
		IMUTracker tracker = new IMUTracker(
			null,
			VRServer.getNextLocalTrackerId(),
			0,
			"test",
			"test",
			null,
			null
		);
		tracker.rotQuaternion.set(trackerQuat);
		tracker.resetFull(referenceQuat);
		Quaternion read = new Quaternion();
		assertTrue(tracker.getRotation(read), "Adjusted tracker didn't return rotation");

		// Use only yaw HMD rotation
		Quaternion targetTrackerRotation = new Quaternion(referenceQuat);
		float[] angles = new float[3];
		targetTrackerRotation.toAngles(angles);
		targetTrackerRotation.fromAngles(0, angles[1], 0);

		assertEquals(
			new QuatEqualFullWithEpsilon(read),
			new QuatEqualFullWithEpsilon(targetTrackerRotation),
			"Adjusted quat is not equal to reference quat ("
				+ toDegs(targetTrackerRotation)
				+ " vs "
				+ toDegs(read)
				+ ")"
		);
	}

	public void checkReferenceAdjustmentYaw(
		Quaternion trackerQuat,
		int refPitch,
		int refYaw,
		int refRoll
	) {
		// FIXME
		Quaternion referenceQuat = q(refPitch, refYaw, refRoll);
		IMUTracker tracker = new IMUTracker(
			null,
			VRServer.getNextLocalTrackerId(),
			0,
			"test",
			"test",
			null,
			null
		);
		tracker.rotQuaternion.set(trackerQuat);
		tracker.resetYaw(referenceQuat);
		Quaternion read = new Quaternion();
		assertTrue(tracker.getRotation(read), "Adjusted tracker didn't return rotation");
		assertEquals(
			new QuatEqualYawWithEpsilon(referenceQuat),
			new QuatEqualYawWithEpsilon(read),
			"Adjusted quat is not equal to reference quat ("
				+ toDegs(referenceQuat)
				+ " vs "
				+ toDegs(read)
				+ ")"
		);
	}

	private void testAdjustedTrackerRotation(
		Quaternion trackerQuat,
		int refPitch,
		int refYaw,
		int refRoll
	) {
		// FIXME
		Quaternion referenceQuat = q(refPitch, refYaw, refRoll);
		IMUTracker tracker = new IMUTracker(
			null,
			VRServer.getNextLocalTrackerId(),
			0,
			"test",
			"test",
			null,
			null
		);
		tracker.rotQuaternion.set(trackerQuat);
		tracker.resetFull(referenceQuat);

		// Use only yaw HMD rotation
		Quaternion targetTrackerRotation = new Quaternion(referenceQuat);
		float[] angles = new float[3];
		targetTrackerRotation.toAngles(angles);
		targetTrackerRotation.fromAngles(0, angles[1], 0);

		Quaternion read = new Quaternion();
		Quaternion rotation = new Quaternion();
		Quaternion rotationCompare = new Quaternion();
		Quaternion diff = new Quaternion();
		float[] anglesAdj = new float[3];
		float[] anglesDiff = new float[3];

		TransformNode trackerNode = new TransformNode(BoneType.HIP, true);
		TransformNode rotationNode = new TransformNode(BoneType.HIP, true);
		rotationNode.attachChild(trackerNode);

		trackerNode.getLocalTransform().setRotation(tracker.rotQuaternion);

		for (int yaw = 0; yaw <= 360; yaw += 30) {
			for (int pitch = -90; pitch <= 90; pitch += 15) {
				for (int roll = -90; roll <= 90; roll += 15) {
					rotation
						.fromAngles(
							pitch * FastMath.DEG_TO_RAD,
							yaw * FastMath.DEG_TO_RAD,
							roll * FastMath.DEG_TO_RAD
						);
					rotationCompare
						.fromAngles(
							pitch * FastMath.DEG_TO_RAD,
							(yaw + refYaw) * FastMath.DEG_TO_RAD,
							roll * FastMath.DEG_TO_RAD
						);
					rotationNode.getLocalTransform().setRotation(rotation);
					rotationNode.update();
					tracker.rotQuaternion.set(trackerNode.getWorldTransform().getRotation());
					tracker.rotQuaternion.toAngles(angles);

					tracker.getRotation(read);
					read.toAngles(anglesAdj);

					diff.set(read).inverseLocal().multLocal(rotationCompare);
					diff.toAngles(anglesDiff);

					if (!PRINT_TEST_RESULTS) {
						assertTrue(
							FloatMath.equalsToZero(anglesDiff[0])
								&& FloatMath.equalsToZero(anglesDiff[1])
								&& FloatMath.equalsToZero(anglesDiff[2]),
							name(yaw, pitch, roll, angles, anglesAdj, anglesDiff)
						);
					} else {
						if (
							FloatMath.equalsToZero(anglesDiff[0])
								&& FloatMath.equalsToZero(anglesDiff[1])
								&& FloatMath.equalsToZero(anglesDiff[2])
						)
							successes++;
						else
							errors++;
						System.out.println(name(yaw, pitch, roll, angles, anglesAdj, anglesDiff));
					}
				}
			}
		}
		if (PRINT_TEST_RESULTS)
			System.out.println("Errors: " + errors + ", successes: " + successes);
	}

	private record QuatEqualYawWithEpsilon(Quaternion q) {

		@Override
		public String toString() {
			return String.valueOf(q);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Quaternion)
				obj = new QuatEqualYawWithEpsilon((Quaternion) obj);
			if (!(obj instanceof QuatEqualYawWithEpsilon))
				return false;
			Quaternion q2 = ((QuatEqualYawWithEpsilon) obj).q;
			float[] degs1 = new float[3];
			q.toAngles(degs1);
			float[] degs2 = new float[3];
			q2.toAngles(degs2);
			if (degs1[1] < -FloatMath.ANGLE_EPSILON_RAD)
				degs1[1] += FastMath.TWO_PI;
			if (degs2[1] < -FloatMath.ANGLE_EPSILON_RAD)
				degs2[1] += FastMath.TWO_PI;
			return FloatMath.equalsWithEpsilon(degs1[1], degs2[1]);
		}
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
			if (obj instanceof Quaternion)
				obj = new QuatEqualFullWithEpsilon((Quaternion) obj);
			if (!(obj instanceof QuatEqualFullWithEpsilon))
				return false;
			Quaternion q2 = ((QuatEqualFullWithEpsilon) obj).q;
			float[] degs1 = new float[3];
			q.toAngles(degs1);
			float[] degs2 = new float[3];
			q2.toAngles(degs2);
			if (degs1[1] < -FloatMath.ANGLE_EPSILON_RAD)
				degs1[1] += FastMath.TWO_PI;
			if (degs2[1] < -FloatMath.ANGLE_EPSILON_RAD)
				degs2[1] += FastMath.TWO_PI;
			return FloatMath.equalsWithEpsilon(degs1[0], degs2[0])
				&& FloatMath.equalsWithEpsilon(degs1[1], degs2[1])
				&& FloatMath.equalsWithEpsilon(degs1[2], degs2[2]);
		}
	}

	public record AnglesSet(int pitch, int yaw, int roll) {
	}
}
