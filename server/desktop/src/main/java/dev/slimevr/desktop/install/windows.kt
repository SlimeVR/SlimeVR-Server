package dev.slimevr.desktop.install

import com.sun.jna.platform.win32.Advapi32Util
import com.sun.jna.platform.win32.WinReg
import dev.slimevr.AppLogger
import kotlin.io.path.Path
import kotlin.io.path.exists

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
	val workingDir = System.getProperty("user.dir")
	val steamVRLocation = getKeyByPath(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\Steam App 250820")["InstallLocation"]
	if (steamVRLocation == null || !steamVRLocation.endsWith("SteamVR")) {
		AppLogger.install.warn("SteamVR driver installation failed (couldn't find SteamVR install location)")
		return
	}

	run {
		val standaloneDriverLocation = Path(steamVRLocation, "drivers", "slimevr")
		if (standaloneDriverLocation.exists()) {
			AppLogger.install.warn("Skipping SteamVR driver installation as the non-Steam installer has already placed it in the SteamVR drivers folder")
			AppLogger.install.warn("If you would like the Steam version to manage the SteamVR driver (recommended), uninstall the non-Steam version of SlimeVR, or delete the '$standaloneDriverLocation' folder")
			return
		}
	}

	val pathRegPath = "$steamVRLocation\\bin\\win64\\vrpathreg.exe"
	val (findExitCode, _) = executeShellCommand(pathRegPath, "finddriver", "slimevr") ?: run {
		AppLogger.install.warn("SteamVR driver installation failed (failed to run vrpathreg finddriver)")
		return
	}
	if (!shouldInstallDriver(findExitCode)) {
		AppLogger.install.info("Skipping SteamVR driver installation (${getDriverInstallSkipReason(findExitCode)})")
		return
	}

	val (addExitCode, _) = executeShellCommand(pathRegPath, "adddriver", "$workingDir\\$WINDOWS_STEAMVR_DRIVER_DIRECTORY") ?: run {
		AppLogger.install.warn("SteamVR driver installation failed (failed to run vrpathreg adddriver)")
		return
	}
	if (addExitCode != 0) {
		AppLogger.install.warn("SteamVR driver installation failed (vrpathreg adddriver exited with code $addExitCode)")
		return
	}

	AppLogger.install.info("SteamVR driver successfully installed")
}
