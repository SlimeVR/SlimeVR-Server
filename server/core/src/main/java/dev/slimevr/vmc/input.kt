package dev.slimevr.vmc

import dev.slimevr.AppContextProvider
import dev.slimevr.VRServerActions
import dev.slimevr.bones.BodyPart
import dev.slimevr.bones.BoneId
import dev.slimevr.bones.CompiledSkeleton
import dev.slimevr.config.Settings
import dev.slimevr.device.Device
import dev.slimevr.device.DeviceActions
import dev.slimevr.logging.AppLogger
import dev.slimevr.osc.OscBundle
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
import solarxr_protocol.datatypes.DeviceOrigin
import solarxr_protocol.datatypes.TrackerStatus
import solarxr_protocol.rpc.VMCOSCInputState

internal class VmcTrackerRegistry(
	private val appContext: AppContextProvider,
	private val manager: VMCManager,
) {
	private var deviceId: Int? = null
	private val boneTrackerIds = mutableMapOf<BoneId, Int>()
	private val poseTrackerIds = mutableMapOf<String, Int>()

	fun boneTracker(boneId: BoneId, unityName: String): Tracker {
		boneTrackerIds[boneId]?.let { id -> appContext.server.getTracker(id)?.let { existing -> return existing } }

		val device = findOrCreateDevice()
		val trackerId = appContext.server.nextHandle()
		val registry = appContext.skeleton.definition.registry
		val hardwareKey = registry.bodyPartOf(boneId) ?: registry.keyOf(boneId) ?: unityName
		val runtimeTracker = Tracker.create(
			scope = manager.context.scope,
			id = trackerId,
			name = "VMC-Bone-$unityName",
			boneId = boneId,
			intendedBoneId = boneId,
			deviceId = device.context.state.value.id,
			hardwareId = "vmc:bone:$hardwareKey",
			origin = DeviceOrigin.VMC,
			appContext = appContext,
		)
		appContext.server.context.dispatch(VRServerActions.NewTracker(trackerId, runtimeTracker))
		runtimeTracker.context.dispatch(TrackerActions.SetStatus(TrackerStatus.OK))
		boneTrackerIds[boneId] = trackerId
		return runtimeTracker
	}

	fun poseTracker(serial: String): Tracker {
		poseTrackerIds[serial]?.let { id -> appContext.server.getTracker(id)?.let { existing -> return existing } }

		val device = findOrCreateDevice()
		val trackerId = appContext.server.nextHandle()
		val runtimeTracker = Tracker.create(
			scope = manager.context.scope,
			id = trackerId,
			name = "VMC-Tracker-$serial",
			boneId = null,
			intendedBoneId = null,
			deviceId = device.context.state.value.id,
			hardwareId = "vmc:tracker:$serial",
			origin = DeviceOrigin.VMC,
			appContext = appContext,
		)
		appContext.server.context.dispatch(VRServerActions.NewTracker(trackerId, runtimeTracker))
		runtimeTracker.context.dispatch(TrackerActions.SetStatus(TrackerStatus.OK))
		poseTrackerIds[serial] = trackerId
		return runtimeTracker
	}

	fun setStatus(status: TrackerStatus) {
		deviceId?.let { id -> appContext.server.getDevice(id) }
			?.context?.dispatch(DeviceActions.Update { copy(status = status) })
		for ((_, trackerId) in boneTrackerIds) {
			appContext.server.getTracker(trackerId)?.context?.dispatch(TrackerActions.SetStatus(status))
		}
		for ((_, trackerId) in poseTrackerIds) {
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
			address = "vmc-receiver",
			origin = DeviceOrigin.VMC,
			protocolVersion = 0,
		)
		device.context.dispatch(
			DeviceActions.Update {
				copy(
					name = "VMC receiver",
					status = TrackerStatus.OK,
				)
			},
		)
		appContext.server.context.dispatch(VRServerActions.NewDevice(id, device))
		deviceId = id
		return device
	}
}

class VMCInputBehaviour(
	private val appContext: AppContextProvider,
	private val settings: Settings,
) : VMCBehaviour {
	internal class InputRuntime(definition: CompiledSkeleton) {
		var lastFrame: VmcInputFrame = emptyVmcInputFrame(definition)
	}

	override fun observe(receiver: VMCManager) {
		val registry = VmcTrackerRegistry(appContext, receiver)
		val runtime = InputRuntime(appContext.skeleton.definition)
		var oscReceiver: OscReceiver? = null

		settings.context.state
			.map { Pair(it.data.vmcConfig.enabled, it.data.vmcConfig.portIn) }
			.distinctUntilChanged()
			.onEach { (enabled, portIn) ->
				oscReceiver?.close()
				oscReceiver = null

				if (!enabled) {
					registry.setStatus(TrackerStatus.DISCONNECTED)
					receiver.context.dispatch(VMCActions.SetInput(state = VMCOSCInputState.IDLE))
					return@onEach
				}

				val newReceiver = try {
					OscReceiver(portIn)
				} catch (e: Exception) {
					dispatchInputError(
						receiver = receiver,
						port = portIn,
						message = "Failed to start VMC receiver",
						throwable = e,
					)
					return@onEach
				}
				oscReceiver = newReceiver
				receiver.context.dispatch(
					VMCActions.SetInput(state = VMCOSCInputState.LISTENING, port = portIn),
				)
				AppLogger.vmc.info("VMC input listening on port $portIn")

				receiver.context.scope.launch {
					try {
						newReceiver.listenBundles { bundle -> handleBundle(bundle, runtime, registry, receiver, portIn) }
					} catch (e: Exception) {
						dispatchInputError(
							receiver = receiver,
							port = portIn,
							message = "VMC receiver error",
							throwable = e,
						)
					}
				}
			}.launchIn(receiver.context.scope)
	}

	private suspend fun dispatchInputError(
		receiver: VMCManager,
		port: Int?,
		message: String,
		throwable: Throwable,
	) {
		AppLogger.vmc.error(throwable, message)
		receiver.context.dispatch(
			VMCActions.SetInput(
				state = VMCOSCInputState.ERROR,
				port = port,
				error = formatExceptionMessage(message, throwable),
			),
		)
	}

	internal fun handleBundle(
		bundle: OscBundle,
		runtime: InputRuntime,
		registry: VmcTrackerRegistry,
		receiver: VMCManager,
		portIn: Int,
	) {
		val definition = appContext.skeleton.definition
		forEachOscMessage(bundle) { msg -> decodeVmcMessage(msg, runtime.lastFrame, definition) }
		val frame = runtime.lastFrame

		val vrmHeight = receiver.context.state.value.vrm?.vrmHeight
		val skeletonHeight = appContext.skeleton.context.state.value.skeletonHeight
		val scale = if (vrmHeight != null && vrmHeight > 0f) skeletonHeight / vrmHeight else 1f

		val worldTransforms = vmcWorldTransforms(
			definition = definition,
			locals = frame.boneLocalRotations,
			localPositions = frame.boneLocalPositions,
			rootRotation = frame.rootRotation,
			rootPosition = frame.rootPosition,
			scale = scale,
		)

		for (boneId in frame.boneLocalRotations.keys) {
			val transform = worldTransforms[boneId] ?: continue
			val unityName = definition.vmcOutputOf(boneId)?.names?.first() ?: continue
			val tracker = registry.boneTracker(boneId, unityName)
			tracker.context.dispatch(TrackerActions.SetStatus(TrackerStatus.OK))
			tracker.context.dispatch(TrackerActions.SetRotation(rotation = transform.rotation, position = transform.position))
		}

		for (pose in frame.poseTrackers.values) {
			val tracker = registry.poseTracker(pose.serial)
			val position = if (pose.deviceScale) pose.position else pose.position * scale
			tracker.context.dispatch(TrackerActions.SetStatus(TrackerStatus.OK))
			tracker.context.dispatch(TrackerActions.SetRotation(rotation = pose.rotation, position = position))
		}

		registry.setStatus(TrackerStatus.OK)

		receiver.context.dispatchAll(
			listOf(
				VMCActions.SetInput(state = VMCOSCInputState.LISTENING, port = portIn),
				VMCActions.SetLastReceivedInput(System.currentTimeMillis()),
			),
		)
	}
}
