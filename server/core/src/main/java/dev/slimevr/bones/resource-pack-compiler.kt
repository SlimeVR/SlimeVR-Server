package dev.slimevr.bones

import dev.slimevr.resourcepacks.Offset
import dev.slimevr.resourcepacks.ParsedResourcePack
import dev.slimevr.resourcepacks.ProportionDefinition
import dev.slimevr.resourcepacks.ResourcePackCatalog
import dev.slimevr.resourcepacks.SourcedResource
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.connection.BoneDefinition
import dev.slimevr.resourcepacks.BoneDefinition as PackBoneDefinition
import solarxr_protocol.connection.BoneRegistry as WireBoneRegistry

data class ResourcePackCompilationDiagnostic(val packId: String, val path: String, val message: String)

class ResourcePackCompilationException(val diagnostics: List<ResourcePackCompilationDiagnostic>) :
	IllegalArgumentException(
		diagnostics.joinToString("\n") { "${it.packId}:${it.path}: ${it.message}" },
	)

private data class BoneContribution(
	val pack: ParsedResourcePack,
	val resource: SourcedResource<PackBoneDefinition>,
)

private data class ProportionContribution(
	val pack: ParsedResourcePack,
	val resource: SourcedResource<ProportionDefinition>,
)

/**
 * Compiles the complete, ordered resource-pack stack into the runtime [CompiledSkeleton]: bone
 * identity and hierarchy, the proportion catalog, and each bone's offset.
 *
 * Core owns the standard [BodyPart] definitions and their stable IDs. Every subsequent pack can
 * contribute new bones and proportions; extension bones receive dense IDs in catalog order.
 */
fun compileResourcePacks(catalog: ResourcePackCatalog): CompiledSkeleton {
	val diagnostics = mutableListOf<ResourcePackCompilationDiagnostic>()
	val packs = listOf(catalog.core) + catalog.userPacks
	val packIds = mutableMapOf<String, ParsedResourcePack>()
	for (pack in packs) {
		val id = pack.manifest.value.id
		if (packIds.put(id, pack) != null) {
			diagnostics += ResourcePackCompilationDiagnostic(id, pack.manifest.path, "Duplicate pack ID '$id'")
		}
	}
	if (catalog.core.manifest.value.id != "slimevr:core") {
		diagnostics += ResourcePackCompilationDiagnostic(catalog.core.manifest.value.id, catalog.core.manifest.path, "Expected bundled core pack ID 'slimevr:core'")
	}

	val bonesByKey = linkedMapOf<String, BoneContribution>()
	for (pack in packs) {
		for (resource in pack.bones) {
			val contribution = BoneContribution(pack, resource)
			val previous = bonesByKey.put(resource.value.key, contribution)
			if (previous != null) {
				diagnostics += ResourcePackCompilationDiagnostic(
					pack.manifest.value.id,
					resource.path,
					"Duplicate bone key '${resource.value.key}' (first defined by ${previous.pack.manifest.value.id}:${previous.resource.path})",
				)
			}
		}
	}

	val proportionsByKey = linkedMapOf<String, ProportionContribution>()
	for (pack in packs) {
		for (resource in pack.proportions) {
			val contribution = ProportionContribution(pack, resource)
			val previous = proportionsByKey.put(resource.value.key, contribution)
			if (previous != null) {
				diagnostics += ResourcePackCompilationDiagnostic(
					pack.manifest.value.id,
					resource.path,
					"Duplicate proportion key '${resource.value.key}' (first defined by ${previous.pack.manifest.value.id}:${previous.resource.path})",
				)
			}
		}
	}

	val standardParts = BodyPart.entries.filter { it != BodyPart.NONE }
	val standardPartsByKey = standardParts.associateBy(BodyPart::key)
	for (part in standardParts) {
		if (part.key !in bonesByKey) {
			diagnostics += ResourcePackCompilationDiagnostic(catalog.core.manifest.value.id, catalog.core.manifest.path, "Missing standard bone '${part.key}'")
		}
	}
	for ((key, contribution) in bonesByKey) {
		val part = standardPartsByKey[key]
		if (part != null && contribution.pack !== catalog.core) {
			diagnostics += ResourcePackCompilationDiagnostic(contribution.pack.manifest.value.id, contribution.resource.path, "Standard bone '$key' belongs to the core pack")
		}
		if (part == null && contribution.pack === catalog.core) {
			diagnostics += ResourcePackCompilationDiagnostic(catalog.core.manifest.value.id, contribution.resource.path, "Core pack defines non-standard bone '$key'")
		}
		contribution.resource.value.parent?.let { parent ->
			if (parent !in bonesByKey) diagnostics += ResourcePackCompilationDiagnostic(contribution.pack.manifest.value.id, contribution.resource.path, "Unknown parent bone '$parent'")
		}
	}
	if (diagnostics.isEmpty()) validateHierarchy(catalog.core, bonesByKey, diagnostics)
	if (diagnostics.isNotEmpty()) throw ResourcePackCompilationException(diagnostics)

	val standardContributions = standardParts.map { part -> bonesByKey.getValue(part.key) }
	val extensions = bonesByKey.values.filter { it.resource.value.key !in standardPartsByKey }
	val allContributions = standardContributions + extensions
	val idByKey = allContributions.mapIndexed { index, contribution -> contribution.resource.value.key to (index + 1).toUShort() }.toMap()

	val registry = BoneRegistry.from(
		WireBoneRegistry(
			bones = allContributions.mapIndexed { index, contribution ->
				val definition = contribution.resource.value
				val standardPart = standardPartsByKey[definition.key]
				BoneDefinition(
					id = (index + 1).toUShort(),
					key = definition.key,
					displayName = standardPart?.name?.replace('_', ' ')?.lowercase() ?: displayName(contribution),
					parent = definition.parent?.let(idByKey::getValue) ?: 0.toUShort(),
				)
			},
		),
	)

	val proportions = proportionsByKey.mapValues { (key, contribution) ->
		val definition = contribution.resource.value
		CompiledProportion(
			key = key,
			nameKey = definition.nameKey,
			descriptionKey = definition.descriptionKey,
			contributesToHeight = definition.contributesToHeight ?: false,
			minimum = definition.minimum,
			maximum = definition.maximum,
			default = definition.default,
		)
	}

	val tailOffsets = BoneMap.of<CompiledOffset>(registry)
	val headOffsets = BoneMap.of<CompiledOffset>(registry)
	for (contribution in allContributions) {
		val definition = contribution.resource.value
		val boneId = registry[definition.key] ?: continue
		definition.tailOffset?.let { tailOffsets[boneId] = compileOffset(it, contribution, proportionsByKey, diagnostics) }
		definition.headOffset?.let { headOffsets[boneId] = compileOffset(it, contribution, proportionsByKey, diagnostics) }
	}
	if (diagnostics.isNotEmpty()) throw ResourcePackCompilationException(diagnostics)

	return CompiledSkeleton(registry, proportions, tailOffsets, headOffsets)
}

private fun compileOffset(
	offset: Offset,
	contribution: BoneContribution,
	proportionsByKey: Map<String, ProportionContribution>,
	diagnostics: MutableList<ResourcePackCompilationDiagnostic>,
): CompiledOffset {
	val base = offset.base?.let { Vector3(it.x, it.y, it.z) } ?: Vector3.ZERO
	val terms = (offset.terms ?: emptyList()).map { term ->
		if (term.proportion !in proportionsByKey) {
			diagnostics += ResourcePackCompilationDiagnostic(
				contribution.pack.manifest.value.id,
				contribution.resource.path,
				"Unknown proportion '${term.proportion}'",
			)
		}
		CompiledOffsetTerm(term.proportion, Vector3(term.direction.x, term.direction.y, term.direction.z))
	}
	return CompiledOffset(base, terms)
}

private const val DEFAULT_LANGUAGE_PATH = "assets/lang/en.json"

/**
 * The bone's compiled-in display name: its English translation if the pack ships one, else its
 * bare nameKey. [BoneDefinition] needs a resolved string rather than a translation key, so this
 * bakes in one language at compile time, same as [BoneRegistry.standard] does for standard bones.
 */
private fun displayName(contribution: BoneContribution): String {
	val nameKey = contribution.resource.value.nameKey
	val translations = contribution.pack.languages.firstOrNull { it.path == DEFAULT_LANGUAGE_PATH }?.value?.translations
	return translations?.get(nameKey) ?: nameKey
}

private fun validateHierarchy(
	core: ParsedResourcePack,
	bonesByKey: Map<String, BoneContribution>,
	diagnostics: MutableList<ResourcePackCompilationDiagnostic>,
) {
	// The registry is one hierarchy spanning every pack, so exactly one root is required across
	// the whole stack, not per pack.
	val roots = bonesByKey.values.filter { it.resource.value.parent == null }
	if (roots.isEmpty()) {
		diagnostics += ResourcePackCompilationDiagnostic(core.manifest.value.id, core.manifest.path, "A resource-pack registry must have exactly one root; found none")
	} else if (roots.size > 1) {
		for (contribution in roots) {
			diagnostics += ResourcePackCompilationDiagnostic(
				contribution.pack.manifest.value.id,
				contribution.resource.path,
				"A resource-pack registry must have exactly one root; found ${roots.size}",
			)
		}
	}

	val reportedCycles = mutableSetOf<Set<String>>()
	for ((key, contribution) in bonesByKey) {
		val visited = linkedSetOf<String>()
		var current: String? = key
		while (current != null && visited.add(current)) current = bonesByKey.getValue(current).resource.value.parent
		if (current != null) {
			val cycle = visited.dropWhile { it != current }.toSet()
			if (reportedCycles.add(cycle)) {
				diagnostics += ResourcePackCompilationDiagnostic(
					contribution.pack.manifest.value.id,
					contribution.resource.path,
					"Bone hierarchy contains a cycle: ${cycle.joinToString(" -> ")}",
				)
			}
		}
	}
}
