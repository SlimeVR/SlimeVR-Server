package dev.slimevr.updater

import java.nio.file.Paths

class Linux {

	val path = Paths.get("").toAbsolutePath().toString()

	fun updateLinux() {
		updateLinuxSteamVRDriver()
		//feeder()
		updateUdev()
		//updateServer()
	}

	//TODO: tell user in gui to add steamvr launch arguments
	fun updateLinuxSteamVRDriver() {
		val vrPathRegContents = executeShellCommand("${System.getProperty("user.home")}/.steam/steam/steamapps/common/SteamVR/bin/vrpathreg.sh")
		val isDriverRegistered = vrPathRegContents.contains("slimevr")
		if (!isDriverRegistered) {
			println("Downloading driver")
			downloadFile(LINUXSTEAMVRDRIVERURL, LINUXSTEAMVRDRIVERNAME)
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
		println("Downloading feeder")
		downloadFile(LINUXFEEDERURL, LINUXFEEDERNAME)
		println("Unzipping feeder")
		unzip(LINUXFEEDERNAME, LINUXFEEDERDIRECTORY)
		executeShellCommand("${path}/${LINUXFEEDERDIRECTORY}/SlimeVR-Feeder-App",  "--install")
	}

	fun updateServer() {
		println("Downloading Server")
		downloadFile(LINUXSERVERURL, LINUXSERVERNAME)
	}

	//TODO: Probably best to just ask the user in the ui to update udev if i can't access usb devices. Also does the steam frame have udev?
	fun updateUdev() {
		val res = executeShellCommand("pkexec", "cp", "${path}/69-slimevr-devices.rules", "/etc/udev/rules.d/69-slimevr-devices.rules")
		if (res.contains("Error")) {
			println("Error installing udev rules")
		}
		else {
			println("Successfully installed udev rules")
		}
	}

	companion object {
		// Linux URL's
		private const val LINUXSTEAMVRDRIVERURL = "https://github.com/SlimeVR/SlimeVR-OpenVR-Driver/releases/latest/download/slimevr-openvr-driver-x64-linux.zip"
		private const val LINUXSTEAMVRDRIVERNAME = "slimevr-openvr-driver-x64-linux.zip"
		private const val LINUXSTEAMVRDRIVERDIRECTORY = "slimevr-openvr-driver-x64-linux"
		private const val LINUXFEEDERURL = "https://github.com/SlimeVR/SlimeVR-Feeder-App/releases/latest/download/SlimeVR-Feeder-App-linux.zip"
		private const val LINUXFEEDERNAME = "SlimeVR-Feeder-App-Linux.zip"
		private const val LINUXFEEDERDIRECTORY = "SlimeVR-Feeder-App-Linux"
		private const val LINUXSERVERURL = "https://github.com/SlimeVR/SlimeVR-Server/releases/latest/download/SlimeVR-amd64.appimage"
		private const val LINUXSERVERNAME = "SlimeVR-amd64.appimage"
		private const val LINUXSERVERDIRECTORY = "slimevr-linux"
	}
}
