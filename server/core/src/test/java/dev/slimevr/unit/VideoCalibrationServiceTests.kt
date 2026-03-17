package dev.slimevr.unit

import dev.slimevr.poseframeformat.PfsIO
import dev.slimevr.poseframeformat.PoseFrames
import dev.slimevr.protocol.ConnectionContext
import dev.slimevr.protocol.GenericConnection
import dev.slimevr.tracking.processor.config.SkeletonConfigManager
import dev.slimevr.tracking.processor.config.SkeletonConfigOffsets
import dev.slimevr.tracking.trackers.Tracker
import dev.slimevr.tracking.trackers.TrackerPosition
import dev.slimevr.tracking.videocalibration.data.Camera
import dev.slimevr.tracking.videocalibration.data.CameraExtrinsic
import dev.slimevr.tracking.videocalibration.data.CameraIntrinsic
import dev.slimevr.tracking.videocalibration.networking.WebRTCManager
import dev.slimevr.tracking.videocalibration.snapshots.ImageSnapshot
import dev.slimevr.tracking.videocalibration.snapshots.TrackerSnapshot
import dev.slimevr.tracking.videocalibration.snapshots.TrackersSnapshot
import dev.slimevr.tracking.videocalibration.sources.HumanPoseSource
import dev.slimevr.tracking.videocalibration.sources.SnapshotsDatabase
import dev.slimevr.tracking.videocalibration.steps.Step
import dev.slimevr.tracking.videocalibration.steps.VideoCalibrator
import dev.slimevr.tracking.videocalibration.util.DebugOutput
import io.eiren.util.logging.LogManager
import io.github.axisangles.ktmath.QuaternionD
import io.github.axisangles.ktmath.Vector3D
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.awt.Dimension
import java.nio.ByteBuffer
import java.util.UUID
import javax.imageio.ImageIO
import kotlin.io.path.Path
import kotlin.io.path.listDirectoryEntries
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

class VideoCalibrationServiceTests {

	@Test
	fun e2eCalibration() {
		val dataFolder = Path("C:\\Users\\yilan\\AppData\\Roaming\\dev.slimevr.SlimeVR\\VideoCalibrationTest19")

		val imageSnapshots = Channel<ImageSnapshot>(Channel.CONFLATED)
		val trackersSnapshots = Channel<TrackersSnapshot>(Channel.UNLIMITED)

		val trackersFPS = 120

		val debugOutput = DebugOutput(Path("C:\\SlimeVR\\VideoCalibrationTest"), detailed = true)

		val webRTCManager = WebRTCManager()

		val humanPoseSource = HumanPoseSource(imageSnapshots, webRTCManager, debugOutput)
		humanPoseSource.start()

		val database =
			SnapshotsDatabase(
				2.seconds / trackersFPS.toDouble(),
				humanPoseSource.humanPoseSnapshots,
				trackersSnapshots,
			)

		val trackerPositions = setOf(
			TrackerPosition.CHEST,
			TrackerPosition.HIP,
			TrackerPosition.LEFT_UPPER_LEG,
			TrackerPosition.LEFT_LOWER_LEG,
			TrackerPosition.RIGHT_UPPER_LEG,
			TrackerPosition.RIGHT_LOWER_LEG,
// 			TrackerPosition.LEFT_UPPER_ARM,
// 			TrackerPosition.RIGHT_UPPER_ARM, // FIXME: Data may not contain these positions
		)

		val trackersToReset =
			trackerPositions
				.associateWith {
					Tracker(
						device = null,
						id = 0,
						name = it.toString(),
						displayName = it.toString(),
						trackerPosition = it,
					)
				}

		val websocket = object : GenericConnection {
			override val connectionId = UUID.randomUUID()

			override val context: ConnectionContext
				get() = TODO("Not yet implemented")

			override fun send(bytes: ByteBuffer) {
				LogManager.debug("Sending progress update...")
			}
		}

		val skeletonConfigManager = SkeletonConfigManager(false, null)
		skeletonConfigManager.setOffset(SkeletonConfigOffsets.HIPS_WIDTH, 0.32f)
		skeletonConfigManager.setOffset(SkeletonConfigOffsets.UPPER_LEG, 0.52f)
		skeletonConfigManager.setOffset(SkeletonConfigOffsets.LOWER_LEG, 0.50f)

		val webcamFolder = dataFolder.resolve("1_webcam")
		webcamFolder.toFile().copyRecursively(debugOutput.webcamDir.toFile(), true)

		val videoCalibrator =
			VideoCalibrator(
				trackersToReset,
				skeletonConfigManager,
				database,
				websocket,
				debugOutput,
			)

		videoCalibrator.start()

		val cameraParams = dataFolder.resolve("camera.txt").toFile().readLines()

		val camera = Camera(
			CameraExtrinsic.fromCameraPose(
				QuaternionD(cameraParams[0].toDouble(), cameraParams[1].toDouble(), cameraParams[2].toDouble(), cameraParams[3].toDouble()),
				Vector3D(cameraParams[4].toDouble(), cameraParams[5].toDouble(), cameraParams[6].toDouble()),
			),
			CameraIntrinsic(cameraParams[7].toDouble(), cameraParams[8].toDouble(), cameraParams[9].toDouble(), cameraParams[10].toDouble()),
			Dimension(cameraParams[11].toInt(), cameraParams[12].toInt()),
		)

		CoroutineScope(Dispatchers.IO).launch {
			val startTime = TimeSource.Monotonic.markNow()

			val imagePaths = webcamFolder.listDirectoryEntries().sorted().toList()
			val regex = Regex("""webcam_(\d+)\.jpg""")

			for (imagePath in imagePaths) {
				val timeOffset = regex.matchEntire(imagePath.fileName.toString())
					?.groupValues?.get(1)
					?.toInt()
					?.milliseconds

				if (timeOffset == null) {
					continue
				}

				val now = TimeSource.Monotonic.markNow()
				val toDelay = timeOffset - (now - startTime)
				if (toDelay.isPositive()) {
					delay(toDelay)
				}

				val image = ImageIO.read(imagePath.toFile())
				val snapshot = ImageSnapshot(now, timeOffset, image, camera)
				imageSnapshots.trySend(snapshot)
			}
		}

		CoroutineScope(Dispatchers.IO).launch {
			val startTime = TimeSource.Monotonic.markNow()
			val trackersInterval = 1.seconds / trackersFPS

			val poseFrames = PfsIO.readFromFile(dataFolder.resolve("trackers.pfs").toFile())

			var frameIndex = 0
			while (true) {
				val trackers = buildTrackersSnapshot(poseFrames, frameIndex)
				if (trackers.isEmpty()) {
					break
				}

				val now = TimeSource.Monotonic.markNow()

				val trackersSnapshot = TrackersSnapshot(now, trackers)
				trackersSnapshots.trySend(trackersSnapshot)

				val nextTrackersTime = startTime + trackersInterval * frameIndex
				val toDelay = nextTrackersTime - now
				++frameIndex

				if (toDelay.isPositive()) {
					delay(toDelay)
				}
			}
		}

		while (true) {
			val step = videoCalibrator.step.get()
			if (step == Step.DONE) {
				break
			}

			Thread.sleep(1000)
		}

		assertEquals(Step.DONE, videoCalibrator.step.get())
	}

	private fun buildTrackersSnapshot(poseFrames: PoseFrames, frameIndex: Int): Map<TrackerPosition, TrackerSnapshot> {
		val map = mutableMapOf<TrackerPosition, TrackerSnapshot>()

		for (holder in poseFrames.frameHolders) {
			if (frameIndex >= holder.frames.size) continue
			val trackerFrame = holder.frames[frameIndex] ?: continue
			val position = trackerFrame.trackerPosition ?: continue

			map[position] =
				TrackerSnapshot(
					trackerFrame.rawRotation?.toDouble()
						?: error("Missing raw rotation"),
					trackerFrame.rotation?.toDouble()
						?: error("Missing adjusted rotation"),
					trackerFrame.position?.toDouble(),
				)
		}

		return map
	}
}
