package dev.slimevr.desktop.install.drivers

import io.eiren.util.logging.LogManager
import java.nio.file.Paths

class Windows {

	val path = Paths.get("").toAbsolutePath().toString()

	fun updateWindows() {
		// First check if everything is already installed. Install it if it isn't
		usbDrivers()
		steamVRDriver()
		feeder()
	}

	fun usbDrivers() {

		val installedDriversList = executeShellCommand("powershell.exe", "pnputil /enum-drivers")
		val ch341ser = installedDriversList.contains("ch341ser.inf")
		val ch343ser = installedDriversList.contains("ch343ser.inf")
		val silabser = installedDriversList.contains("silabser.inf")

		if (ch341ser && ch343ser && silabser) {
			LogManager.info("drivers already installed!")
			return
		}
		LogManager.info("Cannot find one of the drivers, installing drivers")
		val driverinstallOutput = executeShellCommand("$path\\installusbdrivers.bat")
		LogManager.info(driverinstallOutput)
	}


	fun feeder() {
		executeShellCommand("${path}\\${WINDOWSFEEDERDIRECTORY}\\SlimeVR-Feeder-App.exe", "--install")
	}

	fun steamVRDriver() {
		val steamVRLocation = executeShellCommand("powershell.exe", "-Command", "(Get-ItemProperty \'HKLM:\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\Steam App 250820\').InstallLocation").trim()
		if (!steamVRLocation.contains("SteamVR")) {
			LogManager.warning("SteamVR not installed, cannot install SlimeVR Steam driver.")
			return
		}
		val vrPathRegContents = executeShellCommand("${steamVRLocation}\\bin\\win64\\vrpathreg.exe", "finddriver", "slimevr")
		val isDriverRegistered = vrPathRegContents.contains("WINDOWSSTEAMVRDRIVERDIRECTORY")
		if (isDriverRegistered) {
			LogManager.info("steamVR driver is already registered. Skipping...")
			return
		}
		executeShellCommand(
			"${steamVRLocation}\\bin\\win64\\vrpathreg.exe",
			"adddriver",
			"${
				Paths.get(
					"",
				).toAbsolutePath()
			}\\${WINDOWSSTEAMVRDRIVERDIRECTORY}\\slimevr",
		)
	}

	companion object {
		private const val WINDOWSSTEAMVRDRIVERDIRECTORY = "slimevr-openvr-driver-win64"
		private const val WINDOWSFEEDERDIRECTORY = "SlimeVR-Feeder-App-win64"

	}
}
