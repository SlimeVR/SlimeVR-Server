package dev.slimevr.bones

import kotlinx.coroutines.test.runTest
import solarxr_protocol.connection.BoneDefinition
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class BoneRegistryManagerTest {
	@Test
	fun `RegisterBones before freeze appends above every existing ID`() = runTest {
		val manager = BoneRegistryManager.create(backgroundScope)
		val maxIdBefore = manager.current.maxId

		manager.register(listOf(BoneDefinition(id = 0u, key = "test:extension", parent = manager.current.root.value)))

		val registry = manager.current
		assertEquals(maxIdBefore + 1, registry.maxId)
		val extensionId = requireNotNull(registry["test:extension"])
		assertEquals(maxIdBefore + 1, extensionId.value.toInt())
	}

	@Test
	fun `RegisterBones leaves every standard ID untouched`() = runTest {
		val manager = BoneRegistryManager.create(backgroundScope)
		val before = manager.current

		manager.register(listOf(BoneDefinition(id = 0u, key = "test:extension", parent = before.root.value)))

		val after = manager.current
		for (bodyPart in BodyPart.entries) {
			if (bodyPart == BodyPart.NONE) continue
			assertEquals(before[bodyPart], after[bodyPart], "$bodyPart's ID moved after registering an extension bone")
		}
	}

	@Test
	fun `RegisterBones after Freeze throws`() = runTest {
		val manager = BoneRegistryManager.create(backgroundScope)
		manager.freeze()

		assertFailsWith<IllegalArgumentException> {
			manager.register(listOf(BoneDefinition(id = 0u, key = "test:extension", parent = manager.current.root.value)))
		}
	}

	@Test
	fun `a registered extension bone resolves by key and by id and has no body part`() = runTest {
		val manager = BoneRegistryManager.create(backgroundScope)
		manager.register(listOf(BoneDefinition(id = 0u, key = "test:extension", parent = manager.current.root.value)))

		val registry = manager.current
		val id = requireNotNull(registry["test:extension"])
		assertEquals("test:extension", registry.keyOf(id))
		assertNull(registry.bodyPartOf(id))
	}
}
