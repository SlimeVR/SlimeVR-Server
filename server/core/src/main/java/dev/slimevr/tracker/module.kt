package dev.slimevr.tracker

import dev.slimevr.context.Behaviour
import dev.slimevr.context.Context
import dev.slimevr.device.DeviceOrigin
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlinx.coroutines.CoroutineScope
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.hardware_info.ImuType

data class TrackerIdNum(val id: Int, val trackerNum: Int)

data class TrackerState(
	val id: Int,
	val name: String,
	val hardwareId: String,
	val sensorType: ImuType,
	val bodyPart: BodyPart?,
	val customName: String?,
	val rawRotation: Quaternion,
	val deviceId: Int,
	val origin: DeviceOrigin,
	val tps: UShort,
	val position: Vector3?,
)

sealed interface TrackerActions {
	data class Update(val transform: TrackerState.() -> TrackerState) : TrackerActions
}

typealias TrackerContext = Context<TrackerState, TrackerActions>
typealias TrackerBehaviour = Behaviour<TrackerState, TrackerActions, TrackerContext>

class Tracker(
	val context: TrackerContext,
) {
	companion object {
		fun create(
			scope: CoroutineScope,
			id: Int,
			deviceId: Int,
			sensorType: ImuType,
			hardwareId: String,
			origin: DeviceOrigin,
		): Tracker {
			val trackerState = TrackerState(
				id = id,
				hardwareId = hardwareId,
				name = "Tracker #$id",
				rawRotation = Quaternion.IDENTITY,
				bodyPart = null,
				origin = origin,
				deviceId = deviceId,
				customName = null,
				sensorType = sensorType,
				position = null,
				tps = 0u,
			)

			val behaviours = listOf(TrackerBasicBehaviour, TrackerTPSBehaviour)
			val context = Context.create(initialState = trackerState, scope = scope, behaviours = behaviours)
			behaviours.forEach { it.observe(context) }
			return Tracker(context = context)
		}
	}
}
