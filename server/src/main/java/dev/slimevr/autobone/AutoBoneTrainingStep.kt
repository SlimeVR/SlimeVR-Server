package dev.slimevr.autobone

import dev.slimevr.config.AutoBoneConfig
import dev.slimevr.poseframeformat.PoseFrames
import dev.slimevr.tracking.processor.BoneType
import dev.slimevr.tracking.processor.HumanPoseManager

class AutoBoneTrainingStep(
	val config: AutoBoneConfig,
	val targetHeight: Float,
	val humanPoseManager1: HumanPoseManager,
	val humanPoseManager2: HumanPoseManager,
	val trainingFrames: PoseFrames,
	val intermediateOffsets: Map<BoneType, Float>,
	var cursor1: Int = 0,
	var cursor2: Int = 0,
	var currentHeight: Float = 0f,
) {

	fun setCursors(cursor1: Int, cursor2: Int) {
		this.cursor1 = cursor1
		this.cursor2 = cursor2
	}

	val heightOffset: Float
		get() = targetHeight - currentHeight
}
