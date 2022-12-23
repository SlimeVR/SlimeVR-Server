package dev.slimevr.autobone;

import java.util.Map;

import dev.slimevr.poserecorder.PoseFrameSkeleton;
import dev.slimevr.poserecorder.PoseFrames;
import dev.slimevr.vr.processor.skeletonParts.BoneType;


public class AutoBoneTrainingStep {
	private int cursor1 = 0;
	private int cursor2 = 0;

	private float currentHeight;
	private final float targetHeight;

	private final PoseFrameSkeleton skeleton1;
	private final PoseFrameSkeleton skeleton2;

	private final PoseFrames trainingFrames;

	private final Map<BoneType, Float> intermediateOffsets;

	public AutoBoneTrainingStep(
		int cursor1,
		int cursor2,
		float targetHeight,
		PoseFrameSkeleton skeleton1,
		PoseFrameSkeleton skeleton2,
		PoseFrames trainingFrames,
		Map<BoneType, Float> intermediateOffsets
	) {
		this.cursor1 = cursor1;
		this.cursor2 = cursor2;
		this.targetHeight = targetHeight;
		this.skeleton1 = skeleton1;
		this.skeleton2 = skeleton2;
		this.trainingFrames = trainingFrames;
		this.intermediateOffsets = intermediateOffsets;
	}

	public AutoBoneTrainingStep(
		float targetHeight,
		PoseFrameSkeleton skeleton1,
		PoseFrameSkeleton skeleton2,
		PoseFrames trainingFrames,
		Map<BoneType, Float> intermediateOffsets
	) {
		this.targetHeight = targetHeight;
		this.skeleton1 = skeleton1;
		this.skeleton2 = skeleton2;
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

	public PoseFrameSkeleton getSkeleton1() {
		return skeleton1;
	}

	public PoseFrameSkeleton getSkeleton2() {
		return skeleton2;
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
