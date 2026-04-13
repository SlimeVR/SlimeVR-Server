package dev.slimevr.tracker

import dev.slimevr.VRServer
import dev.slimevr.config.Settings
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
	val sensorType: ImuType?,
	val bodyPart: BodyPart?,
	val customName: String?,
	val mountingOrientation: Quaternion?,
	val rawRotation: Quaternion,
	val acceleration: Vector3,
	val deviceId: Int,
	val origin: DeviceOrigin,
	val tps: UShort,
	val imuTemp: Float?,
	val position: Vector3?,
)

sealed interface TrackerActions {
	data class Update(val transform: TrackerState.() -> TrackerState) : TrackerActions
}

typealias TrackerContext = Context<TrackerState, TrackerActions>
typealias TrackerBehaviour = Behaviour<TrackerState, TrackerActions, Tracker>

class Tracker(
	val context: TrackerContext,
	val server: VRServer,
) {
	companion object {
		fun create(
			scope: CoroutineScope,
			id: Int,
			deviceId: Int,
			sensorType: ImuType?,
			hardwareId: String,
			origin: DeviceOrigin,
			server: VRServer,
			settings: Settings,
		): Tracker {
			val trackerState = TrackerState(
				id = id,
				hardwareId = hardwareId,
				name = "Tracker #$id",
				rawRotation = Quaternion.IDENTITY,
				acceleration = Vector3.NULL,
				bodyPart = null,
				mountingOrientation = null,
				origin = origin,
				deviceId = deviceId,
				customName = null,
				sensorType = sensorType,
				position = null,
				tps = 0u,
				imuTemp = null,
			)

			val behaviours = listOf(
				TrackerBasicBehaviour,
				TrackerTPSBehaviour,
				TrackerTapDetectionBehaviour,
				TrackerConfigBehaviour(settings, hardwareId)
			)
			val context = Context.create(initialState = trackerState, scope = scope, behaviours = behaviours)
			val tracker = Tracker(context = context, server)
			behaviours.forEach { it.observe(tracker) }
			return tracker
		}
	}
}
