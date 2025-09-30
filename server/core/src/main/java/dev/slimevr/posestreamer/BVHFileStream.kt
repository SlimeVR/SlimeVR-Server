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

	constructor(outputStream: OutputStream, bvhSettings: BVHSettings = BVHSettings.BLENDER) : super(outputStream) {
		this.bvhSettings = bvhSettings
		writer = BufferedWriter(OutputStreamWriter(outputStream), 4096)
	}

	constructor(file: File, bvhSettings: BVHSettings = BVHSettings.BLENDER) : super(file) {
		this.bvhSettings = bvhSettings
		writer = BufferedWriter(OutputStreamWriter(outputStream), 4096)
	}

	constructor(file: String, bvhSettings: BVHSettings = BVHSettings.BLENDER) : super(file) {
		this.bvhSettings = bvhSettings
		writer = BufferedWriter(OutputStreamWriter(outputStream), 4096)
	}

	private fun getBufferedFrameCount(frameCount: Long): String {
		val frameString = frameCount.toString()
		val bufferCount = LONG_MAX_VALUE_DIGITS - frameString.length

		return if (bufferCount > 0) frameString + StringUtils.repeat(' ', bufferCount) else frameString
	}

	private fun internalNavigateSkeleton(
		bone: Bone,
		header: (bone: Bone, lastBone: Bone?, invertParentRot: Quaternion, distance: Int, hasBranch: Boolean, isParent: Boolean) -> Unit,
		footer: (distance: Int) -> Unit,
		lastBone: Bone? = null,
		invertParentRot: Quaternion = Quaternion.IDENTITY,
		distance: Int = 0,
		isParent: Boolean = false,
	) {
		val parent = bone.parent
		// If we're visiting the parents or at root, continue to the next parent
		val visitParent = (isParent || lastBone == null) && parent != null

		val children = bone.children
		val childCount = children.size - (if (isParent) 1 else 0)

		val hasBranch = visitParent || childCount > 0

		header(bone, lastBone, invertParentRot, distance, hasBranch, isParent)

		if (hasBranch) {
			// Cache this inverted rotation to reduce computation for each branch
			val thisInvertRot = bone.getGlobalRotation().inv()

			if (visitParent) {
				internalNavigateSkeleton(parent, header, footer, bone, thisInvertRot, distance + 1, true)
			}

			for (child in children) {
				// If we're a parent, ignore the child
				if (isParent && child == lastBone) continue
				internalNavigateSkeleton(child, header, footer, bone, thisInvertRot, distance + 1, false)
			}
		}

		footer(distance)
	}

	private fun navigateSkeleton(
		root: Bone,
		header: (bone: Bone, lastBone: Bone?, invertParentRot: Quaternion, distance: Int, hasBranch: Boolean, isParent: Boolean) -> Unit,
		footer: (distance: Int) -> Unit = {},
	) {
		internalNavigateSkeleton(root, header, footer)
	}

	private fun writeBoneDefHeader(bone: Bone?, lastBone: Bone?, invertParentRot: Quaternion, distance: Int, hasBranch: Boolean, isParent: Boolean) {
		val indentLevel = StringUtils.repeat("\t", distance)
		val nextIndentLevel = indentLevel + "\t"

		// Handle ends
		if (bone == null) {
			writer.write("${indentLevel}End Site\n")
		} else {
			writer
				.write("${indentLevel}${if (distance > 0) "JOINT" else "ROOT"} ${bone.boneType}\n")
		}
		writer.write("$indentLevel{\n")

		// Ignore the root and endpoint offsets
		if (bone != null && lastBone != null) {
			writer.write(
				"${nextIndentLevel}OFFSET 0.0 ${(if (isParent) lastBone.length else -lastBone.length) * bvhSettings.offsetScale} 0.0\n",
			)
		} else {
			writer.write("${nextIndentLevel}OFFSET 0.0 0.0 0.0\n")
		}

		// Define channels
		if (bone != null) {
			// Only give position for root
			if (lastBone != null) {
				writer.write("${nextIndentLevel}CHANNELS 3 Zrotation Xrotation Yrotation\n")
			} else {
				writer.write(
					"${nextIndentLevel}CHANNELS 6 Xposition Yposition Zposition Zrotation Xrotation Yrotation\n",
				)
			}

			// Write an empty end bone if there are no branches
			// We use null for convenience and treat it as an end node (no bone)
			if (!hasBranch) {
				val endDistance = distance + 1
				writeBoneDefHeader(null, bone, Quaternion.IDENTITY, endDistance, false, false)
				writeBoneDefFooter(endDistance)
			}
		}
	}

	private fun writeBoneDefFooter(level: Int) {
		// Closing bracket
		writer.write("${StringUtils.repeat("\t", level)}}\n")
	}

	private fun writeSkeletonDef(rootBone: Bone) {
		navigateSkeleton(rootBone, ::writeBoneDefHeader, ::writeBoneDefFooter)
	}

	@Throws(IOException::class)
	override fun writeHeader(skeleton: HumanSkeleton, streamer: PoseStreamer) {
		writer.write("HIERARCHY\n")
		writeSkeletonDef(skeleton.getBone(bvhSettings.rootBone))

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
		writer.write("Frame Time: ${streamer.frameInterval}\n")
	}

	private fun writeBoneRot(bone: Bone, lastBone: Bone?, invertParentRot: Quaternion, distance: Int, hasBranch: Boolean, isParent: Boolean) {
		val rot = invertParentRot * bone.getGlobalRotation()
		val angles = rot.toEulerAngles(EulerOrder.ZXY)

		// Output in order of roll (Z), pitch (X), yaw (Y) (extrinsic)
		// Assume spacing is needed at the start (we start with position with no following space)
		writer
			.write(" ${angles.z * FastMath.RAD_TO_DEG} ${angles.x * FastMath.RAD_TO_DEG} ${angles.y * FastMath.RAD_TO_DEG}")
	}

	@Throws(IOException::class)
	override fun writeFrame(skeleton: HumanSkeleton) {
		val rootBone = skeleton.getBone(bvhSettings.rootBone)

		val rootPos = rootBone.getPosition()

		// Write root position
		val positionScale = bvhSettings.positionScale
		writer
			.write("${rootPos.x * positionScale} ${rootPos.y * positionScale} ${rootPos.z * positionScale}")

		navigateSkeleton(rootBone, ::writeBoneRot)
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
