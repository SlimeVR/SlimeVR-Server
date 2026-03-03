package dev.slimevr.desktop.install.drivers

import io.eiren.util.logging.LogManager

class InstallDrivers {

	val os = System.getProperty("os.name").lowercase()

	fun runInstaller() {
		if (os.contains("linux")) {
			val linuxUpdater = Linux()
			val linuxFlavour = executeShellCommand("cat", "/proc/version")
			if (linuxFlavour.lowercase().contains("nix")) {
				LogManager.warning("Running on NixOS, server will not install itself.")
				return
			}
			if (linuxFlavour.lowercase().contains("steam")) {
				LogManager.info("Running on steamos, skipping installation of udev rules")
				linuxUpdater.updateSteamOS()
			} else {
				linuxUpdater.updateLinux()
			}
		} else if (os.contains("windows")) {
			LogManager.info("Running windows updater")
			val windowsUpdater = Windows()
			windowsUpdater.updateWindows()
		} else {
			println("Unsupported Operating System")
		}
		return
	}
}
