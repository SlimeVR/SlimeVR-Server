package dev.slimevr.skeleton

import dev.slimevr.config.UserConfig
import dev.slimevr.logging.AppLogger
import dev.slimevr.util.MonotonicValueTimeMark
import dev.slimevr.util.PreciseWaiter
import dev.slimevr.util.timeSource
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import solarxr_protocol.datatypes.BodyPart
import java.util.EnumMap
import java.util.concurrent.Executors
import java.util.concurrent.locks.LockSupport
import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
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
		private val OVERRUN_WINDOW = 1.minutes
		private const val MINIMUM_OVERRUNS_TO_LOG = 10
	}
}

class ComputedSkeletonBehaviour(
	val hz: Int,
	val inputProcessors: List<SkeletonInputProcessor> = emptyList(),
	val computedProcessors: List<SkeletonComputedProcessor> = emptyList(),
	val fkProcessors: List<SkeletonFkProcessor> = emptyList(),
	val targetProcessors: List<SkeletonTargetProcessor> = emptyList(),
	val waiter: PreciseWaiter
) : SkeletonBehaviour {
	private val intervalDuration = (1.0 / hz).seconds

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

	/**
	 * Runs [processors] over a copy of [boneInputs].
	 *
	 * The processors share one buffer and write into it, so the copy here is what keeps the caller's
	 * map, and the state it came from, untouched.
	 */
	private fun runInputProcessors(processors: List<SkeletonInputProcessor>, boneInputs: InputSkeleton, skeletonHeight: Float): InputSkeleton {
		val bones = EnumMap(boneInputs)
		for (processor in processors) processor.process(bones, skeletonHeight)
		return bones
	}

	override fun observe(receiver: Skeleton) {
		// The loop parks its thread to hit its interval, so it gets one to itself. Sharing would
		// starve everything else on the dispatcher, since parking blocks the thread rather than
		// suspending the coroutine
		val dispatcher = Executors.newSingleThreadExecutor { runnable ->
			Thread(runnable, "Skeleton").apply { isDaemon = true }
		}.asCoroutineDispatcher()
		receiver.context.scope.coroutineContext[Job]?.invokeOnCompletion { dispatcher.close() }

		var nextTick = timeSource.markNow()
		val fkChangedParts = mutableSetOf<BodyPart>()
		val timings = TickTimings(hz, 10.seconds, intervalDuration)

		receiver.context.scope.launch(dispatcher) {
			while (true) {
				try {
					val tickStart = timeSource.markNow()
					timings.started(tickStart)

					val processTime = measureTime {
						val targetState = receiver.context.state.value

						val boneInputs = if (targetState.pausedProcessedBoneInputs != null) {
							// TODO improve pause tracking code
							//  and also possibly head default position (HeadPositionFallbackProcessor)
							// Use already-processed paused tracking data except for the head
							val headBone = targetState.boneInputs[BodyPart.HEAD]
							targetState.pausedProcessedBoneInputs.mutateCopy { it[BodyPart.HEAD] = headBone?.copy(position = if (headBone.isPositionActive) headBone.position else it[BodyPart.HEAD]?.position) }
						} else {
							// Run pre-FK processors
							// TODO: Add a constrain processor (maybe not needed)
							val processedInputs = runInputProcessors(inputProcessors, targetState.boneInputs, targetState.skeletonHeight)
							if (targetState.paused) {
								// We just paused tracking and this is the last frame before we rely on paused bone inputs
								// The buffer keeps being written after this, so state gets a copy
								receiver.context.dispatch(SkeletonActions.SetPausedBoneInputs(EnumMap(processedInputs)))
							}
							processedInputs
						}

						// Run initial FK
						var fk = buildBones(boneInputs)

						// These write into fk, not the inputs, so the rebuilds below carry their
						// values forward
						for (processor in computedProcessors) processor.process(fk, targetState.floorLevel)

						// Run FK processors. They write into boneInputs, and beforeFk allows figuring out
						// which bones changed.
						val beforeFk = BodyPartMap(boneInputs)
						for (processor in fkProcessors) {
							processor.process(boneInputs, fk, targetState.floorLevel)

							// Comparing and filtering the maps allocates an entry per bone each time, so
							// the changed set is collected in one walk instead
							fkChangedParts.clear()
							boneInputs.forEachBone { bodyPart, boneInput ->
								val previous = beforeFk[bodyPart]
								if (boneInput == previous) return@forEachBone
								fkChangedParts.add(bodyPart)
								beforeFk[bodyPart] = boneInput

								// For changed bones with inactive positions, update their input's position in state (needed for Localizer)
								if (!boneInput.isPositionActive && boneInput.position != previous?.position) {
									receiver.context.dispatch(SkeletonActions.SetBonePosition(bodyPart, boneInput.position, false))
								}
							}
							// Inputs changed; re-run FK
							if (fkChangedParts.isNotEmpty()) fk = buildBones(boneInputs, fkChangedParts, fk)
						}

						// Run IK processors
						val ikTargets = bodyPartMap<Vector3>()
						for (processor in targetProcessors) processor.process(ikTargets, fk, targetState.floorLevel)

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

					timings.finished(processTime)
					timings.report()?.let { AppLogger.skeleton.info(it) }

					// Sleep to an absolute deadline, so timer granularity and dispatch latency come out
					// of the interval rather than being added on top of it
					nextTick += intervalDuration
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
