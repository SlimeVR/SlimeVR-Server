package dev.slimevr.stepmounting

import dev.slimevr.logging.AppLogger
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import dev.slimevr.tracker.TrackerState
import dev.slimevr.util.ButterworthCoefficients
import dev.slimevr.util.Vector3Butterworth
import dev.slimevr.util.allContextStates
import dev.slimevr.util.inFloatingSeconds
import dev.slimevr.util.timeSource
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.withTimeoutOrNull
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.StepMountingStatus
import kotlin.time.ComparableTimeMark
import kotlin.time.TimeSource

class StepMountingBasicBehaviour : StepMountingBehaviour {
	fun canCalibrate(trackers: List<TrackerState>): Boolean {
		val hasPositionalHead = trackers.any { it.bodyPart == BodyPart.HEAD && it.position != null }
		return hasPositionalHead
	}

	override fun observe(receiver: StepMountingManager) {
		// React to per-tracker position/bodyPart changes, not just tracker add/remove. canCalibrate
		// only looks at bodyPart and whether position is set, so dedup on that per tracker rather
		// than letting every rotation packet resume the combine.
		allContextStates(receiver.server.context.state, { it.trackers.values }) { tracker ->
			tracker.context.state.distinctUntilChanged { old, new ->
				old.bodyPart == new.bodyPart && (old.position == null) == (new.position == null)
			}
		}
			.map(::canCalibrate)
			.distinctUntilChanged()
			.onEach { receiver.context.dispatch(StepMountingActions.SetCanCalibrate(it)) }
			.launchIn(receiver.context.scope)
	}
}

const val TIMEOUT_MS = 25_000L
const val WAIT_TIMEOUT_MS = 10_000L
const val RECORD_TIMEOUT_MS = 8_000L

val COEFFICIENTS = ButterworthCoefficients(
	1.3f,
	0.02f,
)
const val START_THRESHOLD = 0.4f // in m/s^2
const val END_THRESHOLD = 0.3f // in m/s^2
const val MIN_MOVEMENT_DURATION_MS = 3000L
const val ERROR_THRESHOLD = 0.3f

fun movementDetector(updates: Flow<TrackerSnapshot>) = flow {
	val lowpass = Vector3Butterworth(COEFFICIENTS)
	var lastTime: ComparableTimeMark? = null
	emit(0f)
	updates.collect { update ->
		// Dynamically adjust lowpass timestep
		val delta = lastTime?.elapsedNow()?.inFloatingSeconds ?: COEFFICIENTS.Ts
		lastTime = timeSource.markNow()
		lowpass.swapCoefficients(COEFFICIENTS.copy(Ts = delta))

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
		withTimeoutOrNull(WAIT_TIMEOUT_MS) {
			isMoving.first { it >= START_THRESHOLD }
		} ?: run {
			dispatch(StepMountingStatus.ERROR_TIMEOUT)
			return@withTimeoutOrNull
		}

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
		withTimeoutOrNull(RECORD_TIMEOUT_MS) {
			delay(MIN_MOVEMENT_DURATION_MS)
			isMoving.first { it < END_THRESHOLD }
		} ?: run {
			dispatch(StepMountingStatus.ERROR_TIMEOUT)
			return@withTimeoutOrNull
		}

		dispatch(StepMountingStatus.PROCESSING)
		AppLogger.stepMounting.info("No more movement detected, processing...")

		if (headRecording.isEmpty() || trackerRecordings.isEmpty()) {
			dispatch(StepMountingStatus.ERROR_NO_DATA)
			return@withTimeoutOrNull
		}

		val headOffset = headRecording.last().position - headRecording.first().position
		AppLogger.stepMounting.info("${BodyPart.HEAD}: $headOffset")
		val results = trackerRecordings.filter {
			it.second.isNotEmpty()
		}.map { (tracker, recording) ->
			tracker to estimateHeadingAlign(recording, headOffset)
		}.onEach { (tracker, result) ->
			AppLogger.stepMounting.info("${tracker.context.state.value.bodyPart}: $result")
		}

		// Fail on high error
		results.firstOrNull { (_, result) ->
			result.errorMeters >= ERROR_THRESHOLD
		}?.let { (tracker, result) ->
			dispatch(StepMountingStatus.ERROR_THRESHOLD_EXCEEDED)
			AppLogger.stepMounting.error("Tracker assigned to ${tracker.context.state.value.bodyPart} exceeded the error threshold (${result.errorMeters}m >= ${ERROR_THRESHOLD}m).")
			return@withTimeoutOrNull
		}

		// Apply to trackers
		results.forEach { (tracker, result) ->
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
