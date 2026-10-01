package dev.slimevr.customosc

import dev.slimevr.config.CustomOscConfig
import dev.slimevr.config.CustomOscParamMapping
import dev.slimevr.config.CustomOscProfile
import dev.slimevr.config.CustomOscTrackerMapping
import dev.slimevr.config.OscAxisSource
import dev.slimevr.skeleton.BoneState
import dev.slimevr.skeleton.ZERO_VELOCITY
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import solarxr_protocol.datatypes.BodyPart

class CustomOscOutputTest {

	@Test
	fun testParamValueExtraction() {
		val boneState = BoneState(
			parentBone = null,
			bodyPart = BodyPart.UPPER_CHEST,
			headOffset = Vector3.ZERO,
			offset = Vector3.ZERO,
			rotation = Quaternion.IDENTITY,
			acceleration = Vector3.ZERO,
			headPosition = Vector3(1.2f, 3.4f, 5.6f),
			tailPosition = Vector3.ZERO,
			velocity = ZERO_VELOCITY,
		)

		assertEquals(1.2f, boneState.headPosition.x)
		assertEquals(3.4f, boneState.headPosition.y)
		assertEquals(5.6f, boneState.headPosition.z)
	}

	@Test
	fun testConfigSerialization() {
		val profile = CustomOscProfile(
			id = "test-config",
			name = "Test Config",
			enabled = true,
			address = "127.0.0.1",
			port = 9000,
			trackers = listOf(
				CustomOscTrackerMapping(
					bodyPart = BodyPart.UPPER_CHEST,
					params = listOf(
						CustomOscParamMapping(OscAxisSource.POSITION_X, "/slimevr/chest/x"),
						CustomOscParamMapping(OscAxisSource.POSITION_Y, "/slimevr/chest/y"),
						CustomOscParamMapping(OscAxisSource.POSITION_Z, "/slimevr/chest/z"),
					),
				),
			),
		)

		val config = CustomOscConfig(
			enabled = true,
			profiles = listOf(profile),
		)

		assertNotNull(config)
		assertEquals(1, config.profiles.size)
		assertEquals("test-config", config.profiles[0].id)
		assertEquals(BodyPart.UPPER_CHEST, config.profiles[0].trackers[0].bodyPart)
	}
}
