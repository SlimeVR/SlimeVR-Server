package dev.slimevr.desktop.install.drivers

import io.eiren.util.logging.LogManager

class InstallDrivers {

	val os = System.getProperty("os.name").lowercase()

	fun runUpdater() {

		if (os.contains("linux")) {
			val linuxUpdater = Linux()
			val linuxFlavour = executeShellCommand("uname", "-n")
			linuxUpdater.updateLinux()
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
