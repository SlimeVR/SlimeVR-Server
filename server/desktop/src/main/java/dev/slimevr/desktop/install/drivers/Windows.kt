package dev.slimevr.desktop.install.drivers

import com.sun.jna.platform.win32.WinReg
import dev.slimevr.desktop.games.vrchat.RegEditWindows
import io.eiren.util.logging.LogManager

class Windows {

	val path: String = System.getProperty("user.dir")

	fun updateWindows() {
		steamVRDriver()
	}


	fun steamVRDriver() {
		val regEdit = RegEditWindows()
		val regQuery = regEdit.getKeyByPath(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\Steam App 250820")
		val steamVRLocation = regQuery["InstallLocation"]
		if (steamVRLocation == null || !steamVRLocation.contains("SteamVR")) {
			LogManager.warning("Can't find SteamVR, unable to install SteamVR driver")
			return
		}

		val pathRegPath = "${steamVRLocation}\\bin\\win64\\vrpathreg.exe"
		val vrPathRegContents = executeShellCommand(pathRegPath, "finddriver", "slimevr")
		if (vrPathRegContents == null) {
			LogManager.warning("Error installing SteamVR driver")
			return
		}
		if (vrPathRegContents.contains("slimevr")) {
			LogManager.info("SteamVR driver is already installed")
			return
		}

		executeShellCommand(pathRegPath, "adddriver", "${path}\\${WINDOWS_STEAMVR_DRIVER_DIRECTORY}")

		if (executeShellCommand(pathRegPath, "finddriver", "slimevr")?.contains("slimevr") != true) {
			LogManager.warning("Failed to install SlimeVR driver")
			return
		}
		LogManager.info("SteamVR driver successfully installed")
	}

	companion object {
		private const val WINDOWS_STEAMVR_DRIVER_DIRECTORY = "slimevr-openvr-driver-win64"
		private const val WINDOWS_SERVER_NAME = "slimevr-win64.zip"
	}
}
