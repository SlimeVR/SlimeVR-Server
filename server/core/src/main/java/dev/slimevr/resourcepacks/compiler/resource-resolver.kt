package dev.slimevr.resourcepacks.compiler

import dev.slimevr.resourcepacks.ParsedResourcePack
import dev.slimevr.resourcepacks.ResourcePackCatalog
import dev.slimevr.resourcepacks.ResourcePackDiagnostic
import dev.slimevr.resourcepacks.ResourceType
import dev.slimevr.resourcepacks.SourcedResource
import dev.slimevr.resourcepacks.orderResourcePacks
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

internal class ResolvedResourcePacks(
	val catalog: ResourcePackCatalog,
	private val resources: Map<ResourceType<*>, Map<String, Contribution<*>>>,
) {
	val entries = (listOf(catalog.core) + catalog.userPacks).flatMap { it.entries.entries }.associate { it.key to it.value }

	@Suppress("UNCHECKED_CAST")
	fun <T : Any> get(type: ResourceType<T>): Map<String, Contribution<T>> = resources[type].orEmpty() as Map<String, Contribution<T>>

	fun <T : Any> keyed(type: ResourceType<T>, keyOf: (T) -> String): Map<String, Contribution<T>> {
		val result = linkedMapOf<String, Contribution<T>>()
		val diagnostics = mutableListOf<ResourcePackCompilationDiagnostic>()
		for (contribution in get(type).values) {
			val key = keyOf(contribution.resource.value)
			if (result.putIfAbsent(key, contribution) != null) {
				diagnostics += contribution.validationOrigin().diagnostic("Duplicate ${type.id} key '$key'")
			}
		}
		if (diagnostics.isNotEmpty()) throw ResourcePackCompilationException(diagnostics)
		return result
	}
}

/** Applies path precedence, inheritance and validation once for every registered resource type. */
internal fun resolveResourcePacks(catalog: ResourcePackCatalog): ResolvedResourcePacks {
	val ordered = orderResourcePacks(catalog)
	val packs = listOf(ordered.core) + ordered.userPacks
	val diagnostics = mutableListOf<ResourcePackCompilationDiagnostic>()
	val resources = packs.flatMap { it.objects.keys }.distinct().associateWith { type ->
		resolveType(packs, type, diagnostics)
	}
	if (diagnostics.isNotEmpty()) throw ResourcePackCompilationException(diagnostics)
	return ResolvedResourcePacks(ordered, resources)
}

private data class Layer(val pack: ParsedResourcePack, val document: JsonObject)
private data class LayerId(val path: String, val index: Int)
private data class ResolvedDocument(val document: JsonObject, val origins: Map<List<String>, ResourceOrigin>)

private fun <T : Any> resolveType(
	packs: List<ParsedResourcePack>,
	type: ResourceType<T>,
	diagnostics: MutableList<ResourcePackCompilationDiagnostic>,
): Map<String, Contribution<T>> {
	val layers = linkedMapOf<String, MutableList<Layer>>()
	for (pack in packs) {
		for ((path, document) in pack.objects[type].orEmpty()) {
			layers.getOrPut(path) { mutableListOf() }.add(Layer(pack, document))
		}
	}
	val cache = mutableMapOf<LayerId, ResolvedDocument?>()
	val visiting = linkedSetOf<LayerId>()
	fun resolve(id: LayerId): ResolvedDocument? {
		if (cache.containsKey(id)) return cache[id]
		val layer = layers.getValue(id.path)[id.index]
		val origin = ResourceOrigin(layer.pack, id.path)
		if (!visiting.add(id)) {
			diagnostics += origin.diagnostic("Cyclic \$extend detected: ${visiting.joinToString(" -> ") { it.path }} -> ${id.path}")
			return null
		}
		fun resolveDocument(): ResolvedDocument? {
			val extend = layer.document["\$extend"]
			val parent = if (extend != null) {
				val path = (extend as? JsonPrimitive)?.takeIf { it.isString }?.content
				if (path == null) {
					diagnostics += origin.diagnostic("\$extend must be a resource path string")
					return null
				}
				val parentIndex = if (path == id.path) id.index - 1 else layers[path]?.lastIndex ?: -1
				if (parentIndex < 0) {
					diagnostics += origin.diagnostic("Unknown \$extend target '$path' (no inherited layer)")
					return null
				}
				resolve(LayerId(path, parentIndex)) ?: return null
			} else if (type.mergeLayers && id.index > 0) {
				resolve(LayerId(id.path, id.index - 1)) ?: return null
			} else {
				ResolvedDocument(JsonObject(emptyMap()), emptyMap())
			}
			val origins = parent.origins.toMutableMap()
			fun merge(target: JsonObject, source: JsonObject, prefix: List<String>): JsonObject {
				val result = target.toMutableMap()
				for ((key, value) in source) {
					if (key == "\$extend") continue
					val path = prefix + key
					val existing = result[key]
					if (value is JsonObject) {
						if (existing !is JsonObject) origins.keys.removeAll { it.take(path.size) == path }
						result[key] = merge(existing as? JsonObject ?: JsonObject(emptyMap()), value, path)
						origins[path] = origin
					} else {
						origins.keys.removeAll { it.take(path.size) == path }
						origins[path] = origin
						result[key] = value
					}
				}
				return JsonObject(result)
			}
			val document = merge(parent.document, layer.document, emptyList())
			origins[emptyList()] = origin
			return ResolvedDocument(document, origins)
		}
		val resolved = resolveDocument()
		visiting.remove(id)
		cache[id] = resolved
		return resolved
	}
	val result = linkedMapOf<String, Contribution<T>>()
	for ((path, stack) in layers) {
		val resolved = resolve(LayerId(path, stack.lastIndex)) ?: continue
		val origin = ResourceOrigin(stack.last().pack, path)
		val localDiagnostics = mutableListOf<ResourcePackDiagnostic>()
		val decoded = type.validateAndDecode(path, resolved.document, localDiagnostics)
		diagnostics += localDiagnostics.map { origin.diagnostic(it.message) }
		if (decoded != null) {
			result[path] = Contribution(stack.first().pack, SourcedResource(path, decoded.value, resolved.document), resolved.origins, origin)
		}
	}
	return result
}
