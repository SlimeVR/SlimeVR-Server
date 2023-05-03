package dev.slimevr.unit;

import com.jme3.math.FastMath;
import dev.slimevr.VRServer;
import dev.slimevr.tracking.processor.BoneType;
import dev.slimevr.tracking.processor.TransformNode;
import dev.slimevr.tracking.trackers.Tracker;
import dev.slimevr.tracking.trackers.TrackerResetsHandler;
import dev.slimevr.tracking.trackers.udp.IMUType;
import io.eiren.math.FloatMath;
import io.eiren.util.StringUtils;
import io.github.axisangles.ktmath.EulerAngles;
import io.github.axisangles.ktmath.EulerOrder;
import io.github.axisangles.ktmath.Quaternion;
import io.github.axisangles.ktmath.Vector3;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;


/**
 * Tests {@link TrackerResetsHandler#resetFull(Quaternion)}
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
		EulerAngles angles,
		EulerAngles anglesAdj,
		EulerAngles anglesDiff
	) {
		return "Rot: "
			+ yaw
			+ "/"
			+ pitch
			+ "/"
			+ roll
			+ ". "
			+ "Angles: "
			+ StringUtils.prettyNumber(angles.getX() * FastMath.RAD_TO_DEG, 1)
			+ "/"
			+ StringUtils.prettyNumber(anglesAdj.getX() * FastMath.RAD_TO_DEG, 1)
			+ ", "
			+ StringUtils.prettyNumber(angles.getY() * FastMath.RAD_TO_DEG, 1)
			+ "/"
			+ StringUtils.prettyNumber(anglesAdj.getY() * FastMath.RAD_TO_DEG, 1)
			+ ", "
			+ StringUtils.prettyNumber(angles.getZ() * FastMath.RAD_TO_DEG, 1)
			+ "/"
			+ StringUtils.prettyNumber(anglesAdj.getZ() * FastMath.RAD_TO_DEG, 1)
			+ ". Diff: "
			+ StringUtils.prettyNumber(anglesDiff.getX() * FastMath.RAD_TO_DEG, 1)
			+ ", "
			+ StringUtils.prettyNumber(anglesDiff.getY() * FastMath.RAD_TO_DEG, 1)
			+ ", "
			+ StringUtils.prettyNumber(anglesDiff.getZ() * FastMath.RAD_TO_DEG, 1);
	}

	public static Quaternion q(float pitch, float yaw, float roll) {
		return new EulerAngles(
			EulerOrder.YZX,
			pitch * FastMath.DEG_TO_RAD,
			yaw * FastMath.DEG_TO_RAD,
			roll * FastMath.DEG_TO_RAD
		).toQuaternion();
	}

	public static String toDegs(Quaternion q) {
		EulerAngles angles = q.toEulerAngles(EulerOrder.YZX);
		return StringUtils.prettyNumber(angles.getX() * FastMath.RAD_TO_DEG, 0)
			+ ","
			+ StringUtils.prettyNumber(angles.getY() * FastMath.RAD_TO_DEG, 0)
			+ ","
			+ StringUtils.prettyNumber(angles.getZ() * FastMath.RAD_TO_DEG, 0);
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
		Tracker tracker = new Tracker(
			null,
			VRServer.getNextLocalTrackerId(),
			"test",
			"test",
			null,
			false,
			true,
			false,
			false,
			true,
			false,
			IMUType.UNKNOWN,
			false,
			false,
			true
		);
		tracker.setRotation(trackerQuat);
		tracker.getResetsHandler().resetFull(referenceQuat);
		Quaternion read = tracker.getRotation();
		assertNotNull(read, "Adjusted tracker didn't return rotation");

		// Use only yaw HMD rotation
		referenceQuat = referenceQuat.project(Vector3.Companion.getPOS_Y()).unit();

		assertEquals(
			new QuatEqualFullWithEpsilon(referenceQuat),
			new QuatEqualFullWithEpsilon(read),
			"Adjusted quat is not equal to reference quat ("
				+ toDegs(referenceQuat)
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
		Tracker tracker = new Tracker(
			null,
			VRServer.getNextLocalTrackerId(),
			"test",
			"test",
			null,
			false,
			true,
			false,
			false,
			true,
			false,
			IMUType.UNKNOWN,
			false,
			false,
			true
		);
		tracker.setRotation(trackerQuat);
		tracker.getResetsHandler().resetYaw(referenceQuat);
		Quaternion read = tracker.getRotation();
		assertNotNull(read, "Adjusted tracker didn't return rotation");

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
		Quaternion referenceQuat = q(refPitch, refYaw, refRoll);
		Tracker tracker = new Tracker(
			null,
			VRServer.getNextLocalTrackerId(),
			"test",
			"test",
			null,
			false,
			true,
			false,
			false,
			true,
			false,
			IMUType.UNKNOWN,
			false,
			false,
			true
		);
		tracker.setRotation(trackerQuat);
		tracker.getResetsHandler().resetFull(referenceQuat);

		// Use only yaw HMD rotation
		referenceQuat.project(Vector3.Companion.getPOS_Y()).unit();

		TransformNode trackerNode = new TransformNode(BoneType.HIP, true);
		TransformNode rotationNode = new TransformNode(BoneType.HIP, true);
		rotationNode.attachChild(trackerNode);

		trackerNode.getLocalTransform().setRotation(tracker.getRawRotation());

		for (int yaw = 0; yaw <= 360; yaw += 30) {
			for (int pitch = -90; pitch <= 90; pitch += 15) {
				for (int roll = -90; roll <= 90; roll += 15) {
					Quaternion rotation = new EulerAngles(
						EulerOrder.YZX,
						pitch * FastMath.DEG_TO_RAD,
						yaw * FastMath.DEG_TO_RAD,
						roll * FastMath.DEG_TO_RAD
					).toQuaternion();
					Quaternion rotationCompare = new EulerAngles(
						EulerOrder.YZX,
						pitch * FastMath.DEG_TO_RAD,
						(yaw + refYaw) * FastMath.DEG_TO_RAD,
						roll * FastMath.DEG_TO_RAD
					).toQuaternion();

					rotationNode.getLocalTransform().setRotation(rotation);
					rotationNode.update();
					tracker.setRotation(trackerNode.getWorldTransform().getRotation());

					EulerAngles angles = tracker.getRawRotation().toEulerAngles(EulerOrder.YZX);
					EulerAngles anglesAdj = tracker.getRotation().toEulerAngles(EulerOrder.YZX);
					EulerAngles anglesDiff = tracker
						.getRotation()
						.inv()
						.times(rotationCompare)
						.toEulerAngles(EulerOrder.YZX);

					if (!PRINT_TEST_RESULTS) {
						assertTrue(
							FloatMath.equalsToZero(anglesDiff.getX())
								&& FloatMath.equalsToZero(anglesDiff.getY())
								&& FloatMath.equalsToZero(anglesDiff.getZ()),
							name(yaw, pitch, roll, angles, anglesAdj, anglesDiff)
						);
					} else {
						if (
							FloatMath.equalsToZero(anglesDiff.getX())
								&& FloatMath.equalsToZero(anglesDiff.getY())
								&& FloatMath.equalsToZero(anglesDiff.getZ())
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
			EulerAngles degs1 = q.toEulerAngles(EulerOrder.YZX);
			EulerAngles degs2 = q2.toEulerAngles(EulerOrder.YZX);
			if (degs1.getY() < -FloatMath.ANGLE_EPSILON_RAD)
				degs1 = new EulerAngles(
					EulerOrder.YZX,
					degs1.getX(),
					degs1.getY() + FastMath.TWO_PI,
					degs1.getZ()
				);
			if (degs2.getY() < -FloatMath.ANGLE_EPSILON_RAD)
				degs2 = new EulerAngles(
					EulerOrder.YZX,
					degs2.getX(),
					degs2.getY() + FastMath.TWO_PI,
					degs2.getZ()
				);
			return FloatMath.equalsWithEpsilon(degs1.getY(), degs2.getY());
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
			EulerAngles degs1 = q.toEulerAngles(EulerOrder.YZX);
			EulerAngles degs2 = q2.toEulerAngles(EulerOrder.YZX);
			if (degs1.getY() < -FloatMath.ANGLE_EPSILON_RAD)
				degs1 = new EulerAngles(
					EulerOrder.YZX,
					degs1.getX(),
					degs1.getY() + FastMath.TWO_PI,
					degs1.getZ()
				);
			if (degs2.getY() < -FloatMath.ANGLE_EPSILON_RAD)
				degs2 = new EulerAngles(
					EulerOrder.YZX,
					degs2.getX(),
					degs2.getY() + FastMath.TWO_PI,
					degs2.getZ()
				);
			return FloatMath.equalsWithEpsilon(degs1.getX(), degs2.getX())
				&& FloatMath.equalsWithEpsilon(degs1.getY(), degs2.getY())
				&& FloatMath.equalsWithEpsilon(degs1.getZ(), degs2.getZ());
		}
	}

	public record AnglesSet(int pitch, int yaw, int roll) {
	}
}
