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
import dev.slimevr.vectorAssertEquals
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

private fun bone(bodyPart: BodyPart, rotation: Quaternion = Quaternion.IDENTITY, headPosition: Vector3 = Vector3(0f, 1f, 0f)) = BoneState(
	parentBone = null,
	bodyPart = bodyPart,
	headOffset = Vector3.ZERO,
	offset = Vector3(0f, -0.1f, 0f),
	rotation = rotation,
	acceleration = Vector3.ZERO,
	headPosition = headPosition,
	tailPosition = headPosition + Vector3(0f, -0.1f, 0f),
	velocity = Velocity(Vector3.ZERO, Vector3.ZERO),
)

private fun messages(bundle: OscBundle): List<OscMessage> = bundle.contents.map { (it as OscContent.Message).msg }

private fun boneMessage(bundle: OscBundle, unityName: String): OscMessage? = messages(bundle)
	.firstOrNull { it.address == "/VMC/Ext/Bone/Pos" && (it.args.firstOrNull() as? OscArg.String)?.value == unityName }

// The encoder negates z on the wire, so undo that to recover the emitted position.
private fun messagePosition(msg: OscMessage): Vector3 = Vector3(
	(msg.args[1] as OscArg.Float).value,
	(msg.args[2] as OscArg.Float).value,
	-(msg.args[3] as OscArg.Float).value,
)

class OutputEncoderTest {
	private val defaultConfig = VMCConfig()

	@Test
	fun testAlwaysSendsTimeAndOkAndRoot() {
		val bundle = buildOutgoingBundle(
			bones = bodyPartMap(),
			routedBones = emptySet(),
			config = defaultConfig,
			vrm = null,
			skeletonHeight = 1.7f,
			floorLevel = 0f,
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
				BodyPart.LOWER_WAIST to bone(BodyPart.LOWER_WAIST),
			),
		)

		val bundle = buildOutgoingBundle(
			bones = bones,
			routedBones = setOf(BodyPart.HIP),
			config = defaultConfig,
			vrm = null,
			skeletonHeight = 1.7f,
			floorLevel = 0f,
			elapsed = 0.seconds,
		)

		assertNotNull(boneMessage(bundle, "Hips"))
		assertNull(boneMessage(bundle, "Spine"))
	}

	@Test
	fun testSkipsRoutedBonesMissingFromTheSkeleton() {
		val bundle = buildOutgoingBundle(
			bones = BodyPartMap(mapOf(BodyPart.HIP to bone(BodyPart.HIP))),
			routedBones = setOf(BodyPart.HIP, BodyPart.LOWER_WAIST),
			config = defaultConfig,
			vrm = null,
			skeletonHeight = 1.7f,
			floorLevel = 0f,
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

		val plain = buildOutgoingBundle(bones, routed, defaultConfig, null, 1.7f, 0f, 0.seconds)
		val mirrored = buildOutgoingBundle(
			bones,
			routed,
			defaultConfig.copy(mirrorTracking = true),
			null,
			1.7f,
			0f,
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
				BodyPart.LOWER_WAIST to bone(BodyPart.LOWER_WAIST),
			),
		)
		val routed = setOf(BodyPart.HIP, BodyPart.LOWER_WAIST)
		val vrm = buildVrmGeometry(VrmReader(VRM_JSON))

		val bundle = buildOutgoingBundle(bones, routed, defaultConfig, vrm, 1.7f, 0f, 0.seconds)

		val hips = assertNotNull(boneMessage(bundle, "Hips"))
		assertEquals(0.9f, (hips.args[2] as OscArg.Float).value)
	}

	@Test
	fun testAnchoredHipsWithVrmIgnoreLiveSkeletonPosition() {
		val vrm = buildVrmGeometry(VrmReader(VRM_JSON))
		// The hip bone itself is far from where the anchored output should place it, to prove
		// the live position isn't read at all.
		val bones = BodyPartMap(mapOf(BodyPart.HIP to bone(BodyPart.HIP, headPosition = Vector3(3f, 5f, -2f))))
		val config = defaultConfig.copy(anchorAtHips = true)

		val bundle = buildOutgoingBundle(bones, setOf(BodyPart.HIP), config, vrm, 1.7f, 0.3f, 0.seconds)

		val hips = assertNotNull(boneMessage(bundle, "Hips"))
		vectorAssertEquals(vrm.hipLocalPosition, messagePosition(hips))
	}

	@Test
	fun testAnchoredHipsWithoutVrmUseRestHeightAboveFloor() {
		val bones = BodyPartMap(
			mapOf(
				BodyPart.HIP to bone(BodyPart.HIP, headPosition = Vector3(3f, 5f, -2f)),
				BodyPart.NECK to bone(BodyPart.NECK),
				BodyPart.LOWER_CHEST to bone(BodyPart.LOWER_CHEST),
			),
		)
		val config = defaultConfig.copy(anchorAtHips = true)

		val bundle = buildOutgoingBundle(bones, setOf(BodyPart.HIP), config, null, 1.7f, 0f, 0.seconds)

		val hips = assertNotNull(boneMessage(bundle, "Hips"))
		// skeletonHeight (1.7) minus the two present bones' rest lengths (0.1 each)
		vectorAssertEquals(Vector3(0f, 1.5f, 0f), messagePosition(hips))
	}

	@Test
	fun testFreeAnchorWithoutVrmPlacesHipsFromNeckMinusFloor() {
		val hipHead = Vector3(0f, 0.9f, 0f)
		val bones = BodyPartMap(
			mapOf(
				BodyPart.HIP to bone(BodyPart.HIP, headPosition = hipHead),
				BodyPart.UPPER_WAIST to bone(BodyPart.UPPER_WAIST, headPosition = Vector3(0f, 1.1f, 0f)),
				BodyPart.LOWER_CHEST to bone(BodyPart.LOWER_CHEST, headPosition = Vector3(0f, 1.3f, 0f)),
				BodyPart.NECK to bone(BodyPart.NECK, headPosition = Vector3(0f, 1.5f, 0f)),
			),
		)
		val config = defaultConfig.copy(anchorAtHips = false)
		val skeletonHeight = 1.5f

		val atFloorZero = buildOutgoingBundle(bones, setOf(BodyPart.HIP), config, null, skeletonHeight, 0f, 0.seconds)
		vectorAssertEquals(hipHead, messagePosition(assertNotNull(boneMessage(atFloorZero, "Hips"))))

		val atRaisedFloor = buildOutgoingBundle(bones, setOf(BodyPart.HIP), config, null, skeletonHeight, 0.2f, 0.seconds)
		vectorAssertEquals(hipHead - Vector3(0f, 0.2f, 0f), messagePosition(assertNotNull(boneMessage(atRaisedFloor, "Hips"))))
	}

	@Test
	fun testFreeAnchorTranslatesWithWholeSkeleton() {
		val delta = Vector3(0.5f, 0.3f, -0.2f)
		fun bones(shift: Vector3) = BodyPartMap(
			mapOf(
				BodyPart.HIP to bone(BodyPart.HIP, headPosition = Vector3(0f, 0.9f, 0f) + shift),
				BodyPart.UPPER_WAIST to bone(BodyPart.UPPER_WAIST, headPosition = Vector3(0f, 1.1f, 0f) + shift),
				BodyPart.LOWER_CHEST to bone(BodyPart.LOWER_CHEST, headPosition = Vector3(0f, 1.3f, 0f) + shift),
				BodyPart.NECK to bone(BodyPart.NECK, headPosition = Vector3(0f, 1.5f, 0f) + shift),
			),
		)
		val config = defaultConfig.copy(anchorAtHips = false)
		val skeletonHeight = 1.5f

		val base = buildOutgoingBundle(bones(Vector3.ZERO), setOf(BodyPart.HIP), config, null, skeletonHeight, 0f, 0.seconds)
		val shifted = buildOutgoingBundle(bones(delta), setOf(BodyPart.HIP), config, null, skeletonHeight, 0f, 0.seconds)

		val baseHips = messagePosition(assertNotNull(boneMessage(base, "Hips")))
		val shiftedHips = messagePosition(assertNotNull(boneMessage(shifted, "Hips")))

		vectorAssertEquals(baseHips + delta, shiftedHips)
	}

	@Test
	fun testFreeAnchorWithVrmAtRestMatchesHipLocalPosition() {
		val vrm = buildVrmGeometry(VrmReader(VRM_JSON))
		val bones = BodyPartMap(
			mapOf(
				BodyPart.HIP to bone(BodyPart.HIP),
				BodyPart.UPPER_WAIST to bone(BodyPart.UPPER_WAIST),
				BodyPart.LOWER_CHEST to bone(BodyPart.LOWER_CHEST),
				BodyPart.NECK to bone(BodyPart.NECK, headPosition = Vector3(0f, vrm.outputRestHeight, 0f)),
			),
		)
		val config = defaultConfig.copy(anchorAtHips = false)

		val bundle = buildOutgoingBundle(bones, setOf(BodyPart.HIP), config, vrm, vrm.outputRestHeight, 0f, 0.seconds)

		val hips = assertNotNull(boneMessage(bundle, "Hips"))
		vectorAssertEquals(vrm.hipLocalPosition, messagePosition(hips))
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
