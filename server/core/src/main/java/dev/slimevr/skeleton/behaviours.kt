package dev.slimevr.skeleton

import dev.slimevr.config.UserConfig
import dev.slimevr.logging.AppLogger
import dev.slimevr.util.timeSource
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChangedBy
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

class ProportionsBehaviour(private val userConfig: UserConfig) : SkeletonBehaviour {
	override fun observe(receiver: Skeleton) {
		userConfig.context.state
			.distinctUntilChangedBy { it.data.proportions }
			.map { it.data.proportions }
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
				.distinctUntilChangedBy { it.skeletonHeight }
				.map { it.skeletonHeight }
				.collect { height -> AppLogger.skeleton.info("User height changed: ${"%.2f".format(height)}m") }
		}
	}
}

/**
 * Handles resetting the head position whenever mocap mode gets disabled
 */
class LocalizerResetBehaviour : SkeletonBehaviour {
	override fun observe(receiver: Skeleton) {
		receiver.settings.context.state.distinctUntilChangedBy { it.data.skeletonConfig.toggles.mocapMode }.onEach {
			if (!it.data.skeletonConfig.toggles.mocapMode) receiver.context.dispatch(SkeletonActions.ResetHeadPosition)
		}.launchIn(receiver.context.scope)
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

class ComputedSkeletonBehaviour(
	val hz: Int,
	val inputProcessors: List<SkeletonInputProcessor> = emptyList(),
	val fkProcessors: List<SkeletonFkProcessor> = emptyList(),
	val targetProcessors: List<SkeletonTargetProcessor> = emptyList(),
) : SkeletonBehaviour {
	private val intervalDuration = (1.0 / hz).seconds
	private val minimumDelay = 1.nanoseconds
	private val logSpamWait = 1.minutes
	private val minimumFramesToLog = 10

	override fun observe(receiver: Skeleton) {
		var nextLogTime = timeSource.markNow() + logSpamWait
		var processTooLongCount = 0

		receiver.context.scope.launch {
			while (true) {
				try {
					val processTime = measureTime {
						val targetState = receiver.context.state.value

						// TODO find out a cleaner way to pause tracking that doesn't add 1 frame latency (not a priority)
						val boneInputs = if (targetState.pausedBoneInputs != null) {
							// Use already-processed paused tracking data except for the head
							targetState.pausedBoneInputs.mutateCopy { it[BodyPart.HEAD] = targetState.boneInputs[BodyPart.HEAD] }
						} else {
							// Run pre-FK processors
							// TODO: Add a constrain processor (maybe not needed)
							val processedInputs = inputProcessors.fold(targetState.boneInputs) { boneInputs, processor -> processor.process(boneInputs, targetState.skeletonHeight) }
							if (targetState.paused) {
								// We just paused tracking and this is the last frame before we rely on paused bone inputs
								receiver.context.dispatch(SkeletonActions.SetPausedBoneInputs(processedInputs))
							}
							processedInputs
						}

						// Run initial FK
						var fk = buildBones(boneInputs)

						// Run FK processors
						fkProcessors.fold((boneInputs)) { inputs, processor ->
							// Process new inputs from
							val newInputs = processor.process(inputs, fk, targetState.floorLevel)
							if (newInputs != inputs) {
								// Inputs changed; re-run FK
								// TODO only run FK for what changed `val changedInputs = newInputs.filter { (part, boneInput) -> boneInput != inputs.getValue(part) }`
								fk = buildBones(newInputs)

								// For bones with inactive position, update their input's position in state (needed for Localizer)
								for (input in newInputs.filter { (part, boneInput) -> boneInput.rawPosition != inputs.getValue(part).rawPosition }) {
									receiver.context.dispatch(SkeletonActions.SetBonePosition(input.key, input.value.rawPosition, false))
								}
							}
							newInputs
						}

						// Run IK processors
						val ikTargets = targetProcessors.fold(bodyPartMap<Vector3>()) { targets, processor -> processor.process(fk, targets, targetState.floorLevel) }

						// Run IK
						val ikOutput = ccdIk(
							boneInputs,
							fk,
							ikTargets.map { (bodyPart, target) ->
								IKChainGoal(
									BODY_PART_IK_CHAIN_MAP[bodyPart] ?: listOf(bodyPart),
									target,
								)
							},
							BODY_PART_CONSTRAINT_MAP,
							0.01f,
							100,
						)

						// Updated the computed skeleton with the result
						receiver.computed.tryEmit(ikOutput.bones)
					}

					// Wait the remainder of last process
					val delayDuration = intervalDuration - processTime
					if (delayDuration <= Duration.ZERO) {
						// Skeleton took to long to compute this frame
						processTooLongCount++
						if (nextLogTime.hasPassedNow()) {
							if (processTooLongCount >= minimumFramesToLog) {
								AppLogger.skeleton.warn("Couldn't reach ${hz}Hz $processTooLongCount times in the last $logSpamWait")
							}
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
