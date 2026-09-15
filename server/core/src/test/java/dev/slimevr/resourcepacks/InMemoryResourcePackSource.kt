package dev.slimevr.resourcepacks

class InMemoryResourcePackSource(
	private val files: Map<String, String>,
	override val description: String = "in memory",
) : ResourcePackSource {
	override suspend fun entries(): List<ResourcePackEntry> = files.entries
		.map { ResourcePackEntry(normalizePath(it.key), it.value) }
		.sortedBy { it.path }
}
