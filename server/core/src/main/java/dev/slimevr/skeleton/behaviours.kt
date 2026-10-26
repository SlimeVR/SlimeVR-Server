package dev.slimevr.skeleton

import dev.slimevr.config.Settings
import dev.slimevr.config.UserConfig
import dev.slimevr.logging.AppLogger
import dev.slimevr.util.MonotonicValueTimeMark
import dev.slimevr.util.PreciseWaiter
import dev.slimevr.util.millisecondsInSecond
import dev.slimevr.util.timeSource
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.ResetType
import java.util.EnumMap
import java.util.concurrent.Executors
import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime

class ProportionsBehaviour(private val userConfig: UserConfig) : SkeletonBehaviour {
	override fun observe(receiver: Skeleton) {
		userConfig.context.state
			.map { it.data.proportions }
			.distinctUntilChanged()
			.onEach { proportions ->
				receiver.context.dispatch(SkeletonActions.SetProportions(configToBoneValues(proportions)))
				receiver.context.dispatch(SkeletonActions.RequestProcessorReset(ResetType.FULL))
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
class LocalizerResetBehaviour(val settings: Settings) : SkeletonBehaviour {
	override fun observe(receiver: Skeleton) {
		settings.context.state.distinctUntilChangedBy { it.data.skeletonConfig.toggles.mocapMode }.onEach {
			if (!it.data.skeletonConfig.toggles.mocapMode) receiver.context.dispatch(SkeletonActions.ResetHeadPosition)
		}.launchIn(receiver.context.scope)
	}
}

class YouSpinMeRightRoundBehaviour(val inputHz: Int = 1) : SkeletonBehaviour {
	override fun observe(receiver: Skeleton) {
		receiver.context.scope.launch {
			val intervalMs = (millisecondsInSecond / inputHz.toFloat()).toLong()
			val startTime = timeSource.markNow()
			while (true) {
				delay(intervalMs)
				val elapsed = startTime.elapsedNow().inWholeMilliseconds / millisecondsInSecond
				val state = receiver.context.state.value

				receiver.context.dispatch(
					SkeletonActions.SetBonePose(
						BodyPart.LOWER_CHEST,
						Vector3.ZERO,
						inputHz.toUShort(),
						Quaternion.fromRotationVector(Vector3(cos(elapsed), sin(elapsed), 0f)),
						Vector3.ZERO,
						null,
					),
				)
				receiver.context.dispatch(
					SkeletonActions.SetBonePose(
						BodyPart.LEFT_LOWER_LEG,
						Vector3.ZERO,
						inputHz.toUShort(),
						Quaternion.fromRotationVector(Vector3(cos(elapsed + 1000), sin(elapsed + 1000), 0f)),
						Vector3.ZERO,
						null,
					),
				)

				val circleRadius = 0.5f
				val circleX = cos(elapsed * 2f) * circleRadius
				val circleZ = sin(elapsed * 2f) * circleRadius
				val jumpHeight = maxOf(0f, sin(elapsed * 3f) * 0.3f)
				receiver.context.dispatch(
					SkeletonActions.SetBonePose(
						BodyPart.HEAD,
						Vector3.ZERO,
						inputHz.toUShort(),
						Quaternion.IDENTITY,
						Vector3.ZERO,
						Vector3(circleX, state.skeletonHeight + jumpHeight, circleZ),
					),
				)
			}
		}
	}
}

private class TickTimings(private val hz: Int, private val window: Duration, private val target: Duration) {
	private val capacity = (hz * window.inWholeSeconds + hz).toInt()
	private val computeNanos = LongArray(capacity)
	private val intervalNanos = LongArray(capacity)
	private var count = 0
	private var lastStart = timeSource.markNow()
	private var sinceLastStart = Duration.ZERO
	private var nextReport = timeSource.markNow() + window
	private var overruns = 0
	private var nextOverrunReport = timeSource.markNow() + OVERRUN_WINDOW

	/** Called with the start of a tick, so the gap between two starts is what gets measured */
	fun started(at: MonotonicValueTimeMark) {
		sinceLastStart = at - lastStart
		lastStart = at
	}

	fun finished(compute: Duration) {
		if (count == capacity) return
		computeNanos[count] = compute.inWholeNanoseconds
		intervalNanos[count] = sinceLastStart.inWholeNanoseconds
		count++
	}

	/** The line to log once a window has passed, or null while one is still filling */
	fun report(): String? {
		if (!AppLogger.ShouldDebug.skeletonTicks) return null
		if (!nextReport.hasPassedNow()) return null
		val compute = computeNanos.copyOf(count).also { it.sort() }
		val interval = intervalNanos.copyOf(count).also { it.sort() }
		count = 0
		nextReport = timeSource.markNow() + window
		return "Skeleton tick over ${compute.size} frames: " +
			"compute p50 %.0fus p99 %.0fus max %.0fus | interval p50 %.0fus p99 %.0fus min %.0fus max %.0fus, target ${target.inWholeMicroseconds}us".format(
				micros(compute, 0.5),
				micros(compute, 0.99),
				micros(compute, 1.0),
				micros(interval, 0.5),
				micros(interval, 0.99),
				micros(interval, 0.0),
				micros(interval, 1.0),
			)
	}

	fun missedDeadline(): String? {
		overruns++
		if (!nextOverrunReport.hasPassedNow()) return null
		val line = "Couldn't reach ${hz}Hz $overruns times in the last $OVERRUN_WINDOW".takeIf { overruns >= MINIMUM_OVERRUNS_TO_LOG }
		overruns = 0
		nextOverrunReport = timeSource.markNow() + OVERRUN_WINDOW
		return line
	}

	private fun micros(sortedNanos: LongArray, percentile: Double): Double = sortedNanos[((sortedNanos.size - 1) * percentile).toInt()] / 1000.0

	companion object {
		private val OVERRUN_WINDOW = 2.minutes
		private const val MINIMUM_OVERRUNS_TO_LOG = 600 // 1% of skeleton ticks
	}
}

class ComputedSkeletonBehaviour(
	val hz: Int,
	val inputProcessors: List<SkeletonInputProcessor> = emptyList(),
	val fkComputedProcessors: List<SkeletonComputedProcessor> = emptyList(),
	val fkProcessors: List<SkeletonFkProcessor> = emptyList(),
	val targetProcessors: List<SkeletonTargetProcessor> = emptyList(),
	val ikComputedProcessors: List<SkeletonComputedProcessor> = emptyList(),
	val waiter: PreciseWaiter,
) : SkeletonBehaviour {
	// BodyParts that are active even when tracking is paused
	private val ALWAYS_ACTIVE_PARTS = arrayOf(BodyPart.HEAD, BodyPart.NECK)

	private val IK_THRESHOLD = 0.002f
	private val IK_MAX_ITERATIONS = 100

	private val intervalDuration = (1.0 / hz).seconds

	/**
	 * Interval while no bone has an active input. The pose is static then, so running at [hz] only
	 * burns CPU.
	 */
	private val idleIntervalDuration = 100.milliseconds

	/** Shortest gap between two tick starts before the later one is pushed to the next slot */
	private val minimumGap = intervalDuration * 0.75

	/**
	 * The first slot on the tick grid falling at least [minimumGap] after [tickStart].
	 *
	 * Ticks stay on the grid they started on so the rate doesn't drift, and a tick that ran late
	 * gives up its own slot rather than letting the next one fire early to catch up. The gap is
	 * measured from when the tick actually ran, which always trails its deadline by the wake
	 * latency, so only a materially short gap costs a slot.
	 */
	private fun slotAfter(slot: MonotonicValueTimeMark, tickStart: MonotonicValueTimeMark): MonotonicValueTimeMark {
		var next = slot
		while (next - tickStart < minimumGap) next += intervalDuration
		return next
	}

	private fun hasActiveInput(boneInputs: InputSkeleton): Boolean = boneInputs.values.any { it.isRotationActive || it.isAccelerationActive || it.isPositionActive }

	override fun observe(receiver: Skeleton) {
		// The loop parks its thread to hit its interval, so it gets one to itself. Sharing would
		// starve everything else on the dispatcher, since parking blocks the thread rather than
		// suspending the coroutine
		val dispatcher = Executors.newSingleThreadExecutor { runnable ->
			Thread(runnable, "Skeleton").apply { isDaemon = true }
		}.asCoroutineDispatcher()
		receiver.context.scope.coroutineContext[Job]?.invokeOnCompletion { dispatcher.close() }

		val resettableProcessors = (inputProcessors + fkComputedProcessors + fkProcessors + targetProcessors + ikComputedProcessors)
			.filterIsInstance<ResettableSkeletonProcessor>()
			.distinct()

		var nextTick = timeSource.markNow()
		val fkChangedParts = mutableSetOf<BodyPart>()
		val timings = TickTimings(hz, 10.seconds, intervalDuration)

		// Build a single empty map of BoneStates (FK/IK result) for output that we'll mutate for performance
		val mutableComputedSkeleton: ComputedSkeleton = bodyPartMap()

		receiver.context.scope.launch(dispatcher) {
			while (true) {
				try {
					val tickStart = timeSource.markNow()
					timings.started(tickStart)
					val interval = if (hasActiveInput(receiver.context.state.value.boneInputs)) intervalDuration else idleIntervalDuration

					val processTime = measureTime {
						val targetState = receiver.context.state.value

						val pendingResets = targetState.processorResets
						if (pendingResets.isNotEmpty()) {
							for (resetType in pendingResets.distinct()) {
								for (processor in resettableProcessors) processor.reset(resetType)
							}
							receiver.context.dispatch(SkeletonActions.ProcessorResetsApplied(pendingResets.size))
						}

						// Use single map for the inputs that we'll mutate for performance
						val mutableBoneInputs: InputSkeleton = BodyPartMap(targetState.boneInputs)

						// Run pre-FK processors (input processors)
						for (processor in inputProcessors) processor.process(mutableBoneInputs, targetState.skeletonHeight)

						// mutableComputedSkeleton is reused across frames
						mutableComputedSkeleton.clear()

						// Run initial FK
						buildBones(mutableComputedSkeleton, mutableBoneInputs)

						// These write into fk, not the inputs, and the rebuilds below carry their values forward
						for (processor in fkComputedProcessors) processor.process(mutableComputedSkeleton, mutableBoneInputs)

						// Run FK processors. They write into boneInputs.
						// beforeFk allows figuring out which bones changed to re-run FK on.
						val beforeFk = BodyPartMap(mutableBoneInputs)
						for (processor in fkProcessors) {
							processor.process(mutableBoneInputs, mutableComputedSkeleton, targetState.floorLevel)

							// Comparing and filtering the maps allocates an entry per bone each time, so
							// the changed set is collected in one walk instead
							fkChangedParts.clear()
							for ((bodyPart, boneInput) in mutableBoneInputs) {
								val previous = beforeFk[bodyPart]
								if (boneInput == previous) continue
								fkChangedParts.add(bodyPart)
								beforeFk[bodyPart] = boneInput

								// If head changed position, set the new position in state (for Localizer).
								if (bodyPart == BodyPart.HEAD && !boneInput.isPositionActive && boneInput.position != previous?.position) {
									receiver.context.dispatch(SkeletonActions.SetBonePose(BodyPart.HEAD, Vector3.ZERO, null, null, null, boneInput.position, false))
								}
							}
							// Inputs changed; re-run FK
							if (fkChangedParts.isNotEmpty()) buildBones(mutableComputedSkeleton, mutableBoneInputs, fkChangedParts)
						}

						// Run IK processors
						val ikTargets = bodyPartMap<Vector3>()
						for (processor in targetProcessors) processor.process(ikTargets, mutableBoneInputs, mutableComputedSkeleton, targetState.floorLevel)

						// Run IK
						ccdIk(
							mutableBoneInputs,
							mutableComputedSkeleton,
							ikTargets.map { (bodyPart, target) ->
								IKChainGoal(
									BODY_PART_IK_CHAIN_MAP[bodyPart] ?: listOf(bodyPart),
									target,
								)
							},
							BODY_PART_CONSTRAINT_MAP,
							IK_THRESHOLD,
							IK_MAX_ITERATIONS,
						)

						// Save or restore paused tracking
						if (targetState.pausedProcessedBoneInputs != null) {
							// Merge paused inputs from state into our current inputs
							for ((bodyPart, bone) in targetState.pausedProcessedBoneInputs) {
								if (bodyPart !in ALWAYS_ACTIVE_PARTS) {
									mutableBoneInputs[bodyPart] = bone
								}
							}
							buildBones(mutableComputedSkeleton, mutableBoneInputs)
						} else if (targetState.paused) {
							// We are paused but don't have a copy of the inputs in state, so we make one.
							// This is our last loop processing all inputs.
							receiver.context.dispatch(SkeletonActions.SetPausedBoneInputs(EnumMap(mutableBoneInputs)))
						}

						// Run ik computed processors. These are used to update values for consumers (e.g. drivers wants velocity after IK)
						for (processor in ikComputedProcessors) processor.process(mutableComputedSkeleton, mutableBoneInputs)

						// Updated the computed skeleton with a copy of the result
						receiver.computed.tryEmit(BodyPartMap(mutableComputedSkeleton))
					}

					timings.finished(processTime)
					timings.report()?.let { AppLogger.skeleton.info(it) }

					// Sleep to an absolute deadline, so timer granularity and dispatch latency come out
					// of the interval rather than being added on top of it
					nextTick += interval
					// Skeleton took to long to compute this frame
					if (nextTick.elapsedNow() >= Duration.ZERO) {
						timings.missedDeadline()?.let { AppLogger.skeleton.warn(it) }
					}
					nextTick = slotAfter(nextTick, tickStart)
					var remaining = -nextTick.elapsedNow()
					while (remaining > Duration.ZERO) {
						waiter.sleep(remaining)
						coroutineContext.ensureActive()
						remaining = -nextTick.elapsedNow()
					}
				} catch (e: CancellationException) {
					throw e
				} catch (e: Exception) {
					AppLogger.coroutines.error(e, "Error in ComputedSkeletonBehaviour")
				}
			}
		}
	}
}
