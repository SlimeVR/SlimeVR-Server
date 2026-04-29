@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class, kotlinx.coroutines.FlowPreview::class)

package dev.slimevr.heightcalibration

import dev.slimevr.buildTestUserConfig
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.currentTime
import kotlinx.coroutines.test.runTest
import solarxr_protocol.rpc.UserHeightCalibrationStatus
import kotlin.test.Test
import kotlin.test.assertEquals

// Stability durations in milliseconds (converted from nanosecond constants)
private const val FLOOR_STABILITY_MS = CONTROLLER_STABILITY_DURATION / 1_000_000L
private const val HEIGHT_STABILITY_MS = HEAD_STABILITY_DURATION / 1_000_000L

// Rotation that maps controller forward (-Z) to down (-Y), satisfying the pointing-down check
private val POINTING_DOWN = Quaternion.fromTo(Vector3.NEG_Z, Vector3.NEG_Y)

// Identity: controller forward is -Z, not pointing down
private val POINTING_FORWARD = Quaternion.IDENTITY

// Identity: HMD up is +Y, within 15° leveled threshold
private val HMD_LEVEL = Quaternion.IDENTITY

// 90° around Z: HMD up maps to +X, failing the leveled check
private val HMD_TILTED = Quaternion.fromTo(Vector3.POS_Y, Vector3.POS_X)

// Position just below the floor threshold (0.10m)
private val FLOOR_POSITION = Vector3(0f, 0.05f, 0f)

// Position comfortably above rise threshold (1.2m) with zero floor level
private val STANDING_POSITION = Vector3(0f, 1.7f, 0f)

private fun makeContext(scope: kotlinx.coroutines.CoroutineScope) = HeightCalibrationContext.create(
	initialState = INITIAL_HEIGHT_CALIBRATION_STATE,
	scope = scope,
	behaviours = listOf(CalibrationBehaviour),
	name = "HeightCalibrationTest",
)

// Launches a calibration session with the virtual-time clock, so that advancing virtual time
// drives both the sample() operator and the stability duration checks in one unified timeline.
private fun TestScope.launchSession(
	context: HeightCalibrationContext,
	hmdFlow: MutableSharedFlow<TrackerSnapshot>,
	controllerFlow: MutableSharedFlow<TrackerSnapshot>,
): Job {
	val scope = this
	val userConfig = buildTestUserConfig(backgroundScope)
	return launch {
		runCalibrationSession(context, userConfig, hmdFlow, controllerFlow, clock = { scope.currentTime * 1_000_000L })
	}
}

// Emits snapshot at SAMPLE_INTERVAL_MS rate for the given duration of virtual time,
// then does one extra advance to let any pending coroutine resumptions finish.
private suspend fun TestScope.emitFor(
	flow: MutableSharedFlow<TrackerSnapshot>,
	snapshot: TrackerSnapshot,
	durationMs: Long,
) {
	val end = currentTime + durationMs
	while (currentTime < end) {
		flow.emit(snapshot)
		advanceTimeBy(SAMPLE_INTERVAL_MS)
	}
	advanceTimeBy(SAMPLE_INTERVAL_MS)
}

// Holds the controller steady on the floor long enough for the floor phase to complete.
private suspend fun TestScope.completeFloorPhase(
	controllerFlow: MutableSharedFlow<TrackerSnapshot>,
	floorSnapshot: TrackerSnapshot,
) {
	emitFor(controllerFlow, floorSnapshot, FLOOR_STABILITY_MS + SAMPLE_INTERVAL_MS * 5)
}

// Holds the HMD steady at standing height long enough for the height phase to complete.
private suspend fun TestScope.completeHeightPhase(
	hmdFlow: MutableSharedFlow<TrackerSnapshot>,
	hmdSnapshot: TrackerSnapshot,
) {
	emitFor(hmdFlow, hmdSnapshot, HEIGHT_STABILITY_MS + SAMPLE_INTERVAL_MS * 5)
}

class HeightCalibrationReducerTest {
	@Test
	fun `Update changes status and height`() = runTest {
		val context = makeContext(this)

		context.dispatch(HeightCalibrationActions.Update(UserHeightCalibrationStatus.RECORDING_HEIGHT, 1.65f))

		assertEquals(UserHeightCalibrationStatus.RECORDING_HEIGHT, context.state.value.status)
		assertEquals(1.65f, context.state.value.currentHeight)
	}
}

class HeightCalibrationSessionTest {
	@Test
	fun `session starts in RECORDING_FLOOR`() = runTest {
		val context = makeContext(this)
		val controllerFlow = MutableSharedFlow<TrackerSnapshot>(extraBufferCapacity = 1)
		val hmdFlow = MutableSharedFlow<TrackerSnapshot>(extraBufferCapacity = 1)
		val job = launchSession(context, hmdFlow, controllerFlow)
		advanceTimeBy(SAMPLE_INTERVAL_MS)

		assertEquals(UserHeightCalibrationStatus.RECORDING_FLOOR, context.state.value.status)
		job.cancel()
	}

	@Test
	fun `controller too high does not change status`() = runTest {
		val context = makeContext(this)
		val controllerFlow = MutableSharedFlow<TrackerSnapshot>(extraBufferCapacity = 1)
		val hmdFlow = MutableSharedFlow<TrackerSnapshot>(extraBufferCapacity = 1)
		val job = launchSession(context, hmdFlow, controllerFlow)
		advanceTimeBy(SAMPLE_INTERVAL_MS)

		emitFor(controllerFlow, TrackerSnapshot(Vector3(0f, 0.5f, 0f), POINTING_DOWN), SAMPLE_INTERVAL_MS)

		assertEquals(UserHeightCalibrationStatus.RECORDING_FLOOR, context.state.value.status)
		job.cancel()
	}

	@Test
	fun `controller not pointing down transitions to WAITING_FOR_CONTROLLER_PITCH`() = runTest {
		val context = makeContext(this)
		val controllerFlow = MutableSharedFlow<TrackerSnapshot>(extraBufferCapacity = 1)
		val hmdFlow = MutableSharedFlow<TrackerSnapshot>(extraBufferCapacity = 1)
		val job = launchSession(context, hmdFlow, controllerFlow)
		advanceTimeBy(SAMPLE_INTERVAL_MS)

		emitFor(controllerFlow, TrackerSnapshot(FLOOR_POSITION, POINTING_FORWARD), SAMPLE_INTERVAL_MS)

		assertEquals(UserHeightCalibrationStatus.WAITING_FOR_CONTROLLER_PITCH, context.state.value.status)
		job.cancel()
	}

	@Test
	fun `stable floor transitions to WAITING_FOR_RISE`() = runTest {
		val context = makeContext(this)
		val controllerFlow = MutableSharedFlow<TrackerSnapshot>(extraBufferCapacity = 1)
		val hmdFlow = MutableSharedFlow<TrackerSnapshot>(extraBufferCapacity = 1)
		val job = launchSession(context, hmdFlow, controllerFlow)
		advanceTimeBy(SAMPLE_INTERVAL_MS)

		completeFloorPhase(controllerFlow, TrackerSnapshot(FLOOR_POSITION, POINTING_DOWN))

		assertEquals(UserHeightCalibrationStatus.WAITING_FOR_RISE, context.state.value.status)
		job.cancel()
	}

	@Test
	fun `HMD below rise threshold stays WAITING_FOR_RISE`() = runTest {
		val context = makeContext(this)
		val controllerFlow = MutableSharedFlow<TrackerSnapshot>(extraBufferCapacity = 1)
		val hmdFlow = MutableSharedFlow<TrackerSnapshot>(extraBufferCapacity = 1)
		val job = launchSession(context, hmdFlow, controllerFlow)
		advanceTimeBy(SAMPLE_INTERVAL_MS)

		completeFloorPhase(controllerFlow, TrackerSnapshot(FLOOR_POSITION, POINTING_DOWN))

		emitFor(hmdFlow, TrackerSnapshot(Vector3(0f, 0.5f, 0f), HMD_LEVEL), SAMPLE_INTERVAL_MS)

		assertEquals(UserHeightCalibrationStatus.WAITING_FOR_RISE, context.state.value.status)
		job.cancel()
	}

	@Test
	fun `HMD not leveled transitions to WAITING_FOR_FW_LOOK`() = runTest {
		val context = makeContext(this)
		val controllerFlow = MutableSharedFlow<TrackerSnapshot>(extraBufferCapacity = 1)
		val hmdFlow = MutableSharedFlow<TrackerSnapshot>(extraBufferCapacity = 1)
		val job = launchSession(context, hmdFlow, controllerFlow)
		advanceTimeBy(SAMPLE_INTERVAL_MS)

		completeFloorPhase(controllerFlow, TrackerSnapshot(FLOOR_POSITION, POINTING_DOWN))

		emitFor(hmdFlow, TrackerSnapshot(STANDING_POSITION, HMD_TILTED), SAMPLE_INTERVAL_MS)

		assertEquals(UserHeightCalibrationStatus.WAITING_FOR_FW_LOOK, context.state.value.status)
		job.cancel()
	}

	@Test
	fun `stable HMD at valid height transitions to DONE`() = runTest {
		val context = makeContext(this)
		val controllerFlow = MutableSharedFlow<TrackerSnapshot>(extraBufferCapacity = 1)
		val hmdFlow = MutableSharedFlow<TrackerSnapshot>(extraBufferCapacity = 1)
		val job = launchSession(context, hmdFlow, controllerFlow)
		advanceTimeBy(SAMPLE_INTERVAL_MS)

		completeFloorPhase(controllerFlow, TrackerSnapshot(FLOOR_POSITION, POINTING_DOWN))
		completeHeightPhase(hmdFlow, TrackerSnapshot(STANDING_POSITION, HMD_LEVEL))

		assertEquals(UserHeightCalibrationStatus.DONE, context.state.value.status)
		assertEquals(STANDING_POSITION.y - FLOOR_POSITION.y, context.state.value.currentHeight)
		job.cancel()
	}

	@Test
	fun `stable HMD below HEIGHT_MIN transitions to ERROR_TOO_SMALL`() = runTest {
		val context = makeContext(this)
		val controllerFlow = MutableSharedFlow<TrackerSnapshot>(extraBufferCapacity = 1)
		val hmdFlow = MutableSharedFlow<TrackerSnapshot>(extraBufferCapacity = 1)
		val job = launchSession(context, hmdFlow, controllerFlow)
		advanceTimeBy(SAMPLE_INTERVAL_MS)

		completeFloorPhase(controllerFlow, TrackerSnapshot(Vector3(0f, 0f, 0f), POINTING_DOWN))
		completeHeightPhase(hmdFlow, TrackerSnapshot(Vector3(0f, 1.3f, 0f), HMD_LEVEL))

		assertEquals(UserHeightCalibrationStatus.ERROR_TOO_SMALL, context.state.value.status)
		job.cancel()
	}

	@Test
	fun `stable HMD above HEIGHT_MAX transitions to ERROR_TOO_HIGH`() = runTest {
		val context = makeContext(this)
		val controllerFlow = MutableSharedFlow<TrackerSnapshot>(extraBufferCapacity = 1)
		val hmdFlow = MutableSharedFlow<TrackerSnapshot>(extraBufferCapacity = 1)
		val job = launchSession(context, hmdFlow, controllerFlow)
		advanceTimeBy(SAMPLE_INTERVAL_MS)

		completeFloorPhase(controllerFlow, TrackerSnapshot(Vector3(0f, 0f, 0f), POINTING_DOWN))
		completeHeightPhase(hmdFlow, TrackerSnapshot(Vector3(0f, 2.0f, 0f), HMD_LEVEL))

		assertEquals(UserHeightCalibrationStatus.ERROR_TOO_HIGH, context.state.value.status)
		job.cancel()
	}

	@Test
	fun `unstable floor sample resets controller stability window`() = runTest {
		val context = makeContext(this)
		val controllerFlow = MutableSharedFlow<TrackerSnapshot>(extraBufferCapacity = 1)
		val hmdFlow = MutableSharedFlow<TrackerSnapshot>(extraBufferCapacity = 1)
		val job = launchSession(context, hmdFlow, controllerFlow)
		advanceTimeBy(SAMPLE_INTERVAL_MS)

		val stableSnapshot = TrackerSnapshot(FLOOR_POSITION, POINTING_DOWN)
		// X offset of 1m pushes energy well above CONTROLLER_STABILITY_THRESHOLD
		val unstableSnapshot = TrackerSnapshot(Vector3(1f, FLOOR_POSITION.y, 0f), POINTING_DOWN)

		// Build up stability but not long enough to complete
		emitFor(controllerFlow, stableSnapshot, FLOOR_STABILITY_MS - SAMPLE_INTERVAL_MS * 5)
		assertEquals(UserHeightCalibrationStatus.RECORDING_FLOOR, context.state.value.status)

		// Unstable sample resets the stability window
		emitFor(controllerFlow, unstableSnapshot, SAMPLE_INTERVAL_MS)
		assertEquals(UserHeightCalibrationStatus.RECORDING_FLOOR, context.state.value.status)

		// Must hold stable for the full duration again from the reset point
		completeFloorPhase(controllerFlow, stableSnapshot)
		assertEquals(UserHeightCalibrationStatus.WAITING_FOR_RISE, context.state.value.status)
		job.cancel()
	}

	@Test
	fun `out-of-threshold HMD sample resets height stability window`() = runTest {
		val context = makeContext(this)
		val controllerFlow = MutableSharedFlow<TrackerSnapshot>(extraBufferCapacity = 1)
		val hmdFlow = MutableSharedFlow<TrackerSnapshot>(extraBufferCapacity = 1)
		val job = launchSession(context, hmdFlow, controllerFlow)
		advanceTimeBy(SAMPLE_INTERVAL_MS)

		completeFloorPhase(controllerFlow, TrackerSnapshot(FLOOR_POSITION, POINTING_DOWN))

		val stableSnapshot = TrackerSnapshot(STANDING_POSITION, HMD_LEVEL)
		val unstableSnapshot = TrackerSnapshot(Vector3(0f, 1.9f, 0f), HMD_LEVEL)

		// Build up stability but not long enough to complete
		emitFor(hmdFlow, stableSnapshot, HEIGHT_STABILITY_MS - SAMPLE_INTERVAL_MS * 5)
		assertEquals(UserHeightCalibrationStatus.RECORDING_HEIGHT, context.state.value.status)

		// Unstable sample resets the stability window
		emitFor(hmdFlow, unstableSnapshot, SAMPLE_INTERVAL_MS)
		assertEquals(UserHeightCalibrationStatus.RECORDING_HEIGHT, context.state.value.status)

		// Must hold stable for the full duration again from the reset point
		completeHeightPhase(hmdFlow, stableSnapshot)
		assertEquals(UserHeightCalibrationStatus.DONE, context.state.value.status)
		job.cancel()
	}

	@Test
	fun `timeout fires ERROR_TIMEOUT`() = runTest {
		val context = makeContext(this)
		val controllerFlow = MutableSharedFlow<TrackerSnapshot>(extraBufferCapacity = 1)
		val hmdFlow = MutableSharedFlow<TrackerSnapshot>(extraBufferCapacity = 1)
		val job = launch { runCalibrationSession(context, buildTestUserConfig(backgroundScope), hmdFlow, controllerFlow) }

		advanceTimeBy(TIMEOUT_MS + 1)

		assertEquals(UserHeightCalibrationStatus.ERROR_TIMEOUT, context.state.value.status)
		job.cancel()
	}

	@Test
	fun `controller pitch recovery leads to WAITING_FOR_RISE`() = runTest {
		val context = makeContext(this)
		val controllerFlow = MutableSharedFlow<TrackerSnapshot>(extraBufferCapacity = 1)
		val hmdFlow = MutableSharedFlow<TrackerSnapshot>(extraBufferCapacity = 1)
		val job = launchSession(context, hmdFlow, controllerFlow)
		advanceTimeBy(SAMPLE_INTERVAL_MS)

		// Bad pitch triggers WAITING_FOR_CONTROLLER_PITCH
		emitFor(controllerFlow, TrackerSnapshot(FLOOR_POSITION, POINTING_FORWARD), SAMPLE_INTERVAL_MS)
		assertEquals(UserHeightCalibrationStatus.WAITING_FOR_CONTROLLER_PITCH, context.state.value.status)

		// Recovery: hold steady on floor for the required duration
		completeFloorPhase(controllerFlow, TrackerSnapshot(FLOOR_POSITION, POINTING_DOWN))
		assertEquals(UserHeightCalibrationStatus.WAITING_FOR_RISE, context.state.value.status)
		job.cancel()
	}

	@Test
	fun `leveling HMD after WAITING_FOR_FW_LOOK transitions to RECORDING_HEIGHT`() = runTest {
		val context = makeContext(this)
		val controllerFlow = MutableSharedFlow<TrackerSnapshot>(extraBufferCapacity = 1)
		val hmdFlow = MutableSharedFlow<TrackerSnapshot>(extraBufferCapacity = 1)
		val job = launchSession(context, hmdFlow, controllerFlow)
		advanceTimeBy(SAMPLE_INTERVAL_MS)

		completeFloorPhase(controllerFlow, TrackerSnapshot(FLOOR_POSITION, POINTING_DOWN))

		// Tilted HMD
		emitFor(hmdFlow, TrackerSnapshot(STANDING_POSITION, HMD_TILTED), SAMPLE_INTERVAL_MS)
		assertEquals(UserHeightCalibrationStatus.WAITING_FOR_FW_LOOK, context.state.value.status)

		// Recovery: level the HMD
		emitFor(hmdFlow, TrackerSnapshot(STANDING_POSITION, HMD_LEVEL), SAMPLE_INTERVAL_MS)
		assertEquals(UserHeightCalibrationStatus.RECORDING_HEIGHT, context.state.value.status)
		job.cancel()
	}

	@Test
	fun `HMD rising above threshold transitions to RECORDING_HEIGHT`() = runTest {
		val context = makeContext(this)
		val controllerFlow = MutableSharedFlow<TrackerSnapshot>(extraBufferCapacity = 1)
		val hmdFlow = MutableSharedFlow<TrackerSnapshot>(extraBufferCapacity = 1)
		val job = launchSession(context, hmdFlow, controllerFlow)
		advanceTimeBy(SAMPLE_INTERVAL_MS)

		completeFloorPhase(controllerFlow, TrackerSnapshot(FLOOR_POSITION, POINTING_DOWN))

		// HMD below threshold
		emitFor(hmdFlow, TrackerSnapshot(Vector3(0f, 0.5f, 0f), HMD_LEVEL), SAMPLE_INTERVAL_MS)
		assertEquals(UserHeightCalibrationStatus.WAITING_FOR_RISE, context.state.value.status)

		// HMD rises above threshold
		emitFor(hmdFlow, TrackerSnapshot(STANDING_POSITION, HMD_LEVEL), SAMPLE_INTERVAL_MS)
		assertEquals(UserHeightCalibrationStatus.RECORDING_HEIGHT, context.state.value.status)
		job.cancel()
	}
}
