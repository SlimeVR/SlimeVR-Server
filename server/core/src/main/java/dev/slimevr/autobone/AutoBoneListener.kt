package dev.slimevr.autobone

import dev.slimevr.autobone.AutoBone.Epoch
import dev.slimevr.poseframeformat.PoseFrames
import dev.slimevr.tracking.processor.config.SkeletonConfigOffsets
import java.util.*

interface AutoBoneListener {
	fun onAutoBoneProcessStatus(
		processType: AutoBoneProcessType,
		message: String?,
		current: Long,
		total: Long,
		eta: Float,
		completed: Boolean,
		success: Boolean,
	)

	fun onAutoBoneRecordingEnd(recording: PoseFrames)
	fun onAutoBoneEpoch(epoch: Epoch)
	fun onAutoBoneEnd(configValues: EnumMap<SkeletonConfigOffsets, Float>)
}
