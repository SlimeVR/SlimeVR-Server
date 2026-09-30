package dev.slimevr.solarxr.driver

import dev.slimevr.AppContextProvider
import dev.slimevr.VRServerActions
import dev.slimevr.device.Device
import dev.slimevr.device.DeviceActions
import dev.slimevr.solarxr.SolarXRBridge
import dev.slimevr.solarxr.SolarXRBridgeBehaviour
import dev.slimevr.solarxr.toQuaternion
import dev.slimevr.solarxr.toVector3
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import dev.slimevr.util.MonotonicValueTimeMark
import dev.slimevr.util.inFloatingSeconds
import dev.slimevr.util.timeSource
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.DeviceOrigin
import solarxr_protocol.driver_protocol.AddTrackerRequest
import solarxr_protocol.driver_protocol.AddTrackerResponse
import solarxr_protocol.driver_protocol.AddTrackerStatus
import solarxr_protocol.driver_protocol.UpdateTrackerBattery
import solarxr_protocol.driver_protocol.UpdateTrackerPosition
import solarxr_protocol.driver_protocol.UpdateTrackerStatus
import kotlin.to

data class Offset(
	/** Default position offset from bone to tracker. Can be changed by the user. */
	val boneOffset: Vector3,
	/** Rotation offset applied on the tracker's raw rotation before everything else. */
	val rotationOffset: Quaternion = Quaternion.IDENTITY,
)
private val indexBoneOffset = Vector3(-0.02f, 0.07f, 0.13f)
private val indexRotX = Quaternion.rotationAroundXAxis(0.4f)
private val indexRotZ = Quaternion.rotationAroundZAxis(0.35f)

// TODO add more devices
private val DISPLAY_NAME_TO_OFFSET = mapOf(
	"Knuckles Left" to Offset(indexBoneOffset, indexRotX * indexRotZ),
	"Knuckles Right" to Offset(indexBoneOffset.unaryMinusX(), indexRotX * indexRotZ.inv()),
)
// Used as fallback when map above doesn't contain the entry
private val BODY_PART_TO_OFFSET = mapOf(
	BodyPart.HEAD to Offset(Vector3(0f, 0f, 0.1f)),
	BodyPart.LEFT_HAND to Offset(indexBoneOffset, indexRotX * indexRotZ),
	BodyPart.RIGHT_HAND to Offset(indexBoneOffset.unaryMinusX(), indexRotX * indexRotZ.inv()),
)

class DriverIncomingTrackersBehaviour(
	private val appContext: AppContextProvider,
) : SolarXRBridgeBehaviour {
	private val rotationOffsets: MutableMap<Int, Quaternion> = mutableMapOf()
	private val lastVelocities: MutableMap<Int, Pair<MonotonicValueTimeMark, Vector3>> = mutableMapOf()

	override fun observe(receiver: SolarXRBridge) {
		val server = appContext.server

		receiver.onDriverMessage<AddTrackerRequest> { req, replyTo ->
			val driverName = receiver.context.state.value.driverName ?: run {
				receiver.sendDriverMessage(AddTrackerResponse(status = AddTrackerStatus.ERROR), replyTo = replyTo)
				return@onDriverMessage
			}
			val hardwareId = req.hardwareIdentifier

			val existing = server.context.state.value.trackers.values
				.find { it.context.state.value.hardwareId == hardwareId }
			if (existing != null) {
				val trackerState = existing.context.state.value
				// Tracker is in use by another driver right now
				if (trackerState.driverName != null && trackerState.driverName != driverName) {
					receiver.sendDriverMessage(AddTrackerResponse(status = AddTrackerStatus.ERROR), replyTo = replyTo)
					return@onDriverMessage
				}

				existing.context.dispatchAll(listOf(TrackerActions.SetDriverName(driverName), TrackerActions.Update { copy(intendedBodyPart = req.bodyPart) }))
				receiver.sendDriverMessage(
					AddTrackerResponse(
						status = AddTrackerStatus.ALREADY_EXISTS,
						trackerId = trackerState.id.toUShort(),
					),
					replyTo = replyTo,
				)
				return@onDriverMessage
			}

			val scope = server.context.scope
			val deviceId = server.nextHandle()
			val device = Device.create(
				scope = scope,
				appContext = appContext,
				id = deviceId,
				name = req.displayName ?: "Device #$deviceId",
				manufacturer = req.manufacturer ?: "External",
				address = hardwareId,
				macAddress = hardwareId,
				origin = DeviceOrigin.DRIVER,
				driverName = driverName,
				protocolVersion = 0,
			)
			server.context.dispatch(VRServerActions.NewDevice(deviceId, device))

			val offset = DISPLAY_NAME_TO_OFFSET[req.displayName] ?: BODY_PART_TO_OFFSET[req.bodyPart]
			val trackerId = server.nextHandle()
			val tracker = Tracker.create(
				scope = scope,
				id = trackerId,
				name = req.displayName ?: "Tracker #$trackerId",
				bodyPart = req.bodyPart,
				intendedBodyPart = req.bodyPart,
				intendedBoneOffset = offset?.boneOffset,
				deviceId = deviceId,
				hardwareId = hardwareId,
				origin = DeviceOrigin.DRIVER,
				driverName = driverName,
				appContext = appContext,
			)
			rotationOffsets[trackerId] = offset?.rotationOffset ?: Quaternion.IDENTITY
			server.context.dispatch(VRServerActions.NewTracker(trackerId, tracker))

			receiver.sendDriverMessage(
				AddTrackerResponse(status = AddTrackerStatus.CREATED, trackerId = trackerId.toUShort()),
				replyTo = replyTo,
			)
		}.launchIn(receiver.context.scope)

		receiver.driverDispatcher.on<UpdateTrackerStatus> { event ->
			if (receiver.context.state.value.driverName == null) return@on
			val trackerId = event.trackerId.toInt()
			if (trackerId == 0) return@on
			val status = event.status
			server.getTracker(trackerId)?.context?.dispatch(TrackerActions.SetStatus(status))
		}.launchIn(receiver.context.scope)

		receiver.driverDispatcher.on<UpdateTrackerBattery> { event ->
			if (receiver.context.state.value.driverName == null) return@on
			val trackerId = event.trackerId.toInt()
			if (trackerId == 0) return@on
			val tracker = server.getTracker(trackerId) ?: return@on
			val device = server.getDevice(tracker.context.state.value.deviceId) ?: return@on

			device.context.dispatch(
				DeviceActions.Update {
					copy(batteryLevel = event.batteryLevel.toFloat() / 100f, batteryVoltage = if (event.charging) 4.3f else 3.7f)
				},
			)
		}.launchIn(receiver.context.scope)

		receiver.driverDispatcher.on<UpdateTrackerPosition> { event ->
			if (receiver.context.state.value.driverName == null) return@on
			val trackerId = event.trackerId.toInt()
			if (trackerId == 0) return@on

			// Map velocity to accel TODO: driver doesn't send velocity 30/09/2026
			val acceleration = event.linearVelocity?.let { velocity ->
				val now = timeSource.markNow()
				val velocity = velocity.toVector3()
				val lastVelocity = lastVelocities[trackerId]
				lastVelocities[trackerId] = now to velocity

				lastVelocity?.let {
					val deltaVelocity = (velocity - it.second)
					val deltaTime = (now - it.first).inFloatingSeconds
					deltaVelocity / deltaTime
				}
			}
			// Rotation offset done here before rotation gets to the tracker
			val rotationOffset = rotationOffsets[trackerId] ?: Quaternion.IDENTITY
			// Update tracker with new data
			server.getTracker(trackerId)?.context?.dispatch(
				TrackerActions.SetRotation(
					rotation = event.rotation?.toQuaternion()?.times(rotationOffset),
					acceleration = acceleration,
					position = event.position?.toVector3(),
				),
			)
		}.launchIn(receiver.context.scope)
	}
}
