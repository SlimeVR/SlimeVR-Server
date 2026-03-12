package dev.slimevr.desktop.install.drivers

import com.sun.jna.platform.win32.WinReg
import dev.slimevr.desktop.games.vrchat.RegEditWindows
import io.eiren.util.logging.LogManager

class Windows {

	val path: String = System.getProperty("user.dir")

	fun updateWindows() {
		steamVRDriver()
		feeder()
	}

	fun feeder() {
		val feederOutput = executeShellCommand("${path}\\${WINDOWS_FEEDER_DIRECTORY}\\SlimeVR-Feeder-App.exe", "--install")
		if (feederOutput == null) {
			LogManager.warning("Error installing feeder")
			return
		}
		if (feederOutput.lowercase().contains("manifest is not installed")) {
			LogManager.warning("Could not install feeder application")
		} else {
			LogManager.info("Successfully installed feeder application")
		}
	}

	fun steamVRDriver() {
		val regEdit = RegEditWindows()
		val regQuery = regEdit.getKeyByPath(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\Steam App 250820")
		val steamVRLocation = regQuery["InstallLocation"]
		if (steamVRLocation == null) {
			LogManager.warning("Error installing SteamVR driver.")
			return
		}
		if (!steamVRLocation.contains("SteamVR")) {
			LogManager.warning("SteamVR not installed, cannot install SlimeVR Steam driver.")
			return
		}
		var vrPathRegContents = executeShellCommand("${steamVRLocation}\\bin\\win64\\vrpathreg.exe", "finddriver", "slimevr")
		if (vrPathRegContents == null) {
			LogManager.warning("Error installing SteamVR driver.")
			return
		}
		var isDriverRegistered = vrPathRegContents.contains("slimevr")
		if (isDriverRegistered) {
			LogManager.info("steamVR driver is already registered. Skipping...")
			return
		}
		LogManager.info("Installing SteamVR Driver")
		executeShellCommand(
			"${steamVRLocation}\\bin\\win64\\vrpathreg.exe",
			"adddriver",
			"${path}\\${WINDOWS_STEAMVR_DRIVER_DIRECTORY}",
		)
		vrPathRegContents = executeShellCommand("${steamVRLocation}\\bin\\win64\\vrpathreg.exe", "finddriver", "slimevr")
		if (vrPathRegContents == null) {
			LogManager.warning("Error installing SteamVR driver.")
			return
		}
		isDriverRegistered = vrPathRegContents.contains("slimevr")
		if (!isDriverRegistered) {
			LogManager.warning("Server couldn't install SlimeVR driver.")
			return
		}
		LogManager.info("SteamVR driver successfully installed.")
	}

	companion object {
		private const val WINDOWS_STEAMVR_DRIVER_DIRECTORY = "slimevr-openvr-driver-win64"
		private const val WINDOWS_FEEDER_DIRECTORY = "SlimeVR-Feeder-App-win64"
	}
}
