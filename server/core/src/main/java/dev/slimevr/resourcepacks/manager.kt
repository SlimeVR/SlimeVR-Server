package dev.slimevr.resourcepacks

import dev.slimevr.config.ConfigStorage
import dev.slimevr.config.StorageEntryType
import dev.slimevr.config.configPath
import dev.slimevr.logging.AppLogger

object ResourcePackManager {
	suspend fun load(storage: ConfigStorage, classLoader: ClassLoader): ResourcePackCatalog {
		val core = ResourcePackParser.parse(ClasspathResourcePackSource.core(classLoader))
		val root = "resourcepacks"
		if (!storage.ensureDirectory(root)) {
			AppLogger.config.error("Unable to create ${storage.displayPath(root)}; loading bundled core pack only")
			return ResourcePackCatalog(core, emptyList(), emptyList())
		}
		val folders = try {
			storage.list(root).filter { it.type == StorageEntryType.DIRECTORY }.sortedBy { it.name }
		} catch (e: Exception) {
			AppLogger.config.error(e, "Unable to enumerate ${storage.displayPath(root)}; loading bundled core pack only")
			return ResourcePackCatalog(core, emptyList(), emptyList())
		}
		val packs = mutableListOf<ParsedResourcePack>()
		val failures = mutableListOf<ResourcePackFailure>()
		for (folder in folders) {
			val packRoot = configPath(root, folder.name)
			val containsManifest = try {
				storage.list(packRoot).any { it.name == "manifest.json" && it.type == StorageEntryType.FILE }
			} catch (e: Exception) {
				val diagnostic = ResourcePackDiagnostic("manifest.json", message = "Unable to inspect pack: ${e.message}")
				failures += ResourcePackFailure(folder.name, listOf(diagnostic))
				AppLogger.config.error("Skipping unreadable resource pack '${folder.name}': ${diagnostic.message}")
				continue
			}
			if (!containsManifest) continue
			try {
				packs += ResourcePackParser.parse(StorageResourcePackSource(storage, packRoot))
			} catch (e: ResourcePackParseException) {
				failures += ResourcePackFailure(folder.name, e.diagnostics)
				AppLogger.config.error("Skipping invalid resource pack '${folder.name}': ${e.message}")
			}
		}
		return ResourcePackCatalog(core, packs.toList(), failures.toList())
	}
}
