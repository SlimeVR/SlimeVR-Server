package dev.slimevr.updater

import kotlinx.serialization.Serializable


class Updater {

	@Serializable
	data class GHResponse(val tag_name: String)

	//val os = System.getProperty("os.name").lowercase()
	val os = "windows"

	suspend fun runUpdater() {

		val shouldUpdate: Boolean = shouldUpdate()
		println("Should update $shouldUpdate")

		//if (!shouldUpdate) {
		//	return
		//}

		if (os.contains("linux")) {
			println("Running linux updater")
			val linuxUpdater = Linux()
			linuxUpdater.updateLinux()
		}
		else if (os.contains("windows")) {
			println("Running windows updater")
			val windowsUpdater = Windows()
			windowsUpdater.updateWindows()

		}
		else if (os.contains("darwin")) {
			println("I dunno")
		}
		else {
			println("guess I'll die")
		}
		println("Done Updating")
	}

}
