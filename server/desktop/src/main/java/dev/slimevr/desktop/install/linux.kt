package dev.slimevr.desktop.install

import dev.slimevr.AppLogger
import dev.slimevr.FeatureFlags
import dev.slimevr.desktop.linux.findAppLibraryLocation

private const val STEAMVR_APPID = 250820
private const val LINUX_STEAM_DRIVER_DIRECTORY = "slimevr-openvr-driver-x64-linux"

suspend fun installLinux() {
	installLinuxSteamVRDriver()
}

private suspend fun installLinuxSteamVRDriver() {
	val workingDir = System.getProperty("user.dir")
	val steamVRLocation = try {
		findAppLibraryLocation(STEAMVR_APPID).resolve("steamapps/common/SteamVR")
	} catch (e: Exception) {
		AppLogger.install.warn(e, "SteamVR driver installation failed (couldn't find SteamVR install location)")
		return
	}
	val pathRegPath = steamVRLocation.resolve("bin/vrpathreg.sh").toString()

	val (findExitCode, _) = executeShellCommand(pathRegPath, "finddriver", "slimevr") ?: run {
		AppLogger.install.warn("SteamVR driver installation failed (failed to run vrpathreg finddriver)")
		return
	}
	if (!shouldInstallDriver(findExitCode)) {
		AppLogger.install.info("Skipping SteamVR driver installation (${getDriverInstallSkipReason(findExitCode)})")
		return
	}

	val (addExitCode, _) = executeShellCommand(pathRegPath, "adddriver", "$workingDir/$LINUX_STEAM_DRIVER_DIRECTORY") ?: run {
		AppLogger.install.warn("SteamVR driver installation failed (failed to run vrpathreg adddriver)")
		return
	}
	if (addExitCode != 0) {
		AppLogger.install.warn("SteamVR driver installation failed (vrpathreg adddriver exited with code $addExitCode)")
		return
	}

	AppLogger.install.info("SteamVR driver successfully installed")
}
