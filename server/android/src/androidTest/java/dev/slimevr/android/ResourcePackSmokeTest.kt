package dev.slimevr.android

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.slimevr.android.config.AndroidConfigStorage
import dev.slimevr.resourcepacks.ResourcePackManager
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class ResourcePackSmokeTest {
	@Test
	fun loadsBundledCoreAndCreatesLocalPackDirectory() = runBlocking {
		val context = ApplicationProvider.getApplicationContext<android.content.Context>()
		val loaded = ResourcePackManager.loadCompiled(
			AndroidConfigStorage(context.filesDir),
			context.javaClass.classLoader ?: ClassLoader.getSystemClassLoader(),
		)

		assertEquals("slimevr:core", loaded.catalog.core.manifest.value.id)
		assertEquals(61, loaded.skeleton.registry.maxId)
		assertTrue(File(context.filesDir, "resourcepacks").isDirectory)
	}
}
