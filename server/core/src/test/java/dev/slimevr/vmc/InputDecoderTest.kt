package dev.slimevr.vmc

import dev.slimevr.bones.BodyPart
import dev.slimevr.bones.BoneId
import dev.slimevr.bones.BoneMap
import dev.slimevr.bones.boneId
import dev.slimevr.computedSkeletonOf
import dev.slimevr.config.VMCConfig
import dev.slimevr.osc.OscArg
import dev.slimevr.osc.OscBundle
import dev.slimevr.osc.OscContent
import dev.slimevr.osc.OscMessage
import dev.slimevr.osc.forEachOscMessage
import dev.slimevr.quaternionApproxEqual
import dev.slimevr.skeleton.BoneState
import dev.slimevr.skeleton.Velocity
import dev.slimevr.testCompiledSkeleton
import dev.slimevr.vectorAssertEquals
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

private fun assertApprox(expected: Vector3, actual: Vector3, message: String = "") = vectorAssertEquals(expected, actual, message = message)

// Quaternions q and -q represent the same rotation, so either sign is an acceptable match.
private fun assertApprox(expected: Quaternion, actual: Quaternion, message: String = "") {
	val negated = Quaternion(-expected.w, -expected.x, -expected.y, -expected.z)
	assertTrue(
		quaternionApproxEqual(expected, actual) || quaternionApproxEqual(negated, actual),
		"$message expected=$expected actual=$actual",
	)
}

private val definition = testCompiledSkeleton
private val registry = definition.registry

private fun bone(bodyPart: BodyPart, rotation: Quaternion = Quaternion.IDENTITY, headPosition: Vector3 = Vector3.ZERO) = BoneState(
	parentBone = null,
	boneId = bodyPart.boneId,
	headOffset = Vector3.ZERO,
	offset = Vector3(0f, -0.1f, 0f),
	rotation = rotation,
	acceleration = Vector3.ZERO,
	headPosition = headPosition,
	tailPosition = headPosition - Vector3(0f, 0.1f, 0f),
	velocity = Velocity(Vector3.ZERO, Vector3.ZERO),
)

private fun transformArgs(name: String, pos: Vector3, rot: Quaternion): List<OscArg> = listOf(
	OscArg.String(name),
	OscArg.Float(pos.x),
	OscArg.Float(pos.y),
	OscArg.Float(-pos.z),
	OscArg.Float(rot.x),
	OscArg.Float(rot.y),
	OscArg.Float(-rot.z),
	OscArg.Float(-rot.w),
)

private fun bundleOf(vararg messages: OscMessage) = OscBundle(1L, messages.map { OscContent.Message(it) })

private fun emptyFrame() = emptyVmcInputFrame(definition)

private fun decodeVmcBundle(bundle: OscBundle, frame: VmcInputFrame): VmcInputFrame {
	forEachOscMessage(bundle) { msg -> decodeVmcMessage(msg, frame, definition) }
	return frame
}

private fun boneMapOf(vararg entries: Pair<BodyPart, Vector3>): BoneMap<Vector3> = BoneMap.of<Vector3>(registry).also { map ->
	for ((bodyPart, value) in entries) map[bodyPart.boneId] = value
}

private fun rotationMapOf(vararg entries: Pair<BodyPart, Quaternion>): BoneMap<Quaternion> = BoneMap.of<Quaternion>(registry).also { map ->
	for ((bodyPart, value) in entries) map[bodyPart.boneId] = value
}

private fun worldTransforms(
	locals: BoneMap<Quaternion>,
	localPositions: BoneMap<Vector3>,
	rootRotation: Quaternion,
	rootPosition: Vector3,
	scale: Float,
) = vmcWorldTransforms(definition, locals, localPositions, rootRotation, rootPosition, scale)

class InputDecoderTest {

	@Test
	fun `Ignores unmapped bone names`() {
		val bundle = bundleOf(
			OscMessage("/VMC/Ext/Bone/Pos", transformArgs("LeftEye", Vector3.ZERO, Quaternion.IDENTITY)),
		)

		val frame = decodeVmcBundle(bundle, emptyFrame())

		assertEquals(0, frame.boneLocalRotations.size)
	}

	@Test
	fun `Carries forward bones missing from this bundle`() {
		val first = bundleOf(
			OscMessage("/VMC/Ext/Bone/Pos", transformArgs("Hips", Vector3(0f, 1f, 0f), Quaternion.IDENTITY)),
		)
		val firstFrame = decodeVmcBundle(first, emptyFrame())

		val second = bundleOf(
			OscMessage("/VMC/Ext/Bone/Pos", transformArgs("Spine", Vector3.ZERO, Quaternion.IDENTITY)),
		)
		val secondFrame = decodeVmcBundle(second, firstFrame)

		assertApprox(Vector3(0f, 1f, 0f), assertNotNull(secondFrame.boneLocalPositions[BodyPart.HIP.boneId]))
	}

	@Test
	fun `Reads root pose`() {
		val bundle = bundleOf(
			OscMessage("/VMC/Ext/Root/Pos", transformArgs("root", Vector3(1f, 0f, 0f), Quaternion.rotationAroundYAxis(0.5f))),
		)

		val frame = decodeVmcBundle(bundle, emptyFrame())

		assertApprox(Vector3(1f, 0f, 0f), frame.rootPosition)
		assertApprox(Quaternion.rotationAroundYAxis(0.5f), frame.rootRotation)
	}

	@Test
	fun `Pos-Local takes precedence over Pos for the same serial`() {
		val bundle = bundleOf(
			OscMessage("/VMC/Ext/Tra/Pos/Local", transformArgs("serial-1", Vector3(1f, 1f, 1f), Quaternion.IDENTITY)),
			OscMessage("/VMC/Ext/Tra/Pos", transformArgs("serial-1", Vector3(9f, 9f, 9f), Quaternion.IDENTITY)),
		)

		val frame = decodeVmcBundle(bundle, emptyFrame())

		val tracker = assertNotNull(frame.poseTrackers["serial-1"])
		assertApprox(Vector3(1f, 1f, 1f), tracker.position)
		assertEquals(true, tracker.deviceScale)
	}

	@Test
	fun `Round trip through the output encoder recovers world transforms`() {
		val chestRotation = Quaternion.rotationAroundYAxis(0.4f)
		val chestHead = Vector3(0f, 0.3f, 0f)

		val bonesByPart = mapOf(
			BodyPart.HIP to bone(BodyPart.HIP, Quaternion.rotationAroundYAxis(0.1f), Vector3.ZERO),
			BodyPart.UPPER_WAIST to bone(BodyPart.UPPER_WAIST, Quaternion.rotationAroundXAxis(0.2f), Vector3(0f, 0.15f, 0f)),
			BodyPart.LOWER_CHEST to bone(BodyPart.LOWER_CHEST, chestRotation, chestHead),
			BodyPart.UPPER_CHEST to bone(BodyPart.UPPER_CHEST, chestRotation, chestHead),
			BodyPart.NECK to bone(BodyPart.NECK, Quaternion.rotationAroundZAxis(0.15f), Vector3(0f, 0.5f, 0f)),
			BodyPart.HEAD to bone(BodyPart.HEAD, Quaternion.rotationAroundXAxis(-0.1f), Vector3(0f, 0.65f, 0f)),
			BodyPart.LEFT_SHOULDER to bone(BodyPart.LEFT_SHOULDER, Quaternion.rotationAroundZAxis(0.05f), Vector3(0.05f, 0.45f, 0f)),
			BodyPart.LEFT_UPPER_ARM to bone(BodyPart.LEFT_UPPER_ARM, Quaternion.rotationAroundZAxis(0.7f), Vector3(0.15f, 0.45f, 0f)),
			BodyPart.LEFT_LOWER_ARM to bone(BodyPart.LEFT_LOWER_ARM, Quaternion.rotationAroundXAxis(0.9f), Vector3(0.4f, 0.45f, 0f)),
			BodyPart.LEFT_HAND to bone(BodyPart.LEFT_HAND, Quaternion.rotationAroundYAxis(0.2f), Vector3(0.65f, 0.45f, 0f)),
			BodyPart.LEFT_UPPER_LEG to bone(BodyPart.LEFT_UPPER_LEG, Quaternion.rotationAroundXAxis(0.5f), Vector3(0.1f, 0f, 0f)),
			BodyPart.LEFT_LOWER_LEG to bone(BodyPart.LEFT_LOWER_LEG, Quaternion.rotationAroundXAxis(-0.3f), Vector3(0.1f, -0.4f, 0f)),
			BodyPart.LEFT_FOOT to bone(BodyPart.LEFT_FOOT, Quaternion.rotationAroundYAxis(0.25f), Vector3(0.1f, -0.8f, 0f)),
		)
		val bones = registry.computedSkeletonOf(*bonesByPart.toList().toTypedArray())
		val routedBones = bonesByPart.keys.map { it.boneId }.toSet()

		val bundle = buildOutgoingBundle(definition, bones, routedBones, VMCConfig(), vrm = null, elapsed = 0.seconds)
		val frame = decodeVmcBundle(bundle, emptyFrame())
		val worldTransforms = worldTransforms(
			locals = frame.boneLocalRotations,
			localPositions = frame.boneLocalPositions,
			rootRotation = frame.rootRotation,
			rootPosition = frame.rootPosition,
			scale = 1f,
		)

		for ((bodyPart, originalBone) in bonesByPart) {
			val recovered = assertNotNull(worldTransforms[bodyPart.boneId], "missing $bodyPart")
			assertApprox(originalBone.rotation, recovered.rotation, "$bodyPart rotation")
			assertApprox(originalBone.headPosition, recovered.position, "$bodyPart position")
		}
	}

	@Test
	fun `Root rotation is applied as a world-space pre-rotation`() {
		val hipLocalRotation = Quaternion.rotationAroundXAxis(0.2f)
		val rootRotation = Quaternion.rotationAroundYAxis(0.9f)
		val rootPosition = Vector3(1f, 0f, 0f)

		val locals = rotationMapOf(BodyPart.HIP to hipLocalRotation)
		val localPositions = BoneMap.of<Vector3>(registry)

		val result = worldTransforms(locals, localPositions, rootRotation, rootPosition, scale = 1f)
		val hip = assertNotNull(result[BodyPart.HIP.boneId])

		assertApprox(rootRotation * hipLocalRotation, hip.rotation)
		assertApprox(rootPosition, hip.position)
	}

	@Test
	fun `Scale multiplies position and leaves rotation alone`() {
		val locals = rotationMapOf(BodyPart.HIP to Quaternion.rotationAroundZAxis(0.3f))
		val localPositions = boneMapOf(BodyPart.HIP to Vector3(1f, 2f, 3f))

		val unscaled = worldTransforms(locals, localPositions, Quaternion.IDENTITY, Vector3.ZERO, scale = 1f)
		val scaled = worldTransforms(locals, localPositions, Quaternion.IDENTITY, Vector3.ZERO, scale = 2f)

		assertApprox(Quaternion.rotationAroundZAxis(0.3f), assertNotNull(scaled[BodyPart.HIP.boneId]).rotation)
		assertApprox(
			assertNotNull(unscaled[BodyPart.HIP.boneId]).position * 2f,
			assertNotNull(scaled[BodyPart.HIP.boneId]).position,
		)
	}

	@Test
	fun `UpperChest defaults to identity when never received`() {
		val neckLocal = Quaternion.rotationAroundXAxis(0.4f)

		// A sender whose model has no UpperChest bone never emits its local rotation, so it's
		// absent from the decoded locals map entirely. This should behave exactly as if
		// UpperChest's local rotation had been received as IDENTITY.
		val withoutUpperChest = rotationMapOf(BodyPart.NECK to neckLocal)
		val withIdentityUpperChest = rotationMapOf(BodyPart.NECK to neckLocal, BodyPart.UPPER_CHEST to Quaternion.IDENTITY)
		val emptyPositions = BoneMap.of<Vector3>(registry)

		val a = worldTransforms(withoutUpperChest, emptyPositions, Quaternion.IDENTITY, Vector3.ZERO, 1f)
		val b = worldTransforms(withIdentityUpperChest, emptyPositions, Quaternion.IDENTITY, Vector3.ZERO, 1f)

		assertApprox(assertNotNull(a[BodyPart.NECK.boneId]).rotation, assertNotNull(b[BodyPart.NECK.boneId]).rotation)
		assertApprox(assertNotNull(a[BodyPart.HEAD.boneId]).rotation, assertNotNull(b[BodyPart.HEAD.boneId]).rotation)
	}

	@Test
	fun `vrmHeight sums floor-to-neck offsets`() {
		val vrm = buildVrmGeometry(definition, VrmReader(VRM_HEIGHT_JSON))
		assertApprox(Vector3(0f, 1.25f, 0f), Vector3(0f, vrm.vrmHeight, 0f))
	}
}

// A minimal VRM 1.0 document with all five floor-to-neck bones present.
private val VRM_HEIGHT_JSON = """
	{
	  "extensions": {
	    "VRMC_vrm": {
	      "specVersion": "1.0",
	      "humanoid": {
	        "humanBones": {
	          "hips": { "node": 0 },
	          "spine": { "node": 1 },
	          "chest": { "node": 2 },
	          "upperChest": { "node": 3 },
	          "neck": { "node": 4 }
	        }
	      }
	    }
	  },
	  "nodes": [
	    { "translation": [0.0, 0.9, 0.0] },
	    { "translation": [0.0, 0.1, 0.0] },
	    { "translation": [0.0, 0.1, 0.0] },
	    { "translation": [0.0, 0.05, 0.0] },
	    { "translation": [0.0, 0.1, 0.0] }
	  ]
	}
""".trimIndent()
