package dev.slimevr.config

import io.eiren.util.logging.LogManager
import kotlinx.serialization.KSerializer
import net.mamoe.yamlkt.Yaml
import net.mamoe.yamlkt.YamlMap
import okio.FileSystem
import okio.Path.Companion.toOkioPath
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

inline fun <reified T : Any> ConfigFileHandler<T>.modify(crossinline transform: T.() -> T): T = update { it.transform() }
open class ConfigFileHandler<T : Any>(
	private val serializer: KSerializer<T>,
	val initDefault: () -> T,
	private val path: String,
	private val currentVersion: Int,
	private val migrateYamlConfig: ((YamlMap, Int) -> YamlMap)?,
) {

	@Volatile
	private lateinit var value: T

	private val cfgFile = Paths.get(path)
	private val tmpCfgFile = Paths.get("$path.tmp")

	@Synchronized
	fun get(): T = value

	fun set(newValue: T) {
		value = newValue
	}

	@Synchronized
	fun update(transform: (T) -> T): T {
		value = transform(value)
		return value
	}

	fun load(): T {
		if (!cfgFile.toFile().exists()) {
			value = initDefault()
			return value
		}

		try {
			if (migrateYamlConfig != null) {
				handleMigrations()
			}
			value = decode(readAndPrepareYaml())
		} catch (e: Exception) {
			LogManager.severe("Failed to load config", e)
			backup()
		}

		return value
	}

	@Synchronized
	fun save() {
		val snapshot = value

		runCatching {
			writeToTmp(encode(snapshot))
			atomicMove(tmpCfgFile, cfgFile)
		}.onFailure {
			LogManager.severe("Failed to save config", it)
		}
	}

	fun reset(): T {
		value = initDefault()
		save()
		return value
	}

	private fun handleMigrations() {
		val root = decodeYamlRoot(readAndPrepareYaml())
		val version = root["version"]
			?.toString()
			?.toIntOrNull()
			?: error("Missing version")

		if (version >= currentVersion) return

		backup(".v$version")

		val migrated = migrateYamlConfig(root, version)
		writeToTmp(Yaml.encodeToString(migrated))
		atomicMove(tmpCfgFile, cfgFile)
	}

	private fun readAndPrepareYaml(): String = FileSystem.SYSTEM.read(cfgFile.toOkioPath()) {
		readUtf8().trim()
	}

	private fun encode(data: T): String = Yaml.encodeToString(serializer, data)

	private fun decode(yaml: String): T = Yaml.decodeFromString(serializer, yaml)

	private fun decodeYamlRoot(yaml: String): YamlMap = Yaml.decodeYamlFromString(yaml) as YamlMap

	private fun writeToTmp(text: String) {
		cfgFile.parent?.toFile()?.mkdirs()
		tmpCfgFile.toFile().writeText(text)
	}

	private fun backup(suffix: String = "") {
		val tmp = Paths.get("$path$suffix.bak.tmp")
		val bak = Paths.get("$path$suffix.bak")

		runCatching {
			Files.copy(cfgFile, tmp, StandardCopyOption.REPLACE_EXISTING)
			atomicMove(tmp, bak)
		}.onFailure {
			LogManager.severe("Backup failed", it)
		}
	}

	private fun atomicMove(from: Path, to: Path) {
		try {
			Files.move(from, to, StandardCopyOption.ATOMIC_MOVE)
		} catch (_: Exception) {
			Files.move(from, to, StandardCopyOption.REPLACE_EXISTING)
		}
	}

	@Synchronized
	fun updateAndSave(transform: (T) -> T): T {
		val result = update(transform)
		save()
		return result
	}
}
