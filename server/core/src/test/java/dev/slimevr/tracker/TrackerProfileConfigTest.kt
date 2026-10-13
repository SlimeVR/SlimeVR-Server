package dev.slimevr.tracker

import dev.slimevr.config.TrackerConfig
import dev.slimevr.tracker.behaviours.TrackerConfigBehaviour
import io.github.axisangles.ktmath.Quaternion
import solarxr_protocol.datatypes.BodyPart
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TrackerProfileConfigTest {
	private val base = Tracker.DEFAULT_STATE.copy(bodyPart = BodyPart.UPPER_CHEST)

	@Test
	fun `a profile config replaces what the previous profile set`() {
		val live = base.copy(bodyPart = BodyPart.HIP, customName = "old")
		val config = TrackerConfig(bodyPart = BodyPart.LEFT_FOOT, customName = "new")

		val result = TrackerConfigBehaviour.applyProfileConfig(live, base, config, saveMountingReset = false)

		assertEquals(BodyPart.LEFT_FOOT, result.bodyPart)
		assertEquals("new", result.customName)
	}

	@Test
	fun `a tracker unknown to the profile goes back to its creation state`() {
		val live = base.copy(bodyPart = BodyPart.HIP, customName = "old", mountingOrientation = Quaternion(0f, 0f, 1f, 0f))

		val result = TrackerConfigBehaviour.applyProfileConfig(live, base, null, saveMountingReset = false)

		assertEquals(BodyPart.UPPER_CHEST, result.bodyPart)
		assertNull(result.customName)
		assertEquals(Quaternion.IDENTITY, result.mountingOrientation)
	}
}
