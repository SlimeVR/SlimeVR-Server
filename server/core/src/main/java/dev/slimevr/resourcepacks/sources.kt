package dev.slimevr.resourcepacks

import dev.slimevr.config.ConfigStorage
import dev.slimevr.config.StorageEntryType
import dev.slimevr.config.configPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

data class ResourcePackEntry(val path: String, val contents: String)

interface ResourcePackSource {
	val description: String
	suspend fun entries(): List<ResourcePackEntry>
}

class StorageResourcePackSource(
	private val storage: ConfigStorage,
	private val root: String,
	override val description: String = storage.displayPath(root),
) : ResourcePackSource {
	override suspend fun entries(): List<ResourcePackEntry> {
		val result = mutableListOf<ResourcePackEntry>()
		suspend fun visit(relative: String) {
			val directory = if (relative.isEmpty()) root else configPath(root, relative)
			for (entry in storage.list(directory).sortedBy { it.name }) {
				if (entry.name.isEmpty() || entry.name.contains('/') || entry.name.contains('\\') || entry.name == "." || entry.name == "..") {
					throw IOException("Invalid entry name '${entry.name}' in $directory")
				}
				val child = if (relative.isEmpty()) entry.name else "$relative/${entry.name}"
				when (entry.type) {
					StorageEntryType.SYMLINK -> throw IOException("Symbolic links are not permitted in resource packs ($child)")

					StorageEntryType.DIRECTORY -> visit(child)

					StorageEntryType.FILE -> {
						val path = normalizePath(child)
						val contents = storage.read(configPath(root, path))
							?: throw IOException("Unable to read $path")
						result += ResourcePackEntry(path, contents)
					}

					StorageEntryType.OTHER -> throw IOException("Unsupported filesystem entry in resource pack: $child")
				}
			}
		}
		visit("")
		return result.sortedBy { it.path }
	}
}

class ClasspathResourcePackSource private constructor(
	private val classLoader: ClassLoader,
	override val description: String,
) : ResourcePackSource {
	override suspend fun entries(): List<ResourcePackEntry> = withContext(Dispatchers.IO) {
		val indexPath = "$CORE_ROOT/index.txt"
		val paths = classLoader.getResourceAsStream(indexPath)?.bufferedReader()?.use { reader ->
			reader.lineSequence().filter { it.isNotBlank() }.map(::normalizePath).toList()
		} ?: throw IOException("Bundled core resource-pack index is missing ($indexPath)")
		paths.sorted().map { path ->
			val resource = "$CORE_ROOT/$path"
			val contents = classLoader.getResourceAsStream(resource)?.bufferedReader()?.use { it.readText() }
				?: throw IOException("Bundled core resource is missing ($resource)")
			ResourcePackEntry(path, contents)
		}
	}

	companion object {
		const val CORE_ROOT = "dev/slimevr/resourcepacks/core"

		fun core(classLoader: ClassLoader): ClasspathResourcePackSource {
			val resource = "$CORE_ROOT/manifest.json"
			val urls = classLoader.getResources(resource).toList()
			require(urls.size == 1) {
				when (urls.size) {
					0 -> "Bundled core resource pack is missing ($resource)"
					else -> "Bundled core resource pack is duplicated: ${urls.joinToString()}"
				}
			}
			return ClasspathResourcePackSource(classLoader, "bundled core pack (${urls.single()})")
		}
	}
}

internal fun normalizePath(path: String): String {
	val normalized = path.replace('\\', '/')
	require(normalized.isNotEmpty() && !normalized.startsWith('/') && normalized.split('/').none { it.isEmpty() || it == "." || it == ".." }) {
		"Invalid resource-pack path: $path"
	}
	return normalized
}
