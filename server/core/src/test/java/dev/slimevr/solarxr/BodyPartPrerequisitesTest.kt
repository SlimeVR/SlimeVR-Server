package dev.slimevr.solarxr

import dev.slimevr.solarxr.rpc.TrackingPoint
import dev.slimevr.solarxr.rpc.PREREQUISITE_BODY_PARTS
import dev.slimevr.solarxr.rpc.bodyPartPrerequisites
import dev.slimevr.solarxr.rpc.ikSolvedParts
import solarxr_protocol.datatypes.BodyPart
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * [bodyPartPrerequisites] is read off the skeleton hierarchy and the imputing processors rather
 * than written out by hand, so this pins what that derivation produces. The GUI drives which dots
 * it offers and which assignments it warns about from the result.
 */
class BodyPartPrerequisitesTest {

	private val spineParts = listOf(
		BodyPart.UPPER_CHEST,
		BodyPart.LOWER_CHEST,
		BodyPart.UPPER_WAIST,
		BodyPart.LOWER_WAIST,
		BodyPart.HIP,
	)

	@Test
	fun `every bone is asked about except the unassignable one`() {
		for (bodyPart in spineParts + BodyPart.HEAD + BodyPart.LEFT_BIG_TOE + BodyPart.LEFT_THUMB_PROXIMAL) {
			assertContains(PREREQUISITE_BODY_PARTS, bodyPart)
		}
		assertFalse(BodyPart.NONE in PREREQUISITE_BODY_PARTS)
	}

	@Test
	fun `a digit needs the hand or foot it hangs off, never a sibling digit`() {
		// the finger processors impute each phalanx from its siblings, so one is no use as a
		// prerequisite for another
		assertEquals(
			listOf(
				listOf(BodyPart.LEFT_HAND),
				listOf(BodyPart.LEFT_LOWER_ARM),
				listOf(BodyPart.LEFT_UPPER_ARM),
				spineParts,
			),
			bodyPartPrerequisites(BodyPart.LEFT_INDEX_INTERMEDIATE),
		)
		assertEquals(
			listOf(
				listOf(BodyPart.LEFT_FOOT),
				listOf(BodyPart.LEFT_LOWER_LEG),
				listOf(BodyPart.LEFT_UPPER_LEG),
				spineParts,
			),
			bodyPartPrerequisites(BodyPart.LEFT_BIG_TOE),
		)
	}

	@Test
	fun `every part resolves and no requirement is empty`() {
		for (bodyPart in BodyPart.entries) {
			for (requirement in bodyPartPrerequisites(bodyPart)) {
				assertTrue(requirement.isNotEmpty(), "$bodyPart has an empty requirement")
			}
		}
	}

	@Test
	fun `the head roots the skeleton and the neck hangs off it`() {
		assertEquals(emptyList(), bodyPartPrerequisites(BodyPart.HEAD))
		assertEquals(emptyList(), bodyPartPrerequisites(BodyPart.NECK))
	}

	@Test
	fun `the top of the spine stands on its own and the rest of it looks upwards`() {
		assertEquals(emptyList(), bodyPartPrerequisites(BodyPart.UPPER_CHEST))
		assertEquals(listOf(listOf(BodyPart.UPPER_CHEST)), bodyPartPrerequisites(BodyPart.LOWER_CHEST))
		assertEquals(
			listOf(listOf(BodyPart.UPPER_CHEST, BodyPart.LOWER_CHEST, BodyPart.UPPER_WAIST, BodyPart.LOWER_WAIST)),
			bodyPartPrerequisites(BodyPart.HIP),
		)
	}

	@Test
	fun `a limb needs every segment above it plus a spine reference`() {
		assertEquals(listOf(spineParts), bodyPartPrerequisites(BodyPart.LEFT_UPPER_LEG))
		assertEquals(
			listOf(listOf(BodyPart.LEFT_UPPER_LEG), spineParts),
			bodyPartPrerequisites(BodyPart.LEFT_LOWER_LEG),
		)
		assertEquals(
			listOf(listOf(BodyPart.RIGHT_LOWER_LEG), listOf(BodyPart.RIGHT_UPPER_LEG), spineParts),
			bodyPartPrerequisites(BodyPart.RIGHT_FOOT),
		)
	}

	@Test
	fun `a shoulder and an upper arm only need a spine reference`() {
		assertEquals(listOf(spineParts), bodyPartPrerequisites(BodyPart.LEFT_SHOULDER))
		assertEquals(listOf(spineParts), bodyPartPrerequisites(BodyPart.LEFT_UPPER_ARM))
	}

	@Test
	fun `a shoulder is linked to the spine so nothing past it waits on one`() {
		assertEquals(
			listOf(listOf(BodyPart.LEFT_LOWER_ARM), listOf(BodyPart.LEFT_UPPER_ARM), spineParts),
			bodyPartPrerequisites(BodyPart.LEFT_HAND),
		)
	}

	@Test
	fun `bones the IK solves are not asked for, and the spine reference still is`() {
		// what a positional left hand aims the IK at, per BODY_PART_IK_CHAIN_MAP
		val leftArm = setOf(BodyPart.LEFT_UPPER_ARM, BodyPart.LEFT_LOWER_ARM)

		assertEquals(listOf(spineParts), bodyPartPrerequisites(BodyPart.LEFT_HAND, leftArm))

		// the other hand is on its own, so it still wants its arm tracked
		assertEquals(
			listOf(listOf(BodyPart.RIGHT_LOWER_ARM), listOf(BodyPart.RIGHT_UPPER_ARM), spineParts),
			bodyPartPrerequisites(BodyPart.RIGHT_HAND, leftArm),
		)

		// a spine group is never one the arm IK covers
		assertEquals(
			listOf(spineParts),
			bodyPartPrerequisites(BodyPart.LEFT_UPPER_ARM, leftArm),
		)
	}

	@Test
	fun `a controller hands its whole arm chain to the IK`() {
		val leftController = TrackingPoint(BodyPart.LEFT_HAND, hasPosition = true)

		assertEquals(
			setOf(BodyPart.LEFT_UPPER_ARM, BodyPart.LEFT_LOWER_ARM),
			ikSolvedParts(listOf(leftController), useTrackerPositions = true),
		)
	}

	@Test
	fun `an upper arm tracker does not take the arm back off the IK`() {
		// a hand position and an upper arm rotation place the elbow between them, so the
		// forearm is still worked out and asking for one would be the wrong advice
		val trackers = listOf(
			TrackingPoint(BodyPart.LEFT_HAND, hasPosition = true),
			TrackingPoint(BodyPart.LEFT_UPPER_ARM, hasPosition = false),
		)

		val solved = ikSolvedParts(trackers, useTrackerPositions = true)
		assertContains(solved, BodyPart.LEFT_LOWER_ARM)

		// only the spine reference is left to ask for, never the forearm
		assertEquals(
			listOf(spineParts),
			bodyPartPrerequisites(BodyPart.LEFT_HAND, solved),
		)
	}

	@Test
	fun `nothing is handed to the IK without a controller or with positions switched off`() {
		val leftController = TrackingPoint(BodyPart.LEFT_HAND, hasPosition = true)
		val imuHand = TrackingPoint(BodyPart.LEFT_HAND, hasPosition = false)

		assertEquals(emptySet(), ikSolvedParts(listOf(leftController), useTrackerPositions = false))
		assertEquals(emptySet(), ikSolvedParts(listOf(imuHand), useTrackerPositions = true))
		assertEquals(emptySet(), ikSolvedParts(emptyList(), useTrackerPositions = true))
	}
}
