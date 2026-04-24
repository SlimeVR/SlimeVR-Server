package dev.slimevr.updater

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

class Manifest {
	private val json: Json = Json { ignoreUnknownKeys = true }
	private val manifest: ManifestObject

	init {
		val text = loadManifestText("update-manifest.json")
		manifest = Json.decodeFromString<ManifestObject>(text)
		val json = Json {
			ignoreUnknownKeys = true
		}
	}

	fun getManifest(): ManifestObject = manifest

	private fun loadManifestText(fileName: String): String {
		val externalFile = File(fileName)
		if (externalFile.exists()) {
			return externalFile.readText()
		}

		val resourceStream = object {}.javaClass.getResourceAsStream("/$fileName")
		if (resourceStream != null) {
			return resourceStream.bufferedReader().use { it.readText() }
		}
		error("Could not find $fileName in filesystem or JAR resources")
	}
}

@Serializable
data class Release(
	val url: String,
	val run: List<String>,
)

@Serializable
data class Versions(
	@SerialName("release_notes")
	val releaseNotes: String,
	val builds: Map<String, Map<String, Release>>,
)

@Serializable
data class Channel(
	val description: String,
	@SerialName("current_version")
	val currentVersion: String,
	val versions: Map<String, Versions>,
)

@Serializable
data class ManifestObject(
	@SerialName("default_channel")
	val defaultChannel: String,
	val channels: Map<String, Channel>,
)

class ChannelDisplayObject(
	val channelName: String,
	val Description: String,
)

class VersionDisplayObject(
	val version: String,
	val Description: String,
	val Url: String,
)
