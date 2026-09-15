package dev.slimevr.resourcepacks

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonObject

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

		val rawManifest = try {
			ResourcePackJson.parseToJsonElement(manifestEntry.contents).jsonObject
		} catch (e: Exception) {
			diagnostics += ResourcePackDiagnostic("manifest.json", message = "Malformed JSON: ${e.message}")
			throw ResourcePackParseException(diagnostics)
		}

		val migratedManifest = ResourceTypes.MANIFEST.migrateJson(rawManifest, CURRENT_FORMAT_VERSION)
		val parsedManifest = ResourceTypes.MANIFEST.validateAndDecode(manifestEntry.path, migratedManifest, diagnostics) as? SourcedResource<PackManifest>

		if (parsedManifest == null) {
			throw ResourcePackParseException(diagnostics)
		}

		val formatVersion = parsedManifest.value.formatVersion
		if (formatVersion < MIN_SUPPORTED_FORMAT_VERSION) {
			diagnostics += ResourcePackDiagnostic("manifest.json", message = "Unsupported format version $formatVersion. Minimum supported is $MIN_SUPPORTED_FORMAT_VERSION")
			throw ResourcePackParseException(diagnostics)
		}

		val objects = mutableMapOf<ResourceType<*>, MutableList<Pair<String, JsonObject>>>()
		val patches = mutableMapOf<ResourceType<*>, MutableList<Pair<String, JsonArray>>>()

		for (entry in entries) {
			if (entry.path == "manifest.json") continue
			val type = types.firstOrNull { it.matches(entry.path) }
			if (type != null) {
				try {
					val element = ResourcePackJson.parseToJsonElement(entry.contents)
					if (element is JsonArray) {
						patches.getOrPut(type) { mutableListOf() }.add(entry.path to element)
					} else if (element is JsonObject) {
					    val migrated = (type as JsonResourceType<*>).migrateJson(element, formatVersion)
						objects.getOrPut(type) { mutableListOf() }.add(entry.path to migrated)
					} else {
					    diagnostics += ResourcePackDiagnostic(entry.path, message = "Expected JSON object or array")
					}
				} catch (e: Exception) {
					diagnostics += ResourcePackDiagnostic(entry.path, message = "Malformed JSON: ${e.message}")
				}
			}
		}
		if (diagnostics.isNotEmpty()) throw ResourcePackParseException(diagnostics.sortedWith(compareBy<ResourcePackDiagnostic> { it.path }.thenBy { it.pointer }))
		return ParsedResourcePack(source.description, parsedManifest, objects, patches)
	}
}
