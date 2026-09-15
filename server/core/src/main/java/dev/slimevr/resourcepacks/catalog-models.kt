package dev.slimevr.resourcepacks

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

data class SourcedResource<T>(val path: String, val value: T, val raw: JsonObject)


class ParsedResourcePack(
	val source: String,
	val manifest: SourcedResource<PackManifest>,
	val resources: Map<ResourceType<*>, List<SourcedResource<*>>>,
) {
	@Suppress("UNCHECKED_CAST")
	fun <T : Any> get(type: ResourceType<T>): List<SourcedResource<T>> = (resources[type] as List<SourcedResource<T>>?) ?: emptyList()
}

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
