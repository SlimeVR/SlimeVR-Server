package dev.slimevr.resourcepacks

import dev.slimevr.resourcepacks.compiler.ResourcePackCompilationException
import dev.slimevr.resourcepacks.compiler.compileResourcePacks
import dev.slimevr.resourcepacks.compiler.resolveResourcePacks
import dev.slimevr.testCoreResourcePack
import kotlinx.coroutines.test.runTest
import solarxr_protocol.rpc.RoutingOutput
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private const val HIP_PATH = "data/slimevr/bones/hip.json"
private fun stack(vararg packs: ParsedResourcePack) = ResourcePackCatalog(testCoreResourcePack, packs.toList(), emptyList())

class ResourcePackOverrideTest {
	@Test
	fun `replacement removes omitted fields and keeps core bone ownership`() = runTest {
		val pack = testPack(
			"example:replace",
			HIP_PATH to """
			{"key":"slimevr:hip","nameKey":"slimevr:bone.hip","parent":"slimevr:lower_waist"}
			""".trimIndent(),
		)
		val definition = compileResourcePacks(stack(pack))
		val hip = definition.registry["slimevr:hip"]!!
		assertEquals(emptyList(), definition.batterySourcesOf(hip))
		assertFalse(hip in definition.acceptedBones(RoutingOutput.DRIVER))
		assertEquals("Hip", definition.registry[hip]?.displayName)
	}

	@Test
	fun `self extend merges successive layers in manifest order`() = runTest {
		val first = testPack(
			"example:z-first",
			HIP_PATH to """
			{"${'$'}extend":"$HIP_PATH","batterySources":["slimevr:upper_chest"],"outputs":{"vrchat":{"emit":{"/example/first":{"from":"position"}}}}}
			""".trimIndent(),
			before = listOf("example:a-second"),
		)
		val second = testPack(
			"example:a-second",
			HIP_PATH to """
			{"${'$'}extend":"$HIP_PATH","batterySources":["slimevr:lower_chest"],"outputs":{"vrchat":{"emit":{"/example/second":{"from":"rotation"}}}}}
			""".trimIndent(),
		)
		val definition = compileResourcePacks(stack(second, first))
		val hip = definition.registry["slimevr:hip"]!!
		assertEquals(listOf(definition.registry["slimevr:lower_chest"]!!), definition.batterySourcesOf(hip))
		assertTrue(definition.emitEntriesOf(hip).keys.containsAll(listOf("/tracking/trackers/1/position", "/example/first", "/example/second")))
	}

	@Test
	fun `cross file inheritance resolves transitively`() = runTest {
		val pack = testPack(
			"example:extend",
			"data/example/bones/a.json" to """{"key":"example:a","nameKey":"example:a","parent":"slimevr:hip","batterySources":["slimevr:hip"]}""",
			"data/example/bones/b.json" to """{"${'$'}extend":"data/example/bones/a.json","key":"example:b"}""",
			"data/example/bones/c.json" to """{"${'$'}extend":"data/example/bones/b.json","key":"example:c"}""",
		)
		val definition = compileResourcePacks(stack(pack))
		assertEquals(listOf(definition.registry["slimevr:hip"]!!), definition.batterySourcesOf(definition.registry["example:c"]!!))
	}

	@Test
	fun `invalid $extend targets fail explicitly`() = runTest {
		for (target in listOf("\"data/example/bones/missing.json\"", "{}", "null", "42")) {
			val pack = testPack(
				"example:bad",
				"data/example/bones/a.json" to """
				{"${'$'}extend":$target,"key":"example:a","nameKey":"example:a","parent":"slimevr:hip"}
				""".trimIndent(),
			)
			val error = assertFailsWith<ResourcePackCompilationException> { compileResourcePacks(stack(pack)) }
			assertTrue(error.diagnostics.any { "${'$'}extend" in it.message && it.packId == "example:bad" }, target)
		}

		val cycle = testPack(
			"example:cycle",
			"data/example/bones/a.json" to """{"${'$'}extend":"data/example/bones/b.json","key":"example:a"}""",
			"data/example/bones/b.json" to """{"${'$'}extend":"data/example/bones/a.json","key":"example:b"}""",
		)
		assertTrue(assertFailsWith<ResourcePackCompilationException> { compileResourcePacks(stack(cycle)) }.message!!.contains("Cyclic"))

		val self = testPack("example:self", "data/example/bones/a.json" to """{"${'$'}extend":"data/example/bones/a.json"}""")
		assertTrue(assertFailsWith<ResourcePackCompilationException> { compileResourcePacks(stack(self)) }.message!!.contains("no inherited layer"))
	}

	@Test
	fun `semantic and schema errors point to the overriding pack`() = runTest {
		for (fields in listOf("\"batterySources\":[\"example:missing\"]", "\"outputs\":{\"vmc\":{\"name\":\"Head\"}}", "\"unknownProperty\":true")) {
			val pack = testPack("example:bad", HIP_PATH to """{"${'$'}extend":"$HIP_PATH",$fields}""")
			val error = assertFailsWith<ResourcePackCompilationException> { compileResourcePacks(stack(pack)) }
			assertTrue(error.diagnostics.isNotEmpty())
			assertTrue(error.diagnostics.all { it.packId == "example:bad" && it.path == HIP_PATH }, error.message)
		}
	}

	@Test
	fun `inherited field diagnostics retain the pack that supplied the field`() = runTest {
		val first = testPack("example:first", HIP_PATH to """{"${'$'}extend":"$HIP_PATH","batterySources":["example:missing"]}""")
		val second = testPack("example:second", HIP_PATH to """{"${'$'}extend":"$HIP_PATH","nameKey":"example:hip"}""")
		val error = assertFailsWith<ResourcePackCompilationException> { compileResourcePacks(stack(first, second)) }
		assertEquals("example:first", error.diagnostics.single { "batterySources" in it.message }.packId)
	}

	@Test
	fun `language namespaces and pack overrides share one resolved dictionary`() = runTest {
		val first = testPack(
			"example:first",
			"data/example/bones/a.json" to """{"key":"example:a","nameKey":"example:a","parent":"slimevr:hip"}""",
			"assets/example/lang/en.json" to """{"example:a":"Original","example:kept":"Kept"}""",
		)
		val second = testPack(
			"example:second",
			"assets/example/lang/en.json" to """{"example:a":"Overridden"}""",
			"assets/slimevr/lang/en.json" to """{"slimevr:bone.hip":"Custom hip"}""",
		)
		val catalog = stack(first, second)
		val definition = compileResourcePacks(catalog)
		assertEquals("Overridden", definition.registry.byKey("example:a")?.displayName)
		assertEquals("Custom hip", definition.registry.byKey("slimevr:hip")?.displayName)
		assertEquals("Head", definition.registry.byKey("slimevr:head")?.displayName)
		val languages = resolveResourcePacks(catalog).get(ResourceTypes.LANGUAGE)
		assertEquals("Kept", languages.getValue("assets/example/lang/en.json").resource.value.translations["example:kept"])
	}
}
