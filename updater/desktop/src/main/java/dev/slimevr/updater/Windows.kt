package dev.slimevr.updater

import java.nio.file.Paths

class Windows {

	val path = Paths.get("").toAbsolutePath().toString()

	fun updateWindows() {
		// First check if everything is already installed. Install it if it isn't
		usbDrivers()
		steamVRDriver()
		feeder()
		//Turn off updater stuff not needed for the steam release
		//slimeServer()
	}

	fun usbDrivers() {
		mainProgressBar.string = "Updating usb drivers"
		val installedDriversList =
			executeShellCommand("powershell.exe", "pnputil /enum-drivers")
		val ch341ser = installedDriversList.contains("ch341ser.inf")
		val ch343ser = installedDriversList.contains("ch343ser.inf")
		val silabser = installedDriversList.contains("silabser.inf")

		if (ch341ser && ch343ser && silabser) {
			println("drivers already installed!")
			return
		}

		println("Cannot find one of the drivers, installing drivers")
		val driverinstallOutput = executeShellCommand("$path\\installusbdrivers.bat")
		println(driverinstallOutput)
		mainProgressBar.value = (100 / 3)
	}

	fun slimeServer() {
		// downloading slime server
		downloadFile(WINDOWSSERVERURL, WINDOWSSERVERNAME)
		println("extracting")
		newAndCoolUnzip(WINDOWSSERVERNAME, WINDOWSSERVERDIRECTORY)
	}

	fun feeder() {
		mainProgressBar.string = "Updating feeder"
		println("Downloading feeder")
		downloadFile(WINDOWSFEEDERURL, WINDOWSFEEDERNAME)
		println("Unzipping feeder")
		newAndCoolUnzip(WINDOWSFEEDERNAME, WINDOWSFEEDERDIRECTORY)
		executeShellCommand("${path}\\${WINDOWSFEEDERDIRECTORY}\\SlimeVR-Feeder-App.exe",  "--install")
		mainProgressBar.value = (100 / 3 * 3)
	}

	fun steamVRDriver() {
		mainProgressBar.string = "Updating SteamVR Driver"
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
		newAndCoolUnzip(WINDOWSSTEAMVRDRIVERNAME, WINDOWSSTEAMVRDRIVERDIRECTORY)
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
		mainProgressBar.value = (100 / 3 * 2)
	}

	fun updateWindowsGui() {

	}

	companion object {
		private const val WINDOWSSTEAMVRDRIVERURL = "https://github.com/SlimeVR/SlimeVR-OpenVR-Driver/releases/latest/download/slimevr-openvr-driver-win64.zip"
		private const val WINDOWSSTEAMVRDRIVERNAME = "slimevr-openvr-driver-win64.zip"
		private const val WINDOWSSTEAMVRDRIVERDIRECTORY = "slimevr-openvr-driver-win64"
		private const val WINDOWSFEEDERURL = "https://github.com/SlimeVR/SlimeVR-Feeder-App/releases/latest/download/SlimeVR-Feeder-App-win64.zip"
		private const val WINDOWSFEEDERNAME = "SlimeVR-Feeder-App-win64.zip"
		private const val WINDOWSFEEDERDIRECTORY = "SlimeVR-Feeder-App-win64"
		private const val WINDOWSSERVERURL = "https://github.com/SlimeVR/SlimeVR-Server/releases/latest/download/SlimeVR-win64.zip"
		private const val WINDOWSSERVERNAME = "slimevr-win64.zip"
		private const val WINDOWSSERVERDIRECTORY = "slimevr-win64"
	}
}
