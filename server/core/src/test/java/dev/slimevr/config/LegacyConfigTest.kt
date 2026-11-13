package dev.slimevr.config

import dev.slimevr.skeleton.computeAllDefaultProportionsByBone
import io.github.axisangles.ktmath.Quaternion
import kotlinx.coroutines.test.runTest
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.ArmsMountingResetMode
import solarxr_protocol.rpc.StayAlignedRelaxedPose
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

private val LEGACY_CONFIG = """
---
server:
  trackerPort: 6969
  useMagnetometerOnAllTrackers: true
filters:
  type: "prediction"
  amount: 1.0
vmc:
  enabled: true
  portIn: 39541
  portOut: 39538
  address: "192.168.1.20"
  anchorHip: false
  vrmJson: "{\"nodes\":[]}"
  mirrorTracking: true
skeleton:
  toggles:
    floorClip: false
    selfLocalization: true
    toeSnap: true
    enforceConstraints: false
    extendedSpine: false
  values:
    kneeTrackerAnkleAveraging: 0.6
    kneeAnkleAveraging: 0.2
  offsets:
    chestLength: 0.42
  hmdHeight: 1.72
  floorHeight: 0.02
legTweaks:
  correctionStrength: 0.8
tapDetection:
  yawResetDelay: 0.4
  fullResetDelay: 2.0
  mountingResetDelay: 1.0
  yawResetEnabled: false
  fullResetEnabled: true
  mountingResetEnabled: true
  yawResetTracker: "CHEST"
  mountingResetTracker: "WAIST"
  fullResetTracker: "LEFT_UPPER_LEG"
  setupMode: false
  yawResetTaps: 4
  fullResetTaps: 3
  mountingResetTaps: 5
  numberTrackersOverThreshold: 2
resetsConfig:
  resetMountingFeet: true
  mode: "TPOSE_DOWN"
  yawResetSmoothTime: 0.5
  saveMountingReset: true
  resetHmdPitch: true
  lastMountingMethod: "AUTOMATIC"
stayAlignedConfig:
  enabled: true
  standingRelaxedPose:
    enabled: true
    upperLegAngleInDeg: 4.5
    lowerLegAngleInDeg: -2.0
    footAngleInDeg: 10.0
  sittingRelaxedPose:
    enabled: false
    upperLegAngleInDeg: 0.0
    lowerLegAngleInDeg: 0.0
    footAngleInDeg: 0.0
  flatRelaxedPose:
    enabled: false
    upperLegAngleInDeg: 0.0
    lowerLegAngleInDeg: 0.0
    footAngleInDeg: 0.0
  setupComplete: true
hidConfig:
  trackersOverHID: true
trackers:
  udp://AA:BB:CC:DD:EE:01/0:
    customName: "Chest"
    designation: "body:chest"
    hide: false
    adjustment: null
    mountingOrientation:
      x: 0.0
      "y": 1.0
      z: -0.0
      w: -4.371139E-8
    mountingResetOrientation:
      x: 0.0
      "y": 0.3
      z: 0.0
      w: 0.95
    allowDriftCompensation: true
    shouldHaveMagEnabled: null
  udp://AA:BB:CC:DD:EE:01/1:
    customName: null
    designation: "body:waist"
    hide: false
    adjustment: null
    mountingOrientation: null
    mountingResetOrientation: null
    allowDriftCompensation: true
    shouldHaveMagEnabled: null
  udp://AA:BB:CC:DD:EE:02/0:
    designation: "body:upper_chest"
  "123456ABCDEF/0":
    designation: "body:left_foot"
  ABCDEF123456/0:
    customName: null
    designation: null
    mountingOrientation: null
  HMD:
    designation: "body:head"
  PICO_CONTROLLER_LEFT:
    designation: "body:left_hand"
knownDevices:
- "AA:BB:CC:DD:EE:01"
- "aa:bb:cc:dd:ee:02"
velocityConfig:
  sendDerivedVelocity: true
vrcOSC:
  enabled: true
overlay:
  mirrored: false
  visible: false
modelVersion: "15"
""".trimIndent()

class LegacyConfigTest {
	private val migration = assertNotNull(migrateLegacyConfig(LEGACY_CONFIG))

	@Test
	fun `listed settings are imported`() {
		val settings = migration.settings
		assertTrue(settings.trackersConfig.globalMagEnabled)
		assertTrue(settings.hidConfig.trackersOverHid)
		assertTrue(settings.stayAlignedConfig.enabled)

		val toggles = settings.skeletonConfig.toggles
		assertFalse(toggles.floorClip)
		assertTrue(toggles.mocapMode)
		assertTrue(toggles.toeSnap)
		assertFalse(toggles.enforceConstraints)
		assertEquals(SkeletonTogglesConfig().skatingCorrection, toggles.skatingCorrection)
		assertEquals(SkeletonTogglesConfig().footPlant, toggles.footPlant)
		assertEquals(0.6f, settings.skeletonConfig.ratios.interpolateUpperLegsTwistWithLowerLegs)

		val resets = settings.resetsConfig
		assertTrue(resets.resetMountingFeet)
		assertEquals(ArmsMountingResetMode.SIDE, resets.armsMountingResetMode)
		assertEquals(0.5f, resets.yawResetSmoothTime)
		assertTrue(resets.saveMountingReset)
		assertTrue(resets.resetHmdAttitude)

		val tap = settings.tapDetectionConfig
		assertEquals(0.4f, tap.yawResetDelay)
		assertEquals(2.0f, tap.fullResetDelay)
		assertEquals(1.0f, tap.mountingResetDelay)
		assertFalse(tap.yawResetEnabled)
		assertEquals(BodyPart.UPPER_CHEST, tap.yawResetBodyPart)
		assertEquals(BodyPart.LOWER_WAIST, tap.mountingResetBodyPart)
		assertEquals(BodyPart.LEFT_UPPER_LEG, tap.fullResetBodyPart)
		assertEquals(4, tap.yawResetTaps)
		assertEquals(5, tap.mountingResetTaps)
		assertEquals(2, tap.numberTrackersOverThreshold)

		val vmc = settings.vmcConfig
		assertTrue(vmc.enabled)
		assertEquals(39541, vmc.portIn)
		assertEquals(39538, vmc.portOut)
		assertEquals("192.168.1.20", vmc.address)
		assertFalse(vmc.anchorAtHips)
		assertTrue(vmc.mirrorTracking)
		assertEquals("{\"nodes\":[]}", vmc.vrmJson)
	}

	@Test
	fun `unlisted settings keep the defaults`() {
		val defaults = SettingsConfigState()
		val settings = migration.settings
		assertEquals(defaults.skeletonConfig.ratios.copy(interpolateUpperLegsTwistWithLowerLegs = 0.6f), settings.skeletonConfig.ratios)
		assertEquals(defaults.driverConfig, settings.driverConfig)
		assertEquals(defaults.vrcOscConfig, settings.vrcOscConfig)
		assertEquals(defaults.boneRoutingConfig, settings.boneRoutingConfig)
		assertEquals(defaults.trackersConfig.timeoutDelay, settings.trackersConfig.timeoutDelay)
		assertEquals(defaults.resetsConfig.mountingMethod, settings.resetsConfig.mountingMethod)
	}

	@Test
	fun `an uncalibrated height keeps the default user`() {
		val yaml = LEGACY_CONFIG.replace("hmdHeight: 1.72", "hmdHeight: -1.0").replace("floorHeight: 0.02", "floorHeight: 0.0")
		val user = assertNotNull(migrateLegacyConfig(yaml)).user
		assertEquals(UserConfigData().userHeight, user.userHeight)
		assertEquals(UserConfigData().proportions, user.proportions)
	}

	@Test
	fun `a null vrm is not imported`() {
		val yaml = LEGACY_CONFIG.replace("vrmJson: \"{\\\"nodes\\\":[]}\"", "vrmJson: null")
		assertNull(assertNotNull(migrateLegacyConfig(yaml)).settings.vmcConfig.vrmJson)
	}

	@Test
	fun `user height is the old hmd height above the floor`() {
		// hmdHeight 1.72 minus floorHeight 0.02 in the fixture
		assertEquals(1.70f, migration.user.userHeight, 0.0001f)
	}

	@Test
	fun `proportions are the defaults for the imported height`() {
		val proportions = migration.user.proportions
		assertEquals(computeAllDefaultProportionsByBone(1.70f), proportions)
		assertNotEquals(UserConfigData().proportions, proportions)
	}

	@Test
	fun `recorded stay aligned poses are imported`() {
		val poses = migration.user.stayAlignedPoses
		assertEquals(setOf(StayAlignedRelaxedPose.STANDING), poses.keys)
		assertEquals(StayAlignedPoseConfig(recorded = true, enabled = true, upperLegAngleInDeg = 4.5f, lowerLegAngleInDeg = -2.0f, footAngleInDeg = 10.0f), poses[StayAlignedRelaxedPose.STANDING])
	}

	@Test
	fun `udp and hid trackers map to their hardware ids`() {
		val trackers = migration.devices.trackers
		assertEquals(setOf("udp:AA:BB:CC:DD:EE:01:0", "udp:AA:BB:CC:DD:EE:01:1", "udp:AA:BB:CC:DD:EE:02:0", "hid:123456ABCDEF", "hid:ABCDEF123456"), trackers.keys)

		val chest = trackers.getValue("udp:AA:BB:CC:DD:EE:01:0")
		assertEquals(BodyPart.UPPER_CHEST, chest.bodyPart)
		assertEquals("Chest", chest.customName)
		assertEquals(Quaternion(-4.371139E-8f, 0f, 1f, 0f), chest.mountingOrientation)
		assertNull(chest.mountingResetOrientation)
		assertNull(chest.magEnabled)

		assertEquals(BodyPart.LOWER_WAIST, trackers.getValue("udp:AA:BB:CC:DD:EE:01:1").bodyPart)
		assertEquals(Quaternion.IDENTITY, trackers.getValue("udp:AA:BB:CC:DD:EE:01:1").mountingOrientation)
		assertEquals(BodyPart.UPPER_CHEST, trackers.getValue("udp:AA:BB:CC:DD:EE:02:0").bodyPart)
		assertEquals(BodyPart.LEFT_FOOT, trackers.getValue("hid:123456ABCDEF").bodyPart)
		assertEquals(TrackerConfig(), trackers.getValue("hid:ABCDEF123456"))
		assertEquals(setOf("AA:BB:CC:DD:EE:01", "AA:BB:CC:DD:EE:02"), migration.devices.allowedUdpDevices)
	}

	@Test
	fun `a whole glTF JSON is trimmed to what the GUI sends`() {
		val gltf = """{"asset":{"version":"2.0"},"materials":[{"name":"skin"}],"extensionsUsed":["VRMC_vrm"],"extensions":{"VRMC_vrm":{"specVersion":"1.0","humanoid":{"humanBones":{"hips":{"node":0}}}},"KHR_materials_unlit":{}},"nodes":[{"translation":[0.0,1.0,0.0]}]}"""
		val yaml = LEGACY_CONFIG.replace("vrmJson: \"{\\\"nodes\\\":[]}\"", "vrmJson: '$gltf'")
		val vrmJson = assertNotNull(assertNotNull(migrateLegacyConfig(yaml)).settings.vmcConfig.vrmJson)

		assertEquals(
			"""{"extensions":{"VRMC_vrm":{"specVersion":"1.0","humanoid":{"humanBones":{"hips":{"node":0}}}}},"extensionsUsed":["VRMC_vrm"],"nodes":[{"translation":[0.0,1.0,0.0]}]}""",
			vrmJson,
		)
	}

	@Test
	fun `a config larger than 3 MB is still read`() {
		val padding = "x".repeat(4 * 1024 * 1024)
		val yaml = LEGACY_CONFIG.replace("vrmJson: \"{\\\"nodes\\\":[]}\"", "vrmJson: '{\"nodes\":[],\"padding\":\"$padding\"}'")
		assertEquals("{\"nodes\":[]}", assertNotNull(migrateLegacyConfig(yaml)).settings.vmcConfig.vrmJson)
	}

	@Test
	fun `configs older than version 14 are skipped`() {
		assertNull(migrateLegacyConfig(LEGACY_CONFIG.replace("modelVersion: \"15\"", "modelVersion: \"13\"")))
		assertNull(migrateLegacyConfig(LEGACY_CONFIG.replace("\nmodelVersion: \"15\"", "")))
	}

	@Test
	fun `first start loads the imported config`() = runTest {
		val storage = MemoryStorage()
		storage.files[LEGACY_CONFIG_PATH] = LEGACY_CONFIG
		val config = AppConfig.create(backgroundScope, storage)

		assertEquals(migration.settings, config.settings.context.state.value.data)
		assertEquals(migration.user, config.userConfig.context.state.value.data)
		assertEquals(migration.devices, config.devices.context.state.value)
		assertEquals(LEGACY_CONFIG, storage.files[LEGACY_CONFIG_PATH])
	}

	@Test
	fun `an existing config is never overwritten by the import`() = runTest {
		val storage = MemoryStorage()
		AppConfig.create(backgroundScope, storage)
		storage.files[LEGACY_CONFIG_PATH] = LEGACY_CONFIG
		val config = AppConfig.create(backgroundScope, storage)

		assertEquals(SettingsConfigState(), config.settings.context.state.value.data)
		assertEquals(DevicesConfigState(), config.devices.context.state.value)
	}
}
