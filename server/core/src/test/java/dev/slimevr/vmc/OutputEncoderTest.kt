package dev.slimevr.vmc

import dev.slimevr.config.VMCConfig
import dev.slimevr.osc.OscArg
import dev.slimevr.osc.OscBundle
import dev.slimevr.osc.OscContent
import dev.slimevr.osc.OscMessage
import dev.slimevr.skeleton.BodyPartMap
import dev.slimevr.skeleton.BoneState
import dev.slimevr.skeleton.Velocity
import dev.slimevr.skeleton.bodyPartMap
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

private fun bone(bodyPart: BodyPart, rotation: Quaternion = Quaternion.IDENTITY) = BoneState(
	parentBone = null,
	bodyPart = bodyPart,
	offset = Vector3(0f, -0.1f, 0f),
	rotation = rotation,
	acceleration = Vector3.ZERO,
	headPosition = Vector3(0f, 1f, 0f),
	tailPosition = Vector3(0f, 0.9f, 0f),
	velocity = Velocity(Vector3.ZERO, Vector3.ZERO),
)

private fun messages(bundle: OscBundle): List<OscMessage> = bundle.contents.map { (it as OscContent.Message).msg }

private fun boneMessage(bundle: OscBundle, unityName: String): OscMessage? = messages(bundle)
	.firstOrNull { it.address == "/VMC/Ext/Bone/Pos" && (it.args.firstOrNull() as? OscArg.String)?.value == unityName }

class OutputEncoderTest {
	private val defaultConfig = VMCConfig()

	@Test
	fun testAlwaysSendsTimeAndOkAndRoot() {
		val bundle = buildOutgoingBundle(
			bones = bodyPartMap(),
			routedBones = emptySet(),
			config = defaultConfig,
			vrm = null,
			elapsed = 2.seconds,
		)

		val addresses = messages(bundle).map { it.address }
		assertEquals(listOf("/VMC/Ext/T", "/VMC/Ext/OK", "/VMC/Ext/Root/Pos"), addresses)
		assertEquals(2f, (messages(bundle)[0].args[0] as OscArg.Float).value)
		assertEquals(1, (messages(bundle)[1].args[0] as OscArg.Int).value)
	}

	@Test
	fun testSkipsBonesThatAreNotRouted() {
		val bones = BodyPartMap(
			mapOf(
				BodyPart.HIP to bone(BodyPart.HIP),
				BodyPart.WAIST to bone(BodyPart.WAIST),
			),
		)

		val bundle = buildOutgoingBundle(
			bones = bones,
			routedBones = setOf(BodyPart.HIP),
			config = defaultConfig,
			vrm = null,
			elapsed = 0.seconds,
		)

		assertNotNull(boneMessage(bundle, "Hips"))
		assertNull(boneMessage(bundle, "Spine"))
	}

	@Test
	fun testSkipsRoutedBonesMissingFromTheSkeleton() {
		val bundle = buildOutgoingBundle(
			bones = BodyPartMap(mapOf(BodyPart.HIP to bone(BodyPart.HIP))),
			routedBones = setOf(BodyPart.HIP, BodyPart.WAIST),
			config = defaultConfig,
			vrm = null,
			elapsed = 0.seconds,
		)

		assertNotNull(boneMessage(bundle, "Hips"))
		assertNull(boneMessage(bundle, "Spine"))
	}

	@Test
	fun testMirrorTrackingReadsTheOppositeSideBone() {
		val leftRotation = Quaternion.rotationAroundXAxis(0.5f)
		val bones = BodyPartMap(
			mapOf(
				BodyPart.LEFT_UPPER_LEG to bone(BodyPart.LEFT_UPPER_LEG, leftRotation),
				BodyPart.RIGHT_UPPER_LEG to bone(BodyPart.RIGHT_UPPER_LEG),
				BodyPart.HIP to bone(BodyPart.HIP),
			),
		)
		val routed = setOf(BodyPart.HIP, BodyPart.LEFT_UPPER_LEG, BodyPart.RIGHT_UPPER_LEG)

		val plain = buildOutgoingBundle(bones, routed, defaultConfig, null, 0.seconds)
		val mirrored = buildOutgoingBundle(
			bones,
			routed,
			defaultConfig.copy(mirrorTracking = true),
			null,
			0.seconds,
		)

		// Only the left leg is rotated, so mirroring must move that rotation onto the right leg
		// and leave the left leg reading the (identity) right one.
		assertTrue(boneMessage(plain, "RightUpperLeg")?.args != boneMessage(mirrored, "RightUpperLeg")?.args)
		assertTrue(boneMessage(plain, "LeftUpperLeg")?.args != boneMessage(mirrored, "LeftUpperLeg")?.args)
	}

	@Test
	fun testVrmBindOffsetsReplaceComputedPositions() {
		val bones = BodyPartMap(
			mapOf(
				BodyPart.HIP to bone(BodyPart.HIP),
				BodyPart.WAIST to bone(BodyPart.WAIST),
			),
		)
		val routed = setOf(BodyPart.HIP, BodyPart.WAIST)
		val vrm = buildVrmGeometry(VrmReader(VRM_JSON))

		val bundle = buildOutgoingBundle(bones, routed, defaultConfig, vrm, 0.seconds)

		val hips = assertNotNull(boneMessage(bundle, "Hips"))
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
