package dev.slimevr.resourcepacks.proportions

import dev.slimevr.resourcepacks.ProportionDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
