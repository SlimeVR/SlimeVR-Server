package dev.slimevr.bvh

import com.jme3.math.FastMath
import dev.slimevr.config.TextFileHandle
import dev.slimevr.skeleton.BODY_PART_HIERARCHY_MAP
import dev.slimevr.skeleton.BoneState
import dev.slimevr.skeleton.DEFAULT_BONE_OFFSETS
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import solarxr_protocol.datatypes.BodyPart

private const val FRAME_COUNT_DIGITS = Long.MAX_VALUE.toString().length
private const val OFFSET_SCALE = 1f
private const val POSITION_SCALE = 1f

class BvhStream(
	private val file: TextFileHandle,
) {
	private val mutex = Mutex()
	private var frameCount = 0L
	private var frameCountOffset = 0L
	private var frameTimeOffset = 0L
	private var lastFrameTime = System.currentTimeMillis()
	private val frameIntervals = mutableListOf<Float>()
	private var closed = false

	suspend fun writeHeader(bones: Map<BodyPart, BoneState>) {
		mutex.withLock {
			check(!closed) { "BVH stream is closed" }
			file.write("HIERARCHY\n")
			writeBone(BodyPart.HEAD, null, bones, 0)
			file.write("MOTION\n")
			file.write("Frames: ")
			file.flush()
			frameCountOffset = file.position()
			frameCount = 0L
			file.write(getBufferedFrameCount(0L) + "\n")
			file.write("Frame Time: ")
			file.flush()
			frameTimeOffset = file.position()
			file.write(getBufferedFrameInterval(0.01f) + "\n")
			lastFrameTime = System.currentTimeMillis()
		}
	}

	suspend fun writeFrame(bones: Map<BodyPart, BoneState>) {
		mutex.withLock {
			if (closed) return

			val now = System.currentTimeMillis()
			if (frameCount > 0) {
				val interval = (now - lastFrameTime) / 1000f
				frameIntervals.add(interval)
			}
			lastFrameTime = now

			val head = bones[BodyPart.HEAD]
			val pos = head?.headPosition ?: Vector3.NULL
			file.write("${pos.x * POSITION_SCALE} ${pos.y * POSITION_SCALE} ${pos.z * POSITION_SCALE}")
			writeRotations(BodyPart.HEAD, bones)
			file.write("\n")
			frameCount++
		}
	}

	suspend fun close() {
		mutex.withLock {
			if (closed) return
			closed = true

			file.flush()
			val avgInterval = if (frameIntervals.isNotEmpty()) frameIntervals.average().toFloat() else 0.01f

			file.seek(frameCountOffset)
			file.write(getBufferedFrameCount(frameCount))

			file.seek(frameTimeOffset)
			file.write(getBufferedFrameInterval(avgInterval))
			file.flush()

			file.close()
		}
	}

	private fun getBufferedFrameCount(frameCount: Long): String {
		val frameString = frameCount.toString()
		val bufferCount = FRAME_COUNT_DIGITS - frameString.length
		return if (bufferCount > 0) frameString + " ".repeat(bufferCount) else frameString
	}

	private fun getBufferedFrameInterval(interval: Float): String {
		val intervalString = String.format("%.4f", interval)
		return intervalString.padEnd(9)
	}

	private fun getBvhOffset(parentPart: BodyPart, bones: Map<BodyPart, BoneState>): Vector3 {
		val offset = bones[parentPart]?.offset ?: return Vector3.NULL
		return offset * OFFSET_SCALE
	}

	private suspend fun writeRotations(part: BodyPart, bones: Map<BodyPart, BoneState>) {
		val bone = bones[part]
		val rot = bone?.localRotation ?: Quaternion.IDENTITY
		val angles = rot.toEulerAngles(EulerOrder.ZXY)
		file.write(" ${angles.z * FastMath.RAD_TO_DEG} ${angles.x * FastMath.RAD_TO_DEG} ${angles.y * FastMath.RAD_TO_DEG}")
		BODY_PART_HIERARCHY_MAP[part]?.forEach { child -> writeRotations(child, bones) }
	}

	private suspend fun writeBone(part: BodyPart, parent: BodyPart?, bones: Map<BodyPart, BoneState>, depth: Int) {
		val indent = "\t".repeat(depth)
		val childIndent = "\t".repeat(depth + 1)
		file.write("$indent${if (parent == null) "ROOT" else "JOINT"} $part\n")
		file.write("$indent{\n")

		if (parent == null) {
			file.write("${childIndent}OFFSET 0.0 0.0 0.0\n")
			file.write("${childIndent}CHANNELS 6 Xposition Yposition Zposition Zrotation Xrotation Yrotation\n")
		} else {
			val offset = getBvhOffset(parent, bones)
			file.write("${childIndent}OFFSET ${offset.x} ${offset.y} ${offset.z}\n")
			file.write("${childIndent}CHANNELS 3 Zrotation Xrotation Yrotation\n")
		}

		val children = BODY_PART_HIERARCHY_MAP[part]
		if (children.isNullOrEmpty()) {
			file.write("${childIndent}End Site\n")
			file.write("$childIndent{\n")
			file.write("${"\t".repeat(depth + 2)}OFFSET 0.0 0.0 0.0\n")
			file.write("$childIndent}\n")
		} else {
			children.forEach { child -> writeBone(child, part, bones, depth + 1) }
		}
		file.write("$indent}\n")
	}
}
