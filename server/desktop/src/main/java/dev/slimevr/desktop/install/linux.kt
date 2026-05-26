package dev.slimevr.desktop.install

import dev.slimevr.AppLogger
import dev.slimevr.FeatureFlags

private const val LINUX_STEAM_DRIVER_DIRECTORY = "slimevr-openvr-driver-x64-linux"
private const val LINUX_FEEDER_DIRECTORY = "SlimeVR-Feeder-App-Linux"

suspend fun installLinux(featureFlags: FeatureFlags) {
	installLinuxSteamVRDriver()
	installLinuxFeeder(featureFlags)
}

private suspend fun installLinuxSteamVRDriver() {
	val path = System.getProperty("user.dir")
	val vrPathReg = "${System.getProperty("user.home")}/.steam/steam/steamapps/common/SteamVR/bin/vrpathreg.sh"
	val existing = executeShellCommand(vrPathReg)
	if (existing == null) {
		AppLogger.install.warn("SteamVR driver installation failed")
		return
	}
	if (existing.contains("slimevr")) {
		AppLogger.install.info("SteamVR driver is already installed")
		return
	}
	executeShellCommand(vrPathReg, "adddriver", "$path/$LINUX_STEAM_DRIVER_DIRECTORY")
	if (executeShellCommand(vrPathReg)?.contains("slimevr") != true) {
		AppLogger.install.warn("Failed to install SteamVR driver")
		return
	}
	AppLogger.install.info("SteamVR driver successfully installed")
}

private suspend fun installLinuxFeeder(featureFlags: FeatureFlags) {
	val path = System.getProperty("user.dir")
	executeShellCommand("chmod", "+x", "$path/$LINUX_FEEDER_DIRECTORY/SlimeVR-Feeder-App")
	val command = if (featureFlags.steam) {
		arrayOf("steam-runtime-launch-client", "--alongside-steam", "--", "$path/$LINUX_FEEDER_DIRECTORY/SlimeVR-Feeder-App", "--install")
	} else {
		arrayOf("$path/$LINUX_FEEDER_DIRECTORY/SlimeVR-Feeder-App", "--install")
	}
	val output = executeShellCommand(*command)
	if (output == null) {
		AppLogger.install.warn("Error installing feeder")
		return
	}
	if (output.lowercase().contains("manifest is not installed")) {
		AppLogger.install.warn("Could not install feeder application")
	} else {
		AppLogger.install.info("Successfully installed feeder application")
	}
}
