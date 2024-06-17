package dev.slimevr.posestreamer

import com.jme3.math.FastMath
import dev.slimevr.tracking.processor.Bone
import dev.slimevr.tracking.processor.skeleton.HumanSkeleton
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import org.apache.commons.lang3.StringUtils
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.io.OutputStreamWriter

class BVHFileStream : PoseDataStream {
	var bvhSettings: BVHSettings = BVHSettings.BLENDER

	private val writer: BufferedWriter
	private var frameCount: Long = 0
	private var frameCountOffset: Long = 0

	constructor(outputStream: OutputStream) : super(outputStream) {
		writer = BufferedWriter(OutputStreamWriter(outputStream), 4096)
	}

	constructor(outputStream: OutputStream, bvhSettings: BVHSettings) : this(outputStream) {
		this.bvhSettings = bvhSettings
	}

	constructor(file: File) : super(file) {
		writer = BufferedWriter(OutputStreamWriter(outputStream), 4096)
	}

	constructor(file: File, bvhSettings: BVHSettings) : this(file) {
		this.bvhSettings = bvhSettings
	}

	constructor(file: String) : super(file) {
		writer = BufferedWriter(OutputStreamWriter(outputStream), 4096)
	}

	constructor(file: String, bvhSettings: BVHSettings) : this(file) {
		this.bvhSettings = bvhSettings
	}

	private fun getBufferedFrameCount(frameCount: Long): String {
		val frameString = frameCount.toString()
		val bufferCount = LONG_MAX_VALUE_DIGITS - frameString.length

		return if (bufferCount > 0) frameString + StringUtils.repeat(' ', bufferCount) else frameString
	}

	private fun isEndBone(bone: Bone?): Boolean = bone == null || (!bvhSettings.shouldWriteEndNodes() && bone.children.isEmpty())

	@Throws(IOException::class)
	private fun writeBoneHierarchy(bone: Bone?, level: Int = 0) {
		// Treat null as bone. This allows for simply writing empty end bones
		val isEndBone = isEndBone(bone)

		// Don't write end sites at populated bones, BVH parsers don't like that
		// Ex case caught: `joint{ joint{ end }, end, end }` outputs `joint{ end
		// }` instead
		// Ex case let through: `joint{ end }`
		val isSingleChild = (bone?.parent?.children?.size ?: 0) <= 1
		if (isEndBone && !isSingleChild) {
			return
		}

		val indentLevel = StringUtils.repeat("\t", level)
		val nextIndentLevel = indentLevel + "\t"

		// Handle ends
		if (isEndBone) {
			writer.write(indentLevel + "End Site\n")
		} else {
			writer
				.write((if (level > 0) indentLevel + "JOINT " else "ROOT ") + bone!!.boneType + "\n")
		}
		writer.write("$indentLevel{\n")

		// Ignore the root offset and original root offset
		if (level > 0 && bone != null && bone.parent != null) {
			val offsetScale = bvhSettings.offsetScale
			writer
				.write(
					(
						nextIndentLevel +
							"OFFSET " +
							0 +
							" "
						) + -bone.parent!!.length * offsetScale + " " +
						0 +
						"\n",
				)
		} else {
			writer.write(nextIndentLevel + "OFFSET 0.0 0.0 0.0\n")
		}

		// Handle ends
		if (!isEndBone) {
			// Only give position for root
			if (level > 0) {
				writer.write(nextIndentLevel + "CHANNELS 3 Zrotation Xrotation Yrotation\n")
			} else {
				writer
					.write(
						nextIndentLevel +
							"CHANNELS 6 Xposition Yposition Zposition Zrotation Xrotation Yrotation\n",
					)
			}

			// If the bone has children
			if (bone!!.children.isNotEmpty()) {
				for (childBone in bone.children) {
					writeBoneHierarchy(childBone, level + 1)
				}
			} else {
				// Write an empty end bone
				writeBoneHierarchy(null, level + 1)
			}
		}

		writer.write("$indentLevel}\n")
	}

	@Throws(IOException::class)
	override fun writeHeader(skeleton: HumanSkeleton, streamer: PoseStreamer) {
		writer.write("HIERARCHY\n")
		writeBoneHierarchy(skeleton.headBone)

		writer.write("MOTION\n")
		writer.write("Frames: ")

		// Get frame offset for finishing writing the file
		if (outputStream is FileOutputStream) {
			// Flush buffer to get proper offset
			writer.flush()
			frameCountOffset = outputStream.channel.position()
		}

		writer.write(getBufferedFrameCount(frameCount) + "\n")

		// Frame time in seconds
		writer.write("Frame Time: ${streamer.frameInterval / 1000.0}\n")
	}

	@Throws(IOException::class)
	private fun writeBoneHierarchyRotation(bone: Bone, inverseRootRot: Quaternion?) {
		var rot = bone.getGlobalRotation()

		// Adjust to local rotation
		if (inverseRootRot != null) {
			rot = inverseRootRot * rot
		}

		// Pitch (X), Yaw (Y), Roll (Z)
		val angles = rot.toEulerAngles(EulerOrder.ZXY)

		// Output in order of roll (Z), pitch (X), yaw (Y) (extrinsic)
		writer
			.write("${angles.z * FastMath.RAD_TO_DEG} ${angles.x * FastMath.RAD_TO_DEG} ${angles.y * FastMath.RAD_TO_DEG}")

		// Get inverse rotation for child local rotations
		if (bone.children.isNotEmpty()) {
			val inverseRot = bone.getGlobalRotation().inv()
			for (childBode in bone.children) {
				if (isEndBone(childBode)) {
					// If it's an end bone, skip
					continue
				}

				// Add spacing
				writer.write(" ")
				writeBoneHierarchyRotation(childBode, inverseRot)
			}
		}
	}

	@Throws(IOException::class)
	public override fun writeFrame(skeleton: HumanSkeleton) {
		val rootBone = skeleton.headBone

		val rootPos = rootBone.getPosition()

		// Write root position
		val positionScale = bvhSettings.positionScale
		writer
			.write("${rootPos.x * positionScale} ${rootPos.y * positionScale} ${rootPos.z * positionScale}")

		// Add spacing
		writer.write(" ")
		writeBoneHierarchyRotation(rootBone, null)

		writer.newLine()

		frameCount++
	}

	@Throws(IOException::class)
	override fun writeFooter(skeleton: HumanSkeleton) {
		// Write the final frame count for files
		if (outputStream is FileOutputStream) {
			// Flush before anything else
			writer.flush()
			// Seek to the count offset
			outputStream.channel.position(frameCountOffset)
			// Overwrite the count with a new value
			writer.write(frameCount.toString())
		}
	}

	@Throws(IOException::class)
	override fun close() {
		writer.close()
		super.close()
	}

	companion object {
		private const val LONG_MAX_VALUE_DIGITS = Long.MAX_VALUE.toString().length
	}
}
