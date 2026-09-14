package dev.slimevr.resourcepacks

import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.serializer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

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

	@Test
	fun `bone remove supports every optional property and rejects identity paths`() {
		val base = BoneDefinition(
			key = "example:bone", nameKey = "name", mirror = "example:mirror", batterySources = listOf("example:battery"), candidateSources = listOf("example:candidate"),
			overridable = true, parent = "example:parent", headOffset = Offset(), tailOffset = Offset(), rotationFallback = NoRotationFallback, constraint = TwistSwingConstraint(1f, 2f),
			outputs = BoneOutputs(DriverOutput(true), VmcOutput(VmcNames(listOf("Bone"))), VrchatOutput(true, mapOf("/one" to EmitEntry(EmitSource.POSITION), "/two" to EmitEntry(EmitSource.ROTATION)))),
			inputs = BoneInputs(VrchatInput("/input")),
		)
		assertNull(base.withRemoved(listOf("mirror"))!!.mirror)
		assertNull(base.withRemoved(listOf("batterySources"))!!.batterySources)
		assertNull(base.withRemoved(listOf("candidateSources"))!!.candidateSources)
		assertNull(base.withRemoved(listOf("overridable"))!!.overridable)
		assertNull(base.withRemoved(listOf("parent"))!!.parent)
		assertNull(base.withRemoved(listOf("headOffset"))!!.headOffset)
		assertNull(base.withRemoved(listOf("tailOffset"))!!.tailOffset)
		assertNull(base.withRemoved(listOf("rotationFallback"))!!.rotationFallback)
		assertNull(base.withRemoved(listOf("constraint"))!!.constraint)
		assertNull(base.withRemoved(listOf("outputs"))!!.outputs)
		assertNull(base.withRemoved(listOf("inputs"))!!.inputs)
		assertNull(base.withRemoved(listOf("inputs", "vrchat"))!!.inputs!!.vrchat)
		assertNull(base.withRemoved(listOf("outputs", "driver"))!!.outputs!!.driver)
		assertNull(base.withRemoved(listOf("outputs", "vmc"))!!.outputs!!.vmc)
		assertNull(base.withRemoved(listOf("outputs", "vrchat"))!!.outputs!!.vrchat)
		assertNull(base.withRemoved(listOf("outputs", "vrchat", "required"))!!.outputs!!.vrchat!!.required)
		assertEquals(setOf("/two"), base.withRemoved(listOf("outputs", "vrchat", "emit", "/one"))!!.outputs!!.vrchat!!.emit.keys)
	}

	@Test
	fun `proportion remove supports every optional property and rejects required fields`() {
		val base = ProportionDefinition(key = "example:proportion", nameKey = "name", descriptionKey = "description", contributesToHeight = true, minimum = 0f, maximum = 1f, default = FixedProportionDefault(0.5f))
		assertNull(base.withRemoved("descriptionKey")!!.descriptionKey)
		assertNull(base.withRemoved("contributesToHeight")!!.contributesToHeight)
		assertNull(base.withRemoved("minimum")!!.minimum)
		assertNull(base.withRemoved("maximum")!!.maximum)
	}
}
