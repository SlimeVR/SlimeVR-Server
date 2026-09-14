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
import dev.slimevr.skeleton.BoneState
import dev.slimevr.skeleton.Velocity
import dev.slimevr.testCompiledSkeleton
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

private val definition = testCompiledSkeleton
private val registry = definition.registry

private fun bone(bodyPart: BodyPart, rotation: Quaternion = Quaternion.IDENTITY) = BoneState(
	parentBone = null,
	boneId = bodyPart.boneId,
	headOffset = Vector3.ZERO,
	offset = Vector3(0f, -0.1f, 0f),
	rotation = rotation,
	acceleration = Vector3.ZERO,
	headPosition = Vector3(0f, 1f, 0f),
	tailPosition = Vector3(0f, 0.9f, 0f),
	velocity = Velocity(Vector3.ZERO, Vector3.ZERO),
)

private fun routed(vararg bodyParts: BodyPart): Set<BoneId> = bodyParts.map { it.boneId }.toSet()

private fun bundle(
	bones: dev.slimevr.skeleton.ComputedSkeleton,
	routedBones: Set<BoneId>,
	config: VMCConfig = VMCConfig(),
	vrm: VrmGeometry? = null,
	elapsed: kotlin.time.Duration = 0.seconds,
) = buildOutgoingBundle(definition, bones, routedBones, config, vrm, elapsed)

private fun messages(bundle: OscBundle): List<OscMessage> = bundle.contents.map { (it as OscContent.Message).msg }

private fun boneMessage(bundle: OscBundle, unityName: String): OscMessage? = messages(bundle)
	.firstOrNull { it.address == "/VMC/Ext/Bone/Pos" && (it.args.firstOrNull() as? OscArg.String)?.value == unityName }

class OutputEncoderTest {
	private val defaultConfig = VMCConfig()

	@Test
	fun testAlwaysSendsTimeAndOkAndRoot() {
		val result = bundle(
			bones = BoneMap.of(registry),
			routedBones = emptySet(),
			elapsed = 2.seconds,
		)

		val addresses = messages(result).map { it.address }
		assertEquals(listOf("/VMC/Ext/T", "/VMC/Ext/OK", "/VMC/Ext/Root/Pos"), addresses)
		assertEquals(2f, (messages(result)[0].args[0] as OscArg.Float).value)
		assertEquals(1, (messages(result)[1].args[0] as OscArg.Int).value)
	}

	@Test
	fun testSkipsBonesThatAreNotRouted() {
		val bones = registry.computedSkeletonOf(
			BodyPart.HIP to bone(BodyPart.HIP),
			BodyPart.LOWER_WAIST to bone(BodyPart.LOWER_WAIST),
		)

		val result = bundle(bones = bones, routedBones = routed(BodyPart.HIP))

		assertNotNull(boneMessage(result, "Hips"))
		assertNull(boneMessage(result, "Spine"))
	}

	@Test
	fun testSkipsRoutedBonesMissingFromTheSkeleton() {
		val result = bundle(
			bones = registry.computedSkeletonOf(BodyPart.HIP to bone(BodyPart.HIP)),
			routedBones = routed(BodyPart.HIP, BodyPart.LOWER_WAIST),
		)

		assertNotNull(boneMessage(result, "Hips"))
		assertNull(boneMessage(result, "Spine"))
	}

	@Test
	fun testMirrorTrackingReadsTheOppositeSideBone() {
		val leftRotation = Quaternion.rotationAroundXAxis(0.5f)
		val bones = registry.computedSkeletonOf(
			BodyPart.LEFT_UPPER_LEG to bone(BodyPart.LEFT_UPPER_LEG, leftRotation),
			BodyPart.RIGHT_UPPER_LEG to bone(BodyPart.RIGHT_UPPER_LEG),
			BodyPart.HIP to bone(BodyPart.HIP),
		)
		val routedBones = routed(BodyPart.HIP, BodyPart.LEFT_UPPER_LEG, BodyPart.RIGHT_UPPER_LEG)

		val plain = bundle(bones, routedBones, defaultConfig)
		val mirrored = bundle(bones, routedBones, defaultConfig.copy(mirrorTracking = true))

		// Only the left leg is rotated, so mirroring must move that rotation onto the right leg
		// and leave the left leg reading the (identity) right one.
		assertTrue(boneMessage(plain, "RightUpperLeg")?.args != boneMessage(mirrored, "RightUpperLeg")?.args)
		assertTrue(boneMessage(plain, "LeftUpperLeg")?.args != boneMessage(mirrored, "LeftUpperLeg")?.args)
	}

	@Test
	fun testVrmBindOffsetsReplaceComputedPositions() {
		val bones = registry.computedSkeletonOf(
			BodyPart.HIP to bone(BodyPart.HIP),
			BodyPart.LOWER_WAIST to bone(BodyPart.LOWER_WAIST),
		)
		val routedBones = routed(BodyPart.HIP, BodyPart.LOWER_WAIST)
		val vrm = buildVrmGeometry(definition, VrmReader(VRM_JSON))

		val result = bundle(bones, routedBones, defaultConfig, vrm)

		val hips = assertNotNull(boneMessage(result, "Hips"))
		assertEquals(0.9f, (hips.args[2] as OscArg.Float).value)
	}
}

// Minimal VRM 1.0 document: a hips node at y = 0.9 and a spine node above it.
private val VRM_JSON = """
	{
	  "extensions": {
	    "VRMC_vrm": {
	      "specVersion": "1.0",
	      "humanoid": {
	        "humanBones": {
	          "hips": { "node": 0 },
	          "spine": { "node": 1 }
	        }
	      }
	    }
	  },
	  "nodes": [
	    { "translation": [0.0, 0.9, 0.0] },
	    { "translation": [0.0, 0.1, 0.0] }
	  ]
	}
""".trimIndent()
