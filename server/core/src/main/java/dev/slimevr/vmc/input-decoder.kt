package dev.slimevr.vmc

import dev.slimevr.osc.OscArg
import dev.slimevr.osc.OscMessage
import dev.slimevr.skeleton.BodyPartMap
import dev.slimevr.skeleton.bodyPartMap
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3

data class VmcPoseTracker(
	val serial: String,
	val position: Vector3,
	val rotation: Quaternion,
	/** True for a /Pos/Local message: device raw scale, must never be avatar-scaled. */
	val deviceScale: Boolean,
)

/**
 * Reused across bundles instead of being copied
 * every frame, so a bundle that only touches a few bones doesn't allocate fresh backing storage
 * for the rest.
 */
class VmcInputFrame(
	val boneLocalRotations: BodyPartMap<Quaternion> = bodyPartMap(),
	val boneLocalPositions: BodyPartMap<Vector3> = bodyPartMap(),
	val poseTrackers: MutableMap<String, VmcPoseTracker> = mutableMapOf(),
	var rootPosition: Vector3 = Vector3.ZERO,
	var rootRotation: Quaternion = Quaternion.IDENTITY,
)

fun emptyVmcInputFrame() = VmcInputFrame()

internal fun decodeVmcMessage(msg: OscMessage, frame: VmcInputFrame) {
	when (msg.address) {
		"/VMC/Ext/Bone/Pos" -> {
			val name = (msg.args.getOrNull(0) as? OscArg.String)?.value ?: return
			val bodyPart = UNITY_BONE_TO_BODY_PART[name.lowercase()] ?: return
			val (pos, rot) = parseVmcTransform(msg.args, startIndex = 1) ?: return
			frame.boneLocalPositions[bodyPart] = pos
			frame.boneLocalRotations[bodyPart] = rot
		}

		"/VMC/Ext/Root/Pos" -> {
			val (pos, rot) = parseVmcTransform(msg.args, startIndex = 1) ?: return
			frame.rootPosition = pos
			frame.rootRotation = rot
		}

		"/VMC/Ext/Hmd/Pos", "/VMC/Ext/Con/Pos", "/VMC/Ext/Tra/Pos" ->
			decodePoseTrackerMessage(msg, frame, deviceScale = false)

		"/VMC/Ext/Hmd/Pos/Local", "/VMC/Ext/Con/Pos/Local", "/VMC/Ext/Tra/Pos/Local" ->
			decodePoseTrackerMessage(msg, frame, deviceScale = true)
	}
}

private fun decodePoseTrackerMessage(msg: OscMessage, frame: VmcInputFrame, deviceScale: Boolean) {
	val serial = (msg.args.getOrNull(0) as? OscArg.String)?.value ?: return
	val (pos, rot) = parseVmcTransform(msg.args, startIndex = 1) ?: return
	// A /Pos/Local for the same serial always takes precedence over a plain /Pos,
	// regardless of which one arrives first in the bundle.
	if (deviceScale || frame.poseTrackers[serial]?.deviceScale != true) {
		frame.poseTrackers[serial] = VmcPoseTracker(serial, pos, rot, deviceScale)
	}
}

/**
 * Decodes the position + rotation starting at [startIndex], applying the Unity to SlimeVR
 * handedness mirror. The exact inverse of transformMessage in output-encoder.kt.
 * Returns null if any of the seven floats are missing or not numeric.
 */
internal fun parseVmcTransform(args: List<OscArg>, startIndex: Int): Pair<Vector3, Quaternion>? {
	val px = args.getOrNull(startIndex)?.asFloatOrNull() ?: return null
	val py = args.getOrNull(startIndex + 1)?.asFloatOrNull() ?: return null
	val pz = args.getOrNull(startIndex + 2)?.asFloatOrNull() ?: return null
	val qx = args.getOrNull(startIndex + 3)?.asFloatOrNull() ?: return null
	val qy = args.getOrNull(startIndex + 4)?.asFloatOrNull() ?: return null
	val qz = args.getOrNull(startIndex + 5)?.asFloatOrNull() ?: return null
	val qw = args.getOrNull(startIndex + 6)?.asFloatOrNull() ?: return null

	return Vector3(px, py, -pz) to Quaternion(-qw, qx, qy, -qz)
}

data class VmcBoneTransform(val rotation: Quaternion, val position: Vector3)

/**
 * Inverse of vmcLocalRotation/vmcLocalPosition (output-encoder.kt): walks VMC_INPUT_BONE_ORDER
 * parent-before-child, accumulating VMC's parent-local bone rotations/positions into world
 * rotations and positions.
 */
fun vmcWorldTransforms(
	locals: BodyPartMap<Quaternion>,
	localPositions: BodyPartMap<Vector3>,
	rootRotation: Quaternion,
	rootPosition: Vector3,
	scale: Float,
): BodyPartMap<VmcBoneTransform> {
	val restAdjusted = bodyPartMap<Quaternion>()
	val modelPositions = bodyPartMap<Vector3>()
	val result = bodyPartMap<VmcBoneTransform>()

	for (bone in VMC_INPUT_BONE_ORDER) {
		val parent = VMC_INPUT_BONE_PARENTS[bone]
		val local = locals[bone] ?: Quaternion.IDENTITY
		val localPosition = localPositions[bone] ?: Vector3.ZERO
		val parentAdjusted = parent?.let { restAdjusted[it] }

		val adjusted = if (parentAdjusted != null) parentAdjusted * local else local
		restAdjusted[bone] = adjusted

		val modelPosition = if (parent != null && parentAdjusted != null) {
			(modelPositions[parent] ?: Vector3.ZERO) + parentAdjusted.sandwich(localPosition)
		} else {
			localPosition
		}
		modelPositions[bone] = modelPosition

		val rest = VMC_REST_ROTATIONS[bone] ?: Quaternion.IDENTITY
		result[bone] = VmcBoneTransform(
			rotation = rootRotation * (adjusted * rest),
			position = (rootPosition + rootRotation.sandwich(modelPosition)) * scale,
		)
	}

	return result
}
