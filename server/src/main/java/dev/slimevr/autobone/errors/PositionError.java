package dev.slimevr.autobone.errors;

import java.util.List;

import com.jme3.math.FastMath;

import dev.slimevr.autobone.AutoBoneTrainingStep;
import dev.slimevr.poserecorder.PoseFrameTracker;
import dev.slimevr.poserecorder.TrackerFrame;
import dev.slimevr.poserecorder.TrackerFrameData;
import dev.slimevr.tracking.processor.skeleton.HumanSkeleton;
import dev.slimevr.tracking.trackers.ComputedTracker;


// The distance of any points to the corresponding absolute position
public class PositionError implements IAutoBoneError {
	@Override
	public float getStepError(AutoBoneTrainingStep trainingStep) throws AutoBoneException {
		List<PoseFrameTracker> trackers = trainingStep.getTrainingFrames().getTrackers();
		return (getPositionError(
			trackers,
			trainingStep.getCursor1(),
			trainingStep.getHumanPoseManager1().getSkeleton()
		)
			+ getPositionError(
				trackers,
				trainingStep.getCursor2(),
				trainingStep.getHumanPoseManager2().getSkeleton()
			))
			/ 2f;
	}

	public static float getPositionError(
		List<PoseFrameTracker> trackers,
		int cursor,
		HumanSkeleton skeleton
	) {
		float offset = 0f;
		int offsetCount = 0;

		for (PoseFrameTracker tracker : trackers) {
			TrackerFrame trackerFrame = tracker.safeGetFrame(cursor);
			if (
				trackerFrame == null
					|| !trackerFrame.hasData(TrackerFrameData.POSITION)
					|| !trackerFrame.hasData(TrackerFrameData.DESIGNATION)
					|| trackerFrame.designation.trackerRole.isEmpty()
			) {
				continue;
			}

			ComputedTracker computedTracker = skeleton
				.getComputedTracker(trackerFrame.designation.trackerRole.get());
			if (computedTracker != null) {
				offset += FastMath.abs(computedTracker.position.distance(trackerFrame.position));
				offsetCount++;
			}
		}

		return offsetCount > 0 ? offset / offsetCount : 0f;
	}
}
