package dev.slimevr.bvh

import dev.slimevr.bones.BoneRegistry
import dev.slimevr.bones.boneId
import dev.slimevr.bones.mutateCopy
import dev.slimevr.config.TextFileHandle
import dev.slimevr.skeleton.buildBones
import dev.slimevr.skeleton.defaultSkeletonState
import io.github.axisangles.ktmath.Vector3
import kotlinx.coroutines.test.runTest
import solarxr_protocol.datatypes.BodyPart
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BvhStreamTest {
	private val registry = BoneRegistry.standard()
	private val headId = BodyPart.HEAD.boneId
	private val defaultState = defaultSkeletonState(registry)

	@Test
	fun `close persists header and frame data`() = runTest {
		val file = InMemoryBvhFile()
		val stream = BvhStream(file)
		val initialBones = buildBones(defaultState.boneInputs)
		val firstFrame = buildBones(
			defaultState.boneInputs.mutateCopy {
				it[headId] = it.getValue(headId).copy(position = Vector3(1f, 2f, 3f))
			},
		)
		val secondFrame = buildBones(
			defaultState.boneInputs.mutateCopy {
				it[headId] = it.getValue(headId).copy(position = Vector3(4f, 5f, 6f))
			},
		)

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
		val bones = buildBones(defaultState.boneInputs)

		stream.writeHeader(bones)
		stream.close()
		val before = file.content()

		stream.writeFrame(
			buildBones(
				defaultState.boneInputs.mutateCopy {
					it[headId] = it.getValue(headId).copy(position = Vector3(7f, 8f, 9f))
				},
			),
		)

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
