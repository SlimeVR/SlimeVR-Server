package dev.slimevr.desktop.games.vrchat

import com.sun.jna.Memory
import com.sun.jna.platform.win32.Advapi32
import com.sun.jna.platform.win32.Advapi32Util
import com.sun.jna.platform.win32.WinNT
import com.sun.jna.platform.win32.WinReg
import com.sun.jna.ptr.IntByReference
import io.eiren.util.logging.LogManager
import java.io.BufferedReader
import java.io.FileReader
import java.io.InvalidObjectException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.io.path.Path
import kotlin.io.path.exists

abstract class AbstractRegEdit {
	abstract fun getQwordValue(path: String, key: String): Double?
	abstract fun getDwordValue(path: String, key: String): Int?
	abstract fun getVRChatKeys(path: String): Map<String, String>
}

class RegEditWindows : AbstractRegEdit() {
	// Vrchat is dumb and write 64 bit doubles in the registry as DWORD instead of QWORD.
	// so we have to be creative
	override fun getQwordValue(path: String, key: String): Double? {
		val hKey = WinReg.HKEY_CURRENT_USER
		val phkResult = WinReg.HKEYByReference()

		// Open the registry key
		if (Advapi32.INSTANCE.RegOpenKeyEx(hKey, path, 0, WinNT.KEY_READ, phkResult) != 0) {
			LogManager.severe("[VRChatRegEdit] Error: Cannot open registry key")
			return null
		}

		val lpData = Memory(8)
		val lpcbData = IntByReference(8)

		val result = Advapi32.INSTANCE.RegQueryValueEx(
			phkResult.value,
			key,
			0,
			null,
			lpData,
			lpcbData,
		)
		Advapi32.INSTANCE.RegCloseKey(phkResult.value)

		if (result != 0) {
			LogManager.severe("[VRChatRegEdit] Error: Cannot read registry key")
			return null
		}
		return lpData.getDouble(0)
	}

	override fun getDwordValue(path: String, key: String): Int? = try {
		val data = Advapi32Util.registryGetIntValue(WinReg.HKEY_CURRENT_USER, path, key)
		data
	} catch (e: Exception) {
		LogManager.severe("[VRChatRegEdit] Error reading DWORD: ${e.message}")
		null
	}

	override fun getVRChatKeys(path: String): Map<String, String> {
		val keysMap = mutableMapOf<String, String>()

		try {
			Advapi32Util.registryGetValues(WinReg.HKEY_CURRENT_USER, path).forEach {
				keysMap[it.key.replace("""_h\d+$""".toRegex(), "")] = it.key
			}
		} catch (e: Exception) {
			LogManager.severe("[VRChatRegEdit] Error reading Values from VRC registry: ${e.message}")
		}
		return keysMap
	}
}

class RegEditLinux : AbstractRegEdit() {
	init {
		if (USER_REG_PATH == null) {
			LogManager.info("[VRChatRegEdit] Couldn't find any VRChat registry file")
		} else {
			LogManager.info("[VRChatRegEdit] Using VRChat registry file: $USER_REG_PATH")
		}
	}
	lateinit var registry: Map<String, String>

	@OptIn(ExperimentalStdlibApi::class)
	override fun getQwordValue(path: String, key: String): Double? {
		val value = registry[key] ?: return null
		if (!value.startsWith("hex(4):")) {
			LogManager.severe("[VRChatRegEdit] Couldn't find value with the expected type")
			return null
		}
		return ByteBuffer.wrap(value.substring(7).hexToByteArray(HEX_FORMAT))
			.order(ByteOrder.LITTLE_ENDIAN)
			.double
	}

	override fun getDwordValue(path: String, key: String): Int? = try {
		val value = registry[key] ?: return null
		if (value.startsWith("dword:")) {
			value.substring(6).toInt()
		} else {
			throw InvalidObjectException("The requested key is not a DWORD but it is instead a $value")
		}
	} catch (e: Exception) {
		LogManager.severe("[VRChatRegEdit] Error reading DWORD: ${e.message}")
		null
	}

	override fun getVRChatKeys(path: String): Map<String, String> {
		val keysMap = mutableMapOf<String, String>()
		val map = mutableMapOf<String, String>()

		try {
			BufferedReader(FileReader(USER_REG_PATH?.toFile() ?: return keysMap)).use { reader ->
				// The reg file uses double backward-slash for paths
				val actualPath = "[${path.replace("\\", """\\""")}]"
				while (reader.ready()) {
					val line = reader.readLine()
					if (!line.startsWith(actualPath)) continue
					// Skip the `#time` line
					reader.readLine()
					while (reader.ready()) {
						val keyValue = reader.readLine()
						if (keyValue == "") break

						KEY_VALUE_PATTERN.matchEntire(keyValue)?.let {
							map[it.groupValues[1]] = it.groupValues[2]
							keysMap[it.groupValues[1].replace("""_h\d+$""".toRegex(), "")] = it.groupValues[1]
						}
					}
					break
				}
			}
		} catch (e: Exception) {
			LogManager.severe("[VRChatRegEdit] Error reading Values from VRC registry: ${e.message}")
		}
		registry = map
		return keysMap
	}

	companion object {
		const val USER_REG_SUBPATH = "steamapps/compatdata/438100/pfx/user.reg"
		val USER_REG_PATH =
			System.getenv("HOME")?.let {
				Path(it, ".steam", "root", USER_REG_SUBPATH).let { if (it.exists()) it else null }
					?: Path(it, ".steam", "debian-installation", USER_REG_SUBPATH).let { if (it.exists()) it else null }
					?: Path(it, ".var", "app", "com.valvesoftware.Steam", "data", "Steam", USER_REG_SUBPATH).let { if (it.exists()) it else null }
			}
		val KEY_VALUE_PATTERN = Regex(""""(.+)"=(.+)""")

		@OptIn(ExperimentalStdlibApi::class)
		val HEX_FORMAT = HexFormat {
			upperCase = false
			bytes.byteSeparator = ","
		}
	}
}
