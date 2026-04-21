package dev.slimevr.updater

import dev.slimevr.updater.ManifestUtils.Companion.getRelease
import dev.slimevr.updater.ManifestUtils.Companion.listChannels
import dev.slimevr.updater.ManifestUtils.Companion.listVersions
import kotlin.test.Test

class UpdaterTest {

	@Test
	fun testArguments() {

	}

	@Test
	fun testUpdater() {
		val updater = Updater()
		updater.runUpdater()
	}

	@Test
	fun testDownload() {

	}

	@Test
	fun testListVersions() {
		val manifest = Manifest().getManifest()
		listVersions(manifest, "stable")
		println("/------------------------------------------")
		listVersions(manifest, "beta")
	}

	@Test
	fun testListChannels() {
		val manifest = Manifest().getManifest()
		listChannels(manifest)
	}

	@Test
	fun testGetLatestWindowsRelease() {
		val manifest = Manifest().getManifest()
		val release = getRelease(manifest, "stable", "v19.0.0", "windows", "x86_64")
		println(release?.url)
	}

	@Test
	fun downgrade() {

	}

	@Test
	fun updateToSpecificVersion() {

	}

}
