package dev.slimevr.resourcepacks

import kotlinx.serialization.json.JsonObject

data class SourcedResource<T>(val path: String, val value: T, val raw: JsonObject)

class ParsedResourcePack(
	val source: String,
	val manifest: SourcedResource<PackManifest>,
	val objects: Map<ResourceType<*>, List<Pair<String, JsonObject>>>,
	val entries: Map<String, ResourcePackEntry> = emptyMap(),
)

data class ResourcePackDiagnostic(val path: String, val pointer: String = "", val message: String)
class ResourcePackParseException(val diagnostics: List<ResourcePackDiagnostic>) :
	IllegalArgumentException(
		diagnostics.joinToString("\n") { "${it.path}${it.pointer}: ${it.message}" },
	)

data class ResourcePackFailure(val folder: String, val diagnostics: List<ResourcePackDiagnostic>)
data class ResourcePackCatalog(
	val core: ParsedResourcePack,
	val userPacks: List<ParsedResourcePack>,
	val failures: List<ResourcePackFailure>,
)
