package dev.slimevr.desktop.install.drivers

import io.eiren.util.logging.LogManager

class Linux {

	val path: String = System.getProperty("user.dir")

	fun updateLinux() {
		updateLinuxSteamVRDriver()
		feeder()
	}

	fun updateLinuxSteamVRDriver() {
		val pathRegPath = "${System.getProperty("user.home")}/.steam/steam/steamapps/common/SteamVR/bin/vrpathreg.sh"
		val vrPathRegContents = executeShellCommand(pathRegPath)
		if (vrPathRegContents == null) {
			LogManager.warning("SteamVR driver installation failed")
			return
		}
		if (vrPathRegContents.contains("slimevr")) {
			return
		}

		executeShellCommand(pathRegPath, "adddriver", "$path/$LINUX_STEAM_DRIVER_DIRECTORY")

		if (executeShellCommand(pathRegPath)?.contains("slimevr") != true) {
			LogManager.warning("Failed to install SlimeVR driver")
			return
		}
		LogManager.info("SteamVR driver successfully installed")
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

	companion object {
		private const val LINUX_STEAM_DRIVER_DIRECTORY = "slimevr-openvr-driver-x64-linux"
		private const val LINUX_FEEDER_DIRECTORY = "SlimeVR-Feeder-App-Linux"
	}
}
