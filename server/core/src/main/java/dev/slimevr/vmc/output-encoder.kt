package dev.slimevr.vmc

import dev.slimevr.bones.BoneId
import dev.slimevr.config.VMCConfig
import dev.slimevr.osc.OscArg
import dev.slimevr.osc.OscBundle
import dev.slimevr.osc.OscContent
import dev.slimevr.osc.OscMessage
import dev.slimevr.resourcepacks.bones.CompiledSkeleton
import dev.slimevr.skeleton.BoneState
import dev.slimevr.skeleton.ComputedSkeleton
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlin.time.Duration

internal fun buildOutgoingBundle(
	definition: CompiledSkeleton,
	bones: ComputedSkeleton,
	routedBones: Set<BoneId>,
	config: VMCConfig,
	vrm: VrmGeometry?,
	elapsed: Duration,
): OscBundle {
	val contents = buildList {
		add(OscContent.Message(OscMessage("/VMC/Ext/T", listOf(OscArg.Float(elapsed.inWholeMilliseconds / 1000f)))))
		add(OscContent.Message(OscMessage("/VMC/Ext/OK", listOf(OscArg.Int(1)))))

		// Send the origin (0, 0, 0) as root
		add(OscContent.Message(transformMessage("/VMC/Ext/Root/Pos", "root", Vector3.ZERO, Quaternion.IDENTITY)))

		for (targetBoneId in definition.vmcNamedBones) {
			if (targetBoneId !in routedBones) continue
			val output = definition.vmcOutputOf(targetBoneId) ?: continue

			val targetParentBoneId = output.outputParent
			val trackingBoneId = if (config.mirrorTracking) definition.mirrorOf(targetBoneId) else targetBoneId
			val trackingBone = bones[trackingBoneId] ?: continue

			if (targetParentBoneId == null) {
				// TODO anchorHip https://github.com/SlimeVR/SlimeVR-Server/blob/main/server/core/src/main/java/dev/slimevr/osc/VMCHandler.kt#L371
				val pos = vrm?.hipLocalPosition ?: Vector3.ZERO
				val rot = vmcLocalRotation(trackingBone, output.restRotation, null, Quaternion.IDENTITY, config.mirrorTracking)
				add(OscContent.Message(transformMessage("/VMC/Ext/Bone/Pos", output.names.first(), pos, rot)))
				continue
			}

			val targetParentRest = definition.vmcOutputOf(targetParentBoneId)?.restRotation ?: Quaternion.IDENTITY
			val trackingParentBoneId = if (config.mirrorTracking) definition.mirrorOf(targetParentBoneId) else targetParentBoneId
			val trackingParent = bones[trackingParentBoneId] ?: continue

			val pos = if (vrm != null) {
				vrm.bindOffsets[targetBoneId] ?: Vector3.ZERO
			} else {
				vmcLocalPosition(trackingBone, trackingParent, targetParentRest, config.mirrorTracking)
			}
			val rot = vmcLocalRotation(
				trackingBone,
				output.restRotation,
				trackingParent,
				targetParentRest,
				config.mirrorTracking,
			)
			for (outputName in output.names) {
				add(OscContent.Message(transformMessage("/VMC/Ext/Bone/Pos", outputName, pos, rot)))
			}
		}
	}

	return OscBundle(1L, contents)
}

internal fun buildInitRequestMessage(): OscMessage = OscMessage("/VMC/Ext/Req", emptyList())

private fun restAdjustedWorld(
	bone: BoneState,
	rest: Quaternion,
	mirror: Boolean = false,
): Quaternion {
	val world = if (mirror) vmcMirrorRotation(bone.rotation) else bone.rotation
	return world * rest.inv()
}

internal fun vmcLocalRotation(
	bone: BoneState,
	rest: Quaternion,
	parent: BoneState?,
	parentRest: Quaternion,
	mirror: Boolean,
): Quaternion {
	val adjusted = restAdjustedWorld(bone, rest, mirror)
	if (parent == null) return adjusted
	return restAdjustedWorld(parent, parentRest, mirror).inv() * adjusted
}

internal fun vmcLocalPosition(
	bone: BoneState,
	parent: BoneState,
	parentRest: Quaternion,
	mirror: Boolean,
): Vector3 {
	val parentAdjusted = restAdjustedWorld(parent, parentRest, mirror)
	val localPosition = bone.headPosition - parent.headPosition
	return parentAdjusted.inv().sandwich(if (mirror) vmcMirrorPosition(localPosition) else localPosition)
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

internal fun vmcMirrorPosition(pos: Vector3): Vector3 = Vector3(-pos.x, pos.y, pos.z)

internal fun vmcMirrorRotation(rot: Quaternion): Quaternion = Quaternion(rot.w, rot.x, -rot.y, -rot.z)
