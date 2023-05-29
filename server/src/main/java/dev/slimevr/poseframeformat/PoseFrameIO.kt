package dev.slimevr.poseframeformat

import dev.slimevr.poseframeformat.trackerdata.TrackerFrame
import dev.slimevr.poseframeformat.trackerdata.TrackerFrameData
import dev.slimevr.poseframeformat.trackerdata.TrackerFrames
import dev.slimevr.tracking.trackers.TrackerPosition
import dev.slimevr.tracking.trackers.TrackerPosition.Companion.getByDesignation
import io.eiren.util.collections.FastList
import io.eiren.util.logging.LogManager
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

object PoseFrameIO {
	@Throws(IOException::class)
	private fun writeVector3f(outputStream: DataOutputStream, vector: Vector3) {
		outputStream.writeFloat(vector.x)
		outputStream.writeFloat(vector.y)
		outputStream.writeFloat(vector.z)
	}

	@Throws(IOException::class)
	private fun writeQuaternion(outputStream: DataOutputStream, quaternion: Quaternion) {
		outputStream.writeFloat(quaternion.x)
		outputStream.writeFloat(quaternion.y)
		outputStream.writeFloat(quaternion.z)
		outputStream.writeFloat(quaternion.w)
	}

	fun writeFrames(outputStream: DataOutputStream, frames: PoseFrames) {
		outputStream.writeInt(frames.frameHolders.size)
		for (tracker in frames.frameHolders) {
			outputStream.writeUTF(tracker.name)
			outputStream.writeInt(tracker.frames.size)
			for (i in 0 until tracker.frames.size) {
				val trackerFrame = tracker.tryGetFrame(i)
				if (trackerFrame == null) {
					outputStream.writeInt(0)
					continue
				}
				var dataFlags = trackerFrame.dataFlags

				// Don't write destination strings anymore, replace with
				// the enum
				if (trackerFrame.hasData(TrackerFrameData.DESIGNATION_STRING)) {
					dataFlags = TrackerFrameData.TRACKER_POSITION_ENUM
						.add(TrackerFrameData.DESIGNATION_STRING.remove(dataFlags))
				}
				outputStream.writeInt(dataFlags)
				if (trackerFrame.hasData(TrackerFrameData.ROTATION)) {
					writeQuaternion(outputStream, trackerFrame.rotation!!)
				}
				if (trackerFrame.hasData(TrackerFrameData.POSITION)) {
					writeVector3f(outputStream, trackerFrame.position!!)
				}
				if (TrackerFrameData.TRACKER_POSITION_ENUM.check(dataFlags)) {
					outputStream.writeInt(trackerFrame.trackerPosition!!.ordinal)
				}
				if (trackerFrame.hasData(TrackerFrameData.ACCELERATION)) {
					writeVector3f(outputStream, trackerFrame.acceleration!!)
				}
				if (trackerFrame.hasData(TrackerFrameData.RAW_ROTATION)) {
					writeQuaternion(outputStream, trackerFrame.rawRotation!!)
				}
			}
		}
	}

	fun tryWriteFrames(outputStream: DataOutputStream, frames: PoseFrames): Boolean {
		try {
			writeFrames(outputStream, frames)
		} catch (e: Exception) {
			LogManager.severe("Error writing frame to stream", e)
			return false
		}
		return true
	}

	fun writeToFile(file: File, frames: PoseFrames) {
		DataOutputStream(
			BufferedOutputStream(FileOutputStream(file))
		).use { outputStream -> writeFrames(outputStream, frames) }
	}

	fun tryWriteToFile(file: File, frames: PoseFrames): Boolean {
		try {
			writeToFile(file, frames)
		} catch (e: Exception) {
			LogManager.severe("Error writing frames to file", e)
			return false
		}
		return true
	}

	@Throws(IOException::class)
	private fun readVector3f(inputStream: DataInputStream): Vector3 {
		return Vector3(
			inputStream.readFloat(),
			inputStream.readFloat(),
			inputStream.readFloat()
		)
	}

	@Throws(IOException::class)
	private fun readQuaternion(inputStream: DataInputStream): Quaternion {
		val x = inputStream.readFloat()
		val y = inputStream.readFloat()
		val z = inputStream.readFloat()
		val w = inputStream.readFloat()

		return Quaternion(w, x, y, z)
	}

	fun readFrames(inputStream: DataInputStream): PoseFrames {
		val trackerCount = inputStream.readInt()
		val trackers = FastList<TrackerFrames>(trackerCount)
		for (i in 0 until trackerCount) {
			val name = inputStream.readUTF()
			val trackerFrameCount = inputStream.readInt()
			val trackerFrames = FastList<TrackerFrame?>(
				trackerFrameCount
			)
			for (j in 0 until trackerFrameCount) {
				val dataFlags = inputStream.readInt()
				var designation: TrackerPosition? = null
				if (TrackerFrameData.DESIGNATION_STRING.check(dataFlags)) {
					designation = getByDesignation(inputStream.readUTF())
				}
				var rotation: Quaternion? = null
				if (TrackerFrameData.ROTATION.check(dataFlags)) {
					rotation = readQuaternion(inputStream)
				}
				var position: Vector3? = null
				if (TrackerFrameData.POSITION.check(dataFlags)) {
					position = readVector3f(inputStream)
				}
				if (TrackerFrameData.TRACKER_POSITION_ENUM.check(dataFlags)) {
					designation = TrackerPosition.values()[inputStream.readInt()]
				}
				var acceleration: Vector3? = null
				if (TrackerFrameData.ACCELERATION.check(dataFlags)) {
					acceleration = readVector3f(inputStream)
				}
				var rawRotation: Quaternion? = null
				if (TrackerFrameData.RAW_ROTATION.check(dataFlags)) {
					rawRotation = readQuaternion(inputStream)
				}
				trackerFrames
					.add(
						TrackerFrame(
							designation,
							rotation,
							position,
							acceleration,
							rawRotation
						)
					)
			}
			trackers.add(TrackerFrames(name, trackerFrames))
		}
		return PoseFrames(trackers)
	}

	fun tryReadFrames(inputStream: DataInputStream): PoseFrames? {
		return try {
			return readFrames(inputStream)
		} catch (e: Exception) {
			LogManager.severe("Error reading frames from stream", e)
			null
		}
	}

	fun readFromFile(file: File): PoseFrames {
		return readFrames(
			DataInputStream(BufferedInputStream(FileInputStream(file)))
		)
	}

	fun tryReadFromFile(file: File): PoseFrames? {
		return try {
			return readFrames(
				DataInputStream(BufferedInputStream(FileInputStream(file)))
			)
		} catch (e: Exception) {
			LogManager.severe("Error reading frames from file", e)
			null
		}
	}
}
