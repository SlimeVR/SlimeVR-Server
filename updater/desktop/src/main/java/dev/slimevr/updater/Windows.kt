package dev.slimevr.updater

import java.nio.file.Paths

class Windows {

	fun updateWindows() {
		// First check if everything is already installed. Install it if it isn't
		usbDrivers()
		slimeServer()
		steamVRDriver()
	}

	fun usbDrivers() {
		val installedDriversList =
			executeShellCommand("powershell.exe", "pnputil /enum-drivers")
		val ch341ser = installedDriversList.contains("ch341ser.inf")
		val ch343ser = installedDriversList.contains("ch343ser.inf")
		val silabser = installedDriversList.contains("silabser.inf")
		val path = Paths.get("").toAbsolutePath().toString()

		if (ch341ser && ch343ser && silabser) {
			println("drivers already installed!")
			return
		}

		println("Cannot find one of the drivers, installing drivers")
		val driverinstallOutput = executeShellCommand("$path\\installusbdrivers.bat")
		println(driverinstallOutput)
	}

	fun slimeServer() {
		// downloading slime server
		downloadFile(WINDOWSSERVERURL, WINDOWSSERVERNAME)
		println("extracting")
		unzip(WINDOWSSERVERNAME, WINDOWSSERERDIRECTORY)
	}

	fun steamVRDriver() {
		val steamVRLocation = executeShellCommand("powershell.exe", "-Command", "(Get-ItemProperty \'HKLM:\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\Steam App 250820\').InstallLocation").trim()
		if (!steamVRLocation.contains("SteamVR")) {
			println("SteamVR not installed, cannot install SlimeVR Steam driver.")
			return
		}
		val vrPathRegContents = executeShellCommand("${steamVRLocation}\\bin\\win64\\vrpathreg.exe", "finddriver", "slimevr")
		println(vrPathRegContents)
		val isDriverRegistered = vrPathRegContents.contains("WINDOWSSTEAMVRDRIVERDIRECTORY")
		println(isDriverRegistered)
		if (isDriverRegistered) {
			println("steamVR driver is already registered. Skipping...")
			return
		}
		println("Installing SteamVR Driver")
		println("Downloading SteamVR driver")
		downloadFile(WINDOWSSTEAMVRDRIVERURL, WINDOWSSTEAMVRDRIVERNAME)
		unzip(WINDOWSSTEAMVRDRIVERNAME, WINDOWSSTEAMVRDRIVERDIRECTORY)
		println("Driver downloaded")
		println("Registering driver with steamvr")
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

	fun updateWindowsGui() {
	}

	companion object {
		private const val WINDOWSSTEAMVRDRIVERURL = "https://github.com/SlimeVR/SlimeVR-OpenVR-Driver/releases/latest/download/slimevr-openvr-driver-win64.zip"
		private const val WINDOWSSTEAMVRDRIVERNAME = "slimevr-openvr-driver-win64.zip"
		private const val WINDOWSSTEAMVRDRIVERDIRECTORY = "slimevr-openvr-driver-win64"
		private const val WINDOWSFEEDERURL = "https://github.com/SlimeVR/SlimeVR-Feeder-App/releases/latest/download/SlimeVR-Feeder-App-win64.zip"
		private const val WINDOWSFEEDERNAME = "SlimeVR-Feeder-App-win64.zip"
		private const val WINDOWSSERVERURL = "https://github.com/SlimeVR/SlimeVR-Server/releases/latest/download/SlimeVR-win64.zip"
		private const val WINDOWSSERVERNAME = "slimevr-win64.zip"
		private const val WINDOWSSERERDIRECTORY = "slimevr-win64"
	}
}
