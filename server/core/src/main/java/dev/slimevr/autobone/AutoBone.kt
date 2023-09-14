package dev.slimevr.autobone

import com.jme3.math.FastMath
import dev.slimevr.SLIMEVR_IDENTIFIER
import dev.slimevr.VRServer
import dev.slimevr.autobone.errors.*
import dev.slimevr.config.AutoBoneConfig
import dev.slimevr.poseframeformat.PoseFrameIO
import dev.slimevr.poseframeformat.PoseFrames
import dev.slimevr.tracking.processor.HumanPoseManager
import dev.slimevr.tracking.processor.config.SkeletonConfigManager
import dev.slimevr.tracking.processor.config.SkeletonConfigOffsets
import dev.slimevr.tracking.trackers.TrackerRole
import io.eiren.util.OperatingSystem
import io.eiren.util.StringUtils
import io.eiren.util.collections.FastList
import io.eiren.util.logging.LogManager
import io.github.axisangles.ktmath.Vector3
import org.apache.commons.lang3.tuple.Pair
import java.io.File
import java.util.*
import java.util.function.Consumer
import java.util.function.Function

class AutoBone(server: VRServer) {
	// This is filled by loadConfigValues()
	val offsets = EnumMap<SkeletonConfigOffsets, Float>(
		SkeletonConfigOffsets::class.java
	)
	val adjustOffsets = FastList(
		arrayOf(
			SkeletonConfigOffsets.HEAD,
			SkeletonConfigOffsets.NECK,
			SkeletonConfigOffsets.UPPER_CHEST,
			SkeletonConfigOffsets.CHEST,
			SkeletonConfigOffsets.WAIST,
			SkeletonConfigOffsets.HIP,
			// HIPS_WIDTH now works when using body proportion error! It's not the
			// best still, but it is somewhat functional
			SkeletonConfigOffsets.HIPS_WIDTH,
			SkeletonConfigOffsets.UPPER_LEG,
			SkeletonConfigOffsets.LOWER_LEG
		)
	)
	val heightOffsetDefaults = EnumMap<SkeletonConfigOffsets, Float>(
		SkeletonConfigOffsets::class.java
	)

	// This is filled by loadConfigValues()
	val heightOffsets = FastList(
		arrayOf(
			SkeletonConfigOffsets.NECK,
			SkeletonConfigOffsets.UPPER_CHEST,
			SkeletonConfigOffsets.CHEST,
			SkeletonConfigOffsets.WAIST,
			SkeletonConfigOffsets.HIP,
			SkeletonConfigOffsets.UPPER_LEG,
			SkeletonConfigOffsets.LOWER_LEG
		)
	)

	var estimatedHeight: Float = 1f

	private val server: VRServer

	// #region Error functions
	var slideError = SlideError()
	var offsetSlideError = OffsetSlideError()
	var footHeightOffsetError = FootHeightOffsetError()
	var bodyProportionError = BodyProportionError()
	var heightError = HeightError()
	var positionError = PositionError()
	var positionOffsetError = PositionOffsetError()
	// #endregion

	private val rand = Random()

	val globalConfig: AutoBoneConfig

	init {
		globalConfig = server.configManager.vrConfig.autoBone
		this.server = server
		loadConfigValues()
	}

	private fun loadConfigValues() {
		// Remove all previous values
		offsets.clear()

		// Get current or default skeleton configs
		val skeleton = server.humanPoseManager
		val getOffset: Function<SkeletonConfigOffsets, Float> =
			if (skeleton != null) {
				Function { key: SkeletonConfigOffsets -> skeleton.getOffset(key) }
			} else {
				val defaultConfig = SkeletonConfigManager(false)
				Function { config: SkeletonConfigOffsets ->
					defaultConfig.getOffset(config)
				}
			}
		for (bone in adjustOffsets) {
			val offset = getOffset.apply(bone)
			if (offset > 0f) {
				offsets[bone] = offset
			}
		}
		for (bone in heightOffsets) {
			val offset = getOffset.apply(bone)
			if (offset > 0f) {
				heightOffsetDefaults[bone] = offset
			}
		}
	}

	fun getBoneDirection(
		skeleton: HumanPoseManager,
		configOffset: SkeletonConfigOffsets,
		rightSide: Boolean,
	): Vector3 {
		// IMPORTANT: This assumption for acquiring BoneType only works if
		// SkeletonConfigOffsets is set up to only affect one BoneType, make sure no
		// changes to SkeletonConfigOffsets goes against this assumption, please!
		val boneType = when (configOffset) {
			SkeletonConfigOffsets.HIPS_WIDTH, SkeletonConfigOffsets.SHOULDERS_WIDTH,
			SkeletonConfigOffsets.SHOULDERS_DISTANCE, SkeletonConfigOffsets.UPPER_ARM,
			SkeletonConfigOffsets.LOWER_ARM, SkeletonConfigOffsets.UPPER_LEG,
			SkeletonConfigOffsets.LOWER_LEG, SkeletonConfigOffsets.FOOT_LENGTH,
			->
				if (rightSide) configOffset.affectedOffsets[1] else configOffset.affectedOffsets[0]
			else -> configOffset.affectedOffsets[0]
		}
		return skeleton.getBone(boneType).getGlobalRotation().toRotationVector()
	}

	fun getDotProductDiff(
		skeleton1: HumanPoseManager,
		skeleton2: HumanPoseManager,
		configOffset: SkeletonConfigOffsets,
		rightSide: Boolean,
		offset: Vector3,
	): Float {
		val normalizedOffset = offset.unit()
		val dot1 = normalizedOffset.dot(getBoneDirection(skeleton1, configOffset, rightSide))
		val dot2 = normalizedOffset.dot(getBoneDirection(skeleton2, configOffset, rightSide))
		return dot2 - dot1
	}

	fun applyConfig(
		humanPoseManager: HumanPoseManager,
		offsets: Map<SkeletonConfigOffsets, Float> = this.offsets,
	) {
		for ((offset, value) in offsets) {
			humanPoseManager.setOffset(offset, value)
		}
	}

	@JvmOverloads
	fun applyAndSaveConfig(humanPoseManager: HumanPoseManager? = this.server.humanPoseManager): Boolean {
		if (humanPoseManager == null) return false
		applyConfig(humanPoseManager)
		humanPoseManager.saveConfig()
		server.configManager.saveConfig()
		LogManager.info("[AutoBone] Configured skeleton bone lengths")
		return true
	}

	fun <T> sumSelectConfigs(
		selection: List<T>,
		configs: Map<T, Float>,
		configsAlt: Map<T, Float>? = null,
	): Float {
		var sum = 0f
		for (config in selection) {
			val length = configs[config] ?: configsAlt?.get(config)
			if (length != null) {
				sum += length
			}
		}
		return sum
	}

	fun calcHeight(): Float {
		return sumSelectConfigs(heightOffsets, offsets, heightOffsetDefaults)
	}

	fun getLengthSum(configs: Map<SkeletonConfigOffsets, Float>): Float {
		return getLengthSum(configs, null)
	}

	fun getLengthSum(
		configs: Map<SkeletonConfigOffsets, Float>,
		configsAlt: Map<SkeletonConfigOffsets, Float>?,
	): Float {
		var length = 0f
		if (configsAlt != null) {
			for ((key, value) in configsAlt) {
				// If there isn't a duplicate config
				if (!configs.containsKey(key)) {
					length += value
				}
			}
		}
		for (boneLength in configs.values) {
			length += boneLength
		}
		return length
	}

	fun calcTargetHmdHeight(
		frames: PoseFrames,
		config: AutoBoneConfig = globalConfig,
	): Float {
		val targetHeight: Float
		// Get the current skeleton from the server
		val humanPoseManager = server.humanPoseManager
		if (config.useSkeletonHeight && humanPoseManager != null) {
			// If there is a skeleton available, calculate the target height
			// from its configs
			targetHeight = humanPoseManager.userHeightFromConfig
			LogManager
				.warning(
					"[AutoBone] Target height loaded from skeleton (Make sure you reset before running!): $targetHeight"
				)
		} else {
			// Otherwise if there is no skeleton available, attempt to get the
			// max HMD height from the recording
			val hmdHeight = frames.maxHmdHeight
			if (hmdHeight <= 0.4f) {
				LogManager
					.warning(
						"[AutoBone] Max headset height detected (Value seems too low, did you not stand up straight while measuring?): $hmdHeight"
					)
			} else {
				LogManager.info("[AutoBone] Max headset height detected: $hmdHeight")
			}

			// Estimate target height from HMD height
			targetHeight = hmdHeight
		}
		return targetHeight
	}

	@Throws(AutoBoneException::class)
	fun processFrames(
		frames: PoseFrames,
		config: AutoBoneConfig = globalConfig,
		epochCallback: Consumer<Epoch>? = null,
	): AutoBoneResults {
		// Load current values for adjustable configs
		loadConfigValues()

		// Set the target heights either from config or calculate them
		val targetHmdHeight = if (config.targetHmdHeight > 0f) {
			config.targetHmdHeight
		} else {
			calcTargetHmdHeight(frames, config)
		}
		val targetFullHeight = if (config.targetFullHeight > 0f) {
			config.targetFullHeight
		} else {
			targetHmdHeight * BodyProportionError.eyeHeightToHeightRatio
		}

		// Set up the current state, making all required players and setting up the
		// skeletons appropriately
		val trainingStep = AutoBoneStep(
			config = config,
			targetHmdHeight = targetHmdHeight,
			targetFullHeight = targetFullHeight,
			frames = frames,
			epochCallback = epochCallback,
			serverConfig = server.configManager
		)

		estimatedHeight = targetHmdHeight

		// Initialize the frame order randomizer with a repeatable seed
		rand.setSeed(config.randSeed)

		// Epoch loop, each epoch is one full iteration over the full dataset
		for (epoch in (if (config.calcInitError) -1 else 0) until config.numEpochs) {
			// Set the current epoch to process
			trainingStep.curEpoch = epoch

			// Process the epoch
			internalEpoch(trainingStep)
		}

		for (entry in offsets.entries) {
			entry.setValue((entry.value * estimatedHeight) / trainingStep.skeleton1.userHeightFromConfig)
		}

		val finalHeight = calcHeight()
		LogManager
			.info(
				"[AutoBone] Target height: ${trainingStep.targetHmdHeight}, New height: $finalHeight"
			)

		return AutoBoneResults(
			finalHeight,
			trainingStep.targetHmdHeight,
			trainingStep.errorStats,
			offsets
		)
	}

	private fun internalEpoch(trainingStep: AutoBoneStep) {
		// Pull frequently used variables out of trainingStep to reduce call length
		val config = trainingStep.config
		val frameCount = trainingStep.maxFrameCount
		val errorStats = trainingStep.errorStats
		val epoch = trainingStep.curEpoch

		// Set the current adjust rate based on the current epoch
		trainingStep.curAdjustRate = decayFunc(config.initialAdjustRate, config.adjustRateDecay, epoch)

		var randomFrameIndices: IntArray? = null
		if (config.randomizeFrameOrder) {
			randomFrameIndices = IntArray(frameCount)
			var zeroPos = -1
			for (i in 0 until frameCount) {
				var index = rand.nextInt(frameCount)
				if (i > 0) {
					while (index == zeroPos || randomFrameIndices[index] > 0) {
						index = rand.nextInt(frameCount)
					}
				} else {
					zeroPos = index
				}
				randomFrameIndices[index] = i
			}
		}

		// Iterate over the frames using a cursor and an offset for comparing
		// frames a certain number of frames apart
		var cursorOffset = config.minDataDistance
		while (cursorOffset <= config.maxDataDistance &&
			cursorOffset < frameCount
		) {
			var frameCursor = 0
			while (frameCursor < frameCount - cursorOffset) {
				val frameCursor2 = frameCursor + cursorOffset

				// Apply the current adjusted config to both skeletons
				applyConfig(trainingStep.skeleton1)
				applyConfig(trainingStep.skeleton2)

				// Scale to 1 meter before starting the first iteration, as it's scaled after otherwise
				val height = trainingStep.skeleton1.userHeightFromConfig
				for (entry in offsets.entries) {
					entry.setValue(entry.value / height)
				}

				// Then set the frame cursors and apply them to both skeletons
				if (config.randomizeFrameOrder && randomFrameIndices != null) {
					trainingStep
						.setCursors(
							randomFrameIndices[frameCursor],
							randomFrameIndices[frameCursor2],
							updatePlayerCursors = true
						)
				} else {
					trainingStep.setCursors(
						frameCursor,
						frameCursor2,
						updatePlayerCursors = true
					)
				}

				// Process the iteration
				internalIter(trainingStep)

				// Move on to the next iteration
				frameCursor += config.cursorIncrement
			}
			cursorOffset++
		}

		// Calculate average error over the epoch
		if (epoch <= 0 || epoch >= config.numEpochs - 1 || (epoch + 1) % config.printEveryNumEpochs == 0) {
			LogManager
				.info(
					"[AutoBone] Epoch: ${epoch + 1}, Mean error: ${errorStats.mean} (SD ${errorStats.standardDeviation}), Adjust rate: ${trainingStep.curAdjustRate}"
				)
			LogManager
				.info(
					"[AutoBone] Estimated height: $estimatedHeight, Target height: ${trainingStep.targetHmdHeight}"
				)
		}

		val scaledOffsets = EnumMap(offsets)
		for (entry in scaledOffsets.entries) {
			entry.setValue((entry.value * estimatedHeight) / trainingStep.skeleton1.userHeightFromConfig)
		}
		trainingStep.epochCallback?.accept(Epoch(epoch + 1, config.numEpochs, errorStats, scaledOffsets))
	}

	private fun internalIter(trainingStep: AutoBoneStep) {
		// Pull frequently used variables out of trainingStep to reduce call length
		val skeleton1 = trainingStep.skeleton1
		val skeleton2 = trainingStep.skeleton2

		val totalLength = getLengthSum(offsets)
		val curHeight = calcHeight()
		trainingStep.currentHmdHeight = curHeight

		// Try to estimate a new height
		val maxHeight = trainingStep.targetHmdHeight + 0.2f
		val minHeight = trainingStep.targetHmdHeight - 0.2f

		val heightErrorDeriv = getErrorDeriv(trainingStep)
		val heightError = errorFunc(heightErrorDeriv)
		val heightAdjust = heightError * trainingStep.curAdjustRate

		val negHeight = (estimatedHeight - heightAdjust).coerceIn(minHeight, maxHeight)
		val negScale = 1f / negHeight
		trainingStep.framePlayer1.setScales(negScale)
		trainingStep.framePlayer2.setScales(negScale)
		skeleton1.update()
		skeleton2.update()
		val negHeightErrorDeriv = getErrorDeriv(trainingStep)

		val posHeight = (estimatedHeight + heightAdjust).coerceIn(minHeight, maxHeight)
		val posScale = 1f / posHeight
		trainingStep.framePlayer1.setScales(posScale)
		trainingStep.framePlayer2.setScales(posScale)
		skeleton1.update()
		skeleton2.update()
		val posHeightErrorDeriv = getErrorDeriv(trainingStep)

		if (negHeightErrorDeriv < heightErrorDeriv && negHeightErrorDeriv < posHeightErrorDeriv) {
			estimatedHeight = negHeight

			// Reset head position to estimated height
			val initScale = 1f / estimatedHeight
			trainingStep.framePlayer1.setScales(initScale)
			trainingStep.framePlayer2.setScales(initScale)
			skeleton1.update()
			skeleton2.update()
		} else if (posHeightErrorDeriv < heightErrorDeriv) {
			estimatedHeight = posHeight
			// The last estimated height set was positive, so no need to reset
		} else {
			// Reset head position to estimated height
			val initScale = 1f / estimatedHeight
			trainingStep.framePlayer1.setScales(initScale)
			trainingStep.framePlayer2.setScales(initScale)
			skeleton1.update()
			skeleton2.update()
		}

		val errorDeriv = getErrorDeriv(trainingStep)
		val error = errorFunc(errorDeriv)

		// In case of fire
		if (java.lang.Float.isNaN(error) || java.lang.Float.isInfinite(error)) {
			// Extinguish
			LogManager
				.warning(
					"[AutoBone] Error value is invalid, resetting variables to recover"
				)
			// Reset adjustable config values
			loadConfigValues()

			// Reset error sum values
			trainingStep.errorStats.reset()

			// Continue on new data
			return
		}

		// Store the error count for logging purposes
		trainingStep.errorStats.addValue(errorDeriv)
		val adjustVal = error * trainingStep.curAdjustRate

		// If there is no adjustment whatsoever, skip this
		if (adjustVal == 0f) {
			return
		}

		val slideLeft = skeleton2
			.getComputedTracker(TrackerRole.LEFT_FOOT).position -
			skeleton1.getComputedTracker(TrackerRole.LEFT_FOOT).position

		val slideRight = skeleton2
			.getComputedTracker(TrackerRole.RIGHT_FOOT).position -
			skeleton1.getComputedTracker(TrackerRole.RIGHT_FOOT).position

		val intermediateOffsets = EnumMap(offsets)
		for (entry in intermediateOffsets.entries) {
			// Skip adjustment if the epoch is before starting (for
			// logging only) or if there are no BoneTypes for this value
			if (trainingStep.curEpoch < 0 || entry.key.affectedOffsets.isEmpty()) {
				break
			}
			val originalLength = entry.value

			val leftDotProduct = getDotProductDiff(
				skeleton1,
				skeleton2,
				entry.key,
				false,
				slideLeft
			)
			val rightDotProduct = getDotProductDiff(
				skeleton1,
				skeleton2,
				entry.key,
				true,
				slideRight
			)

			// Calculate the total effect of the bone based on change in rotation
			val dotLength = (
				originalLength
					* ((leftDotProduct + rightDotProduct) / 2f)
				)

			// Scale by the ratio for smooth adjustment and more
			// stable results
			val curAdjustVal = adjustVal * -dotLength / totalLength
			val newLength = originalLength + curAdjustVal

			// No small or negative numbers!!! Bad algorithm!
			if (newLength < 0.01f) {
				continue
			}

			// Apply new offset length
			skeleton1.setOffset(entry.key, newLength)
			skeleton2.setOffset(entry.key, newLength)
			scaleSkeleton(skeleton1)
			scaleSkeleton(skeleton2)

			// Update the skeleton poses for the new offset length
			skeleton1.update()
			skeleton2.update()

			val newErrorDeriv = getErrorDeriv(trainingStep)
			if (newErrorDeriv < errorDeriv) {
				// Apply the adjusted length to the current adjusted offsets
				entry.setValue(newLength)
			}

			// Reset the skeleton values to minimize bias in other variables,
			// it's applied later
			applyConfig(trainingStep.skeleton1)
			applyConfig(trainingStep.skeleton2)
		}

		// Update the offsets from the adjusted ones
		offsets.putAll(intermediateOffsets)
		applyConfig(trainingStep.skeleton1)
		applyConfig(trainingStep.skeleton2)

		// Normalize the scale, it will be upscaled to the target height later
		val height = trainingStep.skeleton1.userHeightFromConfig
		for (entry in offsets.entries) {
			entry.setValue(entry.value / height)
		}
	}

	private fun scaleToTargetHeight(trainingStep: AutoBoneStep) {
		// Recalculate the height and update it in the AutoBoneStep
		val stepHeight = calcHeight()
		trainingStep.currentHmdHeight = stepHeight

		if (stepHeight > 0f) {
			val stepHeightDiff = trainingStep.targetHmdHeight - stepHeight
			for (entry in offsets.entries) {
				// Only height variables
				if (entry.key == SkeletonConfigOffsets.NECK ||
					!heightOffsets.contains(entry.key)
				) {
					continue
				}
				val length = entry.value

				// Multiply the diff by the length to height
				// ratio
				val adjVal = stepHeightDiff * (length / stepHeight)

				// Scale the length to fit the target height
				entry.setValue(
					(length + adjVal / 2f).coerceAtLeast(
						0.01f
					)
				)
			}
		}
	}

	private fun scaleSkeleton(humanPoseManager: HumanPoseManager, targetHeight: Float = 1f) {
		val scale = targetHeight / humanPoseManager.userHeightFromConfig
		for (offset in SkeletonConfigOffsets.values) {
			humanPoseManager.setOffset(offset, humanPoseManager.getOffset(offset) * scale)
		}
	}

	@Throws(AutoBoneException::class)
	private fun getErrorDeriv(trainingStep: AutoBoneStep): Float {
		val config = trainingStep.config
		var sumError = 0f
		if (config.slideErrorFactor > 0f) {
			sumError += slideError.getStepError(trainingStep) * config.slideErrorFactor
		}
		if (config.offsetSlideErrorFactor > 0f) {
			sumError += (
				offsetSlideError.getStepError(trainingStep) *
					config.offsetSlideErrorFactor
				)
		}
		if (config.footHeightOffsetErrorFactor > 0f) {
			sumError += (
				footHeightOffsetError.getStepError(trainingStep) *
					config.footHeightOffsetErrorFactor
				)
		}
		if (config.bodyProportionErrorFactor > 0f) {
			sumError += (
				bodyProportionError.getStepError(trainingStep) *
					config.bodyProportionErrorFactor
				)
		}
		if (config.heightErrorFactor > 0f) {
			sumError += heightError.getStepError(trainingStep) * config.heightErrorFactor
		}
		if (config.positionErrorFactor > 0f) {
			sumError += (
				positionError.getStepError(trainingStep) *
					config.positionErrorFactor
				)
		}
		if (config.positionOffsetErrorFactor > 0f) {
			sumError += (
				positionOffsetError.getStepError(trainingStep) *
					config.positionOffsetErrorFactor
				)
		}
		return sumError
	}

	val lengthsString: String
		get() {
			val configInfo = StringBuilder()
			offsets.forEach { (key: SkeletonConfigOffsets, value: Float) ->
				if (configInfo.isNotEmpty()) {
					configInfo.append(", ")
				}
				configInfo
					.append(key.configKey)
					.append(": ")
					.append(StringUtils.prettyNumber(value * 100f, 2))
			}
			return configInfo.toString()
		}

	fun saveRecording(frames: PoseFrames, recordingFile: File) {
		if (saveDir.isDirectory || saveDir.mkdirs()) {
			LogManager
				.info("[AutoBone] Exporting frames to \"${recordingFile.path}\"...")
			if (PoseFrameIO.tryWriteToFile(recordingFile, frames)) {
				LogManager
					.info(
						"[AutoBone] Done exporting! Recording can be found at \"${recordingFile.path}\"."
					)
			} else {
				LogManager
					.severe(
						"[AutoBone] Failed to export the recording to \"${recordingFile.path}\"."
					)
			}
		} else {
			LogManager
				.severe(
					"[AutoBone] Failed to create the recording directory \"${saveDir.path}\"."
				)
		}
	}

	fun saveRecording(frames: PoseFrames, recordingFileName: String) {
		saveRecording(frames, File(saveDir, recordingFileName))
	}

	fun saveRecording(frames: PoseFrames) {
		var recordingFile: File
		var recordingIndex = 1
		do {
			recordingFile = File(saveDir, "ABRecording${recordingIndex++}.pfr")
		} while (recordingFile.exists())
		saveRecording(frames, recordingFile)
	}

	fun loadRecordings(): FastList<Pair<String, PoseFrames>> {
		val recordings = FastList<Pair<String, PoseFrames>>()
		if (loadDir.isDirectory) {
			val files = loadDir.listFiles()
			if (files != null) {
				for (file in files) {
					if (file.isFile &&
						org.apache.commons.lang3.StringUtils
							.endsWithIgnoreCase(file.name, ".pfr")
					) {
						LogManager
							.info(
								"[AutoBone] Detected recording at \"${file.path}\", loading frames..."
							)
						val frames = PoseFrameIO.tryReadFromFile(file)
						if (frames == null) {
							LogManager
								.severe("Reading frames from \"${file.path}\" failed...")
						} else {
							recordings.add(Pair.of(file.name, frames))
						}
					}
				}
			}
		}
		return recordings
	}

	inner class Epoch(
		val epoch: Int,
		val totalEpochs: Int,
		val epochError: StatsCalculator,
		val configValues: EnumMap<SkeletonConfigOffsets, Float>,
	) {
		override fun toString(): String {
			return "Epoch: $epoch, Epoch error: $epochError"
		}
	}

	inner class AutoBoneResults(
		val finalHeight: Float,
		val targetHeight: Float,
		val epochError: StatsCalculator,
		val configValues: EnumMap<SkeletonConfigOffsets, Float>,
	) {
		val heightDifference: Float
			get() = FastMath.abs(targetHeight - finalHeight)
	}

	companion object {
		const val AUTOBONE_FOLDER = "AutoBone Recordings"
		const val LOADAUTOBONE_FOLDER = "Load AutoBone Recordings"

		// FIXME: Won't work on iOS and Android, maybe fix resolveConfigDirectory more than this
		val saveDir = File(
			OperatingSystem.resolveConfigDirectory(SLIMEVR_IDENTIFIER)?.resolve(
				AUTOBONE_FOLDER
			)?.toString() ?: AUTOBONE_FOLDER
		)
		val loadDir = File(
			OperatingSystem.resolveConfigDirectory(SLIMEVR_IDENTIFIER)?.resolve(
				LOADAUTOBONE_FOLDER
			)?.toString() ?: LOADAUTOBONE_FOLDER
		)

		// Mean square error function
		private fun errorFunc(errorDeriv: Float): Float {
			return 0.5f * (errorDeriv * errorDeriv)
		}

		private fun decayFunc(initialAdjustRate: Float, adjustRateDecay: Float, epoch: Int): Float {
			return if (epoch >= 0) initialAdjustRate / (1 + (adjustRateDecay * epoch)) else 0.0f
		}
	}
}
