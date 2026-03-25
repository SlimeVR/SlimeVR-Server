package dev.slimevr.tracker

import dev.slimevr.VRServer
import dev.slimevr.context.BasicBehaviour
import dev.slimevr.context.Context
import dev.slimevr.context.createContext
import io.github.axisangles.ktmath.Quaternion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.datatypes.hardware_info.ImuType

data class TrackerIdNum(val id: Int, val trackerNum: Int)

data class TrackerState(
	val id: Int,
	val name: String,
	val hardwareId: String,
	val sensorType: ImuType,
	val bodyPart: BodyPart?,
	val status: TrackerStatus,
	val customName: String?,
	val rawRotation: Quaternion,
	val deviceId: Int,
	val origin: DeviceOrigin,
)

sealed interface TrackerActions {
	data class Update(val transform: TrackerState.() -> TrackerState) : TrackerActions
}

typealias TrackerContext = Context<TrackerState, TrackerActions>
typealias TrackerBehaviour = BasicBehaviour<TrackerState, TrackerActions>

data class Tracker(
	val context: TrackerContext,
)

val TrackerInfosBehaviour = TrackerBehaviour(
	reducer = { s, a -> if (a is TrackerActions.Update) a.transform(s) else s },
	observer = {
		it.state.onEach { state ->
// 			AppLogger.tracker.info("Tracker state changed {State}", state)
		}.launchIn(it.scope)
	},
)

fun createTracker(
	scope: CoroutineScope,
	id: Int,
	deviceId: Int,
	sensorType: ImuType,
	hardwareId: String,
	origin: DeviceOrigin,
	serverContext: VRServer,
): Tracker {
	val trackerState = TrackerState(
		id = id,
		hardwareId = hardwareId,
		name = "Tracker #$id",
		rawRotation = Quaternion.IDENTITY,
		status = TrackerStatus.DISCONNECTED,
		bodyPart = null,
		origin = origin,
		deviceId = deviceId,
		customName = null,
		sensorType = sensorType,
	)

	val behaviours = listOf(TrackerInfosBehaviour)

	val context = createContext(
		initialState = trackerState,
		reducers = behaviours.map { it.reducer },
		scope = scope,
	)

	behaviours.map { it.observer }.forEach { it?.invoke(context) }

	return Tracker(
		context = context,
	)
}
