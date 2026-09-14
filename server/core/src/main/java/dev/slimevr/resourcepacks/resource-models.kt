@file:OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)

package dev.slimevr.resourcepacks

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@Serializable
data class Vector3(val x: Float, val y: Float, val z: Float)

@Serializable
data class OffsetTerm(val proportion: String, val direction: Vector3)

@Serializable
data class Offset(val base: Vector3? = null, val terms: List<OffsetTerm>? = null)

@Serializable
@JsonClassDiscriminator("type")
sealed interface RotationFallback

@Serializable
@SerialName("none")
data object NoRotationFallback : RotationFallback

@Serializable
@SerialName("copy")
data class CopyRotationFallback(val source: String) : RotationFallback

@Serializable
@SerialName("firstActive")
data class FirstActiveRotationFallback(val sources: List<String>) : RotationFallback

@Serializable
@JsonClassDiscriminator("type")
sealed interface Constraint

@Serializable
@SerialName("twistSwing")
data class TwistSwingConstraint(val twistDegrees: Float, val swingDegrees: Float, val allowedDeviationDegrees: Float? = null) : Constraint

@Serializable
@SerialName("hinge")
data class HingeConstraint(val minDegrees: Float, val maxDegrees: Float, val axis: Vector3) : Constraint

@Serializable
@SerialName("looseHinge")
data class LooseHingeConstraint(val minDegrees: Float, val maxDegrees: Float, val allowedDeviationDegrees: Float, val axis: Vector3) : Constraint

@Serializable
@JsonClassDiscriminator("type")
sealed interface ProportionDefault {
	val value: Float
}

@Serializable
@SerialName("fixed")
data class FixedProportionDefault(override val value: Float) : ProportionDefault

@Serializable
@SerialName("heightRatio")
data class HeightRatioProportionDefault(override val value: Float) : ProportionDefault

@Serializable
data class DriverOutput(val supported: Boolean)

@Serializable(with = VmcNamesSerializer::class)
data class VmcNames(val values: List<String>) {
	init {
		require(values.isNotEmpty())
	}
}

sealed interface VmcInputParent {
	data object Omitted : VmcInputParent
	data object ExplicitNull : VmcInputParent
	data class Bone(val key: String) : VmcInputParent
}

@Serializable
data class RestRotation(val axis: Vector3, val degrees: Float)

@Serializable(with = VmcOutputSerializer::class)
data class VmcOutput(
	val name: VmcNames,
	val outputParent: String? = null,
	val inputParent: VmcInputParent = VmcInputParent.Omitted,
	val restRotation: List<RestRotation>? = null,
)

@Serializable
data class VrchatOutput(val required: Boolean? = null, val emit: Map<String, EmitEntry>)

@Serializable
data class EmitEntry(val from: EmitSource, val relativeTo: String? = null, val value: List<PipelineStep>? = null)

@Serializable
enum class EmitSource {
	@SerialName("position")
	POSITION,

	@SerialName("rotation")
	ROTATION,
}

@Serializable
data class PipelineStep(
	val euler: EulerSpec? = null,
	val scale: ScalarOrVector? = null,
	val divide: ScalarOrVector? = null,
	val offset: ScalarOrVector? = null,
	val clamp: List<Float>? = null,
	val greaterThan: Float? = null,
	val lessThan: Float? = null,
)

@Serializable(with = EulerSpecSerializer::class)
data class EulerSpec(val axis: Axis? = null, val order: EulerOrder? = null, val unit: EulerUnit? = null)

@Serializable enum class Axis {
	@SerialName("x")
	X,

	@SerialName("y")
	Y,

	@SerialName("z")
	Z,
}

@Serializable enum class EulerOrder { XYZ, XZY, YXZ, YZX, ZXY, ZYX }

@Serializable enum class EulerUnit {
	@SerialName("degrees")
	DEGREES,

	@SerialName("radians")
	RADIANS,
}

@Serializable(with = ScalarOrVectorSerializer::class)
sealed interface ScalarOrVector {
	data class Scalar(val value: Float) : ScalarOrVector
	data class Vector(val value: PartialVector3) : ScalarOrVector
}

@Serializable
data class PartialVector3(val x: Float? = null, val y: Float? = null, val z: Float? = null)

@Serializable
data class BoneOutputs(val driver: DriverOutput? = null, val vmc: VmcOutput? = null, val vrchat: VrchatOutput? = null)

@Serializable
data class VrchatInput(val address: String)

@Serializable
data class BoneInputs(val vrchat: VrchatInput? = null)

@Serializable
data class BoneOverrideSet(
	val nameKey: String? = null,
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
data class ProportionOverrideSet(
	val nameKey: String? = null,
	val descriptionKey: String? = null,
	val contributesToHeight: Boolean? = null,
	val minimum: Float? = null,
	val maximum: Float? = null,
	val default: ProportionDefault? = null,
)
