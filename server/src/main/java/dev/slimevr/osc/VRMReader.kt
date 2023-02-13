package dev.slimevr.osc

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.jme3.math.Vector3f
import dev.slimevr.tracking.processor.BoneType
import dev.slimevr.tracking.trackers.UnityBone
import io.eiren.util.logging.LogManager
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.buffer
import java.util.*

class InvalidGltfFile(message: String?) : Exception(message)

class VRMReader(private val vrmPath: String) {
    private val objectMapper: ObjectMapper = ObjectMapper(JsonFactory())
	private val data: GLTF

	init {
//		vrmPath = "C:/Users/mario/Documents/VSeeFace Custom Data/Auy2.3.1.vrm";
		FileSystem.SYSTEM.source(vrmPath.toPath()).use { fileSource ->
			fileSource.buffer().use { bufferedSource ->
				if (bufferedSource.readIntLe() != 0x46546C67) { // "glTF"
					throw InvalidGltfFile("Magic numbers are not the expected ones")
				}

				if (bufferedSource.readIntLe() != 2) {
					throw InvalidGltfFile("Only glTF 2.0 is supported")
				}

				bufferedSource.readIntLe() // length of file
				val jsonLength: Long = bufferedSource.readIntLe().toLong()

				if (bufferedSource.readIntLe() != 0x4E4F534A) { // "JSON"
					throw InvalidGltfFile("JSON chunk not found")
				}

				data = Json.decodeFromString(
					GLTF.serializer(),
					bufferedSource.readUtf8(jsonLength)
				)
			}
		}
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    fun getOffsetForBone(boneType: BoneType): Vector3f? {
        val translation = Vector3f()

		val unityBone = UnityBone.getByBoneType(boneType)
		val bone = try {
			data.extensions.vrm.humanoid.humanoidBones.first { it.bone == unityBone.name }
		} catch (e: NoSuchElementException) {
			LogManager.warning("[VMCReader] Bone ${boneType.name} not found in \"$vrmPath\"")
			return null
		}

		val translationNode = data.nodes[bone.node].translation
		translation.x = translationNode[0].toFloat()
		translation.y = translationNode[1].toFloat()
		translation.z = -translationNode[2].toFloat()

		return translation
	}
}

@Serializable
data class GLTF(
	val extensions: Extensions,
	val extensionsUsed: List<String>,
	val extensionsRequired: List<String>,
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
	val humanoidBones: List<HumanoidBone>,
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
data class HumanoidBone(
	val bone: String,
	val node: Int,
	val useDefaultValues: Boolean,
)

@Serializable
data class Node(
	val translation: List<Double>,
	val rotation: List<Double>,
	val scale: List<Double>,
	// GLTF says that there can be a matrix instead of translation,
	// rotation and scale, so we need to support that too in the future.
	// val matrix: List<List<Double>>,
	val children: List<Int>,
)
