package dev.slimevr.desktop.updater

import dev.slimevr.desktop.updater.downloadFile
import dev.slimevr.desktop.updater.executeShellCommand
import dev.slimevr.desktop.updater.unzip
import java.net.URL
import java.nio.file.Paths

class Linux {

	fun updateLinux() {
		updateLinuxSteamVRDriver()
	}

	fun updateLinuxSteamVRDriver() {
		val vrPathRegContents = executeShellCommand("${System.getProperty("user.home")}/.steam/steam/steamapps/common/SteamVR/bin/vrpathreg.sh")
		val isDriverRegistered = vrPathRegContents.contains("LINUXSTEAMVRDRIVERDIRECTORY")
		if (!isDriverRegistered) {
			println("Downloading driver")
			downloadFile(LINUXSTEAMVRDRIVERURL, LINUXSTEAMVRDRIVERNAME)
			unzip(LINUXSTEAMVRDRIVERNAME, LINUXSTEAMVRDRIVERDIRECTORY)
			println("Driver downloaded")
			println("Registering driver with steamvr")
			executeShellCommand(
				"${System.getProperty("user.home")}/.steam/steam/steamapps/common/SteamVR/bin/vrpathreg.sh adddriver ${
					Paths.get(
						""
					).toAbsolutePath()
				}/${LINUXSTEAMVRDRIVERDIRECTORY}/slimevr"
			)
		} else {
			println("steamVR driver is already registered. Skipping...")
		}
	}

	fun updateLinuxServer() {

	}

	fun updateUdev() {

	}

	companion object {
		//Linux URL's
		private const val LINUXSTEAMVRDRIVERURL = "https://github.com/SlimeVR/SlimeVR-OpenVR-Driver/releases/latest/download/slimevr-openvr-driver-x64-linux.zip"
		private const val LINUXSTEAMVRDRIVERNAME = "slimevr-openvr-driver-x64-linux.zip"
		private const val LINUXSTEAMVRDRIVERDIRECTORY = "slimevr-openvr-driver-x64-linux"
		private const val LINUXFEEDERURL = "https://github.com/SlimeVR/SlimeVR-Feeder-App/releases/latest/download/SlimeVR-Feeder-App-linux64.zip"
		private const val LINUXFEEDERNAME = "SlimeVR-Feeder-App-linux64.zip"
		private const val LINUXFEEDERDIRECTORY = "SlimeVR-Feeder-App-linux64"
	}
}
