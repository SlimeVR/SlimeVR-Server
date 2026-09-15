package dev.slimevr.resourcepacks

import dev.slimevr.resourcepacks.compiler.resolveResourcePacks
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonObject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ResourcePackParserTest {
	@Test
	fun `a new resource type resolves without changing the skeleton compiler`() = runBlocking {
		val notes = object : ResourceType<JsonObject>("note") {
			override fun matches(path: String) = path.startsWith("data/example/notes/")
			override fun validateAndDecode(path: String, document: JsonObject, diagnostics: MutableList<in ResourcePackDiagnostic>) = SourcedResource(path, document, document)
		}
		val pack = ResourcePackParser.parse(
			InMemoryResourcePackSource(
				mapOf(
					"manifest.json" to manifest("example:notes"),
					"data/example/notes/base.json" to """{"title":"Base"}""",
					"data/example/notes/derived.json" to """{"${'$'}extend":"data/example/notes/base.json","body":"Content"}""",
				),
			),
			ResourceTypes.ALL + notes,
		)
		val resolved = resolveResourcePacks(ResourcePackCatalog(pack, emptyList(), emptyList()))
		assertEquals(setOf("title", "body"), resolved.get(notes).getValue("data/example/notes/derived.json").resource.value.keys)
	}

	@Test
	fun `manifest rejections report only the manifest`() = runBlocking {
		for (manifestJson in listOf("{not json}", "{}")) {
			val error = assertFailsWith<ResourcePackParseException> {
				ResourcePackParser.parse(
					InMemoryResourcePackSource(
						mapOf(
							"manifest.json" to manifestJson,
							"data/not-a-definition.json" to "{}",
						),
					),
				)
			}
			assertEquals(listOf("manifest.json"), error.diagnostics.map { it.path }, manifestJson)
		}
	}

	@Test
	fun `unrecognized assets are retained without reading their payload`() = runBlocking {
		var reads = 0
		val bytes = byteArrayOf(0, -1, 42)
		val source = object : ResourcePackSource {
			override val description = "lazy binary asset"
			override suspend fun entries() = listOf(
				ResourcePackEntry("manifest.json", manifest("example:asset")),
				ResourcePackEntry("assets/example/picture.bin") {
					reads++
					bytes
				},
			)
		}
		val pack = ResourcePackParser.parse(source)
		assertEquals(0, reads)
		assertTrue(pack.entries.getValue("assets/example/picture.bin").readBytes().contentEquals(bytes))
		assertEquals(1, reads)
	}

	@Test
	fun `every typed bone variant decodes`() = runBlocking {
		val pack = ResourcePackParser.parse(
			InMemoryResourcePackSource(
				mapOf(
					"manifest.json" to manifest("example:variants"),
					"data/example/bones/omitted.json" to bone("example:omitted", "{\"name\":\"Omitted\"}"),
					"data/example/bones/null.json" to bone("example:null", "{\"name\":\"Null\",\"inputParent\":null}"),
					"data/example/bones/key.json" to bone("example:key", "{\"name\":\"Key\",\"inputParent\":\"example:parent\"}"),
					"data/example/bones/branch.json" to """
					{"key":"example:branch","nameKey":"example:name",
					"rotationFallback":{"type":"firstActive","sources":["example:first","example:last"]},
					"constraint":{"type":"hinge","minDegrees":-1,"maxDegrees":1,"axis":{"x":1,"y":0,"z":0}},
					"outputs":{"vrchat":{"emit":{"/example":{"from":"rotation","value":[
						{"euler":"x"},{"euler":{"order":"YXZ","unit":"radians"}},{"scale":2},{"divide":{"x":2}},{"offset":{"z":1}},{"clamp":[-1,1]},{"greaterThan":0},{"lessThan":2}
					]}}}}}
					""".trimIndent(),
				),
			),
		)

		assertEquals(VmcInputParent.Omitted, pack.decoded(ResourceTypes.BONE).single { it.value.key == "example:omitted" }.value.outputs!!.vmc!!.inputParent)
		assertEquals(VmcInputParent.ExplicitNull, pack.decoded(ResourceTypes.BONE).single { it.value.key == "example:null" }.value.outputs!!.vmc!!.inputParent)
		assertEquals(VmcInputParent.Bone("example:parent"), pack.decoded(ResourceTypes.BONE).single { it.value.key == "example:key" }.value.outputs!!.vmc!!.inputParent)

		val branch = pack.decoded(ResourceTypes.BONE).single { it.value.key == "example:branch" }.value
		assertIs<FirstActiveRotationFallback>(branch.rotationFallback)
		assertIs<HingeConstraint>(branch.constraint)
		val steps = branch.outputs!!.vrchat!!.emit.getValue("/example").value!!
		val bareAxis = steps[0]
		assertIs<PipelineStep.Euler>(bareAxis)
		assertEquals(Axis.X, bareAxis.spec.axis)
		val orderedEuler = steps[1]
		assertIs<PipelineStep.Euler>(orderedEuler)
		assertEquals(EulerOrder.YXZ, orderedEuler.spec.order)
		val scale = steps[2]
		assertIs<PipelineStep.Scale>(scale)
		assertIs<ScalarOrVector.Scalar>(scale.operand)
		val divide = steps[3]
		assertIs<PipelineStep.Divide>(divide)
		assertIs<ScalarOrVector.Vector>(divide.operand)
		val clamp = steps[5]
		assertIs<PipelineStep.Clamp>(clamp)
		assertEquals(listOf(-1f, 1f), clamp.bounds)
		val greaterThan = steps[6]
		assertIs<PipelineStep.GreaterThan>(greaterThan)
		assertEquals(0f, greaterThan.threshold)
		val lessThan = steps[7]
		assertIs<PipelineStep.LessThan>(lessThan)
		assertEquals(2f, lessThan.threshold)
	}
}

private fun bone(key: String, vmc: String) = """
	{"key":"$key","nameKey":"example:name","outputs":{"vmc":$vmc}}
""".trimIndent()
