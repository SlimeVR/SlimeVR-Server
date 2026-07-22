package dev.slimevr.desktop.install.drivers

import dev.slimevr.desktop.platform.linux.SteamUtils
import io.eiren.util.logging.LogManager

class Linux {
	val path: String = System.getProperty("user.dir")
	val isFrame = System.getenv("SLIME_SERVER_IS_FRAME")?.toInt()

	fun updateLinux() {
		updateLinuxSteamVRDriver()
	}

	fun updateLinuxSteamVRDriver() {

		val pathRegPath =
			if (isFrame == 1) {
				"/opt/steamvr/bin/linuxarm64/vrpathreg"
			} else {
				val steamVRLocation = SteamUtils.findAppLibraryLocation(250820)?.resolve("steamapps/common/SteamVR")
				if (steamVRLocation != null) {
					LogManager.warning("SteamVR driver installation failed: couldn't find SteamVR")
					null
				} else {
					"${steamVRLocation}/bin/vrpathreg.sh"
				}
			}

		if (pathRegPath == null) return


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
		private const val LINUX_STEAM_DRIVER_DIRECTORY = "slimevr-openvr-driver"
	}
}
