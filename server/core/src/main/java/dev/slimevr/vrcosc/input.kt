package dev.slimevr.vrcosc

import dev.slimevr.AppContextProvider
import dev.slimevr.VRServerActions
import dev.slimevr.config.Settings
import dev.slimevr.device.Device
import dev.slimevr.device.DeviceActions
import dev.slimevr.device.DeviceOrigin
import dev.slimevr.logging.AppLogger
import dev.slimevr.osc.OscMessage
import dev.slimevr.osc.OscReceiver
import dev.slimevr.osc.forEachOscMessage
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import dev.slimevr.util.formatExceptionMessage
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.rpc.VRCOSCInputState

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
			deviceId = device.context.state.value.id,
			hardwareId = "vrcosc:vrsystem:${tracker.name.lowercase()}",
			origin = DeviceOrigin.VRC,
			appContext = appContext,
		)
		appContext.server.context.dispatch(VRServerActions.NewTracker(trackerId, runtimeTracker))
		runtimeTracker.context.dispatch(TrackerActions.SetStatus(TrackerStatus.OK))
		// TODO : what's the mounting orientation of these trackers, or is it even used?
		// setting the bodyPart will automatically set it, which may or may not be a problem.
		trackerIds[tracker] = trackerId
		return runtimeTracker
	}

	fun setStatus(status: TrackerStatus) {
		deviceId?.let { id -> appContext.server.getDevice(id) }
			?.context?.dispatch(DeviceActions.Update { copy(status = status) })
		for ((_, trackerId) in trackerIds) {
			appContext.server.getTracker(trackerId)?.context?.dispatch(TrackerActions.Update { copy(status = status) })
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
	override fun reduce(state: VRCOSCState, action: VRCOSCActions) = when (action) {
		is VRCOSCActions.SetInput -> state.copy(
			status = state.status.copy(
				inputState = action.state,
				inputPort = action.port,
				inputError = action.error,
			),
		)

		is VRCOSCActions.SetLastReceivedInput -> state.copy(
			status = state.status.copy(lastReceivedInputMillis = action.millis),
		)

		else -> state
	}

	override fun observe(receiver: VRCOSCManager) {
		val registry = VRSystemTrackerRegistry(appContext, receiver)
		var oscReceiver: OscReceiver? = null

		settings.context.state
			.map { state -> Pair(state.data.vrcOscConfig.enabled, state.data.vrcOscConfig.portIn) }
			.distinctUntilChanged()
			.onEach { (enabled, portIn) ->
				oscReceiver?.close()
				oscReceiver = null

				if (!enabled) {
					registry.setStatus(TrackerStatus.DISCONNECTED)
					receiver.context.dispatch(VRCOSCActions.SetInput(state = VRCOSCInputState.IDLE))
					return@onEach
				}

				val newReceiver = try {
					OscReceiver(portIn)
				} catch (e: Exception) {
					dispatchInputError(
						receiver = receiver,
						port = portIn,
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

	private suspend fun dispatchInputError(
		receiver: VRCOSCManager,
		port: Int?,
		message: String,
		throwable: Throwable,
	) {
		AppLogger.vrc.error(message, throwable)
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
		if (!message.address.startsWith("$TRACKING_VRSYSTEM_PATH/")) return
		val tracker = when (message.address) {
			"$TRACKING_VRSYSTEM_PATH/head/pose" -> VRSystemTracker.HEAD
			"$TRACKING_VRSYSTEM_PATH/leftwrist/pose" -> VRSystemTracker.LEFT_WRIST
			"$TRACKING_VRSYSTEM_PATH/rightwrist/pose" -> VRSystemTracker.RIGHT_WRIST
			else -> return
		}

		val position = parsePosition(message.args) ?: return
		val rotation = parseVrcEulerRotation(message.args, startIndex = 3) ?: return
		val runtimeTracker = registry.trackerFor(tracker)
		runtimeTracker.context.dispatchAll(
			listOf(
				TrackerActions.SetStatus(TrackerStatus.OK),
				TrackerActions.SetRotation(rotation = rotation, position = position),
			),
		)
		registry.setStatus(TrackerStatus.OK)
		receiver.context.dispatchAll(
			listOf(
				VRCOSCActions.SetInput(state = VRCOSCInputState.LISTENING, port = portIn),
				VRCOSCActions.SetLastReceivedInput(System.currentTimeMillis()),
			),
		)
	}
}
