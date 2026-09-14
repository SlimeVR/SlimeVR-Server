package dev.slimevr.resourcepacks

import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.serializer
import kotlin.test.Test
import kotlin.test.assertEquals

private fun SerialDescriptor.fieldNames(): Set<String> = (0 until elementsCount).map(::getElementName).toSet()

class ResourcePackOverrideTest {
	@Test
	fun `bone override set covers every overridable definition field`() {
		assertEquals(
			serializer<BoneDefinition>().descriptor.fieldNames() - setOf("\$schema", "key"),
			serializer<BoneOverrideSet>().descriptor.fieldNames(),
		)
	}

	@Test
	fun `proportion override set covers every overridable definition field`() {
		assertEquals(
			serializer<ProportionDefinition>().descriptor.fieldNames() - setOf("\$schema", "key"),
			serializer<ProportionOverrideSet>().descriptor.fieldNames(),
		)
	}

	@Test
	fun `bone set applies every scalar definition property`() {
		val base = BoneDefinition(key = "example:bone", nameKey = "old")
		val set = BoneOverrideSet(
			nameKey = "new", mirror = "example:mirror", batterySources = listOf("example:battery"), candidateSources = listOf("example:candidate"),
			overridable = true, parent = "example:parent", headOffset = Offset(base = Vector3(1f, 2f, 3f)), tailOffset = Offset(base = Vector3(4f, 5f, 6f)),
			rotationFallback = CopyRotationFallback("example:fallback"), constraint = TwistSwingConstraint(10f, 20f), inputs = BoneInputs(VrchatInput("/example/input")),
		)
		val result = base.withSet(set)
		assertEquals(set.nameKey, result.nameKey)
		assertEquals(set.mirror, result.mirror)
		assertEquals(set.batterySources, result.batterySources)
		assertEquals(set.candidateSources, result.candidateSources)
		assertEquals(set.overridable, result.overridable)
		assertEquals(set.parent, result.parent)
		assertEquals(set.headOffset, result.headOffset)
		assertEquals(set.tailOffset, result.tailOffset)
		assertEquals(set.rotationFallback, result.rotationFallback)
		assertEquals(set.constraint, result.constraint)
		assertEquals(set.inputs, result.inputs)
	}

	@Test
	fun `proportion set applies every definition property`() {
		val base = ProportionDefinition(key = "example:proportion", nameKey = "old", default = FixedProportionDefault(1f))
		val set = ProportionOverrideSet("new", "description", true, 0.1f, 2f, HeightRatioProportionDefault(0.5f))
		val result = base.withSet(set)
		assertEquals(set.nameKey, result.nameKey)
		assertEquals(set.descriptionKey, result.descriptionKey)
		assertEquals(set.contributesToHeight, result.contributesToHeight)
		assertEquals(set.minimum, result.minimum)
		assertEquals(set.maximum, result.maximum)
		assertEquals(set.default, result.default)
	}
}
