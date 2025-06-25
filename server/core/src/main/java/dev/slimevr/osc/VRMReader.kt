package dev.slimevr.osc

import io.eiren.util.logging.LogManager
import io.github.axisangles.ktmath.Vector3
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.*

private val jsonIgnoreKeys = Json { ignoreUnknownKeys = true }
class VRMReader(vrmJson: String) {

	private val data: GLTF = jsonIgnoreKeys.decodeFromString(vrmJson)

	fun getOffsetForBone(unityBone: UnityBone): Vector3 {
		val node = try {
			if (data.extensions.vrmV1 != null) {
				if (data.extensions.vrmV1.specVersion != "1.0") {
					LogManager.warning("[VRMReader] VRM version is not 1.0")
				}
				data.extensions.vrmV1.humanoid.humanBones.getValue(unityBone).node
			} else {
				data.extensions.vrmV0?.humanoid?.humanBones?.first {
					it.bone.equals(unityBone.stringVal, ignoreCase = true)
				}?.node
			}
		} catch (_: NoSuchElementException) {
			LogManager.warning("[VRMReader] Bone ${unityBone.stringVal} not found in JSON")
			null
		} ?: return Vector3.NULL

		val translationNode = data.nodes[node].translation ?: return Vector3.NULL

		return Vector3(translationNode[0].toFloat(), translationNode[1].toFloat(), translationNode[2].toFloat())
	}
}

@Serializable
data class GLTF(
	val extensions: Extensions,
	val extensionsUsed: List<String>,
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
	val humanBones: Map<UnityBone, HumanBoneV1>,
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
	val armStretch: Double,
	val legStretch: Double,
	val upperArmTwist: Double,
	val lowerArmTwist: Double,
	val upperLegTwist: Double,
	val lowerLegTwist: Double,
	val feetSpacing: Double,
	val hasTranslationDoF: Boolean,
)

@Serializable
data class HumanBoneV0(
	val bone: String,
	val node: Int,
	val useDefaultValues: Boolean,
)

@Serializable
data class Node(
	val translation: List<Double>? = null,
	val rotation: List<Double>? = null,
	val scale: List<Double>? = null,
	// GLTF says that there can be a matrix instead of translation,
	// rotation and scale, so we need to support that too in the future.
	// val matrix: List<List<Double>>,
	val children: List<Int> = emptyList(),
)
