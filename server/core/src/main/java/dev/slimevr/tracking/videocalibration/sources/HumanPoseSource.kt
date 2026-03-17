package dev.slimevr.tracking.videocalibration.sources

import RtmposeOnnxPipeline
import dev.onvoid.webrtc.media.FourCC
import dev.onvoid.webrtc.media.video.NativeI420Buffer
import dev.onvoid.webrtc.media.video.VideoBufferConverter
import dev.onvoid.webrtc.media.video.VideoFrame
import dev.slimevr.tracking.videocalibration.networking.WebRTCManager
import dev.slimevr.tracking.videocalibration.snapshots.HumanPoseSnapshot
import dev.slimevr.tracking.videocalibration.snapshots.ImageSnapshot
import dev.slimevr.tracking.videocalibration.util.DebugOutput
import io.eiren.util.logging.LogManager
import io.github.axisangles.ktmath.Vector2D
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.awt.image.DataBufferInt
import java.util.concurrent.atomic.AtomicReference
import kotlin.io.path.absolutePathString
import kotlin.io.path.toPath

class HumanPoseSource(
	val imagesSource: Channel<ImageSnapshot>,
	val webRTCManager: WebRTCManager,
	val debugOutput: DebugOutput,
) {

	enum class Status {
		NOT_RUNNING,
		RUNNING,
		DONE,
	}

	val status = AtomicReference(Status.NOT_RUNNING)
	val humanPoseSnapshots = Channel<HumanPoseSnapshot>(Channel.Factory.UNLIMITED)

	private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
	private val pipeline = RtmposeOnnxPipeline(
		"",
		HumanPoseSource::class.java.protectionDomain.codeSource.location.toURI().toPath().parent.resolve("rtmpose-m_simcc-body7_pt-body7_420e-256x192-e48f03d0_20230504.onnx").absolutePathString(),
	)

	fun start() {
		scope.launch {
			status.set(Status.RUNNING)
			pipeline.start()
			try {
				for (imageFrame in imagesSource) {
					detectPose(imageFrame)
				}
			} catch (e: Exception) {
				LogManager.warning("Human pose source failed", e)
				scope.cancel()
			} finally {
				status.set(Status.DONE)
			}
		}
	}

	fun requestStop() {
		scope.cancel()
	}

	private fun detectPose(imageFrame: ImageSnapshot) {
		val poseResponse = pipeline.processImage(imageFrame.image)
		val detection = poseResponse.detections.firstOrNull()
		if (detection != null) {
			val joints = detection.joints.filter {
				it.visible
			}.associate {
				it.name to Vector2D(it.x, it.y)
			}

			val humanPoseSnapshot = HumanPoseSnapshot(imageFrame.instant, imageFrame.timestamp, joints, imageFrame.camera)
			humanPoseSnapshots.trySend(humanPoseSnapshot)

			val poseImage = debugOutput.drawHumanPoseImage(
				imageFrame.image,
				humanPoseSnapshot,
			)

			debugOutput.saveHumanPoseImage(imageFrame.timestamp, poseImage)

			val imageBytes = intRgbToRgbaBytes(poseImage.raster.dataBuffer as DataBufferInt)
			val imageI420Bytes = NativeI420Buffer.allocate(poseImage.width, poseImage.height)
			VideoBufferConverter.convertToI420(imageBytes, imageI420Bytes, FourCC.RGBA)

			val videoFrame = VideoFrame(imageI420Bytes, imageFrame.timestamp.inWholeNanoseconds)

			webRTCManager.broadcastVideoFrame(WebRTCManager.VideoProvider.VIDEO_CALIBRATION, videoFrame)
		}
	}

	private fun intRgbToRgbaBytes(buffer: DataBufferInt): ByteArray {
		val src = buffer.data
		val dst = ByteArray(src.size * 4)

		var di = 0
		for (pixel in src) {
			dst[di++] = 0xFF.toByte() // A
			dst[di++] = (pixel and 0xFF).toByte() // B
			dst[di++] = ((pixel shr 8) and 0xFF).toByte() // G
			dst[di++] = ((pixel shr 16) and 0xFF).toByte() // R
		}

		return dst
	}
}
