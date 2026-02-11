package dev.slimevr.config

import net.mamoe.yamlkt.YamlElement
import net.mamoe.yamlkt.YamlList
import net.mamoe.yamlkt.YamlLiteral
import net.mamoe.yamlkt.YamlMap
import java.util.regex.Pattern
import kotlin.collections.forEach
import kotlin.collections.set

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

	config["modelVersion"] = SettingsConfig.CONFIG_VERSION.toString()

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
