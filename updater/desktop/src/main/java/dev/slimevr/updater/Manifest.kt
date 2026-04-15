package dev.slimevr.updater

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

class Manifest {
	val manifestObj: ManifestObject

	init {
		val manifest = File("update-manifest.json").readText()
		manifestObj = Json.decodeFromString<ManifestObject>(manifest)

		getAllVersions()
		println(manifestObj)
	}


	fun getAllVersions() {
		manifestObj.channels.
	}

	fun getChannels() {

	}
}

@Serializable
data class Arch(
	val url: String,
	val run: List<String>
)

@Serializable
data class Versions(
	@SerialName("release_notes")
	val releaseNotes: String,
	val builds: Map<String, Map<String, Arch>>
)

@Serializable
data class Channel(
	val description: String,
	@SerialName("current_version")
	val currentVersion: String,
	val versions: Map<String, Versions>
)

@Serializable
data class ManifestObject(
	@SerialName("default_channel")
	val defaultChannel: String,
	val channels: Map<String, Channel>
)
