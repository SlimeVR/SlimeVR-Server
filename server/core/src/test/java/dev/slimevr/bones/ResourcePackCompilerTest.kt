package dev.slimevr.bones

import dev.slimevr.resourcepacks.ClasspathResourcePackSource
import dev.slimevr.resourcepacks.InMemoryResourcePackSource
import dev.slimevr.resourcepacks.ResourcePackCatalog
import dev.slimevr.resourcepacks.ResourcePackParser
import io.github.axisangles.ktmath.Vector3
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

private const val EPSILON = 1e-6f

private fun assertVectorEquals(expected: Vector3, actual: Vector3?, message: String) {
	assertTrue(actual != null && (expected - actual).lenSq() < EPSILON, "$message: expected $expected, got $actual")
}

private fun assertFloatEquals(expected: Float, actual: Float, message: String) {
	assertTrue(kotlin.math.abs(expected - actual) < EPSILON, "$message: expected $expected, got $actual")
}

class ResourcePackCompilerTest {
	@Test
	fun `bundled core pack compiles to the standard bone registry`() = runTest {
		val core = ResourcePackParser().parse(ClasspathResourcePackSource.core(javaClass.classLoader))
		assertEquals(BoneRegistry.standard().value, compileResourcePacks(catalog(core)).registry.value)
	}

	@Test
	fun `bundled core pack compiles 18 proportions, 8 of which contribute to height`() = runTest {
		val core = ResourcePackParser().parse(ClasspathResourcePackSource.core(javaClass.classLoader))
		val definition = compileResourcePacks(catalog(core))
		assertEquals(18, definition.proportions.size)
		assertEquals(
			setOf("slimevr:neck", "slimevr:upper_chest", "slimevr:lower_chest", "slimevr:upper_waist", "slimevr:lower_waist", "slimevr:hip", "slimevr:upper_leg", "slimevr:lower_leg"),
			definition.proportions.filterValues { it.contributesToHeight }.keys,
		)
	}

	@Test
	fun `bundled core pack's default proportions reproduce the reference height`() = runTest {
		val core = ResourcePackParser().parse(ClasspathResourcePackSource.core(javaClass.classLoader))
		val definition = compileResourcePacks(catalog(core))
		val defaults = definition.defaultProportionValues()
		assertFloatEquals(REFERENCE_HEIGHT, definition.height(defaults), "height")
	}

	@Test
	fun `bundled core pack resolves bone offsets from default proportions`() = runTest {
		val core = ResourcePackParser().parse(ClasspathResourcePackSource.core(javaClass.classLoader))
		val definition = compileResourcePacks(catalog(core))
		val offsets = definition.toBoneOffsets(definition.defaultProportionValues())
		val registry = definition.registry

		assertEquals(61, offsets.tail.entries.size)
		assertEquals(22, offsets.head.entries.size)

		fun tail(key: String) = offsets.tail[registry[key]!!]
		fun head(key: String) = offsets.head[registry[key]!!]

		assertVectorEquals(Vector3(0f, -0.04f, 0f), tail("slimevr:hip"), "hip tail")
		assertVectorEquals(Vector3(-0.13f, 0f, 0f), head("slimevr:left_upper_leg"), "left_upper_leg head")
		assertVectorEquals(Vector3(0f, -0.42f, 0f), tail("slimevr:left_upper_leg"), "left_upper_leg tail")
		assertVectorEquals(Vector3(-0.175f, -0.06f, 0f), tail("slimevr:left_shoulder"), "left_shoulder tail")
		assertVectorEquals(Vector3(0f, -0.5f, 0.05f), tail("slimevr:left_lower_leg"), "left_lower_leg tail")
		assertVectorEquals(Vector3(0f, -0.08f, 0f), tail("slimevr:left_hand"), "left_hand tail")
		assertVectorEquals(Vector3(0f, 0f, 0.1f), tail("slimevr:head"), "head tail")
		assertVectorEquals(Vector3(0.0364f, 0f, 0f), head("slimevr:left_big_toe"), "left_big_toe head")
		assertVectorEquals(Vector3(0f, 0f, -0.0351f), tail("slimevr:left_big_toe"), "left_big_toe tail")
		assertVectorEquals(Vector3(0.0128f, 0.024f, -0.0224f), head("slimevr:left_thumb_metacarpal"), "left_thumb_metacarpal head")
		assertVectorEquals(Vector3(0.0011787056f, -0.0235741504f, -0.0165018776f), tail("slimevr:left_thumb_metacarpal"), "left_thumb_metacarpal tail")
	}

	@Test
	fun `toProportionValues is the inverse of toBoneOffsets for the bundled core pack`() = runTest {
		val core = ResourcePackParser().parse(ClasspathResourcePackSource.core(javaClass.classLoader))
		val definition = compileResourcePacks(catalog(core))
		val defaults = definition.defaultProportionValues()
		val offsets = definition.toBoneOffsets(defaults)
		val recovered = definition.toProportionValues(offsets.tail, offsets.head)
		assertEquals(defaults.keys, recovered.keys)
		for (key in defaults.keys) assertFloatEquals(defaults.getValue(key), recovered.getValue(key), key)
	}

	@Test
	fun `compiler includes user-pack bones after the core registry`() = runTest {
		val core = ResourcePackParser().parse(ClasspathResourcePackSource.core(javaClass.classLoader))
		val user = ResourcePackParser().parse(
			InMemoryResourcePackSource(
				mapOf(
					"manifest.json" to manifest("example:extra"),
					"data/bones/extra.json" to """{"key":"example:extra","nameKey":"example:bone.extra","parent":"slimevr:head"}""",
					"assets/lang/en.json" to """{"example:bone.extra":"Extra bone"}""",
				),
			),
		)
		val registry = compileResourcePacks(catalog(core, user)).registry
		assertEquals(BoneId(62u.toUShort()), registry["example:extra"])
		assertEquals(BoneId(1u.toUShort()), registry.parentOf(BoneId(62u.toUShort())))
		assertEquals("Extra bone", registry[BoneId(62u.toUShort())]?.displayName)
	}

	@Test
	fun `compiler resolves a user-pack bone's offset against a user-pack proportion`() = runTest {
		val core = ResourcePackParser().parse(ClasspathResourcePackSource.core(javaClass.classLoader))
		val user = ResourcePackParser().parse(
			InMemoryResourcePackSource(
				mapOf(
					"manifest.json" to manifest("example:extra"),
					"data/proportions/tail.json" to """{"key":"example:tail","nameKey":"example:proportion.tail","default":{"type":"fixed","value":0.3}}""",
					"data/bones/extra.json" to """{"key":"example:extra","nameKey":"example:bone.extra","parent":"slimevr:head","tailOffset":{"terms":[{"proportion":"example:tail","direction":{"x":0,"y":0,"z":1}}]}}""",
					"assets/lang/en.json" to """{"example:bone.extra":"Extra bone"}""",
				),
			),
		)
		val definition = compileResourcePacks(catalog(core, user))
		val offsets = definition.toBoneOffsets(definition.defaultProportionValues())
		assertVectorEquals(Vector3(0f, 0f, 0.3f), offsets.tail[definition.registry["example:extra"]!!], "extra bone tail")
	}

	@Test
	fun `compiler reports missing standard core bone`() = runTest {
		val core = ResourcePackParser().parse(ClasspathResourcePackSource.core(javaClass.classLoader))
		val error = assertFailsWith<ResourcePackCompilationException> {
			compileResourcePacks(catalog(core.copy(bones = core.bones.drop(1))))
		}
		assertTrue(error.diagnostics.any { "Missing standard bone" in it.message })
	}

	@Test
	fun `compiler reports an extra hierarchy root from a user pack`() = runTest {
		val core = ResourcePackParser().parse(ClasspathResourcePackSource.core(javaClass.classLoader))
		val user = ResourcePackParser().parse(
			InMemoryResourcePackSource(
				mapOf(
					"manifest.json" to manifest("example:root"),
					"data/bones/root.json" to """{"key":"example:root","nameKey":"example:bone.root"}""",
				),
			),
		)
		val error = assertFailsWith<ResourcePackCompilationException> { compileResourcePacks(catalog(core, user)) }
		assertTrue(error.diagnostics.any { "exactly one root" in it.message })
	}

	@Test
	fun `compiler reports no root when every bone has a parent`() = runTest {
		val core = ResourcePackParser().parse(ClasspathResourcePackSource.core(javaClass.classLoader))
		// head is the real root; giving it a parent leaves no bone without one.
		val head = core.bones.single { it.value.key == "slimevr:head" }
		val patched = core.copy(bones = core.bones - head + head.copy(value = head.value.copy(parent = "slimevr:neck")))
		val error = assertFailsWith<ResourcePackCompilationException> { compileResourcePacks(catalog(patched)) }
		assertTrue(error.diagnostics.any { "exactly one root" in it.message && "found none" in it.message })
	}

	@Test
	fun `compiler reports a duplicate proportion key across packs`() = runTest {
		val core = ResourcePackParser().parse(ClasspathResourcePackSource.core(javaClass.classLoader))
		val user = ResourcePackParser().parse(
			InMemoryResourcePackSource(
				mapOf(
					"manifest.json" to manifest("example:extra"),
					"data/proportions/upper_leg.json" to """{"key":"slimevr:upper_leg","nameKey":"example:proportion.upper_leg","default":{"type":"fixed","value":0.3}}""",
				),
			),
		)
		val error = assertFailsWith<ResourcePackCompilationException> { compileResourcePacks(catalog(core, user)) }
		assertTrue(error.diagnostics.any { "Duplicate proportion key" in it.message })
	}

	@Test
	fun `compiler reports a bone offset term naming an unknown proportion`() = runTest {
		val core = ResourcePackParser().parse(ClasspathResourcePackSource.core(javaClass.classLoader))
		val user = ResourcePackParser().parse(
			InMemoryResourcePackSource(
				mapOf(
					"manifest.json" to manifest("example:extra"),
					"data/bones/extra.json" to """{"key":"example:extra","nameKey":"example:bone.extra","parent":"slimevr:head","tailOffset":{"terms":[{"proportion":"example:missing","direction":{"x":0,"y":0,"z":1}}]}}""",
					"assets/lang/en.json" to """{"example:bone.extra":"Extra bone"}""",
				),
			),
		)
		val error = assertFailsWith<ResourcePackCompilationException> { compileResourcePacks(catalog(core, user)) }
		assertTrue(error.diagnostics.any { "Unknown proportion" in it.message })
	}

	private fun catalog(core: dev.slimevr.resourcepacks.ParsedResourcePack, vararg users: dev.slimevr.resourcepacks.ParsedResourcePack) = ResourcePackCatalog(core, users.toList(), emptyList())

	private fun manifest(id: String) = """{"formatVersion":1,"id":"$id","nameKey":"example:name","descriptionKey":"example:description","version":[1,0,0],"authors":["Test"]}"""
}
