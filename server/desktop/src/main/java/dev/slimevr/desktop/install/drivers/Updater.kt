package dev.slimevr.desktop.install.drivers

import io.eiren.util.logging.LogManager


class Updater {

	val os = System.getProperty("os.name").lowercase()

	fun runUpdater() {
		if (os.contains("linux")) {
			LogManager.info("Running linux updater")
			val linuxUpdater = Linux()
			val linuxFlavour = executeShellCommand("uname", "-n")
			linuxUpdater.updateLinux()
		} else if (os.contains("windows")) {
			LogManager.info("Running windows updater")
			val windowsUpdater = Windows()
			windowsUpdater.updateWindows()
		} else if (os.contains("darwin")) {
			LogManager.info("I dunno")
		} else {
			LogManager.info("guess I'll die")
		}
	}
}
