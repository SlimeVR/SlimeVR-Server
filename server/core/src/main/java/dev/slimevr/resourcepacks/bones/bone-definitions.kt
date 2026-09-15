package dev.slimevr.resourcepacks.bones

import dev.slimevr.resourcepacks.BoneInputs
import dev.slimevr.resourcepacks.BoneOutputs
import dev.slimevr.resourcepacks.Constraint
import dev.slimevr.resourcepacks.Offset
import dev.slimevr.resourcepacks.RotationFallback
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
