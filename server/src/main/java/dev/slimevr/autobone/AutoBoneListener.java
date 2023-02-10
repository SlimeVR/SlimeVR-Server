package dev.slimevr.autobone;

import dev.slimevr.autobone.AutoBone.Epoch;
import dev.slimevr.poserecorder.PoseFrames;
import dev.slimevr.tracking.processor.config.SkeletonConfigOffsets;

import java.util.EnumMap;


public interface AutoBoneListener {

	void onAutoBoneProcessStatus(
		AutoBoneProcessType processType,
		String message,
		long current,
		long total,
		boolean completed,
		boolean success
	);

	void onAutoBoneRecordingEnd(PoseFrames recording);

	void onAutoBoneEpoch(Epoch epoch);

	void onAutoBoneEnd(EnumMap<SkeletonConfigOffsets, Float> configValues);
}
