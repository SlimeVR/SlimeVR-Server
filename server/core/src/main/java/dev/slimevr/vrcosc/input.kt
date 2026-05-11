package dev.slimevr.vrcosc

import dev.slimevr.AppLogger
import dev.slimevr.Phase1ContextProvider
import dev.slimevr.VRServerActions
import dev.slimevr.device.Device
import dev.slimevr.device.DeviceActions
import dev.slimevr.device.DeviceOrigin
import dev.slimevr.osc.OscBundle
import dev.slimevr.osc.OscContent
import dev.slimevr.osc.OscMessage
import dev.slimevr.osc.OscReceiver
import dev.slimevr.tracker.Tracker
import dev.slimevr.tracker.TrackerActions
import dev.slimevr.util.safeLaunch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import solarxr_protocol.datatypes.BodyPart
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.rpc.VRCOSCInputState

/**
 * Holds the long-lived VRC VRSystem device + head/wrist trackers. Trackers are
 * created lazily when their first packet arrives and reused afterwards.
 *
 * The runtime cannot remove existing devices/trackers from the VRServer, so on
 * disable we only mark them DISCONNECTED — they are reused on the next enable.
 */
private class VRSystemTrackerRegistry(
	private val ctx: Phase1ContextProvider,
	private val manager: VRCOSCManager,
) {
	private var deviceId: Int? = null
	private val trackerIds = mutableMapOf<VRSystemTracker, Int>()

	fun trackerFor(tracker: VRSystemTracker): Tracker {
		trackerIds[tracker]?.let { id -> ctx.server.getTracker(id)?.let { existing -> return existing } }

		val device = findOrCreateDevice()
		val trackerId = ctx.server.nextHandle()
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
			id = trackerId,
			deviceId = device.context.state.value.id,
			sensorType = null,
			hardwareId = "vrcosc:vrsystem:${tracker.name.lowercase()}",
			origin = DeviceOrigin.VRC,
			scope = manager.context.scope,
			server = ctx.server,
			settings = ctx.config.settings,
		)
		ctx.server.context.dispatch(VRServerActions.NewTracker(trackerId, runtimeTracker))
		runtimeTracker.context.dispatch(
			TrackerActions.Update {
				copy(
					name = trackerName,
					customName = trackerName,
					bodyPart = bodyPart,
					status = TrackerStatus.OK,
				)
			},
		)
		trackerIds[tracker] = trackerId
		return runtimeTracker
	}

	fun setStatus(status: TrackerStatus) {
		deviceId?.let { id -> ctx.server.getDevice(id) }
			?.context?.dispatch(DeviceActions.Update { copy(status = status) })
		for ((_, trackerId) in trackerIds) {
			ctx.server.getTracker(trackerId)?.context?.dispatch(TrackerActions.Update { copy(status = status) })
		}
	}

	private fun findOrCreateDevice(): Device {
		deviceId?.let { id -> ctx.server.getDevice(id)?.let { return it } }

		val id = ctx.server.nextHandle()
		val device = Device.create(
			scope = manager.context.scope,
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
		ctx.server.context.dispatch(VRServerActions.NewDevice(id, device))
		deviceId = id
		return device
	}
}

class VRCOSCInputBehaviour(
	private val ctx: Phase1ContextProvider,
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
		val registry = VRSystemTrackerRegistry(ctx, receiver)
		var oscReceiver: OscReceiver? = null

		receiver.context.state
			.map { state -> Pair(state.config.enabled, vrcOscPortIn(state.config)) }
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

				receiver.context.scope.safeLaunch {
					try {
						newReceiver.listenBundles { bundle -> handleBundle(bundle, registry, receiver, portIn) }
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

	private fun handleBundle(
		bundle: OscBundle,
		registry: VRSystemTrackerRegistry,
		receiver: VRCOSCManager,
		portIn: Int,
	) {
		handleBundleContents(bundle.contents, registry, receiver, portIn)
	}

	private fun handleBundleContents(
		contents: List<OscContent>,
		registry: VRSystemTrackerRegistry,
		receiver: VRCOSCManager,
		portIn: Int,
	) {
		for (content in contents) {
			when (content) {
				is OscContent.Message -> handleIncomingMessage(content.msg, registry, receiver, portIn)
				is OscContent.Bundle -> handleBundleContents(content.bundle.contents, registry, receiver, portIn)
			}
		}
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
				TrackerActions.Update {
					copy(
						position = position,
						status = TrackerStatus.OK,
					)
				},
				TrackerActions.SetRotation(rotation = rotation),
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
