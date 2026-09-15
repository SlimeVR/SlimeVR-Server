package dev.slimevr.resourcepacks.compiler

import dev.slimevr.bones.BodyPart
import dev.slimevr.bones.BoneId
import dev.slimevr.bones.BoneMap
import dev.slimevr.bones.BoneRegistry
import dev.slimevr.bones.key
import dev.slimevr.resourcepacks.CopyRotationFallback
import dev.slimevr.resourcepacks.EmitEntry
import dev.slimevr.resourcepacks.FirstActiveRotationFallback
import dev.slimevr.resourcepacks.JsonResourceType
import dev.slimevr.resourcepacks.NoRotationFallback
import dev.slimevr.resourcepacks.ParsedResourcePack
import dev.slimevr.resourcepacks.ResourcePackCatalog
import dev.slimevr.resourcepacks.ResourceTypes
import dev.slimevr.resourcepacks.SourcedResource
import dev.slimevr.resourcepacks.VmcOutput
import dev.slimevr.resourcepacks.bones.CompiledBone
import dev.slimevr.resourcepacks.bones.CompiledProportion
import dev.slimevr.resourcepacks.bones.CompiledSkeleton
import dev.slimevr.resourcepacks.bones.CompiledVrchatOutput
import dev.slimevr.resourcepacks.bones.compileConstraint
import dev.slimevr.resourcepacks.bones.compileOffset
import dev.slimevr.resourcepacks.bones.displayName
import dev.slimevr.resourcepacks.bones.orderFallbackDependencies
import dev.slimevr.resourcepacks.bones.origin
import dev.slimevr.resourcepacks.bones.outputs.compileEmitEntries
import dev.slimevr.resourcepacks.bones.outputs.compileVmcInputOrder
import dev.slimevr.resourcepacks.bones.outputs.compileVmcOutputs
import dev.slimevr.resourcepacks.bones.resolveBoneKey
import dev.slimevr.resourcepacks.bones.resolveFallbackBone
import dev.slimevr.resourcepacks.bones.validateHierarchy
import dev.slimevr.resourcepacks.deepMerge
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.collections.iterator
import kotlin.collections.plusAssign
import kotlin.collections.set
import solarxr_protocol.connection.BoneDefinition as WireBoneDefinition
import solarxr_protocol.connection.BoneRegistry as WireBoneRegistry

/** Compiles the ordered resource-pack stack into a runtime [CompiledSkeleton]. */
fun compileResourcePacks(catalog: ResourcePackCatalog): CompiledSkeleton {
	val diagnostics = mutableListOf<ResourcePackCompilationDiagnostic>()
	val packs = listOf(catalog.core) + catalog.userPacks
	val packIds = mutableMapOf<String, ParsedResourcePack>()
	for (pack in packs) {
		val id = pack.manifest.value.id
		if (packIds.put(id, pack) != null) {
			diagnostics += ResourcePackCompilationDiagnostic(
				id,
				pack.manifest.path,
				"Duplicate pack ID '$id'",
			)
		}
	}
	if (catalog.core.manifest.value.id != "slimevr:core") {
		diagnostics += ResourcePackCompilationDiagnostic(
			catalog.core.manifest.value.id,
			catalog.core.manifest.path,
			"Expected bundled core pack ID 'slimevr:core'",
		)
	}

	val bonesByKey = compileResourceMap(packs, ResourceTypes.BONE, "bone key", diagnostics) { it.key }
	val proportionsByKey = compileResourceMap(packs, ResourceTypes.PROPORTION, "proportion key", diagnostics) { it.key }

	val standardParts = BodyPart.entries.filter { it != BodyPart.NONE }
	val standardPartsByKey = standardParts.associateBy(BodyPart::key)
	for (part in standardParts) {
		if (part.key !in bonesByKey) {
			diagnostics += ResourcePackCompilationDiagnostic(
				catalog.core.manifest.value.id,
				catalog.core.manifest.path,
				"Missing standard bone '${part.key}'",
			)
		}
	}
	for ((key, contribution) in bonesByKey) {
		val part = standardPartsByKey[key]
		if (part != null && contribution.pack !== catalog.core) {
			diagnostics += ResourcePackCompilationDiagnostic(
				contribution.pack.manifest.value.id,
				contribution.resource.path,
				"Standard bone '$key' belongs to the core pack",
			)
		}
		if (part == null && contribution.pack === catalog.core) {
			diagnostics += ResourcePackCompilationDiagnostic(
				catalog.core.manifest.value.id,
				contribution.resource.path,
				"Core pack defines non-standard bone '$key'",
			)
		}

		contribution.resource.value.parent?.let { parent ->
			if (parent !in bonesByKey) diagnostics += contribution.origin(BoneField.Parent).diagnostic("Unknown parent bone '$parent'")
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
					displayName = standardPart?.name?.replace('_', ' ')?.lowercase() ?: displayName(
						contribution,
					),
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

	val bones = mutableMapOf<BoneId, CompiledBone>()
	val copyFallbacksByBoneId = mutableMapOf<BoneId, Pair<BoneContribution, CopyRotationFallback>>()
	val firstActiveFallbacksByBoneId = mutableMapOf<BoneId, Pair<BoneContribution, FirstActiveRotationFallback>>()
	val vmcContributions = mutableMapOf<BoneId, Pair<BoneContribution, VmcOutput>>()
	val emitContributions = mutableMapOf<BoneId, Pair<BoneContribution, Map<String, EmitEntry>>>()
	val vrchatRequired = mutableSetOf<BoneId>()
	val vrchatInputAddresses = mutableMapOf<String, BoneId>()
	for (contribution in allContributions) {
		val definition = contribution.resource.value
		val boneId = registry[definition.key] ?: continue
		when (val fallback = definition.rotationFallback) {
			null, is NoRotationFallback -> {}
			is CopyRotationFallback -> copyFallbacksByBoneId[boneId] = contribution to fallback
			is FirstActiveRotationFallback -> firstActiveFallbacksByBoneId[boneId] = contribution to fallback
		}
		definition.outputs?.vmc?.let { vmcContributions[boneId] = contribution to it }
		definition.outputs?.vrchat?.let {
			if (it.required == true) vrchatRequired += boneId
			emitContributions[boneId] = contribution to it.emit
		}
		definition.inputs?.vrchat?.let { input ->
			reportDuplicateBoneKey(
				vrchatInputAddresses,
				input.address,
				boneId,
				"VRChat input address",
				registry,
				contribution.origin(BoneField.InputsVrchat),
				diagnostics,
			)
		}
		bones[boneId] = CompiledBone(
			tailOffset = definition.tailOffset?.let {
				compileOffset(
					it,
					contribution.origin(BoneField.TailOffset),
					proportionsByKey,
					diagnostics,
				)
			},
			headOffset = definition.headOffset?.let {
				compileOffset(
					it,
					contribution.origin(BoneField.HeadOffset),
					proportionsByKey,
					diagnostics,
				)
			},
			constraint = definition.constraint?.let { compileConstraint(it) },
			mirror = definition.mirror?.let {
				resolveBoneKey(
					it,
					contribution.origin(BoneField.Mirror),
					"mirror",
					registry,
					diagnostics,
				)
			},
			candidateSources = definition.candidateSources?.map {
				resolveBoneKey(
					it,
					contribution.origin(BoneField.CandidateSources),
					"candidateSources",
					registry,
					diagnostics,
				)
			},
			batterySources = definition.batterySources?.map {
				resolveBoneKey(
					it,
					contribution.origin(BoneField.BatterySources),
					"batterySources",
					registry,
					diagnostics,
				)
			},
			overridable = definition.overridable == true,
			driverOutput = definition.outputs?.driver != null,
			vrchatInput = definition.inputs?.vrchat?.address,
		)
	}

	val hierarchyOrder = registry.hierarchyFrom(registry.root).map { it.second }
	val resolvedCopyRotationFallbacks = hierarchyOrder.mapNotNull { boneId ->
		copyFallbacksByBoneId[boneId]?.let { (contribution, fallback) ->
			boneId to resolveFallbackBone(
				fallback.source,
				contribution,
				registry,
				diagnostics,
			)
		}
	}
	val resolvedFirstActiveRotationFallbacks = hierarchyOrder.mapNotNull { boneId ->
		firstActiveFallbacksByBoneId[boneId]?.let { (contribution, fallback) ->
			boneId to fallback.sources.map {
				resolveFallbackBone(
					it,
					contribution,
					registry,
					diagnostics,
				)
			}
		}
	}
	val firstActiveBones = resolvedFirstActiveRotationFallbacks.mapTo(mutableSetOf()) { it.first }
	for ((boneId, source) in resolvedCopyRotationFallbacks) {
		if (source !in firstActiveBones) continue
		val contribution = copyFallbacksByBoneId.getValue(boneId).first
		val sourceKey = registry.keyOf(source) ?: source.toString()
		diagnostics += contribution.origin(BoneField.RotationFallback).diagnostic(
			"A copy rotationFallback cannot use firstActive bone '$sourceKey' as its source because copy fallbacks run first",
		)
	}
	val copyRotationFallbacks =
		orderFallbackDependencies(resolvedCopyRotationFallbacks) { it }
	val firstActiveRotationFallbacks =
		orderFallbackDependencies(resolvedFirstActiveRotationFallbacks) { it.lastOrNull() }
	val vmcOutputMetadata = compileVmcOutputs(registry, vmcContributions, diagnostics)
	val emitEntries = compileEmitEntries(registry, emitContributions, diagnostics)
	val vmcInputOrder = compileVmcInputOrder(vmcOutputMetadata)
	if (diagnostics.isNotEmpty()) throw ResourcePackCompilationException(diagnostics)

	val boneMap = BoneMap.of<CompiledBone>(registry)
	for ((boneId, bone) in bones) {
		boneMap[boneId] = bone.copy(
			vmc = vmcOutputMetadata[boneId],
			vrchat = emitEntries[boneId]?.let { CompiledVrchatOutput(required = boneId in vrchatRequired, emit = it) },
		)
	}

	return CompiledSkeleton(registry, proportions, boneMap, copyRotationFallbacks, firstActiveRotationFallbacks, vmcInputOrder)
}

private fun <T : Any> compileResourceMap(
	packs: List<ParsedResourcePack>,
	type: JsonResourceType<T>,
	keyName: String,
	diagnostics: MutableList<ResourcePackCompilationDiagnostic>,
	keyExtractor: (T) -> String,
): Map<String, Contribution<T>> {
	class RawContribution(
		val pack: ParsedResourcePack,
		val path: String,
		val raw: kotlinx.serialization.json.JsonObject,
		val latestOverride: ResourceOrigin? = null,
	)
	val latestDefs = mutableMapOf<String, RawContribution>()

	for (pack in packs) {
		val objects = pack.objects[type] ?: emptyList()
		for ((path, jsonObject) in objects) {
			val origin = ResourceOrigin(pack, path)
			val existing = latestDefs[path]
			if (existing != null) {
				latestDefs[path] = RawContribution(existing.pack, path, jsonObject, origin)
			} else {
				latestDefs[path] = RawContribution(pack, path, jsonObject, origin)
			}
		}
	}


	fun resolveExtend(path: String, visited: Set<String>): JsonObject {
		val base = latestDefs[path]?.raw ?: return JsonObject(emptyMap())
		val extendPath = base["\$extend"]?.jsonPrimitive?.content ?: return base
		if (extendPath in visited) {
			diagnostics += ResourcePackCompilationDiagnostic(latestDefs[path]!!.pack.manifest.value.id, path, "Cyclic \$extend detected: $extendPath")
			return base
		}
		val parent = resolveExtend(extendPath, visited + path)
		return deepMerge(parent, base)
	}

	val byKey = mutableMapOf<String, Contribution<T>>()
	for ((path, rawCont) in latestDefs) {
		val resolvedRaw = resolveExtend(path, emptySet())
		val localDiags = mutableListOf<dev.slimevr.resourcepacks.ResourcePackDiagnostic>()
		val decoded = type.validateAndDecode(path, resolvedRaw, localDiags)
		for (diag in localDiags) {
			val msg = if (diag.pointer.isNotEmpty()) "${diag.pointer}: ${diag.message}" else diag.message
			diagnostics += ResourcePackCompilationDiagnostic(rawCont.pack.manifest.value.id, diag.path, msg)
		}
		if (decoded != null) {
			val fullContribution = Contribution(
				rawCont.pack,
				SourcedResource(path, decoded.value, resolvedRaw),
				emptyMap(),
				rawCont.latestOverride
			)
			val key = keyExtractor(decoded.value)
			if (key in byKey) {
				val existing = byKey.getValue(key)
				val diagnostic = existing.origin(null).diagnostic("Multiple definitions for $keyName '$key'")
				diagnostics += diagnostic
			} else {
				byKey[key] = fullContribution
			}
		}
	}
	return byKey
}
