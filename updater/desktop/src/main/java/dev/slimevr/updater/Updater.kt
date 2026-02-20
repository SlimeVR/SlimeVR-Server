package dev.slimevr.updater

import kotlinx.serialization.Serializable

class Updater {

	@Serializable
	data class GHResponse(
		val tag_name: String,
	)

	val os = System.getProperty("os.name").lowercase()

	suspend fun runUpdater() {
		val shouldUpdate: Boolean = shouldUpdate()
		println("Should update $shouldUpdate")

		// if (!shouldUpdate) {
		// 		println("Updater didn't find any new version")
		// 	return
		// }

		if (os.contains("linux")) {
			val linuxUpdater = Linux()
			val linuxFlavour = executeShellCommand("uname", "-n")

			// Skip some install stuff if running on frame
			// if (linuxFlavour.contains("amos")) {
			// 	linuxUpdater.updateFrame()
			// }
			// else {
			linuxUpdater.updateLinux()
			// }
		} else if (os.contains("windows")) {
			println("Running windows updater")
			val windowsUpdater = Windows()
			windowsUpdater.updateWindows()
		} else if (os.contains("darwin")) {
			println("I dunno")
		} else {
			println("guess I'll die")
		}
		println("Done Updating")
	}
}
