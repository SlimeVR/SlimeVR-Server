package dev.slimevr.vmc

import io.github.axisangles.ktmath.Vector3
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import solarxr_protocol.datatypes.BodyPart

private val vrmJsonParser = Json { ignoreUnknownKeys = true }

class VrmReader(json: String) {
	private val data: GLTF = vrmJsonParser.decodeFromString(json)

	// VRM 1.0 humanBones, lowercased for case-insensitive lookup. Null if the model is VRM 0.x.
	private val v1Bones: Map<String, Int>? = data.extensions.vrmV1?.humanoid?.humanBones
		?.mapKeys { (key, _) -> key.lowercase() }
		?.mapValues { (_, value) -> value.node }

	fun offsetForBone(unityBoneName: String): Vector3? {
		val nodeIndex = v1Bones?.get(unityBoneName.lowercase())
			?: data.extensions.vrmV0?.humanoid?.humanBones
				?.firstOrNull { bone -> bone.bone.equals(unityBoneName, ignoreCase = true) }
				?.node
			?: return null
		val translation = data.nodes.getOrNull(nodeIndex)?.translation ?: return null
		return Vector3(translation[0].toFloat(), translation[1].toFloat(), translation[2].toFloat())
	}
}

@Serializable
data class GLTF(
	val extensions: Extensions,
	val nodes: List<Node>,
)

@Serializable
data class Extensions(
	@SerialName("VRM")
	val vrmV0: VRMV0? = null,
	@SerialName("VRMC_vrm")
	val vrmV1: VRMV1? = null,
)

@Serializable
data class VRMV1(
	val specVersion: String,
	val humanoid: HumanoidV1,
)

@Serializable
data class HumanoidV1(
	val humanBones: Map<String, HumanBoneV1>,
)

@Serializable
data class HumanBoneV1(
	val node: Int,
)

@Serializable
data class VRMV0(
	val humanoid: HumanoidV0,
)

@Serializable
data class HumanoidV0(
	val humanBones: List<HumanBoneV0>,
)

@Serializable
data class HumanBoneV0(
	val bone: String,
	val node: Int,
)

@Serializable
data class Node(
	val translation: List<Double>? = null,
	val rotation: List<Double>? = null,
	val scale: List<Double>? = null,
	val children: List<Int> = emptyList(),
)

// VRM bind-pose geometry derived from a parsed VRM JSON. Used to keep the avatar's
// local bone offsets aligned with the model's own proportions.
data class VrmGeometry(
	val bindOffsets: Map<BodyPart, Vector3>,
	val hipLocalPosition: Vector3,
)

fun buildVrmGeometry(reader: VrmReader): VrmGeometry {
	val bindOffsets = BODY_PART_TO_UNITY_BONE.mapValues { (_, unityName) ->
		reader.offsetForBone(unityName) ?: Vector3.NULL
	}
	fun offset(bodyPart: BodyPart) = bindOffsets[bodyPart] ?: Vector3.NULL

	val hipLocalPosition = offset(BodyPart.HIP)

	return VrmGeometry(
		bindOffsets = bindOffsets,
		hipLocalPosition = hipLocalPosition,
	)
}
