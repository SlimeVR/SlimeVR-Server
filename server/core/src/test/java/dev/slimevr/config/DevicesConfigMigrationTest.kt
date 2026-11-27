package dev.slimevr.config

import kotlinx.coroutines.test.runTest
import solarxr_protocol.datatypes.BodyPart
import kotlin.test.Test
import kotlin.test.assertEquals

class DevicesConfigMigrationTest {
	@Test
	fun `version 1 tracker ids get the prefix of their origin`() = runTest {
		val storage = MemoryStorage()
		storage.files[DEVICES_CONFIG_PATH] = """
			{
				"version": 1,
				"allowedUdpDevices": ["AA:BB:CC:DD:EE:01"],
				"trackers": {
					"AA:BB:CC:DD:EE:01:0": { "bodyPart": "HIP" },
					"192.168.1.20:1": { "bodyPart": "LEFT_FOOT" },
					"A1B2C3D4E5F6": { "bodyPart": "RIGHT_FOOT" },
					"0000A1B2C3D4E5F6": { "bodyPart": "LEFT_HAND" },
					"LHR-1A2B3C4D": { "bodyPart": "LOWER_CHEST" },
					"vmc:tracker:abc": { "bodyPart": "HEAD" },
					"vrcosc:vrsystem:head": { "bodyPart": "NECK" }
				}
			}
		""".trimIndent()

		val devices = AppConfig.create(backgroundScope, storage).devices.context.state.value

		assertEquals(
			mapOf(
				"udp:AA:BB:CC:DD:EE:01:0" to BodyPart.HIP,
				"udp:192.168.1.20:1" to BodyPart.LEFT_FOOT,
				"hid:A1B2C3D4E5F6" to BodyPart.RIGHT_FOOT,
				"hid:0000A1B2C3D4E5F6" to BodyPart.LEFT_HAND,
				"driver:LHR-1A2B3C4D" to BodyPart.LOWER_CHEST,
				"vmc:tracker:abc" to BodyPart.HEAD,
				"vrcosc:vrsystem:head" to BodyPart.NECK,
			),
			devices.trackers.mapValues { it.value.bodyPart },
		)
		assertEquals(setOf("AA:BB:CC:DD:EE:01"), devices.allowedUdpDevices)
		assertEquals(2, devices.version)
	}
}
