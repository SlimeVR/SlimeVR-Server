package dev.slimevr.resourcepacks

import kotlinx.serialization.json.Json

internal val ResourcePackJson = Json {
	ignoreUnknownKeys = false
	explicitNulls = true
}

object ResourcePackParser {
	suspend fun parse(source: ResourcePackSource, types: List<ResourceType<*>> = ResourceTypes.ALL): ParsedResourcePack {
		val entries = try {
			source.entries()
		} catch (e: Exception) {
			throw ResourcePackParseException(listOf(ResourcePackDiagnostic("", message = "Unable to scan ${source.description}: ${e.message}")))
		}
		val diagnostics = mutableListOf<ResourcePackDiagnostic>()
		entries.groupBy { it.path }.filterValues { it.size > 1 }.keys.sorted().forEach { diagnostics += ResourcePackDiagnostic(it, message = "Duplicate resource path") }
		val manifestEntry = entries.firstOrNull { it.path == "manifest.json" }
		if (manifestEntry == null) diagnostics += ResourcePackDiagnostic("manifest.json", message = "Missing manifest")

		val parsedResources = mutableMapOf<ResourceType<*>, MutableList<SourcedResource<*>>>()
		var manifest: SourcedResource<PackManifest>? = null

		for (entry in entries) {
			val type = types.firstOrNull { it.matches(entry.path) }
			if (type != null) {
				val parsed = type.parse(entry, diagnostics)
				if (parsed != null) {
					if (type == ResourceTypes.MANIFEST) {
						@Suppress("UNCHECKED_CAST")
						manifest = parsed as SourcedResource<PackManifest>
					} else {
						parsedResources.getOrPut(type) { mutableListOf() }.add(parsed)
					}
				}
			} else if (ResourceTypes.isMisplaced(entry.path)) {
				diagnostics += ResourcePackDiagnostic(entry.path, message = "JSON document is not allowed at this pack path")
			}
		}
		if (diagnostics.isNotEmpty()) throw ResourcePackParseException(diagnostics.sortedWith(compareBy<ResourcePackDiagnostic> { it.path }.thenBy { it.pointer }))
		return ParsedResourcePack(source.description, requireNotNull(manifest), parsedResources)
	}
}
