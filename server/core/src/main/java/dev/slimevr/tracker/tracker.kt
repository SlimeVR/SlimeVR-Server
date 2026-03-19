package dev.slimevr.tracker

import dev.slimevr.AppLogger
import dev.slimevr.VRServer
import dev.slimevr.context.BasicModule
import dev.slimevr.context.Context
import dev.slimevr.context.createContext
import dev.slimevr.skeleton.BodyPart
import io.github.axisangles.ktmath.Quaternion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

enum class TrackerStatus(val id: UByte) {
	DISCONNECTED(solarxr_protocol.datatypes.TrackerStatus.DISCONNECTED),
	OK(solarxr_protocol.datatypes.TrackerStatus.OK),
	BUSY(solarxr_protocol.datatypes.TrackerStatus.BUSY),
	ERROR(solarxr_protocol.datatypes.TrackerStatus.ERROR),
	OCCLUDED(solarxr_protocol.datatypes.TrackerStatus.OCCLUDED),
	TIMEDOUT(solarxr_protocol.datatypes.TrackerStatus.TIMEDOUT);

	companion object {
		private val map = entries.associateBy { it.id }
		fun fromId(id: UByte) = map[id]
	}
}

data class TrackerIdNum(val id: Int, val trackerNum: Int)

data class TrackerState(
	val id: Int,
	val name: String,
	val hardwareId: String,
	val sensorType: IMUType,
	val bodyPart: BodyPart?,
	val status: TrackerStatus,
	val customName: String?,
	val rawRotation: Quaternion,
	val deviceId: Int,
	val origin: DeviceOrigin
)

sealed interface TrackerActions {
	data class Update(val transform: TrackerState.() -> TrackerState) : TrackerActions
}

typealias TrackerContext = Context<TrackerState, TrackerActions>
typealias TrackerModule = BasicModule<TrackerState, TrackerActions>

data class Tracker(
	val context: TrackerContext
)


val TrackerInfosModule = TrackerModule(
	reducer = { s, a -> if (a is TrackerActions.Update) a.transform(s) else s },
	observer = {
		it.state.onEach { state -> AppLogger.tracker.info("Tracker state changed {State}", state) }.launchIn(it.scope)
	}
)

fun createTracker(
	scope: CoroutineScope,
	id: Int,
	deviceId: Int,
	sensorType: IMUType,
	hardwareId: String,
	origin: DeviceOrigin,
	serverContext: VRServer
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
		sensorType = sensorType
	)

	val modules = listOf(TrackerInfosModule)

	val context = createContext(
		initialState = trackerState,
		reducers = modules.map { it.reducer },
		scope = scope,
	)

	modules.map { it.observer }.forEach { it?.invoke(context) }

	return Tracker(
		context = context,
	)
}
