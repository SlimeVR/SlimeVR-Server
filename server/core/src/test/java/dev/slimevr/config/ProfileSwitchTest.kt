package dev.slimevr.config

import dev.slimevr.TestAppContext
import dev.slimevr.VRServer
import dev.slimevr.buildTestAppContext
import dev.slimevr.buildTestSkeleton
import dev.slimevr.buildTestTracker
import dev.slimevr.buildTestVrServerStub
import dev.slimevr.skeleton.Skeleton
import dev.slimevr.skeleton.computeAllDefaultProportionsByBone
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import dev.slimevr.tracker.behaviours.TrackerConfigBehaviour
import dev.slimevr.vmc.VMCInputBehaviour
import dev.slimevr.vmc.VMCManager
import dev.slimevr.vmc.VMCVrmBehaviour
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.ProfileKind
import solarxr_protocol.rpc.ProfileOperationStatus
import solarxr_protocol.rpc.VMCOSCInputState
import solarxr_protocol.rpc.VMCOSCVrmState
import java.net.DatagramSocket
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

private fun settingsFile(name: String) = "settings/$name.json"
private fun userFile(name: String) = "user/$name.json"

private fun putSettings(storage: MemoryStorage, name: String, data: SettingsConfigState) {
	storage.files[settingsFile(name)] = jsonConfig.encodeToString(data)
}

private fun putUser(storage: MemoryStorage, name: String, data: UserConfigData) {
	storage.files[userFile(name)] = jsonConfig.encodeToString(data)
}

private fun freeUdpPort(): Int = DatagramSocket(0).use { it.localPort }

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileSwitchTest {
	private suspend fun buildConfig(scope: TestScope, storage: MemoryStorage) = AppConfig.create(scope.backgroundScope, storage)

	@Test
	fun `the settings every consumer reads follow the selected profile`() = runTest {
		val storage = MemoryStorage()
		putSettings(storage, "default", SettingsConfigState(trackersConfig = TrackersConfig(timeoutDelay = 10f)))
		putSettings(storage, "other", SettingsConfigState(trackersConfig = TrackersConfig(timeoutDelay = 30f)))
		val config = buildConfig(this, storage)
		val seen = mutableListOf<Float>()
		backgroundScope.launch { config.settings.context.state.collect { seen += it.data.trackersConfig.timeoutDelay } }
		runCurrent()

		config.switchProfile(ProfileKind.SETTINGS, "other")
		runCurrent()
		config.switchProfile(ProfileKind.SETTINGS, "default")
		runCurrent()

		assertEquals(listOf(10f, 30f, 10f), seen.filterIndexed { i, v -> i == 0 || v != seen[i - 1] })
		assertEquals(10f, config.settings.context.state.value.data.trackersConfig.timeoutDelay)
	}

	@Test
	fun `the selection is stored in the global config`() = runTest {
		val storage = MemoryStorage()
		putSettings(storage, "other", SettingsConfigState())
		putUser(storage, "me", UserConfigData())
		val config = buildConfig(this, storage)

		config.switchProfile(ProfileKind.SETTINGS, "other")
		config.switchProfile(ProfileKind.USER, "me")

		assertEquals("other", config.globalConfig.context.state.value.selectedSettingsProfile)
		assertEquals("me", config.globalConfig.context.state.value.selectedUserProfile)
	}

	@Test
	fun `switching to a profile that does not exist changes nothing`() = runTest {
		val config = buildConfig(this, MemoryStorage())

		assertEquals(ProfileOperationStatus.NOT_FOUND, config.switchProfile(ProfileKind.SETTINGS, "missing"))

		assertEquals("default", config.settings.context.state.value.name)
		assertEquals("default", config.globalConfig.context.state.value.selectedSettingsProfile)
	}

	@Test
	fun `a profile file that fails to load falls back to the defaults`() = runTest {
		val storage = MemoryStorage()
		storage.files[settingsFile("broken")] = "{ not json"
		val config = buildConfig(this, storage)

		assertEquals(ProfileOperationStatus.OK, config.switchProfile(ProfileKind.SETTINGS, "broken"))

		assertEquals("broken", config.settings.context.state.value.name)
		assertEquals(SettingsConfigState(), config.settings.context.state.value.data)
	}

	@Test
	fun `two switches at the same time end on one consistent profile`() = runTest {
		val storage = MemoryStorage()
		putSettings(storage, "a", SettingsConfigState())
		putSettings(storage, "b", SettingsConfigState())
		val config = buildConfig(this, storage)

		val first = backgroundScope.launch { config.switchProfile(ProfileKind.SETTINGS, "a") }
		val second = backgroundScope.launch { config.switchProfile(ProfileKind.SETTINGS, "b") }
		first.join()
		second.join()

		assertEquals(config.settings.context.state.value.name, config.globalConfig.context.state.value.selectedSettingsProfile)
	}

	@Test
	fun `a user profile without proportions gets the defaults for its height`() = runTest {
		val storage = MemoryStorage()
		storage.files[userFile("bare")] = """{"userHeight": 1.9}"""
		val config = buildConfig(this, storage)

		config.switchProfile(ProfileKind.USER, "bare")

		val data = config.userConfig.context.state.value.data
		assertEquals(1.9f, data.userHeight)
		assertEquals(computeAllDefaultProportionsByBone(1.9f), data.proportions)
	}

	@Test
	fun `the skeleton takes the proportions of the selected user profile`() = runTest {
		val storage = MemoryStorage()
		putUser(storage, "default", UserConfigData(userHeight = 1.6f, proportions = computeAllDefaultProportionsByBone(1.6f)))
		putUser(storage, "tall", UserConfigData(userHeight = 2.1f, proportions = computeAllDefaultProportionsByBone(2.1f)))
		val config = buildConfig(this, storage)
		val skeleton: Skeleton = buildTestSkeleton(backgroundScope, config.userConfig)
		runCurrent()
		val shortHeight = skeleton.context.state.value.skeletonHeight

		config.switchProfile(ProfileKind.USER, "tall")
		runCurrent()
		val tallHeight = skeleton.context.state.value.skeletonHeight
		config.switchProfile(ProfileKind.USER, "default")
		runCurrent()

		assertTrue(tallHeight > shortHeight, "tall=$tallHeight short=$shortHeight")
		assertEquals(shortHeight, skeleton.context.state.value.skeletonHeight)
	}

	@Test
	fun `a new user profile gets the default proportions applied to the skeleton`() = runTest {
		val storage = MemoryStorage()
		putUser(storage, "default", UserConfigData(userHeight = 2.1f, proportions = computeAllDefaultProportionsByBone(2.1f)))
		val config = buildConfig(this, storage)
		val skeleton = buildTestSkeleton(backgroundScope, config.userConfig)
		runCurrent()
		val tallHeight = skeleton.context.state.value.skeletonHeight

		config.createProfile(ProfileKind.USER, "fresh", copyFrom = null)
		config.switchProfile(ProfileKind.USER, "fresh")
		runCurrent()

		assertTrue(skeleton.context.state.value.skeletonHeight < tallHeight)
	}

	private fun buildTracker(scope: TestScope, config: AppConfig): Tracker {
		val server = buildTestVrServerStub(scope.backgroundScope)
		val tracker = buildTestTracker(scope.backgroundScope, buildTestAppContext(server), config.settings, id = 1)
		TrackerConfigBehaviour(config.devices, "test-1").observe(tracker)
		return tracker
	}

	@Test
	fun `switching the settings profile keeps the tracker config`() = runTest {
		val storage = MemoryStorage()
		putSettings(storage, "other", SettingsConfigState())
		val config = buildConfig(this, storage)
		val tracker = buildTracker(this, config)
		runCurrent()
		tracker.context.dispatch(TrackerActions.Update { copy(bodyPart = BodyPart.HIP) })
		runCurrent()

		config.switchProfile(ProfileKind.SETTINGS, "other")
		runCurrent()

		assertEquals(BodyPart.HIP, tracker.context.state.value.bodyPart)
		assertEquals(BodyPart.HIP, config.devices.context.state.value.trackers.getValue("test-1").bodyPart)
	}

	private fun vmcHarness(scope: TestScope, config: AppConfig): VMCManager {
		val server = buildTestVrServerStub(scope.backgroundScope)
		val skeleton = buildTestSkeleton(scope.backgroundScope)
		val appContext = object : TestAppContext() {
			override val server: VRServer = server
			override val config: AppConfig = config
			override val skeleton: Skeleton = skeleton
		}
		val manager = VMCManager.create(scope.backgroundScope)
		VMCInputBehaviour(appContext, config.settings).observe(manager)
		VMCVrmBehaviour(config.settings).observe(manager)
		return manager
	}

	@Test
	fun `vmc input starts and stops with the enabled flag of the profile`() = runTest {
		val storage = MemoryStorage()
		val port = freeUdpPort()
		putSettings(storage, "default", SettingsConfigState(vmcConfig = VMCConfig(enabled = false, portIn = port)))
		putSettings(storage, "on", SettingsConfigState(vmcConfig = VMCConfig(enabled = true, portIn = port)))
		val config = buildConfig(this, storage)
		val manager = vmcHarness(this, config)
		runCurrent()
		assertEquals(VMCOSCInputState.IDLE, manager.context.state.value.status.inputState)

		config.switchProfile(ProfileKind.SETTINGS, "on")
		runCurrent()
		assertEquals(VMCOSCInputState.LISTENING, manager.context.state.value.status.inputState)
		assertEquals(port, manager.context.state.value.status.inputPort)

		config.switchProfile(ProfileKind.SETTINGS, "default")
		runCurrent()
		assertEquals(VMCOSCInputState.IDLE, manager.context.state.value.status.inputState)
	}

	@Test
	fun `vmc input moves to the port of the selected profile`() = runTest {
		val storage = MemoryStorage()
		val first = freeUdpPort()
		val second = freeUdpPort().takeIf { it != first } ?: (first + 1)
		putSettings(storage, "default", SettingsConfigState(vmcConfig = VMCConfig(enabled = true, portIn = first)))
		putSettings(storage, "other", SettingsConfigState(vmcConfig = VMCConfig(enabled = true, portIn = second)))
		val config = buildConfig(this, storage)
		val manager = vmcHarness(this, config)
		runCurrent()
		assertEquals(first, manager.context.state.value.status.inputPort)

		config.switchProfile(ProfileKind.SETTINGS, "other")
		runCurrent()

		assertEquals(second, manager.context.state.value.status.inputPort)
		assertNotEquals(VMCOSCInputState.ERROR, manager.context.state.value.status.inputState)
	}

	@Test
	fun `the vrm follows the selected profile`() = runTest {
		val storage = MemoryStorage()
		putSettings(storage, "bad", SettingsConfigState(vmcConfig = VMCConfig(vrmJson = "{ not a vrm")))
		val config = buildConfig(this, storage)
		val manager = vmcHarness(this, config)
		runCurrent()
		assertEquals(VMCOSCVrmState.NONE, manager.context.state.value.status.vrmState)

		config.switchProfile(ProfileKind.SETTINGS, "bad")
		runCurrent()
		assertEquals(VMCOSCVrmState.ERROR, manager.context.state.value.status.vrmState)

		config.switchProfile(ProfileKind.SETTINGS, "default")
		runCurrent()
		assertEquals(VMCOSCVrmState.NONE, manager.context.state.value.status.vrmState)
	}
}
