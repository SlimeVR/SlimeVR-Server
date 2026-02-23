package dev.slimevr.desktop.install.drivers

import java.nio.file.Paths
import kotlin.io.path.Path
import kotlin.io.path.exists

class Linux {

	val path = Paths.get("").toAbsolutePath().toString()

	fun updateLinux() {
		updateLinuxSteamVRDriver()
		feeder()
		updateUdev()
	}

	fun updateLinuxSteamVRDriver() {
		val vrPathRegContents = executeShellCommand("${System.getProperty("user.home")}/.steam/steam/steamapps/common/SteamVR/bin/vrpathreg.sh")
		val isDriverRegistered = vrPathRegContents.contains("slimevr")
		if (!isDriverRegistered) {
			println("${Paths.get("").toAbsolutePath()}/$LINUXSTEAMVRDRIVERDIRECTORY/slimevr")
			executeShellCommand(
				"${System.getProperty("user.home")}/.steam/steam/steamapps/common/SteamVR/bin/vrpathreg.sh",
				"adddriver",
				"$path/$LINUXSTEAMVRDRIVERDIRECTORY/slimevr",
			)
		} else {
			println("steamVR driver is already registered. Skipping...")
		}
	}

	fun feeder() {
		executeShellCommand("chmod", "+x", "$path/$LINUXFEEDERDIRECTORY/SlimeVR-Feeder-App")
		executeShellCommand("$path/$LINUXFEEDERDIRECTORY/SlimeVR-Feeder-App", "--install")
		println("feeder is registered.")
	}

	fun updateUdev() {
		val file = Path("/etc/udev/rules.d/69-slimevr-devices.rules")
		if (file.exists()) {
			println("Udev rules already exist")
			return
		}
		val res = executeShellCommand("pkexec", "cp", "$path/69-slimevr-devices.rules", "/etc/udev/rules.d/69-slimevr-devices.rules")
		if (res.contains("Error")) {
			println("Error installing udev rules")
		} else {
			println("Successfully installed udev rules")
		}
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
