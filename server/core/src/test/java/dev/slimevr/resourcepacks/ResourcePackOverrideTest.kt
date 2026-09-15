package dev.slimevr.resourcepacks

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlin.test.Test
import kotlin.test.assertEquals

private val schemaJson = Json { ignoreUnknownKeys = true }

private fun readSchema(fileName: String): String = object {}.javaClass.classLoader
	.getResourceAsStream("${ClasspathResourcePackSource.CORE_ROOT}/schemas/v1/$fileName")!!
	.bufferedReader().use { it.readText() }

private fun properties(fileName: String): Set<String> = schemaJson.parseToJsonElement(readSchema(fileName)).jsonObject.getValue("properties").jsonObject.keys

private fun overridableSetProperties(overrideFileName: String): Set<String> = schemaJson.parseToJsonElement(readSchema(overrideFileName)).jsonObject
	.getValue("properties").jsonObject.getValue("set").jsonObject.getValue("properties").jsonObject.keys

class ResourcePackOverrideTest {
	@Test
	fun `bone override set covers every overridable bone property`() {
		assertEquals(properties("bone.schema.json") - setOf("\$schema", "key"), overridableSetProperties("bone-override.schema.json"))
	}

	@Test
	fun `proportion override set covers every overridable proportion property`() {
		assertEquals(properties("proportion.schema.json") - setOf("\$schema", "key"), overridableSetProperties("proportion-override.schema.json"))
	}
}
