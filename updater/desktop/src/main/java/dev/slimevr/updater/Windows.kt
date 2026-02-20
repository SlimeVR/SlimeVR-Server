package dev.slimevr.updater

import java.nio.file.Paths

class Windows {

	val sendMainProgress: (Int) -> Unit = { progress -> updaterGui.mainProgressBar.setProgress(progress) }
	val path = Paths.get("").toAbsolutePath().toString()

	suspend fun updateWindows() {
		// First check if everything is already installed. Install it if it isn't
		usbDrivers()
		steamVRDriver()
		feeder()
	}

	fun usbDrivers() {
		//mainProgressBar.string = "Updating usb drivers"
		updaterGui.subLabel.text = "Checking Windows Drivers"
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
		sendMainProgress(33)
	}


	suspend fun feeder() {
		updaterGui.subLabel.text = "Downloading Feeder App"
		downloadFile(WINDOWSFEEDERURL, WINDOWSFEEDERNAME)
		updaterGui.subLabel.text = "Unzipping Feeder App"
		unzip(WINDOWSFEEDERNAME, WINDOWSFEEDERDIRECTORY)
		updaterGui.subLabel.text = "Registering Feeder App"
		executeShellCommand("${path}\\${WINDOWSFEEDERDIRECTORY}\\SlimeVR-Feeder-App.exe", "--install")
		sendMainProgress(100)
		updaterGui.subLabel.text = "Feeder App Done"
	}

	suspend fun steamVRDriver() {
		updaterGui.subLabel.text = "Updating SteamVR Driver"
		val steamVRLocation = executeShellCommand("powershell.exe", "-Command", "(Get-ItemProperty \'HKLM:\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\Steam App 250820\').InstallLocation").trim()
		if (!steamVRLocation.contains("SteamVR")) {
			println("SteamVR not installed, cannot install SlimeVR Steam driver.")
			return
		}
		val vrPathRegContents = executeShellCommand("${steamVRLocation}\\bin\\win64\\vrpathreg.exe", "finddriver", "slimevr")
		val isDriverRegistered = vrPathRegContents.contains("WINDOWSSTEAMVRDRIVERDIRECTORY")
		if (isDriverRegistered) {
			println("steamVR driver is already registered. Skipping...")
			return
		}
		updaterGui.subLabel.text = "Downloading SteamVR Driver"
		downloadFile(WINDOWSSTEAMVRDRIVERURL, WINDOWSSTEAMVRDRIVERNAME)
		updaterGui.subLabel.text = "Unzipping SteamVR Driver"
		unzip(WINDOWSSTEAMVRDRIVERNAME, WINDOWSSTEAMVRDRIVERDIRECTORY)
		executeShellCommand(
			"${steamVRLocation}\\bin\\win64\\vrpathreg.exe",
			"adddriver",
			"${
				Paths.get(
					"",
				).toAbsolutePath()
			}\\${WINDOWSSTEAMVRDRIVERDIRECTORY}\\slimevr",
		)
		updaterGui.subLabel.text = "SteamVR Driver done"
		sendMainProgress(66)
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
