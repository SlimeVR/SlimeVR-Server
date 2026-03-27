package dev.slimevr.desktop.vrchat

import dev.slimevr.AppLogger
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.FileReader
import java.io.InvalidObjectException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.io.path.Path
import kotlin.io.path.exists

private const val USER_REG_SUBPATH = "steamapps/compatdata/438100/pfx/user.reg"
private val KEY_VALUE_PATTERN = Regex(""""(.+)"=(.+)""")
private val HEX_FORMAT = HexFormat { upperCase = false; bytes.byteSeparator = "," }

internal val linuxUserRegPath = System.getenv("HOME")?.let { home ->
	listOf(
		Path(home, ".steam", "root", USER_REG_SUBPATH),
		Path(home, ".steam", "debian-installation", USER_REG_SUBPATH),
		Path(home, ".var", "app", "com.valvesoftware.Steam", "data", "Steam", USER_REG_SUBPATH),
	).firstOrNull { it.exists() }
}

internal suspend fun linuxGetVRChatKeys(path: String, registry: MutableMap<String, String>): Map<String, String> {
	val keysMap = mutableMapOf<String, String>()
	registry.clear()
	try {
		withContext(Dispatchers.IO) {
			BufferedReader(FileReader(linuxUserRegPath?.toFile() ?: return@withContext)).use { reader ->
				val actualPath = "[${path.replace("\\", """\\""")}]"
				while (reader.ready()) {
					val line = reader.readLine()
					if (!line.startsWith(actualPath)) continue
					reader.readLine() // skip `#time` line
					while (reader.ready()) {
						val keyValue = reader.readLine()
						if (keyValue == "") break
						KEY_VALUE_PATTERN.matchEntire(keyValue)?.let {
							registry[it.groupValues[1]] = it.groupValues[2]
							keysMap[it.groupValues[1].replace("""_h\d+$""".toRegex(), "")] = it.groupValues[1]
						}
					}
					break
				}
			}
		}
	} catch (e: CancellationException) {
		throw e
	} catch (e: Exception) {
		AppLogger.vrc.error("[VRChatRegEdit] Error reading VRC registry values: ${e.message}")
	}
	return keysMap
}

internal suspend fun linuxGetQwordValue(registry: Map<String, String>, key: String): Double? {
	val value = registry[key] ?: return null
	if (!value.startsWith("hex(4):")) {
		AppLogger.vrc.error("[VRChatRegEdit] Unexpected registry value type for key $key")
		return null
	}
	return ByteBuffer.wrap(value.substring(7).hexToByteArray(HEX_FORMAT)).order(ByteOrder.LITTLE_ENDIAN).double
}

internal suspend fun linuxGetDwordValue(registry: Map<String, String>, key: String): Int? = try {
	val value = registry[key] ?: return null
	if (value.startsWith("dword:")) value.substring(6).toInt(16)
	else throw InvalidObjectException("Expected DWORD but got: $value")
} catch (e: CancellationException) {
	throw e
} catch (e: Exception) {
	AppLogger.vrc.error("[VRChatRegEdit] Error reading DWORD: ${e.message}")
	null
}

internal fun linuxVRCConfigFlow(): Flow<solarxr_protocol.rpc.VRCConfigValues?> = flow {
	val regPath = linuxUserRegPath ?: run {
		AppLogger.vrc.info("[VRChatRegEdit] Couldn't find any VRChat registry file")
		return@flow
	}
	AppLogger.vrc.info("[VRChatRegEdit] Using VRChat registry file: $regPath")

	val registry = mutableMapOf<String, String>()
	while (true) {
		val keys = linuxGetVRChatKeys(VRC_REG_PATH, registry)
		if (keys.isEmpty()) {
			emit(null)
		} else {
			emit(buildVRCConfigValues(
				intValue = { key -> keys[key]?.let { linuxGetDwordValue(registry, it) } },
				doubleValue = { key -> keys[key]?.let { linuxGetQwordValue(registry, it) } },
			))
		}
		delay(3000)
		// it seems that on linux, steam writes to the reg file is unpredictable.
		// I tried multiple things to just watch for file change instead of polling
		// without success. Polling was the simplest and most reliable
	}
}.flowOn(Dispatchers.IO)
