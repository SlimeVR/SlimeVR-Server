package dev.slimevr.resourcepacks.bones.outputs

import dev.slimevr.bones.BoneId
import dev.slimevr.bones.BoneMap
import dev.slimevr.bones.BoneRegistry
import dev.slimevr.resourcepacks.EmitEntry
import dev.slimevr.resourcepacks.EmitSource
import dev.slimevr.resourcepacks.PipelineStep
import dev.slimevr.resourcepacks.ScalarOrVector
import dev.slimevr.resourcepacks.bones.CompiledEmitEntry
import dev.slimevr.resourcepacks.bones.origin
import dev.slimevr.resourcepacks.bones.resolveBoneKey
import dev.slimevr.resourcepacks.compiler.BoneContribution
import dev.slimevr.resourcepacks.compiler.BoneField
import dev.slimevr.resourcepacks.compiler.ResourceOrigin
import dev.slimevr.resourcepacks.compiler.ResourcePackCompilationDiagnostic
import dev.slimevr.resourcepacks.compiler.diagnostic
import dev.slimevr.resourcepacks.compiler.reportDuplicateBoneKey
import kotlin.collections.iterator

internal fun compileEmitEntries(
	registry: BoneRegistry,
	contributions: Map<BoneId, Pair<BoneContribution, Map<String, EmitEntry>>>,
	diagnostics: MutableList<ResourcePackCompilationDiagnostic>,
): BoneMap<Map<String, CompiledEmitEntry>> {
	val result = BoneMap.of<Map<String, CompiledEmitEntry>>(registry)
	val addresses = mutableMapOf<String, BoneId>()
	for ((boneId, pair) in contributions) {
		val (contribution, entries) = pair
		result[boneId] = entries.mapValues { (address, entry) ->
			val origin = contribution.origin(BoneField.OutputsVrchatEmit(address))
			reportDuplicateBoneKey(
				addresses,
				address,
				boneId,
				"VRChat emit address",
				registry,
				origin,
				diagnostics,
			)
			validateEmitPipeline(address, entry, origin, diagnostics)
			CompiledEmitEntry(
				from = entry.from,
				relativeTo = entry.relativeTo?.let {
					resolveBoneKey(
						it,
						origin,
						"outputs.vrchat.emit.relativeTo",
						registry,
						diagnostics,
					)
				},
				steps = entry.value ?: emptyList(),
			)
		}
	}
	return result
}

private enum class EmitValueType { ROTATION, VECTOR, NUMBER, BOOLEAN }

private fun validateEmitPipeline(
	address: String,
	entry: EmitEntry,
	origin: ResourceOrigin,
	diagnostics: MutableList<ResourcePackCompilationDiagnostic>,
) {
	var type = when (entry.from) {
		EmitSource.POSITION -> EmitValueType.VECTOR
		EmitSource.ROTATION -> EmitValueType.ROTATION
	}
	for ((index, step) in entry.value.orEmpty().withIndex()) {
		val (operation, next) = when (step) {
			is PipelineStep.Euler -> {
				"euler" to if (type != EmitValueType.ROTATION) {
					null
				} else if (step.spec.axis == null) {
					EmitValueType.VECTOR
				} else {
					EmitValueType.NUMBER
				}
			}

			is PipelineStep.Scale -> "scale" to arithmeticResult(type, step.operand)

			is PipelineStep.Divide -> "divide" to arithmeticResult(type, step.operand)

			is PipelineStep.Offset -> "offset" to arithmeticResult(type, step.operand)

			is PipelineStep.Clamp -> "clamp" to type.takeIf { it == EmitValueType.NUMBER || it == EmitValueType.VECTOR }

			is PipelineStep.GreaterThan -> "greaterThan" to EmitValueType.BOOLEAN.takeIf { type == EmitValueType.NUMBER }

			is PipelineStep.LessThan -> "lessThan" to EmitValueType.BOOLEAN.takeIf { type == EmitValueType.NUMBER }
		}
		if (next == null) {
			diagnostics += origin.diagnostic("VRChat emit '$address' cannot apply $operation to ${type.name.lowercase()} at pipeline step ${index + 1}")
			return
		}
		type = next
	}
}

private fun arithmeticResult(type: EmitValueType, operand: ScalarOrVector): EmitValueType? = when (type) {
	EmitValueType.ROTATION, EmitValueType.VECTOR -> type
	EmitValueType.NUMBER -> if (operand is ScalarOrVector.Scalar) EmitValueType.NUMBER else null
	EmitValueType.BOOLEAN -> null
}
