package dev.slimevr.osc

import kotlinx.io.Buffer
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

private fun encode(msg: OscMessage) = Buffer().also { writeMessage(it, msg) }

private fun encode(bundle: OscBundle) = Buffer().also { writeBundle(it, bundle) }

class CodecTest {
	@Test
	fun testEncodeDecodeIntMessage() {
		val msg = OscMessage("/test", listOf(OscArg.Int(42)))
		val encoded = encode(msg)
		val decoded = readMessage(encoded)

		assertEquals("/test", decoded.address)
		assertEquals(1, decoded.args.size)
		assertEquals(42, (decoded.args[0] as OscArg.Int).value)
	}

	@Test
	fun testEncodeDecodeFloatMessage() {
		val msg = OscMessage("/test/float", listOf(OscArg.Float(3.14f)))
		val encoded = encode(msg)
		val decoded = readMessage(encoded)

		assertEquals("/test/float", decoded.address)
		assertEquals(1, decoded.args.size)
		val float = (decoded.args[0] as OscArg.Float).value
		assertTrue((float - 3.14f) < 0.001f)
	}

	@Test
	fun testEncodeDecodeStringMessage() {
		val msg = OscMessage("/address", listOf(OscArg.String("hello")))
		val encoded = encode(msg)
		val decoded = readMessage(encoded)

		assertEquals("/address", decoded.address)
		assertEquals(1, decoded.args.size)
		assertEquals("hello", (decoded.args[0] as OscArg.String).value)
	}

	@Test
	fun testEncodeDecodeBlobMessage() {
		val blobData = byteArrayOf(1, 2, 3, 4, 5)
		val msg = OscMessage("/blob", listOf(OscArg.Blob(blobData)))
		val encoded = encode(msg)
		val decoded = readMessage(encoded)

		assertEquals("/blob", decoded.address)
		assertEquals(1, decoded.args.size)
		val decodedBlob = (decoded.args[0] as OscArg.Blob).value
		assertTrue(decodedBlob.contentEquals(blobData))
	}

	@Test
	fun testEncodeDecodeLongMessage() {
		val msg = OscMessage("/long", listOf(OscArg.Long(9876543210L)))
		val encoded = encode(msg)
		val decoded = readMessage(encoded)

		assertEquals("/long", decoded.address)
		assertEquals(1, decoded.args.size)
		assertEquals(9876543210L, (decoded.args[0] as OscArg.Long).value)
	}

	@Test
	fun testEncodeDecodeDoubleMessage() {
		val msg = OscMessage("/double", listOf(OscArg.Double(2.718281828)))
		val encoded = encode(msg)
		val decoded = readMessage(encoded)

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
		val encoded = encode(msg)
		val decoded = readMessage(encoded)

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
		val encoded = encode(msg)
		val decoded = readMessage(encoded)

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
		val encoded = encode(msg)
		val decoded = readMessage(encoded)
		assertEquals(42, (decoded.args[0] as OscArg.Int).value)
	}

	@Test
	fun testBundleWithOneMessage() {
		val msg = OscMessage("/single", listOf(OscArg.Int(99)))
		val bundle = OscBundle(5, listOf(OscContent.Message(msg)))
		val encoded = encode(bundle)

		val decoded = readBundle(encoded)

		assertEquals(5, decoded.timetag)
		assertEquals(1, decoded.contents.size)

		val decodedMsg = (decoded.contents[0] as OscContent.Message).msg
		assertEquals("/single", decodedMsg.address)
		assertTrue(decodedMsg.args.isNotEmpty(), "Message has no args!")
		val arg = decodedMsg.args[0]
		assertTrue(arg is OscArg.Int, "First arg is not Int, got: ${arg::class.simpleName}")
		assertEquals(99, (arg).value, "Int arg value mismatch")
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
		val encoded = encode(bundle)
		val decoded = readBundle(encoded)

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

		val encoded = encode(outerBundle)
		val decoded = readBundle(encoded)

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
		val encoded = encode(msg)
		val decoded = readMessage(encoded)

		assertEquals("/empty", decoded.address)
		assertEquals(0, decoded.args.size)
	}

	@Test
	fun testSpecialArgs() {
		val msg = OscMessage(
			"/special",
			listOf(OscArg.True, OscArg.False, OscArg.Null, OscArg.Impulse),
		)
		val encoded = encode(msg)
		val decoded = readMessage(encoded)

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

		val encodedShort = encode(short)
		val encodedLong = encode(long)

		// Both should encode/decode correctly regardless of alignment
		val decodedShort = readMessage(encodedShort)
		val decodedLong = readMessage(encodedLong)

		assertEquals("/a", decodedShort.address)
		assertEquals("/this/is/a/very/long/address", decodedLong.address)
	}

	@Test
	fun testOffsetParsing() {
		val msg1 = OscMessage("/first", listOf(OscArg.Int(1)))
		val msg2 = OscMessage("/second", listOf(OscArg.Int(2)))

		// Both written back to back into one source: reading the first has to leave the second aligned
		val combined = Buffer()
		writeMessage(combined, msg1)
		writeMessage(combined, msg2)

		assertEquals("/first", readMessage(combined).address)
		assertEquals("/second", readMessage(combined).address)
		assertTrue(combined.exhausted(), "Reading both messages should consume the whole source")
	}

	@Test
	fun testStringAndBlobPaddingBoundaries() {
		// Every length either side of a four-byte boundary, where the alignment maths is easiest to get wrong
		for (n in 0..9) {
			val text = "z".repeat(n)
			val decoded = readMessage(encode(OscMessage("/s", listOf(OscArg.String(text)))))
			assertEquals(text, (decoded.args[0] as OscArg.String).value, "string of length $n")

			val blob = ByteArray(n) { it.toByte() }
			val decodedBlob = readMessage(encode(OscMessage("/b", listOf(OscArg.Blob(blob)))))
			assertContentEquals(blob, (decodedBlob.args[0] as OscArg.Blob).value, "blob of length $n")
		}
	}

	@Test
	fun testAddressLengthsAcrossBoundaries() {
		for (n in 1..12) {
			val address = "/" + "a".repeat(n)
			val decoded = readMessage(encode(OscMessage(address, listOf(OscArg.Int(7)))))
			assertEquals(address, decoded.address, "address of length ${address.length}")
			assertEquals(7, (decoded.args[0] as OscArg.Int).value)
		}
	}

	@Test
	fun testLargeBundleRoundTrips() {
		// A VMC-shaped frame: many small messages, far past any single-segment buffer
		val contents = (0 until 55).map { i ->
			OscContent.Message(
				OscMessage(
					"/VMC/Ext/Bone/Pos",
					listOf(OscArg.String("BoneNameNumber$i")) + (0 until 7).map { OscArg.Float(i * 0.125f + it) },
				),
			)
		}
		val decoded = readBundle(encode(OscBundle(1, contents)))

		assertEquals(contents.size, decoded.contents.size)
		assertEquals(contents, decoded.contents)
	}

	@Test
	fun testOversizedBlobRoundTrips() {
		val blob = ByteArray(9000) { (it * 31).toByte() }
		val msg = OscMessage("/big", listOf(OscArg.Blob(blob), OscArg.String("tail"), OscArg.Int(42)))
		val decoded = readMessage(encode(msg))

		assertContentEquals(blob, (decoded.args[0] as OscArg.Blob).value)
		assertEquals("tail", (decoded.args[1] as OscArg.String).value)
		assertEquals(42, (decoded.args[2] as OscArg.Int).value)
	}

	@Test
	fun testNonBundleIsRejectedWithoutConsuming() {
		// listenBundles relies on this to fall back to reading the same source as a plain message
		val source = encode(OscMessage("/plain", listOf(OscArg.Int(1))))
		val before = source.size

		assertFailsWith<IllegalArgumentException> { readBundle(source) }
		assertEquals(before, source.size, "A rejected bundle must not consume any bytes")
		assertEquals("/plain", readMessage(source).address)
	}
}
