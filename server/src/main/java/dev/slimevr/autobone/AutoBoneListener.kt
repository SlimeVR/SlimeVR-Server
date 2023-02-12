package dev.slimevr.autobone

import dev.slimevr.autobone.AutoBone.Epoch
import dev.slimevr.poserecorder.PoseFrames
import dev.slimevr.tracking.processor.config.SkeletonConfigOffsets
import java.util.EnumMap

interface AutoBoneListener {
	fun onAutoBoneProcessStatus(
		processType: AutoBoneProcessType,
		message: String?,
		current: Long,
		total: Long,
		completed: Boolean,
		success: Boolean
	)

	fun onAutoBoneRecordingEnd(recording: PoseFrames)
	fun onAutoBoneEpoch(epoch: Epoch)
	fun onAutoBoneEnd(configValues: EnumMap<SkeletonConfigOffsets, Float>)
}
