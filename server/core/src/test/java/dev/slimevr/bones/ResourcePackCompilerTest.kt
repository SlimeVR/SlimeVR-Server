package dev.slimevr.bones

import com.jme3.math.FastMath
import com.jme3.math.FastMath.DEG_TO_RAD
import dev.slimevr.resourcepacks.ClasspathResourcePackSource
import dev.slimevr.resourcepacks.InMemoryResourcePackSource
import dev.slimevr.resourcepacks.REFERENCE_HEIGHT
import dev.slimevr.resourcepacks.ResourcePackCatalog
import dev.slimevr.resourcepacks.ResourcePackCompilationException
import dev.slimevr.resourcepacks.ResourcePackParser
import dev.slimevr.resourcepacks.compileResourcePacks
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlinx.coroutines.test.runTest
import solarxr_protocol.rpc.RoutingOutput
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

private fun assertQuaternionEquals(expected: Quaternion, actual: Quaternion, message: String) {
	val same = kotlin.math.abs(expected.w - actual.w) < EPSILON &&
		kotlin.math.abs(expected.x - actual.x) < EPSILON &&
		kotlin.math.abs(expected.y - actual.y) < EPSILON &&
		kotlin.math.abs(expected.z - actual.z) < EPSILON
	val opposite = kotlin.math.abs(expected.w + actual.w) < EPSILON &&
		kotlin.math.abs(expected.x + actual.x) < EPSILON &&
		kotlin.math.abs(expected.y + actual.y) < EPSILON &&
		kotlin.math.abs(expected.z + actual.z) < EPSILON
	assertTrue(same || opposite, "$message: expected $expected, got $actual")
}

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
	fun `bundled core pack compiles constraints matching the retired hardcoded table`() = runTest {
		val core = ResourcePackParser().parse(ClasspathResourcePackSource.core(javaClass.classLoader))
		val definition = compileResourcePacks(catalog(core))
		val constraints = definition.constraints
		assertEquals(19, constraints.entries.size)

		fun twistSwing(part: BodyPart, twist: Float, swing: Float) {
			val c = constraints[definition.registry[part.key]!!] as TwistSwingConstraint
			assertFloatEquals(twist * DEG_TO_RAD, c.twist, "${part.key} twist")
			assertFloatEquals(swing * DEG_TO_RAD, c.swing, "${part.key} swing")
		}
		fun looseHinge(part: BodyPart, min: Float, max: Float, allowedDeviation: Float) {
			val c = constraints[definition.registry[part.key]!!] as LooseHingeConstraint
			assertFloatEquals(min * DEG_TO_RAD, c.min, "${part.key} min")
			assertFloatEquals(max * DEG_TO_RAD, c.max, "${part.key} max")
			assertFloatEquals(allowedDeviation * DEG_TO_RAD, c.allowedDeviation, "${part.key} allowedDeviation")
		}

		twistSwing(BodyPart.LEFT_SHOULDER, 0f, 30f)
		twistSwing(BodyPart.LEFT_UPPER_ARM, 120f, 180f)
		looseHinge(BodyPart.LEFT_LOWER_ARM, -180f, 0f, 40f)
		twistSwing(BodyPart.LEFT_HAND, 120f, 120f)
		twistSwing(BodyPart.UPPER_CHEST, 90f, 120f)
		twistSwing(BodyPart.LOWER_CHEST, 60f, 120f)
		twistSwing(BodyPart.UPPER_WAIST, 60f, 120f)
		twistSwing(BodyPart.LOWER_WAIST, 60f, 120f)
		twistSwing(BodyPart.HIP, 60f, 120f)
		twistSwing(BodyPart.LEFT_UPPER_LEG, 120f, 170f)
		looseHinge(BodyPart.LEFT_LOWER_LEG, -5f, 180f, 10f)
		looseHinge(BodyPart.LEFT_FOOT, -60f, 90f, 60f)
	}

	@Test
	fun `bundled core pack's copy rotation-fallback schedule matches the retired BoneDirectLinkInputProcessor table`() = runTest {
		val core = ResourcePackParser().parse(ClasspathResourcePackSource.core(javaClass.classLoader))
		val definition = compileResourcePacks(catalog(core))
		val schedule = definition.copyRotationFallbacks
		val registry = definition.registry

		val expectedBones = setOf(
			BodyPart.HEAD, BodyPart.NECK,
			BodyPart.LEFT_FOOT, BodyPart.RIGHT_FOOT,
			BodyPart.LEFT_SHOULDER, BodyPart.RIGHT_SHOULDER,
			BodyPart.LEFT_UPPER_ARM, BodyPart.RIGHT_UPPER_ARM,
			BodyPart.LEFT_LOWER_ARM, BodyPart.RIGHT_LOWER_ARM,
			BodyPart.LEFT_HAND, BodyPart.RIGHT_HAND,
		).map { registry[it.key]!! }.toSet()
		assertEquals(expectedBones, schedule.map { it.first }.toSet())

		val order = schedule.map { it.first }
		fun indexOf(part: BodyPart) = order.indexOf(registry[part.key]!!)

		assertTrue(indexOf(BodyPart.HEAD) < indexOf(BodyPart.NECK), "head before neck")
		assertTrue(indexOf(BodyPart.LEFT_SHOULDER) < indexOf(BodyPart.LEFT_UPPER_ARM), "shoulder before upper_arm")
		assertTrue(indexOf(BodyPart.LEFT_UPPER_ARM) < indexOf(BodyPart.LEFT_LOWER_ARM), "upper_arm before lower_arm")
		assertTrue(indexOf(BodyPart.LEFT_LOWER_ARM) < indexOf(BodyPart.LEFT_HAND), "lower_arm before hand")

		val headFallback = schedule.first { it.first == registry[BodyPart.HEAD.key]!! }
		assertEquals(registry[BodyPart.NECK.key]!!, headFallback.second)
	}

	@Test
	fun `bundled core pack's firstActive rotation-fallback schedule matches the retired ToeActiveLinkInputProcessor table`() = runTest {
		val core = ResourcePackParser().parse(ClasspathResourcePackSource.core(javaClass.classLoader))
		val definition = compileResourcePacks(catalog(core))
		val schedule = definition.firstActiveRotationFallbacks
		val registry = definition.registry

		val expectedBones = setOf(
			BodyPart.LEFT_BIG_TOE, BodyPart.LEFT_INDEX_TOE, BodyPart.LEFT_MIDDLE_TOE, BodyPart.LEFT_RING_TOE, BodyPart.LEFT_LITTLE_TOE,
			BodyPart.RIGHT_BIG_TOE, BodyPart.RIGHT_INDEX_TOE, BodyPart.RIGHT_MIDDLE_TOE, BodyPart.RIGHT_RING_TOE, BodyPart.RIGHT_LITTLE_TOE,
		).map { registry[it.key]!! }.toSet()
		assertEquals(expectedBones, schedule.map { it.first }.toSet())

		// left_foot itself is copy-type, not firstActive, so it's resolved by
		// CopyRotationFallbackInputProcessor before this schedule ever runs.
		val bigToeFallback = schedule.first { it.first == registry[BodyPart.LEFT_BIG_TOE.key]!! }
		assertEquals(registry[BodyPart.LEFT_FOOT.key]!!, bigToeFallback.second.last())
	}

	@Test
	fun `compiler reports an unknown bone named in a rotation fallback`() = runTest {
		val core = ResourcePackParser().parse(ClasspathResourcePackSource.core(javaClass.classLoader))
		val user = ResourcePackParser().parse(
			InMemoryResourcePackSource(
				mapOf(
					"manifest.json" to manifest("example:extra"),
					"data/bones/extra.json" to """{"key":"example:extra","nameKey":"example:bone.extra","parent":"slimevr:head","rotationFallback":{"type":"copy","source":"example:missing"}}""",
					"assets/lang/en.json" to """{"example:bone.extra":"Extra bone"}""",
				),
			),
		)
		val error = assertFailsWith<ResourcePackCompilationException> { compileResourcePacks(catalog(core, user)) }
		assertTrue(error.diagnostics.any { "Unknown bone" in it.message && "rotationFallback" in it.message })
	}

	@Test
	fun `compiler orders extension fallback chains source before consumer`() = runTest {
		val core = ResourcePackParser().parse(ClasspathResourcePackSource.core(javaClass.classLoader))
		val user = ResourcePackParser().parse(
			InMemoryResourcePackSource(
				mapOf(
					"manifest.json" to manifest("example:fallback-order"),
					"data/bones/copy-consumer.json" to """{"key":"example:copy_consumer","nameKey":"example:copy_consumer","parent":"slimevr:head","rotationFallback":{"type":"copy","source":"example:copy_source"}}""",
					"data/bones/copy-source.json" to """{"key":"example:copy_source","nameKey":"example:copy_source","parent":"slimevr:head","rotationFallback":{"type":"copy","source":"slimevr:head"}}""",
					"data/bones/first-consumer.json" to """{"key":"example:first_consumer","nameKey":"example:first_consumer","parent":"slimevr:head","rotationFallback":{"type":"firstActive","sources":["example:first_source"]}}""",
					"data/bones/first-source.json" to """{"key":"example:first_source","nameKey":"example:first_source","parent":"slimevr:head","rotationFallback":{"type":"firstActive","sources":["slimevr:head"]}}""",
				),
			),
		)
		val definition = compileResourcePacks(catalog(core, user))
		val copyOrder = definition.copyRotationFallbacks.map { it.first }
		assertTrue(copyOrder.indexOf(definition.registry["example:copy_source"]!!) < copyOrder.indexOf(definition.registry["example:copy_consumer"]!!))
		val firstActiveOrder = definition.firstActiveRotationFallbacks.map { it.first }
		assertTrue(firstActiveOrder.indexOf(definition.registry["example:first_source"]!!) < firstActiveOrder.indexOf(definition.registry["example:first_consumer"]!!))
	}

	@Test
	fun `compiler rejects a copy fallback that depends on a firstActive fallback`() = runTest {
		val core = ResourcePackParser().parse(ClasspathResourcePackSource.core(javaClass.classLoader))
		val user = ResourcePackParser().parse(
			InMemoryResourcePackSource(
				mapOf(
					"manifest.json" to manifest("example:cross-fallback"),
					"data/bones/copy.json" to """{"key":"example:copy","nameKey":"example:copy","parent":"slimevr:head","rotationFallback":{"type":"copy","source":"example:first"}}""",
					"data/bones/first.json" to """{"key":"example:first","nameKey":"example:first","parent":"slimevr:head","rotationFallback":{"type":"firstActive","sources":["slimevr:head"]}}""",
				),
			),
		)
		val error = assertFailsWith<ResourcePackCompilationException> { compileResourcePacks(catalog(core, user)) }
		val diagnostic = error.diagnostics.single { "copy fallbacks run first" in it.message }
		assertEquals("example:cross-fallback", diagnostic.packId)
		assertEquals("data/bones/copy.json", diagnostic.path)
	}

	@Test
	fun `bundled core pack's driver and VMC accepted sets match the retired hardcoded tables`() = runTest {
		val core = ResourcePackParser().parse(ClasspathResourcePackSource.core(javaClass.classLoader))
		val definition = compileResourcePacks(catalog(core))
		val registry = definition.registry

		val driverBones = setOf(
			BodyPart.UPPER_CHEST, BodyPart.LEFT_UPPER_ARM, BodyPart.RIGHT_UPPER_ARM, BodyPart.HIP,
			BodyPart.LEFT_UPPER_LEG, BodyPart.RIGHT_UPPER_LEG, BodyPart.LEFT_FOOT, BodyPart.RIGHT_FOOT,
			BodyPart.LEFT_SHOULDER, BodyPart.RIGHT_SHOULDER, BodyPart.LEFT_HAND, BodyPart.RIGHT_HAND,
		).map { registry[it.key]!! }.toSet()
		assertEquals(driverBones, definition.acceptedBones(RoutingOutput.DRIVER))

		assertEquals(60, definition.acceptedBones(RoutingOutput.VMC).size)
		assertEquals(definition.acceptedBones(RoutingOutput.VMC), definition.requiredBones(RoutingOutput.VMC))
	}

	@Test
	fun `bundled core pack's VRChat accepted and required sets grow past the retired hardcoded table`() = runTest {
		val core = ResourcePackParser().parse(ClasspathResourcePackSource.core(javaClass.classLoader))
		val definition = compileResourcePacks(catalog(core))
		val registry = definition.registry

		val oldEight = setOf(
			BodyPart.HIP,
			BodyPart.LEFT_FOOT,
			BodyPart.RIGHT_FOOT,
			BodyPart.LEFT_UPPER_LEG,
			BodyPart.RIGHT_UPPER_LEG,
			BodyPart.UPPER_CHEST,
			BodyPart.LEFT_UPPER_ARM,
			BodyPart.RIGHT_UPPER_ARM,
		).map { registry[it.key]!! }.toSet()
		val accepted = definition.acceptedBones(RoutingOutput.VRC_OSC)
		assertEquals(19, accepted.size)
		assertTrue(accepted.containsAll(oldEight))

		assertEquals(setOf(registry[BodyPart.HEAD.key]!!), definition.requiredBones(RoutingOutput.VRC_OSC))
	}

	@Test
	fun `bundled core pack's overridable bones match the retired hardcoded set`() = runTest {
		val core = ResourcePackParser().parse(ClasspathResourcePackSource.core(javaClass.classLoader))
		val definition = compileResourcePacks(catalog(core))
		val registry = definition.registry
		assertEquals(setOf(registry[BodyPart.LEFT_HAND.key]!!, registry[BodyPart.RIGHT_HAND.key]!!), definition.overridableBones)
	}

	@Test
	fun `bundled core pack's candidate bones include the retired table plus every toe`() = runTest {
		val core = ResourcePackParser().parse(ClasspathResourcePackSource.core(javaClass.classLoader))
		val definition = compileResourcePacks(catalog(core))
		val registry = definition.registry

		val oldEight = setOf(
			BodyPart.UPPER_CHEST,
			BodyPart.LEFT_UPPER_ARM,
			BodyPart.RIGHT_UPPER_ARM,
			BodyPart.HIP,
			BodyPart.LEFT_UPPER_LEG,
			BodyPart.RIGHT_UPPER_LEG,
			BodyPart.LEFT_FOOT,
			BodyPart.RIGHT_FOOT,
		).map { registry[it.key]!! }.toSet()
		val toes = setOf(
			BodyPart.LEFT_BIG_TOE, BodyPart.LEFT_INDEX_TOE, BodyPart.LEFT_MIDDLE_TOE, BodyPart.LEFT_RING_TOE, BodyPart.LEFT_LITTLE_TOE,
			BodyPart.RIGHT_BIG_TOE, BodyPart.RIGHT_INDEX_TOE, BodyPart.RIGHT_MIDDLE_TOE, BodyPart.RIGHT_RING_TOE, BodyPart.RIGHT_LITTLE_TOE,
		).map { registry[it.key]!! }.toSet()
		assertEquals(oldEight + toes, definition.candidateBones)

		val hip = registry[BodyPart.HIP.key]!!
		assertEquals(
			setOf(BodyPart.HIP, BodyPart.LOWER_WAIST, BodyPart.UPPER_WAIST, BodyPart.LOWER_CHEST, BodyPart.UPPER_CHEST).map { registry[it.key]!! }.toSet(),
			definition.candidateSourcesOf(hip).toSet(),
		)
	}

	@Test
	fun `compiler reports an unknown bone named in candidateSources`() = runTest {
		val core = ResourcePackParser().parse(ClasspathResourcePackSource.core(javaClass.classLoader))
		val user = ResourcePackParser().parse(
			InMemoryResourcePackSource(
				mapOf(
					"manifest.json" to manifest("example:extra"),
					"data/bones/extra.json" to """{"key":"example:extra","nameKey":"example:bone.extra","parent":"slimevr:head","candidateSources":["example:missing"]}""",
					"assets/lang/en.json" to """{"example:bone.extra":"Extra bone"}""",
				),
			),
		)
		val error = assertFailsWith<ResourcePackCompilationException> { compileResourcePacks(catalog(core, user)) }
		assertTrue(error.diagnostics.any { "Unknown bone" in it.message && "candidateSources" in it.message })
	}

	@Test
	fun `bundled core pack's battery sources match the retired driver fallback table`() = runTest {
		val core = ResourcePackParser().parse(ClasspathResourcePackSource.core(javaClass.classLoader))
		val definition = compileResourcePacks(catalog(core))
		val registry = definition.registry
		fun sources(vararg parts: BodyPart) = parts.map { registry[it.key]!! }
		fun actual(part: BodyPart) = definition.batterySourcesOf(registry[part.key]!!)

		assertEquals(sources(BodyPart.UPPER_CHEST, BodyPart.LOWER_CHEST), actual(BodyPart.UPPER_CHEST))
		assertEquals(sources(BodyPart.HIP, BodyPart.LOWER_WAIST, BodyPart.UPPER_WAIST, BodyPart.LOWER_CHEST, BodyPart.UPPER_CHEST), actual(BodyPart.HIP))
		assertEquals(sources(BodyPart.LEFT_UPPER_LEG), actual(BodyPart.LEFT_UPPER_LEG))
		assertEquals(sources(BodyPart.RIGHT_UPPER_LEG), actual(BodyPart.RIGHT_UPPER_LEG))
		assertEquals(sources(BodyPart.LEFT_FOOT, BodyPart.LEFT_LOWER_LEG), actual(BodyPart.LEFT_FOOT))
		assertEquals(sources(BodyPart.RIGHT_FOOT, BodyPart.RIGHT_LOWER_LEG), actual(BodyPart.RIGHT_FOOT))
		assertEquals(sources(BodyPart.LEFT_UPPER_ARM, BodyPart.LEFT_LOWER_ARM), actual(BodyPart.LEFT_UPPER_ARM))
		assertEquals(sources(BodyPart.RIGHT_UPPER_ARM, BodyPart.RIGHT_LOWER_ARM), actual(BodyPart.RIGHT_UPPER_ARM))
		assertEquals(sources(BodyPart.LEFT_HAND, BodyPart.LEFT_LOWER_ARM), actual(BodyPart.LEFT_HAND))
		assertEquals(sources(BodyPart.RIGHT_HAND, BodyPart.RIGHT_LOWER_ARM), actual(BodyPart.RIGHT_HAND))
		assertEquals(emptyList(), actual(BodyPart.HEAD))
	}

	@Test
	fun `compiler reports an unknown bone named in batterySources`() = runTest {
		val core = ResourcePackParser().parse(ClasspathResourcePackSource.core(javaClass.classLoader))
		val user = ResourcePackParser().parse(
			InMemoryResourcePackSource(
				mapOf(
					"manifest.json" to manifest("example:extra"),
					"data/bones/extra.json" to """{"key":"example:extra","nameKey":"example:bone.extra","parent":"slimevr:head","batterySources":["example:missing"]}""",
					"assets/lang/en.json" to """{"example:bone.extra":"Extra bone"}""",
				),
			),
		)
		val error = assertFailsWith<ResourcePackCompilationException> { compileResourcePacks(catalog(core, user)) }
		assertTrue(error.diagnostics.any { "Unknown bone" in it.message && "batterySources" in it.message })
	}

	@Test
	fun `bundled core pack's VRChat input addresses match the retired tracker table`() = runTest {
		val core = ResourcePackParser().parse(ClasspathResourcePackSource.core(javaClass.classLoader))
		val definition = compileResourcePacks(catalog(core))
		val registry = definition.registry

		assertEquals(
			mapOf(
				"/tracking/vrsystem/head/pose" to registry[BodyPart.HEAD.key]!!,
				"/tracking/vrsystem/leftwrist/pose" to registry[BodyPart.LEFT_HAND.key]!!,
				"/tracking/vrsystem/rightwrist/pose" to registry[BodyPart.RIGHT_HAND.key]!!,
			),
			definition.vrchatInputAddresses,
		)
	}

	@Test
	fun `compiler reports an unknown bone named in a VRChat emit relativeTo`() = runTest {
		val core = ResourcePackParser().parse(ClasspathResourcePackSource.core(javaClass.classLoader))
		val user = ResourcePackParser().parse(
			InMemoryResourcePackSource(
				mapOf(
					"manifest.json" to manifest("example:extra"),
					"data/bones/extra.json" to """{"key":"example:extra","nameKey":"example:bone.extra","parent":"slimevr:head","outputs":{"vrchat":{"emit":{"/example":{"from":"rotation","relativeTo":"example:missing"}}}}}""",
					"assets/lang/en.json" to """{"example:bone.extra":"Extra bone"}""",
				),
			),
		)
		val error = assertFailsWith<ResourcePackCompilationException> { compileResourcePacks(catalog(core, user)) }
		assertTrue(error.diagnostics.any { "Unknown bone" in it.message && "outputs.vrchat.emit.relativeTo" in it.message })
	}

	@Test
	fun `compiler rejects duplicate VRChat input and emit addresses`() = runTest {
		val core = ResourcePackParser().parse(ClasspathResourcePackSource.core(javaClass.classLoader))
		val user = ResourcePackParser().parse(
			InMemoryResourcePackSource(
				mapOf(
					"manifest.json" to manifest("example:duplicate-addresses"),
					"data/bones/first.json" to """{"key":"example:first","nameKey":"example:first","parent":"slimevr:head","outputs":{"vrchat":{"emit":{"/example/output":{"from":"position"}}}},"inputs":{"vrchat":{"address":"/example/input"}}}""",
					"data/bones/second.json" to """{"key":"example:second","nameKey":"example:second","parent":"slimevr:head","outputs":{"vrchat":{"emit":{"/example/output":{"from":"rotation"}}}},"inputs":{"vrchat":{"address":"/example/input"}}}""",
				),
			),
		)

		val error = assertFailsWith<ResourcePackCompilationException> { compileResourcePacks(catalog(core, user)) }
		assertTrue(error.diagnostics.any { "Duplicate VRChat input address '/example/input'" in it.message })
		assertTrue(error.diagnostics.any { "Duplicate VRChat emit address '/example/output'" in it.message })
		assertTrue(error.diagnostics.filter { "Duplicate VRChat" in it.message }.all { it.path == "data/bones/second.json" })
	}

	@Test
	fun `compiler rejects invalid VRChat emit pipeline types`() = runTest {
		val core = ResourcePackParser().parse(ClasspathResourcePackSource.core(javaClass.classLoader))
		val user = ResourcePackParser().parse(
			InMemoryResourcePackSource(
				mapOf(
					"manifest.json" to manifest("example:invalid-pipelines"),
					"data/bones/extra.json" to """
						{"key":"example:extra","nameKey":"example:extra","parent":"slimevr:head","outputs":{"vrchat":{"emit":{
							"/example/euler":{"from":"position","value":[{"euler":"x"}]},
							"/example/vector-on-number":{"from":"rotation","value":[{"euler":"x"},{"scale":{"x":2}}]},
							"/example/boolean":{"from":"rotation","value":[{"euler":"x"},{"greaterThan":0},{"offset":1}]}
						}}}}
					""".trimIndent(),
				),
			),
		)

		val error = assertFailsWith<ResourcePackCompilationException> { compileResourcePacks(catalog(core, user)) }
		val diagnostics = error.diagnostics.filter { "VRChat emit" in it.message }
		assertEquals(3, diagnostics.size)
		assertTrue(diagnostics.any { "'/example/euler'" in it.message && "cannot apply euler to vector" in it.message })
		assertTrue(diagnostics.any { "'/example/vector-on-number'" in it.message && "cannot apply scale to number" in it.message })
		assertTrue(diagnostics.any { "'/example/boolean'" in it.message && "cannot apply offset to boolean" in it.message })
		assertTrue(diagnostics.all { it.packId == "example:invalid-pipelines" && it.path == "data/bones/extra.json" })
	}

	@Test
	fun `user-pack set overrides apply in pack order and merge VRChat emit addresses`() = runTest {
		val core = ResourcePackParser().parse(ClasspathResourcePackSource.core(javaClass.classLoader))
		val first = ResourcePackParser().parse(
			InMemoryResourcePackSource(
				mapOf(
					"manifest.json" to manifest("example:first"),
					"data/overrides/bones/hip.json" to """{"target":"slimevr:hip","set":{"batterySources":["slimevr:upper_chest"],"outputs":{"vrchat":{"emit":{"/example/first":{"from":"position"}}}}}}""",
				),
			),
		)
		val second = ResourcePackParser().parse(
			InMemoryResourcePackSource(
				mapOf(
					"manifest.json" to manifest("example:second"),
					"data/overrides/bones/hip.json" to """{"target":"slimevr:hip","set":{"batterySources":["slimevr:lower_chest"],"outputs":{"vrchat":{"emit":{"/example/second":{"from":"rotation"}}}}}}""",
				),
			),
		)
		val definition = compileResourcePacks(catalog(core, first, second))
		val hip = definition.registry[BodyPart.HIP.key]!!
		assertEquals(listOf(definition.registry[BodyPart.LOWER_CHEST.key]!!), definition.batterySourcesOf(hip))
		assertTrue("/tracking/trackers/1/position" in definition.emitEntriesOf(hip))
		assertTrue("/example/first" in definition.emitEntriesOf(hip))
		assertTrue("/example/second" in definition.emitEntriesOf(hip))
	}

	@Test
	fun `a later pack can set a property removed by an earlier pack`() = runTest {
		val core = ResourcePackParser().parse(ClasspathResourcePackSource.core(javaClass.classLoader))
		val remove = ResourcePackParser().parse(
			InMemoryResourcePackSource(
				mapOf(
					"manifest.json" to manifest("example:remove"),
					"data/overrides/bones/hip.json" to """{"target":"slimevr:hip","remove":[["batterySources"]]}""",
				),
			),
		)
		val set = ResourcePackParser().parse(
			InMemoryResourcePackSource(
				mapOf(
					"manifest.json" to manifest("example:set"),
					"data/overrides/bones/hip.json" to """{"target":"slimevr:hip","set":{"batterySources":["slimevr:lower_chest"]}}""",
				),
			),
		)
		val definition = compileResourcePacks(catalog(core, remove, set))
		val hip = definition.registry[BodyPart.HIP.key]!!
		assertEquals(listOf(definition.registry[BodyPart.LOWER_CHEST.key]!!), definition.batterySourcesOf(hip))
	}

	@Test
	fun `one override document applies set before remove`() = runTest {
		val core = ResourcePackParser().parse(ClasspathResourcePackSource.core(javaClass.classLoader))
		val user = ResourcePackParser().parse(
			InMemoryResourcePackSource(
				mapOf(
					"manifest.json" to manifest("example:set-remove"),
					"data/overrides/bones/hip.json" to """{"target":"slimevr:hip","set":{"batterySources":["slimevr:lower_chest"]},"remove":[["batterySources"]]}""",
				),
			),
		)
		val definition = compileResourcePacks(catalog(core, user))
		assertEquals(emptyList(), definition.batterySourcesOf(definition.registry[BodyPart.HIP.key]!!))
	}

	@Test
	fun `compiler reports unknown bone and proportion override targets even for remove-only overrides`() = runTest {
		val core = ResourcePackParser().parse(ClasspathResourcePackSource.core(javaClass.classLoader))
		val user = ResourcePackParser().parse(
			InMemoryResourcePackSource(
				mapOf(
					"manifest.json" to manifest("example:unknown-targets"),
					"data/overrides/bones/missing.json" to """{"target":"example:missing","remove":[["mirror"]]}""",
					"data/overrides/proportions/missing.json" to """{"target":"example:missing","remove":["descriptionKey"]}""",
				),
			),
		)
		val error = assertFailsWith<ResourcePackCompilationException> { compileResourcePacks(catalog(core, user)) }
		assertEquals(2, error.diagnostics.size)
		assertTrue(error.diagnostics.any { it.path == "data/overrides/bones/missing.json" && "Unknown bone" in it.message })
		assertTrue(error.diagnostics.any { it.path == "data/overrides/proportions/missing.json" && "Unknown proportion" in it.message })
	}

	@Test
	fun `removing the last nested override value removes its empty containers`() = runTest {
		val core = ResourcePackParser().parse(ClasspathResourcePackSource.core(javaClass.classLoader))
		val user = ResourcePackParser().parse(
			InMemoryResourcePackSource(
				mapOf(
					"manifest.json" to manifest("example:normalize"),
					"data/overrides/bones/head.json" to """{"target":"slimevr:head","remove":[["outputs","vrchat","emit","/tracking/trackers/head/position"],["inputs","vrchat"]]}""",
					"data/overrides/bones/upper-waist.json" to """{"target":"slimevr:upper_waist","remove":[["outputs","vmc"]]}""",
				),
			),
		)
		val definition = compileResourcePacks(catalog(core, user))
		val head = definition.registry[BodyPart.HEAD.key]!!
		assertFalse(head in definition.acceptedBones(RoutingOutput.VRC_OSC))
		assertFalse(head in definition.requiredBones(RoutingOutput.VRC_OSC))
		assertEquals(emptyMap(), definition.emitEntriesOf(head))
		assertFalse("/tracking/vrsystem/head/pose" in definition.vrchatInputAddresses)
		assertFalse(definition.registry[BodyPart.UPPER_WAIST.key]!! in definition.acceptedBones(RoutingOutput.VMC))
	}

	@Test
	fun `compiler attributes invalid references introduced by an override to that override`() = runTest {
		val core = ResourcePackParser().parse(ClasspathResourcePackSource.core(javaClass.classLoader))
		val user = ResourcePackParser().parse(
			InMemoryResourcePackSource(
				mapOf(
					"manifest.json" to manifest("example:invalid-reference"),
					"data/overrides/bones/hip.json" to """{"target":"slimevr:hip","set":{"batterySources":["example:missing"]}}""",
				),
			),
		)
		val error = assertFailsWith<ResourcePackCompilationException> { compileResourcePacks(catalog(core, user)) }
		val diagnostic = error.diagnostics.single { "batterySources" in it.message }
		assertEquals("example:invalid-reference", diagnostic.packId)
		assertEquals("data/overrides/bones/hip.json", diagnostic.path)
	}

	@Test
	fun `compiler rejects removing required or unknown bone override properties`() = runTest {
		val core = ResourcePackParser().parse(ClasspathResourcePackSource.core(javaClass.classLoader))
		val user = ResourcePackParser().parse(
			InMemoryResourcePackSource(
				mapOf(
					"manifest.json" to manifest("example:remove"),
					"data/overrides/bones/hip.json" to """{"target":"slimevr:hip","remove":[["nameKey"],["notAProperty"]]}""",
				),
			),
		)
		val error = assertFailsWith<ResourcePackCompilationException> { compileResourcePacks(catalog(core, user)) }
		assertEquals(2, error.diagnostics.size)
		assertTrue(error.diagnostics.all { "Cannot remove" in it.message })
	}

	@Test
	fun `compiler rejects duplicate VMC names`() = runTest {
		val core = ResourcePackParser().parse(ClasspathResourcePackSource.core(javaClass.classLoader))
		val user = ResourcePackParser().parse(
			InMemoryResourcePackSource(
				mapOf(
					"manifest.json" to manifest("example:vmc-names"),
					"data/bones/first.json" to """{"key":"example:first","nameKey":"example:first","parent":"slimevr:head","outputs":{"vmc":{"name":"ExtraBone"}}}""",
					"data/bones/second.json" to """{"key":"example:second","nameKey":"example:second","parent":"slimevr:head","outputs":{"vmc":{"name":"ExtraBone"}}}""",
				),
			),
		)
		val error = assertFailsWith<ResourcePackCompilationException> { compileResourcePacks(catalog(core, user)) }
		val diagnostic = error.diagnostics.single { "Duplicate VMC name" in it.message }
		assertEquals("example:vmc-names", diagnostic.packId)
		assertEquals("data/bones/second.json", diagnostic.path)
	}

	@Test
	fun `compiler rejects VMC parents without VMC output metadata`() = runTest {
		val core = ResourcePackParser().parse(ClasspathResourcePackSource.core(javaClass.classLoader))
		val user = ResourcePackParser().parse(
			InMemoryResourcePackSource(
				mapOf(
					"manifest.json" to manifest("example:vmc-parent"),
					"data/bones/extra.json" to """{"key":"example:extra","nameKey":"example:extra","parent":"slimevr:head","outputs":{"vmc":{"name":"ExtraBone","outputParent":"slimevr:lower_waist","inputParent":"slimevr:lower_waist"}}}""",
				),
			),
		)
		val error = assertFailsWith<ResourcePackCompilationException> { compileResourcePacks(catalog(core, user)) }
		assertTrue(error.diagnostics.any { "VMC outputParent" in it.message && "does not have a VMC output" in it.message })
		assertTrue(error.diagnostics.any { "VMC inputParent" in it.message && "does not have a VMC output" in it.message })
	}

	@Test
	fun `compiler rejects cycles in both VMC parent graphs`() = runTest {
		val core = ResourcePackParser().parse(ClasspathResourcePackSource.core(javaClass.classLoader))
		val user = ResourcePackParser().parse(
			InMemoryResourcePackSource(
				mapOf(
					"manifest.json" to manifest("example:vmc-cycle"),
					"data/bones/first.json" to """{"key":"example:first","nameKey":"example:first","parent":"slimevr:head","outputs":{"vmc":{"name":"ExtraFirst","outputParent":"example:second","inputParent":"example:second"}}}""",
					"data/bones/second.json" to """{"key":"example:second","nameKey":"example:second","parent":"slimevr:head","outputs":{"vmc":{"name":"ExtraSecond","outputParent":"example:first","inputParent":"example:first"}}}""",
				),
			),
		)
		val error = assertFailsWith<ResourcePackCompilationException> { compileResourcePacks(catalog(core, user)) }
		assertTrue(error.diagnostics.any { "VMC outputParent graph contains a cycle" in it.message })
		assertTrue(error.diagnostics.any { "VMC inputParent graph contains a cycle" in it.message })
	}

	@Test
	fun `VMC input order includes every explicit input root`() = runTest {
		val core = ResourcePackParser().parse(ClasspathResourcePackSource.core(javaClass.classLoader))
		val user = ResourcePackParser().parse(
			InMemoryResourcePackSource(
				mapOf(
					"manifest.json" to manifest("example:vmc-root"),
					"data/bones/extra.json" to """{"key":"example:extra","nameKey":"example:extra","parent":"slimevr:head","outputs":{"vmc":{"name":"ExtraRoot","outputParent":"slimevr:head","inputParent":null}}}""",
				),
			),
		)
		val definition = compileResourcePacks(catalog(core, user))
		assertTrue(definition.registry["example:extra"]!! in definition.vmcInputOrder)
		assertEquals(definition.vmcNamedBones, definition.vmcInputOrder.toSet())
	}

	@Test
	fun `bundled core pack's VMC output metadata matches the retired hardcoded tables`() = runTest {
		val core = ResourcePackParser().parse(ClasspathResourcePackSource.core(javaClass.classLoader))
		val definition = compileResourcePacks(catalog(core))
		val registry = definition.registry
		fun id(part: BodyPart) = registry[part.key]!!

		val names: Map<BodyPart, List<String>> = mapOf(
			BodyPart.HEAD to listOf("Head"),
			BodyPart.NECK to listOf("Neck"),
			BodyPart.UPPER_CHEST to listOf("UpperChest"),
			BodyPart.LOWER_CHEST to listOf("Chest"),
			BodyPart.UPPER_WAIST to listOf("Spine"),
			BodyPart.HIP to listOf("Hips"),
			BodyPart.LEFT_SHOULDER to listOf("LeftShoulder"),
			BodyPart.RIGHT_SHOULDER to listOf("RightShoulder"),
			BodyPart.LEFT_UPPER_ARM to listOf("LeftUpperArm"),
			BodyPart.RIGHT_UPPER_ARM to listOf("RightUpperArm"),
			BodyPart.LEFT_LOWER_ARM to listOf("LeftLowerArm"),
			BodyPart.RIGHT_LOWER_ARM to listOf("RightLowerArm"),
			BodyPart.LEFT_HAND to listOf("LeftHand"),
			BodyPart.RIGHT_HAND to listOf("RightHand"),
			BodyPart.LEFT_UPPER_LEG to listOf("LeftUpperLeg"),
			BodyPart.RIGHT_UPPER_LEG to listOf("RightUpperLeg"),
			BodyPart.LEFT_LOWER_LEG to listOf("LeftLowerLeg"),
			BodyPart.RIGHT_LOWER_LEG to listOf("RightLowerLeg"),
			BodyPart.LEFT_FOOT to listOf("LeftFoot"),
			BodyPart.RIGHT_FOOT to listOf("RightFoot"),
			BodyPart.LEFT_THUMB_METACARPAL to listOf("LeftThumbProximal"),
			BodyPart.LEFT_THUMB_PROXIMAL to listOf("LeftThumbIntermediate"),
			BodyPart.LEFT_THUMB_DISTAL to listOf("LeftThumbDistal"),
			BodyPart.LEFT_INDEX_PROXIMAL to listOf("LeftIndexProximal"),
			BodyPart.LEFT_INDEX_INTERMEDIATE to listOf("LeftIndexIntermediate"),
			BodyPart.LEFT_INDEX_DISTAL to listOf("LeftIndexDistal"),
			BodyPart.LEFT_MIDDLE_PROXIMAL to listOf("LeftMiddleProximal"),
			BodyPart.LEFT_MIDDLE_INTERMEDIATE to listOf("LeftMiddleIntermediate"),
			BodyPart.LEFT_MIDDLE_DISTAL to listOf("LeftMiddleDistal"),
			BodyPart.LEFT_RING_PROXIMAL to listOf("LeftRingProximal"),
			BodyPart.LEFT_RING_INTERMEDIATE to listOf("LeftRingIntermediate"),
			BodyPart.LEFT_RING_DISTAL to listOf("LeftRingDistal"),
			BodyPart.LEFT_LITTLE_PROXIMAL to listOf("LeftLittleProximal"),
			BodyPart.LEFT_LITTLE_INTERMEDIATE to listOf("LeftLittleIntermediate"),
			BodyPart.LEFT_LITTLE_DISTAL to listOf("LeftLittleDistal"),
			BodyPart.RIGHT_THUMB_METACARPAL to listOf("RightThumbProximal"),
			BodyPart.RIGHT_THUMB_PROXIMAL to listOf("RightThumbIntermediate"),
			BodyPart.RIGHT_THUMB_DISTAL to listOf("RightThumbDistal"),
			BodyPart.RIGHT_INDEX_PROXIMAL to listOf("RightIndexProximal"),
			BodyPart.RIGHT_INDEX_INTERMEDIATE to listOf("RightIndexIntermediate"),
			BodyPart.RIGHT_INDEX_DISTAL to listOf("RightIndexDistal"),
			BodyPart.RIGHT_MIDDLE_PROXIMAL to listOf("RightMiddleProximal"),
			BodyPart.RIGHT_MIDDLE_INTERMEDIATE to listOf("RightMiddleIntermediate"),
			BodyPart.RIGHT_MIDDLE_DISTAL to listOf("RightMiddleDistal"),
			BodyPart.RIGHT_RING_PROXIMAL to listOf("RightRingProximal"),
			BodyPart.RIGHT_RING_INTERMEDIATE to listOf("RightRingIntermediate"),
			BodyPart.RIGHT_RING_DISTAL to listOf("RightRingDistal"),
			BodyPart.RIGHT_LITTLE_PROXIMAL to listOf("RightLittleProximal"),
			BodyPart.RIGHT_LITTLE_INTERMEDIATE to listOf("RightLittleIntermediate"),
			BodyPart.RIGHT_LITTLE_DISTAL to listOf("RightLittleDistal"),
			BodyPart.LEFT_BIG_TOE to listOf("LeftToes", "LeftBigToe"),
			BodyPart.LEFT_INDEX_TOE to listOf("LeftIndexToe"),
			BodyPart.LEFT_MIDDLE_TOE to listOf("LeftMiddleToe"),
			BodyPart.LEFT_RING_TOE to listOf("LeftRingToe"),
			BodyPart.LEFT_LITTLE_TOE to listOf("LeftLittleToe"),
			BodyPart.RIGHT_BIG_TOE to listOf("RightToes", "RightBigToe"),
			BodyPart.RIGHT_INDEX_TOE to listOf("RightIndexToe"),
			BodyPart.RIGHT_MIDDLE_TOE to listOf("RightMiddleToe"),
			BodyPart.RIGHT_RING_TOE to listOf("RightRingToe"),
			BodyPart.RIGHT_LITTLE_TOE to listOf("RightLittleToe"),
		)
		assertEquals(60, names.size)
		assertEquals(names.keys.map(::id).toSet(), definition.vmcNamedBones)
		assertNull(definition.vmcOutputOf(id(BodyPart.LOWER_WAIST)), "lower_waist has no VMC output")

		// outputParent: HIP-rooted re-derivation of the real hierarchy, matching the retired
		// VMC_OUTPUT_BONE_PARENTS exactly. inputParent matches outputParent everywhere except the
		// 3 bones that restate an override (neck, left_shoulder, right_shoulder end up parented to
		// upper_chest for input, but to lower_chest for output).
		val outputParent: Map<BodyPart, BodyPart?> = mapOf(
			BodyPart.HIP to null,
			BodyPart.UPPER_WAIST to BodyPart.HIP,
			BodyPart.LOWER_CHEST to BodyPart.UPPER_WAIST,
			BodyPart.UPPER_CHEST to BodyPart.LOWER_CHEST,
			BodyPart.NECK to BodyPart.LOWER_CHEST,
			BodyPart.HEAD to BodyPart.NECK,
			BodyPart.LEFT_UPPER_LEG to BodyPart.HIP,
			BodyPart.RIGHT_UPPER_LEG to BodyPart.HIP,
			BodyPart.LEFT_LOWER_LEG to BodyPart.LEFT_UPPER_LEG,
			BodyPart.RIGHT_LOWER_LEG to BodyPart.RIGHT_UPPER_LEG,
			BodyPart.LEFT_FOOT to BodyPart.LEFT_LOWER_LEG,
			BodyPart.RIGHT_FOOT to BodyPart.RIGHT_LOWER_LEG,
			BodyPart.LEFT_SHOULDER to BodyPart.LOWER_CHEST,
			BodyPart.RIGHT_SHOULDER to BodyPart.LOWER_CHEST,
			BodyPart.LEFT_UPPER_ARM to BodyPart.LEFT_SHOULDER,
			BodyPart.RIGHT_UPPER_ARM to BodyPart.RIGHT_SHOULDER,
			BodyPart.LEFT_LOWER_ARM to BodyPart.LEFT_UPPER_ARM,
			BodyPart.RIGHT_LOWER_ARM to BodyPart.RIGHT_UPPER_ARM,
			BodyPart.LEFT_HAND to BodyPart.LEFT_LOWER_ARM,
			BodyPart.RIGHT_HAND to BodyPart.RIGHT_LOWER_ARM,
			BodyPart.LEFT_THUMB_METACARPAL to BodyPart.LEFT_HAND,
			BodyPart.LEFT_THUMB_PROXIMAL to BodyPart.LEFT_THUMB_METACARPAL,
			BodyPart.LEFT_THUMB_DISTAL to BodyPart.LEFT_THUMB_PROXIMAL,
			BodyPart.LEFT_INDEX_PROXIMAL to BodyPart.LEFT_HAND,
			BodyPart.LEFT_INDEX_INTERMEDIATE to BodyPart.LEFT_INDEX_PROXIMAL,
			BodyPart.LEFT_INDEX_DISTAL to BodyPart.LEFT_INDEX_INTERMEDIATE,
			BodyPart.LEFT_MIDDLE_PROXIMAL to BodyPart.LEFT_HAND,
			BodyPart.LEFT_MIDDLE_INTERMEDIATE to BodyPart.LEFT_MIDDLE_PROXIMAL,
			BodyPart.LEFT_MIDDLE_DISTAL to BodyPart.LEFT_MIDDLE_INTERMEDIATE,
			BodyPart.LEFT_RING_PROXIMAL to BodyPart.LEFT_HAND,
			BodyPart.LEFT_RING_INTERMEDIATE to BodyPart.LEFT_RING_PROXIMAL,
			BodyPart.LEFT_RING_DISTAL to BodyPart.LEFT_RING_INTERMEDIATE,
			BodyPart.LEFT_LITTLE_PROXIMAL to BodyPart.LEFT_HAND,
			BodyPart.LEFT_LITTLE_INTERMEDIATE to BodyPart.LEFT_LITTLE_PROXIMAL,
			BodyPart.LEFT_LITTLE_DISTAL to BodyPart.LEFT_LITTLE_INTERMEDIATE,
			BodyPart.RIGHT_THUMB_METACARPAL to BodyPart.RIGHT_HAND,
			BodyPart.RIGHT_THUMB_PROXIMAL to BodyPart.RIGHT_THUMB_METACARPAL,
			BodyPart.RIGHT_THUMB_DISTAL to BodyPart.RIGHT_THUMB_PROXIMAL,
			BodyPart.RIGHT_INDEX_PROXIMAL to BodyPart.RIGHT_HAND,
			BodyPart.RIGHT_INDEX_INTERMEDIATE to BodyPart.RIGHT_INDEX_PROXIMAL,
			BodyPart.RIGHT_INDEX_DISTAL to BodyPart.RIGHT_INDEX_INTERMEDIATE,
			BodyPart.RIGHT_MIDDLE_PROXIMAL to BodyPart.RIGHT_HAND,
			BodyPart.RIGHT_MIDDLE_INTERMEDIATE to BodyPart.RIGHT_MIDDLE_PROXIMAL,
			BodyPart.RIGHT_MIDDLE_DISTAL to BodyPart.RIGHT_MIDDLE_INTERMEDIATE,
			BodyPart.RIGHT_RING_PROXIMAL to BodyPart.RIGHT_HAND,
			BodyPart.RIGHT_RING_INTERMEDIATE to BodyPart.RIGHT_RING_PROXIMAL,
			BodyPart.RIGHT_RING_DISTAL to BodyPart.RIGHT_RING_INTERMEDIATE,
			BodyPart.RIGHT_LITTLE_PROXIMAL to BodyPart.RIGHT_HAND,
			BodyPart.RIGHT_LITTLE_INTERMEDIATE to BodyPart.RIGHT_LITTLE_PROXIMAL,
			BodyPart.RIGHT_LITTLE_DISTAL to BodyPart.RIGHT_LITTLE_INTERMEDIATE,
			BodyPart.LEFT_BIG_TOE to BodyPart.LEFT_FOOT,
			BodyPart.LEFT_INDEX_TOE to BodyPart.LEFT_FOOT,
			BodyPart.LEFT_MIDDLE_TOE to BodyPart.LEFT_FOOT,
			BodyPart.LEFT_RING_TOE to BodyPart.LEFT_FOOT,
			BodyPart.LEFT_LITTLE_TOE to BodyPart.LEFT_FOOT,
			BodyPart.RIGHT_BIG_TOE to BodyPart.RIGHT_FOOT,
			BodyPart.RIGHT_INDEX_TOE to BodyPart.RIGHT_FOOT,
			BodyPart.RIGHT_MIDDLE_TOE to BodyPart.RIGHT_FOOT,
			BodyPart.RIGHT_RING_TOE to BodyPart.RIGHT_FOOT,
			BodyPart.RIGHT_LITTLE_TOE to BodyPart.RIGHT_FOOT,
		)
		val inputParentOverrides = mapOf(
			BodyPart.NECK to BodyPart.UPPER_CHEST,
			BodyPart.LEFT_SHOULDER to BodyPart.UPPER_CHEST,
			BodyPart.RIGHT_SHOULDER to BodyPart.UPPER_CHEST,
		)

		for ((part, names2) in names) {
			val output = definition.vmcOutputOf(id(part))!!
			assertEquals(names2, output.names, "$part names")
			assertEquals(outputParent[part]?.let(::id), output.outputParent, "$part outputParent")
			assertEquals((inputParentOverrides[part] ?: outputParent[part])?.let(::id), output.inputParent, "$part inputParent")
		}

		// Rest rotation: only the arms and fingers deviate from IDENTITY, mirrored left/right.
		val leftArm = Quaternion.rotationAroundZAxis(-FastMath.HALF_PI)
		val rightArm = Quaternion.rotationAroundZAxis(FastMath.HALF_PI)
		val leftFingers = setOf(
			BodyPart.LEFT_THUMB_METACARPAL, BodyPart.LEFT_THUMB_PROXIMAL, BodyPart.LEFT_THUMB_DISTAL,
			BodyPart.LEFT_INDEX_PROXIMAL, BodyPart.LEFT_INDEX_INTERMEDIATE, BodyPart.LEFT_INDEX_DISTAL,
			BodyPart.LEFT_MIDDLE_PROXIMAL, BodyPart.LEFT_MIDDLE_INTERMEDIATE, BodyPart.LEFT_MIDDLE_DISTAL,
			BodyPart.LEFT_RING_PROXIMAL, BodyPart.LEFT_RING_INTERMEDIATE, BodyPart.LEFT_RING_DISTAL,
			BodyPart.LEFT_LITTLE_PROXIMAL, BodyPart.LEFT_LITTLE_INTERMEDIATE, BodyPart.LEFT_LITTLE_DISTAL,
		)
		val rightFingers = setOf(
			BodyPart.RIGHT_THUMB_METACARPAL, BodyPart.RIGHT_THUMB_PROXIMAL, BodyPart.RIGHT_THUMB_DISTAL,
			BodyPart.RIGHT_INDEX_PROXIMAL, BodyPart.RIGHT_INDEX_INTERMEDIATE, BodyPart.RIGHT_INDEX_DISTAL,
			BodyPart.RIGHT_MIDDLE_PROXIMAL, BodyPart.RIGHT_MIDDLE_INTERMEDIATE, BodyPart.RIGHT_MIDDLE_DISTAL,
			BodyPart.RIGHT_RING_PROXIMAL, BodyPart.RIGHT_RING_INTERMEDIATE, BodyPart.RIGHT_RING_DISTAL,
			BodyPart.RIGHT_LITTLE_PROXIMAL, BodyPart.RIGHT_LITTLE_INTERMEDIATE, BodyPart.RIGHT_LITTLE_DISTAL,
		)
		val leftRest = setOf(BodyPart.LEFT_UPPER_ARM, BodyPart.LEFT_LOWER_ARM, BodyPart.LEFT_HAND) + leftFingers
		val rightRest = setOf(BodyPart.RIGHT_UPPER_ARM, BodyPart.RIGHT_LOWER_ARM, BodyPart.RIGHT_HAND) + rightFingers

		for (part in names.keys) {
			val expected = when (part) {
				in leftRest -> leftArm
				in rightRest -> rightArm
				else -> Quaternion.IDENTITY
			}
			assertQuaternionEquals(expected, definition.vmcOutputOf(id(part))!!.restRotation, "$part restRotation")
		}

		// mirror pairs, symmetric both ways.
		val mirrorPairs = listOf(
			BodyPart.LEFT_SHOULDER to BodyPart.RIGHT_SHOULDER,
			BodyPart.LEFT_UPPER_ARM to BodyPart.RIGHT_UPPER_ARM,
			BodyPart.LEFT_LOWER_ARM to BodyPart.RIGHT_LOWER_ARM,
			BodyPart.LEFT_HAND to BodyPart.RIGHT_HAND,
			BodyPart.LEFT_UPPER_LEG to BodyPart.RIGHT_UPPER_LEG,
			BodyPart.LEFT_LOWER_LEG to BodyPart.RIGHT_LOWER_LEG,
			BodyPart.LEFT_FOOT to BodyPart.RIGHT_FOOT,
			BodyPart.LEFT_THUMB_METACARPAL to BodyPart.RIGHT_THUMB_METACARPAL,
			BodyPart.LEFT_THUMB_PROXIMAL to BodyPart.RIGHT_THUMB_PROXIMAL,
			BodyPart.LEFT_THUMB_DISTAL to BodyPart.RIGHT_THUMB_DISTAL,
			BodyPart.LEFT_INDEX_PROXIMAL to BodyPart.RIGHT_INDEX_PROXIMAL,
			BodyPart.LEFT_INDEX_INTERMEDIATE to BodyPart.RIGHT_INDEX_INTERMEDIATE,
			BodyPart.LEFT_INDEX_DISTAL to BodyPart.RIGHT_INDEX_DISTAL,
			BodyPart.LEFT_MIDDLE_PROXIMAL to BodyPart.RIGHT_MIDDLE_PROXIMAL,
			BodyPart.LEFT_MIDDLE_INTERMEDIATE to BodyPart.RIGHT_MIDDLE_INTERMEDIATE,
			BodyPart.LEFT_MIDDLE_DISTAL to BodyPart.RIGHT_MIDDLE_DISTAL,
			BodyPart.LEFT_RING_PROXIMAL to BodyPart.RIGHT_RING_PROXIMAL,
			BodyPart.LEFT_RING_INTERMEDIATE to BodyPart.RIGHT_RING_INTERMEDIATE,
			BodyPart.LEFT_RING_DISTAL to BodyPart.RIGHT_RING_DISTAL,
			BodyPart.LEFT_LITTLE_PROXIMAL to BodyPart.RIGHT_LITTLE_PROXIMAL,
			BodyPart.LEFT_LITTLE_INTERMEDIATE to BodyPart.RIGHT_LITTLE_INTERMEDIATE,
			BodyPart.LEFT_LITTLE_DISTAL to BodyPart.RIGHT_LITTLE_DISTAL,
			BodyPart.LEFT_BIG_TOE to BodyPart.RIGHT_BIG_TOE,
			BodyPart.LEFT_INDEX_TOE to BodyPart.RIGHT_INDEX_TOE,
			BodyPart.LEFT_MIDDLE_TOE to BodyPart.RIGHT_MIDDLE_TOE,
			BodyPart.LEFT_RING_TOE to BodyPart.RIGHT_RING_TOE,
			BodyPart.LEFT_LITTLE_TOE to BodyPart.RIGHT_LITTLE_TOE,
		)
		for ((left, right) in mirrorPairs) {
			assertEquals(id(right), definition.mirrorOf(id(left)), "$left mirrors to $right")
			assertEquals(id(left), definition.mirrorOf(id(right)), "$right mirrors to $left")
		}
		assertEquals(id(BodyPart.HIP), definition.mirrorOf(id(BodyPart.HIP)), "unmirrored bone mirrors to itself")

		// unityNameToBone is the exact, case-sensitive inverse of names.
		assertEquals(id(BodyPart.HIP), definition.unityNameToBone["Hips"])
		assertEquals(id(BodyPart.LEFT_BIG_TOE), definition.unityNameToBone["LeftBigToe"])
		assertEquals(id(BodyPart.LEFT_BIG_TOE), definition.unityNameToBone["LeftToes"])
		assertNull(definition.unityNameToBone["hips"])
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
