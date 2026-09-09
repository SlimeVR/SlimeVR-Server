package dev.slimevr.vmc

import dev.slimevr.config.VMCConfig
import dev.slimevr.osc.OscArg
import dev.slimevr.osc.OscBundle
import dev.slimevr.osc.OscContent
import dev.slimevr.osc.OscMessage
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
	elapsed: Duration,
): OscBundle {
	val contents = buildList {
		add(OscContent.Message(OscMessage("/VMC/Ext/T", listOf(OscArg.Float(elapsed.inWholeMilliseconds / 1000f)))))
		add(OscContent.Message(OscMessage("/VMC/Ext/OK", listOf(OscArg.Int(1)))))

		// Send the origin (0, 0, 0) as root
		add(OscContent.Message(transformMessage("/VMC/Ext/Root/Pos", "root", Vector3.ZERO, Quaternion.IDENTITY)))

		for ((targetBodyPart, unityNames) in BODY_PART_TO_UNITY_BONE) {
			if (targetBodyPart !in routedBones) continue

			val targetParentBodyPart = VMC_BONE_PARENTS[targetBodyPart]
			val trackingBodyPart = if (config.mirrorTracking) vmcMirrorSource(targetBodyPart) else targetBodyPart
			val trackingBone = bones[trackingBodyPart] ?: continue

			if (targetParentBodyPart == null) {
				// TODO anchorHip https://github.com/SlimeVR/SlimeVR-Server/blob/main/server/core/src/main/java/dev/slimevr/osc/VMCHandler.kt#L371
				val pos = vrm?.hipLocalPosition ?: Vector3.ZERO
				val rot = vmcLocalRotation(trackingBone, null, targetBodyPart, null, config.mirrorTracking)
				add(OscContent.Message(transformMessage("/VMC/Ext/Bone/Pos", unityNames.first(), pos, rot)))
				continue
			}

			val trackingParentBodyPart = if (config.mirrorTracking) {
				vmcMirrorSource(targetParentBodyPart)
			} else {
				targetParentBodyPart
			}
			val trackingParent = bones[trackingParentBodyPart] ?: continue

			val pos = if (vrm != null) {
				vrm.bindOffsets[targetBodyPart] ?: Vector3.ZERO
			} else {
				vmcLocalPosition(trackingBone, trackingParent, targetParentBodyPart, config.mirrorTracking)
			}
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
