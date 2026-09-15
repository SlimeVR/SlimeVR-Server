package dev.slimevr.resourcepacks

import dev.slimevr.config.ConfigStorage
import dev.slimevr.config.StorageEntry
import dev.slimevr.config.StorageEntryType
import dev.slimevr.config.TextFileHandle
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonPrimitive
import java.io.File
import java.net.URLClassLoader
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ResourcePackParserTest {
	@Test
	fun `bundled core pack is packaged and parsed`(): Unit = runBlocking {
		val core = ResourcePackParser.parse(ClasspathResourcePackSource.core(javaClass.classLoader))

		assertEquals("slimevr:core", core.manifest.value.id)
		assertEquals(61, core.get(ResourceTypes.BONE).size)
		assertEquals(18, core.get(ResourceTypes.PROPORTION).size)
		assertEquals(1, core.get(ResourceTypes.LANGUAGE).size)
		assertTrue(core.get(ResourceTypes.BONE_OVERRIDE).isEmpty())
		assertTrue(core.get(ResourceTypes.PROPORTION_OVERRIDE).isEmpty())

		val head = core.get(ResourceTypes.BONE).single { it.value.key == "slimevr:head" }.value
		val fallback = assertIs<CopyRotationFallback>(head.rotationFallback)
		assertEquals("slimevr:neck", fallback.source)
		val step = head.outputs!!.vrchat!!.emit.getValue("/tracking/trackers/head/position").value!!.single()
		assertIs<PipelineStep.Scale>(step)
		assertIs<ScalarOrVector.Vector>(step.operand)
		assertIs<HeightRatioProportionDefault>(core.get(ResourceTypes.PROPORTION).single { it.value.key == "slimevr:upper_chest" }.value.default)
	}

	@Test
	fun `parser aggregates malformed and misplaced resource diagnostics`() {
		val error = assertFailsWith<ResourcePackParseException> {
			runBlocking {
				ResourcePackParser.parse(
					InMemoryResourcePackSource(
						mapOf(
							"manifest.json" to "{not json}",
							"data/not-a-definition.json" to "{}",
						),
					),
				)
			}
		}

		assertEquals(listOf("data/not-a-definition.json", "manifest.json"), error.diagnostics.map { it.path })
	}

	@Test
	fun `bundled pack loads from the built jar through its generated index`() = runBlocking {
		val jar = File("build/libs/core-jvm.jar")
		assertTrue(jar.isFile, "jvmJar must run before the test")
		URLClassLoader(arrayOf(jar.toURI().toURL()), null).use { loader ->
			val core = ResourcePackParser.parse(ClasspathResourcePackSource.core(loader))
			assertEquals(61, core.get(ResourceTypes.BONE).size)
			assertEquals(18, core.get(ResourceTypes.PROPORTION).size)
		}
	}

	@Test
	fun `manager orders valid user packs and isolates failures`() = runBlocking {
		val storage = MemoryStorage(
			files = mapOf(
				"resourcepacks/20-custom/manifest.json" to manifest("example:custom"),
				"resourcepacks/10-base/manifest.json" to manifest("example:base"),
				"resourcepacks/30-invalid/manifest.json" to "{}",
				"resourcepacks/40-no-manifest/data/bones/ignored.json" to "{}",
				"resourcepacks/50-symlink/manifest.json" to manifest("example:symlink"),
			),
			symlinks = setOf("resourcepacks/50-symlink/data/bones/linked.json"),
		)

		val catalog = ResourcePackManager.load(storage, javaClass.classLoader)

		assertEquals(listOf("example:base", "example:custom"), catalog.userPacks.map { it.manifest.value.id })
		assertEquals(listOf("30-invalid", "50-symlink"), catalog.failures.map { it.folder })
		assertTrue(storage.createdResourcePackDirectory)
	}

	@Test
	fun `manager falls back to core when resourcepack directory cannot be created`() = runBlocking {
		val catalog = ResourcePackManager.load(MemoryStorage(emptyMap(), ensureDirectoryResult = false), javaClass.classLoader)
		assertTrue(catalog.userPacks.isEmpty())
		assertTrue(catalog.failures.isEmpty())
	}

	@Test
	fun `overrides and all vmc input parent states decode`() = runBlocking {
		val pack = ResourcePackParser.parse(
			InMemoryResourcePackSource(
				mapOf(
					"manifest.json" to manifest("example:variants"),
					"data/bones/omitted.json" to bone("example:omitted", "{\"name\":\"Omitted\"}"),
					"data/bones/null.json" to bone("example:null", "{\"name\":\"Null\",\"inputParent\":null}"),
					"data/bones/key.json" to bone("example:key", "{\"name\":\"Key\",\"inputParent\":\"example:parent\"}"),
					"data/overrides/bones/one.json" to "{\"target\":\"example:omitted\",\"set\":{\"parent\":\"example:parent\"}}",
					"data/overrides/proportions/one.json" to "{\"target\":\"example:height\",\"set\":{\"default\":{\"type\":\"fixed\",\"value\":1}}}",
				),
			),
		)

		assertEquals(VmcInputParent.Omitted, pack.get(ResourceTypes.BONE).single { it.value.key == "example:omitted" }.value.outputs!!.vmc!!.inputParent)
		assertEquals(VmcInputParent.ExplicitNull, pack.get(ResourceTypes.BONE).single { it.value.key == "example:null" }.value.outputs!!.vmc!!.inputParent)
		assertEquals(VmcInputParent.Bone("example:parent"), pack.get(ResourceTypes.BONE).single { it.value.key == "example:key" }.value.outputs!!.vmc!!.inputParent)
		assertEquals("example:parent", pack.get(ResourceTypes.BONE_OVERRIDE).single().value.set!!.getValue("parent").jsonPrimitive.content)
		assertIs<FixedProportionDefault>(ResourcePackJson.decodeFromJsonElement<ProportionDefault>(pack.get(ResourceTypes.PROPORTION_OVERRIDE).single().value.set!!.getValue("default")))
	}

	@Test
	fun `typed fallback constraint and transform variants decode`() = runBlocking {
		val pack = ResourcePackParser.parse(
			InMemoryResourcePackSource(
				mapOf(
					"manifest.json" to manifest("example:branches"),
					"data/bones/branch.json" to """
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
		val bone = pack.get(ResourceTypes.BONE).single().value
		assertIs<FirstActiveRotationFallback>(bone.rotationFallback)
		assertIs<HingeConstraint>(bone.constraint)
		val steps = bone.outputs!!.vrchat!!.emit.getValue("/example").value!!
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

private fun manifest(id: String) = """
	{"formatVersion":1,"id":"$id","nameKey":"example:name","descriptionKey":"example:description","version":[1,0,0],"authors":["Test"]}
""".trimIndent()

private fun bone(key: String, vmc: String) = """
	{"key":"$key","nameKey":"example:name","outputs":{"vmc":$vmc}}
""".trimIndent()

private class MemoryStorage(
	private val files: Map<String, String>,
	private val symlinks: Set<String> = emptySet(),
	private val ensureDirectoryResult: Boolean = true,
) : ConfigStorage {
	var createdResourcePackDirectory = false

	override suspend fun read(path: String): String? = files[path]
	override suspend fun write(path: String, content: String) = Unit
	override suspend fun backup(path: String) = Unit
	override suspend fun exists(path: String): Boolean = path in files
	override suspend fun ensureDirectory(path: String): Boolean {
		if (path == "resourcepacks") createdResourcePackDirectory = ensureDirectoryResult
		return ensureDirectoryResult
	}
	override suspend fun list(path: String): List<StorageEntry> {
		val prefix = path.trimEnd('/') + "/"
		val children = linkedMapOf<String, StorageEntryType>()
		for (entry in files.keys + symlinks) {
			if (!entry.startsWith(prefix)) continue
			val remainder = entry.removePrefix(prefix)
			val name = remainder.substringBefore('/')
			val type = when {
				entry in symlinks && remainder == name -> StorageEntryType.SYMLINK
				remainder == name -> StorageEntryType.FILE
				else -> StorageEntryType.DIRECTORY
			}
			children[name] = type
		}
		return children.map { StorageEntry(it.key, it.value) }.sortedBy { it.name }
	}
	override suspend fun openTextFile(path: String): TextFileHandle = error("Not used")
}
