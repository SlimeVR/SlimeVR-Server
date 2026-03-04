package dev.slimevr.posestreamer

import com.jme3.math.FastMath
import dev.slimevr.tracking.processor.Bone
import dev.slimevr.tracking.processor.skeleton.HumanSkeleton
import io.github.axisangles.ktmath.EulerOrder
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
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
		header: (bone: Bone, lastBone: Bone?, invertParentRot: Quaternion, distance: Int, hasBranch: Boolean, invertOffset: Boolean, zeroOffset: Boolean) -> Unit,
		footer: (distance: Int) -> Unit,
		lastBone: Bone? = null,
		invertParentRot: Quaternion = Quaternion.IDENTITY,
		distance: Int = 0,
		invertOffset: Boolean = false,
		zeroOffset: Boolean = false,
	) {
		val isRoot = lastBone == null
		val isInverse = bone === lastBone?.parent
		val parent = bone.parent

		// If we're visiting the parents or at root, continue to the next parent
		val visitParent = (isInverse || isRoot) && parent != null

		// We are navigating inversely; this bone's node is at the head rather than
		//  the tail, so we let our origin child take our children, and we take our
		//  parent's children in turn
		// Root is a special condition where it is both states simultaneously
		val invChildren = if (visitParent) {
			parent.children
		} else {
			emptyList()
		}
		val children = if (!isInverse) {
			bone.children
		} else {
			emptyList()
		}

		val hasBranch = visitParent || children.isNotEmpty()

		header(
			bone,
			lastBone,
			invertParentRot,
			distance,
			hasBranch,
			invertOffset,
			zeroOffset,
		)

		if (hasBranch) {
			// Cache this inverted rotation to reduce computation for each branch
			val thisInvertRot = bone.rotationOffset * bone.getGlobalRotation().inv()

			if (visitParent) {
				internalNavigateSkeleton(
					parent,
					header,
					footer,
					bone,
					thisInvertRot,
					distance + 1,
					invertOffset || isInverse,
					false,
				)
			}

			for (child in invChildren) {
				// Ignore our own bone (from parent's children)
				if (child == bone) continue
				internalNavigateSkeleton(
					child,
					header,
					footer,
					bone,
					thisInvertRot,
					distance + 1,
					true,
					!invertOffset,
				)
			}

			for (child in children) {
				internalNavigateSkeleton(
					child,
					header,
					footer,
					bone,
					thisInvertRot,
					distance + 1,
					false,
					invertOffset,
				)
			}
		}

		footer(distance)
	}

	private fun navigateSkeleton(
		root: Bone,
		header: (bone: Bone, lastBone: Bone?, invertParentRot: Quaternion, distance: Int, hasBranch: Boolean, invertOffset: Boolean, zeroOffset: Boolean) -> Unit,
		footer: (distance: Int) -> Unit = {},
		// Default true if the root isn't the true root (usually hip)
		tailRoot: Boolean = root.parent != null,
	) {
		// Root is treated as a parent if we want to target the tail as the root node
		internalNavigateSkeleton(root, header, footer, invertOffset = tailRoot)
	}

	private fun writeBoneDefHeader(bone: Bone?, lastBone: Bone?, invertParentRot: Quaternion, distance: Int, hasBranch: Boolean, invertOffset: Boolean, zeroOffset: Boolean) {
		val isRoot = lastBone == null
		val indentLevel = StringUtils.repeat("\t", distance)
		val nextIndentLevel = indentLevel + "\t"

		// Handle ends
		if (bone == null) {
			writer.write("${indentLevel}End Site\n")
		} else {
			writer.write("${indentLevel}${if (!isRoot) "JOINT" else "ROOT"} ${bone.boneType}\n")
		}
		writer.write("$indentLevel{\n")

		// "OFFSET": Defines the parent bone's local tail position
		// Ignore the root and endpoint offsets
		val offset = if (zeroOffset || isRoot) {
			Vector3.NULL
		} else {
			lastBone.rotationOffset.sandwichUnitY() * ((if (invertOffset) 1 else -1) * lastBone.length * bvhSettings.offsetScale)
		}
		writer.write("${nextIndentLevel}OFFSET ${offset.x} ${offset.y} ${offset.z}\n")

		// Define channels
		if (bone != null) {
			// Only give position for root
			if (!isRoot) {
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
				writeBoneDefHeader(
					null,
					bone,
					Quaternion.IDENTITY,
					endDistance,
					hasBranch = false,
					invertOffset = false,
					zeroOffset = false,
				)
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

	private fun writeBoneRot(bone: Bone, lastBone: Bone?, invertParentRot: Quaternion, distance: Int, hasBranch: Boolean, invertOffset: Boolean, zeroOffset: Boolean) {
		val rot = bone.rotationOffset.inv() * invertParentRot * bone.getGlobalRotation()
		val angles = rot.toEulerAngles(EulerOrder.ZXY)

		// We're the root, so write position
		if (lastBone == null) {
			val rootPos = if (invertOffset) {
				bone.getTailPosition()
			} else {
				bone.getPosition()
			}
			// Write root position
			val positionScale = bvhSettings.positionScale
			writer.write("${rootPos.x * positionScale} ${rootPos.y * positionScale} ${rootPos.z * positionScale}")
		}

		// Output in order of roll (Z), pitch (X), yaw (Y) (extrinsic)
		// Assume spacing is needed at the start (we start with position with no following space)
		writer
			.write(" ${angles.z * FastMath.RAD_TO_DEG} ${angles.x * FastMath.RAD_TO_DEG} ${angles.y * FastMath.RAD_TO_DEG}")
	}

	@Throws(IOException::class)
	override fun writeFrame(skeleton: HumanSkeleton) {
		val rootBone = skeleton.getBone(bvhSettings.rootBone)
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
