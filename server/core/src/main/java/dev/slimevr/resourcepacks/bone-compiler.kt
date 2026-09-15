package dev.slimevr.resourcepacks

import dev.slimevr.bones.BoneId
import dev.slimevr.bones.BoneRegistry
import dev.slimevr.bones.Constraint
import dev.slimevr.bones.HingeConstraint
import dev.slimevr.bones.LooseHingeConstraint
import dev.slimevr.bones.TwistSwingConstraint
import io.github.axisangles.ktmath.Vector3
import com.jme3.math.FastMath.DEG_TO_RAD as degToRad
import dev.slimevr.resourcepacks.Constraint as PackConstraint
import dev.slimevr.resourcepacks.HingeConstraint as PackHingeConstraint
import dev.slimevr.resourcepacks.LooseHingeConstraint as PackLooseHingeConstraint
import dev.slimevr.resourcepacks.TwistSwingConstraint as PackTwistSwingConstraint

/** The pack that last touched [field], or null if none did. */
internal fun BoneContribution.originOrNull(field: BoneField): ResourceOrigin? = originOrNull(field.path)

/** [originOrNull] falling back to this bone's own defining pack. */
internal fun BoneContribution.origin(field: BoneField): ResourceOrigin = originOrNull(field.path) ?: definitionOrigin

internal fun compileConstraint(constraint: PackConstraint): Constraint = when (constraint) {
	is PackTwistSwingConstraint -> TwistSwingConstraint(
		twist = constraint.twistDegrees * degToRad,
		swing = constraint.swingDegrees * degToRad,
		allowedDeviation = (constraint.allowedDeviationDegrees ?: 0f) * degToRad,
	)

	is PackHingeConstraint -> HingeConstraint(
		min = constraint.minDegrees * degToRad,
		max = constraint.maxDegrees * degToRad,
		hingeAxis = Vector3(constraint.axis.x, constraint.axis.y, constraint.axis.z),
	)

	is PackLooseHingeConstraint -> LooseHingeConstraint(
		min = constraint.minDegrees * degToRad,
		max = constraint.maxDegrees * degToRad,
		allowedDeviation = constraint.allowedDeviationDegrees * degToRad,
		hingeAxis = Vector3(constraint.axis.x, constraint.axis.y, constraint.axis.z),
	)
}

/** Resolves a bone key referenced from [origin], reporting [context] on an unknown key. */
internal fun resolveBoneKey(
	key: String,
	origin: ResourceOrigin,
	context: String,
	registry: BoneRegistry,
	diagnostics: MutableList<ResourcePackCompilationDiagnostic>,
): BoneId {
	val boneId = registry[key]
	if (boneId != null) return boneId
	diagnostics += origin.diagnostic("Unknown bone '$key' in $context")
	return BoneId(0u)
}

/** Resolves one rotation-fallback source key (or the `"parent"` shorthand) to a [BoneId]. */
internal fun resolveFallbackBone(
	key: String,
	contribution: BoneContribution,
	registry: BoneRegistry,
	diagnostics: MutableList<ResourcePackCompilationDiagnostic>,
): BoneId {
	val target = if (key == "parent") contribution.resource.value.parent else key
	val origin = contribution.origin(BoneField.RotationFallback)
	if (target == null) {
		diagnostics += origin.diagnostic("Unknown bone '$key' in rotationFallback")
		return BoneId(0u)
	}
	return resolveBoneKey(target, origin, "rotationFallback", registry, diagnostics)
}

internal fun compileOffset(
	offset: Offset,
	origin: ResourceOrigin,
	proportionsByKey: Map<String, ProportionContribution>,
	diagnostics: MutableList<ResourcePackCompilationDiagnostic>,
): CompiledOffset {
	val base = offset.base?.let { Vector3(it.x, it.y, it.z) } ?: Vector3.ZERO
	val terms = (offset.terms ?: emptyList()).map { term ->
		if (term.proportion !in proportionsByKey) {
			diagnostics += origin.diagnostic("Unknown proportion '${term.proportion}'")
		}
		CompiledOffsetTerm(term.proportion, Vector3(term.direction.x, term.direction.y, term.direction.z))
	}
	return CompiledOffset(base, terms)
}

/**
 * Orders an acyclic fallback chain source-before-consumer. A cycle keeps its existing hierarchy
 * order
 */
internal fun <T> orderFallbackDependencies(
	entries: List<Pair<BoneId, T>>,
	dependency: (T) -> BoneId?,
): List<Pair<BoneId, T>> {
	val remaining = entries.toMutableList()
	val result = mutableListOf<Pair<BoneId, T>>()
	while (remaining.isNotEmpty()) {
		val pending = remaining.associate { it.first to it.second }
		val readyIndex = remaining.indexOfFirst { (_, value) ->
			val source = dependency(value)
			source == null || source !in pending
		}
		val cycleIndex = remaining.indexOfFirst { (boneId, _) ->
			var source = dependency(pending.getValue(boneId))
			val visited = mutableSetOf<BoneId>()
			while (source != null && source != boneId && visited.add(source)) source = pending[source]?.let(dependency)
			source == boneId
		}
		val nextIndex = listOf(readyIndex, cycleIndex).filter { it >= 0 }.minOrNull() ?: 0
		result += remaining.removeAt(nextIndex)
	}
	return result
}

private const val DEFAULT_LANGUAGE_PATH = "assets/lang/en.json"

/**
 * The bone's compiled-in display name: its English translation if the pack ships one, else its
 * bare nameKey
 */
internal fun displayName(contribution: BoneContribution): String {
	val nameKey = contribution.resource.value.nameKey
	val translations = contribution.pack.languages.firstOrNull { it.path == DEFAULT_LANGUAGE_PATH }?.value?.translations
	return translations?.get(nameKey) ?: nameKey
}

internal fun validateHierarchy(
	core: ParsedResourcePack,
	bonesByKey: Map<String, BoneContribution>,
	diagnostics: MutableList<ResourcePackCompilationDiagnostic>,
) {
	val roots = bonesByKey.values.filter { it.resource.value.parent == null }
	if (roots.isEmpty()) {
		val overrideOrigin = bonesByKey.values.mapNotNull { it.originOrNull(BoneField.Parent) }.lastOrNull()
		diagnostics += overrideOrigin?.diagnostic("A resource-pack registry must have exactly one root; found none")
			?: ResourcePackCompilationDiagnostic(core.manifest.value.id, core.manifest.path, "A resource-pack registry must have exactly one root; found none")
	} else if (roots.size > 1) {
		for (contribution in roots) {
			diagnostics += contribution.origin(BoneField.Parent).diagnostic("A resource-pack registry must have exactly one root; found ${roots.size}")
		}
	}

	reportCycles(bonesByKey.keys, { bonesByKey.getValue(it).resource.value.parent }) { start, _, cycle ->
		val overrideOrigin = cycle.mapNotNull { bonesByKey.getValue(it).originOrNull(BoneField.Parent) }.lastOrNull()
		diagnostics += bonesByKey.getValue(start).origin(overrideOrigin).diagnostic("Bone hierarchy contains a cycle: ${cycle.joinToString(" -> ")}")
	}
}
