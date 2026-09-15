package dev.slimevr.resourcepacks

import kotlinx.serialization.json.Json

internal val ResourcePackJson = Json {
	ignoreUnknownKeys = false
	explicitNulls = true
}

object ResourcePackParser {
	const val MIN_SUPPORTED_FORMAT_VERSION = 1
	const val CURRENT_FORMAT_VERSION = 1

	suspend fun parse(source: ResourcePackSource, types: List<ResourceType<*>> = ResourceTypes.ALL): ParsedResourcePack {
		val entries = try {
			source.entries()
		} catch (e: Exception) {
			throw ResourcePackParseException(listOf(ResourcePackDiagnostic("", message = "Unable to scan ${source.description}: ${e.message}")))
		}
		val diagnostics = mutableListOf<ResourcePackDiagnostic>()
		entries.groupBy { it.path }.filterValues { it.size > 1 }.keys.sorted().forEach { diagnostics += ResourcePackDiagnostic(it, message = "Duplicate resource path") }
		
		val manifestEntry = entries.firstOrNull { it.path == "manifest.json" }
		if (manifestEntry == null) {
			diagnostics += ResourcePackDiagnostic("manifest.json", message = "Missing manifest")
			throw ResourcePackParseException(diagnostics)
		}

		// Always parse manifest first assuming the current version so we can read its formatVersion
		val parsedManifest = ResourceTypes.MANIFEST.parse(manifestEntry, CURRENT_FORMAT_VERSION, diagnostics) as? SourcedResource<PackManifest>
		if (parsedManifest == null) {
			throw ResourcePackParseException(diagnostics)
		}

		val formatVersion = parsedManifest.value.formatVersion
		if (formatVersion < MIN_SUPPORTED_FORMAT_VERSION) {
			diagnostics += ResourcePackDiagnostic("manifest.json", message = "Unsupported format version $formatVersion. Minimum supported is $MIN_SUPPORTED_FORMAT_VERSION")
			throw ResourcePackParseException(diagnostics)
		}

		val parsedResources = mutableMapOf<ResourceType<*>, MutableList<SourcedResource<*>>>()

		for (entry in entries) {
			if (entry.path == "manifest.json") continue
			val type = types.firstOrNull { it.matches(entry.path) }
			if (type != null) {
				val parsed = type.parse(entry, formatVersion, diagnostics)
				if (parsed != null) {
					parsedResources.getOrPut(type) { mutableListOf() }.add(parsed)
				}
			} else if (ResourceTypes.isMisplaced(entry.path)) {
				diagnostics += ResourcePackDiagnostic(entry.path, message = "JSON document is not allowed at this pack path")
			}
		}
		if (diagnostics.isNotEmpty()) throw ResourcePackParseException(diagnostics.sortedWith(compareBy<ResourcePackDiagnostic> { it.path }.thenBy { it.pointer }))
		return ParsedResourcePack(source.description, parsedManifest, parsedResources)
	}
}
