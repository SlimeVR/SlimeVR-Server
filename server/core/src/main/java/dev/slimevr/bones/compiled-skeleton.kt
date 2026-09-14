package dev.slimevr.bones

import dev.slimevr.resourcepacks.FixedProportionDefault
import dev.slimevr.resourcepacks.HeightRatioProportionDefault
import dev.slimevr.resourcepacks.ProportionDefault
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.rpc.SkeletonBone

private fun resolveDefault(default: ProportionDefault, height: Float): Float = when (default) {
	is FixedProportionDefault -> default.value
	is HeightRatioProportionDefault -> default.value * height
}

/** Height, in meters, that the core pack's `heightRatio` proportion defaults are expressed against. */
const val REFERENCE_HEIGHT = 1.58f

/**
 * The proportion key a [SkeletonBone] always names: `"slimevr:" + name.lowercase()`, mirroring
 * [BodyPart.key]. [SkeletonBone] only exists at the SolarXR RPC boundary; the rest of the server
 * addresses proportions by resource key directly.
 *
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

/**
 * Resolved bone geometry. [tail] is the head->tail vector in the bone's own frame; [head] is the
 * parent-tail->head vector in the parent's frame, present only for bones whose pack definition
 * carries a `headOffset`.
 */
class BoneOffsets(val tail: BoneMap<Vector3>, val head: BoneMap<Vector3>)

/**
 * The compiled bone registry, proportion catalog, and per-bone offset produced by
 * [compileResourcePacks].
 */
class CompiledSkeleton(
	val registry: BoneRegistry,
	val proportions: Map<String, CompiledProportion>,
	private val tailOffsets: BoneMap<CompiledOffset>,
	private val headOffsets: BoneMap<CompiledOffset>,
) {
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

	fun toBoneOffsets(values: Map<String, Float>): BoneOffsets {
		val tail = BoneMap.of<Vector3>(registry)
		val head = BoneMap.of<Vector3>(registry)
		for ((boneId, offset) in tailOffsets) if (isResolvable(offset, values)) tail[boneId] = resolve(offset, values)
		for ((boneId, offset) in headOffsets) if (isResolvable(offset, values)) head[boneId] = resolve(offset, values)
		return BoneOffsets(tail, head)
	}

	private fun isResolvable(offset: CompiledOffset, values: Map<String, Float>): Boolean = offset.terms.isEmpty() || offset.terms.any { it.proportion in values }

	/**
	 * Inverse of [toBoneOffsets]: recovers each proportion's value from resolved offsets
	 */
	fun toProportionValues(tail: BoneMap<Vector3>, head: BoneMap<Vector3>): Map<String, Float> {
		val result = mutableMapOf<String, Float>()
		fun collect(resolved: BoneMap<Vector3>, offsets: BoneMap<CompiledOffset>) {
			for ((boneId, vector) in resolved) {
				val offset = offsets[boneId] ?: continue
				val remainder = vector - offset.base
				for (term in offset.terms) {
					if (term.proportion in result) continue
					val lenSq = term.direction.lenSq()
					if (lenSq == 0f) continue
					result[term.proportion] = remainder.dot(term.direction) / lenSq
				}
			}
		}
		collect(tail, tailOffsets)
		collect(head, headOffsets)
		return result
	}
}
