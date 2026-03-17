package dev.slimevr.tracking.videocalibration.util

import dev.slimevr.SLIMEVR_IDENTIFIER
import dev.slimevr.poseframeformat.PfsIO
import dev.slimevr.poseframeformat.PoseFrames
import dev.slimevr.poseframeformat.trackerdata.TrackerFrame
import dev.slimevr.poseframeformat.trackerdata.TrackerFrames
import dev.slimevr.tracking.processor.config.SkeletonConfigOffsets
import dev.slimevr.tracking.processor.skeleton.refactor.Skeleton
import dev.slimevr.tracking.processor.skeleton.refactor.SkeletonUpdater
import dev.slimevr.tracking.trackers.Tracker
import dev.slimevr.tracking.trackers.TrackerPosition
import dev.slimevr.tracking.videocalibration.data.Camera
import dev.slimevr.tracking.videocalibration.data.CocoWholeBodyKeypoint
import dev.slimevr.tracking.videocalibration.data.TrackerResetOverride
import dev.slimevr.tracking.videocalibration.snapshots.HumanPoseSnapshot
import dev.slimevr.tracking.videocalibration.snapshots.TrackerSnapshot
import dev.slimevr.tracking.videocalibration.snapshots.TrackersSnapshot
import io.eiren.util.OperatingSystem
import io.eiren.util.collections.FastList
import io.eiren.util.logging.LogManager
import io.github.axisangles.ktmath.QuaternionD
import io.github.axisangles.ktmath.Vector2D
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.Stroke
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.Path
import javax.imageio.ImageIO
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.createDirectory
import kotlin.io.path.exists
import kotlin.io.path.writeText
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

class DebugOutput(
	dir: Path,
	private val detailed: Boolean = false,
) {

	val cameraFile: Path
	val webcamDir: Path
	val humanPosesDir: Path
	val reconstructionDir: Path
	val skeletonOffsetsDir: Path
	val trackersFile: Path
	val cameraAlignmentImage: Path
	val reconstructionVideo: Path
	val skeletonOffsetsVideo: Path

	init {
		dir.toFile().deleteRecursively()
		dir.createDirectory()

		cameraFile = dir.resolve("camera.txt")

		webcamDir = dir.resolve("1_webcam")
		webcamDir.createDirectory()

		humanPosesDir = dir.resolve("2_poses")
		humanPosesDir.createDirectory()

		reconstructionDir = dir.resolve("3_reconstruction")
		reconstructionDir.createDirectory()

		skeletonOffsetsDir = dir.resolve("4_skeleton_offsets")
		skeletonOffsetsDir.createDirectory()

		trackersFile = dir.resolve("trackers.pfs")
		cameraAlignmentImage = dir.resolve("camera_alignment.png")
		reconstructionVideo = dir.resolve("reconstruction.mp4")
		skeletonOffsetsVideo = dir.resolve("skeleton_offsets.mp4")
	}

	fun saveCamera(camera: Camera) {
		val c2w = camera.extrinsic.cameraToWorld
		val co = camera.extrinsic.cameraOriginInWorld
		val i = camera.intrinsic
		val s = camera.imageSize

		val lines = listOf(
			c2w.w,
			c2w.x,
			c2w.y,
			c2w.z,
			co.x,
			co.y,
			co.z,
			i.fx,
			i.fy,
			i.tx,
			i.ty,
			s.width,
			s.height,
		)

		cameraFile.writeText(
			lines.joinToString("\r\n") { it.toString() },
		)
	}

	fun webcamImage(timestamp: Duration): Path = webcamDir.resolve("webcam_${timestamp.inWholeMilliseconds.toString().padStart(6, '0')}.jpg")

	fun saveWebcamImage(timestamp: Duration, image: BufferedImage) {
		ImageIO.write(image, "jpg", webcamImage(timestamp).toFile())
	}

	fun humanPoseImage(timestamp: Duration): Path = humanPosesDir.resolve("pose_${timestamp.inWholeMilliseconds.toString().padStart(6, '0')}.jpg")

	fun drawHumanPoseImage(image: BufferedImage, humanPoseSnapshot: HumanPoseSnapshot): BufferedImage {
		val poseImage = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)

		val g = poseImage.createGraphics()
		g.drawImage(image, 0, 0, image.width, image.height, 0, 0, image.width, image.height, null)
		drawBones(g, humanPoseSnapshot)
		g.dispose()

		return poseImage
	}

	fun saveHumanPoseImage(timestamp: Duration, image: BufferedImage) {
		if (!detailed) {
			return
		}

		ImageIO.write(image, "jpg", humanPoseImage(timestamp).toFile())
	}

	fun saveHandToControllerMatches(
		correspondences: List<Pair<Vector2D?, Vector2D>>,
		imageSize: Dimension,
	) {
		val image = BufferedImage(imageSize.width, imageSize.height, BufferedImage.TYPE_INT_RGB)

		val g = image.createGraphics()

		g.background = MATCH_BACKGROUND
		g.clearRect(0, 0, image.width, image.height)

		for ((controller, joint) in correspondences) {
			if (controller == null) {
				continue
			}

			drawLine(g, controller, joint, MATCH_MATCH_COLOR, MATCH_STROKE)

			drawLine(g, controller + Vector2D(-MATCH_MARKER_SIZE, -MATCH_MARKER_SIZE), controller + Vector2D(MATCH_MARKER_SIZE, MATCH_MARKER_SIZE), MATCH_CONTROLLER_COLOR, MATCH_STROKE)
			drawLine(g, controller + Vector2D(MATCH_MARKER_SIZE, -MATCH_MARKER_SIZE), controller + Vector2D(-MATCH_MARKER_SIZE, MATCH_MARKER_SIZE), MATCH_CONTROLLER_COLOR, MATCH_STROKE)

			drawCircle(g, joint, MATCH_MARKER_SIZE, MATCH_JOINT_COLOR, MATCH_STROKE)
		}

		for (i in 0 until correspondences.size - 1) {
			val pA = correspondences[i].second
			val pB = correspondences[i + 1].second
			drawLine(g, pA, pB, MATCH_PATH_COLOR, MATCH_STROKE)
		}

		g.dispose()

		ImageIO.write(image, "png", cameraAlignmentImage.toFile())
	}

	/**
	 * Saves all the tracker snapshots as a [PfsIO] recording.
	 */
	fun saveTrackerSnapshots(
		trackers: Map<TrackerPosition, Tracker>,
		snapshots: List<TrackersSnapshot>,
		interval: Duration,
	) {
		val poseFrames = PoseFrames(trackers.size)
		poseFrames.frameInterval = interval.toDouble(DurationUnit.SECONDS).toFloat()

		for ((trackerPosition, tracker) in trackers) {
			val frames = FastList<TrackerFrame?>(snapshots.size)
			for (snapshot in snapshots) {
				val trackerSnapshot = snapshot.trackers[trackerPosition]
				val frame =
					if (trackerSnapshot != null) {
						TrackerFrame(
							trackerPosition,
							trackerSnapshot.adjustedTrackerToWorld.toFloat(),
							trackerSnapshot.trackerOriginInWorld?.toFloat(),
							null,
							trackerSnapshot.rawTrackerToWorld.toFloat(),
						)
					} else {
						TrackerFrame(
							trackerPosition,
							null,
							null,
							null,
							null,
						)
					}
				frames.add(frame)
			}

			poseFrames.frameHolders.add(TrackerFrames(tracker.name, frames))
		}

		PfsIO.writeToFile(trackersFile.toFile(), poseFrames)
	}

	fun reconstructionImage(index: Int): File = reconstructionDir.resolve("reconstruction_${index.toString().padStart(6, '0')}.jpg").toFile()

	fun saveReconstruction(
		camera: Camera,
		frames: List<Pair<HumanPoseSnapshot, Map<TrackerPosition, TrackerSnapshot>>>,
		trackerResets: Map<TrackerPosition, TrackerResetOverride>,
	) {
		if (!detailed) {
			return
		}

		LogManager.info("Saving video calibration reconstruction...")

		CoroutineScope(Dispatchers.Default).launch {
			val jobs = frames.withIndex().map { (i, frame) ->
				async {
					val (humanSnapshot, trackersSnapshot) = frame

					val image =
						ImageIO.read(humanPoseImage(humanSnapshot.timestamp).toFile())

					val g = image.createGraphics()
					drawBones(g, humanSnapshot)

					val leftShoulder =
						humanSnapshot.joints[CocoWholeBodyKeypoint.LEFT_SHOULDER]
					val rightShoulder =
						humanSnapshot.joints[CocoWholeBodyKeypoint.RIGHT_SHOULDER]
					val leftHip =
						humanSnapshot.joints[CocoWholeBodyKeypoint.LEFT_HIP]
					val rightHip =
						humanSnapshot.joints[CocoWholeBodyKeypoint.RIGHT_HIP]
					if (leftShoulder != null && rightShoulder != null && leftHip != null && rightHip != null) {
						val midShoulder = (leftShoulder + rightShoulder) * 0.5
						val midHip = (leftHip + rightHip) * 0.5
						drawLine(g, midShoulder, midHip, BONE_COLOR, BONE_STROKE)

						for ((i, trackerPosition) in UPPER_BODY_TRACKERS.withIndex()) {
							val trackerReset =
								trackerResets[trackerPosition] ?: continue
							val tracker = trackersSnapshot[trackerPosition]
								?: continue

							val t = (1.0 + i) / (UPPER_BODY_TRACKERS.size + 2)
							val root = midShoulder * (1.0 - t) + midHip * t

							val r =
								trackerReset.toBoneRotation(tracker.rawTrackerToWorld)
							drawAxis(g, r, root, camera, AXIS_SCALE)
						}
					}

					val headTracker =
						trackersSnapshot[TrackerPosition.HEAD]
					if (headTracker != null) {
						val nose = humanSnapshot.joints[CocoWholeBodyKeypoint.NOSE]
						if (nose != null) {
							val r = headTracker.rawTrackerToWorld
							drawAxis(g, r, nose, camera, AXIS_SCALE)
						}
					}

					for ((trackerPosition, trackerSnapshot) in trackersSnapshot) {
						val (j1, j2) = TRACKER_TO_BONE[trackerPosition] ?: continue
						val trackerReset =
							trackerResets[trackerPosition] ?: continue

						val joint1 = humanSnapshot.joints[j1] ?: continue
						val joint2 = humanSnapshot.joints[j2] ?: continue
						val boneCenter = (joint1 + joint2) * 0.5

						val r =
							trackerReset.toBoneRotation(trackerSnapshot.rawTrackerToWorld)
						drawAxis(g, r, boneCenter, camera, AXIS_SCALE)
					}

					g.dispose()

					ImageIO.write(image, "jpg", reconstructionImage(i))
				}
			}

			jobs.awaitAll()

			val ffmpeg = System.getenv("FFMPEG_PATH")
			if (ffmpeg != null && Path(ffmpeg).exists()) {
				LogManager.info("Generating reconstruction video...")

				var sumInterval = Duration.ZERO
				for (i in 1 until frames.size) {
					val f1 = frames[i - 1]
					val f2 = frames[i]
					val interval = f2.first.timestamp - f1.first.timestamp
					sumInterval += interval
				}

				val avgInterval = sumInterval / frames.size
				val fps = 1.seconds / avgInterval

				val inputPattern = reconstructionDir.resolve("reconstruction_%06d.jpg")

				val command = listOf(
					ffmpeg,
					"-y", // overwrite output
					"-framerate", fps.toString(),
					"-i", inputPattern.absolutePathString(),
					"-c:v", "libx264",
					"-pix_fmt", "yuv420p",
					reconstructionVideo.absolutePathString(),
				)

				val process = ProcessBuilder(command)
					.redirectErrorStream(true)
					.start()

				process.inputStream.bufferedReader().useLines { lines ->
					lines.forEach { println(it) }
				}

				val exitCode = process.waitFor()
				LogManager.info("ffmpeg exited with $exitCode")
			}

			LogManager.info("Video calibration reconstruction complete")
		}
	}

	fun skeletonOffsetImage(index: Int): File = skeletonOffsetsDir.resolve("skeleton_offset_${index.toString().padStart(6, '0')}.jpg").toFile()

	fun saveSkeletonOffsets(
		camera: Camera,
		frames: List<Pair<HumanPoseSnapshot, Map<TrackerPosition, TrackerSnapshot>>>,
		initialSkeletonOffsets: Map<SkeletonConfigOffsets, Float>,
		optimizedSkeletonOffsets: Map<SkeletonConfigOffsets, Float>,
	) {
		if (!detailed) {
			return
		}

		val config = SkeletonUpdater.HumanSkeletonConfig()

		CoroutineScope(Dispatchers.Default).launch {
			val jobs = frames.withIndex().map { (i, frame) ->
				async {
					val (humanSnapshot, trackersSnapshot) = frame

					val baseImage =
						ImageIO.read(humanPoseImage(humanSnapshot.timestamp).toFile())

					val image = BufferedImage(
						baseImage.width * 2,
						baseImage.height,
						BufferedImage.TYPE_INT_RGB,
					)

					val g = image.createGraphics()
					g.drawImage(
						baseImage,
						0,
						0,
						baseImage.width,
						baseImage.height,
						0,
						0,
						baseImage.width,
						baseImage.height,
						null,
					)
					g.drawImage(
						baseImage,
						baseImage.width,
						0,
						baseImage.width * 2,
						baseImage.height,
						0,
						0,
						baseImage.width,
						baseImage.height,
						null,
					)

					val skeleton = Skeleton(false, false)
					val trackersData =
						SkeletonUpdater.TrackersData.fromSnapshot(trackersSnapshot)

					// Draw initial
					SkeletonUpdater(
						skeleton,
						trackersData,
						config,
						initialSkeletonOffsets,
					).update()
					drawSkeleton(g, skeleton, camera, Vector2D.NULL)

					// Draw optimized
					SkeletonUpdater(
						skeleton,
						trackersData,
						config,
						optimizedSkeletonOffsets,
					).update()
					drawSkeleton(
						g,
						skeleton,
						camera,
						Vector2D(baseImage.width.toDouble(), 0.0),
					)

					g.dispose()

					ImageIO.write(image, "jpg", skeletonOffsetImage(i))
				}
			}

			jobs.awaitAll()

			val ffmpeg = System.getenv("FFMPEG_PATH")
			if (ffmpeg != null && Path(ffmpeg).exists()) {
				LogManager.info("Generating reconstruction video...")

				var sumInterval = Duration.ZERO
				for (i in 1 until frames.size) {
					val f1 = frames[i - 1]
					val f2 = frames[i]
					val interval = f2.first.timestamp - f1.first.timestamp
					sumInterval += interval
				}

				val avgInterval = sumInterval / frames.size
				val fps = 1.seconds / avgInterval

				val inputPattern = skeletonOffsetsDir.resolve("skeleton_offset_%06d.jpg")

				val command = listOf(
					ffmpeg,
					"-y", // overwrite output
					"-framerate", fps.toString(),
					"-i", inputPattern.absolutePathString(),
					"-c:v", "libx264",
					"-pix_fmt", "yuv420p",
					skeletonOffsetsVideo.absolutePathString(),
				)

				val process = ProcessBuilder(command)
					.redirectErrorStream(true)
					.start()

				process.inputStream.bufferedReader().useLines { lines ->
					lines.forEach { println(it) }
				}

				val exitCode = process.waitFor()
				LogManager.info("ffmpeg exited with $exitCode")
			}
		}
	}

	private fun drawSkeleton(g: Graphics2D, skeleton: Skeleton, camera: Camera, offset: Vector2D) {
		val bones = mutableListOf(skeleton.headBone)
		while (bones.isNotEmpty()) {
			val bone = bones.removeLast()
			val head = camera.project(bone.getPosition().toDouble())
			val tail = camera.project(bone.getTailPosition().toDouble())
			if (head != null && tail != null) {
				drawLine(g, head + offset, tail + offset, SKELETON_BONE_COLOR, BONE_STROKE)
			}
			bones.addAll(bone.children)
		}
	}

	private fun drawAxis(g: Graphics2D, rotation: QuaternionD, imagePoint: Vector2D, camera: Camera, scale: Double) {
		val x = camera.project(rotation.sandwichUnitX() * scale, imagePoint, 1.0)
		val y = camera.project(rotation.sandwichUnitY() * scale, imagePoint, 1.0)
		val z = camera.project(rotation.sandwichUnitZ() * scale, imagePoint, 1.0)
		if (x != null && y != null && z != null) {
			drawLine(g, imagePoint, imagePoint + x, AXIS_X_COLOR, AXIS_STROKE)
			drawLine(g, imagePoint, imagePoint + y, AXIS_Y_COLOR, AXIS_STROKE)
			drawLine(g, imagePoint, imagePoint + z, AXIS_Z_COLOR, AXIS_STROKE)
		}
	}

	private fun drawBones(g: Graphics2D, humanPoseSnapshot: HumanPoseSnapshot) {
		for ((j1, j2) in BONES) {
			val joint1 = humanPoseSnapshot.joints[j1] ?: continue
			val joint2 = humanPoseSnapshot.joints[j2] ?: continue
			drawLine(g, joint1, joint2, BONE_COLOR, BONE_STROKE)
		}
	}

	private fun drawLine(g: Graphics2D, p1: Vector2D, p2: Vector2D, color: Color, stroke: Stroke) {
		g.color = color
		g.stroke = stroke
		g.drawLine(p1.x.toInt(), p1.y.toInt(), p2.x.toInt(), p2.y.toInt())
	}

	private fun drawCircle(g: Graphics2D, p: Vector2D, r: Double, color: Color, stroke: Stroke) {
		g.color = color
		g.stroke = stroke
		g.drawOval((p.x - r).toInt(), (p.y - r).toInt(), (2.0 * r).toInt(), (2.0 * r).toInt())
	}

	companion object {

		private const val VIDEO_CALIBRATION_FOLDER = "VideoCalibration"

		private val BONES = listOf(
			CocoWholeBodyKeypoint.LEFT_SHOULDER to CocoWholeBodyKeypoint.RIGHT_SHOULDER,
			CocoWholeBodyKeypoint.LEFT_SHOULDER to CocoWholeBodyKeypoint.LEFT_ELBOW,
			CocoWholeBodyKeypoint.LEFT_ELBOW to CocoWholeBodyKeypoint.LEFT_WRIST,
			CocoWholeBodyKeypoint.RIGHT_SHOULDER to CocoWholeBodyKeypoint.RIGHT_ELBOW,
			CocoWholeBodyKeypoint.RIGHT_ELBOW to CocoWholeBodyKeypoint.RIGHT_WRIST,
			CocoWholeBodyKeypoint.LEFT_HIP to CocoWholeBodyKeypoint.RIGHT_HIP,
			CocoWholeBodyKeypoint.LEFT_HIP to CocoWholeBodyKeypoint.LEFT_KNEE,
			CocoWholeBodyKeypoint.LEFT_KNEE to CocoWholeBodyKeypoint.LEFT_ANKLE,
			CocoWholeBodyKeypoint.RIGHT_HIP to CocoWholeBodyKeypoint.RIGHT_KNEE,
			CocoWholeBodyKeypoint.RIGHT_KNEE to CocoWholeBodyKeypoint.RIGHT_ANKLE,
			CocoWholeBodyKeypoint.LEFT_SHOULDER to CocoWholeBodyKeypoint.LEFT_HIP,
			CocoWholeBodyKeypoint.RIGHT_SHOULDER to CocoWholeBodyKeypoint.RIGHT_HIP,
		)

		private val TRACKER_TO_BONE = mapOf(
			TrackerPosition.LEFT_UPPER_ARM to (CocoWholeBodyKeypoint.LEFT_SHOULDER to CocoWholeBodyKeypoint.LEFT_ELBOW),
			TrackerPosition.RIGHT_UPPER_ARM to (CocoWholeBodyKeypoint.RIGHT_SHOULDER to CocoWholeBodyKeypoint.RIGHT_ELBOW),
			TrackerPosition.LEFT_UPPER_LEG to (CocoWholeBodyKeypoint.LEFT_HIP to CocoWholeBodyKeypoint.LEFT_KNEE),
			TrackerPosition.LEFT_LOWER_LEG to (CocoWholeBodyKeypoint.LEFT_KNEE to CocoWholeBodyKeypoint.LEFT_ANKLE),
			TrackerPosition.RIGHT_UPPER_LEG to (CocoWholeBodyKeypoint.RIGHT_HIP to CocoWholeBodyKeypoint.RIGHT_KNEE),
			TrackerPosition.RIGHT_LOWER_LEG to (CocoWholeBodyKeypoint.RIGHT_KNEE to CocoWholeBodyKeypoint.RIGHT_ANKLE),
		)

		private val UPPER_BODY_TRACKERS = listOf(
			TrackerPosition.UPPER_CHEST,
			TrackerPosition.CHEST,
			TrackerPosition.WAIST,
			TrackerPosition.HIP,
		)

		private val MATCH_BACKGROUND = Color.WHITE
		private val MATCH_STROKE = BasicStroke(2.0f)
		private val MATCH_PATH_COLOR = Color.GRAY
		private val MATCH_MATCH_COLOR = Color.GRAY
		private val MATCH_MARKER_SIZE = 5.0
		private val MATCH_JOINT_COLOR = Color.RED
		private val MATCH_CONTROLLER_COLOR = Color.BLUE

		private val BONE_COLOR = Color.GRAY
		private val BONE_STROKE = BasicStroke(4.0f)

		private val AXIS_STROKE = BasicStroke(6.0f)
		private val AXIS_X_COLOR = Color.RED
		private val AXIS_Y_COLOR = Color.GREEN
		private val AXIS_Z_COLOR = Color.BLUE
		private const val AXIS_SCALE = 0.05

		private val SKELETON_BONE_COLOR = Color.GREEN

		val DEFAULT_DIR: Path =
			OperatingSystem
				.resolveConfigDirectory(SLIMEVR_IDENTIFIER)!!
				.resolve(VIDEO_CALIBRATION_FOLDER)
	}
}
