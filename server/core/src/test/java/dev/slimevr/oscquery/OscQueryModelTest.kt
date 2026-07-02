package dev.slimevr.oscquery

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class OscQueryModelTest {
	private val json = Json { ignoreUnknownKeys = true }

	@Test
	fun hostInfoJsonUsesOscQueryFieldNames() {
		val rendered = json.encodeToString(
			OscQueryHostInfo(
				name = "SlimeVR",
				oscIp = "127.0.0.1",
				oscPort = 9001u,
			),
		)
		val obj = json.parseToJsonElement(rendered).jsonObject

		assertEquals("SlimeVR", obj["NAME"]?.jsonPrimitive?.content)
		assertEquals("127.0.0.1", obj["OSC_IP"]?.jsonPrimitive?.content)
		assertEquals(9001, obj["OSC_PORT"]?.jsonPrimitive?.content?.toInt())
	}

	@Test
	fun nodeJsonUsesOscQueryFieldNames() {
		val rendered = json.encodeToString(
			OscQueryNode(
				fullPath = "/avatar/parameters/Test",
				type = "f",
				access = OscQueryAccess.READ_WRITE,
				value = listOf(JsonPrimitive(0.5f)),
				description = "Test parameter",
			),
		)
		val obj = json.parseToJsonElement(rendered).jsonObject

		assertEquals("/avatar/parameters/Test", obj["FULL_PATH"]?.jsonPrimitive?.content)
		assertEquals("f", obj["TYPE"]?.jsonPrimitive?.content)
		assertEquals(3, obj["ACCESS"]?.jsonPrimitive?.content?.toInt())
		assertEquals(0.5f, obj["VALUE"]?.jsonArray?.single()?.jsonPrimitive?.content?.toFloat())
		assertEquals("Test parameter", obj["DESCRIPTION"]?.jsonPrimitive?.content)
	}

	@Test
	fun treeBuildsParentsAndFindsNodes() {
		val tree = OscQueryTree()
		tree.add(OscQueryNode(fullPath = "/tracking/vrsystem"))

		val root = assertNotNull(tree.find("/"))
		val tracking = assertNotNull(tree.find("/tracking"))
		val vrsystem = assertNotNull(tree.find("/tracking/vrsystem"))

		assertEquals("tracking", root.contents?.keys?.single())
		assertEquals("vrsystem", tracking.contents?.keys?.single())
		assertEquals("vrsystem", root.contents?.get("tracking")?.contents?.keys?.single())
		assertEquals("/tracking/vrsystem", vrsystem.fullPath)
		assertEquals(vrsystem, tree.find("/tracking/vrsystem?HOST_INFO"))
		assertNull(tree.find("/tracking/unknown"))
	}
}
