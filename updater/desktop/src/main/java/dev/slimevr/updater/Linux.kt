package dev.slimevr.updater

import java.nio.file.Paths
import kotlin.io.path.Path
import kotlin.io.path.exists

class Linux {

	val path = Paths.get("").toAbsolutePath().toString()

	var steamVRDriverSuccess = false
	var udevSuccess = false
	var feederSuccess = false

	fun updateLinux() {
		removeLinuxSteamVRDriver()
		updateLinuxSteamVRDriver()
		updateUdev()
		feeder()
	}

	fun updateFrame() {
		updateLinuxSteamVRDriver()
	}

	// TODO: tell user in gui to add steamvr launch arguments
	fun updateLinuxSteamVRDriver() {
		mainProgressBar.string = "updating SteamVR Driver"
		subProgressBar.isVisible = true
		val vrPathRegContents = executeShellCommand("${System.getProperty("user.home")}/.steam/steam/steamapps/common/SteamVR/bin/vrpathreg.sh")
		val isDriverRegistered = vrPathRegContents.contains("slimevr")
		if (!isDriverRegistered) {
			subProgressBar.string = "Downloading SteamVR Driver"
			downloadFile(LINUXSTEAMVRDRIVERURL, LINUXSTEAMVRDRIVERNAME)
			subProgressBar.string = "Unzipping SteamVR Driver"
			unzip(LINUXSTEAMVRDRIVERNAME, LINUXSTEAMVRDRIVERDIRECTORY)
			println("Driver downloaded")
			println("Registering driver with steamvr")
			println("${Paths.get("").toAbsolutePath()}/$LINUXSTEAMVRDRIVERDIRECTORY/slimevr")
			executeShellCommand(
				"${System.getProperty("user.home")}/.steam/steam/steamapps/common/SteamVR/bin/vrpathreg.sh",
				"adddriver",
				"$path/$LINUXSTEAMVRDRIVERDIRECTORY/slimevr",
			)
		} else {
			println("steamVR driver is already registered. Skipping...")
		}
		subProgressBar.string = "SteamVR Driver done"
		mainProgressBar.value = (100 / 3)
		steamVRDriverSuccess = true
	}

	fun removeLinuxSteamVRDriver() {
		val vrPathRegContents = executeShellCommand("${System.getProperty("user.home")}/.steam/steam/steamapps/common/SteamVR/bin/vrpathreg.sh")
		val isDriverRegistered = vrPathRegContents.contains("slimevr")
		if (isDriverRegistered) {
			println("Removing driver from steamvr")
			executeShellCommand(
				"${System.getProperty("user.home")}/.steam/steam/steamapps/common/SteamVR/bin/vrpathreg.sh",
				"removedriver",
				"$path/$LINUXSTEAMVRDRIVERDIRECTORY/slimevr",
			)
		} else {
			println("steamVR driver isn't registered. Skipping...")
		}
	}

	fun feeder() {
		mainProgressBar.string = "Updating Feeder app"
		subProgressBar.string = "Downloading Feeder App"
		subProgressBar.isVisible = true
		downloadFile(LINUXFEEDERURL, LINUXFEEDERNAME)
		subProgressBar.string = "Unzipping Feeder App"
		unzip(LINUXFEEDERNAME, LINUXFEEDERDIRECTORY)
		subProgressBar.string = "Registering Feeder App"
		println(executeShellCommand("chmod", "+x", "$path/$LINUXFEEDERDIRECTORY/$LINUXFEEDERDIRECTORY/SlimeVR-Feeder-App"))
		println(executeShellCommand("$path/$LINUXFEEDERDIRECTORY/$LINUXFEEDERDIRECTORY/SlimeVR-Feeder-App", "--install"))
		mainProgressBar.value = (100 / 3 * 3)
		subProgressBar.string = "Feeder app done"
		feederSuccess = true
	}

	// TODO: Find a way to do version checking on udev rules
	fun updateUdev() {
		mainProgressBar.string = "Setting udev"
		val file = Path("/etc/udev/rules.d/69-slimevr-devices.rules")
		if (file.exists()) {
			subProgressBar.value = 100
			subProgressBar.string = "Udev rules already installed"
			return
		}
		subProgressBar.string = "Asking for privileges"
		val res = executeShellCommand("pkexec", "cp", "$path/69-slimevr-devices.rules", "/etc/udev/rules.d/69-slimevr-devices.rules")
		if (res.contains("Error")) {
			println("Error installing udev rules")
		} else {
			println("Successfully installed udev rules")
		}
		mainProgressBar.value = (100 / 3 * 2)
		subProgressBar.value = 100
		subProgressBar.string = "Udev done"
		udevSuccess = true
	}

	companion object {
		// Linux URLs
		private const val LINUXSTEAMVRDRIVERURL = "https://github.com/SlimeVR/SlimeVR-OpenVR-Driver/releases/latest/download/slimevr-openvr-driver-x64-linux.zip"
		private const val LINUXSTEAMVRDRIVERNAME = "slimevr-openvr-driver-x64-linux.zip"
		private const val LINUXSTEAMVRDRIVERDIRECTORY = "slimevr-openvr-driver-x64-linux"
		private const val LINUXFEEDERURL = "https://github.com/SlimeVR/SlimeVR-Feeder-App/releases/latest/download/SlimeVR-Feeder-App-Linux.zip"
		private const val LINUXFEEDERNAME = "SlimeVR-Feeder-App-Linux.zip"
		private const val LINUXFEEDERDIRECTORY = "SlimeVR-Feeder-App-Linux"
		private const val LINUXSERVERURL = "https://github.com/SlimeVR/SlimeVR-Server/releases/latest/download/SlimeVR-amd64.appimage"
		private const val LINUXSERVERNAME = "SlimeVR-amd64.appimage"
		private const val LINUXSERVERDIRECTORY = "slimevr-linux"
	}
}
