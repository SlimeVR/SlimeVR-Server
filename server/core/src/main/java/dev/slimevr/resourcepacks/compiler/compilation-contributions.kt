package dev.slimevr.resourcepacks.compiler

import dev.slimevr.resourcepacks.ParsedResourcePack
import dev.slimevr.resourcepacks.SourcedResource
import dev.slimevr.resourcepacks.bones.BoneDefinition
import dev.slimevr.resourcepacks.proportions.ProportionDefinition

/** Tracks bone properties by their JSON path to attribute overrides. */
internal sealed class BoneField(val path: List<String>) {
	object Mirror : BoneField(listOf("mirror"))
	object BatterySources : BoneField(listOf("batterySources"))
	object CandidateSources : BoneField(listOf("candidateSources"))
	object Parent : BoneField(listOf("parent"))
	object HeadOffset : BoneField(listOf("headOffset"))
	object TailOffset : BoneField(listOf("tailOffset"))
	object RotationFallback : BoneField(listOf("rotationFallback"))
	object OutputsVmc : BoneField(listOf("outputs", "vmc"))
	object InputsVrchat : BoneField(listOf("inputs", "vrchat"))
	class OutputsVrchatEmit(address: String) : BoneField(listOf("outputs", "vrchat", "emit", address))
}

/** One pack's contribution of a [T]-typed resource, with the origin of whichever pack last overrode each JSON path. */
internal data class Contribution<T>(
	val pack: ParsedResourcePack,
	val resource: SourcedResource<T>,
	/** The pack that last set or removed the property at each path, keyed by that property's JSON path. */
	val origins: Map<List<String>, ResourceOrigin> = emptyMap(),
	val latestOverride: ResourceOrigin? = null,
) {
	internal val definitionOrigin get() = ResourceOrigin(pack, resource.path)

	internal fun originOrNull(path: List<String>): ResourceOrigin? {
		for (length in path.size downTo 1) origins[path.subList(0, length)]?.let { return it }
		return null
	}

	/** An origin already resolved elsewhere (e.g. across several bones), falling back to this resource's own defining pack. */
	fun origin(resolved: ResourceOrigin?): ResourceOrigin = resolved ?: definitionOrigin

	fun validationOrigin(): ResourceOrigin = latestOverride ?: definitionOrigin
}

internal typealias BoneContribution = Contribution<BoneDefinition>
internal typealias ProportionContribution = Contribution<ProportionDefinition>
