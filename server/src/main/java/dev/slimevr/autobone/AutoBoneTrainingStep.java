package dev.slimevr.autobone;

import dev.slimevr.poserecorder.PoseFrames;
import dev.slimevr.tracking.processor.BoneType;
import dev.slimevr.tracking.processor.HumanPoseManager;

import java.util.Map;


public class AutoBoneTrainingStep {
	private int cursor1 = 0;
	private int cursor2 = 0;

	private float currentHeight;
	private final float targetHeight;

	private final HumanPoseManager humanPoseManager1;
	private final HumanPoseManager humanPoseManager2;

	private final PoseFrames trainingFrames;

	private final Map<BoneType, Float> intermediateOffsets;

	public AutoBoneTrainingStep(
		int cursor1,
		int cursor2,
		float targetHeight,
		HumanPoseManager humanPoseManager1,
		HumanPoseManager humanPoseManager2,
		PoseFrames trainingFrames,
		Map<BoneType, Float> intermediateOffsets
	) {
		this.cursor1 = cursor1;
		this.cursor2 = cursor2;
		this.targetHeight = targetHeight;
		this.humanPoseManager1 = humanPoseManager1;
		this.humanPoseManager2 = humanPoseManager2;
		this.trainingFrames = trainingFrames;
		this.intermediateOffsets = intermediateOffsets;
	}

	public AutoBoneTrainingStep(
		float targetHeight,
		HumanPoseManager humanPoseManager1,
		HumanPoseManager humanPoseManager2,
		PoseFrames trainingFrames,
		Map<BoneType, Float> intermediateOffsets
	) {
		this.targetHeight = targetHeight;
		this.humanPoseManager1 = humanPoseManager1;
		this.humanPoseManager2 = humanPoseManager2;
		this.trainingFrames = trainingFrames;
		this.intermediateOffsets = intermediateOffsets;
	}

	public int getCursor1() {
		return cursor1;
	}

	public void setCursor1(int cursor1) {
		this.cursor1 = cursor1;
	}

	public int getCursor2() {
		return cursor2;
	}

	public void setCursor2(int cursor2) {
		this.cursor2 = cursor2;
	}

	public void setCursors(int cursor1, int cursor2) {
		this.cursor1 = cursor1;
		this.cursor2 = cursor2;
	}

	public float getCurrentHeight() {
		return currentHeight;
	}

	public void setCurrentHeight(float currentHeight) {
		this.currentHeight = currentHeight;
	}

	public float getTargetHeight() {
		return targetHeight;
	}

	public HumanPoseManager getHumanPoseManager1() {
		return humanPoseManager1;
	}

	public HumanPoseManager getHumanPoseManager2() {
		return humanPoseManager2;
	}

	public PoseFrames getTrainingFrames() {
		return trainingFrames;
	}

	public Map<BoneType, Float> getIntermediateOffsets() {
		return intermediateOffsets;
	}

	public float getHeightOffset() {
		return getTargetHeight() - getCurrentHeight();
	}
}
