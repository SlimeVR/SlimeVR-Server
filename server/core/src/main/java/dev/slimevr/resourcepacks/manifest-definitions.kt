package dev.slimevr.resourcepacks

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PackManifest(
	@SerialName("$" + "schema") val schema: String? = null,
	val formatVersion: Int,
	val id: String,
	val nameKey: String,
	val descriptionKey: String,
	val version: List<Int>,
	val authors: List<String>,
)
