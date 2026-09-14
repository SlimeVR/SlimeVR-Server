package dev.slimevr.resourcepacks

import dev.slimevr.osc.OscArg
import dev.slimevr.skeleton.BoneState
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.min

private sealed interface PipelineValue {
	data class Rotation(val value: Quaternion) : PipelineValue
	data class Vector(val value: Vector3) : PipelineValue
	data class Number(val value: Float) : PipelineValue
	data class Boolean(val value: kotlin.Boolean) : PipelineValue
}

internal fun evaluateEmit(entry: CompiledEmitEntry, bone: BoneState, relativeTo: BoneState?): List<OscArg> {
	var value: PipelineValue = when (entry.from) {
		EmitSource.POSITION -> PipelineValue.Vector(
			relativeTo?.let { it.rotation.inv().sandwich(bone.headPosition - it.headPosition) } ?: bone.headPosition,
		)

		EmitSource.ROTATION -> PipelineValue.Rotation(
			relativeTo?.let { it.rotation.inv() * bone.rotation } ?: bone.rotation,
		)
	}
	for (step in entry.steps) value = applyStep(step, value)
	return value.toOscArgs()
}

private fun applyStep(step: PipelineStep, value: PipelineValue): PipelineValue = when {
	step.euler != null -> euler(value, step.euler)
	step.scale != null -> arithmetic(value, step.scale, 1f) { left, right -> left * right }
	step.divide != null -> arithmetic(value, step.divide, 1f) { left, right -> left / right }
	step.offset != null -> arithmetic(value, step.offset, 0f) { left, right -> left + right }
	step.clamp != null -> clamp(value, step.clamp)
	step.greaterThan != null -> PipelineValue.Boolean((value as PipelineValue.Number).value > step.greaterThan)
	step.lessThan != null -> PipelineValue.Boolean((value as PipelineValue.Number).value < step.lessThan)
	else -> error("Pipeline step has no operation")
}

private fun euler(value: PipelineValue, spec: EulerSpec): PipelineValue {
	val rotation = (value as PipelineValue.Rotation).value
	val order = io.github.axisangles.ktmath.EulerOrder.valueOf((spec.order ?: EulerOrder.XYZ).name)
	val angles = rotation.toEulerAngles(order)
	val scale = if ((spec.unit ?: EulerUnit.DEGREES) == EulerUnit.DEGREES) 180f / PI.toFloat() else 1f
	fun axis(axis: Axis): Float = when (axis) {
		Axis.X -> angles.x * scale
		Axis.Y -> angles.y * scale
		Axis.Z -> angles.z * scale
	}
	return spec.axis?.let { PipelineValue.Number(axis(it)) }
		?: PipelineValue.Vector(Vector3(axis(Axis.X), axis(Axis.Y), axis(Axis.Z)))
}

private fun arithmetic(value: PipelineValue, operand: ScalarOrVector, default: Float, operation: (Float, Float) -> Float): PipelineValue = when (value) {
	is PipelineValue.Number -> PipelineValue.Number(operation(value.value, operand.scalar()))

	is PipelineValue.Vector -> PipelineValue.Vector(componentWise(value.value, operand, default, operation))

	is PipelineValue.Rotation -> {
		val q = value.value
		val result = componentWise(Vector3(q.x, q.y, q.z), operand, default, operation)
		PipelineValue.Rotation(Quaternion(q.w, result.x, result.y, result.z))
	}

	is PipelineValue.Boolean -> error("Arithmetic cannot apply to boolean")
}

private fun ScalarOrVector.scalar(): Float = when (this) {
	is ScalarOrVector.Scalar -> value
	is ScalarOrVector.Vector -> error("A vector operand cannot apply to a scalar pipeline value")
}

private fun componentWise(value: Vector3, operand: ScalarOrVector, default: Float, operation: (Float, Float) -> Float): Vector3 = when (operand) {
	is ScalarOrVector.Scalar -> Vector3(operation(value.x, operand.value), operation(value.y, operand.value), operation(value.z, operand.value))

	is ScalarOrVector.Vector -> Vector3(
		operation(value.x, operand.value.x ?: default),
		operation(value.y, operand.value.y ?: default),
		operation(value.z, operand.value.z ?: default),
	)
}

private fun clamp(value: PipelineValue, bounds: List<Float>): PipelineValue {
	val minimum = bounds[0]
	val maximum = bounds[1]
	fun clamped(number: Float) = max(minimum, min(maximum, number))
	return when (value) {
		is PipelineValue.Number -> PipelineValue.Number(clamped(value.value))
		is PipelineValue.Vector -> PipelineValue.Vector(Vector3(clamped(value.value.x), clamped(value.value.y), clamped(value.value.z)))
		else -> error("Clamp can only apply to a number or vector")
	}
}

private fun PipelineValue.toOscArgs(): List<OscArg> = when (this) {
	is PipelineValue.Boolean -> listOf(if (value) OscArg.True else OscArg.False)
	is PipelineValue.Number -> listOf(OscArg.Float(value))
	is PipelineValue.Vector -> listOf(OscArg.Float(value.x), OscArg.Float(value.y), OscArg.Float(value.z))
	is PipelineValue.Rotation -> listOf(OscArg.Float(value.w), OscArg.Float(value.x), OscArg.Float(value.y), OscArg.Float(value.z))
}
