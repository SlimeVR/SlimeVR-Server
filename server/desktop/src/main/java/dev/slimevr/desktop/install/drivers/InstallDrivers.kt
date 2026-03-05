package dev.slimevr.desktop.install.drivers

import io.eiren.util.logging.LogManager
import java.io.File

class InstallDrivers {

	val os = System.getProperty("os.name").lowercase()

	fun runInstaller() {
		if (os.contains("linux")) {
			val linuxUpdater = Linux()
			var linuxFlavour: String? = null
			try {
				linuxFlavour = File("/etc/os-release").readText().lowercase()
			} catch (e: Exception) {
				LogManager.warning("Couldn't get linux release info: $e")
				linuxFlavour = null
			}
			if (linuxFlavour == null) {
				LogManager.warning("Unable to determine OS distribution")
				return
			}
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
			LogManager.warning("Updater doesn't support operating system '$os'")
		}
		return
	}
}
