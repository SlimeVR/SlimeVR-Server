package dev.slimevr.resourcepacks

import dev.slimevr.config.ConfigStorage
import dev.slimevr.config.StorageEntryType
import dev.slimevr.config.configPath
import dev.slimevr.logging.AppLogger
import dev.slimevr.resourcepacks.bones.CompiledSkeleton
import dev.slimevr.resourcepacks.compiler.ResourcePackCompilationException
import dev.slimevr.resourcepacks.compiler.compileResourcePacks
import kotlinx.coroutines.CancellationException
import kotlin.time.measureTimedValue

data class LoadedResourcePacks(val catalog: ResourcePackCatalog, val skeleton: CompiledSkeleton)

object ResourcePackManager {

	suspend fun loadCompiled(storage: ConfigStorage, classLoader: ClassLoader): LoadedResourcePacks {
		val catalog = load(storage, classLoader)
		val coreCatalog = catalog.copy(userPacks = emptyList())
		val (core, coreCompileTime) = measureTimedValue { compileResourcePacks(coreCatalog) }
		AppLogger.config.info("Compiled core pack in ${coreCompileTime.inWholeMilliseconds}ms.")
		if (catalog.userPacks.isEmpty()) return LoadedResourcePacks(catalog, core)
		return try {
			val ordered = orderResourcePacks(catalog)
			val (compiled, compileTime) = measureTimedValue { compileResourcePacks(ordered) }
			AppLogger.config.info("Compiled resource pack stack in ${compileTime.inWholeMilliseconds}ms.")
			LoadedResourcePacks(ordered, compiled)
		} catch (e: ResourcePackCompilationException) {
			AppLogger.config.error("Unable to compile resource packs; loading bundled core pack only:\n${e.message}")
			val failure = ResourcePackFailure(
				"compiled stack",
				e.diagnostics.map {
					ResourcePackDiagnostic("${it.packId}/${it.path}", message = it.message)
				},
			)
			LoadedResourcePacks(coreCatalog.copy(failures = catalog.failures + failure), core)
		}
	}

	suspend fun load(storage: ConfigStorage, classLoader: ClassLoader): ResourcePackCatalog {
		val (core, coreTime) = measureTimedValue { ResourcePackParser.parse(ClasspathResourcePackSource.core(classLoader)) }
		AppLogger.config.info("Loaded core pack in ${coreTime.inWholeMilliseconds}ms.")

		val root = "resourcepacks"
		if (!storage.ensureDirectory(root)) {
			AppLogger.config.error("Unable to create ${storage.displayPath(root)}; loading bundled core pack only")
			return ResourcePackCatalog(core, emptyList(), emptyList())
		}
		val folders = try {
			storage.list(root).filter { it.type == StorageEntryType.DIRECTORY }.sortedBy { it.name }
		} catch (e: Exception) {
			if (e is CancellationException) throw e
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
				if (e is CancellationException) throw e
				val diagnostic = ResourcePackDiagnostic("manifest.json", message = "Unable to inspect pack: ${e.message}")
				failures += ResourcePackFailure(folder.name, listOf(diagnostic))
				AppLogger.config.error("Skipping unreadable resource pack '${folder.name}': ${diagnostic.message}")
				continue
			}
			if (!containsManifest) continue
			try {
				val (parsed, packTime) = measureTimedValue { ResourcePackParser.parse(StorageResourcePackSource(storage, packRoot)) }

				if (parsed.manifest.value.formatVersion < ResourcePackParser.CURRENT_FORMAT_VERSION) {
					AppLogger.config.warn("Loaded pack '${folder.name}' in ${packTime.inWholeMilliseconds}ms. Format version ${parsed.manifest.value.formatVersion} was automatically migrated to ${ResourcePackParser.CURRENT_FORMAT_VERSION}. This older format is deprecated and may lose support in future updates.")
				} else {
					AppLogger.config.info("Loaded pack '${folder.name}' in ${packTime.inWholeMilliseconds}ms.")
				}
				packs += parsed
			} catch (e: ResourcePackParseException) {
				failures += ResourcePackFailure(folder.name, e.diagnostics)
				AppLogger.config.error("Skipping invalid resource pack '${folder.name}': ${e.message}")
			}
		}
		return ResourcePackCatalog(core, packs.toList(), failures.toList())
	}
}
