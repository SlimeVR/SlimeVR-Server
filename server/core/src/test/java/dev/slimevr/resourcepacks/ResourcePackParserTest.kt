package dev.slimevr.resourcepacks

import com.github.erosb.jsonsKema.JsonParser
import com.github.erosb.jsonsKema.Validator
import dev.slimevr.config.ConfigStorage
import dev.slimevr.config.StorageEntry
import dev.slimevr.config.StorageEntryType
import dev.slimevr.config.TextFileHandle
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.io.File
import java.net.URLClassLoader
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ResourcePackParserTest {
	@Test
	fun `bundled core pack is packaged and parsed`() = runBlocking {
		val core = ResourcePackParser().parse(ClasspathResourcePackSource.core(javaClass.classLoader))

		assertEquals("slimevr:core", core.manifest.value.id)
		assertEquals(61, core.bones.size)
		assertEquals(18, core.proportions.size)
		assertEquals(1, core.languages.size)
		assertTrue(core.boneOverrides.isEmpty())
		assertTrue(core.proportionOverrides.isEmpty())

		val head = core.bones.single { it.value.key == "slimevr:head" }.value
		val fallback = assertIs<CopyRotationFallback>(head.rotationFallback)
		assertEquals("slimevr:neck", fallback.source)
		assertIs<ScalarOrVector.Vector>(head.outputs!!.vrchat!!.emit.getValue("/tracking/trackers/head/position").value!!.single().scale)
		assertIs<HeightRatioProportionDefault>(core.proportions.single { it.value.key == "slimevr:upper_chest" }.value.default)
	}

	@Test
	fun `parser aggregates malformed and misplaced resource diagnostics`() {
		val error = assertFailsWith<ResourcePackParseException> {
			runBlocking {
				ResourcePackParser().parse(
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
			val core = ResourcePackParser().parse(ClasspathResourcePackSource.core(loader))
			assertEquals(61, core.bones.size)
			assertEquals(18, core.proportions.size)
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
	fun `core typed documents round trip through their trusted schemas`() = runBlocking {
		val core = ResourcePackParser().parse(ClasspathResourcePackSource.core(javaClass.classLoader))
		val json = Json {
			encodeDefaults = false
			explicitNulls = true
		}

		assertRoundTrip(core.manifest, PackManifest.serializer(), ResourceKind.MANIFEST, json)
		assertRoundTrips(core.bones, BoneDefinition.serializer(), ResourceKind.BONE, json)
		assertRoundTrips(core.proportions, ProportionDefinition.serializer(), ResourceKind.PROPORTION, json)
		assertRoundTrips(core.languages, LanguageResourceSerializer, ResourceKind.LANGUAGE, json)
	}

	@Test
	fun `overrides and all vmc input parent states decode`() = runBlocking {
		val pack = ResourcePackParser().parse(
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

		assertEquals(VmcInputParent.Omitted, pack.bones.single { it.value.key == "example:omitted" }.value.outputs!!.vmc!!.inputParent)
		assertEquals(VmcInputParent.ExplicitNull, pack.bones.single { it.value.key == "example:null" }.value.outputs!!.vmc!!.inputParent)
		assertEquals(VmcInputParent.Bone("example:parent"), pack.bones.single { it.value.key == "example:key" }.value.outputs!!.vmc!!.inputParent)
		assertEquals("example:parent", pack.boneOverrides.single().value.set!!.parent)
		assertIs<FixedProportionDefault>(pack.proportionOverrides.single().value.set!!.default)
	}

	@Test
	fun `typed fallback constraint and transform variants decode`() = runBlocking {
		val pack = ResourcePackParser().parse(
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
		val bone = pack.bones.single().value
		assertIs<FirstActiveRotationFallback>(bone.rotationFallback)
		assertIs<HingeConstraint>(bone.constraint)
		val steps = bone.outputs!!.vrchat!!.emit.getValue("/example").value!!
		assertEquals(Axis.X, steps[0].euler!!.axis)
		assertEquals(EulerOrder.YXZ, steps[1].euler!!.order)
		assertIs<ScalarOrVector.Scalar>(steps[2].scale)
		assertIs<ScalarOrVector.Vector>(steps[3].divide)
		assertEquals(listOf(-1f, 1f), steps[5].clamp)
		assertEquals(0f, steps[6].greaterThan)
		assertEquals(2f, steps[7].lessThan)
	}
}

private fun <T> assertRoundTrips(resources: List<SourcedResource<T>>, serializer: KSerializer<T>, kind: ResourceKind, json: Json) = resources.forEach { assertRoundTrip(it, serializer, kind, json) }

private fun <T> assertRoundTrip(resource: SourcedResource<T>, serializer: KSerializer<T>, kind: ResourceKind, json: Json) {
	val encoded = json.encodeToString(serializer, resource.value)
	assertNull(Validator.forSchema(PackSchemas.schema(kind)).validate(JsonParser(encoded).parse()), resource.path)
	assertEquals(resource.value, json.decodeFromString(serializer, encoded), resource.path)
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
