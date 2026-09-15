package dev.slimevr.resourcepacks

import dev.slimevr.bones.BoneId
import dev.slimevr.bones.BoneMap
import dev.slimevr.bones.BoneRegistry
import dev.slimevr.bones.BoneSet
import dev.slimevr.bones.Constraint
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.rpc.RoutingOutput
import solarxr_protocol.rpc.SkeletonBone

private fun resolveDefault(default: ProportionDefault, height: Float): Float = when (default) {
	is FixedProportionDefault -> default.value
	is HeightRatioProportionDefault -> default.value * height
}

/** Height, in meters, that the core pack's `heightRatio` proportion defaults are expressed against. */
const val REFERENCE_HEIGHT = 1.58f

/** The proportion key corresponding to this [SkeletonBone].
 * FIXME: Remove SkeletonBone completely. Also from solarxr
 */
val SkeletonBone.proportionKey: String get() = "slimevr:${name.lowercase()}"

/** Inverse of [SkeletonBone.proportionKey]; null for a proportion key no [SkeletonBone] names (an extension pack's). */
fun skeletonBoneOf(proportionKey: String): SkeletonBone? = SkeletonBone.entries.firstOrNull { it.proportionKey == proportionKey }

data class CompiledProportion(
	val key: String,
	val nameKey: String,
	val descriptionKey: String?,
	val contributesToHeight: Boolean,
	val minimum: Float?,
	val maximum: Float?,
	val default: ProportionDefault,
)

data class CompiledOffsetTerm(val proportion: String, val direction: Vector3)

/** A bone's resolved offset: a fixed [base] plus a weighted sum of proportion [terms]. */
data class CompiledOffset(val base: Vector3, val terms: List<CompiledOffsetTerm>)

/** Resolved bone geometry mapping tail and head vectors for all bones. */
class BoneOffsets(val tail: BoneMap<Vector3>, val head: BoneMap<Vector3>)

/** A bone's compiled VMC output: its Unity name(s), resolved output/input parent, and rest rotation. */
data class CompiledVmcOutput(
	val names: List<String>,
	val outputParent: BoneId?,
	val inputParent: BoneId?,
	val restRotation: Quaternion,
)

/** A compiled VRChat OSC emission, with its optional relative bone resolved to an ID. */
data class CompiledEmitEntry(
	val from: EmitSource,
	val relativeTo: BoneId?,
	val steps: List<PipelineStep>,
)

/** A bone's compiled VRChat output: whether it's forced on, and its OSC emissions by address. */
data class CompiledVrchatOutput(val required: Boolean, val emit: Map<String, CompiledEmitEntry>)

/**
 * Everything [compileResourcePacks] resolves for one bone: its offsets, constraint, routing
 * facts, and output/input declarations. A plain bone with none of these has every field null.
 */
data class CompiledBone(
	val tailOffset: CompiledOffset? = null,
	val headOffset: CompiledOffset? = null,
	val constraint: Constraint? = null,
	val mirror: BoneId? = null,
	/** Null if the bone declares no `candidateSources` at all; distinct from an empty list. */
	val candidateSources: List<BoneId>? = null,
	val batterySources: List<BoneId>? = null,
	val overridable: Boolean = false,
	val driverOutput: Boolean = false,
	val vmc: CompiledVmcOutput? = null,
	val vrchat: CompiledVrchatOutput? = null,
	val vrchatInput: String? = null,
)

/**
 * The compiled bone registry, proportion catalog, and per-bone data produced by
 * [compileResourcePacks].
 */
class CompiledSkeleton(
	val registry: BoneRegistry,
	val proportions: Map<String, CompiledProportion>,
	private val bones: BoneMap<CompiledBone>,
	val copyRotationFallbacks: List<Pair<BoneId, BoneId>>,
	val firstActiveRotationFallbacks: List<Pair<BoneId, List<BoneId>>>,
	val vmcInputOrder: List<BoneId>,
) {
	private val boneEntries = bones.entries

	val constraints: BoneMap<Constraint> = BoneMap.of<Constraint>(registry).also { map ->
		for ((boneId, bone) in boneEntries) bone.constraint?.let { map[boneId] = it }
	}

	private val driverOutputs = BoneSet.of(registry, boneEntries.filter { it.value.driverOutput }.map { it.key })

	/** Bones with a VMC output declaration. Equal to [acceptedBones] of [RoutingOutput.VMC]. */
	val vmcNamedBones: Set<BoneId> = BoneSet.of(registry, boneEntries.filter { it.value.vmc != null }.map { it.key })

	/** Bones with a VRChat OSC emission declared. Equal to [acceptedBones] of [RoutingOutput.VRC_OSC]. */
	val vrchatEmittingBones: Set<BoneId> = BoneSet.of(registry, boneEntries.filter { it.value.vrchat != null }.map { it.key })

	private val vrchatRequired = BoneSet.of(registry, boneEntries.filter { it.value.vrchat?.required == true }.map { it.key })

	val overridableBones: BoneSet = BoneSet.of(registry, boneEntries.filter { it.value.overridable }.map { it.key })

	/** Bones with a `candidateSources` list, each naming the bones that make it a routing candidate. */
	val candidateBones: Set<BoneId> = BoneSet.of(registry, boneEntries.filter { it.value.candidateSources != null }.map { it.key })

	val vrchatInputAddresses: Map<String, BoneId> =
		boneEntries.mapNotNull { (boneId, bone) -> bone.vrchatInput?.let { it to boneId } }.toMap()

	/** Exact VMC/Unity bone name to the bone it names. */
	val unityNameToBone: Map<String, BoneId> =
		boneEntries.flatMap { (boneId, bone) -> bone.vmc?.names.orEmpty().map { it to boneId } }.toMap()

	/** Bones [output] can receive, from each bone's `outputs.driver`/`outputs.vmc`/`outputs.vrchat`. */
	fun acceptedBones(output: RoutingOutput): Set<BoneId> = when (output) {
		RoutingOutput.DRIVER -> driverOutputs
		RoutingOutput.VMC -> vmcNamedBones
		RoutingOutput.VRC_OSC -> vrchatEmittingBones
	}

	/**
	 * Bones [output] that are required and cannot be disabled
	 */
	fun requiredBones(output: RoutingOutput): Set<BoneId> = when (output) {
		RoutingOutput.DRIVER -> emptySet()
		RoutingOutput.VMC -> vmcNamedBones
		RoutingOutput.VRC_OSC -> vrchatRequired
	}

	fun candidateSourcesOf(boneId: BoneId): List<BoneId> = bones[boneId]?.candidateSources ?: emptyList()

	/** Ordered bones whose assigned tracker's battery represents [boneId]'s battery. */
	fun batterySourcesOf(boneId: BoneId): List<BoneId> = bones[boneId]?.batterySources ?: emptyList()

	fun emitEntriesOf(boneId: BoneId): Map<String, CompiledEmitEntry> = bones[boneId]?.vrchat?.emit ?: emptyMap()

	fun vmcOutputOf(boneId: BoneId): CompiledVmcOutput? = bones[boneId]?.vmc

	/** A bone's VMC mirror-image bone, itself if it has none. */
	fun mirrorOf(boneId: BoneId): BoneId = bones[boneId]?.mirror ?: boneId

	/** Every proportion's default value at [height], keyed by proportion key. */
	fun defaultProportionValues(height: Float = REFERENCE_HEIGHT): Map<String, Float> {
		val result = mutableMapOf<String, Float>()
		for ((key, proportion) in proportions) result[key] = resolveDefault(proportion.default, height)
		return result
	}

	/** The default value of every height-ratio proportion at [height], keyed by proportion key. */
	fun heightScaledProportionValues(height: Float): Map<String, Float> {
		val result = mutableMapOf<String, Float>()
		for ((key, proportion) in proportions) {
			if (proportion.default is HeightRatioProportionDefault) result[key] = resolveDefault(proportion.default, height)
		}
		return result
	}

	/** Sums the `contributesToHeight` proportions in [values] to derive standing height. */
	fun height(values: Map<String, Float>): Float {
		var sum = 0f
		for ((key, proportion) in proportions) if (proportion.contributesToHeight) sum += values[key] ?: 0f
		return sum
	}

	private fun resolve(offset: CompiledOffset, values: Map<String, Float>): Vector3 {
		var sum = offset.base
		for (term in offset.terms) sum += term.direction * (values[term.proportion] ?: 0f)
		return sum
	}

	private fun isResolvable(offset: CompiledOffset, values: Map<String, Float>): Boolean = offset.terms.isEmpty() || offset.terms.any { it.proportion in values }

	fun toBoneOffsets(values: Map<String, Float>): BoneOffsets {
		val tail = BoneMap.of<Vector3>(registry)
		val head = BoneMap.of<Vector3>(registry)
		for ((boneId, bone) in boneEntries) {
			bone.tailOffset?.let { if (isResolvable(it, values)) tail[boneId] = resolve(it, values) }
			bone.headOffset?.let { if (isResolvable(it, values)) head[boneId] = resolve(it, values) }
		}
		return BoneOffsets(tail, head)
	}

	/**
	 * Inverse of [toBoneOffsets]: recovers each proportion's value from resolved offsets
	 */
	fun toProportionValues(tail: BoneMap<Vector3>, head: BoneMap<Vector3>): Map<String, Float> {
		val result = mutableMapOf<String, Float>()
		fun collect(resolved: BoneMap<Vector3>, offsetOf: (CompiledBone) -> CompiledOffset?) {
			for ((boneId, vector) in resolved) {
				val offset = bones[boneId]?.let(offsetOf) ?: continue
				val remainder = vector - offset.base
				for (term in offset.terms) {
					if (term.proportion in result) continue
					val lenSq = term.direction.lenSq()
					if (lenSq == 0f) continue
					result[term.proportion] = remainder.dot(term.direction) / lenSq
				}
			}
		}
		collect(tail) { it.tailOffset }
		collect(head) { it.headOffset }
		return result
	}
}
