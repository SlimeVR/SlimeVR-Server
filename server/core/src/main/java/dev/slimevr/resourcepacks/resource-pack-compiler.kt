package dev.slimevr.resourcepacks

import com.github.erosb.jsonsKema.JsonParser
import com.github.erosb.jsonsKema.Validator
import dev.slimevr.bones.BodyPart
import dev.slimevr.bones.BoneId
import dev.slimevr.bones.BoneMap
import dev.slimevr.bones.BoneRegistry
import dev.slimevr.bones.BoneSet
import dev.slimevr.bones.Constraint
import dev.slimevr.bones.HingeConstraint
import dev.slimevr.bones.LooseHingeConstraint
import dev.slimevr.bones.TwistSwingConstraint
import dev.slimevr.bones.key
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlinx.serialization.json.Json
import kotlin.math.cos
import kotlin.math.sin
import com.jme3.math.FastMath.DEG_TO_RAD as degToRad
import dev.slimevr.resourcepacks.Constraint as PackConstraint
import dev.slimevr.resourcepacks.HingeConstraint as PackHingeConstraint
import dev.slimevr.resourcepacks.LooseHingeConstraint as PackLooseHingeConstraint
import dev.slimevr.resourcepacks.TwistSwingConstraint as PackTwistSwingConstraint
import solarxr_protocol.connection.BoneDefinition as WireBoneDefinition
import solarxr_protocol.connection.BoneRegistry as WireBoneRegistry

data class ResourcePackCompilationDiagnostic(val packId: String, val path: String, val message: String)

class ResourcePackCompilationException(val diagnostics: List<ResourcePackCompilationDiagnostic>) :
	IllegalArgumentException(
		diagnostics.joinToString("\n") { "${it.packId}:${it.path}: ${it.message}" },
	)

private data class ResourceOrigin(val pack: ParsedResourcePack, val path: String)

private data class BoneFieldOrigins(
	val mirror: ResourceOrigin? = null,
	val batterySources: ResourceOrigin? = null,
	val candidateSources: ResourceOrigin? = null,
	val parent: ResourceOrigin? = null,
	val headOffset: ResourceOrigin? = null,
	val tailOffset: ResourceOrigin? = null,
	val rotationFallback: ResourceOrigin? = null,
	val vmcOutput: ResourceOrigin? = null,
	val vrchatEmit: Map<String, ResourceOrigin> = emptyMap(),
)

private data class BoneContribution(
	val pack: ParsedResourcePack,
	val resource: SourcedResource<dev.slimevr.resourcepacks.BoneDefinition>,
	val fieldOrigins: BoneFieldOrigins = BoneFieldOrigins(),
	val latestOverride: ResourceOrigin? = null,
) {
	private val definitionOrigin get() = ResourceOrigin(pack, resource.path)
	fun origin(fieldOrigin: ResourceOrigin?): ResourceOrigin = fieldOrigin ?: definitionOrigin
	fun validationOrigin(): ResourceOrigin = latestOverride ?: definitionOrigin
}

private data class ProportionContribution(
	val pack: ParsedResourcePack,
	val resource: SourcedResource<ProportionDefinition>,
	val latestOverride: ResourceOrigin? = null,
) {
	fun validationOrigin(): ResourceOrigin = latestOverride ?: ResourceOrigin(pack, resource.path)
}

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
	applyOverrides(packs, bonesByKey, proportionsByKey, diagnostics)
	validateMergedDefinitions(bonesByKey, proportionsByKey, diagnostics)

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
			if (parent !in bonesByKey) diagnostics += contribution.origin(contribution.fieldOrigins.parent).diagnostic("Unknown parent bone '$parent'")
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
				WireBoneDefinition(
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
	val batterySources = BoneMap.of<List<BoneId>>(registry)
	val emitContributions = mutableMapOf<BoneId, Pair<BoneContribution, Map<String, EmitEntry>>>()
	val vrchatInputAddresses = mutableMapOf<String, BoneId>()
	val mirrorOf = BoneMap.of<BoneId>(registry)
	val vmcContributions = mutableMapOf<BoneId, Pair<BoneContribution, VmcOutput>>()
	for (contribution in allContributions) {
		val definition = contribution.resource.value
		val boneId = registry[definition.key] ?: continue
		definition.tailOffset?.let { tailOffsets[boneId] = compileOffset(it, contribution.origin(contribution.fieldOrigins.tailOffset), proportionsByKey, diagnostics) }
		definition.headOffset?.let { headOffsets[boneId] = compileOffset(it, contribution.origin(contribution.fieldOrigins.headOffset), proportionsByKey, diagnostics) }
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
			emitContributions[boneId] = contribution to it.emit
		}
		definition.inputs?.vrchat?.let { vrchatInputAddresses[it.address] = boneId }
		if (definition.overridable == true) overridableBones.add(boneId)
		definition.candidateSources?.let { sources ->
			candidateSources[boneId] = sources.map { resolveBoneKey(it, contribution.origin(contribution.fieldOrigins.candidateSources), "candidateSources", registry, diagnostics) }
		}
		definition.batterySources?.let { sources ->
			batterySources[boneId] = sources.map { resolveBoneKey(it, contribution.origin(contribution.fieldOrigins.batterySources), "batterySources", registry, diagnostics) }
		}
		definition.mirror?.let { mirrorOf[boneId] = resolveBoneKey(it, contribution.origin(contribution.fieldOrigins.mirror), "mirror", registry, diagnostics) }
	}
	val hierarchyOrder = registry.hierarchyFrom(registry.root).map { it.second }
	val resolvedCopyRotationFallbacks = hierarchyOrder.mapNotNull { boneId ->
		copyFallbacksByBoneId[boneId]?.let { (contribution, fallback) -> boneId to resolveFallbackBone(fallback.source, contribution, registry, diagnostics) }
	}
	val resolvedFirstActiveRotationFallbacks = hierarchyOrder.mapNotNull { boneId ->
		firstActiveFallbacksByBoneId[boneId]?.let { (contribution, fallback) ->
			boneId to fallback.sources.map { resolveFallbackBone(it, contribution, registry, diagnostics) }
		}
	}
	val firstActiveBones = resolvedFirstActiveRotationFallbacks.mapTo(mutableSetOf()) { it.first }
	for ((boneId, source) in resolvedCopyRotationFallbacks) {
		if (source !in firstActiveBones) continue
		val contribution = copyFallbacksByBoneId.getValue(boneId).first
		val sourceKey = registry.keyOf(source) ?: source.toString()
		diagnostics += contribution.origin(contribution.fieldOrigins.rotationFallback).diagnostic(
			"A copy rotationFallback cannot use firstActive bone '$sourceKey' as its source because copy fallbacks run first",
		)
	}
	val copyRotationFallbacks = orderFallbackDependencies(resolvedCopyRotationFallbacks) { it }
	val firstActiveRotationFallbacks = orderFallbackDependencies(resolvedFirstActiveRotationFallbacks) { it.lastOrNull() }
	val vmcOutputMetadata = compileVmcOutputs(registry, vmcContributions, diagnostics)
	val emitEntries = compileEmitEntries(registry, emitContributions, diagnostics)
	val vmcInputOrder = compileVmcInputOrder(vmcOutputMetadata)
	if (diagnostics.isNotEmpty()) throw ResourcePackCompilationException(diagnostics)

	return CompiledSkeleton(
		registry, proportions, tailOffsets, headOffsets, constraints, copyRotationFallbacks, firstActiveRotationFallbacks,
		driverOutputs, vmcOutputs, vrchatOutputs, vrchatRequired, overridableBones, candidateSources,
		batterySources,
		emitEntries, vrchatInputAddresses,
		vmcOutputMetadata, vmcInputOrder, mirrorOf,
	)
}

/**
 * Orders an acyclic fallback chain source-before-consumer. A cycle keeps its existing hierarchy
 * order, preserving the retired head/neck fallback's deterministic winner.
 */
private fun <T> orderFallbackDependencies(
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

private fun applyOverrides(
	packs: List<ParsedResourcePack>,
	bonesByKey: MutableMap<String, BoneContribution>,
	proportionsByKey: MutableMap<String, ProportionContribution>,
	diagnostics: MutableList<ResourcePackCompilationDiagnostic>,
) {
	for (pack in packs) {
		for (resource in pack.boneOverrides.sortedBy { it.path }) {
			val override = resource.value
			if (override.target !in bonesByKey) {
				diagnostics += ResourcePackCompilationDiagnostic(pack.manifest.value.id, resource.path, "Unknown bone '${override.target}' in override target")
				continue
			}
			val existing = bonesByKey.getValue(override.target)
			val origin = ResourceOrigin(pack, resource.path)
			var updated = existing
			override.set?.let { updated = updated.withSet(it, origin) }
			var definition = updated.resource.value
			var fieldOrigins = updated.fieldOrigins
			for (path in override.remove.orEmpty()) {
				val removed = definition.withRemoved(path)
				if (removed == null) {
					diagnostics += origin.diagnostic("Cannot remove bone property ${path.joinToString(".")}")
				} else {
					definition = removed
					if (path == listOf("parent")) fieldOrigins = fieldOrigins.copy(parent = origin)
				}
			}
			bonesByKey[override.target] = updated.copy(
				resource = updated.resource.copy(value = definition.normalized()),
				fieldOrigins = fieldOrigins,
				latestOverride = origin,
			)
		}
		for (resource in pack.proportionOverrides.sortedBy { it.path }) {
			val override = resource.value
			if (override.target !in proportionsByKey) {
				diagnostics += ResourcePackCompilationDiagnostic(pack.manifest.value.id, resource.path, "Unknown proportion '${override.target}' in override target")
				continue
			}
			val existing = proportionsByKey.getValue(override.target)
			val origin = ResourceOrigin(pack, resource.path)
			var definition = override.set?.let { existing.resource.value.withSet(it) } ?: existing.resource.value
			for (property in override.remove.orEmpty()) {
				val removed = definition.withRemoved(property)
				if (removed == null) {
					diagnostics += origin.diagnostic("Cannot remove proportion property '$property'")
				} else {
					definition = removed
				}
			}
			proportionsByKey[override.target] = existing.copy(resource = existing.resource.copy(value = definition), latestOverride = origin)
		}
	}
}

private fun BoneContribution.withSet(set: BoneOverrideSet, origin: ResourceOrigin): BoneContribution {
	val origins = fieldOrigins.copy(
		mirror = if (set.mirror != null) origin else fieldOrigins.mirror,
		batterySources = if (set.batterySources != null) origin else fieldOrigins.batterySources,
		candidateSources = if (set.candidateSources != null) origin else fieldOrigins.candidateSources,
		parent = if (set.parent != null) origin else fieldOrigins.parent,
		headOffset = if (set.headOffset != null) origin else fieldOrigins.headOffset,
		tailOffset = if (set.tailOffset != null) origin else fieldOrigins.tailOffset,
		rotationFallback = if (set.rotationFallback != null) origin else fieldOrigins.rotationFallback,
		vmcOutput = if (set.outputs?.vmc != null) origin else fieldOrigins.vmcOutput,
		vrchatEmit = fieldOrigins.vrchatEmit + set.outputs?.vrchat?.emit.orEmpty().keys.associateWith { origin },
	)
	return copy(
		resource = resource.copy(value = resource.value.withSet(set)),
		fieldOrigins = origins,
		latestOverride = origin,
	)
}

private fun BoneDefinition.normalized(): BoneDefinition {
	val normalizedOutputs = outputs?.let { output ->
		val vrchat = output.vrchat?.takeIf { it.emit.isNotEmpty() }
		output.copy(vrchat = vrchat).takeIf { it.driver != null || it.vmc != null || it.vrchat != null }
	}
	val normalizedInputs = inputs?.takeIf { it.vrchat != null }
	return copy(outputs = normalizedOutputs, inputs = normalizedInputs)
}

private val mergedDefinitionJson = Json { encodeDefaults = false }

private fun validateMergedDefinitions(
	bonesByKey: Map<String, BoneContribution>,
	proportionsByKey: Map<String, ProportionContribution>,
	diagnostics: MutableList<ResourcePackCompilationDiagnostic>,
) {
	for (contribution in bonesByKey.values) {
		val failure = Validator.forSchema(PackSchemas.schema(ResourceKind.BONE)).validate(
			JsonParser(mergedDefinitionJson.encodeToString(contribution.resource.value)).parse(),
		)
		if (failure != null) diagnostics += contribution.validationOrigin().diagnostic("Merged bone definition does not satisfy the bone schema: $failure")
	}
	for (contribution in proportionsByKey.values) {
		val failure = Validator.forSchema(PackSchemas.schema(ResourceKind.PROPORTION)).validate(
			JsonParser(mergedDefinitionJson.encodeToString(contribution.resource.value)).parse(),
		)
		if (failure != null) diagnostics += contribution.validationOrigin().diagnostic("Merged proportion definition does not satisfy the proportion schema: $failure")
	}
}

private fun ResourceOrigin.diagnostic(message: String) = ResourcePackCompilationDiagnostic(pack.manifest.value.id, path, message)

internal fun BoneDefinition.withSet(set: BoneOverrideSet): BoneDefinition = copy(
	nameKey = set.nameKey ?: nameKey,
	mirror = set.mirror ?: mirror,
	batterySources = set.batterySources ?: batterySources,
	candidateSources = set.candidateSources ?: candidateSources,
	overridable = set.overridable ?: overridable,
	parent = set.parent ?: parent,
	headOffset = set.headOffset ?: headOffset,
	tailOffset = set.tailOffset ?: tailOffset,
	rotationFallback = set.rotationFallback ?: rotationFallback,
	constraint = set.constraint ?: constraint,
	outputs = outputs.withSet(set.outputs),
	inputs = set.inputs ?: inputs,
)

private fun BoneOutputs?.withSet(set: BoneOutputs?): BoneOutputs? {
	if (set == null) return this
	val current = this ?: BoneOutputs()
	return current.copy(
		driver = set.driver ?: current.driver,
		vmc = set.vmc ?: current.vmc,
		vrchat = set.vrchat?.let { incoming ->
			current.vrchat?.copy(required = incoming.required ?: current.vrchat.required, emit = current.vrchat.emit + incoming.emit) ?: incoming
		} ?: current.vrchat,
	)
}

internal fun ProportionDefinition.withSet(set: ProportionOverrideSet): ProportionDefinition = copy(
	nameKey = set.nameKey ?: nameKey,
	descriptionKey = set.descriptionKey ?: descriptionKey,
	contributesToHeight = set.contributesToHeight ?: contributesToHeight,
	minimum = set.minimum ?: minimum,
	maximum = set.maximum ?: maximum,
	default = set.default ?: default,
)

internal fun BoneDefinition.withRemoved(path: List<String>): BoneDefinition? = when (path) {
	listOf("mirror") -> copy(mirror = null)

	listOf("batterySources") -> copy(batterySources = null)

	listOf("candidateSources") -> copy(candidateSources = null)

	listOf("overridable") -> copy(overridable = null)

	listOf("parent") -> copy(parent = null)

	listOf("headOffset") -> copy(headOffset = null)

	listOf("tailOffset") -> copy(tailOffset = null)

	listOf("rotationFallback") -> copy(rotationFallback = null)

	listOf("constraint") -> copy(constraint = null)

	listOf("outputs") -> copy(outputs = null)

	listOf("inputs") -> copy(inputs = null)

	listOf("inputs", "vrchat") -> copy(inputs = inputs?.copy(vrchat = null))

	listOf("outputs", "driver") -> copy(outputs = outputs?.copy(driver = null))

	listOf("outputs", "vmc") -> copy(outputs = outputs?.copy(vmc = null))

	listOf("outputs", "vrchat") -> copy(outputs = outputs?.copy(vrchat = null))

	listOf("outputs", "vrchat", "required") -> copy(outputs = outputs?.copy(vrchat = outputs.vrchat?.copy(required = null)))

	else -> {
		if (path.size == 4 && path.take(3) == listOf("outputs", "vrchat", "emit")) {
			copy(outputs = outputs?.copy(vrchat = outputs.vrchat?.copy(emit = outputs.vrchat.emit - path[3])))
		} else {
			null
		}
	}
}

internal fun ProportionDefinition.withRemoved(property: String): ProportionDefinition? = when (property) {
	"descriptionKey" -> copy(descriptionKey = null)
	"contributesToHeight" -> copy(contributesToHeight = null)
	"minimum" -> copy(minimum = null)
	"maximum" -> copy(maximum = null)
	else -> null
}

private fun compileEmitEntries(
	registry: BoneRegistry,
	contributions: Map<BoneId, Pair<BoneContribution, Map<String, EmitEntry>>>,
	diagnostics: MutableList<ResourcePackCompilationDiagnostic>,
): BoneMap<Map<String, CompiledEmitEntry>> {
	val result = BoneMap.of<Map<String, CompiledEmitEntry>>(registry)
	for ((boneId, pair) in contributions) {
		val (contribution, entries) = pair
		result[boneId] = entries.mapValues { (address, entry) ->
			val origin = contribution.origin(contribution.fieldOrigins.vrchatEmit[address])
			validateEmitPipeline(address, entry, origin, diagnostics)
			CompiledEmitEntry(
				from = entry.from,
				relativeTo = entry.relativeTo?.let {
					resolveBoneKey(it, origin, "outputs.vrchat.emit.relativeTo", registry, diagnostics)
				},
				steps = entry.value ?: emptyList(),
			)
		}
	}
	return result
}

private enum class EmitValueType { ROTATION, VECTOR, NUMBER, BOOLEAN }

private fun validateEmitPipeline(
	address: String,
	entry: EmitEntry,
	origin: ResourceOrigin,
	diagnostics: MutableList<ResourcePackCompilationDiagnostic>,
) {
	var type = when (entry.from) {
		EmitSource.POSITION -> EmitValueType.VECTOR
		EmitSource.ROTATION -> EmitValueType.ROTATION
	}
	for ((index, step) in entry.value.orEmpty().withIndex()) {
		val (operation, next) = when {
			step.euler != null -> {
				"euler" to if (type != EmitValueType.ROTATION) null else if (step.euler.axis == null) EmitValueType.VECTOR else EmitValueType.NUMBER
			}

			step.scale != null -> "scale" to arithmeticResult(type, step.scale)
			step.divide != null -> "divide" to arithmeticResult(type, step.divide)
			step.offset != null -> "offset" to arithmeticResult(type, step.offset)
			step.clamp != null -> "clamp" to type.takeIf { it == EmitValueType.NUMBER || it == EmitValueType.VECTOR }
			step.greaterThan != null -> "greaterThan" to EmitValueType.BOOLEAN.takeIf { type == EmitValueType.NUMBER }
			step.lessThan != null -> "lessThan" to EmitValueType.BOOLEAN.takeIf { type == EmitValueType.NUMBER }
			else -> error("Pipeline step has no operation after schema validation")
		}
		if (next == null) {
			diagnostics += origin.diagnostic("VRChat emit '$address' cannot apply $operation to ${type.name.lowercase()} at pipeline step ${index + 1}")
			return
		}
		type = next
	}
}

private fun arithmeticResult(type: EmitValueType, operand: ScalarOrVector): EmitValueType? = when (type) {
	EmitValueType.ROTATION, EmitValueType.VECTOR -> type
	EmitValueType.NUMBER -> if (operand is ScalarOrVector.Scalar) EmitValueType.NUMBER else null
	EmitValueType.BOOLEAN -> null
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
		val origin = contribution.origin(contribution.fieldOrigins.vmcOutput)
		val outputParent = vmc.outputParent?.let { resolveBoneKey(it, origin, "outputs.vmc.outputParent", registry, diagnostics) } ?: namedAncestor(boneId)
		val inputParent = when (val parent = vmc.inputParent) {
			VmcInputParent.Omitted -> outputParent
			VmcInputParent.ExplicitNull -> null
			is VmcInputParent.Bone -> resolveBoneKey(parent.key, origin, "outputs.vmc.inputParent", registry, diagnostics)
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
	validateVmcOutputs(registry, result, contributions, diagnostics)
	return result
}

private fun validateVmcOutputs(
	registry: BoneRegistry,
	outputs: BoneMap<CompiledVmcOutput>,
	contributions: Map<BoneId, Pair<BoneContribution, VmcOutput>>,
	diagnostics: MutableList<ResourcePackCompilationDiagnostic>,
) {
	val names = mutableMapOf<String, BoneId>()
	for ((boneId, output) in outputs) {
		val contribution = contributions.getValue(boneId).first
		val origin = contribution.origin(contribution.fieldOrigins.vmcOutput)
		for (name in output.names) {
			val previous = names.putIfAbsent(name, boneId) ?: continue
			val previousKey = registry.keyOf(previous) ?: previous.toString()
			diagnostics += origin.diagnostic(
				"Duplicate VMC name '$name'; first used by '$previousKey'",
			)
		}
		for ((field, parent) in listOf("outputParent" to output.outputParent, "inputParent" to output.inputParent)) {
			if (parent != null && parent !in outputs) {
				val parentKey = registry.keyOf(parent) ?: parent.toString()
				diagnostics += origin.diagnostic("VMC $field '$parentKey' does not have a VMC output")
			}
		}
	}

	fun validateParentGraph(field: String, parentOf: (CompiledVmcOutput) -> BoneId?) {
		val reportedCycles = mutableSetOf<Set<BoneId>>()
		for ((boneId, _) in outputs) {
			val visited = linkedSetOf<BoneId>()
			var current: BoneId? = boneId
			while (current != null && visited.add(current)) current = outputs[current]?.let(parentOf)
			if (current == null) continue
			val cycle = visited.dropWhile { it != current }.toSet()
			if (!reportedCycles.add(cycle)) continue
			val contribution = contributions.getValue(visited.last()).first
			val origin = contribution.origin(contribution.fieldOrigins.vmcOutput)
			val keys = cycle.map { registry.keyOf(it) ?: it.toString() }
			diagnostics += origin.diagnostic("VMC $field graph contains a cycle: ${keys.joinToString(" -> ")}")
		}
	}
	validateParentGraph("outputParent", CompiledVmcOutput::outputParent)
	validateParentGraph("inputParent", CompiledVmcOutput::inputParent)
}

private fun compileVmcInputOrder(outputs: BoneMap<CompiledVmcOutput>): List<BoneId> {
	val childrenByParent = outputs.entries.groupBy({ it.value.inputParent }, { it.key })
	val result = mutableListOf<BoneId>()
	val visited = mutableSetOf<BoneId>()
	fun visit(boneId: BoneId) {
		if (!visited.add(boneId)) return
		result += boneId
		childrenByParent[boneId]?.forEach(::visit)
	}
	childrenByParent[null].orEmpty().forEach(::visit)
	// Compilation diagnostics reject cycles and unnamed parents. Still visit any remaining entry
	// defensively so malformed metadata cannot recurse forever or silently disappear before throw.
	outputs.keys.forEach(::visit)
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

/** Resolves a bone key referenced from [origin], reporting [context] on an unknown key. */
private fun resolveBoneKey(
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
private fun resolveFallbackBone(
	key: String,
	contribution: BoneContribution,
	registry: BoneRegistry,
	diagnostics: MutableList<ResourcePackCompilationDiagnostic>,
): BoneId {
	val target = if (key == "parent") contribution.resource.value.parent else key
	val origin = contribution.origin(contribution.fieldOrigins.rotationFallback)
	if (target == null) {
		diagnostics += origin.diagnostic("Unknown bone '$key' in rotationFallback")
		return BoneId(0u)
	}
	return resolveBoneKey(target, origin, "rotationFallback", registry, diagnostics)
}

private fun compileOffset(
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
		val overrideOrigin = bonesByKey.values.mapNotNull { it.fieldOrigins.parent }.lastOrNull()
		diagnostics += overrideOrigin?.diagnostic("A resource-pack registry must have exactly one root; found none")
			?: ResourcePackCompilationDiagnostic(core.manifest.value.id, core.manifest.path, "A resource-pack registry must have exactly one root; found none")
	} else if (roots.size > 1) {
		for (contribution in roots) {
			diagnostics += contribution.origin(contribution.fieldOrigins.parent).diagnostic("A resource-pack registry must have exactly one root; found ${roots.size}")
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
				val overrideOrigin = cycle.mapNotNull { bonesByKey.getValue(it).fieldOrigins.parent }.lastOrNull()
				diagnostics += contribution.origin(overrideOrigin).diagnostic("Bone hierarchy contains a cycle: ${cycle.joinToString(" -> ")}")
			}
		}
	}
}
