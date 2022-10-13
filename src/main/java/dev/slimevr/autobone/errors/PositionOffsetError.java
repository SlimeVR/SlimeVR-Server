package dev.slimevr.autobone.errors;

import java.util.List;

import com.jme3.math.FastMath;

import dev.slimevr.autobone.AutoBoneTrainingStep;
import dev.slimevr.poserecorder.PoseFrameTracker;
import dev.slimevr.poserecorder.TrackerFrame;
import dev.slimevr.poserecorder.TrackerFrameData;
import dev.slimevr.vr.processor.skeleton.HumanSkeleton;
import dev.slimevr.vr.trackers.ComputedTracker;


// The difference between offset of absolute position and the corresponding point over time
public class PositionOffsetError implements IAutoBoneError {
	@Override
	public float getStepError(AutoBoneTrainingStep trainingStep) throws AutoBoneException {
		List<PoseFrameTracker> trackers = trainingStep.getTrainingFrames().getTrackers();
		return getPositionOffsetError(
			trackers,
			trainingStep.getCursor1(),
			trainingStep.getCursor2(),
			trainingStep.getSkeleton1(),
			trainingStep.getSkeleton2()
		);
	}

	public float getPositionOffsetError(
		List<PoseFrameTracker> trackers,
		int cursor1,
		int cursor2,
		HumanSkeleton skeleton1,
		HumanSkeleton skeleton2
	) {
		float offset = 0f;
		int offsetCount = 0;

		for (PoseFrameTracker tracker : trackers) {
			TrackerFrame trackerFrame1 = tracker.safeGetFrame(cursor1);
			if (
				trackerFrame1 == null
					|| !trackerFrame1.hasData(TrackerFrameData.POSITION)
					|| !trackerFrame1.hasData(TrackerFrameData.DESIGNATION)
					|| trackerFrame1.designation.trackerRole.isEmpty()
			) {
				continue;
			}

			TrackerFrame trackerFrame2 = tracker.safeGetFrame(cursor2);
			if (
				trackerFrame2 == null
					|| !trackerFrame2.hasData(TrackerFrameData.POSITION)
					|| !trackerFrame2.hasData(TrackerFrameData.DESIGNATION)
					|| trackerFrame2.designation.trackerRole.isEmpty()
			) {
				continue;
			}

			ComputedTracker computedTracker1 = skeleton1
				.getComputedTracker(trackerFrame1.designation.trackerRole.get());
			if (computedTracker1 == null) {
				continue;
			}

			ComputedTracker computedTracker2 = skeleton2
				.getComputedTracker(trackerFrame2.designation.trackerRole.get());
			if (computedTracker2 == null) {
				continue;
			}

			float dist1 = FastMath.abs(computedTracker1.position.distance(trackerFrame1.position));
			float dist2 = FastMath.abs(computedTracker2.position.distance(trackerFrame2.position));

			offset += FastMath.abs(dist2 - dist1);
			offsetCount++;
		}

		return offsetCount > 0 ? offset / offsetCount : 0f;
	}
}
