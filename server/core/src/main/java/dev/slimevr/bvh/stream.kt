package dev.slimevr.bvh

import com.jme3.math.FastMath
import dev.slimevr.skeleton.BODY_PART_HIERARCHY_MAP
import dev.slimevr.skeleton.BoneState
import dev.slimevr.skeleton.DEFAULT_BONE_OFFSETS
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import solarxr_protocol.datatypes.BodyPart
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

private const val FRAME_COUNT_DIGITS = Long.MAX_VALUE.toString().length
private const val OFFSET_SCALE = 1f
private const val POSITION_SCALE = 1f

class BvhStream(file: File) {
	private val outputStream = FileOutputStream(file)
	private val writer = BufferedWriter(OutputStreamWriter(outputStream), 4096)
	private var frameCount = 0L
	private var frameCountOffset = 0L
	private var frameTimeOffset = 0L
	private var lastFrameTime = System.currentTimeMillis()
	private val frameIntervals = mutableListOf<Float>()

	suspend fun writeHeader(bones: Map<BodyPart, BoneState>) = withContext(Dispatchers.IO) {
		writer.write("HIERARCHY\n")
		writeBone(BodyPart.HEAD, null, bones, 0)
		writer.write("MOTION\n")
		writer.write("Frames: ")
		writer.flush()
		frameCountOffset = outputStream.channel.position()
		frameCount = 0L
		writer.write(getBufferedFrameCount(0L) + "\n")
		writer.write("Frame Time: ")
		writer.flush()
		frameTimeOffset = outputStream.channel.position()
		writer.write(getBufferedFrameInterval(0.01f) + "\n")
		lastFrameTime = System.currentTimeMillis()
	}

	suspend fun writeFrame(bones: Map<BodyPart, BoneState>) = withContext(Dispatchers.IO) {
		val now = System.currentTimeMillis()
		if (frameCount > 0) {
			val interval = (now - lastFrameTime) / 1000f
			frameIntervals.add(interval)
		}
		lastFrameTime = now

		val head = bones[BodyPart.HEAD]
		val pos = head?.headPosition ?: Vector3.NULL
		writer.write("${pos.x * POSITION_SCALE} ${pos.y * POSITION_SCALE} ${pos.z * POSITION_SCALE}")
		writeRotations(BodyPart.HEAD, bones)
		writer.newLine()
		frameCount++
	}

	suspend fun close() = withContext(Dispatchers.IO) {
		writer.flush()
		val avgInterval = if (frameIntervals.isNotEmpty()) frameIntervals.average().toFloat() else 0.01f

		outputStream.channel.position(frameCountOffset)
		writer.write(getBufferedFrameCount(frameCount))

		outputStream.channel.position(frameTimeOffset)
		writer.write(getBufferedFrameInterval(avgInterval))

		writer.close()
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
		val tailOffsetLocal = DEFAULT_BONE_OFFSETS[parentPart] ?: return Vector3.NULL
		val parentLength = bones[parentPart]?.length ?: 0f
		return tailOffsetLocal.unit() * (parentLength * OFFSET_SCALE)
	}

	private fun writeRotations(part: BodyPart, bones: Map<BodyPart, BoneState>) {
		val bone = bones[part]
		val rot = bone?.localRotation ?: Quaternion.IDENTITY
		val angles = rot.toEulerAngles(EulerOrder.ZXY)
		writer.write(" ${angles.z * FastMath.RAD_TO_DEG} ${angles.x * FastMath.RAD_TO_DEG} ${angles.y * FastMath.RAD_TO_DEG}")
		BODY_PART_HIERARCHY_MAP[part]?.forEach { child -> writeRotations(child, bones) }
	}

	private fun writeBone(part: BodyPart, parent: BodyPart?, bones: Map<BodyPart, BoneState>, depth: Int) {
		val indent = "\t".repeat(depth)
		val childIndent = "\t".repeat(depth + 1)
		writer.write("$indent${if (parent == null) "ROOT" else "JOINT"} $part\n")
		writer.write("$indent{\n")

		if (parent == null) {
			writer.write("${childIndent}OFFSET 0.0 0.0 0.0\n")
			writer.write("${childIndent}CHANNELS 6 Xposition Yposition Zposition Zrotation Xrotation Yrotation\n")
		} else {
			val offset = getBvhOffset(parent, bones)
			writer.write("${childIndent}OFFSET ${offset.x} ${offset.y} ${offset.z}\n")
			writer.write("${childIndent}CHANNELS 3 Zrotation Xrotation Yrotation\n")
		}

		val children = BODY_PART_HIERARCHY_MAP[part]
		if (children.isNullOrEmpty()) {
			writer.write("${childIndent}End Site\n")
			writer.write("$childIndent{\n")
			writer.write("${"	".repeat(depth + 2)}OFFSET 0.0 0.0 0.0\n")
			writer.write("$childIndent}\n")
		} else {
			children.forEach { child -> writeBone(child, part, bones, depth + 1) }
		}
		writer.write("$indent}\n")
	}
}
