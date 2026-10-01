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

// These are all only defaults and the user can edit any and they will save.
data class Offset(
	/** Offset from start of palm position to controller position. */
	val boneOffset: Vector3,
	// Don't use directly; use rotationOffset instead.
	val rotationOffsetX: Quaternion = Quaternion.IDENTITY,
	val rotationOffsetYZ: Quaternion = Quaternion.IDENTITY,
) {
	/** Offset from controller rotation to palm rotation. */
	val rotationOffset = rotationOffsetX * rotationOffsetYZ
	val otherSide get() = Offset(boneOffset.unaryMinusX(), rotationOffsetX, rotationOffsetYZ.inv())
}

private val leftIndexControllerOffset = Offset(
	Vector3(-0.02f, 0.07f, 0.13f),
	Quaternion.rotationAroundXAxis(0.42f),
	Quaternion.rotationAroundZAxis(0.28f),
)

// TODO rotation offset needs to be double checked
private val leftPicoControllerOffset = Offset(
	Vector3(0.01f, 0.11f, 0.11f),
	Quaternion.rotationAroundXAxis(0.42f),
	Quaternion.rotationAroundZAxis(0.15f),
)

// TODO add more devices, at least Quest 2 and 3 controllers
private val DISPLAY_NAME_TO_OFFSET = mapOf(
	// Erimel, through SteamVR
	"Knuckles Left" to leftIndexControllerOffset,
	"Knuckles Right" to leftIndexControllerOffset.otherSide,
	// ZRock, through ?
//	"Meta Quest 3 (Left Controller)" to
//	"Meta Quest 3 (Right Controller)" to
	// Spazzwan, through Steam Link
	"PICO 4 (Left Controller)" to leftPicoControllerOffset,
	"PICO 4 (Right Controller)" to leftPicoControllerOffset.otherSide,
	// Spazzwan, through PICO Connect
	"Pico Phoenix Controller left" to leftPicoControllerOffset,
	"Pico Phoenix Controller right" to leftPicoControllerOffset.otherSide,
)

// Offset from eyes to centre of head (same for all HMDs).
private val hmdOffset = Offset(Vector3(0f, 0f, 0.1f))
private val leftGenericControllerOffset = Offset(
	Vector3(0f, 0.1f, 0.12f),
	Quaternion.rotationAroundXAxis(0.42f),
	Quaternion.rotationAroundZAxis(0.25f),
)

// Used as fallback when map above doesn't contain the entry
private val BODY_PART_TO_OFFSET = mapOf(
	BodyPart.HEAD to hmdOffset,
	BodyPart.LEFT_HAND to leftGenericControllerOffset,
	BodyPart.RIGHT_HAND to leftGenericControllerOffset.otherSide,
)

// I don't know why linear velocity seems to be in a different coordinate system. -Erimel
private fun remapVelocity(velocity: Vector3) = Vector3(velocity.z, -velocity.y, velocity.x)

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

			// Map velocity to accel
			val acceleration = event.linearVelocity?.let { velocity ->
				val now = timeSource.markNow()
				val velocity = velocity.toVector3()
				val lastVelocity = lastVelocities[trackerId]
				lastVelocities[trackerId] = now to velocity

				lastVelocity?.let {
					val deltaVelocity = (velocity - it.second)
					val deltaTime = (now - it.first).inFloatingSeconds
					remapVelocity(deltaVelocity / deltaTime)
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
