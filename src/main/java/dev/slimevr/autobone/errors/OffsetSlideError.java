package dev.slimevr.autobone.errors;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

import dev.slimevr.autobone.AutoBoneTrainingStep;
import dev.slimevr.vr.processor.skeleton.HumanSkeleton;
import dev.slimevr.vr.trackers.ComputedTracker;
import dev.slimevr.vr.trackers.TrackerRole;


// The change in distance between both of the ankles over time
public class OffsetSlideError implements IAutoBoneError {
	@Override
	public float getStepError(AutoBoneTrainingStep trainingStep) throws AutoBoneException {
		return getSlideError(trainingStep.getSkeleton1(), trainingStep.getSkeleton2());
	}

	public static float getSlideError(HumanSkeleton skeleton1, HumanSkeleton skeleton2) {
		ComputedTracker leftTracker1 = skeleton1.getComputedTracker(TrackerRole.LEFT_FOOT);
		ComputedTracker rightTracker1 = skeleton1.getComputedTracker(TrackerRole.RIGHT_FOOT);

		ComputedTracker leftTracker2 = skeleton2.getComputedTracker(TrackerRole.LEFT_FOOT);
		ComputedTracker rightTracker2 = skeleton2.getComputedTracker(TrackerRole.RIGHT_FOOT);

		return getSlideError(leftTracker1, rightTracker1, leftTracker2, rightTracker2);
	}

	public static float getSlideError(
		ComputedTracker leftTracker1,
		ComputedTracker rightTracker1,
		ComputedTracker leftTracker2,
		ComputedTracker rightTracker2
	) {
		Vector3f leftFoot1 = leftTracker1.position;
		Vector3f rightFoot1 = rightTracker1.position;

		Vector3f leftFoot2 = leftTracker2.position;
		Vector3f rightFoot2 = rightTracker2.position;

		float slideDist1 = leftFoot1.distance(rightFoot1);
		float slideDist2 = leftFoot2.distance(rightFoot2);

		float slideDist3 = leftFoot1.distance(rightFoot2);
		float slideDist4 = leftFoot2.distance(rightFoot1);

		// Compute all combinations of distances
		float dist1 = FastMath.abs(slideDist1 - slideDist2);
		float dist2 = FastMath.abs(slideDist1 - slideDist3);
		float dist3 = FastMath.abs(slideDist1 - slideDist4);

		float dist4 = FastMath.abs(slideDist2 - slideDist3);
		float dist5 = FastMath.abs(slideDist2 - slideDist4);

		float dist6 = FastMath.abs(slideDist3 - slideDist4);

		// Divide by 12 (6 values * 2 to halve) to halve and average, it's
		// halved because you want to approach a midpoint, not the other point
		return (dist1 + dist2 + dist3 + dist4 + dist5 + dist6) / 12f;
	}
}
