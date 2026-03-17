package dev.slimevr.tracking.videocalibration.steps

import dev.slimevr.tracking.processor.Bone
import dev.slimevr.tracking.processor.config.SkeletonConfigOffsets
import dev.slimevr.tracking.processor.skeleton.refactor.Skeleton
import dev.slimevr.tracking.processor.skeleton.refactor.SkeletonUpdater
import dev.slimevr.tracking.trackers.TrackerPosition
import dev.slimevr.tracking.videocalibration.data.Camera
import dev.slimevr.tracking.videocalibration.data.CocoWholeBodyKeypoint
import dev.slimevr.tracking.videocalibration.data.TrackerResetOverride
import dev.slimevr.tracking.videocalibration.snapshots.HumanPoseSnapshot
import dev.slimevr.tracking.videocalibration.snapshots.TrackerSnapshot
import dev.slimevr.tracking.videocalibration.snapshots.TrackersSnapshot
import dev.slimevr.tracking.videocalibration.util.DebugOutput
import dev.slimevr.tracking.videocalibration.util.numericalJacobian
import io.eiren.util.logging.LogManager
import io.github.axisangles.ktmath.Vector2D
import org.apache.commons.math3.analysis.MultivariateVectorFunction
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresBuilder
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer
import kotlin.math.min

class SkeletonOffsetsSolver(
	private val debugOutput: DebugOutput,
) {

	class Solution(
		val skeletonOffsets: Map<SkeletonConfigOffsets, Float>,
	)

	private val skeleton = Skeleton(false, false)

	fun solve(
		frames: List<Pair<TrackersSnapshot, HumanPoseSnapshot>>,
		camera: Camera,
		initialSkeletonOffsets: Map<SkeletonConfigOffsets, Float>,
		trackerResets: Map<TrackerPosition, TrackerResetOverride>,
	): Solution? {
		val adjustedFrames = frames.map { (trackersSnapshot, humanPoseSnapshot) ->
			val fixedTrackersSnapshot =
				TrackersSnapshot(
					trackersSnapshot.instant,
					trackersSnapshot.trackers.map { (trackerPosition, trackerSnapshot) ->
						val trackerReset = trackerResets[trackerPosition]
						if (trackerReset != null) {
							trackerPosition to
								TrackerSnapshot(
									trackerSnapshot.rawTrackerToWorld,
									trackerReset.toBoneRotation(trackerSnapshot.rawTrackerToWorld),
									trackerSnapshot.trackerOriginInWorld,
								)
						} else {
							trackerPosition to trackerSnapshot
						}
					}.toMap(),
				)
			fixedTrackersSnapshot to humanPoseSnapshot
		}

		val filteredAdjustedFrames = adjustedFrames.filterIndexed { index, _ -> index % SKIP_FRAMES == 0 }

		LogManager.info("Solving skeleton offsets with ${filteredAdjustedFrames.size} frames...")

		if (filteredAdjustedFrames.size < MIN_FRAMES) {
			return null
		}

		val n = filteredAdjustedFrames.size * NUM_JOINTS * 2

		val costFn = MultivariateVectorFunction { p ->
			var index = 0
			val residuals = DoubleArray(n) { 0.0 }

			val config = SkeletonUpdater.HumanSkeletonConfig()
			val skeletonOffsets = makeSkeletonOffsets(p, initialSkeletonOffsets)
			for (frame in filteredAdjustedFrames) {
				val (trackersSnapshot, humanPoseSnapshot) = frame
				val trackersData = SkeletonUpdater.TrackersData.fromSnapshot(trackersSnapshot.trackers)
				val joints = humanPoseSnapshot.joints

				val skeletonUpdater = SkeletonUpdater(skeleton, trackersData, config, skeletonOffsets)
				skeletonUpdater.update()

				val leftShoulderJoint = joints[CocoWholeBodyKeypoint.LEFT_SHOULDER]
				val rightShoulderJoint = joints[CocoWholeBodyKeypoint.RIGHT_SHOULDER]

				index = addResidual(residuals, index, camera, skeleton.leftUpperArmBone, leftShoulderJoint)
				index = addResidual(residuals, index, camera, skeleton.rightUpperArmBone, rightShoulderJoint)

				if (leftShoulderJoint != null && rightShoulderJoint != null) {
					index = addResidual(residuals, index, camera, skeleton.upperChestBone, (leftShoulderJoint + rightShoulderJoint) * 0.5)
				}

				index = addResidual(residuals, index, camera, skeleton.leftUpperLegBone, joints[CocoWholeBodyKeypoint.LEFT_HIP])
				index = addResidual(residuals, index, camera, skeleton.leftLowerLegBone, joints[CocoWholeBodyKeypoint.LEFT_KNEE])
				index = addResidual(residuals, index, camera, skeleton.leftFootBone, joints[CocoWholeBodyKeypoint.LEFT_ANKLE])

				index = addResidual(residuals, index, camera, skeleton.rightUpperLegBone, joints[CocoWholeBodyKeypoint.RIGHT_HIP])
				index = addResidual(residuals, index, camera, skeleton.rightLowerLegBone, joints[CocoWholeBodyKeypoint.RIGHT_KNEE])
				index = addResidual(residuals, index, camera, skeleton.rightFootBone, joints[CocoWholeBodyKeypoint.RIGHT_ANKLE])
			}

			return@MultivariateVectorFunction residuals
		}

		val model = numericalJacobian(costFn)

		// TODO: Use params
		val initial = doubleArrayOf(
			0.7, // TORSO
			0.35, // HIPS_WIDTH
			0.5, // UPPER_LEG
			0.5, // LOWER_LEG
			0.15, // NECK
			0.20, // HEAD
		)

		val problem = LeastSquaresBuilder()
			.start(initial)
			.model(model)
			.target(DoubleArray(n) { 0.0 })
			.maxEvaluations(10000)
			.maxIterations(10000)
			.build()

		val optimizer = LevenbergMarquardtOptimizer()

		val result: LeastSquaresOptimizer.Optimum
		try {
			result = optimizer.optimize(problem)
		} catch (e: Exception) {
			LogManager.warning("Failed to solve skeleton offsets: $e", e)
			return null
		}

		val skeletonOffsets = makeSkeletonOffsets(result.point.toArray(), initialSkeletonOffsets)

		// Give some of the neck back to the upper chest so that head movement doesn't cause as much movement in upper body
		val neck = skeletonOffsets[SkeletonConfigOffsets.NECK]
		if (neck != null) {
			skeletonOffsets[SkeletonConfigOffsets.NECK] = neck * 0.5f
			val upperChest = skeletonOffsets[SkeletonConfigOffsets.UPPER_CHEST]
			if (upperChest != null) {
				skeletonOffsets[SkeletonConfigOffsets.UPPER_CHEST] = upperChest + neck * 0.5f
				skeletonOffsets[SkeletonConfigOffsets.SHOULDERS_DISTANCE] = neck * 0.5f
			}
		}

		// Adjust ankle to the ground
		val lowerLegLength = skeletonOffsets[SkeletonConfigOffsets.LOWER_LEG]
		if (lowerLegLength != null) {
			skeletonOffsets[SkeletonConfigOffsets.LOWER_LEG] = lowerLegLength + ANKLE_TO_HEEL_LENGTH.toFloat()
		}

		fun printSkeletonOffset(skeletonOffset: SkeletonConfigOffsets) {
			val formatter = "%.2f"
			LogManager.info("${skeletonOffset.name.padStart(15, ' ')}: ${formatter.format(skeletonOffsets[skeletonOffset])} (was ${formatter.format(initialSkeletonOffsets[skeletonOffset])})")
		}

		LogManager.info("Solved skeleton offsets:")
		printSkeletonOffset(SkeletonConfigOffsets.HEAD)
		printSkeletonOffset(SkeletonConfigOffsets.NECK)
		printSkeletonOffset(SkeletonConfigOffsets.UPPER_CHEST)
		printSkeletonOffset(SkeletonConfigOffsets.CHEST)
		printSkeletonOffset(SkeletonConfigOffsets.WAIST)
		printSkeletonOffset(SkeletonConfigOffsets.HIP)
		printSkeletonOffset(SkeletonConfigOffsets.HIPS_WIDTH)
		printSkeletonOffset(SkeletonConfigOffsets.UPPER_LEG)
		printSkeletonOffset(SkeletonConfigOffsets.LOWER_LEG)

		return Solution(skeletonOffsets)
	}

	private fun makeSkeletonOffsets(
		p: DoubleArray,
		initialSkeletonOffsets: Map<SkeletonConfigOffsets, Float>,
	): MutableMap<SkeletonConfigOffsets, Float> {
		val torsoLength = p[0]
		val offsets = initialSkeletonOffsets.toMutableMap().apply {
			this[SkeletonConfigOffsets.UPPER_CHEST] = (UPPER_CHEST_RATIO * torsoLength).toFloat()
			this[SkeletonConfigOffsets.CHEST] = (CHEST_RATIO * torsoLength).toFloat()
			this[SkeletonConfigOffsets.WAIST] = (WAIST_RATIO * torsoLength).toFloat()
			this[SkeletonConfigOffsets.HIP] = (HIP_RATIO * torsoLength).toFloat()
			this[SkeletonConfigOffsets.HIPS_WIDTH] = p[1].toFloat()
			this[SkeletonConfigOffsets.UPPER_LEG] = p[2].toFloat()
			this[SkeletonConfigOffsets.LOWER_LEG] = p[3].toFloat()
			this[SkeletonConfigOffsets.NECK] = p[4].toFloat()
			this[SkeletonConfigOffsets.HEAD] = p[5].toFloat()
		}
		return offsets
	}

	private fun addResidual(residuals: DoubleArray, index: Int, camera: Camera, bone: Bone, joint: Vector2D?): Int {
		if (joint == null) {
			return index
		}

		val estimated = camera.project(bone.getPosition().toDouble())
		if (estimated == null) {
			return index
		}

		residuals[index + 0] = joint.x - estimated.x
		residuals[index + 1] = joint.y - estimated.y

		return index + 2
	}

	companion object {
		private const val SKIP_FRAMES = 5
		private const val MIN_FRAMES = 100

		private const val UPPER_CHEST_RATIO = 0.25
		private const val CHEST_RATIO = 0.25
		private const val WAIST_RATIO = 0.25
		private const val HIP_RATIO = 0.25

		private const val ANKLE_TO_HEEL_LENGTH = 0.08

		private const val NUM_JOINTS = 9
	}
}
