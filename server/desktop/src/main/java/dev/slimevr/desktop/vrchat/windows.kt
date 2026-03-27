package dev.slimevr.desktop.vrchat

import com.sun.jna.Library
import com.sun.jna.Memory
import com.sun.jna.Native
import com.sun.jna.platform.win32.Advapi32
import com.sun.jna.platform.win32.Advapi32Util
import com.sun.jna.platform.win32.Kernel32
import com.sun.jna.platform.win32.WinBase
import com.sun.jna.platform.win32.WinNT
import com.sun.jna.platform.win32.WinReg
import com.sun.jna.ptr.IntByReference
import dev.slimevr.AppLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import solarxr_protocol.rpc.VRCConfigValues

// RegNotifyChangeKeyValue is not in JNA's standard Advapi32
private interface RegistryNotify : Library {
	companion object {
		val INSTANCE: RegistryNotify = Native.load("Advapi32", RegistryNotify::class.java)
		const val REG_NOTIFY_CHANGE_LAST_SET = 0x00000004
	}

	fun RegNotifyChangeKeyValue(
		hKey: WinReg.HKEY,
		bWatchSubtree: Boolean,
		dwNotifyFilter: Int,
		hEvent: WinNT.HANDLE,
		fAsynchronous: Boolean,
	): Int
}

// VRChat writes 64-bit doubles as DWORD instead of QWORD, so we read raw bytes.
internal suspend fun windowsGetQwordValue(path: String, key: String): Double? {
	val phkResult = WinReg.HKEYByReference()
	if (Advapi32.INSTANCE.RegOpenKeyEx(WinReg.HKEY_CURRENT_USER, path, 0, WinNT.KEY_READ, phkResult) != 0) {
		AppLogger.vrc.error("[VRChatRegEdit] Cannot open registry key")
		return null
	}
	val lpData = Memory(8)
	val lpcbData = IntByReference(8)
	val result = Advapi32.INSTANCE.RegQueryValueEx(phkResult.value, key, 0, null, lpData, lpcbData)
	Advapi32.INSTANCE.RegCloseKey(phkResult.value)
	if (result != 0) {
		AppLogger.vrc.error("[VRChatRegEdit] Cannot read registry key")
		return null
	}
	return lpData.getDouble(0)
}

internal suspend fun windowsGetDwordValue(path: String, key: String): Int? = try {
	Advapi32Util.registryGetIntValue(WinReg.HKEY_CURRENT_USER, path, key)
} catch (e: Exception) {
	AppLogger.vrc.error("[VRChatRegEdit] Error reading DWORD: ${e.message}")
	null
}

internal suspend fun windowsGetVRChatKeys(path: String): Map<String, String> {
	val keysMap = mutableMapOf<String, String>()
	try {
		Advapi32Util.registryGetValues(WinReg.HKEY_CURRENT_USER, path).forEach {
			keysMap[it.key.replace("""_h\d+$""".toRegex(), "")] = it.key
		}
	} catch (e: Exception) {
		AppLogger.vrc.error("[VRChatRegEdit] Error reading VRC registry values: ${e.message}")
	}
	return keysMap
}

internal fun windowsVRCConfigFlow(): Flow<VRCConfigValues?> = flow {
	while (true) {
		// Open key and register notification BEFORE reading to avoid race conditions:
		// any change that happens between registration and the read will trigger a re-read on next iteration
		val phkResult = WinReg.HKEYByReference()
		if (Advapi32.INSTANCE.RegOpenKeyEx(WinReg.HKEY_CURRENT_USER, VRC_REG_PATH, 0, WinNT.KEY_NOTIFY, phkResult) != 0) {
			// VRChat not installed
			emit(null)
			return@flow
		}

		val hEvent = Kernel32.INSTANCE.CreateEvent(null, true, false, null)
		try {
			if (hEvent != null) {
				RegistryNotify.INSTANCE.RegNotifyChangeKeyValue(
					phkResult.value, false, RegistryNotify.REG_NOTIFY_CHANGE_LAST_SET, hEvent, true,
				)
			}

			val keys = windowsGetVRChatKeys(VRC_REG_PATH)
			emit(if (keys.isEmpty()) null else buildVRCConfigValues(
				intValue = { key -> keys[key]?.let { windowsGetDwordValue(VRC_REG_PATH, it) } },
				doubleValue = { key -> keys[key]?.let { windowsGetQwordValue(VRC_REG_PATH, it) } },
			))

			if (hEvent != null) {
				withContext(Dispatchers.IO) { Kernel32.INSTANCE.WaitForSingleObject(hEvent, WinBase.INFINITE) }
			}
		} finally {
			hEvent?.let { Kernel32.INSTANCE.CloseHandle(it) }
			Advapi32.INSTANCE.RegCloseKey(phkResult.value)
		}
	}
}.flowOn(Dispatchers.IO)
