package dev.slimevr.autobone

import com.jme3.math.FastMath
import dev.slimevr.VRServer
import dev.slimevr.autobone.errors.*
import dev.slimevr.config.AutoBoneConfig
import dev.slimevr.poseframeformat.PoseFrameIO
import dev.slimevr.poseframeformat.PoseFrames
import dev.slimevr.tracking.processor.BoneType
import dev.slimevr.tracking.processor.HumanPoseManager
import dev.slimevr.tracking.processor.config.SkeletonConfigManager
import dev.slimevr.tracking.processor.config.SkeletonConfigOffsets
import dev.slimevr.tracking.trackers.TrackerRole
import io.eiren.util.StringUtils
import io.eiren.util.collections.FastList
import io.eiren.util.logging.LogManager
import io.github.axisangles.ktmath.Vector3
import org.apache.commons.lang3.tuple.Pair
import java.io.File
import java.util.*
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Function

class AutoBone(server: VRServer) {
	// This is filled by reloadConfigValues()
	val offsets = EnumMap<BoneType, Float>(
		BoneType::class.java
	)
	val adjustOffsets = FastList(
		arrayOf(
			BoneType.HEAD,
			BoneType.NECK,
			BoneType.UPPER_CHEST,
			BoneType.CHEST,
			BoneType.WAIST,
			BoneType.HIP, // This now works when using body proportion error! It's not the
			// best still, but it is somewhat functional
			BoneType.LEFT_HIP,
			BoneType.LEFT_UPPER_LEG,
			BoneType.LEFT_LOWER_LEG
		)
	)
	val heightOffsets = FastList(
		arrayOf(
			BoneType.NECK,
			BoneType.UPPER_CHEST,
			BoneType.CHEST,
			BoneType.WAIST,
			BoneType.HIP,
			BoneType.LEFT_UPPER_LEG,
			BoneType.RIGHT_UPPER_LEG,
			BoneType.LEFT_LOWER_LEG,
			BoneType.RIGHT_LOWER_LEG
		)
	)

	val legacyConfigs = EnumMap<SkeletonConfigOffsets, Float>(
		SkeletonConfigOffsets::class.java
	)

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

	fun computeBoneOffset(
		bone: BoneType,
		getOffset: Function<SkeletonConfigOffsets, Float>,
	): Float {
		return when (bone) {
			BoneType.HEAD -> getOffset.apply(SkeletonConfigOffsets.HEAD)
			BoneType.NECK -> getOffset.apply(SkeletonConfigOffsets.NECK)
			BoneType.UPPER_CHEST -> getOffset.apply(SkeletonConfigOffsets.UPPER_CHEST)
			BoneType.CHEST -> getOffset.apply(SkeletonConfigOffsets.CHEST)
			BoneType.WAIST -> getOffset.apply(SkeletonConfigOffsets.WAIST)
			BoneType.HIP -> getOffset.apply(SkeletonConfigOffsets.HIP)
			BoneType.LEFT_HIP, BoneType.RIGHT_HIP -> (
				getOffset.apply(SkeletonConfigOffsets.HIPS_WIDTH) /
					2f
				)

			BoneType.LEFT_UPPER_LEG, BoneType.RIGHT_UPPER_LEG ->
				getOffset
					.apply(SkeletonConfigOffsets.UPPER_LEG)

			BoneType.LEFT_LOWER_LEG, BoneType.RIGHT_LOWER_LEG ->
				getOffset
					.apply(SkeletonConfigOffsets.LOWER_LEG)

			else -> -1f
		}
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
			val offset = computeBoneOffset(bone, getOffset)
			if (offset > 0f) {
				offsets[bone] = offset
			}
		}
	}

	fun getBoneDirection(
		skeleton: HumanPoseManager,
		node: BoneType,
		rightSide: Boolean,
	): Vector3 {
		var node = when (node) {
			BoneType.LEFT_HIP, BoneType.RIGHT_HIP -> if (rightSide) BoneType.RIGHT_HIP else BoneType.LEFT_HIP
			BoneType.LEFT_UPPER_LEG, BoneType.RIGHT_UPPER_LEG ->
				if (rightSide) BoneType.RIGHT_UPPER_LEG else BoneType.LEFT_UPPER_LEG
			BoneType.LEFT_LOWER_LEG, BoneType.RIGHT_LOWER_LEG ->
				if (rightSide) BoneType.RIGHT_LOWER_LEG else BoneType.LEFT_LOWER_LEG
			else -> node
		}
		val relevantTransform = skeleton.getTailNodeOfBone(node)
		return (relevantTransform.worldTransform.translation - relevantTransform.parent!!.worldTransform.translation).unit()
	}

	fun getDotProductDiff(
		skeleton1: HumanPoseManager,
		skeleton2: HumanPoseManager,
		node: BoneType,
		rightSide: Boolean,
		offset: Vector3,
	): Float {
		val normalizedOffset = offset.unit()
		val dot1 = normalizedOffset.dot(getBoneDirection(skeleton1, node, rightSide))
		val dot2 = normalizedOffset.dot(getBoneDirection(skeleton2, node, rightSide))
		return dot2 - dot1
	}

	fun applyConfig(
		configConsumer: BiConsumer<SkeletonConfigOffsets, Float>,
		offsets: Map<BoneType, Float> = this.offsets,
	): Boolean {
		return try {
			val headOffset = offsets[BoneType.HEAD]
			if (headOffset != null) {
				configConsumer.accept(SkeletonConfigOffsets.HEAD, headOffset)
			}
			val neckOffset = offsets[BoneType.NECK]
			if (neckOffset != null) {
				configConsumer.accept(SkeletonConfigOffsets.NECK, neckOffset)
			}
			val upperChestOffset = offsets[BoneType.UPPER_CHEST]
			val chestOffset = offsets[BoneType.CHEST]
			val waistOffset = offsets[BoneType.WAIST]
			val hipOffset = offsets[BoneType.HIP]
			if (upperChestOffset != null) {
				configConsumer
					.accept(SkeletonConfigOffsets.UPPER_CHEST, upperChestOffset)
			}
			if (chestOffset != null) {
				configConsumer
					.accept(SkeletonConfigOffsets.CHEST, chestOffset)
			}
			if (waistOffset != null) {
				configConsumer.accept(SkeletonConfigOffsets.WAIST, waistOffset)
			}
			if (hipOffset != null) {
				configConsumer.accept(SkeletonConfigOffsets.HIP, hipOffset)
			}
			var hipWidthOffset = offsets[BoneType.LEFT_HIP]
			if (hipWidthOffset == null) {
				hipWidthOffset = offsets[BoneType.RIGHT_HIP]
			}
			if (hipWidthOffset != null) {
				configConsumer
					.accept(SkeletonConfigOffsets.HIPS_WIDTH, hipWidthOffset * 2f)
			}
			var upperLegOffset = offsets[BoneType.LEFT_UPPER_LEG]
			if (upperLegOffset == null) {
				upperLegOffset = offsets[BoneType.RIGHT_UPPER_LEG]
			}
			var lowerLegOffset = offsets[BoneType.LEFT_LOWER_LEG]
			if (lowerLegOffset == null) {
				lowerLegOffset = offsets[BoneType.RIGHT_LOWER_LEG]
			}
			if (upperLegOffset != null) {
				configConsumer
					.accept(SkeletonConfigOffsets.UPPER_LEG, upperLegOffset)
			}
			if (lowerLegOffset != null) {
				configConsumer.accept(SkeletonConfigOffsets.LOWER_LEG, lowerLegOffset)
			}
			true
		} catch (e: Exception) {
			false
		}
	}

	fun applyConfig(
		skeletonConfig: MutableMap<SkeletonConfigOffsets, Float>,
		offsets: Map<BoneType, Float> = this.offsets,
	): Boolean {
		return applyConfig({ key: SkeletonConfigOffsets, value: Float -> skeletonConfig[key] = value }, offsets)
	}

	fun applyConfig(
		humanPoseManager: HumanPoseManager,
		offsets: Map<BoneType, Float> = this.offsets,
	): Boolean {
		return applyConfig({ key: SkeletonConfigOffsets?, newLength: Float? ->
			humanPoseManager.setOffset(
				key,
				newLength
			)
		}, offsets)
	}

	@JvmOverloads
	fun applyAndSaveConfig(humanPoseManager: HumanPoseManager? = this.server.humanPoseManager): Boolean {
		if (humanPoseManager == null) return false
		if (!applyConfig(humanPoseManager)) return false
		humanPoseManager.saveConfig()
		server.configManager.saveConfig()
		LogManager.info("[AutoBone] Configured skeleton bone lengths")
		return true
	}

	fun getConfig(config: BoneType): Float? {
		return offsets[config]
	}

	fun <T> sumSelectConfigs(
		selection: List<T>,
		configs: Function<T, Float?>,
	): Float {
		var sum = 0f
		for (config in selection) {
			val length = configs.apply(config)
			if (length != null) {
				sum += length
			}
		}
		return sum
	}

	fun <T> sumSelectConfigs(
		selection: List<T>,
		configs: Map<T, Float>,
	): Float {
		return sumSelectConfigs(selection) { key: T -> configs[key] }
	}

	fun sumSelectConfigs(
		selection: List<SkeletonConfigOffsets>,
		humanPoseManager: HumanPoseManager,
	): Float {
		return sumSelectConfigs(selection) { key: SkeletonConfigOffsets? -> humanPoseManager.getOffset(key) }
	}

	fun getLengthSum(configs: Map<BoneType, Float>): Float {
		return getLengthSum(configs, null)
	}

	fun getLengthSum(
		configs: Map<BoneType, Float>,
		configsAlt: Map<BoneType, Float>?,
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
			if (hmdHeight <= 0.50f) {
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
		epochCallback: Consumer<Epoch?>? = null,
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
			intermediateOffsets = EnumMap(offsets),
			epochCallback = epochCallback,
			serverConfig = server.configManager
		)

		// Initialize the frame order randomizer with a repeatable seed
		rand.setSeed(config.randSeed)

		// Epoch loop, each epoch is one full iteration over the full dataset
		for (epoch in (if (config.calcInitError) -1 else 0) until config.numEpochs) {
			// Set the current epoch to process
			trainingStep.curEpoch = epoch

			// Process the epoch
			internalEpoch(trainingStep)
		}

		val finalHeight = sumSelectConfigs(heightOffsets, offsets)
		LogManager
			.info(
				"[AutoBone] Target height: ${trainingStep.targetHmdHeight}, New height: $finalHeight"
			)

		return AutoBoneResults(
			finalHeight,
			trainingStep.targetHmdHeight,
			trainingStep.errorStats,
			legacyConfigs
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
		}

		// Convert current adjusted config to the legacy config format for the epoch
		// callback, then call it
		applyConfig(legacyConfigs)
		trainingStep.epochCallback?.accept(Epoch(epoch + 1, config.numEpochs, errorStats, legacyConfigs))
	}

	private fun internalIter(trainingStep: AutoBoneStep) {
		// Pull frequently used variables out of trainingStep to reduce call length
		val skeleton1 = trainingStep.skeleton1
		val skeleton2 = trainingStep.skeleton2
		val intermediateOffsets = trainingStep.intermediateOffsets

		val totalLength = getLengthSum(offsets)
		val curHeight = sumSelectConfigs(heightOffsets, offsets)
		trainingStep.currentHmdHeight = curHeight

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

		// Load the current offsets into the working offsets holder
		intermediateOffsets.putAll(offsets)

		for (entry in offsets.entries) {
			// Skip adjustment if the epoch is before starting (for
			// logging only)
			if (trainingStep.curEpoch < 0) {
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
			intermediateOffsets[entry.key] = newLength
			applyConfig(skeleton1, intermediateOffsets)
			applyConfig(skeleton2, intermediateOffsets)

			// Update the skeleton poses for the new offset length
			skeleton1.update()
			skeleton2.update()

			val newErrorDeriv = getErrorDeriv(trainingStep)
			if (newErrorDeriv < errorDeriv) {
				// Apply the adjusted length to the current adjusted offsets
				entry.setValue(newLength)
			}

			// Reset the length to minimize bias in other variables,
			// it's applied later
			intermediateOffsets[entry.key] = originalLength
			applyConfig(skeleton1, intermediateOffsets)
			applyConfig(skeleton2, intermediateOffsets)
		}

		if (trainingStep.config.scaleEachStep) {
			// Scale to the target height if requested by the config
			scaleToTargetHeight(trainingStep)
		}
	}

	private fun scaleToTargetHeight(trainingStep: AutoBoneStep) {
		val stepHeight = sumSelectConfigs(heightOffsets, offsets)
		if (stepHeight > 0f) {
			val stepHeightDiff = trainingStep.targetHmdHeight - stepHeight
			for (entry in offsets.entries) {
				// Only height variables
				if (entry.key == BoneType.NECK ||
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
			offsets.forEach { (key: BoneType, value: Float) ->
				if (configInfo.isNotEmpty()) {
					configInfo.append(", ")
				}
				configInfo
					.append(key.toString())
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
		val saveDir = File("AutoBone Recordings")
		val loadDir = File("Load AutoBone Recordings")

		// Mean square error function
		private fun errorFunc(errorDeriv: Float): Float {
			return 0.5f * (errorDeriv * errorDeriv)
		}

		private fun decayFunc(initialAdjustRate: Float, adjustRateDecay: Float, epoch: Int): Float {
			return if (epoch >= 0) initialAdjustRate / (1 + (adjustRateDecay * epoch)) else 0.0f
		}
	}
}
