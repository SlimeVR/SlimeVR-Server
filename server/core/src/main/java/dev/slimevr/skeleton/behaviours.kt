package dev.slimevr.skeleton

import dev.slimevr.config.UserConfig
import dev.slimevr.logging.AppLogger
import dev.slimevr.timeSource
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import solarxr_protocol.datatypes.BodyPart
import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime

class BoneTransformBehaviour : SkeletonBehaviour {
	override fun reduce(state: SkeletonState, action: SkeletonActions): SkeletonState = when (action) {
		is SkeletonActions.SetBoneRotation -> {
			val bone = state.boneInputs[action.bodyPart] ?: return state
			state.copy(boneInputs = state.boneInputs.mutate { it[action.bodyPart] = bone.copy(rawRotation = action.rotation, isActive = true) })
		}

		is SkeletonActions.SetBonePosition -> {
			val bone = state.boneInputs[action.bodyPart] ?: return state
			state.copy(boneInputs = state.boneInputs.mutate { it[action.bodyPart] = bone.copy(rawPosition = action.position, isActive = true) })
		}

		is SkeletonActions.DisableBone -> {
			val bone = state.boneInputs[action.bodyPart] ?: return state
			state.copy(boneInputs = state.boneInputs.mutate { it[action.bodyPart] = bone.copy(rawRotation = Quaternion.IDENTITY, rawPosition = Vector3.NULL, isActive = false) })
		}

		else -> state
	}
}

class ProportionsBehaviour(private val userConfig: UserConfig) : SkeletonBehaviour {
	override fun reduce(state: SkeletonState, action: SkeletonActions): SkeletonState = when (action) {
		is SkeletonActions.SetProportions -> {
			val bones = action.lengths.toBoneOffsets()
			val newBones = state.boneInputs.mapValues { bodyPart, bone ->
				bone.copy(offset = bones[bodyPart] ?: bone.offset)
			}
			state.copy(boneInputs = newBones, skeletonHeight = action.lengths.height())
		}

		else -> state
	}

	override fun observe(receiver: Skeleton) {
		userConfig.context.state
			.map { state -> state.data.proportions }
			.distinctUntilChanged()
			.onEach { proportions ->
				if (proportions.isNotEmpty()) {
					receiver.context.dispatch(SkeletonActions.SetProportions(configToBoneValues(proportions)))
				}
			}
			.launchIn(receiver.context.scope)
	}
}

class HeightLogBehaviour : SkeletonBehaviour {
	override fun observe(receiver: Skeleton) {
		receiver.context.scope.launch {
			receiver.context.state
				.map { state -> state.skeletonHeight }
				.distinctUntilChanged()
				.collect { height -> AppLogger.skeleton.info("User height changed: ${"%.2f".format(height)}m") }
		}
	}
}

class YouSpinMeRightRoundBehaviour(val inputHz: Float = 1f) : SkeletonBehaviour {
	override fun observe(receiver: Skeleton) {
		receiver.context.scope.launch {
			val intervalMs = (1000f / inputHz).toLong()
			val startTime = timeSource.markNow()
			while (true) {
				delay(intervalMs)
				val elapsed = startTime.elapsedNow().inWholeMilliseconds / 1000f
				val state = receiver.context.state.value

				receiver.context.dispatch(
					SkeletonActions.SetBoneRotation(
						BodyPart.CHEST,
						Quaternion.fromRotationVector(Vector3(cos(elapsed), sin(elapsed), 0f)),
					),
				)
				receiver.context.dispatch(
					SkeletonActions.SetBoneRotation(
						BodyPart.LEFT_LOWER_LEG,
						Quaternion.fromRotationVector(Vector3(cos(elapsed + 1000), sin(elapsed + 1000), 0f)),
					),
				)

				val circleRadius = 0.5f
				val circleX = cos(elapsed * 2f) * circleRadius
				val circleZ = sin(elapsed * 2f) * circleRadius
				val jumpHeight = maxOf(0f, sin(elapsed * 3f) * 0.3f)
				receiver.context.dispatch(
					SkeletonActions.SetBonePosition(
						BodyPart.HEAD,
						Vector3(circleX, state.skeletonHeight + jumpHeight, circleZ),
					),
				)
			}
		}
	}
}

class PauseTrackingBehaviour : SkeletonBehaviour {
	override fun reduce(state: SkeletonState, action: SkeletonActions): SkeletonState = when (action) {
		is SkeletonActions.PauseTracking -> state.copy(paused = action.pause)
		else -> state
	}
}

class ComputedSkeletonBehaviour(
	val hz: Int,
	val processors: List<SkeletonProcessor> = emptyList(),
) : SkeletonBehaviour {
	private val intervalDuration = (1.0 / hz).seconds
	private val minimumDelay = 1.nanoseconds
	private val logSpamWait = 3.minutes

	override fun observe(receiver: Skeleton) {
		var nextLogTime = timeSource.markNow()
		var frameStartTime = timeSource.markNow()
		var lastFrameTime = Duration.ZERO
		var processTooLongCount = 0

		receiver.context.scope.launch {
			while (true) {
				try {
					// Process starts
					val processTime = measureTime {
						val targetState = receiver.context.state.value

						// Run processors
						val processed = processors
							.fold(targetState) { state, processor -> processor.process(state, lastFrameTime) } // TODO: Add a constrain processor (maybe not needed)

						// Get head position
						val rootHead = processed.boneInputs[BodyPart.HEAD]
							?.let { Vector3(it.rawPosition.x, it.rawPosition.y, it.rawPosition.z) }
							?: Vector3(0f, targetState.skeletonHeight, 0f)

						// Run FK
						val fk = buildBones(processed, rootHead = rootHead)

// 	 					val targetProcessors = [FloorClip, FloorSkating, ToePlant, FootPlant]
//
// 	 					val targets = targetProcessors
// 	 						.filter { targetProcessors -> targetProcessors.enabled }
// 	 						.fold(emptyList<Target>()) { targets, processor -> processor.process(fk, targets) }
//
// 	 					val ikOutput = solver.solve(fk, targets)

						// Frame time tracking ends and restarts here
						lastFrameTime = frameStartTime.elapsedNow()
						frameStartTime = timeSource.markNow()

						// Updated the computed skeleton with the result
						if (!targetState.paused) { // FIXME : bones should still follow the head when paused
							receiver.computed.value = ComputedSkeleton(fk, lastFrameTime)
// 							receiver.computed.value = ComputedSkeleton(ikOutput, lastFrameTime)
						}
					}
					// Process ends

					// Wait the remainder of last process
					val delayDuration = intervalDuration - processTime
					if (delayDuration <= Duration.ZERO) {
						// Skeleton took to long to compute this frame
						processTooLongCount++
						if (nextLogTime.hasPassedNow()) {
							AppLogger.skeleton.warn("Couldn't reach ${hz}Hz $processTooLongCount ${if (processTooLongCount > 1) "times" else "time"} in the last $logSpamWait")
							processTooLongCount = 0
							nextLogTime = timeSource.markNow() + logSpamWait
						}
					}
					delay(delayDuration.coerceAtLeast(minimumDelay))
				} catch (e: CancellationException) {
					throw e
				} catch (e: Exception) {
					AppLogger.coroutines.error(e, "Error in ComputedSkeletonBehaviour")
				}
			}
		}
	}
}
