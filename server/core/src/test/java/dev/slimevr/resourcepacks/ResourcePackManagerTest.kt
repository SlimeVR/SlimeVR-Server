package dev.slimevr.resourcepacks

import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ResourcePackManagerTest {
	@Test
	fun `manager orders valid user packs and isolates failures`() = runBlocking {
		val storage = MemoryStorage(
			files = mapOf(
				"resourcepacks/20-custom/manifest.json" to manifest("example:custom"),
				"resourcepacks/10-base/manifest.json" to manifest("example:base"),
				"resourcepacks/30-invalid/manifest.json" to "{}",
				"resourcepacks/40-no-manifest/data/bones/ignored.json" to "{}",
				"resourcepacks/50-symlink/manifest.json" to manifest("example:symlink"),
			),
			symlinks = setOf("resourcepacks/50-symlink/data/bones/linked.json"),
		)

		val catalog = ResourcePackManager.load(storage, javaClass.classLoader)

		assertEquals(listOf("example:base", "example:custom"), catalog.userPacks.map { it.manifest.value.id })
		assertEquals(listOf("30-invalid", "50-symlink"), catalog.failures.map { it.folder })
		assertTrue(storage.createdResourcePackDirectory)
	}

	@Test
	fun `broken packs fall back to a working core skeleton`() = runBlocking {
		data class Case(val name: String, val files: Map<String, String>, val diagnosticContains: String)
		val cases = listOf(
			Case(
				"malformed bone",
				mapOf(
					"resourcepacks/bad/manifest.json" to manifest("example:bad"),
					"resourcepacks/bad/data/example/bones/bad.json" to "{}",
				),
				"example:bad",
			),
			Case(
				"bone with an unknown parent",
				mapOf(
					"resourcepacks/bad/manifest.json" to manifest("example:bad"),
					"resourcepacks/bad/data/example/bones/bad.json" to """{"key":"example:bad","nameKey":"example:bad","parent":"example:missing"}""",
				),
				"example:bad",
			),
			Case(
				"pack ordering cycle",
				mapOf("resourcepacks/bad/manifest.json" to manifest("example:bad").dropLast(1) + ",\"before\":[\"slimevr:core\"]}"),
				"cycle",
			),
		)
		for (case in cases) {
			val loaded = ResourcePackManager.loadCompiled(MemoryStorage(case.files), javaClass.classLoader)
			assertEquals(CORE_BONE_COUNT, loaded.skeleton.registry.maxId, case.name)
			assertTrue(loaded.catalog.userPacks.isEmpty(), case.name)
			assertTrue(loaded.catalog.failures.flatMap { it.diagnostics }.any { case.diagnosticContains in it.message || case.diagnosticContains in it.path }, case.name)
		}
	}

	@Test
	fun `an unusable resourcepacks directory falls back to core with no failures`() = runBlocking {
		val catalog = ResourcePackManager.load(MemoryStorage(emptyMap(), ensureDirectoryResult = false), javaClass.classLoader)
		assertTrue(catalog.userPacks.isEmpty())
		assertTrue(catalog.failures.isEmpty())
	}
}
