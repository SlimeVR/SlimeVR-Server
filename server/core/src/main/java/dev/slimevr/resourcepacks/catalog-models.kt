package dev.slimevr.resourcepacks

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonArray

data class SourcedResource<T>(val path: String, val value: T, val raw: JsonObject)


class ParsedResourcePack(
	val source: String,
	val manifest: SourcedResource<PackManifest>,
	val objects: Map<ResourceType<*>, List<Pair<String, JsonObject>>>,
	val patches: Map<ResourceType<*>, List<Pair<String, JsonArray>>>,
) {
    fun <T : Any> getDecoded(type: JsonResourceType<T>): List<SourcedResource<T>> {
        val result = mutableListOf<SourcedResource<T>>()
        val objs = objects[type] ?: return emptyList()
        for ((path, obj) in objs) {
            val decoded = type.validateAndDecode(path, obj, mutableListOf())
            if (decoded != null) result.add(decoded)
        }
        return result
    }
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
