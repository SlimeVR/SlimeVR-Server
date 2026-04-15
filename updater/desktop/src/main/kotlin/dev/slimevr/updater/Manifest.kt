package dev.slimevr.updater

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

class Manifest {
	private val json: Json = Json { ignoreUnknownKeys = true }
	private val manifest: ManifestObject

	init {
		val file = File("update-manifest.json").readText()
		manifest = Json.decodeFromString<ManifestObject>(file)
		val json = Json {
			ignoreUnknownKeys = true
		}
	}

	fun getManifest(): ManifestObject {
		return manifest
	}
}

@Serializable
data class Release(
	val url: String,
	val run: List<String>
)

@Serializable
data class Versions(
	@SerialName("release_notes")
	val releaseNotes: String,
	val builds: Map<String, Map<String, Release>>
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

class ChannelDisplayObject(
	val channelName: String,
	val Description: String
)

class VersionDisplayObject(
	val version: String,
	val Description: String,
	val Url: String
)
