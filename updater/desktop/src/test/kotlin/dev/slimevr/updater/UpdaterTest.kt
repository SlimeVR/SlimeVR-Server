package dev.slimevr.updater

import dev.slimevr.updater.ManifestUtils.Companion.getChannels
import dev.slimevr.updater.ManifestUtils.Companion.getCurrentVersion
import dev.slimevr.updater.ManifestUtils.Companion.getRelease
import dev.slimevr.updater.ManifestUtils.Companion.getVersionTags
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class UpdaterTest {

	@Test
	fun testArguments() {
	}

	@Test
	fun testUpdaterGUI() {
		val updaterController = UpdaterController()
		updaterController.startGui()
	}

	@Test
	fun `test update process`(): Unit = runTest {
		val updaterController = UpdaterController()
		val result = updaterController.startGui()
	}

	@Test
	fun testDownload() {
	}

	@Test
	fun testGetChannels() {
		val manifest = Manifest().getManifest()
		val channels = getChannels(manifest)
		println(channels)
	}

	@Test
	fun testGetVersionTags() {
		val manifest = Manifest().getManifest()
		val stableVersionTags = getVersionTags(manifest, "stable")
		println(stableVersionTags)
	}

	@Test
	fun testGetPlatforms() {
		val manifest = Manifest().getManifest()
	}

	@Test
	fun testGetArbitraryRelease() {
		val manifest = Manifest().getManifest()
		val release = getRelease(manifest, "stable", "v19.0.0", "windows", "x86_64")
		println(release?.url)
	}

	@Test
	fun testGetLatestStable() {
		val manifest = Manifest().getManifest()
		val currentVersion = getCurrentVersion(manifest, "windows", "x86_64")
		println(currentVersion)
	}

	@Test
	fun downgrade() {
	}

	@Test
	fun testUpdateToLatestStable() {
		val state = UpdaterState()
		val os = System.getProperty("os.name").lowercase()
		val arch = System.getProperty("os.arch").lowercase()
		val normalizedArch = when {
			arch.contains("amd64") || arch.contains("x86_64") -> "x86_64"
			arch.contains("arm") -> "arm64"
			else -> arch
		}

		val updateController = UpdaterController()
		updateController.startGui()
		val featureFlags = FeatureFlags()
		val manifest = Manifest().getManifest()
		val currentVersion = getCurrentVersion(manifest, os, normalizedArch) ?: return
		println("Current Version for $os ($normalizedArch): $currentVersion")
	}

	@Test
	fun updateToSpecificVersion() {
	}
}
