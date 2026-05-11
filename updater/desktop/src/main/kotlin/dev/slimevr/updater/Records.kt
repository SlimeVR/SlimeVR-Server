package dev.slimevr.updater

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Release(
	val run: List<String>,
	val platform: String,
	val architecture: String,
	val url: String,
	val checksum: String,
	val version: String
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
