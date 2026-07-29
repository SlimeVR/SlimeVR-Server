package dev.slimevr.desktop.install.drivers

import dev.slimevr.desktop.platform.linux.SteamUtils
import io.eiren.util.logging.LogManager

class Linux {
	val path: String = System.getProperty("user.dir")

	fun updateLinux() {
		updateLinuxSteamVRDriver()
	}

	fun updateLinuxSteamVRDriver() {
		val steamVRLocation = SteamUtils.findAppLibraryLocation(250820)?.resolve("steamapps/common/SteamVR") ?: run {
			LogManager.warning("SteamVR driver installation failed: couldn't find SteamVR")
			return
		}

		val pathRegPath = "$steamVRLocation/bin/vrpathreg.sh"
		val (findExitCode, _) = executeShellCommand(pathRegPath, "finddriver", "slimevr") ?: run {
			LogManager.warning("SteamVR driver installation failed: couldn't run vrpathreg finddriver")
			return
		}

		if (!shouldInstallDriver(findExitCode)) {
			LogManager.info("Skipping SteamVR driver installation: ${getDriverInstallSkipReason(findExitCode)}")
			return
		}

		val (addExitCode, _) = executeShellCommand(pathRegPath, "adddriver", "$path/$LINUX_STEAM_DRIVER_DIRECTORY") ?: run {
			LogManager.warning("SteamVR driver installation failed: couldn't run vrpathreg adddriver")
			return
		}

		if (addExitCode != 0) {
			LogManager.warning("SteamVR driver installation failed: vrpathreg exited with code $addExitCode")
			return
		}
		LogManager.info("SteamVR driver successfully installed")
	}

	companion object {
		private const val LINUX_STEAM_DRIVER_DIRECTORY = "slimevr-openvr-driver-x64-linux"
	}
}
