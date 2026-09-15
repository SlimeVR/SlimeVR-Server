package dev.slimevr.vrcosc

import dev.slimevr.bones.BoneId
import dev.slimevr.osc.OscArg
import dev.slimevr.osc.OscBundle
import dev.slimevr.osc.OscContent
import dev.slimevr.osc.OscMessage
import dev.slimevr.resourcepacks.bones.CompiledSkeleton
import dev.slimevr.resourcepacks.evaluateEmit
import dev.slimevr.skeleton.ComputedSkeleton
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import kotlin.math.PI

internal fun buildOutgoingBundle(
	definition: CompiledSkeleton,
	bones: ComputedSkeleton,
	routedBones: Set<BoneId>,
): OscBundle? {
	val messages = buildList<OscContent> {
		for (boneId in definition.vrchatEmittingBones) {
			if (boneId !in routedBones) continue
			val bone = bones[boneId] ?: continue
			for ((address, entry) in definition.emitEntriesOf(boneId)) {
				val relativeTo = entry.relativeTo?.let(bones::get)
				if (entry.relativeTo != null && relativeTo == null) continue
				add(OscContent.Message(OscMessage(address, evaluateEmit(entry, bone, relativeTo))))
			}
		}
	}

	return messages.takeIf { it.isNotEmpty() }?.let { OscBundle(1L, it) }
}

internal fun buildYawAlignMessage(headRotation: Quaternion): OscMessage {
	val (_, _, yaw, _) = headRotation.toEulerAngles(EulerOrder.YXZ)
	return OscMessage(
		"/tracking/trackers/head/rotation",
		listOf(
			OscArg.Float(0f),
			OscArg.Float(-yaw * 180f / PI.toFloat()),
			OscArg.Float(0f),
		),
	)
}
