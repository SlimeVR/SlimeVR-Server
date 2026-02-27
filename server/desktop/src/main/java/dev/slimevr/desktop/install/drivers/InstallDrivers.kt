package dev.slimevr.desktop.install.drivers

class InstallDrivers {

	val os = System.getProperty("os.name").lowercase()

	fun runUpdater() {

		if (os.contains("linux")) {
			val linuxUpdater = Linux()
			val linuxFlavour = executeShellCommand("uname", "-n")
			linuxUpdater.updateLinux()
		} else if (os.contains("windows")) {
			println("Running windows updater")
			val windowsUpdater = Windows()
			windowsUpdater.updateWindows()
		} else if (os.contains("darwin")) {
			println("I dunno")
		} else {
			println("guess I'll die")
		}
		return
	}
}
