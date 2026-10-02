package dev.slimevr.stepmounting

import dev.slimevr.logging.AppLogger
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import dev.slimevr.tracker.TrackerState
import dev.slimevr.util.ButterworthCoefficients
import dev.slimevr.util.Vector3Butterworth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.withTimeoutOrNull
import solarxr_protocol.datatypes.BodyPart
import kotlin.time.TimeSource

internal const val TIMEOUT_MS = 10_000L

class StepMountingBasicBehaviour : StepMountingBehaviour {
	fun canCalibrate(trackers: List<TrackerState>): Boolean {
		val hasPositionalHead = trackers.any { it.bodyPart == BodyPart.HEAD && it.position != null }
		return hasPositionalHead
	}

	@OptIn(ExperimentalCoroutinesApi::class)
	override fun observe(receiver: StepMountingManager) {
		receiver.server.context.state
			.flatMapLatest { state ->
				val trackers = state.trackers.values.toList()
				if (trackers.isEmpty()) return@flatMapLatest flowOf(false)
				// React to per-tracker position/bodyPart changes, not just tracker add/remove. canCalibrate
				// only looks at bodyPart and whether position is set, so dedup on that per tracker rather
				// than letting every rotation packet resume the combine.
				combine(
					trackers.map { tracker ->
						tracker.context.state.distinctUntilChanged { a, b ->
							a.bodyPart == b.bodyPart && (a.position == null) == (b.position == null)
						}
					},
				) { states -> canCalibrate(states.toList()) }
			}
			.distinctUntilChanged()
			.onEach { receiver.context.dispatch(StepMountingActions.SetCanCalibrate(it)) }
			.launchIn(receiver.context.scope)
	}
}

val coefficients = ButterworthCoefficients(
	8f,
	0.02f,
)
const val startThreshold = 0.4f // in m/s^2
const val endThreshold = 0.3f // in m/s^2
const val minMovementDurationMs = 3000L

fun movementDetector(updates: Flow<TrackerSnapshot>) = flow {
	val lowpass = Vector3Butterworth(coefficients)
	emit(0f)
	updates.collect { update ->
		val lowpassAccel = lowpass.filter(update.acceleration)
		emit((update.acceleration - lowpassAccel).len())
	}
}

internal suspend fun runCalibrationSession(
	context: StepMountingContext,
	headUpdates: Flow<HeadsetSnapshot>,
	trackers: List<Pair<Tracker, Flow<TrackerSnapshot>>>,
	clock: TimeSource.WithComparableMarks = TimeSource.Monotonic,
) {
	val headRecording = ArrayList<HeadRecordingSample>(1024)
	val trackerRecordings = ArrayList<Pair<Tracker, ArrayList<RecordingSample>>>(trackers.size)

	fun dispatch(status: StepMountingStatus) {
		context.dispatch(StepMountingActions.Update(status))
	}

	dispatch(StepMountingStatus.WAITING_FOR_MOVEMENT)
	AppLogger.stepMounting.info("Waiting for movement...")

	withTimeoutOrNull(TIMEOUT_MS) {
		val isMoving = combine(
			trackers.map { (_, updates) ->
				movementDetector(updates)
			},
		) { updates ->
			// Max tracker movement
			updates.max()
		}.stateIn(this)

		// Wait for movement to start
		isMoving.first { it >= startThreshold }

		dispatch(StepMountingStatus.RECORDING)
		AppLogger.stepMounting.info("Movement detected, recording...")

		// Collect head and tracker data
		headUpdates.takeWhile {
			context.state.value.status == StepMountingStatus.RECORDING
		}.onEach { update ->
			headRecording.add(
				HeadRecordingSample(
					time = clock.markNow(),
					position = update.position,
				),
			)
		}.launchIn(this)
		for ((tracker, updates) in trackers) {
			val recording = ArrayList<RecordingSample>(1024)
			trackerRecordings.add(tracker to recording)

			updates.takeWhile {
				context.state.value.status == StepMountingStatus.RECORDING
			}.onEach { update ->
				recording.add(
					RecordingSample(
						time = clock.markNow(),
						accel = update.acceleration,
					),
				)
			}.launchIn(this)
		}

		// Wait for movement to end
		delay(minMovementDurationMs)
		isMoving.first { it < endThreshold }

		dispatch(StepMountingStatus.PROCESSING)
		AppLogger.stepMounting.info("No more movement detected, processing...")

		if (headRecording.isEmpty() || trackerRecordings.isEmpty()) {
			dispatch(StepMountingStatus.ERROR_NO_DATA)
			error("No data found.")
		}

		val headOffset = headRecording.last().position - headRecording.first().position
		AppLogger.stepMounting.info("${BodyPart.HEAD}: $headOffset")
		trackerRecordings.filter {
			it.second.isNotEmpty()
		}.map { (tracker, recording) ->
			tracker to estimateHeadingAlign(recording, headOffset)
		}.forEach { (tracker, result) ->
			// TODO: Fail on high error
			AppLogger.stepMounting.info("${tracker.context.state.value.bodyPart}: $result")

			tracker.context.dispatch(
				TrackerActions.SetStepMounting(
					result.headingAlignment,
				),
			)
		}

		dispatch(StepMountingStatus.DONE)
		AppLogger.stepMounting.info("Done!")
	} ?: dispatch(StepMountingStatus.ERROR_TIMEOUT)
}
