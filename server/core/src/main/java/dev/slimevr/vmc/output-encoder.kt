package dev.slimevr.vmc

import dev.slimevr.config.VMCConfig
import dev.slimevr.osc.OscArg
import dev.slimevr.osc.OscBundle
import dev.slimevr.osc.OscContent
import dev.slimevr.osc.OscMessage
import dev.slimevr.skeleton.BoneState
import dev.slimevr.skeleton.ComputedSkeleton
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart
import kotlin.time.Duration

internal fun buildOutgoingBundle(
	bones: ComputedSkeleton,
	routedBones: Set<BodyPart>,
	config: VMCConfig,
	vrm: VrmGeometry?,
	skeletonHeight: Float,
	floorLevel: Float,
	elapsed: Duration,
): OscBundle {
	val contents = buildList {
		add(OscContent.Message(OscMessage("/VMC/Ext/T", listOf(OscArg.Float(elapsed.inWholeMilliseconds / 1000f)))))
		add(OscContent.Message(OscMessage("/VMC/Ext/OK", listOf(OscArg.Int(1)))))

		// Send the origin (0, 0, 0) as root
		add(OscContent.Message(transformMessage("/VMC/Ext/Root/Pos", "root", Vector3.ZERO, Quaternion.IDENTITY)))

		for ((targetBodyPart, unityNames) in BODY_PART_TO_UNITY_BONE) {
			if (targetBodyPart !in routedBones) continue

			val targetParentBodyPart = VMC_OUTPUT_BONE_PARENTS[targetBodyPart]
			val trackingBodyPart = trackingBodyPart(targetBodyPart, config.mirrorTracking)
			val trackingBone = bones[trackingBodyPart] ?: continue

			if (targetParentBodyPart == null) {
				val pos = vmcHipPosition(bones, vrm, config, skeletonHeight, floorLevel)
				val rot = vmcLocalRotation(trackingBone, null, targetBodyPart, null, config.mirrorTracking)
				add(OscContent.Message(transformMessage("/VMC/Ext/Bone/Pos", unityNames.first(), pos, rot)))
				continue
			}

			val trackingParentBodyPart = trackingBodyPart(targetParentBodyPart, config.mirrorTracking)
			val trackingParent = bones[trackingParentBodyPart] ?: continue

			val pos = emittedLocalPosition(targetBodyPart, targetParentBodyPart, trackingBone, trackingParent, vrm, config.mirrorTracking)
			val rot = vmcLocalRotation(
				trackingBone,
				trackingParent,
				targetBodyPart,
				targetParentBodyPart,
				config.mirrorTracking,
			)
			for (outputName in unityNames) {
				add(OscContent.Message(transformMessage("/VMC/Ext/Bone/Pos", outputName, pos, rot)))
			}
		}
	}

	return OscBundle(1L, contents)
}

internal fun buildInitRequestMessage(): OscMessage = OscMessage("/VMC/Ext/Req", emptyList())

private fun trackingBodyPart(targetBodyPart: BodyPart, mirror: Boolean): BodyPart = if (mirror) vmcMirrorSource(targetBodyPart) else targetBodyPart

private fun vmcMirrorSource(bodyPart: BodyPart): BodyPart {
	return when (bodyPart) {
		BodyPart.HEAD -> return BodyPart.HEAD
		BodyPart.NECK -> return BodyPart.NECK
		BodyPart.UPPER_CHEST -> return BodyPart.UPPER_CHEST
		BodyPart.LOWER_CHEST -> return BodyPart.LOWER_CHEST
		BodyPart.UPPER_WAIST -> return BodyPart.UPPER_WAIST
		BodyPart.LOWER_WAIST -> return BodyPart.LOWER_WAIST
		BodyPart.HIP -> return BodyPart.HIP
		BodyPart.LEFT_UPPER_LEG -> BodyPart.RIGHT_UPPER_LEG
		BodyPart.RIGHT_UPPER_LEG -> BodyPart.LEFT_UPPER_LEG
		BodyPart.LEFT_LOWER_LEG -> BodyPart.RIGHT_LOWER_LEG
		BodyPart.RIGHT_LOWER_LEG -> BodyPart.LEFT_LOWER_LEG
		BodyPart.LEFT_FOOT -> BodyPart.RIGHT_FOOT
		BodyPart.RIGHT_FOOT -> BodyPart.LEFT_FOOT
		BodyPart.LEFT_UPPER_ARM -> BodyPart.RIGHT_UPPER_ARM
		BodyPart.RIGHT_UPPER_ARM -> BodyPart.LEFT_UPPER_ARM
		BodyPart.LEFT_LOWER_ARM -> BodyPart.RIGHT_LOWER_ARM
		BodyPart.RIGHT_LOWER_ARM -> BodyPart.LEFT_LOWER_ARM
		BodyPart.LEFT_HAND -> BodyPart.RIGHT_HAND
		BodyPart.RIGHT_HAND -> BodyPart.LEFT_HAND
		BodyPart.LEFT_SHOULDER -> BodyPart.RIGHT_SHOULDER
		BodyPart.RIGHT_SHOULDER -> BodyPart.LEFT_SHOULDER
		BodyPart.LEFT_THUMB_METACARPAL -> BodyPart.RIGHT_THUMB_METACARPAL
		BodyPart.LEFT_THUMB_PROXIMAL -> BodyPart.RIGHT_THUMB_PROXIMAL
		BodyPart.LEFT_THUMB_DISTAL -> BodyPart.RIGHT_THUMB_DISTAL
		BodyPart.LEFT_INDEX_PROXIMAL -> BodyPart.RIGHT_INDEX_PROXIMAL
		BodyPart.LEFT_INDEX_INTERMEDIATE -> BodyPart.RIGHT_INDEX_INTERMEDIATE
		BodyPart.LEFT_INDEX_DISTAL -> BodyPart.RIGHT_INDEX_DISTAL
		BodyPart.LEFT_MIDDLE_PROXIMAL -> BodyPart.RIGHT_MIDDLE_PROXIMAL
		BodyPart.LEFT_MIDDLE_INTERMEDIATE -> BodyPart.RIGHT_MIDDLE_INTERMEDIATE
		BodyPart.LEFT_MIDDLE_DISTAL -> BodyPart.RIGHT_MIDDLE_DISTAL
		BodyPart.LEFT_RING_PROXIMAL -> BodyPart.RIGHT_RING_PROXIMAL
		BodyPart.LEFT_RING_INTERMEDIATE -> BodyPart.RIGHT_RING_INTERMEDIATE
		BodyPart.LEFT_RING_DISTAL -> BodyPart.RIGHT_RING_DISTAL
		BodyPart.LEFT_LITTLE_PROXIMAL -> BodyPart.RIGHT_LITTLE_PROXIMAL
		BodyPart.LEFT_LITTLE_INTERMEDIATE -> BodyPart.RIGHT_LITTLE_INTERMEDIATE
		BodyPart.LEFT_LITTLE_DISTAL -> BodyPart.RIGHT_LITTLE_DISTAL
		BodyPart.LEFT_BIG_TOE -> BodyPart.RIGHT_BIG_TOE
		BodyPart.LEFT_INDEX_TOE -> BodyPart.RIGHT_INDEX_TOE
		BodyPart.LEFT_MIDDLE_TOE -> BodyPart.RIGHT_MIDDLE_TOE
		BodyPart.LEFT_RING_TOE -> BodyPart.RIGHT_RING_TOE
		BodyPart.LEFT_LITTLE_TOE -> BodyPart.RIGHT_LITTLE_TOE
		BodyPart.LEFT_BUST -> BodyPart.RIGHT_BUST
		else -> bodyPart
	}
}
private fun restAdjustedWorld(
	bone: BoneState,
	restBodyPart: BodyPart = bone.bodyPart,
	mirror: Boolean = false,
): Quaternion {
	val world = if (mirror) vmcMirrorRotation(bone.rotation) else bone.rotation
	val rest = VMC_REST_ROTATIONS[restBodyPart] ?: return world
	return world * rest.inv()
}

internal fun vmcLocalRotation(
	bone: BoneState,
	parent: BoneState?,
	restBodyPart: BodyPart,
	restParentBodyPart: BodyPart?,
	mirror: Boolean,
): Quaternion {
	val adjusted = restAdjustedWorld(bone, restBodyPart, mirror)
	if (parent == null) return adjusted
	return restAdjustedWorld(parent, restParentBodyPart ?: parent.bodyPart, mirror).inv() * adjusted
}

internal fun vmcLocalPosition(
	bone: BoneState,
	parent: BoneState,
	restParentBodyPart: BodyPart,
	mirror: Boolean,
): Vector3 {
	val parentAdjusted = restAdjustedWorld(parent, restParentBodyPart, mirror)
	val localPosition = bone.headPosition - parent.headPosition
	return parentAdjusted.inv().sandwich(if (mirror) vmcMirrorPosition(localPosition) else localPosition)
}

private fun emittedLocalPosition(
	targetBodyPart: BodyPart,
	targetParentBodyPart: BodyPart,
	trackingBone: BoneState,
	trackingParent: BoneState,
	vrm: VrmGeometry?,
	mirror: Boolean,
): Vector3 = if (vrm != null) {
	vrm.bindOffsets[targetBodyPart] ?: Vector3.ZERO
} else {
	vmcLocalPosition(trackingBone, trackingParent, targetParentBodyPart, mirror)
}

private fun hipToNeckOffset(bones: ComputedSkeleton, vrm: VrmGeometry?, mirror: Boolean): Vector3? {
	var parentBodyPart = BodyPart.HIP
	var offset = Vector3.ZERO
	for (bodyPart in VMC_HIP_TO_NECK_CHAIN) {
		val trackingBone = bones[trackingBodyPart(bodyPart, mirror)] ?: return null
		val trackingParent = bones[trackingBodyPart(parentBodyPart, mirror)] ?: return null
		val localPosition = emittedLocalPosition(bodyPart, parentBodyPart, trackingBone, trackingParent, vrm, mirror)
		val parentWorldRotation = restAdjustedWorld(trackingParent, parentBodyPart, mirror)
		offset += parentWorldRotation.sandwich(localPosition)
		parentBodyPart = bodyPart
	}
	return offset
}

// Hip height above the floor at rest, used when anchored with no VRM loaded
private fun restHipHeight(bones: ComputedSkeleton, skeletonHeight: Float): Float = skeletonHeight +
	SPINE_CHAIN_ABOVE_HIP.sumOf { (bones[it]?.offset?.y ?: 0f).toDouble() }.toFloat()

private fun vmcHipPosition(
	bones: ComputedSkeleton,
	vrm: VrmGeometry?,
	config: VMCConfig,
	skeletonHeight: Float,
	floorLevel: Float,
): Vector3 {
	val anchoredHipPosition = vrm?.hipLocalPosition ?: Vector3(0f, restHipHeight(bones, skeletonHeight), 0f)
	if (config.anchorAtHips) return anchoredHipPosition

	val neck = bones[BodyPart.NECK] ?: return anchoredHipPosition
	val neckOffset = hipToNeckOffset(bones, vrm, config.mirrorTracking) ?: return anchoredHipPosition

	val restHeight = vrm?.outputRestHeight ?: skeletonHeight
	val scale = if (skeletonHeight != 0f) restHeight / skeletonHeight else 1f
	val floorRelativeNeck = (neck.headPosition - Vector3(0f, floorLevel, 0f)) * scale
	return floorRelativeNeck - neckOffset
}

private fun transformMessage(address: String, name: String, pos: Vector3, rot: Quaternion): OscMessage = OscMessage(
	address,
	listOf(
		OscArg.String(name),
		OscArg.Float(pos.x),
		OscArg.Float(pos.y),
		OscArg.Float(-pos.z),
		OscArg.Float(rot.x),
		OscArg.Float(rot.y),
		OscArg.Float(-rot.z),
		OscArg.Float(-rot.w),
	),
)
