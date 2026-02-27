package dev.slimevr.desktop.install.drivers

import io.eiren.util.logging.LogManager
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
			executeShellCommand(
				"${System.getProperty("user.home")}/.steam/steam/steamapps/common/SteamVR/bin/vrpathreg.sh",
				"adddriver",
				"$path/$LINUXSTEAMVRDRIVERDIRECTORY/slimevr",
			)
		} else {
			LogManager.info("steamVR driver is already registered. Skipping...")
		}
	}

	fun feeder() {
		executeShellCommand("chmod", "+x", "$path/$LINUXFEEDERDIRECTORY/SlimeVR-Feeder-App")
		executeShellCommand("$path/$LINUXFEEDERDIRECTORY/SlimeVR-Feeder-App", "--install")
		LogManager.info("feeder is registered.")
	}

	fun updateUdev() {
		val file = Path("/etc/udev/rules.d/69-slimevr-devices.rules")
		if (file.exists()) {
			LogManager.info("Udev rules already exist")
			return
		}
		val res = executeShellCommand("pkexec", "cp", "$path/69-slimevr-devices.rules", "/etc/udev/rules.d/69-slimevr-devices.rules")
		if (res.contains("Error")) {
			LogManager.warning("Error installing udev rules")
		} else {
			LogManager.info("Successfully installed udev rules")
		}
	}

	companion object {
		// Linux URLs
		private const val LINUXSTEAMVRDRIVERDIRECTORY = "slimevr-openvr-driver-x64-linux"
		private const val LINUXFEEDERDIRECTORY = "SlimeVR-Feeder-App-Linux"

	}
}
