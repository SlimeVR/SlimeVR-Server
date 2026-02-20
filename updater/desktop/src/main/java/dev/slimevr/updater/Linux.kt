package dev.slimevr.updater

import java.nio.file.Paths
import kotlin.io.path.Path
import kotlin.io.path.exists

class Linux {

	val sendMainProgress: (Int) -> Unit = { progress -> updaterGui.mainProgressBar.setProgress(progress) }
	val path = Paths.get("").toAbsolutePath().toString()

	fun updateLinux() {
		updateLinuxSteamVRDriver()
		updateUdev()
		feeder()
	}

	fun updateFrame() {
		updateLinuxSteamVRDriver()
	}

	// TODO: tell user in gui to add steamvr launch arguments
	fun updateLinuxSteamVRDriver() {
		updaterGui.subLabel.text = "Updating SteamVR Driver"
		val vrPathRegContents = executeShellCommand("${System.getProperty("user.home")}/.steam/steam/steamapps/common/SteamVR/bin/vrpathreg.sh")
		val isDriverRegistered = vrPathRegContents.contains("slimevr")
		if (isDriverRegistered) {
			updaterGui.subLabel.text = "Downloading SteamVR Driver"
			downloadFile(LINUXSTEAMVRDRIVERURL, LINUXSTEAMVRDRIVERNAME)
			updaterGui.subLabel.text = "Unzipping SteamVR Driver"
			unzip(LINUXSTEAMVRDRIVERNAME, LINUXSTEAMVRDRIVERDIRECTORY)
			println("${Paths.get("").toAbsolutePath()}/$LINUXSTEAMVRDRIVERDIRECTORY/slimevr")
			executeShellCommand(
				"${System.getProperty("user.home")}/.steam/steam/steamapps/common/SteamVR/bin/vrpathreg.sh",
				"adddriver",
				"$path/$LINUXSTEAMVRDRIVERDIRECTORY/slimevr",
			)
		} else {
			println("steamVR driver is already registered. Skipping...")
		}
		updaterGui.subLabel.text = "SteamVR Driver done"
		sendMainProgress(33)
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
		updaterGui.subLabel.text = "Downloading Feeder App"
		downloadFile(LINUXFEEDERURL, LINUXFEEDERNAME)
		updaterGui.subLabel.text = "Unzipping Feeder App"
		unzip(LINUXFEEDERNAME, LINUXFEEDERDIRECTORY)
		updaterGui.subLabel.text = "Registering Feeder App"
		executeShellCommand("chmod", "+x", "$path/$LINUXFEEDERDIRECTORY/SlimeVR-Feeder-App")
		executeShellCommand("$path/$LINUXFEEDERDIRECTORY/SlimeVR-Feeder-App", "--install")
		sendMainProgress(100)
		updaterGui.subLabel.text = "Feeder App Done"
	}

	// TODO: Find a way to do version checking on udev rules
	fun updateUdev() {
		updaterGui.subLabel.text = "Setting udev"
		val file = Path("/etc/udev/rules.d/69-slimevr-devices.rules")
		if (file.exists()) {
			updaterGui.subLabel.text = "Udev rules already installed"
			return
		}
		updaterGui.subLabel.text = "Asking for privileges"
		val res = executeShellCommand("pkexec", "cp", "$path/69-slimevr-devices.rules", "/etc/udev/rules.d/69-slimevr-devices.rules")
		if (res.contains("Error")) {
			println("Error installing udev rules")
		} else {
			println("Successfully installed udev rules")
		}
		sendMainProgress(66)
		updaterGui.subLabel.text = "Udev done"
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
