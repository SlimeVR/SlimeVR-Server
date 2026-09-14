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
	fun `tracker transforms reproduce the retired position and rotation encoder`() {
		val position = Vector3(1f, 2f, 3f)
		val rotation = Quaternion.rotationAroundXAxis(0.3f) * Quaternion.rotationAroundYAxis(-0.4f)
		val state = bone(position, rotation)

		val positionArgs = evaluateEmit(
			entry(EmitSource.POSITION, listOf(PipelineStep(scale = ScalarOrVector.Vector(PartialVector3(z = -1f))))),
			state,
			null,
		)
		assertFloats(listOf(1f, 2f, -3f), positionArgs)

		val rotationArgs = evaluateEmit(
			entry(
				EmitSource.ROTATION,
				listOf(
					PipelineStep(scale = ScalarOrVector.Vector(PartialVector3(x = -1f, y = -1f))),
					PipelineStep(euler = EulerSpec(order = EulerOrder.YXZ)),
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
	fun `toe pipeline reproduces bend splay curl and tiptoe thresholds`() {
		fun toeEntry(axis: Axis, step: PipelineStep) = CompiledEmitEntry(
			EmitSource.ROTATION,
			relativeTo = BoneId(2u),
			steps = listOf(PipelineStep(euler = EulerSpec(axis = axis)), step),
		)
		val foot = bone(rotation = Quaternion.IDENTITY)
		val bentToe = bone(rotation = Quaternion.rotationAroundXAxis(16f * PI.toFloat() / 180f))
		val tiptoe = bone(rotation = Quaternion.rotationAroundXAxis(-15f * PI.toFloat() / 180f))

		assertEquals(OscArg.True, evaluateEmit(toeEntry(Axis.X, PipelineStep(greaterThan = 15f)), bentToe, foot).single())
		assertEquals(OscArg.True, evaluateEmit(toeEntry(Axis.X, PipelineStep(lessThan = -14f)), tiptoe, foot).single())
		assertEquals(
			OscArg.True,
			evaluateEmit(toeEntry(Axis.Z, PipelineStep(greaterThan = 7f)), bone(rotation = Quaternion.rotationAroundZAxis(8f * PI.toFloat() / 180f)), foot).single(),
		)
		val curl = evaluateEmit(
			CompiledEmitEntry(EmitSource.ROTATION, BoneId(2u), listOf(PipelineStep(euler = EulerSpec(axis = Axis.X)), PipelineStep(divide = ScalarOrVector.Scalar(90f)), PipelineStep(clamp = listOf(-1f, 1f)))),
			bone(rotation = Quaternion.rotationAroundXAxis(100f * PI.toFloat() / 180f)),
			foot,
		)
		assertFloats(listOf(1f), curl)
	}
}
