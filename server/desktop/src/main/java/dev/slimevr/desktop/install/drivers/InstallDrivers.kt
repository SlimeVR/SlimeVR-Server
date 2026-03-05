package dev.slimevr.desktop.install.drivers

import io.eiren.util.logging.LogManager
import java.io.File

class InstallDrivers {

	val os = System.getProperty("os.name").lowercase()

	fun runInstaller() {
		if (os.contains("linux")) {
			val linuxUpdater = Linux()
			val linuxFlavour = try {
				File("/etc/os-release").readText()
			} catch (e: Exception) {
				LogManager.warning("Couldn't determine OS distribution: $e")
				return
			}
			if (linuxFlavour.contains("ID=nixos") || linuxFlavour.contains("ID_LIKE=nixos")) {
				LogManager.warning("Running on NixOS, server will not install itself.")
				return
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
	}
}
