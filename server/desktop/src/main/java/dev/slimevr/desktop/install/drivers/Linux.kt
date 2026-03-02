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

	fun updateSteamOS() {
		updateLinuxSteamVRDriver()
		feeder()
	}

	fun updateLinuxSteamVRDriver() {
		val vrPathRegContents = executeShellCommand("${System.getProperty("user.home")}/.steam/steam/steamapps/common/SteamVR/bin/vrpathreg.sh")
		val isDriverRegistered = vrPathRegContents.contains("slimevr")
		if (!isDriverRegistered) {
			executeShellCommand(
				"${System.getProperty("user.home")}/.steam/steam/steamapps/common/SteamVR/bin/vrpathreg.sh",
				"adddriver",
				"$path/$LINUXSTEAMDRIVERDIRECTORY/slimevr",
			)
		} else {
			LogManager.info("steamVR driver is already registered. Skipping...")
		}
	}

	fun feeder() {
		executeShellCommand("chmod", "+x", "$path/$LINUXFEEDERDIRECTORY/SlimeVR-Feeder-App")
		val feederOutput = executeShellCommand("$path/$LINUXFEEDERDIRECTORY/SlimeVR-Feeder-App", "--install")
		LogManager.info(feederOutput)
		if (feederOutput.lowercase().contains("manifest is not installed")) {
			LogManager.warning("Could not install feeder application")
		} else {
			LogManager.info("Successfully installed feeder application")
		}
	}

	fun updateUdev() {
		val file = Path("/etc/udev/rules.d/69-slimevr-devices.rules")
		if (file.exists()) {
			LogManager.info("Udev rules already exist")
			return
		}
		val res = executeShellCommand("pkexec", "cp", "$path/69-slimevr-devices.rules", "/etc/udev/rules.d/69-slimevr-devices.rules")
		LogManager.info(res)
		if (res.contains("Error")) {
			LogManager.warning("Error installing udev rules")
		} else {
			LogManager.info("Successfully installed udev rules")
		}
	}

	companion object {
		private const val LINUXSTEAMDRIVERDIRECTORY = "slimevr-openvr-driver-x64-linux"
		private const val LINUXFEEDERDIRECTORY = "SlimeVR-Feeder-App-Linux"
	}
}
