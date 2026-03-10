package dev.slimevr.desktop.install.drivers

import io.eiren.util.logging.LogManager

class InstallDrivers {

	val os = System.getProperty("os.name").lowercase()

	fun runInstaller() {
		if (os.contains("linux")) {
			val linuxUpdater = Linux()
			linuxUpdater.updateLinux()
		} else if (os.contains("windows")) {
			val windowsUpdater = Windows()
			windowsUpdater.updateWindows()
		} else {
			LogManager.warning("Updater doesn't support operating system '$os'")
		}
	}
}
