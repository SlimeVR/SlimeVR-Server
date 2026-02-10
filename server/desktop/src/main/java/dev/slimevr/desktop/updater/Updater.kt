package dev.slimevr.desktop.updater

import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


class Updater {

	@Serializable
	data class GHResponse(val tag_name: String)

	val os = System.getProperty("os.name").lowercase()

	suspend fun runUpdater() {

		val shouldUpdate: Boolean = shouldUpdate()
		println(shouldUpdate)

		if (!shouldUpdate) {
			return
		}

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



	suspend fun shouldUpdate(): Boolean {
		//We're running from a git branch don't update
		if (VERSION.contains("isDirty")) {
			return false
		}
		val client = HttpClient(CIO) {
			install(ContentNegotiation) {
				json(Json {
					ignoreUnknownKeys = true
				})
			}
		}
		try {
			val response: GHResponse = client.get("https://api.github.com/repos/slimevr/slimevr-server/releases/latest").body()
			client.close()
			//Replace this if versioning ever changes
			val githubVersionArr = response.tag_name.replace("v", "").split(".")
			val localVersionArr = VERSION.replace("v", "").split(".")
			//Cursed?
			return if (githubVersionArr[0] > localVersionArr[0]) {
				true
			} else if (githubVersionArr[0] < localVersionArr[0] && githubVersionArr[1] > localVersionArr[1]) {
				true
			} else if (githubVersionArr[0] < localVersionArr[0] && githubVersionArr[1] < localVersionArr[1] && githubVersionArr[2] > localVersionArr[2]) {
				true
			} else {
				false
			}
		} catch(e: Exception) {
			println("Error getting github release info: ${e.message}")
		}
		return false
	}
}
