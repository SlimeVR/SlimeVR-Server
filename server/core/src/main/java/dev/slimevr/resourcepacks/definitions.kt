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

@Serializable
data class BoneDefinition(
	@SerialName("$" + "schema") val schema: String? = null,
	val key: String,
	val nameKey: String,
	val mirror: String? = null,
	val batterySources: List<String>? = null,
	val candidateSources: List<String>? = null,
	val overridable: Boolean? = null,
	val parent: String? = null,
	val headOffset: Offset? = null,
	val tailOffset: Offset? = null,
	val rotationFallback: RotationFallback? = null,
	val constraint: Constraint? = null,
	val outputs: BoneOutputs? = null,
	val inputs: BoneInputs? = null,
)

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
data class BoneOverride(
	@SerialName("$" + "schema") val schema: String? = null,
	val target: String,
	val set: BoneOverrideSet? = null,
	val remove: List<List<String>>? = null,
)

@Serializable
data class ProportionOverride(
	@SerialName("$" + "schema") val schema: String? = null,
	val target: String,
	val set: ProportionOverrideSet? = null,
	val remove: List<String>? = null,
)
