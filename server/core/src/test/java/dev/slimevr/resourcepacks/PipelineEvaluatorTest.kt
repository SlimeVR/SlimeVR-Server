package dev.slimevr.resourcepacks

import dev.slimevr.bones.BoneId
import dev.slimevr.osc.OscArg
import dev.slimevr.skeleton.BoneState
import dev.slimevr.skeleton.Velocity
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private const val EPSILON = 1e-5f

private fun bone(position: Vector3 = Vector3.ZERO, rotation: Quaternion = Quaternion.IDENTITY) = BoneState(
	parentBone = null,
	boneId = BoneId(1u),
	headOffset = Vector3.ZERO,
	offset = Vector3.ZERO,
	rotation = rotation,
	acceleration = Vector3.ZERO,
	headPosition = position,
	tailPosition = position,
	velocity = Velocity(Vector3.ZERO, Vector3.ZERO),
)

private fun entry(
	from: EmitSource,
	steps: List<PipelineStep>,
) = CompiledEmitEntry(from, relativeTo = null, steps)

private fun floatArgs(args: List<OscArg>): List<Float> = args.map { (it as OscArg.Float).value }

private fun assertFloats(expected: List<Float>, actual: List<OscArg>) {
	val values = floatArgs(actual)
	assertEquals(expected.size, values.size)
	for ((index, value) in values.withIndex()) assertTrue(kotlin.math.abs(expected[index] - value) < EPSILON, "index $index: expected ${expected[index]}, got $value")
}

class PipelineEvaluatorTest {
	@Test
	fun `position and rotation pipelines transform a tracker relative to its parent`() {
		val position = Vector3(1f, 2f, 3f)
		val rotation = Quaternion.rotationAroundXAxis(0.3f) * Quaternion.rotationAroundYAxis(-0.4f)
		val state = bone(position, rotation)

		val positionArgs = evaluateEmit(
			entry(EmitSource.POSITION, listOf(PipelineStep.Scale(ScalarOrVector.Vector(PartialVector3(z = -1f))))),
			state,
			null,
		)
		assertFloats(listOf(1f, 2f, -3f), positionArgs)

		val rotationArgs = evaluateEmit(
			entry(
				EmitSource.ROTATION,
				listOf(
					PipelineStep.Scale(ScalarOrVector.Vector(PartialVector3(x = -1f, y = -1f))),
					PipelineStep.Euler(EulerSpec(order = EulerOrder.YXZ)),
				),
			),
			state,
			null,
		)
		val (_, x, y, z) = Quaternion(rotation.w, -rotation.x, -rotation.y, rotation.z)
			.toEulerAngles(io.github.axisangles.ktmath.EulerOrder.YXZ)
		val degrees = 180f / PI.toFloat()
		assertFloats(listOf(x * degrees, y * degrees, z * degrees), rotationArgs)
	}

	@Test
	fun `toe pipeline evaluates bend splay curl and tiptoe thresholds`() {
		fun toeEntry(axis: Axis, step: PipelineStep) = CompiledEmitEntry(
			EmitSource.ROTATION,
			relativeTo = BoneId(2u),
			steps = listOf(PipelineStep.Euler(EulerSpec(axis = axis)), step),
		)
		val foot = bone(rotation = Quaternion.IDENTITY)
		val bentToe = bone(rotation = Quaternion.rotationAroundXAxis(16f * PI.toFloat() / 180f))
		val tiptoe = bone(rotation = Quaternion.rotationAroundXAxis(-15f * PI.toFloat() / 180f))

		assertEquals(OscArg.True, evaluateEmit(toeEntry(Axis.X, PipelineStep.GreaterThan(15f)), bentToe, foot).single())
		assertEquals(OscArg.True, evaluateEmit(toeEntry(Axis.X, PipelineStep.LessThan(-14f)), tiptoe, foot).single())
		assertEquals(
			OscArg.True,
			evaluateEmit(toeEntry(Axis.Z, PipelineStep.GreaterThan(7f)), bone(rotation = Quaternion.rotationAroundZAxis(8f * PI.toFloat() / 180f)), foot).single(),
		)
		val curl = evaluateEmit(
			CompiledEmitEntry(
				EmitSource.ROTATION,
				BoneId(2u),
				listOf(PipelineStep.Euler(EulerSpec(axis = Axis.X)), PipelineStep.Divide(ScalarOrVector.Scalar(90f)), PipelineStep.Clamp(listOf(-1f, 1f))),
			),
			bone(rotation = Quaternion.rotationAroundXAxis(100f * PI.toFloat() / 180f)),
			foot,
		)
		assertFloats(listOf(1f), curl)
	}
}
