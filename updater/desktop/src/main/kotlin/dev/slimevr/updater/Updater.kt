package dev.slimevr.updater

import dev.slimevr.updater.ManifestUtils.Companion.getChannels
import dev.slimevr.updater.ManifestUtils.Companion.getCurrentVersion
import dev.slimevr.updater.ManifestUtils.Companion.getCurrentVersionTag
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray

class Updater(
	val state: UpdaterState,
) {

	@Serializable
	data class GHResponse(
		val tag_name: String,
		val assets: JsonArray,
	)

	@Serializable
	data class ReleaseInfo(
		val url: String,
		val id: Long,
		val name: String,
		val digest: String,
	)

	suspend fun runUpdater() {
		val manifest = Manifest().getManifest()
		val updaterIO = UpdaterIO(state)
		val os = System.getProperty("os.name").lowercase()
		val arch = System.getProperty("os.arch").lowercase()
		val normalizedArch = when {
			arch.contains("amd64") || arch.contains("x86_64") -> "x86_64"
			arch.contains("arm") -> "arm64"
			else -> arch
		}
		val currentVersion = getCurrentVersion(manifest, os, normalizedArch)
		println(currentVersion)
		if (currentVersion == null) return
		val linux = Linux(state, updaterIO)
		linux.updateServer(currentVersion.url)

		/*
		val shouldUpdate: Boolean = shouldUpdate()
		println("Should update $shouldUpdate")

		/*
		if (!shouldUpdate) {
		 		println("Updater didn't find any new version")
		 	return
		}

		 */

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
		updaterGui.mainLabel.text = "Done Installing"
		updaterGui.mainLabel.isVisible = false

		Thread.sleep(1000)
		return

		 */
	}

	companion object {
		val CDN = "http://127.0.0.1:8080"
	}
}
