package dev.slimevr.config

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ProfilesTest {
	private suspend fun buildConfig(scope: TestScope, storage: MemoryStorage = MemoryStorage()) = AppConfig.create(scope.backgroundScope, storage)

	@Test
	fun `a new profile starts from the defaults and is not selected`() = runTest {
		val config = buildConfig(this)
		config.settings.context.dispatch(SettingsActions.Update { copy(trackersConfig = trackersConfig.copy(timeoutDelay = 99f)) })
		runCurrent()

		assertEquals(ProfileStatus.OK, config.createProfile(ProfileKind.SETTINGS, "second", copyFrom = null))

		assertEquals("default", config.settings.context.state.value.name)
		assertEquals(listOf("default", "second"), config.profiles.value.settings)
		config.switchProfile(ProfileKind.SETTINGS, "second")
		assertEquals(TrackersConfig().timeoutDelay, config.settings.context.state.value.data.trackersConfig.timeoutDelay)
	}

	@Test
	fun `copying the active profile includes edits still waiting to be saved`() = runTest {
		val config = buildConfig(this)
		config.settings.context.dispatch(SettingsActions.Update { copy(trackersConfig = trackersConfig.copy(timeoutDelay = 99f)) })
		runCurrent()

		assertEquals(ProfileStatus.OK, config.createProfile(ProfileKind.SETTINGS, "copy", copyFrom = "default"))

		config.switchProfile(ProfileKind.SETTINGS, "copy")
		assertEquals(99f, config.settings.context.state.value.data.trackersConfig.timeoutDelay)
	}

	@Test
	fun `names are validated and compared case-insensitively`() = runTest {
		val config = buildConfig(this)

		assertEquals(ProfileStatus.INVALID_NAME, config.createProfile(ProfileKind.USER, "../evil", copyFrom = null))
		assertEquals(ProfileStatus.INVALID_NAME, config.createProfile(ProfileKind.USER, "", copyFrom = null))
		assertEquals(ProfileStatus.ALREADY_EXISTS, config.createProfile(ProfileKind.USER, "DEFAULT", copyFrom = null))
		assertEquals(ProfileStatus.NOT_FOUND, config.createProfile(ProfileKind.USER, "x", copyFrom = "missing"))
	}

	@Test
	fun `the active profile cannot be deleted`() = runTest {
		val config = buildConfig(this)
		config.createProfile(ProfileKind.SETTINGS, "other", copyFrom = null)

		assertEquals(ProfileStatus.IS_ACTIVE, config.deleteProfile(ProfileKind.SETTINGS, "default"))
		assertEquals(ProfileStatus.OK, config.deleteProfile(ProfileKind.SETTINGS, "other"))
		assertEquals(listOf("default"), config.profiles.value.settings)
		assertEquals(ProfileStatus.NOT_FOUND, config.deleteProfile(ProfileKind.SETTINGS, "other"))
	}

	@Test
	fun `renaming the active profile moves its file and the selection`() = runTest {
		val storage = MemoryStorage()
		val config = buildConfig(this, storage)

		assertEquals(ProfileStatus.OK, config.renameProfile(ProfileKind.USER, "default", "me"))
		runCurrent()

		assertEquals("me", config.userConfig.context.state.value.name)
		assertEquals("me", config.globalConfig.context.state.value.selectedUserProfile)
		assertTrue("user/me.json" in storage.files)
		assertTrue("user/default.json" !in storage.files)
		assertEquals(listOf("me"), config.profiles.value.user)
	}

	@Test
	fun `switching saves the pending edits of the profile being left`() = runTest {
		val storage = MemoryStorage()
		val config = buildConfig(this, storage)
		config.createProfile(ProfileKind.SETTINGS, "other", copyFrom = null)
		config.settings.context.dispatch(SettingsActions.Update { copy(trackersConfig = trackersConfig.copy(timeoutDelay = 42f)) })
		runCurrent()

		config.switchProfile(ProfileKind.SETTINGS, "other")

		assertTrue(storage.files.getValue("settings/default.json").contains("42.0"))
	}

	@Test
	fun `a hand-edited global config cannot select a profile outside the profile directory`() = runTest {
		val storage = MemoryStorage()
		storage.files["global.json"] = """{"selectedSettingsProfile": "../escape", "selectedUserProfile": "a/b"}"""

		val config = buildConfig(this, storage)

		assertEquals("default", config.settings.context.state.value.name)
		assertEquals("default", config.userConfig.context.state.value.name)
	}
}
