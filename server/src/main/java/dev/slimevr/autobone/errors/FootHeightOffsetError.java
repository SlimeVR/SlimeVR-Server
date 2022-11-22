package dev.slimevr.autobone.errors;

import com.jme3.math.FastMath;

import dev.slimevr.autobone.AutoBoneTrainingStep;
import dev.slimevr.vr.processor.skeleton.HumanSkeleton;
import dev.slimevr.vr.trackers.ComputedTracker;
import dev.slimevr.vr.trackers.TrackerRole;


// The offset between the height both feet at one instant and over time
public class FootHeightOffsetError implements IAutoBoneError {
	@Override
	public float getStepError(AutoBoneTrainingStep trainingStep) throws AutoBoneException {
		return getSlideError(trainingStep.getSkeleton1(), trainingStep.getSkeleton2());
	}

	public static float getSlideError(HumanSkeleton skeleton1, HumanSkeleton skeleton2) {
		ComputedTracker leftTracker1 = skeleton1.getComputedTracker(TrackerRole.LEFT_FOOT);
		ComputedTracker rightTracker1 = skeleton1.getComputedTracker(TrackerRole.RIGHT_FOOT);

		ComputedTracker leftTracker2 = skeleton2.getComputedTracker(TrackerRole.LEFT_FOOT);
		ComputedTracker rightTracker2 = skeleton2.getComputedTracker(TrackerRole.RIGHT_FOOT);

		return getFootHeightError(leftTracker1, rightTracker1, leftTracker2, rightTracker2);
	}

	public static float getFootHeightError(
		ComputedTracker leftTracker1,
		ComputedTracker rightTracker1,
		ComputedTracker leftTracker2,
		ComputedTracker rightTracker2
	) {
		float leftFoot1 = leftTracker1.position.y;
		float rightFoot1 = rightTracker1.position.y;

		float leftFoot2 = leftTracker2.position.y;
		float rightFoot2 = rightTracker2.position.y;

		// Compute all combinations of heights
		float dist1 = FastMath.abs(leftFoot1 - rightFoot1);
		float dist2 = FastMath.abs(leftFoot1 - leftFoot2);
		float dist3 = FastMath.abs(leftFoot1 - rightFoot2);

		float dist4 = FastMath.abs(rightFoot1 - leftFoot2);
		float dist5 = FastMath.abs(rightFoot1 - rightFoot2);

		float dist6 = FastMath.abs(leftFoot2 - rightFoot2);

		// Divide by 12 (6 values * 2 to halve) to halve and average, it's
		// halved because you want to approach a midpoint, not the other point
		return (dist1 + dist2 + dist3 + dist4 + dist5 + dist6) / 12f;
	}
}
