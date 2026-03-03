package dev.slimevr.desktop.install.drivers

import io.eiren.util.logging.LogManager
import java.nio.file.Paths

class Windows {

	val path = Paths.get("").toAbsolutePath().toString()

	fun updateWindows() {
		usbDrivers()
		steamVRDriver()
		feeder()
	}

	fun usbDrivers() {
		val installedDriversList = executeShellCommand("powershell.exe", "pnputil", "/enum-drivers")
		val ch341ser = installedDriversList.contains("ch341ser.inf")
		val ch343ser = installedDriversList.contains("ch343ser.inf")
		val silabser = installedDriversList.contains("silabser.inf")

		if (ch341ser && ch343ser && silabser) {
			LogManager.info("USB drivers already installed!")
			return
		}
		LogManager.info("USB drivers not found, installing")
		val driverInstallOutput = executeShellCommand("$path\\installusbdrivers.bat")
		LogManager.info(driverInstallOutput)
	}

	fun feeder() {
		val feederOutput = executeShellCommand("${path}\\${WINDOWSFEEDERDIRECTORY}\\SlimeVR-Feeder-App.exe", "--install")
		if (feederOutput.lowercase().contains("manifest is not installed")) {
			LogManager.warning("Could not install feeder application")
		} else {
			LogManager.info("Successfully installed feeder application")
		}
	}

	fun steamVRDriver() {
		val steamVRLocation = executeShellCommand("powershell.exe", "-Command", "(Get-ItemProperty \'HKLM:\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\Steam App 250820\').InstallLocation").trim()
		if (!steamVRLocation.contains("SteamVR")) {
			LogManager.warning("SteamVR not installed, cannot install SlimeVR Steam driver.")
			return
		}
		var vrPathRegContents = executeShellCommand("${steamVRLocation}\\bin\\win64\\vrpathreg.exe", "finddriver", "slimevr")
		var isDriverRegistered = vrPathRegContents.contains("slimevr")
		if (isDriverRegistered) {
			LogManager.info("steamVR driver is already registered. Skipping...")
			return
		}
		LogManager.info("Installing SteamVR Driver")
		executeShellCommand(
			"${steamVRLocation}\\bin\\win64\\vrpathreg.exe",
			"adddriver",
			"${
				Paths.get(
					"",
				).toAbsolutePath()
			}\\${WINDOWSSTEAMVRDRIVERDIRECTORY}",
		)
		vrPathRegContents = executeShellCommand("${steamVRLocation}\\bin\\win64\\vrpathreg.exe", "finddriver", "slimevr")
		isDriverRegistered = vrPathRegContents.contains("slimevr")
		if (!isDriverRegistered) {
			LogManager.warning("Server couldn't install SlimeVR driver.")
			return
		}
		LogManager.info("SteamVR driver successfully installed.")
	}

	companion object {
		private const val WINDOWSSTEAMVRDRIVERDIRECTORY = "slimevr-openvr-driver-win64"
		private const val WINDOWSFEEDERDIRECTORY = "SlimeVR-Feeder-App-win64"
	}
}
