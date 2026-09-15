package dev.slimevr.resourcepacks

import dev.slimevr.resourcepacks.compiler.ResourcePackCompilationException
import dev.slimevr.testCoreResourcePack
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ResourcePackOrderingTest {
	@Test
	fun `before and after order packs independently of discovery order`() = runTest {
		val first = testPack("example:z", before = listOf("example:m"))
		val middle = testPack("example:m")
		val last = testPack("example:a", after = listOf("example:m"))
		for (packs in listOf(listOf(first, middle, last), listOf(last, first, middle))) {
			val ordered = orderResourcePacks(ResourcePackCatalog(testCoreResourcePack, packs, emptyList()))
			assertEquals(listOf("example:z", "example:m", "example:a"), ordered.userPacks.map { it.manifest.value.id })
		}
	}

	@Test
	fun `absent ordering targets are optional and ties use pack IDs`() = runTest {
		val z = testPack("example:z", before = listOf("example:absent"))
		val a = testPack("example:a", after = listOf("example:absent"))
		val ordered = orderResourcePacks(ResourcePackCatalog(testCoreResourcePack, listOf(z, a), emptyList()))
		assertEquals(listOf(a, z), ordered.userPacks)
	}

	@Test
	fun `cycles self ordering, ordering before core and duplicate IDs are rejected`() = runTest {
		val a = testPack("example:a", after = listOf("example:b"))
		val b = testPack("example:b", after = listOf("example:a"))
		val self = testPack("example:self", before = listOf("example:self"))
		val beforeCore = testPack("example:before", before = listOf("slimevr:core"))
		val duplicate = testPack("example:duplicate")
		val cases = listOf(
			listOf(a, b) to "cycle",
			listOf(self) to "cycle",
			listOf(beforeCore) to "cycle",
			listOf(duplicate, duplicate) to "Duplicate pack ID",
		)
		for ((packs, expected) in cases) {
			val error = assertFailsWith<ResourcePackCompilationException> {
				orderResourcePacks(ResourcePackCatalog(testCoreResourcePack, packs, emptyList()))
			}
			assertTrue(error.diagnostics.all { it.path == "manifest.json" && expected in it.message }, expected)
		}
	}
}
