package dev.slimevr.resourcepacks

import dev.slimevr.bones.BodyPart
import dev.slimevr.bones.BoneId
import dev.slimevr.bones.BoneRegistry
import dev.slimevr.bones.boneId
import dev.slimevr.bones.key
import dev.slimevr.resourcepacks.bones.REFERENCE_HEIGHT
import dev.slimevr.resourcepacks.compiler.ResourcePackCompilationException
import dev.slimevr.resourcepacks.compiler.compileResourcePacks
import dev.slimevr.testCompiledSkeleton
import dev.slimevr.testCoreResourcePack
import io.github.axisangles.ktmath.Vector3
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import solarxr_protocol.rpc.RoutingOutput
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ResourcePackCompilerTest {
	@Test
	fun `the bundled core pack parses to the expected resources`() {
		val core = testCoreResourcePack

		assertEquals("slimevr:core", core.manifest.value.id)
		assertEquals(CORE_BONE_COUNT, core.decoded(ResourceTypes.BONE).size)
		assertEquals(CORE_PROPORTION_COUNT, core.decoded(ResourceTypes.PROPORTION).size)
		assertEquals(1, core.decoded(ResourceTypes.LANGUAGE).size)

		val head = core.decoded(ResourceTypes.BONE).single { it.value.key == "slimevr:head" }.value
		val fallback = assertIs<CopyRotationFallback>(head.rotationFallback)
		assertEquals("slimevr:neck", fallback.source)
		val step = head.outputs!!.vrchat!!.emit.getValue("/tracking/trackers/head/position").value!!.single()
		assertIs<PipelineStep.Scale>(step)
		assertIs<ScalarOrVector.Vector>(step.operand)
		assertIs<HeightRatioProportionDefault>(core.decoded(ResourceTypes.PROPORTION).single { it.value.key == "slimevr:upper_chest" }.value.default)
	}

	@Test
	fun `the bundled core pack compiles to the standard bone registry and proportions`() {
		val definition = testCompiledSkeleton
		val registry = definition.registry
		assertEquals(BoneRegistry.standard().value.bones.map { Triple(it.id, it.key, it.parent) }, registry.value.bones.map { Triple(it.id, it.key, it.parent) })
		assertEquals("Head", registry[BodyPart.HEAD.boneId]?.displayName)

		assertEquals(CORE_PROPORTION_COUNT, definition.proportions.size)
		assertEquals(
			setOf("slimevr:neck", "slimevr:upper_chest", "slimevr:lower_chest", "slimevr:upper_waist", "slimevr:lower_waist", "slimevr:hip", "slimevr:upper_leg", "slimevr:lower_leg"),
			definition.proportions.filterValues { it.contributesToHeight }.keys,
		)

		val defaults = definition.defaultProportionValues()
		assertFloatEquals(REFERENCE_HEIGHT, definition.height(defaults), "height")
	}

	@Test
	fun `the bundled core pack resolves offsets and output routing`() {
		val definition = testCompiledSkeleton
		val registry = definition.registry
		val offsets = definition.toBoneOffsets(definition.defaultProportionValues())

		assertEquals(CORE_BONE_COUNT, offsets.tail.entries.size)
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
	fun `unknown bone and proportion keys are reported with their field`() = runTest {
		val cases = listOf(
			"\"rotationFallback\":{\"type\":\"copy\",\"source\":\"example:missing\"}" to "rotationFallback",
			"\"candidateSources\":[\"example:missing\"]" to "candidateSources",
			"\"batterySources\":[\"example:missing\"]" to "batterySources",
			"\"outputs\":{\"vrchat\":{\"emit\":{\"/example\":{\"from\":\"rotation\",\"relativeTo\":\"example:missing\"}}}}" to "outputs.vrchat.emit.relativeTo",
		)
		for ((field, marker) in cases) {
			val diagnostics = assertCompileFails(
				"data/example/bones/extra.json" to """{"key":"example:extra","nameKey":"example:bone.extra","parent":"slimevr:head",$field}""",
				"assets/example/lang/en.json" to """{"example:bone.extra":"Extra bone"}""",
			)
			assertTrue(diagnostics.any { "Unknown bone" in it.message && marker in it.message }, marker)
		}

		val proportionDiagnostics = assertCompileFails(
			"data/example/bones/extra.json" to """{"key":"example:extra","nameKey":"example:bone.extra","parent":"slimevr:head","tailOffset":{"terms":[{"proportion":"example:missing","direction":{"x":0,"y":0,"z":1}}]}}""",
			"assets/example/lang/en.json" to """{"example:bone.extra":"Extra bone"}""",
		)
		assertTrue(proportionDiagnostics.any { "Unknown proportion" in it.message })
	}

	@Test
	fun `fallback chains order sources before consumers and reject cross-type dependencies`() = runTest {
		val definition = compilePack(
			"data/example/bones/copy-consumer.json" to """{"key":"example:copy_consumer","nameKey":"example:copy_consumer","parent":"slimevr:head","rotationFallback":{"type":"copy","source":"example:copy_source"}}""",
			"data/example/bones/copy-source.json" to """{"key":"example:copy_source","nameKey":"example:copy_source","parent":"slimevr:head","rotationFallback":{"type":"copy","source":"slimevr:head"}}""",
			"data/example/bones/first-consumer.json" to """{"key":"example:first_consumer","nameKey":"example:first_consumer","parent":"slimevr:head","rotationFallback":{"type":"firstActive","sources":["example:first_source"]}}""",
			"data/example/bones/first-source.json" to """{"key":"example:first_source","nameKey":"example:first_source","parent":"slimevr:head","rotationFallback":{"type":"firstActive","sources":["slimevr:head"]}}""",
		)
		val copyOrder = definition.copyRotationFallbacks.map { it.first }
		assertTrue(copyOrder.indexOf(definition.registry["example:copy_source"]!!) < copyOrder.indexOf(definition.registry["example:copy_consumer"]!!))
		val firstActiveOrder = definition.firstActiveRotationFallbacks.map { it.first }
		assertTrue(firstActiveOrder.indexOf(definition.registry["example:first_source"]!!) < firstActiveOrder.indexOf(definition.registry["example:first_consumer"]!!))

		val diagnostics = assertCompileFails(
			"data/example/bones/copy.json" to """{"key":"example:copy","nameKey":"example:copy","parent":"slimevr:head","rotationFallback":{"type":"copy","source":"example:first"}}""",
			"data/example/bones/first.json" to """{"key":"example:first","nameKey":"example:first","parent":"slimevr:head","rotationFallback":{"type":"firstActive","sources":["slimevr:head"]}}""",
			packId = "example:cross-fallback",
		)
		val diagnostic = diagnostics.single { "copy fallbacks run first" in it.message }
		assertEquals("example:cross-fallback", diagnostic.packId)
		assertEquals("data/example/bones/copy.json", diagnostic.path)
	}

	@Test
	fun `duplicate addresses, names and keys are rejected`() = runTest {
		val addressDiagnostics = assertCompileFails(
			"data/example/bones/first.json" to """{"key":"example:first","nameKey":"example:first","parent":"slimevr:head","outputs":{"vrchat":{"emit":{"/example/output":{"from":"position"}}}},"inputs":{"vrchat":{"address":"/example/input"}}}""",
			"data/example/bones/second.json" to """{"key":"example:second","nameKey":"example:second","parent":"slimevr:head","outputs":{"vrchat":{"emit":{"/example/output":{"from":"rotation"}}}},"inputs":{"vrchat":{"address":"/example/input"}}}""",
			packId = "example:duplicate-addresses",
		)
		assertTrue(addressDiagnostics.any { "Duplicate VRChat input address '/example/input'" in it.message })
		assertTrue(addressDiagnostics.any { "Duplicate VRChat emit address '/example/output'" in it.message })
		assertTrue(addressDiagnostics.filter { "Duplicate VRChat" in it.message }.all { it.path == "data/example/bones/second.json" })

		val nameDiagnostics = assertCompileFails(
			"data/example/bones/first.json" to """{"key":"example:first","nameKey":"example:first","parent":"slimevr:head","outputs":{"vmc":{"name":"ExtraBone"}}}""",
			"data/example/bones/second.json" to """{"key":"example:second","nameKey":"example:second","parent":"slimevr:head","outputs":{"vmc":{"name":"ExtraBone"}}}""",
			packId = "example:vmc-names",
		)
		val nameDiagnostic = nameDiagnostics.single { "Duplicate VMC name" in it.message }
		assertEquals("example:vmc-names", nameDiagnostic.packId)
		assertEquals("data/example/bones/second.json", nameDiagnostic.path)

		val proportionDiagnostics = assertCompileFails(
			"data/example/proportions/upper_leg.json" to """{"key":"slimevr:upper_leg","nameKey":"example:proportion.upper_leg","default":{"type":"fixed","value":0.3}}""",
		)
		assertTrue(proportionDiagnostics.any { "Duplicate proportion key" in it.message })
	}

	@Test
	fun `compiler rejects invalid VRChat emit pipeline types`() = runTest {
		val diagnostics = assertCompileFails(
			"data/example/bones/extra.json" to """
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
		assertTrue(diagnostics.all { it.packId == "example:invalid-pipelines" && it.path == "data/example/bones/extra.json" })
	}

	@Test
	fun `VMC parent graphs must reference VMC bones and stay acyclic`() = runTest {
		val metadataDiagnostics = assertCompileFails(
			"data/example/bones/extra.json" to """{"key":"example:extra","nameKey":"example:extra","parent":"slimevr:head","outputs":{"vmc":{"name":"ExtraBone","outputParent":"slimevr:lower_waist","inputParent":"slimevr:lower_waist"}}}""",
		)
		assertTrue(metadataDiagnostics.any { "VMC outputParent" in it.message && "does not have a VMC output" in it.message })
		assertTrue(metadataDiagnostics.any { "VMC inputParent" in it.message && "does not have a VMC output" in it.message })

		val cycleDiagnostics = assertCompileFails(
			"data/example/bones/first.json" to """{"key":"example:first","nameKey":"example:first","parent":"slimevr:head","outputs":{"vmc":{"name":"ExtraFirst","outputParent":"example:second","inputParent":"example:second"}}}""",
			"data/example/bones/second.json" to """{"key":"example:second","nameKey":"example:second","parent":"slimevr:head","outputs":{"vmc":{"name":"ExtraSecond","outputParent":"example:first","inputParent":"example:first"}}}""",
		)
		assertTrue(cycleDiagnostics.any { "VMC outputParent graph contains a cycle" in it.message })
		assertTrue(cycleDiagnostics.any { "VMC inputParent graph contains a cycle" in it.message })
	}

	@Test
	fun `VMC input order includes every explicit input root`() = runTest {
		val definition = compilePack(
			"data/example/bones/extra.json" to """{"key":"example:extra","nameKey":"example:extra","parent":"slimevr:head","outputs":{"vmc":{"name":"ExtraRoot","outputParent":"slimevr:head","inputParent":null}}}""",
		)
		assertTrue(definition.registry["example:extra"]!! in definition.vmcInputOrder)
		assertEquals(definition.vmcNamedBones, definition.vmcInputOrder.toSet())
	}

	@Test
	fun `the bone hierarchy must have exactly one root`() = runTest {
		val missingStandardBone = assertFailsWith<ResourcePackCompilationException> {
			compileResourcePacks(catalog(ParsedResourcePack(testCoreResourcePack.source, testCoreResourcePack.manifest, testCoreResourcePack.objects.toMutableMap().apply { put(ResourceTypes.BONE, testCoreResourcePack.objects.getValue(ResourceTypes.BONE).drop(1)) })))
		}
		assertTrue(missingStandardBone.diagnostics.any { "Missing standard bone" in it.message })

		val extraRootDiagnostics = assertCompileFails(
			"data/example/bones/root.json" to """{"key":"example:root","nameKey":"example:bone.root"}""",
			packId = "example:root",
		)
		assertTrue(extraRootDiagnostics.any { "exactly one root" in it.message })

		// head is the real root; giving it a parent leaves no bone without one.
		val bones = testCoreResourcePack.objects.getValue(ResourceTypes.BONE).map { (path, document) ->
			path to if (path.endsWith("/head.json")) JsonObject(document + ("parent" to JsonPrimitive("slimevr:neck"))) else document
		}
		val patched = ParsedResourcePack(testCoreResourcePack.source, testCoreResourcePack.manifest, testCoreResourcePack.objects + (ResourceTypes.BONE to bones))
		val noRoot = assertFailsWith<ResourcePackCompilationException> { compileResourcePacks(catalog(patched)) }
		assertTrue(noRoot.diagnostics.any { "exactly one root" in it.message && "found none" in it.message })
	}

	@Test
	fun `user packs contribute bones and proportions after core`() = runTest {
		val registry = compilePack(
			"data/example/bones/extra.json" to """{"key":"example:extra","nameKey":"example:bone.extra","parent":"slimevr:head"}""",
			"assets/example/lang/en.json" to """{"example:bone.extra":"Extra bone"}""",
		).registry
		assertEquals(BoneId(62u.toUShort()), registry["example:extra"])
		assertEquals(BoneId(1u.toUShort()), registry.parentOf(BoneId(62u.toUShort())))
		assertEquals("Extra bone", registry[BoneId(62u.toUShort())]?.displayName)

		val definition = compilePack(
			"data/example/proportions/tail.json" to """{"key":"example:tail","nameKey":"example:proportion.tail","default":{"type":"fixed","value":0.3}}""",
			"data/example/bones/extra.json" to """{"key":"example:extra","nameKey":"example:bone.extra","parent":"slimevr:head","tailOffset":{"terms":[{"proportion":"example:tail","direction":{"x":0,"y":0,"z":1}}]}}""",
			"assets/example/lang/en.json" to """{"example:bone.extra":"Extra bone"}""",
		)
		val offsets = definition.toBoneOffsets(definition.defaultProportionValues())
		assertVectorEquals(Vector3(0f, 0f, 0.3f), offsets.tail[definition.registry["example:extra"]!!], "extra bone tail")
	}
}
