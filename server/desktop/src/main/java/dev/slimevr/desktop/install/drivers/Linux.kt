package dev.slimevr.desktop.install.drivers

import io.eiren.util.logging.LogManager
import kotlin.io.path.Path
import kotlin.io.path.exists

class Linux {

	val path: String? = System.getProperty("user.dir")

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
		var vrPathRegContents = executeShellCommand("${System.getProperty("user.home")}/.steam/steam/steamapps/common/SteamVR/bin/vrpathreg.sh")
		if (vrPathRegContents == null) {
			LogManager.warning("Error installing SteamVR driver.")
			return
		}
		var isDriverRegistered = vrPathRegContents.contains("slimevr")
		if (isDriverRegistered) {
			LogManager.info("steamVR driver is already registered. Skipping...")
			return
		}
		executeShellCommand(
			"${System.getProperty("user.home")}/.steam/steam/steamapps/common/SteamVR/bin/vrpathreg.sh",
			"adddriver",
			"$path/${LINUX_STEAM_DRIVER_DIRECTORY}",
		)

		vrPathRegContents = executeShellCommand("${System.getProperty("user.home")}/.steam/steam/steamapps/common/SteamVR/bin/vrpathreg.sh")
		if (vrPathRegContents == null) {
			LogManager.warning("Error installing SteamVR driver.")
			return
		}
		isDriverRegistered = vrPathRegContents.contains("slimevr")
		if (!isDriverRegistered) {
			LogManager.warning("Server couldn't install SlimeVR driver.")
			return
		}
		LogManager.info("SteamVR driver successfully installed.")
	}

	fun feeder() {
		executeShellCommand("chmod", "+x", "$path/$LINUX_FEEDER_DIRECTORY/SlimeVR-Feeder-App")
		val feederOutput = executeShellCommand("$path/$LINUX_FEEDER_DIRECTORY/SlimeVR-Feeder-App", "--install")
		if (feederOutput == null) {
			LogManager.warning("Error installing feeder")
			return
		}
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
		if (res == null) {
			LogManager.warning("Error during udev step")
			return
		}
		if (res.contains("Error")) {
			LogManager.warning("Error installing udev rules")
		} else {
			LogManager.info("Successfully installed udev rules")
		}
	}

	companion object {
		private const val LINUX_STEAM_DRIVER_DIRECTORY = "slimevr-openvr-driver-x64-linux"
		private const val LINUX_FEEDER_DIRECTORY = "SlimeVR-Feeder-App-Linux"
	}
}
