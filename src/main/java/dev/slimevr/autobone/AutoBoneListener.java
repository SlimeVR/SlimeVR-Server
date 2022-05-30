package dev.slimevr.autobone;

import java.util.EnumMap;

import dev.slimevr.autobone.AutoBone.Epoch;
import dev.slimevr.poserecorder.PoseFrames;
import dev.slimevr.vr.processor.skeleton.SkeletonConfigValue;


public interface AutoBoneListener {

	public void onAutoBoneProcessStatus(
		AutoBoneProcessType processType,
		String message,
		long current,
		long total,
		boolean completed,
		boolean success
	);

	public void onAutoBoneRecordingEnd(PoseFrames recording);

	public void onAutoBoneEpoch(Epoch epoch);

	public void onAutoBoneEnd(EnumMap<SkeletonConfigValue, Float> configValues);
}
