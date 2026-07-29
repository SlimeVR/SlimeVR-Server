package dev.slimevr.desktop.install.drivers

import com.sun.jna.platform.win32.WinReg
import dev.slimevr.desktop.games.vrchat.RegEditWindows
import io.eiren.util.logging.LogManager
import kotlin.io.path.Path
import kotlin.io.path.exists

class Windows {
	val path: String = System.getProperty("user.dir")

	fun updateWindows() {
		steamVRDriver()
	}

	fun steamVRDriver() {
		val regEdit = RegEditWindows()
		val steamVRLocation = regEdit.getKeyByPath(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\Steam App 250820")["InstallLocation"]
		if (steamVRLocation == null || !steamVRLocation.endsWith("SteamVR")) {
			LogManager.warning("SteamVR driver installation failed: couldn't find SteamVR")
			return
		}

		if (Path(steamVRLocation, "drivers", "slimevr").exists()) {
			LogManager.warning("Skipping SteamVR driver installation: driver is in SteamVR drivers folder")
			LogManager.warning("If you would like the SteamVR driver to automatically update, please uninstall the other version of SlimeVR")
			return
		}

		val pathRegPath = "$steamVRLocation\\bin\\win64\\vrpathreg.exe"
		val (findExitCode, _) = executeShellCommand(pathRegPath, "finddriver", "slimevr") ?: run {
			LogManager.warning("SteamVR driver installation failed: couldn't run vrpathreg finddriver")
			return
		}

		if (!shouldInstallDriver(findExitCode)) {
			LogManager.info("Skipping SteamVR driver installation: ${getDriverInstallSkipReason(findExitCode)}")
			return
		}

		val (addExitCode, _) = executeShellCommand(pathRegPath, "adddriver", "$path\\$WINDOWS_STEAMVR_DRIVER_DIRECTORY") ?: run {
			LogManager.warning("SteamVR driver installation failed: couldn't run vrpathreg adddriver")
			return
		}

		if (addExitCode != 0) {
			LogManager.warning("SteamVR driver installation failed: vrpathreg exited with code $addExitCode")
			return
		}
		LogManager.info("SteamVR driver successfully installed")
	}

	companion object {
		private const val WINDOWS_STEAMVR_DRIVER_DIRECTORY = "slimevr-openvr-driver-win64"
	}
}
