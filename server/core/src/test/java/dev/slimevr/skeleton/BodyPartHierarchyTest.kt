package dev.slimevr.skeleton

import solarxr_protocol.datatypes.BodyPart
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * [iterateBodyPartHierarchy] returns a traversal derived once at class init rather than walked per
 * call. That is only safe while the cached result matches a fresh walk of [BODY_PART_HIERARCHY_MAP],
 * so this pins the two together: order included, since [buildBones] relies on a parent being visited
 * before its children.
 */
class BodyPartHierarchyTest {

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
			for (onlyChildren in listOf(false, true)) {
				assertEquals(
					walk(root, onlyChildren),
					iterateBodyPartHierarchy(root, onlyChildren),
					"traversal drifted for root=$root onlyChildren=$onlyChildren",
				)
			}
		}
	}

	@Test
	fun `every bone is visited after its parent`() {
		val seen = mutableSetOf<BodyPart>()
		for ((parent, bone) in iterateBodyPartHierarchy()) {
			if (parent != null) {
				assertTrue(parent in seen, "$bone was visited before its parent $parent")
			}
			seen += bone
		}
	}

	@Test
	fun `repeated calls hand back the same instance`() {
		assertSame(
			iterateBodyPartHierarchy(BodyPart.LEFT_HAND, true),
			iterateBodyPartHierarchy(BodyPart.LEFT_HAND, true),
			"the traversal is meant to be derived once, not rebuilt per call",
		)
	}
}
