package dev.slimevr.bones

import dev.slimevr.resourcepacks.CopyRotationFallback
import dev.slimevr.resourcepacks.FirstActiveRotationFallback
import dev.slimevr.resourcepacks.NoRotationFallback
import dev.slimevr.resourcepacks.Offset
import dev.slimevr.resourcepacks.ParsedResourcePack
import dev.slimevr.resourcepacks.ProportionDefinition
import dev.slimevr.resourcepacks.ResourcePackCatalog
import dev.slimevr.resourcepacks.SourcedResource
import dev.slimevr.resourcepacks.VmcInputParent
import dev.slimevr.resourcepacks.VmcOutput
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.connection.BoneDefinition
import kotlin.math.cos
import kotlin.math.sin
import com.jme3.math.FastMath.DEG_TO_RAD as degToRad
import dev.slimevr.resourcepacks.BoneDefinition as PackBoneDefinition
import dev.slimevr.resourcepacks.Constraint as PackConstraint
import dev.slimevr.resourcepacks.HingeConstraint as PackHingeConstraint
import dev.slimevr.resourcepacks.LooseHingeConstraint as PackLooseHingeConstraint
import dev.slimevr.resourcepacks.TwistSwingConstraint as PackTwistSwingConstraint
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
	val constraints = BoneMap.of<Constraint>(registry)
	val copyFallbacksByBoneId = mutableMapOf<BoneId, Pair<BoneContribution, CopyRotationFallback>>()
	val firstActiveFallbacksByBoneId = mutableMapOf<BoneId, Pair<BoneContribution, FirstActiveRotationFallback>>()
	val driverOutputs = BoneSet.of(registry)
	val vmcOutputs = BoneSet.of(registry)
	val vrchatOutputs = BoneSet.of(registry)
	val vrchatRequired = BoneSet.of(registry)
	val overridableBones = BoneSet.of(registry)
	val candidateSources = BoneMap.of<List<BoneId>>(registry)
	val mirrorOf = BoneMap.of<BoneId>(registry)
	val vmcContributions = mutableMapOf<BoneId, Pair<BoneContribution, VmcOutput>>()
	for (contribution in allContributions) {
		val definition = contribution.resource.value
		val boneId = registry[definition.key] ?: continue
		definition.tailOffset?.let { tailOffsets[boneId] = compileOffset(it, contribution, proportionsByKey, diagnostics) }
		definition.headOffset?.let { headOffsets[boneId] = compileOffset(it, contribution, proportionsByKey, diagnostics) }
		definition.constraint?.let { constraints[boneId] = compileConstraint(it) }
		when (val fallback = definition.rotationFallback) {
			null, is NoRotationFallback -> {}
			is CopyRotationFallback -> copyFallbacksByBoneId[boneId] = contribution to fallback
			is FirstActiveRotationFallback -> firstActiveFallbacksByBoneId[boneId] = contribution to fallback
		}
		definition.outputs?.driver?.let { driverOutputs.add(boneId) }
		definition.outputs?.vmc?.let {
			vmcOutputs.add(boneId)
			vmcContributions[boneId] = contribution to it
		}
		definition.outputs?.vrchat?.let {
			vrchatOutputs.add(boneId)
			if (it.required == true) vrchatRequired.add(boneId)
		}
		if (definition.overridable == true) overridableBones.add(boneId)
		definition.candidateSources?.let { sources ->
			candidateSources[boneId] = sources.map { resolveBoneKey(it, contribution, "candidateSources", registry, diagnostics) }
		}
		definition.mirror?.let { mirrorOf[boneId] = resolveBoneKey(it, contribution, "mirror", registry, diagnostics) }
	}
	val hierarchyOrder = registry.hierarchyFrom(registry.root).map { it.second }
	val copyRotationFallbacks = hierarchyOrder.mapNotNull { boneId ->
		copyFallbacksByBoneId[boneId]?.let { (contribution, fallback) -> boneId to resolveFallbackBone(fallback.source, contribution, registry, diagnostics) }
	}
	val firstActiveRotationFallbacks = hierarchyOrder.mapNotNull { boneId ->
		firstActiveFallbacksByBoneId[boneId]?.let { (contribution, fallback) ->
			boneId to fallback.sources.map { resolveFallbackBone(it, contribution, registry, diagnostics) }
		}
	}
	val vmcOutputMetadata = compileVmcOutputs(registry, vmcContributions, diagnostics)
	val childrenByVmcParent = vmcOutputMetadata.entries.groupBy({ it.value.inputParent }, { it.key })
	val vmcInputOrder = mutableListOf<BoneId>()
	fun visitVmcInputOrder(boneId: BoneId) {
		vmcInputOrder += boneId
		childrenByVmcParent[boneId]?.forEach(::visitVmcInputOrder)
	}
	registry[BodyPart.HIP.key]?.let(::visitVmcInputOrder)
	if (diagnostics.isNotEmpty()) throw ResourcePackCompilationException(diagnostics)

	return CompiledSkeleton(
		registry, proportions, tailOffsets, headOffsets, constraints, copyRotationFallbacks, firstActiveRotationFallbacks,
		driverOutputs, vmcOutputs, vrchatOutputs, vrchatRequired, overridableBones, candidateSources,
		vmcOutputMetadata, vmcInputOrder, mirrorOf,
	)
}

/**
 * Resolves each bone's VMC output. The real skeleton is rooted at `head`; VMC/Unity expects a
 * `hip`-rooted one instead, so `outputParent` and `inputParent` default to the real hierarchy
 * re-rooted at `hip` (walking parent/child edges in either direction, skipping any ancestor with
 * no VMC output of its own), unless the bone declares its own parent explicitly. An omitted
 * `inputParent` defaults to whatever `outputParent` resolved to (derived or overridden), so a
 * bone whose input and output share a parent only has to declare `outputParent` once.
 */
private fun compileVmcOutputs(
	registry: BoneRegistry,
	contributions: Map<BoneId, Pair<BoneContribution, VmcOutput>>,
	diagnostics: MutableList<ResourcePackCompilationDiagnostic>,
): BoneMap<CompiledVmcOutput> {
	val result = BoneMap.of<CompiledVmcOutput>(registry)
	val hip = registry[BodyPart.HIP.key] ?: return result
	val bfsParent = mutableMapOf<BoneId, BoneId?>(hip to null)
	val adjacency = mutableMapOf<BoneId, MutableList<BoneId>>()
	for (index in 1..registry.maxId) {
		val boneId = BoneId(index.toUShort())
		val parent = registry.parentOf(boneId) ?: continue
		adjacency.getOrPut(boneId) { mutableListOf() }.add(parent)
		adjacency.getOrPut(parent) { mutableListOf() }.add(boneId)
	}
	val queue = ArrayDeque(listOf(hip))
	while (queue.isNotEmpty()) {
		val current = queue.removeFirst()
		for (neighbor in adjacency[current].orEmpty()) {
			if (neighbor in bfsParent) continue
			bfsParent[neighbor] = current
			queue += neighbor
		}
	}
	fun namedAncestor(boneId: BoneId): BoneId? {
		var current = bfsParent[boneId]
		while (current != null && current !in contributions) current = bfsParent[current]
		return current
	}

	for ((boneId, pair) in contributions) {
		val (contribution, vmc) = pair
		val outputParent = vmc.outputParent?.let { resolveBoneKey(it, contribution, "outputs.vmc.outputParent", registry, diagnostics) } ?: namedAncestor(boneId)
		val inputParent = when (val parent = vmc.inputParent) {
			VmcInputParent.Omitted -> outputParent
			VmcInputParent.ExplicitNull -> null
			is VmcInputParent.Bone -> resolveBoneKey(parent.key, contribution, "outputs.vmc.inputParent", registry, diagnostics)
		}
		result[boneId] = CompiledVmcOutput(
			names = vmc.name.values,
			outputParent = outputParent,
			inputParent = inputParent,
			// Composed so the first entry is applied first: each later entry rotates on top of
			// the ones before it, in listed order.
			restRotation = (vmc.restRotation ?: emptyList()).fold(Quaternion.IDENTITY) { acc, step ->
				axisAngleQuaternion(Vector3(step.axis.x, step.axis.y, step.axis.z), step.degrees * degToRad) * acc
			},
		)
	}
	return result
}

private fun axisAngleQuaternion(axis: Vector3, radians: Float): Quaternion {
	val half = radians / 2f
	return Quaternion(cos(half), axis.unit() * sin(half))
}

private fun compileConstraint(constraint: PackConstraint): Constraint = when (constraint) {
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

/** Resolves a bone key referenced from [contribution], reporting [context] on an unknown key. */
private fun resolveBoneKey(
	key: String,
	contribution: BoneContribution,
	context: String,
	registry: BoneRegistry,
	diagnostics: MutableList<ResourcePackCompilationDiagnostic>,
): BoneId {
	val boneId = registry[key]
	if (boneId != null) return boneId
	diagnostics += ResourcePackCompilationDiagnostic(contribution.pack.manifest.value.id, contribution.resource.path, "Unknown bone '$key' in $context")
	return BoneId(0u)
}

/** Resolves one rotation-fallback source key (or the `"parent"` shorthand) to a [BoneId]. */
private fun resolveFallbackBone(
	key: String,
	contribution: BoneContribution,
	registry: BoneRegistry,
	diagnostics: MutableList<ResourcePackCompilationDiagnostic>,
): BoneId {
	val target = if (key == "parent") contribution.resource.value.parent else key
	if (target == null) {
		diagnostics += ResourcePackCompilationDiagnostic(contribution.pack.manifest.value.id, contribution.resource.path, "Unknown bone '$key' in rotationFallback")
		return BoneId(0u)
	}
	return resolveBoneKey(target, contribution, "rotationFallback", registry, diagnostics)
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
