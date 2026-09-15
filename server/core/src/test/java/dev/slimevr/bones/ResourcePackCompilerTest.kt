package dev.slimevr.bones

import dev.slimevr.resourcepacks.bones.CompiledSkeleton
import dev.slimevr.resourcepacks.InMemoryResourcePackSource
import dev.slimevr.resourcepacks.ParsedResourcePack
import dev.slimevr.resourcepacks.bones.REFERENCE_HEIGHT
import dev.slimevr.resourcepacks.ResourcePackCatalog
import dev.slimevr.resourcepacks.ResourcePackCompilationDiagnostic
import dev.slimevr.resourcepacks.ResourcePackCompilationException
import dev.slimevr.resourcepacks.ResourcePackParser
import dev.slimevr.resourcepacks.ResourceTypes
import dev.slimevr.resourcepacks.compileResourcePacks
import dev.slimevr.testCoreResourcePack
import io.github.axisangles.ktmath.Vector3
import kotlinx.coroutines.test.runTest
import solarxr_protocol.rpc.RoutingOutput
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private const val EPSILON = 1e-6f

private fun assertVectorEquals(expected: Vector3, actual: Vector3?, message: String) {
	assertTrue(actual != null && (expected - actual).lenSq() < EPSILON, "$message: expected $expected, got $actual")
}

private fun assertFloatEquals(expected: Float, actual: Float, message: String) {
	assertTrue(kotlin.math.abs(expected - actual) < EPSILON, "$message: expected $expected, got $actual")
}

private fun catalog(core: ParsedResourcePack, vararg users: ParsedResourcePack) = ResourcePackCatalog(core, users.toList(), emptyList())

private fun manifest(id: String) = """{"formatVersion":1,"id":"$id","nameKey":"example:name","descriptionKey":"example:description","version":[1,0,0],"authors":["Test"]}"""

private suspend fun compilePack(vararg files: Pair<String, String>, packId: String = "example:test"): CompiledSkeleton {
	val user = ResourcePackParser.parse(InMemoryResourcePackSource(mapOf("manifest.json" to manifest(packId)) + files))
	return compileResourcePacks(catalog(testCoreResourcePack, user))
}

private suspend fun assertCompileFails(vararg files: Pair<String, String>, packId: String = "example:test"): List<ResourcePackCompilationDiagnostic> = assertFailsWith<ResourcePackCompilationException> { compilePack(*files, packId = packId) }.diagnostics

class ResourcePackCompilerTest {
	@Test
	fun `bundled core pack compiles to the standard bone registry`() = runTest {
		assertEquals(BoneRegistry.standard().value, compileResourcePacks(catalog(testCoreResourcePack)).registry.value)
	}

	@Test
	fun `bundled core pack compiles 18 proportions, 8 of which contribute to height`() = runTest {
		val definition = compileResourcePacks(catalog(testCoreResourcePack))
		assertEquals(18, definition.proportions.size)
		assertEquals(
			setOf("slimevr:neck", "slimevr:upper_chest", "slimevr:lower_chest", "slimevr:upper_waist", "slimevr:lower_waist", "slimevr:hip", "slimevr:upper_leg", "slimevr:lower_leg"),
			definition.proportions.filterValues { it.contributesToHeight }.keys,
		)
	}

	@Test
	fun `bundled core pack's default proportions reproduce the reference height`() = runTest {
		val definition = compileResourcePacks(catalog(testCoreResourcePack))
		val defaults = definition.defaultProportionValues()
		assertFloatEquals(REFERENCE_HEIGHT, definition.height(defaults), "height")
	}

	@Test
	fun `bundled core pack resolves bone offsets from default proportions`() = runTest {
		val definition = compileResourcePacks(catalog(testCoreResourcePack))
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
		val definition = compileResourcePacks(catalog(testCoreResourcePack))
		val defaults = definition.defaultProportionValues()
		val offsets = definition.toBoneOffsets(defaults)
		val recovered = definition.toProportionValues(offsets.tail, offsets.head)
		assertEquals(defaults.keys, recovered.keys)
		for (key in defaults.keys) assertFloatEquals(defaults.getValue(key), recovered.getValue(key), key)
	}

	@Test
	fun `bundled core pack compiles cleanly with the expected shape`() = runTest {
		val definition = compileResourcePacks(catalog(testCoreResourcePack))
		val registry = definition.registry
		fun id(part: BodyPart) = registry[part.key]!!

		assertEquals(19, definition.constraints.entries.size)
		assertEquals(12, definition.copyRotationFallbacks.size)
		assertEquals(10, definition.firstActiveRotationFallbacks.size)

		val driverBones = setOf(
			BodyPart.UPPER_CHEST, BodyPart.LEFT_UPPER_ARM, BodyPart.RIGHT_UPPER_ARM, BodyPart.HIP,
			BodyPart.LEFT_UPPER_LEG, BodyPart.RIGHT_UPPER_LEG, BodyPart.LEFT_FOOT, BodyPart.RIGHT_FOOT,
			BodyPart.LEFT_SHOULDER, BodyPart.RIGHT_SHOULDER, BodyPart.LEFT_HAND, BodyPart.RIGHT_HAND,
		).map(::id).toSet()
		assertEquals(driverBones, definition.acceptedBones(RoutingOutput.DRIVER))
		assertEquals(60, definition.acceptedBones(RoutingOutput.VMC).size)
		assertEquals(definition.acceptedBones(RoutingOutput.VMC), definition.requiredBones(RoutingOutput.VMC))

		assertEquals(19, definition.acceptedBones(RoutingOutput.VRC_OSC).size)
		assertEquals(setOf(id(BodyPart.HEAD)), definition.requiredBones(RoutingOutput.VRC_OSC))

		assertEquals(setOf(id(BodyPart.LEFT_HAND), id(BodyPart.RIGHT_HAND)), definition.overridableBones)
		assertEquals(3, definition.vrchatInputAddresses.size)
	}

	@Test
	fun `compiler reports an unknown bone named in a rotation fallback`() = runTest {
		val diagnostics = assertCompileFails(
			"data/bones/extra.json" to """{"key":"example:extra","nameKey":"example:bone.extra","parent":"slimevr:head","rotationFallback":{"type":"copy","source":"example:missing"}}""",
			"assets/lang/en.json" to """{"example:bone.extra":"Extra bone"}""",
		)
		assertTrue(diagnostics.any { "Unknown bone" in it.message && "rotationFallback" in it.message })
	}

	@Test
	fun `compiler orders extension fallback chains source before consumer`() = runTest {
		val definition = compilePack(
			"data/bones/copy-consumer.json" to """{"key":"example:copy_consumer","nameKey":"example:copy_consumer","parent":"slimevr:head","rotationFallback":{"type":"copy","source":"example:copy_source"}}""",
			"data/bones/copy-source.json" to """{"key":"example:copy_source","nameKey":"example:copy_source","parent":"slimevr:head","rotationFallback":{"type":"copy","source":"slimevr:head"}}""",
			"data/bones/first-consumer.json" to """{"key":"example:first_consumer","nameKey":"example:first_consumer","parent":"slimevr:head","rotationFallback":{"type":"firstActive","sources":["example:first_source"]}}""",
			"data/bones/first-source.json" to """{"key":"example:first_source","nameKey":"example:first_source","parent":"slimevr:head","rotationFallback":{"type":"firstActive","sources":["slimevr:head"]}}""",
		)
		val copyOrder = definition.copyRotationFallbacks.map { it.first }
		assertTrue(copyOrder.indexOf(definition.registry["example:copy_source"]!!) < copyOrder.indexOf(definition.registry["example:copy_consumer"]!!))
		val firstActiveOrder = definition.firstActiveRotationFallbacks.map { it.first }
		assertTrue(firstActiveOrder.indexOf(definition.registry["example:first_source"]!!) < firstActiveOrder.indexOf(definition.registry["example:first_consumer"]!!))
	}

	@Test
	fun `compiler rejects a copy fallback that depends on a firstActive fallback`() = runTest {
		val diagnostics = assertCompileFails(
			"data/bones/copy.json" to """{"key":"example:copy","nameKey":"example:copy","parent":"slimevr:head","rotationFallback":{"type":"copy","source":"example:first"}}""",
			"data/bones/first.json" to """{"key":"example:first","nameKey":"example:first","parent":"slimevr:head","rotationFallback":{"type":"firstActive","sources":["slimevr:head"]}}""",
			packId = "example:cross-fallback",
		)
		val diagnostic = diagnostics.single { "copy fallbacks run first" in it.message }
		assertEquals("example:cross-fallback", diagnostic.packId)
		assertEquals("data/bones/copy.json", diagnostic.path)
	}

	@Test
	fun `compiler reports an unknown bone named in candidateSources`() = runTest {
		val diagnostics = assertCompileFails(
			"data/bones/extra.json" to """{"key":"example:extra","nameKey":"example:bone.extra","parent":"slimevr:head","candidateSources":["example:missing"]}""",
			"assets/lang/en.json" to """{"example:bone.extra":"Extra bone"}""",
		)
		assertTrue(diagnostics.any { "Unknown bone" in it.message && "candidateSources" in it.message })
	}

	@Test
	fun `compiler reports an unknown bone named in batterySources`() = runTest {
		val diagnostics = assertCompileFails(
			"data/bones/extra.json" to """{"key":"example:extra","nameKey":"example:bone.extra","parent":"slimevr:head","batterySources":["example:missing"]}""",
			"assets/lang/en.json" to """{"example:bone.extra":"Extra bone"}""",
		)
		assertTrue(diagnostics.any { "Unknown bone" in it.message && "batterySources" in it.message })
	}

	@Test
	fun `compiler reports an unknown bone named in a VRChat emit relativeTo`() = runTest {
		val diagnostics = assertCompileFails(
			"data/bones/extra.json" to """{"key":"example:extra","nameKey":"example:bone.extra","parent":"slimevr:head","outputs":{"vrchat":{"emit":{"/example":{"from":"rotation","relativeTo":"example:missing"}}}}}""",
			"assets/lang/en.json" to """{"example:bone.extra":"Extra bone"}""",
		)
		assertTrue(diagnostics.any { "Unknown bone" in it.message && "outputs.vrchat.emit.relativeTo" in it.message })
	}

	@Test
	fun `compiler rejects duplicate VRChat input and emit addresses`() = runTest {
		val diagnostics = assertCompileFails(
			"data/bones/first.json" to """{"key":"example:first","nameKey":"example:first","parent":"slimevr:head","outputs":{"vrchat":{"emit":{"/example/output":{"from":"position"}}}},"inputs":{"vrchat":{"address":"/example/input"}}}""",
			"data/bones/second.json" to """{"key":"example:second","nameKey":"example:second","parent":"slimevr:head","outputs":{"vrchat":{"emit":{"/example/output":{"from":"rotation"}}}},"inputs":{"vrchat":{"address":"/example/input"}}}""",
			packId = "example:duplicate-addresses",
		)
		assertTrue(diagnostics.any { "Duplicate VRChat input address '/example/input'" in it.message })
		assertTrue(diagnostics.any { "Duplicate VRChat emit address '/example/output'" in it.message })
		assertTrue(diagnostics.filter { "Duplicate VRChat" in it.message }.all { it.path == "data/bones/second.json" })
	}

	@Test
	fun `compiler rejects invalid VRChat emit pipeline types`() = runTest {
		val diagnostics = assertCompileFails(
			"data/bones/extra.json" to """
				{"key":"example:extra","nameKey":"example:extra","parent":"slimevr:head","outputs":{"vrchat":{"emit":{
					"/example/euler":{"from":"position","value":[{"euler":"x"}]},
					"/example/vector-on-number":{"from":"rotation","value":[{"euler":"x"},{"scale":{"x":2}}]},
					"/example/boolean":{"from":"rotation","value":[{"euler":"x"},{"greaterThan":0},{"offset":1}]}
				}}}}
			""".trimIndent(),
			packId = "example:invalid-pipelines",
		).filter { "VRChat emit" in it.message }

		assertEquals(3, diagnostics.size)
		assertTrue(diagnostics.any { "'/example/euler'" in it.message && "cannot apply euler to vector" in it.message })
		assertTrue(diagnostics.any { "'/example/vector-on-number'" in it.message && "cannot apply scale to number" in it.message })
		assertTrue(diagnostics.any { "'/example/boolean'" in it.message && "cannot apply offset to boolean" in it.message })
		assertTrue(diagnostics.all { it.packId == "example:invalid-pipelines" && it.path == "data/bones/extra.json" })
	}

	@Test
	fun `user-pack set overrides apply in pack order and merge VRChat emit addresses`() = runTest {
		val first = ResourcePackParser.parse(
			InMemoryResourcePackSource(
				mapOf(
					"manifest.json" to manifest("example:first"),
					"data/overrides/bones/hip.json" to """{"target":"slimevr:hip","set":{"batterySources":["slimevr:upper_chest"],"outputs":{"vrchat":{"emit":{"/example/first":{"from":"position"}}}}}}""",
				),
			),
		)
		val second = ResourcePackParser.parse(
			InMemoryResourcePackSource(
				mapOf(
					"manifest.json" to manifest("example:second"),
					"data/overrides/bones/hip.json" to """{"target":"slimevr:hip","set":{"batterySources":["slimevr:lower_chest"],"outputs":{"vrchat":{"emit":{"/example/second":{"from":"rotation"}}}}}}""",
				),
			),
		)
		val definition = compileResourcePacks(catalog(testCoreResourcePack, first, second))
		val hip = definition.registry[BodyPart.HIP.key]!!
		assertEquals(listOf(definition.registry[BodyPart.LOWER_CHEST.key]!!), definition.batterySourcesOf(hip))
		assertTrue("/tracking/trackers/1/position" in definition.emitEntriesOf(hip))
		assertTrue("/example/first" in definition.emitEntriesOf(hip))
		assertTrue("/example/second" in definition.emitEntriesOf(hip))
	}

	@Test
	fun `a later pack can set a property removed by an earlier pack`() = runTest {
		val remove = ResourcePackParser.parse(
			InMemoryResourcePackSource(
				mapOf(
					"manifest.json" to manifest("example:remove"),
					"data/overrides/bones/hip.json" to """{"target":"slimevr:hip","remove":[["batterySources"]]}""",
				),
			),
		)
		val set = ResourcePackParser.parse(
			InMemoryResourcePackSource(
				mapOf(
					"manifest.json" to manifest("example:set"),
					"data/overrides/bones/hip.json" to """{"target":"slimevr:hip","set":{"batterySources":["slimevr:lower_chest"]}}""",
				),
			),
		)
		val definition = compileResourcePacks(catalog(testCoreResourcePack, remove, set))
		val hip = definition.registry[BodyPart.HIP.key]!!
		assertEquals(listOf(definition.registry[BodyPart.LOWER_CHEST.key]!!), definition.batterySourcesOf(hip))
	}

	@Test
	fun `one override document applies set before remove`() = runTest {
		val definition = compilePack(
			"data/overrides/bones/hip.json" to """{"target":"slimevr:hip","set":{"batterySources":["slimevr:lower_chest"]},"remove":[["batterySources"]]}""",
		)
		assertEquals(emptyList(), definition.batterySourcesOf(definition.registry[BodyPart.HIP.key]!!))
	}

	@Test
	fun `compiler reports unknown bone and proportion override targets even for remove-only overrides`() = runTest {
		val diagnostics = assertCompileFails(
			"data/overrides/bones/missing.json" to """{"target":"example:missing","remove":[["mirror"]]}""",
			"data/overrides/proportions/missing.json" to """{"target":"example:missing","remove":["descriptionKey"]}""",
		)
		assertEquals(2, diagnostics.size)
		assertTrue(diagnostics.any { it.path == "data/overrides/bones/missing.json" && "Unknown bone" in it.message })
		assertTrue(diagnostics.any { it.path == "data/overrides/proportions/missing.json" && "Unknown proportion" in it.message })
	}

	@Test
	fun `removing the last nested override value removes its empty containers`() = runTest {
		val definition = compilePack(
			"data/overrides/bones/head.json" to """{"target":"slimevr:head","remove":[["outputs","vrchat","emit","/tracking/trackers/head/position"],["inputs","vrchat"]]}""",
			"data/overrides/bones/upper-waist.json" to """{"target":"slimevr:upper_waist","remove":[["outputs","vmc"]]}""",
		)
		val head = definition.registry[BodyPart.HEAD.key]!!
		assertFalse(head in definition.acceptedBones(RoutingOutput.VRC_OSC))
		assertFalse(head in definition.requiredBones(RoutingOutput.VRC_OSC))
		assertEquals(emptyMap(), definition.emitEntriesOf(head))
		assertFalse("/tracking/vrsystem/head/pose" in definition.vrchatInputAddresses)
		assertFalse(definition.registry[BodyPart.UPPER_WAIST.key]!! in definition.acceptedBones(RoutingOutput.VMC))
	}

	@Test
	fun `compiler attributes invalid references introduced by an override to that override`() = runTest {
		val diagnostics = assertCompileFails(
			"data/overrides/bones/hip.json" to """{"target":"slimevr:hip","set":{"batterySources":["example:missing"]}}""",
			packId = "example:invalid-reference",
		)
		val diagnostic = diagnostics.single { "batterySources" in it.message }
		assertEquals("example:invalid-reference", diagnostic.packId)
		assertEquals("data/overrides/bones/hip.json", diagnostic.path)
	}

	@Test
	fun `compiler rejects removing required or unknown bone override properties`() = runTest {
		val diagnostics = assertCompileFails(
			"data/overrides/bones/hip.json" to """{"target":"slimevr:hip","remove":[["nameKey"],["notAProperty"]]}""",
		)
		assertEquals(2, diagnostics.size)
		assertTrue(diagnostics.all { "Cannot remove" in it.message })
	}

	@Test
	fun `compiler rejects duplicate VMC names`() = runTest {
		val diagnostics = assertCompileFails(
			"data/bones/first.json" to """{"key":"example:first","nameKey":"example:first","parent":"slimevr:head","outputs":{"vmc":{"name":"ExtraBone"}}}""",
			"data/bones/second.json" to """{"key":"example:second","nameKey":"example:second","parent":"slimevr:head","outputs":{"vmc":{"name":"ExtraBone"}}}""",
			packId = "example:vmc-names",
		)
		val diagnostic = diagnostics.single { "Duplicate VMC name" in it.message }
		assertEquals("example:vmc-names", diagnostic.packId)
		assertEquals("data/bones/second.json", diagnostic.path)
	}

	@Test
	fun `compiler rejects VMC parents without VMC output metadata`() = runTest {
		val diagnostics = assertCompileFails(
			"data/bones/extra.json" to """{"key":"example:extra","nameKey":"example:extra","parent":"slimevr:head","outputs":{"vmc":{"name":"ExtraBone","outputParent":"slimevr:lower_waist","inputParent":"slimevr:lower_waist"}}}""",
		)
		assertTrue(diagnostics.any { "VMC outputParent" in it.message && "does not have a VMC output" in it.message })
		assertTrue(diagnostics.any { "VMC inputParent" in it.message && "does not have a VMC output" in it.message })
	}

	@Test
	fun `compiler rejects cycles in both VMC parent graphs`() = runTest {
		val diagnostics = assertCompileFails(
			"data/bones/first.json" to """{"key":"example:first","nameKey":"example:first","parent":"slimevr:head","outputs":{"vmc":{"name":"ExtraFirst","outputParent":"example:second","inputParent":"example:second"}}}""",
			"data/bones/second.json" to """{"key":"example:second","nameKey":"example:second","parent":"slimevr:head","outputs":{"vmc":{"name":"ExtraSecond","outputParent":"example:first","inputParent":"example:first"}}}""",
		)
		assertTrue(diagnostics.any { "VMC outputParent graph contains a cycle" in it.message })
		assertTrue(diagnostics.any { "VMC inputParent graph contains a cycle" in it.message })
	}

	@Test
	fun `VMC input order includes every explicit input root`() = runTest {
		val definition = compilePack(
			"data/bones/extra.json" to """{"key":"example:extra","nameKey":"example:extra","parent":"slimevr:head","outputs":{"vmc":{"name":"ExtraRoot","outputParent":"slimevr:head","inputParent":null}}}""",
		)
		assertTrue(definition.registry["example:extra"]!! in definition.vmcInputOrder)
		assertEquals(definition.vmcNamedBones, definition.vmcInputOrder.toSet())
	}

	@Test
	fun `compiler includes user-pack bones after the core registry`() = runTest {
		val registry = compilePack(
			"data/bones/extra.json" to """{"key":"example:extra","nameKey":"example:bone.extra","parent":"slimevr:head"}""",
			"assets/lang/en.json" to """{"example:bone.extra":"Extra bone"}""",
		).registry
		assertEquals(BoneId(62u.toUShort()), registry["example:extra"])
		assertEquals(BoneId(1u.toUShort()), registry.parentOf(BoneId(62u.toUShort())))
		assertEquals("Extra bone", registry[BoneId(62u.toUShort())]?.displayName)
	}

	@Test
	fun `compiler resolves a user-pack bone's offset against a user-pack proportion`() = runTest {
		val definition = compilePack(
			"data/proportions/tail.json" to """{"key":"example:tail","nameKey":"example:proportion.tail","default":{"type":"fixed","value":0.3}}""",
			"data/bones/extra.json" to """{"key":"example:extra","nameKey":"example:bone.extra","parent":"slimevr:head","tailOffset":{"terms":[{"proportion":"example:tail","direction":{"x":0,"y":0,"z":1}}]}}""",
			"assets/lang/en.json" to """{"example:bone.extra":"Extra bone"}""",
		)
		val offsets = definition.toBoneOffsets(definition.defaultProportionValues())
		assertVectorEquals(Vector3(0f, 0f, 0.3f), offsets.tail[definition.registry["example:extra"]!!], "extra bone tail")
	}

	@Test
	fun `compiler reports missing standard core bone`() = runTest {
		val error = assertFailsWith<ResourcePackCompilationException> {
			compileResourcePacks(catalog(ParsedResourcePack(testCoreResourcePack.source, testCoreResourcePack.manifest, testCoreResourcePack.resources.toMutableMap().apply { put(ResourceTypes.BONE, testCoreResourcePack.get(ResourceTypes.BONE).drop(1)) })))
		}
		assertTrue(error.diagnostics.any { "Missing standard bone" in it.message })
	}

	@Test
	fun `compiler reports an extra hierarchy root from a user pack`() = runTest {
		val diagnostics = assertCompileFails(
			"data/bones/root.json" to """{"key":"example:root","nameKey":"example:bone.root"}""",
			packId = "example:root",
		)
		assertTrue(diagnostics.any { "exactly one root" in it.message })
	}

	@Test
	fun `compiler reports no root when every bone has a parent`() = runTest {
		// head is the real root; giving it a parent leaves no bone without one.
		val head = testCoreResourcePack.get(ResourceTypes.BONE).single { it.value.key == "slimevr:head" }
		val patched = ParsedResourcePack(testCoreResourcePack.source, testCoreResourcePack.manifest, testCoreResourcePack.resources.toMutableMap().apply { put(ResourceTypes.BONE, testCoreResourcePack.get(ResourceTypes.BONE) - head + head.copy(value = head.value.copy(parent = "slimevr:neck"))) })
		val error = assertFailsWith<ResourcePackCompilationException> { compileResourcePacks(catalog(patched)) }
		assertTrue(error.diagnostics.any { "exactly one root" in it.message && "found none" in it.message })
	}

	@Test
	fun `compiler reports a duplicate proportion key across packs`() = runTest {
		val diagnostics = assertCompileFails(
			"data/proportions/upper_leg.json" to """{"key":"slimevr:upper_leg","nameKey":"example:proportion.upper_leg","default":{"type":"fixed","value":0.3}}""",
		)
		assertTrue(diagnostics.any { "Duplicate proportion key" in it.message })
	}

	@Test
	fun `compiler reports a bone offset term naming an unknown proportion`() = runTest {
		val diagnostics = assertCompileFails(
			"data/bones/extra.json" to """{"key":"example:extra","nameKey":"example:bone.extra","parent":"slimevr:head","tailOffset":{"terms":[{"proportion":"example:missing","direction":{"x":0,"y":0,"z":1}}]}}""",
			"assets/lang/en.json" to """{"example:bone.extra":"Extra bone"}""",
		)
		assertTrue(diagnostics.any { "Unknown proportion" in it.message })
	}
}
