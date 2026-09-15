package dev.slimevr.vmc

import dev.slimevr.bones.BodyPart
import dev.slimevr.bones.BoneId
import dev.slimevr.bones.BoneMap
import dev.slimevr.bones.key
import dev.slimevr.config.Settings
import dev.slimevr.logging.AppLogger
import dev.slimevr.resourcepacks.bones.CompiledSkeleton
import dev.slimevr.skeleton.Skeleton
import dev.slimevr.util.formatExceptionMessage
import io.github.axisangles.ktmath.Vector3
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import solarxr_protocol.rpc.VMCOSCVrmState

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
	val bindOffsets: BoneMap<Vector3>,
	val hipLocalPosition: Vector3,
	/** Floor-to-neck height, on the same basis as Skeleton.skeletonHeight. Used to scale VMC input positions. */
	val vrmHeight: Float,
)

fun buildVrmGeometry(definition: CompiledSkeleton, reader: VrmReader): VrmGeometry {
	val registry = definition.registry
	val bindOffsets = BoneMap.of<Vector3>(registry)
	for (boneId in definition.vmcNamedBones) {
		val name = definition.vmcOutputOf(boneId)?.names?.first() ?: continue
		bindOffsets[boneId] = reader.offsetForBone(name) ?: Vector3.ZERO
	}

	val hip = registry[BodyPart.HIP.key]!!
	val neck = registry[BodyPart.NECK.key]!!
	val hipLocalPosition = bindOffsets[hip] ?: Vector3.ZERO

	// Floor-to-neck height, summed by walking the pack's own real parent chain from hip up to
	// neck, rather than a fixed anatomy list, so it still works if a pack changes what's between
	// them (e.g. no upper_waist, or an extra bone). Any bone on that chain missing a VRM offset
	// (lower_waist has no VMC output at all) simply contributes 0.
	var vrmHeight = 0f
	var current: BoneId? = hip
	while (current != null) {
		vrmHeight += (bindOffsets[current] ?: Vector3.ZERO).y
		if (current == neck) break
		current = registry.parentOf(current)
	}

	return VrmGeometry(
		bindOffsets = bindOffsets,
		hipLocalPosition = hipLocalPosition,
		vrmHeight = vrmHeight,
	)
}

class VMCVrmBehaviour(private val skeleton: Skeleton, private val settings: Settings) : VMCBehaviour {
	override fun observe(receiver: VMCManager) {
		settings.context.state
			.map { it.data.vmcConfig.vrmJson }
			.distinctUntilChanged()
			.onEach { vrmJson ->
				val json = vrmJson?.takeIf { it.isNotEmpty() }
				if (json == null) {
					receiver.context.dispatch(VMCActions.SetVrm(state = VMCOSCVrmState.NONE))
					return@onEach
				}

				try {
					val vrm = buildVrmGeometry(skeleton.definition, VrmReader(json))
					receiver.context.dispatch(VMCActions.SetVrm(state = VMCOSCVrmState.LOADED, vrm = vrm))
				} catch (e: Exception) {
					val message = "Failed to parse VRM JSON"
					AppLogger.vmc.error(e, message)
					receiver.context.dispatch(
						VMCActions.SetVrm(state = VMCOSCVrmState.ERROR, error = formatExceptionMessage(message, e)),
					)
				}
			}
			.launchIn(receiver.context.scope)
	}
}
