@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package dev.slimevr.heightcalibration

import dev.slimevr.VRServer
import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.rpc.UserHeightCalibrationStatus

data class TrackerSnapshot(val position: Vector3, val rotation: Quaternion)

data class HeightCalibrationState(
	val status: UserHeightCalibrationStatus,
	val currentHeight: Float,
)

sealed interface HeightCalibrationActions {
	data class Update(val status: UserHeightCalibrationStatus, val currentHeight: Float) : HeightCalibrationActions
}

typealias HeightCalibrationContext = Context<HeightCalibrationState, HeightCalibrationActions>
typealias HeightCalibrationBehaviourType = Behaviour<HeightCalibrationState, HeightCalibrationActions, HeightCalibrationManager>

val INITIAL_HEIGHT_CALIBRATION_STATE = HeightCalibrationState(
	status = UserHeightCalibrationStatus.NONE,
	currentHeight = 0f,
)

class HeightCalibrationManager(
	val context: HeightCalibrationContext,
	val serverContext: VRServer,
) {
	private var sessionJob: Job? = null

	// These Flows do nothing until the calibration use collect on it
	val hmdUpdates: Flow<TrackerSnapshot> = serverContext.context.state
		.flatMapLatest { state ->
			val hmd = state.trackers.values
				.find { it.context.state.value.bodyPart == BodyPart.HEAD } // TODO: Need to check for a head with position support
				?: return@flatMapLatest emptyFlow()
			hmd.context.state.map { s ->
				TrackerSnapshot(
					// TODO: get HMD position from VR system once position is set in the tracker
					position = Vector3.NULL,
					rotation = s.rawRotation,
				)
			}
		}

	val controllerUpdates: Flow<TrackerSnapshot> = serverContext.context.state
		.flatMapLatest { state ->
			val controllers = state.trackers.values.filter {
				val bodyPart = it.context.state.value.bodyPart
				bodyPart == BodyPart.LEFT_HAND || bodyPart == BodyPart.RIGHT_HAND
			}
			if (controllers.isEmpty()) return@flatMapLatest emptyFlow()
			combine(controllers.map { controller ->
				controller.context.state.map { s ->
					// TODO: get controller position from tracker once position is set in the tracker
					val position = Vector3.NULL
					TrackerSnapshot(position = position, rotation = s.rawRotation)
				}
			}) { snapshots -> snapshots.minByOrNull { it.position.y }!! }
		}

	fun start() {
		sessionJob?.cancel()
		sessionJob = context.scope.launch { runCalibrationSession(context, hmdUpdates, controllerUpdates) }
	}

	fun cancel() {
		sessionJob?.cancel()
		sessionJob = null
		context.dispatch(HeightCalibrationActions.Update(UserHeightCalibrationStatus.NONE, 0f))
	}

	companion object {
		fun create(
			serverContext: VRServer,
			scope: CoroutineScope,
		): HeightCalibrationManager {
			val behaviours = listOf(CalibrationBehaviour)
			val context = Context.create(
				initialState = INITIAL_HEIGHT_CALIBRATION_STATE,
				scope = scope,
				behaviours = behaviours,
			)
			return HeightCalibrationManager(context = context, serverContext = serverContext)
		}
	}
}
