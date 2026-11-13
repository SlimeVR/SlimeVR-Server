package dev.slimevr.vrcosc

import com.jme3.math.FastMath
import dev.slimevr.AppContextProvider
import dev.slimevr.VRServerActions
import dev.slimevr.config.Settings
import dev.slimevr.device.Device
import dev.slimevr.device.DeviceActions
import dev.slimevr.logging.AppLogger
import dev.slimevr.osc.OscMessage
import dev.slimevr.osc.OscReceiver
import dev.slimevr.osc.forEachOscMessage
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import dev.slimevr.util.formatExceptionMessage
import io.github.axisangles.ktmath.Quaternion
import io.github.axisangles.ktmath.Vector3
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.DeviceOrigin
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.rpc.VRCOSCInputState

private val TRACKER_OFFSET = mapOf(
	VRSystemTracker.HEAD to Vector3(0f, 0f, 0.1f),
)

// I have no idea why we need these somewhat arbitrary offsets -Erimel
private val TRACKER_ROTATION_OFFSET = mapOf(
	VRSystemTracker.LEFT_WRIST to Quaternion.fromRotationVector(45f * FastMath.DEG_TO_RAD, -45f * FastMath.DEG_TO_RAD, -67.5f * FastMath.DEG_TO_RAD),
	VRSystemTracker.RIGHT_WRIST to Quaternion.fromRotationVector(45f * FastMath.DEG_TO_RAD, 45f * FastMath.DEG_TO_RAD, 67.5f * FastMath.DEG_TO_RAD),
)

/**
 * Holds the long-lived VRC VRSystem device + head/wrist trackers. Trackers are
 * created lazily when their first packet arrives and reused afterwards.
 *
 * The runtime cannot remove existing devices/trackers from the VRServer, so on
 * disable we only mark them DISCONNECTED; they are reused on the next enable.
 */
private class VRSystemTrackerRegistry(
	private val appContext: AppContextProvider,
	private val manager: VRCOSCManager,
) {
	private var deviceId: Int? = null
	private val trackerIds = mutableMapOf<VRSystemTracker, Int>()

	fun trackerFor(tracker: VRSystemTracker): Tracker {
		trackerIds[tracker]?.let { id -> appContext.server.getTracker(id)?.let { existing -> return existing } }

		val device = findOrCreateDevice()
		val trackerId = appContext.server.nextHandle()
		val bodyPart = when (tracker) {
			VRSystemTracker.HEAD -> BodyPart.HEAD
			VRSystemTracker.LEFT_WRIST -> BodyPart.LEFT_HAND
			VRSystemTracker.RIGHT_WRIST -> BodyPart.RIGHT_HAND
		}
		val trackerName = when (tracker) {
			VRSystemTracker.HEAD -> "VRChat head"
			VRSystemTracker.LEFT_WRIST -> "VRChat left hand"
			VRSystemTracker.RIGHT_WRIST -> "VRChat right hand"
		}
		val runtimeTracker = Tracker.create(
			scope = manager.context.scope,
			id = trackerId,
			name = trackerName,
			bodyPart = bodyPart,
			intendedBodyPart = bodyPart,
			intendedBoneOffset = TRACKER_OFFSET[tracker],
			deviceId = device.context.state.value.id,
			hardwareId = "vrcosc:vrsystem:${tracker.name.lowercase()}",
			origin = DeviceOrigin.VRC,
			appContext = appContext,
		)
		appContext.server.context.dispatch(VRServerActions.NewTracker(trackerId, runtimeTracker))
		runtimeTracker.context.dispatch(TrackerActions.SetStatus(TrackerStatus.OK))
		trackerIds[tracker] = trackerId
		return runtimeTracker
	}

	fun setStatus(status: TrackerStatus) {
		deviceId?.let { id -> appContext.server.getDevice(id) }
			?.context?.dispatch(DeviceActions.Update { copy(status = status) })
		for ((_, trackerId) in trackerIds) {
			appContext.server.getTracker(trackerId)?.context?.dispatch(TrackerActions.SetStatus(status))
		}
	}

	private fun findOrCreateDevice(): Device {
		deviceId?.let { id -> appContext.server.getDevice(id)?.let { return it } }

		val id = appContext.server.nextHandle()
		val device = Device.create(
			scope = manager.context.scope,
			appContext = appContext,
			id = id,
			address = "vrchat-vrsystem",
			origin = DeviceOrigin.VRC,
			protocolVersion = 0,
		)
		device.context.dispatch(
			DeviceActions.Update {
				copy(
					name = "VRC VRSystem",
					status = TrackerStatus.OK,
				)
			},
		)
		appContext.server.context.dispatch(VRServerActions.NewDevice(id, device))
		deviceId = id
		return device
	}
}

class VRCOSCInputBehaviour(
	private val appContext: AppContextProvider,
	private val settings: Settings,
) : VRCOSCBehaviour {
	override fun observe(receiver: VRCOSCManager) {
		val registry = VRSystemTrackerRegistry(appContext, receiver)
		var oscReceiver: OscReceiver? = null

		settings.context.state
			.map { state -> Triple(state.data.vrcOscConfig.enabled, state.data.vrcOscConfig.portIn, state.data.vrcOscConfig.useManualNetwork) }
			.distinctUntilChanged()
			.onEach { (enabled, configPortIn, useManualNetwork) ->
				oscReceiver?.close()
				oscReceiver = null

				if (!enabled) {
					registry.setStatus(TrackerStatus.DISCONNECTED)
					receiver.context.dispatch(VRCOSCActions.SetInput(state = VRCOSCInputState.IDLE))
					return@onEach
				}

				val bindPort = if (useManualNetwork) configPortIn else 0
				val (newReceiver, portIn) = try {
					bindReceiver(bindPort)
				} catch (e: Exception) {
					dispatchInputError(
						receiver = receiver,
						port = bindPort,
						message = "Failed to start VRChat OSC receiver",
						throwable = e,
					)
					return@onEach
				}
				oscReceiver = newReceiver
				receiver.context.dispatch(
					VRCOSCActions.SetInput(state = VRCOSCInputState.LISTENING, port = portIn),
				)
				AppLogger.vrc.info("VRChat OSC input listening on port $portIn")

				receiver.context.scope.launch {
					try {
						newReceiver.listenBundles { bundle ->
							forEachOscMessage(bundle) { msg -> handleIncomingMessage(msg, registry, receiver, portIn) }
						}
					} catch (e: Exception) {
						dispatchInputError(
							receiver = receiver,
							port = portIn,
							message = "VRChat OSC receiver error",
							throwable = e,
						)
					}
				}
			}.launchIn(receiver.context.scope)
	}

	private suspend fun bindReceiver(port: Int): Pair<OscReceiver, Int> {
		val oscReceiver = OscReceiver(port)
		try {
			return oscReceiver to oscReceiver.getSocketPort()
		} catch (e: Exception) {
			oscReceiver.close()
			throw e
		}
	}

	private suspend fun dispatchInputError(
		receiver: VRCOSCManager,
		port: Int?,
		message: String,
		throwable: Throwable,
	) {
		AppLogger.vrc.error(throwable, message)
		receiver.context.dispatch(
			VRCOSCActions.SetInput(
				state = VRCOSCInputState.ERROR,
				port = port,
				error = formatExceptionMessage(message, throwable),
			),
		)
	}

	private fun handleIncomingMessage(
		message: OscMessage,
		registry: VRSystemTrackerRegistry,
		receiver: VRCOSCManager,
		portIn: Int,
	) {
		receiver.context.dispatchAll(
			listOf(
				VRCOSCActions.SetInput(state = VRCOSCInputState.LISTENING, port = portIn),
				VRCOSCActions.SetLastReceivedInput(System.currentTimeMillis()),
			),
		)

		if (!message.address.startsWith(TRACKING_VRSYSTEM_PATH)) return
		val tracker = when (message.address) {
			"$TRACKING_VRSYSTEM_PATH$HEAD_POSE_SUBPATH" -> VRSystemTracker.HEAD
			"$TRACKING_VRSYSTEM_PATH$LEFT_WRIST_POSE_SUBPATH" -> VRSystemTracker.LEFT_WRIST
			"$TRACKING_VRSYSTEM_PATH$RIGHT_WRIST_POSE_SUBPATH" -> VRSystemTracker.RIGHT_WRIST
			else -> return
		}

		val position = parsePosition(message.args) ?: return
		val rotation = parseVrcEulerRotation(message.args, startIndex = 3) ?: return
		val rotationOffset = TRACKER_ROTATION_OFFSET[tracker]
		val runtimeTracker = registry.trackerFor(tracker)
		runtimeTracker.context.dispatchAll(
			listOf(
				TrackerActions.SetStatus(TrackerStatus.OK),
				TrackerActions.SetRotation(rotation = rotationOffset?.let { rotation * it } ?: rotation, position = position),
			),
		)
		registry.setStatus(TrackerStatus.OK)
		receiver.context.dispatch(VRCOSCActions.SetLastReceivedTracking(System.currentTimeMillis()))
	}
}
