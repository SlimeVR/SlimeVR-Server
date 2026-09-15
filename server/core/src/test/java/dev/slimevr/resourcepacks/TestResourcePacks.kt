package dev.slimevr.resourcepacks

import dev.slimevr.config.ConfigStorage
import dev.slimevr.config.StorageEntry
import dev.slimevr.config.StorageEntryType
import dev.slimevr.config.TextFileHandle
import dev.slimevr.resourcepacks.compiler.ResourcePackCompilationDiagnostic
import dev.slimevr.resourcepacks.compiler.ResourcePackCompilationException
import dev.slimevr.resourcepacks.compiler.compileResourcePacks
import dev.slimevr.resourcepacks.compiler.resolveResourcePacks
import dev.slimevr.testCoreResourcePack
import io.github.axisangles.ktmath.Vector3
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

internal const val CORE_BONE_COUNT = 61
internal const val CORE_PROPORTION_COUNT = 18

private const val EPSILON = 1e-6f

internal fun assertVectorEquals(expected: Vector3, actual: Vector3?, message: String) {
	assertTrue(actual != null && (expected - actual).lenSq() < EPSILON, "$message: expected $expected, got $actual")
}

internal fun assertFloatEquals(expected: Float, actual: Float, message: String) {
	assertTrue(kotlin.math.abs(expected - actual) < EPSILON, "$message: expected $expected, got $actual")
}

internal fun manifest(id: String, before: List<String> = emptyList(), after: List<String> = emptyList()): String = buildJsonObject {
	put("formatVersion", 1)
	put("id", id)
	put("nameKey", "example:name")
	put("descriptionKey", "example:description")
	put("version", JsonArray(listOf(1, 0, 0).map(::JsonPrimitive)))
	put("authors", JsonArray(listOf(JsonPrimitive("Test"))))
	put("before", JsonArray(before.map(::JsonPrimitive)))
	put("after", JsonArray(after.map(::JsonPrimitive)))
}.toString()

internal suspend fun testPack(
	id: String,
	vararg files: Pair<String, String>,
	before: List<String> = emptyList(),
	after: List<String> = emptyList(),
): ParsedResourcePack = ResourcePackParser.parse(InMemoryResourcePackSource(mapOf("manifest.json" to manifest(id, before, after)) + files))

internal fun <T : Any> ParsedResourcePack.decoded(type: ResourceType<T>): List<SourcedResource<T>> = resolveResourcePacks(ResourcePackCatalog(this, emptyList(), emptyList())).get(type).values.map { it.resource }

internal fun catalog(core: ParsedResourcePack, vararg users: ParsedResourcePack) = ResourcePackCatalog(core, users.toList(), emptyList())

internal suspend fun compilePack(vararg files: Pair<String, String>, packId: String = "example:test") = compileResourcePacks(catalog(testCoreResourcePack, ResourcePackParser.parse(InMemoryResourcePackSource(mapOf("manifest.json" to manifest(packId)) + files))))

internal suspend fun assertCompileFails(vararg files: Pair<String, String>, packId: String = "example:test"): List<ResourcePackCompilationDiagnostic> = assertFailsWith<ResourcePackCompilationException> { compilePack(*files, packId = packId) }.diagnostics

class InMemoryResourcePackSource(
	private val files: Map<String, String>,
	override val description: String = "in memory",
) : ResourcePackSource {
	override suspend fun entries(): List<ResourcePackEntry> = files.entries
		.map { ResourcePackEntry(normalizePath(it.key), it.value) }
		.sortedBy { it.path }
}

internal class MemoryStorage(
	private val files: Map<String, String>,
	private val symlinks: Set<String> = emptySet(),
	private val ensureDirectoryResult: Boolean = true,
) : ConfigStorage {
	var createdResourcePackDirectory = false

	override suspend fun read(path: String): String? = files[path]
	override suspend fun write(path: String, content: String) = Unit
	override suspend fun backup(path: String) = Unit
	override suspend fun exists(path: String): Boolean = path in files
	override suspend fun ensureDirectory(path: String): Boolean {
		if (path == "resourcepacks") createdResourcePackDirectory = ensureDirectoryResult
		return ensureDirectoryResult
	}
	override suspend fun list(path: String): List<StorageEntry> {
		val prefix = path.trimEnd('/') + "/"
		val children = linkedMapOf<String, StorageEntryType>()
		for (entry in files.keys + symlinks) {
			if (!entry.startsWith(prefix)) continue
			val remainder = entry.removePrefix(prefix)
			val name = remainder.substringBefore('/')
			val type = when {
				entry in symlinks && remainder == name -> StorageEntryType.SYMLINK
				remainder == name -> StorageEntryType.FILE
				else -> StorageEntryType.DIRECTORY
			}
			children[name] = type
		}
		return children.map { StorageEntry(it.key, it.value) }.sortedBy { it.name }
	}
	override suspend fun openTextFile(path: String): TextFileHandle = error("Not used")
}
