package dev.slimevr.desktop.install

import com.sun.jna.platform.win32.Advapi32Util
import com.sun.jna.platform.win32.WinReg
import dev.slimevr.AppLogger

private const val WINDOWS_STEAMVR_DRIVER_DIRECTORY = "slimevr-openvr-driver-win64"

suspend fun installWindows() {
	installWindowsSteamVRDriver()
}

private suspend fun getKeyByPath(hkey: WinReg.HKEY, path: String): Map<String, String> {
	val keysMap = mutableMapOf<String, String>()
	try {
		Advapi32Util.registryGetValues(hkey, path).forEach {
			keysMap[it.key.replace("""_h\d+$""".toRegex(), "")] = it.value.toString()
		}
	} catch (e: Exception) {
		AppLogger.install.error("[RegEdit] Error reading values from registry: ${e.message}")
	}
	return keysMap
}

private suspend fun installWindowsSteamVRDriver() {
	val path = System.getProperty("user.dir")
	val regQuery = getKeyByPath(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\Steam App 250820")
	val steamVRLocation = regQuery["InstallLocation"]
	if (steamVRLocation == null || !steamVRLocation.contains("SteamVR")) {
		AppLogger.install.warn("Can't find SteamVR, unable to install SteamVR driver")
		return
	}
	val vrPathReg = "${steamVRLocation}\\bin\\win64\\vrpathreg.exe"
	val existing = executeShellCommand(vrPathReg, "finddriver", "slimevr")
	if (existing == null) {
		AppLogger.install.warn("Error installing SteamVR driver")
		return
	}
	if (existing.contains("slimevr")) {
		AppLogger.install.info("SteamVR driver is already installed")
		return
	}
	executeShellCommand(vrPathReg, "adddriver", "${path}\\${WINDOWS_STEAMVR_DRIVER_DIRECTORY}")
	if (executeShellCommand(vrPathReg, "finddriver", "slimevr")?.contains("slimevr") != true) {
		AppLogger.install.warn("Failed to install SlimeVR driver")
		return
	}
	AppLogger.install.info("SteamVR driver successfully installed")
}
