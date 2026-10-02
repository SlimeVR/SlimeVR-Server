@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package dev.slimevr.stepmounting

import dev.slimevr.Phase1ContextProvider
import dev.slimevr.VRServer
import dev.slimevr.config.Settings
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.applyCalibration
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.TrackerStatus
import kotlin.collections.emptyList
import kotlin.collections.listOf
import kotlin.collections.map
import kotlin.time.ComparableTimeMark

// TODO Put this enum in SolarXR
enum class StepMountingStatus(
	val `value`: UByte,
) {
	NONE(0.toUByte()),
	WAITING_FOR_MOVEMENT(1.toUByte()),
	RECORDING(2.toUByte()),
	PROCESSING(3.toUByte()),
	DONE(4.toUByte()),
	ERROR_NO_DATA(5.toUByte()),
	ERROR_TIMEOUT(6.toUByte()),
	;

	companion object {
		fun fromValue(`value`: UByte): StepMountingStatus? = entries.firstOrNull { it.value == value }
	}
}

data class HeadsetSnapshot(
	val rotation: Quaternion,
	val position: Vector3,
)

data class TrackerSnapshot(
	val rotation: Quaternion,
	val acceleration: Vector3,
)

data class HeadRecordingSample(
	val time: ComparableTimeMark,
	val position: Vector3,
)

data class RecordingSample(
	val time: ComparableTimeMark,
	val accel: Vector3,
)

data class StepMountingState(
	val status: StepMountingStatus,
	val canDoStepMounting: Boolean,
)

sealed interface StepMountingActions {
	data class Update(val status: StepMountingStatus) : StepMountingActions
	data class SetCanCalibrate(val canDo: Boolean) : StepMountingActions
}

typealias StepMountingContext = Context<StepMountingState, StepMountingActions>
typealias StepMountingBehaviour = Behaviour<StepMountingManager>

class StepMountingManager(
	val context: StepMountingContext,
	val server: VRServer,
	val settings: Settings,
) {
	fun startObserving() = context.observeAll(this)

	private var sessionJob: Job? = null

	// These Flows do nothing until the calibration uses collect on it
	val headUpdates: Flow<HeadsetSnapshot> = server.context.state
		.flatMapConcat { state ->
			val positionalHead = state.trackers.values
				.find {
					val state = it.context.state.value
					state.bodyPart == BodyPart.HEAD && state.status == TrackerStatus.OK && state.position != null
				}
				?: return@flatMapConcat emptyFlow()
			positionalHead.context.state.map { s ->
				HeadsetSnapshot(
					rotation = s.rotation,
					position = s.position ?: error("Head will always have a position here"),
				)
			}
		}

	fun getTrackers(): List<Pair<Tracker, Flow<TrackerSnapshot>>> {
		val activeTrackers = server.context.state.value.trackers.values.filter {
			val state = it.context.state.value
			state.status == TrackerStatus.OK &&
				state.bodyPart != null &&
				state.acceleration != null &&
				state.position == null
		}
		if (activeTrackers.isEmpty()) return emptyList()
		return activeTrackers.map { controller ->
			controller to controller.context.state.map { s ->
				val accel = s.rawAcceleration ?: error("Trackers will always have acceleration in this case")
				TrackerSnapshot(
					rotation = s.rotation,
					acceleration = applyCalibration(
						accel,
						s.rawRotation,
						// TODO Stay Aligned does add to the headingCorrection not
						//  included here, so eventually this should just use
						//  s.acceleration.
						s.sessionCalibration.headingCorrection,
						// Leave headingALignment out of the equation
					),
				)
			}
		}
	}

	fun start() {
		sessionJob?.cancel()
		sessionJob = context.scope.launch { runCalibrationSession(context, headUpdates, getTrackers()) }
	}

	fun cancel() {
		sessionJob?.cancel()
		sessionJob = null
		context.dispatch(StepMountingActions.Update(StepMountingStatus.NONE))
	}

	companion object {
		fun create(ctx: Phase1ContextProvider, scope: CoroutineScope): StepMountingManager {
			val context = Context.create(
				initialState = StepMountingState(
					status = StepMountingStatus.NONE,
					canDoStepMounting = false,
				),
				scope = scope,
				reducer = ::reduce,
				behaviours = listOf(StepMountingBasicBehaviour()),
				name = "StepMountingManager",
			)
			return StepMountingManager(context, ctx.server, ctx.config.settings)
		}
	}
}
