package dev.slimevr.osc

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CodecTest {
	@Test
	fun testEncodeDecodeIntMessage() {
		val msg = OscMessage("/test", listOf(OscArg.Int(42)))
		val encoded = encodeMessage(msg)
		val (decoded, _) = decodeMessage(encoded)

		assertEquals("/test", decoded.address)
		assertEquals(1, decoded.args.size)
		assertEquals(42, (decoded.args[0] as OscArg.Int).value)
	}

	@Test
	fun testEncodeDecodeFloatMessage() {
		val msg = OscMessage("/test/float", listOf(OscArg.Float(3.14f)))
		val encoded = encodeMessage(msg)
		val (decoded, _) = decodeMessage(encoded)

		assertEquals("/test/float", decoded.address)
		assertEquals(1, decoded.args.size)
		val float = (decoded.args[0] as OscArg.Float).value
		assertTrue((float - 3.14f) < 0.001f)
	}

	@Test
	fun testEncodeDecodeStringMessage() {
		val msg = OscMessage("/address", listOf(OscArg.String("hello")))
		val encoded = encodeMessage(msg)
		val (decoded, _) = decodeMessage(encoded)

		assertEquals("/address", decoded.address)
		assertEquals(1, decoded.args.size)
		assertEquals("hello", (decoded.args[0] as OscArg.String).value)
	}

	@Test
	fun testEncodeDecodeBlobMessage() {
		val blobData = byteArrayOf(1, 2, 3, 4, 5)
		val msg = OscMessage("/blob", listOf(OscArg.Blob(blobData)))
		val encoded = encodeMessage(msg)
		val (decoded, _) = decodeMessage(encoded)

		assertEquals("/blob", decoded.address)
		assertEquals(1, decoded.args.size)
		val decodedBlob = (decoded.args[0] as OscArg.Blob).value
		assertTrue(decodedBlob.contentEquals(blobData))
	}

	@Test
	fun testEncodeDecodeLongMessage() {
		val msg = OscMessage("/long", listOf(OscArg.Long(9876543210L)))
		val encoded = encodeMessage(msg)
		val (decoded, _) = decodeMessage(encoded)

		assertEquals("/long", decoded.address)
		assertEquals(1, decoded.args.size)
		assertEquals(9876543210L, (decoded.args[0] as OscArg.Long).value)
	}

	@Test
	fun testEncodeDecodeDoubleMessage() {
		val msg = OscMessage("/double", listOf(OscArg.Double(2.718281828)))
		val encoded = encodeMessage(msg)
		val (decoded, _) = decodeMessage(encoded)

		assertEquals("/double", decoded.address)
		assertEquals(1, decoded.args.size)
		val double = (decoded.args[0] as OscArg.Double).value
		assertTrue((double - 2.718281828) < 0.000001)
	}

	@Test
	fun testEncodeDecode() {
		val msg = OscMessage(
			"/VMC/Ext/Bone/Pos",
			listOf(
				OscArg.String("Hips"),
				OscArg.Float(1.0f),
				OscArg.Float(2.0f),
				OscArg.Float(3.0f),
				OscArg.Float(0.0f),
				OscArg.Float(0.0f),
				OscArg.Float(0.707f),
				OscArg.Float(0.707f),
			),
		)
		val encoded = encodeMessage(msg)
		val (decoded, _) = decodeMessage(encoded)

		assertEquals("/VMC/Ext/Bone/Pos", decoded.address)
		assertEquals(8, decoded.args.size)
		assertEquals("Hips", (decoded.args[0] as OscArg.String).value)
		assertEquals(1.0f, (decoded.args[1] as OscArg.Float).value)
	}

	@Test
	fun testMultipleArgs() {
		val msg = OscMessage(
			"/test",
			listOf(
				OscArg.Int(1),
				OscArg.String("two"),
				OscArg.Float(3.0f),
				OscArg.Long(4L),
			),
		)
		val encoded = encodeMessage(msg)
		val (decoded, _) = decodeMessage(encoded)

		assertEquals(4, decoded.args.size)
		assertEquals(1, (decoded.args[0] as OscArg.Int).value)
		assertEquals("two", (decoded.args[1] as OscArg.String).value)
		assertEquals(3.0f, (decoded.args[2] as OscArg.Float).value)
		assertEquals(4L, (decoded.args[3] as OscArg.Long).value)
	}

	@Test
	fun testSimpleBundle() {
		// Test with just one message to isolate the issue
		val msg = OscMessage("/test", listOf(OscArg.Int(42)))
		val encoded = encodeMessage(msg)
		val (decoded, _) = decodeMessage(encoded)
		assertEquals(42, (decoded.args[0] as OscArg.Int).value)
	}

	@Test
	fun testBundleWithOneMessage() {
		val msg = OscMessage("/single", listOf(OscArg.Int(99)))
		val bundle = OscBundle(5, listOf(OscContent.Message(msg)))
		val encoded = encodeBundle(bundle)

		val (decoded, _) = decodeBundle(encoded)

		assertEquals(5, decoded.timetag)
		assertEquals(1, decoded.contents.size)

		val decodedMsg = (decoded.contents[0] as OscContent.Message).msg
		assertEquals("/single", decodedMsg.address)
		assertTrue(decodedMsg.args.isNotEmpty(), "Message has no args!")
		val arg = decodedMsg.args[0]
		assertTrue(arg is OscArg.Int, "First arg is not Int, got: ${arg::class.simpleName}")
		assertEquals(99, (arg as OscArg.Int).value, "Int arg value mismatch")
	}

	@Test
	fun testEncodeDecodeBundle() {
		val bundle = OscBundle(
			1,
			listOf(
				OscContent.Message(OscMessage("/msg1", listOf(OscArg.Int(42)))),
				OscContent.Message(OscMessage("/msg2", listOf(OscArg.String("hello")))),
			),
		)
		val encoded = encodeBundle(bundle)
		val (decoded, _) = decodeBundle(encoded)

		assertEquals(1, decoded.timetag)
		assertEquals(2, decoded.contents.size)

		val msg1 = (decoded.contents[0] as OscContent.Message).msg
		assertEquals("/msg1", msg1.address)
		assertEquals(42, (msg1.args[0] as OscArg.Int).value)

		val msg2 = (decoded.contents[1] as OscContent.Message).msg
		assertEquals("/msg2", msg2.address)
		assertEquals("hello", (msg2.args[0] as OscArg.String).value)
	}

	@Test
	fun testNestedBundles() {
		val innerBundle = OscBundle(
			2,
			listOf(OscContent.Message(OscMessage("/inner", listOf(OscArg.Int(99))))),
		)
		val outerBundle = OscBundle(
			1,
			listOf(
				OscContent.Message(OscMessage("/outer", listOf(OscArg.Int(1)))),
				OscContent.Bundle(innerBundle),
			),
		)

		val encoded = encodeBundle(outerBundle)
		val (decoded, _) = decodeBundle(encoded)

		assertEquals(1, decoded.timetag)
		assertEquals(2, decoded.contents.size)

		val outerMsg = (decoded.contents[0] as OscContent.Message).msg
		assertEquals("/outer", outerMsg.address)

		val decodedInner = (decoded.contents[1] as OscContent.Bundle).bundle
		assertEquals(2, decodedInner.timetag)
		val innerMsg = (decodedInner.contents[0] as OscContent.Message).msg
		assertEquals("/inner", innerMsg.address)
		assertEquals(99, (innerMsg.args[0] as OscArg.Int).value)
	}

	@Test
	fun testEmptyMessage() {
		val msg = OscMessage("/empty")
		val encoded = encodeMessage(msg)
		val (decoded, _) = decodeMessage(encoded)

		assertEquals("/empty", decoded.address)
		assertEquals(0, decoded.args.size)
	}

	@Test
	fun testSpecialArgs() {
		val msg = OscMessage(
			"/special",
			listOf(OscArg.True, OscArg.False, OscArg.Null, OscArg.Impulse),
		)
		val encoded = encodeMessage(msg)
		val (decoded, _) = decodeMessage(encoded)

		assertEquals(4, decoded.args.size)
		assertEquals(OscArg.True, decoded.args[0])
		assertEquals(OscArg.False, decoded.args[1])
		assertEquals(OscArg.Null, decoded.args[2])
		assertEquals(OscArg.Impulse, decoded.args[3])
	}

	@Test
	fun testAlignment() {
		// Messages with different address lengths should be properly aligned
		val short = OscMessage("/a", listOf(OscArg.Int(1)))
		val long = OscMessage("/this/is/a/very/long/address", listOf(OscArg.Int(1)))

		val encodedShort = encodeMessage(short)
		val encodedLong = encodeMessage(long)

		// Both should encode/decode correctly regardless of alignment
		val (decodedShort, _) = decodeMessage(encodedShort)
		val (decodedLong, _) = decodeMessage(encodedLong)

		assertEquals("/a", decodedShort.address)
		assertEquals("/this/is/a/very/long/address", decodedLong.address)
	}

	@Test
	fun testOffsetParsing() {
		val msg1 = OscMessage("/first", listOf(OscArg.Int(1)))
		val msg2 = OscMessage("/second", listOf(OscArg.Int(2)))

		val encoded1 = encodeMessage(msg1)
		val encoded2 = encodeMessage(msg2)
		val combined = encoded1 + encoded2

		// Parse first message from combined
		val (decoded1, offset) = decodeMessage(combined, 0)
		assertEquals("/first", decoded1.address)

		// Parse second message from offset
		val (decoded2, _) = decodeMessage(combined, offset)
		assertEquals("/second", decoded2.address)
	}
}
