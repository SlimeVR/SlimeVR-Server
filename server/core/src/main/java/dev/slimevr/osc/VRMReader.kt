package dev.slimevr.osc

import io.eiren.util.logging.LogManager
import io.github.axisangles.ktmath.Vector3
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.*

private val jsonIgnoreKeys = Json { ignoreUnknownKeys = true }
class VRMReader(vrmJson: String) {

	private val data: GLTF = jsonIgnoreKeys.decodeFromString(vrmJson)

	fun getOffsetForBone(unityBone: UnityBone): Vector3 {
		val bone = try {
			data.extensions.vrm.humanoid.humanBones.first { it.bone.equals(unityBone.stringVal, ignoreCase = true) }
		} catch (e: NoSuchElementException) {
			LogManager.warning("[VRMReader] Bone ${unityBone.stringVal} not found in JSON")
			return Vector3.NULL
		}

		val translationNode = data.nodes[bone.node].translation ?: return Vector3.NULL

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
	val vrm: VRM,
)

@Serializable
data class VRM(
	val humanoid: Humanoid,
)

@Serializable
data class Humanoid(
	val humanBones: List<HumanBone>,
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
data class HumanBone(
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
