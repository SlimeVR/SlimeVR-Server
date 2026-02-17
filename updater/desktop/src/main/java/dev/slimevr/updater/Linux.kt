package dev.slimevr.updater

import java.nio.file.Paths
import kotlin.io.path.Path
import kotlin.io.path.exists

class Linux {

	val path = Paths.get("").toAbsolutePath().toString()

	fun updateLinux() {
		updateLinuxSteamVRDriver()
		feeder()
		updateUdev()
		//updateServer()
	}

	fun updateFrame() {
		println("Running Updater for frame")
		updateLinuxSteamVRDriver()
	}

	//TODO: tell user in gui to add steamvr launch arguments
	fun updateLinuxSteamVRDriver() {
		mainProgressBar.string = "updating SteamVR Driver"
		subProgressBar.isVisible = true
		subProgressBar.value = 0
		val vrPathRegContents = executeShellCommand("${System.getProperty("user.home")}/.steam/steam/steamapps/common/SteamVR/bin/vrpathreg.sh")
		val isDriverRegistered = vrPathRegContents.contains("slimevr")
		if (isDriverRegistered) {
			subProgressBar.string = "Downloading SteamVR Driver"
			subProgressBar.value = 25
			downloadFile(LINUXSTEAMVRDRIVERURL, LINUXSTEAMVRDRIVERNAME)
			subProgressBar.string = "Unzipping SteamVR Driver"
			subProgressBar.value = 50
			unzip(LINUXSTEAMVRDRIVERNAME, LINUXSTEAMVRDRIVERDIRECTORY)
			println("Driver downloaded")
			println("Registering driver with steamvr")
			println("${Paths.get("").toAbsolutePath()}/$LINUXSTEAMVRDRIVERDIRECTORY/slimevr")
			executeShellCommand("${System.getProperty("user.home")}/.steam/steam/steamapps/common/SteamVR/bin/vrpathreg.sh",
				"adddriver",
				"${path}/$LINUXSTEAMVRDRIVERDIRECTORY/slimevr"
			)
		} else {
			println("steamVR driver is already registered. Skipping...")
		}
		subProgressBar.string = "SteamVR Driver done"
		subProgressBar.value = 100
		mainProgressBar.value = (100 / 3)
	}

	fun removeLinuxSteamVRDriver() {
		val vrPathRegContents = executeShellCommand("${System.getProperty("user.home")}/.steam/steam/steamapps/common/SteamVR/bin/vrpathreg.sh")
		val isDriverRegistered = vrPathRegContents.contains("slimevr")
		if (isDriverRegistered) {
			println("Removing driver from steamvr")
			executeShellCommand("${System.getProperty("user.home")}/.steam/steam/steamapps/common/SteamVR/bin/vrpathreg.sh",
				"removedriver",
				"${path}/$LINUXSTEAMVRDRIVERDIRECTORY/slimevr"
			)
		} else {
			println("steamVR driver isn't registered. Skipping...")
		}
	}

	fun feeder() {
		mainProgressBar.string = "Updating Feeder app"
		subProgressBar.value = 25
		subProgressBar.string = "Downloading Feeder App"
		println("Downloading feeder")
		downloadFile(LINUXFEEDERURL, LINUXFEEDERNAME)
		subProgressBar.value = 50
		subProgressBar.string = "Unzipping Feeder App"
		unzip(LINUXFEEDERNAME, LINUXFEEDERDIRECTORY)
		subProgressBar.value = 75
		subProgressBar.string = "Registering Feeder App"
		executeShellCommand("${path}/${LINUXFEEDERDIRECTORY}/SlimeVR-Feeder-App",  "--install")
		mainProgressBar.value = (100 / 3 * 2)
		subProgressBar.string = "Feeder app done"
		subProgressBar.value = 100
	}

	fun updateServer() {
		println("Downloading Server")
		downloadFile(LINUXSERVERURL, LINUXSERVERNAME)
	}

	//TODO: Find a way to do version checking on udev rules
	fun updateUdev() {
		mainProgressBar.string = "Setting udev"
		subProgressBar.value = 25
		subProgressBar.string = "Setting udev"
		val file = Path("/etc/udev/rules.d/69-slimevr-devices.rules")
		if (file.exists()) {
			subProgressBar.value = 100
			subProgressBar.string = "Udev rules already installed"
			return
		}
		subProgressBar.value = 75
		subProgressBar.string = "Asking for privileges"
		val res = executeShellCommand("pkexec", "cp", "${path}/69-slimevr-devices.rules", "/etc/udev/rules.d/69-slimevr-devices.rules")
		if (res.contains("Error")) {
			println("Error installing udev rules")
		}
		else {
			println("Successfully installed udev rules")
		}
		mainProgressBar.value = (100 / 3 * 3)
		subProgressBar.value = 100
		subProgressBar.string = "Udev done"
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
