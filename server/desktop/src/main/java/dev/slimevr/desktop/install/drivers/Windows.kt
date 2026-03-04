package dev.slimevr.desktop.install.drivers

import io.eiren.util.logging.LogManager
import java.io.File

class Windows {

	val path = System.getProperty("user.dir")

	fun updateWindows() {
		usbDrivers()
		steamVRDriver()
		feeder()
	}

	fun usbDrivers() {
		val installedDriversList = executeShellCommand("powershell.exe", "pnputil", "/enum-drivers")
		if (installedDriversList == null) {
			LogManager.warning("Error installing usb drivers")
			return
		}
		val ch341ser = installedDriversList.contains("ch341ser.inf")
		val ch343ser = installedDriversList.contains("ch343ser.inf")
		val silabser = installedDriversList.contains("silabser.inf")

		if (ch341ser && ch343ser && silabser) {
			LogManager.info("USB drivers already installed!")
			return
		}
		LogManager.info("USB drivers not found, installing")
		executeShellCommand("$path\\installusbdrivers.bat")
		try {
			val usbDriversLog = File("driver_install.log").readText()
			LogManager.info(usbDriversLog)
		} catch (e: Error) {
			LogManager.warning("Error reading driver installation log, $e")
		}
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
		val steamVRLocation = executeShellCommand("powershell.exe", "-Command", "(Get-ItemProperty \'HKLM:\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\Steam App 250820\').InstallLocation")?.trim()
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
