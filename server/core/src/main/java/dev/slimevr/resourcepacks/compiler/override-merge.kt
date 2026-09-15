package dev.slimevr.resourcepacks

import com.github.erosb.jsonsKema.JsonParser
import com.github.erosb.jsonsKema.Validator
import kotlinx.serialization.json.JsonObject

/** JSON paths where an override's `set` merges key-by-key instead of replacing the subtree. */
private val BONE_MERGE_POINTS: Set<List<String>> = setOf(
	emptyList(),
	listOf("outputs"),
	listOf("outputs", "vrchat"),
	listOf("outputs", "vrchat", "emit"),
)

private val ROOT_ONLY_MERGE_POINT: Set<List<String>> = setOf(emptyList())

/** Bone properties that cannot be removed by an override. */
private val PROTECTED_BONE_PATHS: Set<List<String>> = setOf(listOf("key"), listOf("nameKey"))

private class MergeResult(val raw: JsonObject, val touched: List<List<String>>)

/** Merges [incoming] into [target], replacing existing values or recursing deeply for keys in [mergePoints]. */
private fun mergeAt(target: JsonObject, incoming: JsonObject, path: List<String>, mergePoints: Set<List<String>>): MergeResult {
	val merged = target.toMutableMap()
	val touched = mutableListOf<List<String>>()
	for ((key, value) in incoming) {
		val childPath = path + key
		val existingChild = merged[key]
		if (childPath in mergePoints && existingChild is JsonObject && value is JsonObject) {
			val child = mergeAt(existingChild, value, childPath, mergePoints)
			merged[key] = child.raw
			touched += child.touched
		} else {
			merged[key] = value
			touched += childPath
		}
	}
	return MergeResult(JsonObject(merged), touched)
}

/** Removes [path] from [target], or null if [path] does not name a present value. */
private fun removeAt(target: JsonObject, path: List<String>): JsonObject? {
	val key = path.first()
	val child = target[key] ?: return null
	if (path.size == 1) return JsonObject(target - key)
	val childObject = child as? JsonObject ?: return null
	val updatedChild = removeAt(childObject, path.drop(1)) ?: return null
	return JsonObject(target + (key to updatedChild))
}

/** Removes empty `outputs.vrchat`, `outputs`, and `inputs` objects to satisfy schema constraints. */
private fun pruneEmptyBoneContainers(definition: JsonObject): JsonObject {
	var outputs = definition["outputs"] as? JsonObject
	val vrchat = outputs?.get("vrchat") as? JsonObject
	val emit = vrchat?.get("emit") as? JsonObject
	if (outputs != null && emit != null && emit.isEmpty()) outputs = JsonObject(outputs - "vrchat")
	if (outputs != null && outputs.isEmpty()) outputs = null
	val inputs = (definition["inputs"] as? JsonObject)?.takeIf { it.isNotEmpty() }

	val result = definition.toMutableMap()
	if (outputs != null) result["outputs"] = outputs else result.remove("outputs")
	if (inputs != null) result["inputs"] = inputs else result.remove("inputs")
	return JsonObject(result)
}

internal fun applyOverrides(
	packs: List<ParsedResourcePack>,
	bonesByKey: MutableMap<String, BoneContribution>,
	proportionsByKey: MutableMap<String, ProportionContribution>,
	diagnostics: MutableList<ResourcePackCompilationDiagnostic>,
) {
	for (pack in packs) {
		for (resource in pack.get(ResourceTypes.BONE_OVERRIDE).sortedBy { it.path }) {
			val override = resource.value
			if (override.target !in bonesByKey) {
				diagnostics += ResourcePackCompilationDiagnostic(pack.manifest.value.id, resource.path, "Unknown bone '${override.target}' in override target")
				continue
			}
			val existing = bonesByKey.getValue(override.target)
			val origin = ResourceOrigin(pack, resource.path)
			val merged = override.set?.let { mergeAt(existing.resource.raw, it, emptyList(), BONE_MERGE_POINTS) }
			var raw = merged?.raw ?: existing.resource.raw
			var origins = existing.origins + merged?.touched.orEmpty().associateWith { origin }
			for (path in override.remove.orEmpty()) {
				if (path in PROTECTED_BONE_PATHS) {
					diagnostics += origin.diagnostic("Cannot remove bone property ${path.joinToString(".")}")
					continue
				}
				val removed = removeAt(raw, path)
				if (removed == null) {
					diagnostics += origin.diagnostic("Cannot remove bone property ${path.joinToString(".")}")
				} else {
					raw = removed
					origins = origins + (path to origin)
				}
			}
			bonesByKey[override.target] = existing.copy(
				resource = existing.resource.copy(raw = pruneEmptyBoneContainers(raw)),
				origins = origins,
				latestOverride = origin,
			)
		}
		for (resource in pack.get(ResourceTypes.PROPORTION_OVERRIDE).sortedBy { it.path }) {
			val override = resource.value
			if (override.target !in proportionsByKey) {
				diagnostics += ResourcePackCompilationDiagnostic(pack.manifest.value.id, resource.path, "Unknown proportion '${override.target}' in override target")
				continue
			}
			val existing = proportionsByKey.getValue(override.target)
			val origin = ResourceOrigin(pack, resource.path)
			var raw = override.set?.let { mergeAt(existing.resource.raw, it, emptyList(), ROOT_ONLY_MERGE_POINT).raw } ?: existing.resource.raw
			for (property in override.remove.orEmpty()) raw = removeAt(raw, listOf(property)) ?: raw
			proportionsByKey[override.target] = existing.copy(resource = existing.resource.copy(raw = raw), latestOverride = origin)
		}
	}
}

/**
 * Indexes each pack's [select]ed resources by [keyOf]
 */
internal fun <T> indexByKey(
	packs: List<ParsedResourcePack>,
	select: (ParsedResourcePack) -> List<SourcedResource<T>>,
	keyOf: (T) -> String,
	label: String,
	diagnostics: MutableList<ResourcePackCompilationDiagnostic>,
): LinkedHashMap<String, Contribution<T>> {
	val result = linkedMapOf<String, Contribution<T>>()
	for (pack in packs) {
		for (resource in select(pack)) {
			val key = keyOf(resource.value)
			val previous = result.put(key, Contribution(pack, resource))
			if (previous != null) {
				diagnostics += ResourcePackCompilationDiagnostic(
					pack.manifest.value.id,
					resource.path,
					"Duplicate $label '$key' (first defined by ${previous.pack.manifest.value.id}:${previous.resource.path})",
				)
			}
		}
	}
	return result
}

/**
 * Schema-validates and decodes each overridden contribution's merged raw JSON, replacing its typed
 * value. A resource no override touched keeps the value it decoded to at parse time untouched.
 */
internal fun <T : Any> revalidateMerged(
	byKey: MutableMap<String, Contribution<T>>,
	type: JsonResourceType<T>,
	label: String,
	diagnostics: MutableList<ResourcePackCompilationDiagnostic>,
) {
	for (key in byKey.keys.toList()) {
		val contribution = byKey.getValue(key)
		if (contribution.latestOverride == null) continue
		val raw = contribution.resource.raw
		val failure = Validator.forSchema(type.schema).validate(JsonParser(raw.toString()).parse())
		if (failure != null) {
			diagnostics += contribution.validationOrigin().diagnostic("Merged $label definition does not satisfy the $label schema: $failure")
			continue
		}
		val decoded = try {
			type.decode(raw)
		} catch (e: Exception) {
			diagnostics += contribution.validationOrigin().diagnostic("Merged $label definition does not decode: ${e.message}")
			continue
		}
		byKey[key] = contribution.copy(resource = contribution.resource.copy(value = decoded))
	}
}
