package dev.slimevr.bones

import solarxr_protocol.connection.BoneDefinition
import solarxr_protocol.datatypes.BodyPart
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import solarxr_protocol.connection.BoneRegistry as WireBoneRegistry

class BoneRegistryTest {
	private val registry = BoneRegistry.standard()

	@Test
	fun `standard registry resolves every standardized body part`() {
		for (bodyPart in BodyPart.entries) {
			if (bodyPart == BodyPart.NONE) continue
			val id = assertNotNull(registry[bodyPart], "Expected an ID for $bodyPart")
			assertEquals(bodyPart, registry.bodyPartOf(id))
		}
	}

	@Test
	fun `unknown ID resolves to no definition`() {
		assertNull(registry[BoneId(0xdeadu)])
	}

	@Test
	fun `accepts a multi-level parent chain`() {
		val registry = BoneRegistry.from(
			WireBoneRegistry(
				bones = listOf(
					BoneDefinition(1u, "a", null, 0u, null),
					BoneDefinition(2u, "b", null, 1u, null),
					BoneDefinition(3u, "c", null, 2u, null),
				),
			),
		)
		assertNotNull(registry[BoneId(3u)])
	}

	@Test
	fun `rejects duplicate IDs`() {
		assertFailsWith<IllegalArgumentException> {
			BoneRegistry.from(
				WireBoneRegistry(
					bones = listOf(
						BoneDefinition(1u, "a", null, 0u, null),
						BoneDefinition(1u, "b", null, 0u, null),
					),
				),
			)
		}
	}

	@Test
	fun `rejects ID 0`() {
		assertFailsWith<IllegalArgumentException> {
			BoneRegistry.from(
				WireBoneRegistry(
					bones = listOf(BoneDefinition(0u, "a", null, 0u, null)),
				),
			)
		}
	}

	@Test
	fun `rejects an unknown parent`() {
		assertFailsWith<IllegalArgumentException> {
			BoneRegistry.from(
				WireBoneRegistry(
					bones = listOf(BoneDefinition(1u, "a", null, 2u, null)),
				),
			)
		}
	}

	@Test
	fun `rejects a cycle`() {
		assertFailsWith<IllegalArgumentException> {
			BoneRegistry.from(
				WireBoneRegistry(
					bones = listOf(
						BoneDefinition(1u, "a", null, 2u, null),
						BoneDefinition(2u, "b", null, 1u, null),
					),
				),
			)
		}
	}

	@Test
	fun `rejects a bone that is its own parent`() {
		assertFailsWith<IllegalArgumentException> {
			BoneRegistry.from(
				WireBoneRegistry(
					bones = listOf(BoneDefinition(1u, "a", null, 1u, null)),
				),
			)
		}
	}

	@Test
	fun `rejects duplicate standard body parts`() {
		assertFailsWith<IllegalArgumentException> {
			BoneRegistry.from(
				WireBoneRegistry(
					bones = listOf(
						BoneDefinition(1u, "a", null, 0u, BodyPart.HIP),
						BoneDefinition(2u, "b", null, 0u, BodyPart.HIP),
					),
				),
			)
		}
	}

	@Test
	fun `rejects non-contiguous IDs`() {
		assertFailsWith<IllegalArgumentException> {
			BoneRegistry.from(
				WireBoneRegistry(
					bones = listOf(
						BoneDefinition(1u, "a", null, 0u, null),
						BoneDefinition(3u, "b", null, 0u, null),
					),
				),
			)
		}
	}

	/**
	 * [BoneRegistry.hierarchyFrom] returns a traversal cached lazily per registry rather than walked
	 * every call. That is only safe while the cached result matches a fresh walk of
	 * [BODY_PART_HIERARCHY_MAP], so this pins the two together: order included, since [buildBones]
	 * relies on a parent being visited before its children.
	 */
	private fun walk(root: BodyPart, onlyChildren: Boolean): List<Pair<BodyPart?, BodyPart>> {
		val expected = mutableListOf<Pair<BodyPart?, BodyPart>>()
		fun visit(parent: BodyPart?, bone: BodyPart, skipSelf: Boolean) {
			if (!skipSelf) expected += parent to bone
			for (child in BODY_PART_HIERARCHY_MAP[bone] ?: return) visit(bone, child, false)
		}
		visit(null, root, onlyChildren)
		return expected
	}

	@Test
	fun `cached traversal matches a fresh walk for every root`() {
		for (root in BodyPart.entries) {
			if (root == BodyPart.NONE) continue
			val rootId = requireNotNull(registry[root])
			for (onlyChildren in listOf(false, true)) {
				val actual = registry.hierarchyFrom(rootId, onlyChildren)
					.map { (parentId, boneId) -> parentId?.let { registry.bodyPartOf(it) } to requireNotNull(registry.bodyPartOf(boneId)) }
				assertEquals(
					walk(root, onlyChildren),
					actual,
					"traversal drifted for root=$root onlyChildren=$onlyChildren",
				)
			}
		}
	}

	@Test
	fun `every bone is visited after its parent`() {
		val seen = mutableSetOf<BoneId>()
		val headId = requireNotNull(registry[BodyPart.HEAD])
		for ((parent, boneId) in registry.hierarchyFrom(headId)) {
			if (parent != null) {
				assertTrue(parent in seen, "$boneId was visited before its parent $parent")
			}
			seen += boneId
		}
	}

	@Test
	fun `repeated calls hand back the same instance`() {
		val leftHandId = requireNotNull(registry[BodyPart.LEFT_HAND])
		assertSame(
			registry.hierarchyFrom(leftHandId, true),
			registry.hierarchyFrom(leftHandId, true),
			"the traversal is meant to be derived once, not rebuilt per call",
		)
	}
}
