import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtException
import ai.onnxruntime.OrtSession
import ai.onnxruntime.TensorInfo
import dev.slimevr.tracking.videocalibration.data.CocoWholeBodyKeypoint
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.File
import java.nio.FloatBuffer
import java.util.concurrent.locks.ReentrantLock
import javax.imageio.ImageIO
import kotlin.concurrent.withLock
import kotlin.math.min

data class JointResult(
	val id: Int,
	val name: CocoWholeBodyKeypoint,
	val x: Double,
	val y: Double,
	val confidence: Double,
	val visible: Boolean,
)

data class PersonResult(
	val person_id: Int,
	val joints: List<JointResult>,
)

data class PoseResponse(
	val image_path: String,
	val detections: List<PersonResult>,
)

/**
 * Core RTMPose pipeline in Kotlin (pure JVM image processing):
 * - decode image bytes with ImageIO
 * - run YOLO detector ONNX
 * - run RTMPose ONNX for each person box
 * - return service-compatible JSON model
 *
 * Output assumptions (based on your exported models):
 * - detector: dets [1,N,5] + labels [1,N]
 * - pose: SimCC pair [1,K,384] and [1,K,512]
 */
class RtmposeOnnxPipeline(
	private val yoloOnnxPath: String,
	private val poseOnnxPath: String,
	private val yoloScoreThreshold: Float = 0.3f,
	private val poseScoreThreshold: Float = 0.5f,
	private val useCudaIfAvailable: Boolean = false,
	private val cudaDeviceId: Int = 0,
	private val yoloInputWidth: Int = 640,
	private val yoloInputHeight: Int = 640,
	private val poseInputWidth: Int = 192,
	private val poseInputHeight: Int = 256,
) : AutoCloseable {

	private val env: OrtEnvironment = OrtEnvironment.getEnvironment()
	private var yoloSession: OrtSession? = null
	private var poseSession: OrtSession? = null
	private val inferLock = ReentrantLock()
	private val cocoWholeBodyKeypoints = CocoWholeBodyKeypoint.values()

	fun start() {
// 		val yoloOptions = OrtSession.SessionOptions()
// 		yoloOptions.setOptimizationLevel(OrtSession.SessionOptions.OptLevel.ALL_OPT)
// 		configureExecutionProvider(yoloOptions)
// 		yoloSession = env.createSession(yoloOnnxPath, yoloOptions)

		val poseOptions = OrtSession.SessionOptions()
		poseOptions.setOptimizationLevel(OrtSession.SessionOptions.OptLevel.ALL_OPT)
		configureExecutionProvider(poseOptions)
		poseSession = env.createSession(poseOnnxPath, poseOptions)

		validateSessionSchemas()
	}

	private fun configureExecutionProvider(options: OrtSession.SessionOptions) {
		if (!useCudaIfAvailable) return
		try {
			options.addCUDA(cudaDeviceId)
			println("ONNX Runtime: CUDA EP enabled (device=$cudaDeviceId).")
		} catch (t: Throwable) {
			// Falls back to CPU EP automatically when CUDA EP cannot be added.
			println("ONNX Runtime: CUDA EP unavailable, falling back to CPU. Reason: ${t.message}")
		}
	}

	fun processImage(image: BufferedImage, imageLabel: String = "<image>"): PoseResponse {
		val rgbImage = toRgb(image)
		return inferLock.withLock {
// 			val personBoxes = runDetector(rgbImage)
// 			if (personBoxes.isNotEmpty()) {
// 				saveDebugBboxImage(rgbImage, personBoxes[0], File("C:\\SlimeVR\\bbox.jpg"))
// 			}
			val personBoxes = listOf(BBox(0.0f, 0.0f, image.width.toFloat(), image.height.toFloat()))
			val detections = personBoxes.mapIndexedNotNull { personId, box ->
				val pose = runPoseForBox(rgbImage, box) ?: return@mapIndexedNotNull null
				val joints = pose.mapIndexed { idx, kp ->
					JointResult(
						id = idx,
						name = keypointName(idx),
						x = kp.x.toDouble(),
						y = kp.y.toDouble(),
						confidence = kp.score.toDouble(),
						visible = kp.score >= poseScoreThreshold,
					)
				}
				PersonResult(person_id = personId, joints = joints)
			}
			PoseResponse(image_path = imageLabel, detections = detections)
		}
	}

	private fun saveDebugBboxImage(image: BufferedImage, box: BBox, outputFile: File) {
		val out = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)
		val g = out.createGraphics()
		g.drawImage(image, 0, 0, null)
		g.color = Color(255, 0, 0)
		g.stroke = BasicStroke(3f)
		g.drawRect(
			box.x1.toInt(),
			box.y1.toInt(),
			box.width().toInt().coerceAtLeast(1),
			box.height().toInt().coerceAtLeast(1),
		)
		g.dispose()
		outputFile.parentFile?.mkdirs()
		ImageIO.write(out, "jpg", outputFile)
	}

	private fun runDetector(image: BufferedImage): List<BBox> {
		val yoloSession = yoloSession ?: return listOf()

		val prep = letterboxRgbToNchw(image, yoloInputWidth, yoloInputHeight)
		val inputName = yoloSession.inputNames.iterator().next()
		val shape = longArrayOf(1, 3, yoloInputHeight.toLong(), yoloInputWidth.toLong())
		val tensor = OnnxTensor.createTensor(env, FloatBuffer.wrap(prep.nchw), shape)

		yoloSession.run(mapOf(inputName to tensor)).use { output ->
			val boxes = parseDetectorOutputs(output, prep.scale, prep.padX, prep.padY, image.width, image.height)
			val best = boxes.maxByOrNull { it.score } ?: return emptyList()
			return listOf(best.box)
		}
	}

	private fun runPoseForBox(image: BufferedImage, box: BBox): List<Keypoint>? {
		val poseSession = poseSession ?: return listOf()

		val clamped = clampBox(box, image.width, image.height) ?: return null
		val crop = image.getSubimage(clamped.x1.toInt(), clamped.y1.toInt(), clamped.width().toInt(), clamped.height().toInt())
		val resized = resizeImage(crop, poseInputWidth, poseInputHeight)
		val chw = rgbToNchw(resized)

		val inputName = poseSession.inputNames.iterator().next()
		val shape = longArrayOf(1, 3, poseInputHeight.toLong(), poseInputWidth.toLong())
		val tensor = OnnxTensor.createTensor(env, FloatBuffer.wrap(chw), shape)

		poseSession.run(mapOf(inputName to tensor)).use { output ->
			val keypoints = parsePoseOutputs(output) ?: return null
			return keypoints.map { kp ->
				val x = clamped.x1 + kp.x * clamped.width() / poseInputWidth.toFloat()
				val y = clamped.y1 + kp.y * clamped.height() / poseInputHeight.toFloat()
				Keypoint(x = x, y = y, score = kp.score)
			}
		}
	}

	private fun parsePoseOutputs(output: OrtSession.Result): List<Keypoint>? {
		val simccXTensor = output[0] as? OnnxTensor
			?: throw IllegalArgumentException(
				"Unexpected pose output[0] type: ${output[0]::class.java.name}. Expected OnnxTensor.",
			)
		val simccYTensor = output[1] as? OnnxTensor
			?: throw IllegalArgumentException(
				"Unexpected pose output[1] type: ${output[1]::class.java.name}. Expected OnnxTensor.",
			)

		val xInfo = simccXTensor.info as? TensorInfo
			?: throw IllegalArgumentException("Pose output 'simcc_x' is not tensor info.")
		val yInfo = simccYTensor.info as? TensorInfo
			?: throw IllegalArgumentException("Pose output 'simcc_y' is not tensor info.")
		val xShape = xInfo.shape
		val yShape = yInfo.shape
		require(xShape.size == 3 && yShape.size == 3) {
			"Unexpected pose output shapes: simcc_x=${xShape.contentToString()}, simcc_y=${yShape.contentToString()}"
		}
		require(xShape[0] == yShape[0] && xShape[1] == yShape[1]) {
			"Mismatched simcc batch/keypoint dims: simcc_x=${xShape.contentToString()}, simcc_y=${yShape.contentToString()}"
		}

		val kCount = xShape[1].toInt()
		val xBins = xShape[2].toInt()
		val yBins = yShape[2].toInt()
		val simccSplitRatioX = xBins.toFloat() / poseInputWidth.toFloat()
		val simccSplitRatioY = yBins.toFloat() / poseInputHeight.toFloat()

		val xBuf = simccXTensor.floatBuffer.duplicate()
		val yBuf = simccYTensor.floatBuffer.duplicate()
		val xFlat = FloatArray(xBuf.remaining())
		val yFlat = FloatArray(yBuf.remaining())
		xBuf.get(xFlat)
		yBuf.get(yFlat)

		require(xFlat.size >= kCount * xBins && yFlat.size >= kCount * yBins) {
			"SimCC buffers are smaller than expected for one batch: " +
				"x=${xFlat.size} need>=${kCount * xBins}, y=${yFlat.size} need>=${kCount * yBins}"
		}

		val out = ArrayList<Keypoint>(kCount)
		for (i in 0 until kCount) {
			val (xIdx, xScore) = argMaxSlice(xFlat, i * xBins, xBins)
			val (yIdx, yScore) = argMaxSlice(yFlat, i * yBins, yBins)
			val x = xIdx.toFloat() / simccSplitRatioX
			val y = yIdx.toFloat() / simccSplitRatioY
			out.add(Keypoint(x = x, y = y, score = min(xScore, yScore)))
		}
		return out
	}

	private fun validateSessionSchemas() {
// 		val yoloSession = yoloSession ?: return
		val poseSession = poseSession ?: return

// 		val yoloInputNames = yoloSession.inputNames
// 		require(yoloInputNames.contains("input")) {
// 			"YOLO model must expose input named 'input'. Found: $yoloInputNames"
// 		}
//
// 		val yoloOutputNames = yoloSession.outputNames
// 		require(yoloOutputNames.contains("dets")) {
// 			"YOLO model must expose output named 'dets'. Found: $yoloOutputNames"
// 		}
// 		require(yoloOutputNames.contains("labels")) {
// 			"YOLO model must expose output named 'labels'. Found: $yoloOutputNames"
// 		}

		val poseInputNames = poseSession.inputNames
		require(poseInputNames.contains("input")) {
			"RTMPose model must expose input named 'input'. Found: $poseInputNames"
		}

		val poseOutputNames = poseSession.outputNames
		require(poseOutputNames.contains("simcc_x")) {
			"RTMPose model must expose output named 'simcc_x'. Found: $poseOutputNames"
		}
		require(poseOutputNames.contains("simcc_y")) {
			"RTMPose model must expose output named 'simcc_y'. Found: $poseOutputNames"
		}

		// Soft shape checks for extra safety; fail only when the schema is clearly incompatible.
// 		validateExpectedRank(
// 			session = yoloSession,
// 			tensorName = "dets",
// 			expectedRank = 3,
// 			modelName = "YOLO",
// 		)
// 		validateExpectedRank(
// 			session = yoloSession,
// 			tensorName = "labels",
// 			expectedRank = 2,
// 			modelName = "YOLO",
// 		)
		validateExpectedRank(
			session = poseSession,
			tensorName = "simcc_x",
			expectedRank = 3,
			modelName = "RTMPose",
		)
		validateExpectedRank(
			session = poseSession,
			tensorName = "simcc_y",
			expectedRank = 3,
			modelName = "RTMPose",
		)
	}

	private fun validateExpectedRank(
		session: OrtSession,
		tensorName: String,
		expectedRank: Int,
		modelName: String,
	) {
		val nodeInfo = session.outputInfo[tensorName]
			?: throw IllegalArgumentException("$modelName output '$tensorName' is missing.")
		val tensorInfo = nodeInfo.info as? TensorInfo
			?: throw IllegalArgumentException("$modelName output '$tensorName' is not a tensor.")
		val rank = tensorInfo.shape.size
		require(rank == expectedRank) {
			"$modelName output '$tensorName' rank mismatch. Expected $expectedRank, got $rank " +
				"(shape=${tensorInfo.shape.contentToString()})"
		}
	}

	private fun parseDetectorOutputs(
		output: OrtSession.Result,
		scale: Float,
		padX: Float,
		padY: Float,
		imageW: Int,
		imageH: Int,
	): List<ScoredBox> {
		val detsTensor = output[0] as? OnnxTensor
			?: throw IllegalArgumentException(
				"Unexpected detector output[0] type: ${output[0]::class.java.name}. Expected OnnxTensor.",
			)
		val labelsTensor = output[1] as? OnnxTensor
			?: throw IllegalArgumentException(
				"Unexpected detector output[1] type: ${output[1]::class.java.name}. Expected OnnxTensor.",
			)
		return parseDetsWithLabels(detsTensor, labelsTensor, scale, padX, padY, imageW, imageH)
	}

	private fun parseDetsWithLabels(
		detsTensor: OnnxTensor,
		labelsTensor: OnnxTensor,
		scale: Float,
		padX: Float,
		padY: Float,
		imageW: Int,
		imageH: Int,
	): List<ScoredBox> {
		val detInfo = detsTensor.info as? TensorInfo
			?: throw IllegalArgumentException("Detector 'dets' output is not tensor info.")
		val detShape = detInfo.shape
		require(detShape.size == 3 && detShape[0] == 1L && detShape[2] == 5L) {
			"Unexpected detector 'dets' shape ${detShape.contentToString()}; expected [1,N,5]."
		}

		val detBuf = detsTensor.floatBuffer.duplicate()
		val detFlat = FloatArray(detBuf.remaining())
		detBuf.get(detFlat)
		require(detFlat.size % 5 == 0) {
			"Detector 'dets' flat size must be divisible by 5, got ${detFlat.size}."
		}
		val nByFlat = detFlat.size / 5
		val nByShape = if (detShape[1] > 0) detShape[1].toInt() else nByFlat
		val labelsCount = labelsTensor.info.let { info ->
			(info as? TensorInfo)?.shape?.let { shape ->
				if (shape.size == 2 && shape[0] == 1L && shape[1] > 0) shape[1].toInt() else nByFlat
			} ?: nByFlat
		}
		// Runtime buffers are authoritative; TensorInfo can be partially symbolic/static.
		val n = min(nByFlat, min(nByShape, labelsCount))
		require(n > 0) { "Detector produced no candidate boxes (nByFlat=$nByFlat, nByShape=$nByShape, labelsCount=$labelsCount)." }

		val out = ArrayList<ScoredBox>(n)
		for (i in 0 until n) {
			val base = i * 5

			// Match rtmlib YOLOX outputs.shape[-1] == 5 path:
			// use detector score directly with score threshold.
			val score = detFlat[base + 4]
			if (score < yoloScoreThreshold) continue

			val box = fromLetterboxedXYXY(
				x1 = detFlat[base],
				y1 = detFlat[base + 1],
				x2 = detFlat[base + 2],
				y2 = detFlat[base + 3],
				scale = scale,
				padX = padX,
				padY = padY,
				imageW = imageW,
				imageH = imageH,
			)
			if (box != null) out.add(ScoredBox(box, score))
		}
		return out
	}

	private fun fromLetterboxedXYXY(
		x1: Float,
		y1: Float,
		x2: Float,
		y2: Float,
		scale: Float,
		padX: Float,
		padY: Float,
		imageW: Int,
		imageH: Int,
	): BBox? {
		val ox1 = ((x1 - padX) / scale).coerceIn(0f, (imageW - 1).toFloat())
		val oy1 = ((y1 - padY) / scale).coerceIn(0f, (imageH - 1).toFloat())
		val ox2 = ((x2 - padX) / scale).coerceIn(0f, (imageW - 1).toFloat())
		val oy2 = ((y2 - padY) / scale).coerceIn(0f, (imageH - 1).toFloat())
		val box = BBox(ox1, oy1, ox2, oy2)
		return if (box.width() > 2f && box.height() > 2f) box else null
	}

	private fun clampBox(box: BBox, imageW: Int, imageH: Int): BBox? {
		val x1 = box.x1.coerceIn(0f, (imageW - 1).toFloat())
		val y1 = box.y1.coerceIn(0f, (imageH - 1).toFloat())
		val x2 = box.x2.coerceIn(0f, (imageW - 1).toFloat())
		val y2 = box.y2.coerceIn(0f, (imageH - 1).toFloat())
		val clamped = BBox(x1, y1, x2, y2)
		return if (clamped.width() > 2f && clamped.height() > 2f) clamped else null
	}

	private fun argMaxSlice(arr: FloatArray, offset: Int, length: Int): Pair<Int, Float> {
		if (length <= 0) return 0 to 0f
		var idx = 0
		var best = arr[offset]
		var i = 1
		while (i < length) {
			val v = arr[offset + i]
			if (v > best) {
				best = v
				idx = i
			}
			i += 1
		}
		return idx to best
	}

	private fun keypointName(index: Int): CocoWholeBodyKeypoint = cocoWholeBodyKeypoints.getOrNull(index)
		?: throw IllegalArgumentException(
			"Keypoint index $index is out of range for CocoWholeBodyKeypoint " +
				"(size=${cocoWholeBodyKeypoints.size}).",
		)

	private fun letterboxRgbToNchw(image: BufferedImage, dstW: Int, dstH: Int): Preprocessed {
		val srcW = image.width.toFloat()
		val srcH = image.height.toFloat()
		val scale = min(dstW / srcW, dstH / srcH)
		val newW = (srcW * scale).toInt()
		val newH = (srcH * scale).toInt()

		val canvas = BufferedImage(dstW, dstH, BufferedImage.TYPE_INT_RGB)
		val g = canvas.createGraphics()
		g.color = Color(114, 114, 114)
		g.fillRect(0, 0, dstW, dstH)
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
		// Match rtmlib YOLOX preprocess: paste resized image at top-left.
		g.drawImage(image, 0, 0, newW, newH, null)
		g.dispose()

		return Preprocessed(detectorToNchwYoloX(canvas), scale, 0f, 0f)
	}

	private fun resizeImage(src: BufferedImage, width: Int, height: Int): BufferedImage {
		val out = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
		val g: Graphics2D = out.createGraphics()
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
		g.drawImage(src, 0, 0, width, height, null)
		g.dispose()
		return out
	}

	private fun toRgb(src: BufferedImage): BufferedImage {
		if (src.type == BufferedImage.TYPE_INT_RGB) return src
		val out = BufferedImage(src.width, src.height, BufferedImage.TYPE_INT_RGB)
		val g = out.createGraphics()
		g.drawImage(src, 0, 0, null)
		g.dispose()
		return out
	}

	private fun rgbToNchw(image: BufferedImage): FloatArray {
		val width = image.width
		val height = image.height
		val pixels = IntArray(width * height)
		image.getRGB(0, 0, width, height, pixels, 0, width)

		val hw = width * height
		val chw = FloatArray(hw * 3)
		for (i in pixels.indices) {
			val p = pixels[i]
			val r = (p shr 16 and 0xFF) / 255.0f
			val g = (p shr 8 and 0xFF) / 255.0f
			val b = (p and 0xFF) / 255.0f
			chw[i] = r
			chw[hw + i] = g
			chw[2 * hw + i] = b
		}
		return chw
	}

	/**
	 * Match rtmlib YOLOX input formatting:
	 * - channel order: BGR
	 * - value range: raw 0..255 float32
	 * - no mean/std normalization
	 */
	private fun detectorToNchwYoloX(image: BufferedImage): FloatArray {
		val width = image.width
		val height = image.height
		val pixels = IntArray(width * height)
		image.getRGB(0, 0, width, height, pixels, 0, width)

		val hw = width * height
		val chw = FloatArray(hw * 3)
		for (i in pixels.indices) {
			val p = pixels[i]
			val r = (p shr 16 and 0xFF).toFloat()
			val g = (p shr 8 and 0xFF).toFloat()
			val b = (p and 0xFF).toFloat()

			chw[i] = b
			chw[hw + i] = g
			chw[2 * hw + i] = r
		}
		return chw
	}

	override fun close() {
		val yoloSession = yoloSession ?: return
		val poseSession = poseSession ?: return
		try {
			yoloSession.close()
		} catch (_: OrtException) {
		}
		try {
			poseSession.close()
		} catch (_: OrtException) {
		}
	}

	private data class Keypoint(val x: Float, val y: Float, val score: Float)
	private data class BBox(val x1: Float, val y1: Float, val x2: Float, val y2: Float) {
		fun width(): Float = x2 - x1
		fun height(): Float = y2 - y1
	}
	private data class ScoredBox(val box: BBox, val score: Float)
	private data class Preprocessed(val nchw: FloatArray, val scale: Float, val padX: Float, val padY: Float)
}
