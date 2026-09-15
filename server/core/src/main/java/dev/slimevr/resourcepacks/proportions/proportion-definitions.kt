package dev.slimevr.resourcepacks.proportions

import dev.slimevr.resourcepacks.ProportionDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class ProportionDefinition(
	@SerialName("$" + "schema") val schema: String? = null,
	val key: String,
	val nameKey: String,
	val descriptionKey: String? = null,
	val contributesToHeight: Boolean? = null,
	val minimum: Float? = null,
	val maximum: Float? = null,
	val default: ProportionDefault,
)

@Serializable
data class ProportionOverride(
	@SerialName("$" + "schema") val schema: String? = null,
	val target: String,
	val set: JsonObject? = null,
	val remove: List<String>? = null,
)
