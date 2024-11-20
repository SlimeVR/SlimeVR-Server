package dev.slimevr.autobone

import dev.slimevr.VRServer
import dev.slimevr.autobone.AutoBone.AutoBoneResults
import dev.slimevr.autobone.AutoBone.Companion.loadDir
import dev.slimevr.autobone.errors.AutoBoneException
import dev.slimevr.poseframeformat.PoseFrames
import dev.slimevr.poseframeformat.PoseRecorder
import dev.slimevr.poseframeformat.PoseRecorder.RecordingProgress
import dev.slimevr.poseframeformat.trackerdata.TrackerFrameData
import dev.slimevr.poseframeformat.trackerdata.TrackerFrames
import dev.slimevr.tracking.processor.config.SkeletonConfigManager
import dev.slimevr.tracking.processor.config.SkeletonConfigOffsets
import io.eiren.util.StringUtils
import io.eiren.util.collections.FastList
import io.eiren.util.logging.LogManager
import org.apache.commons.lang3.tuple.Pair
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread
import kotlin.concurrent.withLock

class AutoBoneHandler(private val server: VRServer) {
	private val poseRecorder: PoseRecorder = PoseRecorder(server)
	private val autoBone: AutoBone = AutoBone(server)

	private val recordingLock = ReentrantLock()
	private var recordingThread: Thread? = null
	private val saveRecordingLock = ReentrantLock()
	private var saveRecordingThread: Thread? = null
	private val autoBoneLock = ReentrantLock()
	private var autoBoneThread: Thread? = null

	private val listeners = CopyOnWriteArrayList<AutoBoneListener>()

	fun addListener(listener: AutoBoneListener) {
		listeners.add(listener)
	}

	fun removeListener(listener: AutoBoneListener) {
		listeners.removeIf { listener == it }
	}

	private fun announceProcessStatus(
		processType: AutoBoneProcessType,
		message: String? = null,
		current: Long = -1L,
		total: Long = -1L,
		eta: Float = -1f,
		completed: Boolean = false,
		success: Boolean = true,
	) {
		listeners.forEach {
			it.onAutoBoneProcessStatus(
				processType,
				message,
				current,
				total,
				eta,
				completed,
				success,
			)
		}
	}

	@Throws(AutoBoneException::class)
	private fun processFrames(frames: PoseFrames): AutoBoneResults = autoBone
		.processFrames(frames) { epoch ->
			listeners.forEach { listener -> listener.onAutoBoneEpoch(epoch) }
		}

	fun startProcessByType(processType: AutoBoneProcessType?): Boolean {
		when (processType) {
			AutoBoneProcessType.RECORD -> startRecording()

			AutoBoneProcessType.SAVE -> saveRecording()

			AutoBoneProcessType.PROCESS -> processRecording()

			else -> {
				return false
			}
		}
		return true
	}

	fun startRecording() {
		recordingLock.withLock {
			// Prevent running multiple times
			if (recordingThread != null) {
				return
			}
			recordingThread = thread(start = true) { startRecordingThread() }
		}
	}

	private fun startRecordingThread() {
		try {
			if (poseRecorder.isReadyToRecord) {
				announceProcessStatus(AutoBoneProcessType.RECORD, "Recording...")

				// ex. 1000 samples at 20 ms per sample is 20 seconds
				val sampleCount = autoBone.globalConfig.sampleCount
				val sampleRate = autoBone.globalConfig.sampleRateMs
				// Calculate total time in seconds
				val totalTime: Float = (sampleCount * sampleRate) / 1000f

				val framesFuture = poseRecorder
					.startFrameRecording(
						sampleCount,
						sampleRate,
					) { progress: RecordingProgress ->
						announceProcessStatus(
							AutoBoneProcessType.RECORD,
							current = progress.frame.toLong(),
							total = progress.totalFrames.toLong(),
							eta = totalTime - (progress.frame * totalTime / progress.totalFrames),
						)
					}
				val frames = framesFuture.get()
				LogManager.info("[AutoBone] Done recording!")

				// Save a recurring recording for users to send as debug info
				announceProcessStatus(AutoBoneProcessType.RECORD, "Saving recording...")
				autoBone.saveRecording(frames, "LastABRecording.pfr")
				if (autoBone.globalConfig.saveRecordings) {
					announceProcessStatus(
						AutoBoneProcessType.RECORD,
						"Saving recording (from config option)...",
					)
					autoBone.saveRecording(frames)
				}
				listeners.forEach { listener: AutoBoneListener -> listener.onAutoBoneRecordingEnd(frames) }
				announceProcessStatus(
					AutoBoneProcessType.RECORD,
					"Done recording!",
					completed = true,
					success = true,
				)
			} else {
				announceProcessStatus(
					AutoBoneProcessType.RECORD,
					"The server is not ready to record",
					completed = true,
					success = false,
				)
				LogManager.severe("[AutoBone] Unable to record...")
				return
			}
		} catch (e: Exception) {
			announceProcessStatus(
				AutoBoneProcessType.RECORD,
				"Recording failed: ${e.message}",
				completed = true,
				success = false,
			)
			LogManager.severe("[AutoBone] Failed recording!", e)
		} finally {
			recordingThread = null
		}
	}

	fun stopRecording() {
		if (poseRecorder.isRecording) {
			poseRecorder.stopFrameRecording()
		}
	}

	fun cancelRecording() {
		if (poseRecorder.isRecording) {
			poseRecorder.cancelFrameRecording()
		}
	}

	fun saveRecording() {
		saveRecordingLock.withLock {
			// Prevent running multiple times
			if (saveRecordingThread != null) {
				return
			}
			saveRecordingThread = thread(start = true) { saveRecordingThread() }
		}
	}

	private fun saveRecordingThread() {
		try {
			val framesFuture = poseRecorder.framesAsync
			if (framesFuture != null) {
				announceProcessStatus(AutoBoneProcessType.SAVE, "Waiting for recording...")
				val frames = framesFuture.get()
				check(frames.frameHolders.isNotEmpty()) { "Recording has no trackers." }
				check(frames.maxFrameCount > 0) { "Recording has no frames." }
				announceProcessStatus(AutoBoneProcessType.SAVE, "Saving recording...")
				autoBone.saveRecording(frames)
				announceProcessStatus(
					AutoBoneProcessType.SAVE,
					"Recording saved!",
					completed = true,
					success = true,
				)
			} else {
				announceProcessStatus(
					AutoBoneProcessType.SAVE,
					"No recording found",
					completed = true,
					success = false,
				)
				LogManager.severe("[AutoBone] Unable to save, no recording was done...")
				return
			}
		} catch (e: Exception) {
			announceProcessStatus(
				AutoBoneProcessType.SAVE,
				"Failed to save recording: ${e.message}",
				completed = true,
				success = false,
			)
			LogManager.severe("[AutoBone] Failed to save recording!", e)
		} finally {
			saveRecordingThread = null
		}
	}

	fun processRecording() {
		autoBoneLock.withLock {
			// Prevent running multiple times
			if (autoBoneThread != null) {
				return
			}
			autoBoneThread = thread(start = true) { processRecordingThread() }
		}
	}

	private fun processRecordingThread() {
		try {
			announceProcessStatus(AutoBoneProcessType.PROCESS, "Loading recordings...")
			val frameRecordings = autoBone.loadRecordings()
			if (!frameRecordings.isEmpty()) {
				LogManager.info("[AutoBone] Done loading frames!")
			} else {
				val framesFuture = poseRecorder.framesAsync
				if (framesFuture != null) {
					announceProcessStatus(AutoBoneProcessType.PROCESS, "Waiting for recording...")
					val frames = framesFuture.get()
					frameRecordings.add(Pair.of("<Recording>", frames))
				} else {
					announceProcessStatus(
						AutoBoneProcessType.PROCESS,
						"No recordings found...",
						completed = true,
						success = false,
					)
					LogManager
						.severe(
							"[AutoBone] No recordings found in \"${loadDir.path}\" and no recording was done...",
						)
					return
				}
			}
			announceProcessStatus(AutoBoneProcessType.PROCESS, "Processing recording(s)...")
			LogManager.info("[AutoBone] Processing frames...")
			val errorStats = StatsCalculator()
			val offsetStats = EnumMap<SkeletonConfigOffsets, StatsCalculator>(
				SkeletonConfigOffsets::class.java,
			)
			val skeletonConfigManagerBuffer = SkeletonConfigManager(false)
			for ((key, value) in frameRecordings) {
				LogManager.info("[AutoBone] Processing frames from \"$key\"...")
				// Output tracker info for the recording
				printTrackerInfo(value.frameHolders)

				// Actually process the recording
				val autoBoneResults = processFrames(value)
				LogManager.info("[AutoBone] Done processing!")

				// #region Stats/Values
				// Accumulate height error
				errorStats.addValue(autoBoneResults.heightDifference)

				// Accumulate length values
				for (offset in autoBoneResults.configValues) {
					val statCalc = offsetStats.getOrPut(offset.key) {
						StatsCalculator()
					}
					// Multiply by 100 to get cm
					statCalc.addValue(offset.value * 100f)
				}

				// Calculate and output skeleton ratios
				skeletonConfigManagerBuffer.setOffsets(autoBoneResults.configValues)
				printSkeletonRatios(skeletonConfigManagerBuffer)

				LogManager.info("[AutoBone] Length values: ${autoBone.lengthsString}")
			}
			// Length value stats
			val averageLengthVals = StringBuilder()
			offsetStats.forEach { (key, value) ->
				if (averageLengthVals.isNotEmpty()) {
					averageLengthVals.append(", ")
				}
				averageLengthVals
					.append(key.configKey)
					.append(": ")
					.append(StringUtils.prettyNumber(value.mean, 2))
					.append(" (SD ")
					.append(StringUtils.prettyNumber(value.standardDeviation, 2))
					.append(")")
			}
			LogManager.info("[AutoBone] Average length values: $averageLengthVals")

			// Height error stats
			LogManager
				.info(
					"[AutoBone] Average height error: ${
						StringUtils.prettyNumber(errorStats.mean, 6)
					} (SD ${StringUtils.prettyNumber(errorStats.standardDeviation, 6)})",
				)
			// #endregion
			listeners.forEach { listener: AutoBoneListener -> listener.onAutoBoneEnd(autoBone.offsets) }
			announceProcessStatus(
				AutoBoneProcessType.PROCESS,
				"Done processing!",
				completed = true,
				success = true,
			)
		} catch (e: Exception) {
			announceProcessStatus(
				AutoBoneProcessType.PROCESS,
				"Processing failed: ${e.message}",
				completed = true,
				success = false,
			)
			LogManager.severe("[AutoBone] Failed adjustment!", e)
		} finally {
			autoBoneThread = null
		}
	}

	private fun printTrackerInfo(trackers: FastList<TrackerFrames>) {
		val trackerInfo = StringBuilder()
		for (tracker in trackers) {
			val frame = tracker?.tryGetFrame(0) ?: continue

			// Add a comma if this is not the first item listed
			if (trackerInfo.isNotEmpty()) {
				trackerInfo.append(", ")
			}

			trackerInfo.append(frame.tryGetTrackerPosition()?.designation ?: "unassigned")

			// Represent the data flags
			val trackerFlags = StringBuilder()
			if (frame.hasData(TrackerFrameData.ROTATION)) {
				trackerFlags.append("R")
			}
			if (frame.hasData(TrackerFrameData.POSITION)) {
				trackerFlags.append("P")
			}
			if (frame.hasData(TrackerFrameData.ACCELERATION)) {
				trackerFlags.append("A")
			}
			if (frame.hasData(TrackerFrameData.RAW_ROTATION)) {
				trackerFlags.append("r")
			}

			// If there are data flags, print them in brackets after the designation
			if (trackerFlags.isNotEmpty()) {
				trackerInfo.append(" (").append(trackerFlags).append(")")
			}
		}
		LogManager.info("[AutoBone] (${trackers.size} trackers) [$trackerInfo]")
	}

	private fun printSkeletonRatios(skeleton: SkeletonConfigManager) {
		val neckLength = skeleton.getOffset(SkeletonConfigOffsets.NECK)
		val upperChestLength = skeleton.getOffset(SkeletonConfigOffsets.UPPER_CHEST)
		val chestLength = skeleton.getOffset(SkeletonConfigOffsets.CHEST)
		val waistLength = skeleton.getOffset(SkeletonConfigOffsets.WAIST)
		val hipLength = skeleton.getOffset(SkeletonConfigOffsets.HIP)
		val torsoLength = upperChestLength + chestLength + waistLength + hipLength
		val hipWidth = skeleton.getOffset(SkeletonConfigOffsets.HIPS_WIDTH)
		val legLength = skeleton.getOffset(SkeletonConfigOffsets.UPPER_LEG) +
			skeleton.getOffset(SkeletonConfigOffsets.LOWER_LEG)
		val lowerLegLength = skeleton.getOffset(SkeletonConfigOffsets.LOWER_LEG)

		val neckTorso = neckLength / torsoLength
		val chestTorso = (upperChestLength + chestLength) / torsoLength
		val torsoWaist = hipWidth / torsoLength
		val legTorso = legLength / torsoLength
		val legBody = legLength / (torsoLength + neckLength)
		val kneeLeg = lowerLegLength / legLength

		LogManager.info(
			"[AutoBone] Ratios: [{Neck-Torso: ${
				StringUtils.prettyNumber(neckTorso)}}, {Chest-Torso: ${
				StringUtils.prettyNumber(chestTorso)}}, {Torso-Waist: ${
				StringUtils.prettyNumber(torsoWaist)}}, {Leg-Torso: ${
				StringUtils.prettyNumber(legTorso)}}, {Leg-Body: ${
				StringUtils.prettyNumber(legBody)}}, {Knee-Leg: ${
				StringUtils.prettyNumber(kneeLeg)}}]",
		)
	}

	fun applyValues() {
		autoBone.applyAndSaveConfig()
	}
}
