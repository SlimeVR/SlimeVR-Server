package dev.slimevr.tracking.videocalibration.steps

import com.google.flatbuffers.FlatBufferBuilder
import dev.slimevr.protocol.GenericConnection
import dev.slimevr.tracking.processor.config.SkeletonConfigManager
import dev.slimevr.tracking.trackers.Tracker
import dev.slimevr.tracking.trackers.TrackerPosition
import dev.slimevr.tracking.videocalibration.data.Camera
import dev.slimevr.tracking.videocalibration.data.TrackerResetOverride
import dev.slimevr.tracking.videocalibration.snapshots.HumanPoseSnapshot
import dev.slimevr.tracking.videocalibration.snapshots.TrackerSnapshot
import dev.slimevr.tracking.videocalibration.sources.SnapshotsDatabase
import dev.slimevr.tracking.videocalibration.util.DebugOutput
import io.eiren.util.logging.LogManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import solarxr_protocol.MessageBundle
import solarxr_protocol.datatypes.TransactionId
import solarxr_protocol.datatypes.math.Quat
import solarxr_protocol.datatypes.math.Vec3f
import solarxr_protocol.rpc.RpcMessage
import solarxr_protocol.rpc.RpcMessageHeader
import solarxr_protocol.rpc.VideoTrackerCalibrationCamera
import solarxr_protocol.rpc.VideoTrackerCalibrationProgressResponse
import solarxr_protocol.rpc.VideoTrackerCalibrationStatus
import java.util.concurrent.atomic.AtomicReference
import kotlin.collections.iterator
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * State machine for video calibration.
 */
class VideoCalibrator(
	private val trackersToReset: Map<TrackerPosition, Tracker>,
	private val skeletonConfigManager: SkeletonConfigManager,
	private val snapshotsDatabase: SnapshotsDatabase,
	private val websocket: GenericConnection,
	private val debugOutput: DebugOutput,
) {
	val step = AtomicReference(Step.NOT_STARTED)

	private val scope = CoroutineScope(Dispatchers.Default)

	private val solveCamera = SolveCamera(debugOutput)
	private val captureForwardPose = CaptureForwardPose()
	private val captureBentOverPose = CaptureBentOverPose()
	private val solveUpperBodyTracker = SolveUpperBodyTracker()
	private val solveNonUpperBodyTracker = SolveNonUpperBodyTracker()
	private val skeletonOffsetsSolver = SkeletonOffsetsSolver(debugOutput)

	private var camera: SolveCamera.Solution? = null
	private var forwardPose: CaptureForwardPose.Solution? = null
	private var bentOverPose: CaptureBentOverPose.Solution? = null
	private val trackerResets = mutableMapOf<TrackerPosition, TrackerResetOverride>()
	private val trackerToHumanPoseDelays = mutableMapOf<TrackerPosition, Duration>()

	init {
// 		val missingTrackersToReset = REQUIRED_TRACKERS_TO_RESET.subtract(trackersToReset.keys)
// 		require(missingTrackersToReset.isEmpty()) { "Missing trackers to reset: $missingTrackersToReset" }
//
// 		val unsupportedTrackersToReset = trackersToReset.keys.subtract(SUPPORTED_TRACKERS_TO_RESET)
// 		require(unsupportedTrackersToReset.isEmpty()) { "Unsupported trackers to reset: $unsupportedTrackersToReset" }
	}

	/**
	 * Starts video calibration.
	 */
	fun start() {
		LogManager.info("Resetting trackers: ${trackersToReset.keys}")

		scope.launch {
			try {
				run()
			} catch (e: Exception) {
				LogManager.warning("Video calibration failed", e)
				step.set(Step.DONE)
			}
		}
	}

	/**
	 * Stops video calibration.
	 */
	fun requestStop() {
		scope.cancel()
	}

	private suspend fun run() {
		while (true) {
			coroutineContext.ensureActive()

			when (step.get()) {
				Step.NOT_STARTED -> begin()
				Step.SOLVING_CAMERA -> solveCamera()
				Step.CAPTURING_FORWARD_POSE -> captureForwardPose()
				Step.CAPTURING_BENT_OVER_POSE -> captureBentOverPose()
				Step.SOLVING_UPPER_BODY -> solveUpperBody()
				Step.SOLVING_NON_UPPER_BODY -> solveNonUpperBodyTrackerResets()
				Step.SOLVING_SKELETON_OFFSETS -> solveSkeletonOffsets()
				Step.DONE -> break
			}

			delay(DELAY_BETWEEN_ATTEMPTS)
		}
	}

	private fun begin() {
		step.set(Step.SOLVING_CAMERA)

		val fbb = FlatBufferBuilder(512)
		val progressOffset =
			VideoTrackerCalibrationProgressResponse.createVideoTrackerCalibrationProgressResponse(
				fbb,
				VideoTrackerCalibrationStatus.CALIBRATE_CAMERA,
				0,
				0,
				0,
				0,
			)
		val messageOffset = createRPCMessage(fbb, RpcMessage.VideoTrackerCalibrationProgressResponse, progressOffset)
		fbb.finish(messageOffset)
		websocket.send(fbb.dataBuffer())
	}

	private suspend fun solveCamera() {
		LogManager.info("Solving camera...")

		snapshotsDatabase.update()

		val camera = solveCamera.solve(snapshotsDatabase)
		if (camera == null) {
			return
		}

		this.camera = camera

		trackerToHumanPoseDelays[TrackerPosition.HEAD] = camera.cameraDelay
		trackerToHumanPoseDelays[TrackerPosition.LEFT_HAND] = camera.cameraDelay
		trackerToHumanPoseDelays[TrackerPosition.RIGHT_HAND] = camera.cameraDelay

		val fbb = FlatBufferBuilder(512)
		val cameraOffset = addCamera(fbb, camera.camera)
		val progressOffset =
			VideoTrackerCalibrationProgressResponse.createVideoTrackerCalibrationProgressResponse(
				fbb,
				VideoTrackerCalibrationStatus.CAPTURE_FORWARD_POSE,
				cameraOffset,
				0,
				0,
				0,
			)
		val messageOffset = createRPCMessage(fbb, RpcMessage.VideoTrackerCalibrationProgressResponse, progressOffset)
		fbb.finish(messageOffset)
		websocket.send(fbb.dataBuffer())

		snapshotsDatabase.clearRecent()

		step.set(Step.CAPTURING_FORWARD_POSE)

		delay(2.seconds)
	}

	private fun captureForwardPose() {
		LogManager.info("Capturing forward pose...")

		val camera = camera ?: error("Missing camera")

		snapshotsDatabase.update()

		forwardPose = captureForwardPose.capture(snapshotsDatabase.recentTrackersSnapshots)
		if (forwardPose == null) {
			return
		}

		val fbb = FlatBufferBuilder(512)
		val cameraOffset = addCamera(fbb, camera.camera)
		val progressOffset =
			VideoTrackerCalibrationProgressResponse.createVideoTrackerCalibrationProgressResponse(
				fbb,
				VideoTrackerCalibrationStatus.CAPTURE_BENT_OVER_POSE,
				cameraOffset,
				0,
				0,
				0,
			)
		val messageOffset = createRPCMessage(fbb, RpcMessage.VideoTrackerCalibrationProgressResponse, progressOffset)
		fbb.finish(messageOffset)
		websocket.send(fbb.dataBuffer())

		snapshotsDatabase.clearRecent()

		step.set(Step.CAPTURING_BENT_OVER_POSE)
	}

	private fun captureBentOverPose() {
		LogManager.info("Capturing bent-over pose...")

		val camera = camera ?: error("Missing camera")
		val forwardPoseSolution = forwardPose ?: error("Missing forward pose")

		snapshotsDatabase.update()

		bentOverPose = captureBentOverPose.capture(snapshotsDatabase.recentTrackersSnapshots, forwardPoseSolution)
		if (bentOverPose == null) {
			return
		}

		val fbb = FlatBufferBuilder(512)
		val cameraOffset = addCamera(fbb, camera.camera)
		val trackersDoneOffset = addTrackersReset(fbb)
		val trackersToResetOffset = addRemainingTrackersToReset(fbb)
		val progressOffset =
			VideoTrackerCalibrationProgressResponse.createVideoTrackerCalibrationProgressResponse(
				fbb,
				VideoTrackerCalibrationStatus.CALIBRATE_TRACKERS,
				cameraOffset,
				trackersDoneOffset,
				trackersToResetOffset,
				0,
			)
		val messageOffset = createRPCMessage(fbb, RpcMessage.VideoTrackerCalibrationProgressResponse, progressOffset)
		fbb.finish(messageOffset)
		websocket.send(fbb.dataBuffer())

		snapshotsDatabase.clearRecent()

		step.set(Step.SOLVING_UPPER_BODY)
	}

	private fun solveUpperBody() {
		LogManager.info("Solving upper body trackers resets...")

		val camera = camera ?: error("Missing camera")
		val forwardPose = forwardPose ?: error("Missing forward pose")
		val bentOverPose = bentOverPose ?: error("Missing bent over pose")

		for (trackerPosition in UPPER_BODY_TRACKERS_TO_RESET) {
			val tracker = trackersToReset[trackerPosition] ?: continue

			val trackerReset =
				solveUpperBodyTracker.solve(
					trackerPosition,
					forwardPose,
					bentOverPose,
				)

			if (trackerReset == null) {
				error("Failed to solve upper body tracker $trackerPosition")
			}

			trackerResets[trackerPosition] = trackerReset
			tracker.resetsHandler.trackerResetOverride = trackerReset
			tracker.resetsHandler.postProcessResetFull(forwardPose.reference.toFloat())

			// TODO: This camera delay should be computed from tracker itself, and not from the camera step
			trackerToHumanPoseDelays[trackerPosition] = camera.cameraDelay

			val fbb = FlatBufferBuilder(512)
			val cameraOffset = addCamera(fbb, camera.camera)
			val trackersDoneOffset = addTrackersReset(fbb)
			val trackersToResetOffset = addRemainingTrackersToReset(fbb)
			val progressOffset =
				VideoTrackerCalibrationProgressResponse.createVideoTrackerCalibrationProgressResponse(
					fbb,
					VideoTrackerCalibrationStatus.CALIBRATE_TRACKERS,
					cameraOffset,
					trackersDoneOffset,
					trackersToResetOffset,
					0,
				)
			val messageOffset = createRPCMessage(fbb, RpcMessage.VideoTrackerCalibrationProgressResponse, progressOffset)
			fbb.finish(messageOffset)
			websocket.send(fbb.dataBuffer())
		}

		// No need to clear database because we are collecting movements for the
		// remaining trackers while solving the upper body tracker resets.

		step.set(Step.SOLVING_NON_UPPER_BODY)
	}

	private suspend fun solveNonUpperBodyTrackerResets() {
		LogManager.info("Solving remaining trackers resets...")

		val camera = camera ?: error("Missing camera")
		val forwardPose = forwardPose ?: error("Missing forward pose")

		snapshotsDatabase.update()

		val attemptTrackersToReset = trackersToReset.filter { !trackerResets.contains(it.key) }
		val solvedTrackerResets = coroutineScope {
			val jobs = attemptTrackersToReset.map { (trackerPosition, tracker) ->
				async(Dispatchers.Default) {
					trackerPosition to solveNonUpperBodyTracker.solve(
						trackerPosition,
						camera.camera,
						forwardPose,
						snapshotsDatabase,
						camera.cameraDelay,
					)
				}
			}

			jobs.awaitAll()
		}

		for ((trackerPosition, solution) in solvedTrackerResets) {
			if (solution != null) {
				val tracker = trackersToReset[trackerPosition] ?: error("Missing tracker")
				trackerResets[trackerPosition] = solution.trackerReset
				tracker.resetsHandler.trackerResetOverride = solution.trackerReset
				tracker.resetsHandler.postProcessResetFull(forwardPose.reference.toFloat())

				trackerToHumanPoseDelays[trackerPosition] = solution.cameraDelay
			}
		}

// 		for ((trackerPosition, tracker) in trackersToReset) {
// 			if (trackerResets.containsKey(trackerPosition)) {
// 				continue
// 			}
//
// 			snapshotsDatabase.update()
// 			val frames = snapshotsDatabase.matchRecent(camera.cameraDelay)
//
// 			val trackerReset = solveNonUpperBodyTracker.solve(
// 				trackerPosition,
// 				frames,
// 				camera.camera,
// 				forwardPose,
// 			)
//
// 			if (trackerReset == null) {
// 				continue
// 			}
//
// 			trackerResets[trackerPosition] = trackerReset
// 			tracker.resetsHandler.trackerResetOverride = trackerReset
// 			tracker.resetsHandler.postProcessResetFull(forwardPose.reference.toFloat())
//
// 			val fbb = FlatBufferBuilder(512)
// 			val cameraOffset = addCamera(fbb, camera.camera)
// 			val trackersDoneOffset = addTrackersReset(fbb)
// 			val trackersToResetOffset = addRemainingTrackersToReset(fbb)
// 			val progressOffset =
// 				VideoTrackerCalibrationProgressResponse.createVideoTrackerCalibrationProgressResponse(
// 					fbb,
// 					VideoTrackerCalibrationStatus.CALIBRATE_TRACKERS,
// 					cameraOffset,
// 					trackersDoneOffset,
// 					trackersToResetOffset,
// 					0,
// 				)
// 			val messageOffset = createRPCMessage(fbb, RpcMessage.VideoTrackerCalibrationProgressResponse, progressOffset)
// 			fbb.finish(messageOffset)
// 			websocket.send(fbb.dataBuffer())
// 		}

		val remainingTrackersToReset = trackersToReset.keys.subtract(trackerResets.keys)
		if (remainingTrackersToReset.isNotEmpty()) {
			LogManager.debug("Still need to reset $remainingTrackersToReset")
			return
		}

		LogManager.info("Tracker resets:")
		for ((trackerPosition, trackerReset) in trackerResets) {
			LogManager.info("$trackerPosition: $trackerReset")
		}

		val fbb = FlatBufferBuilder(512)
		val cameraOffset = addCamera(fbb, camera.camera)
		val trackersDoneOffset = addTrackersReset(fbb)
		val trackersToResetOffset = addRemainingTrackersToReset(fbb)
		val progressOffset =
			VideoTrackerCalibrationProgressResponse.createVideoTrackerCalibrationProgressResponse(
				fbb,
				VideoTrackerCalibrationStatus.CALIBRATE_SKELETON_OFFSETS,
				cameraOffset,
				trackersDoneOffset,
				trackersToResetOffset,
				0,
			)
		val messageOffset = createRPCMessage(fbb, RpcMessage.VideoTrackerCalibrationProgressResponse, progressOffset)
		fbb.finish(messageOffset)
		websocket.send(fbb.dataBuffer())

		val alignedFrames = alignFrames(snapshotsDatabase, trackerResets, trackerToHumanPoseDelays)

		// TODO: Move to its own step?
		debugOutput.saveReconstruction(
			camera.camera,
			alignedFrames,
			trackerResets,
		)

		snapshotsDatabase.clearRecent()

		step.set(Step.SOLVING_SKELETON_OFFSETS)
	}

	private suspend fun solveSkeletonOffsets() {
		LogManager.info("Solving skeleton offsets...")

		val camera = camera ?: error("Missing camera")

		snapshotsDatabase.update()
		val frames = snapshotsDatabase.matchAll(camera.cameraDelay)

		val initialSkeletonOffsets = skeletonConfigManager.configOffsets.toMap()
		val solution =
			skeletonOffsetsSolver.solve(
				frames,
				camera.camera,
				initialSkeletonOffsets,
				trackerResets,
			)

		if (solution == null) {
			return
		}

		val alignedFrames = alignFrames(snapshotsDatabase, trackerResets, trackerToHumanPoseDelays)

		debugOutput.saveSkeletonOffsets(
			camera.camera,
			alignedFrames,
			initialSkeletonOffsets,
			solution.skeletonOffsets,
		)

		skeletonConfigManager.setOffsets(solution.skeletonOffsets)

		// Let the user see the result
		// TODO: Should continue sending webcam as long as connected
		delay(10.seconds)

		val fbb = FlatBufferBuilder(512)
		val cameraOffset = addCamera(fbb, camera.camera)
		val trackersDoneOffset = addTrackersReset(fbb)
		val trackersToResetOffset = addRemainingTrackersToReset(fbb)
		val progressOffset =
			VideoTrackerCalibrationProgressResponse.createVideoTrackerCalibrationProgressResponse(
				fbb,
				VideoTrackerCalibrationStatus.DONE,
				cameraOffset,
				trackersDoneOffset,
				trackersToResetOffset,
				0,
			)
		val messageOffset = createRPCMessage(fbb, RpcMessage.VideoTrackerCalibrationProgressResponse, progressOffset)
		fbb.finish(messageOffset)
		websocket.send(fbb.dataBuffer())

		step.set(Step.DONE)
	}

	private fun createRPCMessage(fbb: FlatBufferBuilder, messageType: Byte, messageOffset: Int, respondTo: RpcMessageHeader? = null): Int {
		val data = IntArray(1)

		RpcMessageHeader.startRpcMessageHeader(fbb)
		RpcMessageHeader.addMessage(fbb, messageOffset)
		RpcMessageHeader.addMessageType(fbb, messageType)
		respondTo?.txId()?.let { txId ->
			RpcMessageHeader.addTxId(fbb, TransactionId.createTransactionId(fbb, txId.id()))
		}
		data[0] = RpcMessageHeader.endRpcMessageHeader(fbb)

		val messages = MessageBundle.createRpcMsgsVector(fbb, data)

		MessageBundle.startMessageBundle(fbb)
		MessageBundle.addRpcMsgs(fbb, messages)
		return MessageBundle.endMessageBundle(fbb)
	}

	private fun addCamera(fbb: FlatBufferBuilder, camera: Camera): Int {
		val (extrinsic, intrinsic, imageSize) = camera

		VideoTrackerCalibrationCamera.startVideoTrackerCalibrationCamera(fbb)

		val worldToCameraOffset = extrinsic.worldToCamera.toFloat().let { Quat.createQuat(fbb, it.x, it.y, it.z, it.w) }
		VideoTrackerCalibrationCamera.addWorldToCamera(fbb, worldToCameraOffset)

		val worldOriginInCamera = extrinsic.worldOriginInCamera.toFloat().let { Vec3f.createVec3f(fbb, it.x, it.y, it.z) }
		VideoTrackerCalibrationCamera.addWorldOriginInCamera(fbb, worldOriginInCamera)

		VideoTrackerCalibrationCamera.addFx(fbb, intrinsic.fx.toFloat())
		VideoTrackerCalibrationCamera.addFy(fbb, intrinsic.fy.toFloat())
		VideoTrackerCalibrationCamera.addTx(fbb, intrinsic.tx.toFloat())
		VideoTrackerCalibrationCamera.addTy(fbb, intrinsic.ty.toFloat())

		VideoTrackerCalibrationCamera.addWidth(fbb, imageSize.width)
		VideoTrackerCalibrationCamera.addHeight(fbb, imageSize.height)

		return VideoTrackerCalibrationCamera.endVideoTrackerCalibrationCamera(fbb)
	}

	private fun addTrackersReset(fbb: FlatBufferBuilder): Int = VideoTrackerCalibrationProgressResponse.createTrackersPendingVector(
		fbb,
		trackerResets.keys.map { it.bodyPart.toByte() }.toByteArray(),
	)

	private fun addRemainingTrackersToReset(fbb: FlatBufferBuilder): Int {
		val remaining = trackersToReset.keys.subtract(trackerResets.keys)
		return VideoTrackerCalibrationProgressResponse.createTrackersPendingVector(
			fbb,
			remaining.map { it.bodyPart.toByte() }.toByteArray(),
		)
	}

	private fun alignFrames(
		snapshotsDatabase: SnapshotsDatabase,
		trackerResets: Map<TrackerPosition, TrackerResetOverride>,
		trackerToHumanPoseDelays: Map<TrackerPosition, Duration>,
	): List<Pair<HumanPoseSnapshot, Map<TrackerPosition, TrackerSnapshot>>> {
		val trackerFrames: Map<TrackerPosition, Map<HumanPoseSnapshot, TrackerSnapshot>> =
			trackerToHumanPoseDelays.map { (trackerPosition, delay) ->
				val frames = snapshotsDatabase.matchAll(delay)
				trackerPosition to
					frames.mapNotNull {
						val trackerSnapshot = it.first.trackers[trackerPosition]
						if (trackerSnapshot != null) {
							it.second to trackerSnapshot
						} else {
							null
						}
					}.toMap()
			}.toMap()

		val humanPoseToTrackers: List<Pair<HumanPoseSnapshot, Map<TrackerPosition, TrackerSnapshot>>> =
			snapshotsDatabase.allHumanPoseSnapshots.map { humanPoseSnapshot ->
				humanPoseSnapshot to
					trackerToHumanPoseDelays.keys.mapNotNull { trackerPosition ->
						val hp = trackerFrames[trackerPosition]
						if (hp != null) {
							val ts = hp[humanPoseSnapshot]
							if (ts != null) {
								val tr = trackerResets[trackerPosition]
								trackerPosition to
									if (tr != null) {
										TrackerSnapshot(
											ts.rawTrackerToWorld,
											tr.toBoneRotation(ts.rawTrackerToWorld),
											ts.trackerOriginInWorld,
										)
									} else {
										ts
									}
							} else {
								null
							}
						} else {
							null
						}
					}.toMap()
			}

		return humanPoseToTrackers
	}

	companion object {

		private val REQUIRED_TRACKERS_TO_RESET = listOf(
			TrackerPosition.CHEST,
			TrackerPosition.LEFT_UPPER_LEG,
			TrackerPosition.RIGHT_UPPER_LEG,
			TrackerPosition.LEFT_LOWER_LEG,
			TrackerPosition.RIGHT_LOWER_LEG,
		)

		private val SUPPORTED_TRACKERS_TO_RESET = setOf(
			TrackerPosition.UPPER_CHEST,
			TrackerPosition.CHEST,
			TrackerPosition.WAIST,
			TrackerPosition.HIP,
			TrackerPosition.LEFT_UPPER_LEG,
			TrackerPosition.LEFT_LOWER_LEG,
			TrackerPosition.RIGHT_UPPER_LEG,
			TrackerPosition.RIGHT_LOWER_LEG,
			TrackerPosition.LEFT_UPPER_ARM,
			TrackerPosition.RIGHT_UPPER_ARM,
		)

		private val UPPER_BODY_TRACKERS_TO_RESET = setOf(
			TrackerPosition.UPPER_CHEST,
			TrackerPosition.CHEST,
			TrackerPosition.WAIST,
			TrackerPosition.HIP,
		)

		private val DELAY_BETWEEN_ATTEMPTS = 1.seconds
	}
}
