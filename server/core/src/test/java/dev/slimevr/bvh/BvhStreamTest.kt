package dev.slimevr.bvh

import dev.slimevr.config.TextFileHandle
import dev.slimevr.skeleton.DEFAULT_SKELETON_STATE
import dev.slimevr.skeleton.buildBones
import io.github.axisangles.ktmath.Vector3
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BvhStreamTest {
	@Test
	fun `close persists header and frame data`() = runTest {
		val file = InMemoryBvhFile()
		val stream = BvhStream(file)
		val initialBones = buildBones(DEFAULT_SKELETON_STATE)
		val firstFrame = buildBones(DEFAULT_SKELETON_STATE, rootHead = Vector3(1f, 2f, 3f))
		val secondFrame = buildBones(DEFAULT_SKELETON_STATE, rootHead = Vector3(4f, 5f, 6f))

		stream.writeHeader(initialBones)
		stream.writeFrame(firstFrame)
		stream.writeFrame(secondFrame)
		stream.close()

		val content = file.content()
		assertTrue(content.contains("HIERARCHY"))
		assertTrue(content.contains("MOTION"))
		assertTrue(content.contains("Frames: 2"))
		assertTrue(content.contains("1.0 2.0 3.0"))
		assertTrue(content.contains("4.0 5.0 6.0"))
	}

	@Test
	fun `writeFrame after close is ignored`() = runTest {
		val file = InMemoryBvhFile()
		val stream = BvhStream(file)
		val bones = buildBones(DEFAULT_SKELETON_STATE)

		stream.writeHeader(bones)
		stream.close()
		val before = file.content()

		stream.writeFrame(buildBones(DEFAULT_SKELETON_STATE, rootHead = Vector3(7f, 8f, 9f)))

		assertEquals(before, file.content())
	}
}

private class InMemoryBvhFile : TextFileHandle {
	private val chars = mutableListOf<Char>()
	private var pointer = 0
	private var closed = false

	override suspend fun write(text: String) {
		check(!closed) { "file is closed" }
		ensureCapacity(pointer)
		text.forEachIndexed { index, char ->
			val position = pointer + index
			if (position < chars.size) {
				chars[position] = char
			} else {
				chars.add(char)
			}
		}
		pointer += text.length
	}

	override suspend fun flush() = Unit

	override suspend fun position(): Long = pointer.toLong()

	override suspend fun seek(position: Long) {
		pointer = position.toInt()
	}

	override suspend fun close() {
		closed = true
	}

	fun content(): String = chars.joinToString("")

	private fun ensureCapacity(target: Int) {
		while (chars.size < target) {
			chars.add(' ')
		}
	}
}
