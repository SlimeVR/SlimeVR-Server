package dev.slimevr.desktop.install

import dev.slimevr.AppLogger
import dev.slimevr.FeatureFlags

private const val LINUX_STEAM_DRIVER_DIRECTORY = "slimevr-openvr-driver-x64-linux"

suspend fun installLinux() {
	installLinuxSteamVRDriver()
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
