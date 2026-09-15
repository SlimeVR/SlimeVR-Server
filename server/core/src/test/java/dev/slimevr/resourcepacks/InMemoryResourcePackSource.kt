package dev.slimevr.resourcepacks

import dev.slimevr.resourcepacks.compiler.resolveResourcePacks
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal suspend fun testPack(
	id: String,
	vararg files: Pair<String, String>,
	before: List<String> = emptyList(),
	after: List<String> = emptyList(),
): ParsedResourcePack {
	val manifest = buildJsonObject {
		put("formatVersion", 1)
		put("id", id)
		put("nameKey", "example:name")
		put("descriptionKey", "example:description")
		put("version", JsonArray(listOf(1, 0, 0).map(::JsonPrimitive)))
		put("authors", JsonArray(listOf(JsonPrimitive("Test"))))
		put("before", JsonArray(before.map(::JsonPrimitive)))
		put("after", JsonArray(after.map(::JsonPrimitive)))
	}
	return ResourcePackParser.parse(InMemoryResourcePackSource(mapOf("manifest.json" to manifest.toString()) + files))
}

internal fun <T : Any> ParsedResourcePack.decoded(type: ResourceType<T>): List<SourcedResource<T>> = resolveResourcePacks(ResourcePackCatalog(this, emptyList(), emptyList())).get(type).values.map { it.resource }

class InMemoryResourcePackSource(
	private val files: Map<String, String>,
	override val description: String = "in memory",
) : ResourcePackSource {
	override suspend fun entries(): List<ResourcePackEntry> = files.entries
		.map { ResourcePackEntry(normalizePath(it.key), it.value) }
		.sortedBy { it.path }
}
