package dev.slimevr.resourcepacks

import dev.slimevr.bones.BodyPart
import dev.slimevr.bones.BoneId
import dev.slimevr.bones.BoneMap
import dev.slimevr.bones.BoneRegistry
import dev.slimevr.bones.key
import dev.slimevr.resourcepacks.bones.CompiledVmcOutput
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlin.math.cos
import kotlin.math.sin
import com.jme3.math.FastMath.DEG_TO_RAD as degToRad

/** Resolves each bone's VMC output, dynamically re-rooting the hierarchy at the hip if no explicit parents are set. */
internal fun compileVmcOutputs(
	registry: BoneRegistry,
	contributions: Map<BoneId, Pair<BoneContribution, VmcOutput>>,
	diagnostics: MutableList<ResourcePackCompilationDiagnostic>,
): BoneMap<CompiledVmcOutput> {
	val result = BoneMap.of<CompiledVmcOutput>(registry)
	val hip = registry[BodyPart.HIP.key] ?: return result
	val bfsParent = mutableMapOf<BoneId, BoneId?>(hip to null)
	val adjacency = mutableMapOf<BoneId, MutableList<BoneId>>()
	for (index in 1..registry.maxId) {
		val boneId = BoneId(index.toUShort())
		val parent = registry.parentOf(boneId) ?: continue
		adjacency.getOrPut(boneId) { mutableListOf() }.add(parent)
		adjacency.getOrPut(parent) { mutableListOf() }.add(boneId)
	}
	val queue = ArrayDeque(listOf(hip))
	while (queue.isNotEmpty()) {
		val current = queue.removeFirst()
		for (neighbor in adjacency[current].orEmpty()) {
			if (neighbor in bfsParent) continue
			bfsParent[neighbor] = current
			queue += neighbor
		}
	}
	fun namedAncestor(boneId: BoneId): BoneId? {
		var current = bfsParent[boneId]
		while (current != null && current !in contributions) current = bfsParent[current]
		return current
	}

	for ((boneId, pair) in contributions) {
		val (contribution, vmc) = pair
		val origin = contribution.origin(BoneField.OutputsVmc)
		val outputParent = vmc.outputParent?.let { resolveBoneKey(it, origin, "outputs.vmc.outputParent", registry, diagnostics) } ?: namedAncestor(boneId)
		val inputParent = when (val parent = vmc.inputParent) {
			VmcInputParent.Omitted -> outputParent
			VmcInputParent.ExplicitNull -> null
			is VmcInputParent.Bone -> resolveBoneKey(parent.key, origin, "outputs.vmc.inputParent", registry, diagnostics)
		}
		result[boneId] = CompiledVmcOutput(
			names = vmc.name.values,
			outputParent = outputParent,
			inputParent = inputParent,
			restRotation = (vmc.restRotation
				?: emptyList()).fold(Quaternion.IDENTITY) { acc, step ->
				axisAngleQuaternion(
					Vector3(step.axis.x, step.axis.y, step.axis.z),
					step.degrees * degToRad
				) * acc
			},
		)
	}
	validateVmcOutputs(registry, result, contributions, diagnostics)
	return result
}

private fun validateVmcOutputs(
	registry: BoneRegistry,
	outputs: BoneMap<CompiledVmcOutput>,
	contributions: Map<BoneId, Pair<BoneContribution, VmcOutput>>,
	diagnostics: MutableList<ResourcePackCompilationDiagnostic>,
) {
	val names = mutableMapOf<String, BoneId>()
	for ((boneId, output) in outputs) {
		val contribution = contributions.getValue(boneId).first
		val origin = contribution.origin(BoneField.OutputsVmc)
		for (name in output.names) reportDuplicateBoneKey(names, name, boneId, "VMC name", registry, origin, diagnostics)
		for ((field, parent) in listOf("outputParent" to output.outputParent, "inputParent" to output.inputParent)) {
			if (parent != null && parent !in outputs) {
				val parentKey = registry.keyOf(parent) ?: parent.toString()
				diagnostics += origin.diagnostic("VMC $field '$parentKey' does not have a VMC output")
			}
		}
	}

	fun validateParentGraph(field: String, parentOf: (CompiledVmcOutput) -> BoneId?) {
		reportCycles(outputs.keys, { outputs[it]?.let(parentOf) }) { _, lastVisited, cycle ->
			val origin = contributions.getValue(lastVisited).first.origin(BoneField.OutputsVmc)
			val keys = cycle.map { registry.keyOf(it) ?: it.toString() }
			diagnostics += origin.diagnostic("VMC $field graph contains a cycle: ${keys.joinToString(" -> ")}")
		}
	}
	validateParentGraph("outputParent", CompiledVmcOutput::outputParent)
	validateParentGraph("inputParent", CompiledVmcOutput::inputParent)
}

internal fun compileVmcInputOrder(outputs: BoneMap<CompiledVmcOutput>): List<BoneId> {
	val childrenByParent = outputs.entries.groupBy({ it.value.inputParent }, { it.key })
	val result = mutableListOf<BoneId>()
	val visited = mutableSetOf<BoneId>()
	fun visit(boneId: BoneId) {
		if (!visited.add(boneId)) return
		result += boneId
		childrenByParent[boneId]?.forEach(::visit)
	}
	childrenByParent[null].orEmpty().forEach(::visit)
	outputs.keys.forEach(::visit)
	return result
}

private fun axisAngleQuaternion(axis: Vector3, radians: Float): Quaternion {
	val half = radians / 2f
	return Quaternion(cos(half), axis.unit() * sin(half))
}
