package dev.slimevr.config

import io.eiren.util.ann.ThreadSafe
import io.eiren.util.logging.LogManager
import net.mamoe.yamlkt.Yaml
import net.mamoe.yamlkt.YamlElement
import net.mamoe.yamlkt.YamlList
import net.mamoe.yamlkt.YamlLiteral
import net.mamoe.yamlkt.YamlMap
import okio.FileSystem
import okio.Path.Companion.toPath
import java.io.IOException
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.concurrent.locks.ReentrantReadWriteLock
import java.util.regex.Pattern
import kotlin.concurrent.read
import kotlin.concurrent.write
import kotlin.io.FileAlreadyExistsException

// The yaml object api is very primitive as it is normally supposed to be only used by kotlinx.serialization
// ive added some extensions to make our lives easier
private fun YamlMap.toMutable() = this.entries.associate { it.key to it.value }.toMutableMap()

private operator fun Map<YamlElement, YamlElement>.get(key: String): YamlElement? = this[YamlLiteral(key)]

private operator fun MutableMap<YamlElement, YamlElement>.set(key: String, value: Any?) {
	if (value == null) {
		this.remove(key)
		return
	}
	this[YamlLiteral(key)] = when (value) {
		is YamlElement -> value
		is Number, is Boolean -> YamlLiteral(value.toString())
		else -> YamlLiteral(value.toString())
	}
}

private fun MutableMap<YamlElement, YamlElement>.remove(key: String) = this.remove(YamlLiteral(key))

private fun YamlElement?.asFloat(default: Float = 0f): Float = (this as? YamlLiteral)?.content?.toFloatOrNull() ?: default
private fun YamlElement?.asInt(default: Int = 0): Int = (this as? YamlLiteral)?.content?.toIntOrNull() ?: default

private fun MutableMap<YamlElement, YamlElement>.updateMap(key: String, block: MutableMap<YamlElement, YamlElement>.() -> Unit) {
	val nestedMap = (this[YamlLiteral(key)] as? YamlMap)?.toMutable() ?: mutableMapOf()
	nestedMap.block()
	this[YamlLiteral(key)] = YamlMap(nestedMap)
}

fun migrateYamlConfig(modelData: YamlMap, version: Int): YamlMap {
	val config = modelData.toMutable()

	try {
		if (version < 7) error("Config version $version is too old to be migrated.")

		if (version < 8) {
			config.updateMap("keybindings") {
				val renaming = mapOf(
					"resetBinding" to "fullResetBinding",
					"quickResetBinding" to "yawResetBinding",
					"resetMountingBinding" to "mountingResetBinding",
					"resetDelay" to "fullResetDelay",
					"quickResetDelay" to "yawResetDelay",
					"resetMountingDelay" to "mountingResetDelay",
				)
				renaming.forEach { (old, new) ->
					this[old]?.let { value ->
						this[new] = value
						remove(old)
					}
				}
			}

			config.updateMap("tapDetection") {
				listOf(
					"quickResetDelay" to "yawResetDelay",
					"resetDelay" to "fullResetDelay",
					"quickResetEnabled" to "yawResetEnabled",
					"resetEnabled" to "fullResetEnabled",
					"quickResetTaps" to "yawResetTaps",
					"resetTaps" to "fullResetTaps",
				).forEach { (old, new) ->
					this[old]?.let { this[new] = it }
				}
			}
		}

		if (version < 9) {
			config.updateMap("skeleton") {
				this.updateMap("offsets") {
					this["chestLength"] = this["chestLength"].asFloat() / 2f
					this["upperChestLength"] = this["chestLength"].asFloat() / 2f
				}
			}
		}

		if (version < 10) {
			// Change default AutoBone recording length from 20 to 30
			// seconds
			config.updateMap("autoBone") {
				if (this["sampleCount"].asInt() == 1000) {
					this["sampleCount"] = 1500
				}
			}
		}

		if (version < 11) {
			config.updateMap("trackers") {
				updateMap("HMD") {
					this["designation"] = "body:head"
				}
			}
		}

		if (version < 12) {
			// Update AutoBone defaults
			config.updateMap("autoBone") {
				if (this["offsetSlideErrorFactor"].asFloat() == 2.0f) {
					this["offsetSlideErrorFactor"] = 1f
				}
				if (this["bodyProportionErrorFactor"].asFloat() == 0.825f) {
					this["bodyProportionErrorFactor"] = 0.25f
				}
			}
		}

		if (version < 13) {
			config.updateMap("trackers") {
				val macAddressRegex = "udp://((?:[a-zA-Z\\d]{2}:){5}[a-zA-Z\\d]{2})/0"
				val pattern: Pattern = Pattern.compile(macAddressRegex)
				val devices = (config["knownDevices"] as? YamlList)?.toMutableList() ?: ArrayList()
				this.keys.forEach {
					val trackerId = it.toString()
					val matcher = pattern.matcher(trackerId)
					if (matcher.find()) {
						devices.add(YamlLiteral(matcher.group(1)))
					}
				}
				config["knownDevices"] = YamlList(devices)
			}
		}

		if (version < 14) {
			val autoBone = config["autoBone"] as? YamlMap
			if (autoBone != null) {
				// Move HMD height from AutoBone to Skeleton
				autoBone["targetHmdHeight"]?.let { height ->
					config.updateMap("skeleton") {
						this["hmdHeight"] = height
					}
				}

				config.updateMap("autoBone") {
					if (this["offsetSlideErrorFactor"].asFloat() == 1.0f && this["slideErrorFactor"].asFloat() == 0.0f) {
						this["offsetSlideErrorFactor"] = 0.0f
						this["slideErrorFactor"] = 1.0f
					}
					if (this["bodyProportionErrorFactor"].asFloat() == 0.25f) {
						this["bodyProportionErrorFactor"] = 0.05f
					}
					if (this["numEpochs"].asInt() == 100) {
						this["numEpochs"] = 50
					}
				}
			}
		}

		if (version < 15) {
			config.updateMap("trackingChecklist") {
				this["ignoredStepsIds"] = null
			}
		}
	} catch (e: Exception) {
		error("Migration error: ${e.message}")
	}

	config["modelVersion"] = VRConfig.CONFIG_VERSION.toString()

	return YamlMap(config)
}

fun fixTrackerKeys(input: String): String {
	val regex = """^(\s*)(udp://.*):(\s*)$""".toRegex(RegexOption.MULTILINE)

	return input.replace(regex) { match ->
		val indent = match.groupValues[1]
		val keyPart = match.groupValues[2]
		val trailing = match.groupValues[3]
		"""$indent"$keyPart":$trailing"""
	}
}

class ConfigManager(private val configPath: String) {
	private lateinit var vrConfig: VRConfig

	private val lock = ReentrantReadWriteLock()

	fun get(block: (VRConfig) -> Unit) {
		lock.read {
			return block(vrConfig)
		}
	}

	fun mutate(block: (VRConfig) -> Unit) {
		lock.write {
			block(vrConfig)
		}
	}


	val tmpCfgFile = Paths.get("$configPath.tmp")!!
	val cfgFile = Paths.get(configPath)!!

	fun handleMigrations() {
		val configString = FileSystem.SYSTEM.read(configPath.toPath()) {
			readUtf8()
		}
		val cleanedYaml = configString
			.removePrefix("---")
			.trim()
		val quotedYaml = fixTrackerKeys(cleanedYaml)
		val configRoot = Yaml.decodeYamlFromString(quotedYaml) as YamlMap

		val currentVersion = configRoot["modelVersion"].toString().toIntOrNull() ?: error("unable to get config version")
		if (currentVersion < VRConfig.CONFIG_VERSION) {
			backupConfig(".v$currentVersion")

			LogManager.info("Migrating config from version \"$currentVersion\" to \"${VRConfig.CONFIG_VERSION}\"")
			val migratedElement = migrateYamlConfig(configRoot, currentVersion)
			val configStr = Yaml.encodeToString(migratedElement)
			writeToTmp(configStr)

			try {
				atomicMove(tmpCfgFile, cfgFile)
			} catch (e: IOException) {
				error(
					"Unable to move migrated config from \"$tmpCfgFile\" to \"$cfgFile\", reason: ${e.message}",
				)
			}
			LogManager.info("VRConfig migrated to the latest version \"${VRConfig.CONFIG_VERSION}\"")
		}
	}

	fun loadConfig() {
		if (!configPath.toPath().toFile().exists()) {
			this.vrConfig = VRConfig()
			return
		}

		try {
			handleMigrations()

			val configString = FileSystem.SYSTEM.read(configPath.toPath()) {
				readUtf8()
			}
			val cleanedYaml = configString
				.removePrefix("---")
				.trim()
			val quotedYaml = fixTrackerKeys(cleanedYaml)
			this.vrConfig = Yaml.decodeFromString(VRConfig.serializer(), quotedYaml)
		} catch (e: Exception) {
			// Log the exception
			LogManager.severe("Config failed to load: $e")
			// Make a backup of the erroneous config
			backupConfig()
			this.vrConfig = VRConfig()
		}
	}

	fun backupConfig(suffix: String = "") {
		val tmpBakCfgFile = Paths.get("$configPath$suffix.bak.tmp")
		val bakCfgFile = Paths.get("$configPath$suffix.bak")

		try {
			Files
				.copy(
					cfgFile,
					tmpBakCfgFile,
					StandardCopyOption.REPLACE_EXISTING,
					StandardCopyOption.COPY_ATTRIBUTES,
				)
			LogManager.info("Made a backup copy of config to \"$tmpBakCfgFile\"")
		} catch (e: IOException) {
			LogManager
				.severe(
					("Unable to make backup copy of config from \"$cfgFile\" to \"$tmpBakCfgFile\""),
					e,
				)
			return // Abort write
		}

		try {
			atomicMove(tmpBakCfgFile, bakCfgFile)
		} catch (e: IOException) {
			LogManager
				.severe(
					("Unable to move backup config from \"$tmpBakCfgFile\" to \"$bakCfgFile\""),
					e,
				)
		}
	}

	private fun writeToTmp(config: String) {
		try {
			val cfgFolder = cfgFile.toAbsolutePath().parent.toFile()
			if (!cfgFolder.exists() && !cfgFolder.mkdirs()) {
				error("Unable to create folders for config on path \"$cfgFile\"")
			}
			tmpCfgFile.toFile().writeText(config)
		} catch (e: IOException) {
			error("Unable to write serialized config to \"$tmpCfgFile\", reason: ${e.message}")
		}
	}

	@ThreadSafe
	@Synchronized
	fun saveConfig() {
		// Serialize config
		try {
			val configStr = Yaml.encodeToString(VRConfig.serializer(), this.vrConfig)
			writeToTmp(configStr)
		} catch (e: Exception) {
			LogManager.severe("Unable to write serialized config to \"$tmpCfgFile\"", e)
			return // Abort write
		}

		// Overwrite old config
		try {
			atomicMove(tmpCfgFile, cfgFile)
		} catch (e: IOException) {
			LogManager
				.severe(
					"Unable to move new config from \"$tmpCfgFile\" to \"$cfgFile\"",
					e,
				)
		}
	}

	fun resetConfig() {
		this.vrConfig = VRConfig()
		saveConfig()
	}

	@Throws(IOException::class)
	fun atomicMove(from: Path, to: Path) {
		try {
			// Atomic move to overwrite
			Files.move(from, to, StandardCopyOption.ATOMIC_MOVE)
		} catch (e: AtomicMoveNotSupportedException) {
			// Atomic move not supported or does not replace, try just replacing
			Files.move(from, to, StandardCopyOption.REPLACE_EXISTING)
		} catch (e: FileAlreadyExistsException) {
			Files.move(from, to, StandardCopyOption.REPLACE_EXISTING)
		}
	}
}
