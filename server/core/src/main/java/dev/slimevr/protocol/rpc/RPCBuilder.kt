package dev.slimevr.protocol.rpc

import com.google.flatbuffers.FlatBufferBuilder
import dev.slimevr.tracking.processor.HumanPoseManager
import dev.slimevr.tracking.processor.config.SkeletonConfigOffsets
import solarxr_protocol.rpc.SkeletonConfigResponse
import solarxr_protocol.rpc.SkeletonPart

fun createSkeletonConfig(
	fbb: FlatBufferBuilder,
	humanPoseManager: HumanPoseManager,
): Int {
	val partsOffsets = IntArray(SkeletonConfigOffsets.entries.size)

	for (index in SkeletonConfigOffsets.entries.toTypedArray().indices) {
		val `val` = SkeletonConfigOffsets.values[index]
		val part = SkeletonPart
			.createSkeletonPart(fbb, `val`.id, humanPoseManager.getOffset(`val`))
		partsOffsets[index] = part
	}

	val parts = SkeletonConfigResponse.createSkeletonPartsVector(fbb, partsOffsets)
	val userHeight = humanPoseManager.userHeightFromConfig
	return SkeletonConfigResponse.createSkeletonConfigResponse(fbb, parts, userHeight)
}
