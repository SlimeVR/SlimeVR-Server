package dev.slimevr

import com.jme3.system.NanoTimer
import dev.slimevr.autobone.AutoBoneHandler
import dev.slimevr.bridge.Bridge
import dev.slimevr.bridge.ISteamVRBridge
import dev.slimevr.config.ConfigManager
import dev.slimevr.firmware.FirmwareUpdateHandler
import dev.slimevr.firmware.SerialFlashingHandler
import dev.slimevr.games.vrchat.VRCConfigHandler
import dev.slimevr.games.vrchat.VRCConfigHandlerStub
import dev.slimevr.games.vrchat.VRChatConfigManager
import dev.slimevr.guards.ServerGuards
import dev.slimevr.osc.OSCHandler
import dev.slimevr.osc.OSCRouter
import dev.slimevr.osc.VMCHandler
import dev.slimevr.osc.VRCOSCHandler
import dev.slimevr.posestreamer.BVHRecorder
import dev.slimevr.protocol.ProtocolAPI
import dev.slimevr.protocol.rpc.settings.RPCSettingsHandler
import dev.slimevr.reset.ResetHandler
import dev.slimevr.reset.ResetTimerManager
import dev.slimevr.reset.resetTimer
import dev.slimevr.serial.ProvisioningHandler
import dev.slimevr.serial.SerialHandler
import dev.slimevr.serial.SerialHandlerStub
import dev.slimevr.setup.HandshakeHandler
import dev.slimevr.setup.TapSetupHandler
import dev.slimevr.status.StatusSystem
import dev.slimevr.tracking.processor.HumanPoseManager
import dev.slimevr.tracking.processor.skeleton.HumanSkeleton
import dev.slimevr.tracking.trackers.*
import dev.slimevr.tracking.trackers.udp.TrackersUDPServer
import dev.slimevr.trackingchecklist.TrackingChecklistManager
import dev.slimevr.util.ann.VRServerThread
import dev.slimevr.websocketapi.WebSocketVRBridge
import io.eiren.util.ann.ThreadSafe
import io.eiren.util.ann.ThreadSecure
import io.eiren.util.collections.FastList
import io.eiren.util.logging.LogManager
import solarxr_protocol.datatypes.TrackerIdT
import solarxr_protocol.rpc.ResetType
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Consumer
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

typealias BridgeProvider = (
	server: VRServer,
	computedTrackers: List<Tracker>,
) -> Sequence<Bridge>

const val SLIMEVR_IDENTIFIER = "dev.slimevr.SlimeVR"

class VRServer @JvmOverloads constructor(
	bridgeProvider: BridgeProvider = { _, _ -> sequence {} },
	serialHandlerProvider: (VRServer) -> SerialHandler = { _ -> SerialHandlerStub() },
	flashingHandlerProvider: (VRServer) -> SerialFlashingHandler? = { _ -> null },
	vrcConfigHandlerProvider: (VRServer) -> VRCConfigHandler = { _ -> VRCConfigHandlerStub() },
	networkProfileProvider: (VRServer) -> NetworkProfileChecker = { _ -> NetworkProfileCheckerStub() },
	acquireMulticastLock: () -> Any? = { null },
	@JvmField val configManager: ConfigManager,
) : Thread("VRServer") {

	@JvmField
	val humanPoseManager: HumanPoseManager
	private val trackers: MutableList<Tracker> = FastList()
	val trackersServer: TrackersUDPServer
	private val bridges: MutableList<Bridge> = FastList()
	private val tasks: Queue<Runnable> = LinkedBlockingQueue()
	private val newTrackersConsumers: MutableList<Consumer<Tracker>> = FastList()
	private val trackerStatusListeners: MutableList<TrackerStatusListener> = FastList()
	private val onTick: MutableList<Runnable> = FastList()
	private val lock = acquireMulticastLock()
	val oSCRouter: OSCRouter

	@JvmField
	val vrcOSCHandler: VRCOSCHandler
	val vMCHandler: VMCHandler

	@JvmField
	val deviceManager: DeviceManager

	@JvmField
	val bvhRecorder: BVHRecorder

	@JvmField
	val serialHandler: SerialHandler

	var serialFlashingHandler: SerialFlashingHandler?

	val firmwareUpdateHandler: FirmwareUpdateHandler

	val vrcConfigManager: VRChatConfigManager

	@JvmField
	val autoBoneHandler: AutoBoneHandler

	@JvmField
	val tapSetupHandler: TapSetupHandler

	@JvmField
	val protocolAPI: ProtocolAPI
	private val timer = Timer()
	private val resetTimerManager = ResetTimerManager()
	val fpsTimer = NanoTimer()

	@JvmField
	val provisioningHandler: ProvisioningHandler

	@JvmField
	val resetHandler: ResetHandler

	@JvmField
	val statusSystem = StatusSystem()

	@JvmField
	val handshakeHandler = HandshakeHandler()

	val trackingChecklistManager: TrackingChecklistManager

	val networkProfileChecker: NetworkProfileChecker

	val serverGuards = ServerGuards()

	init {
		// UwU
		deviceManager = DeviceManager(this)
		serialHandler = serialHandlerProvider(this)
		serialFlashingHandler = flashingHandlerProvider(this)
		provisioningHandler = ProvisioningHandler(this)
		resetHandler = ResetHandler()
		tapSetupHandler = TapSetupHandler()
		humanPoseManager = HumanPoseManager(this)
		// AutoBone requires HumanPoseManager first
		autoBoneHandler = AutoBoneHandler(this)
		firmwareUpdateHandler = FirmwareUpdateHandler(this)
		vrcConfigManager = VRChatConfigManager(this, vrcConfigHandlerProvider(this))
		networkProfileChecker = networkProfileProvider(this)
		trackingChecklistManager = TrackingChecklistManager(this)
		protocolAPI = ProtocolAPI(this)
		val computedTrackers = humanPoseManager.computedTrackers

		// Start server for SlimeVR trackers
		val trackerPort = configManager.vrConfig.server.trackerPort
		LogManager.info("Starting the tracker server on port $trackerPort...")
		trackersServer = TrackersUDPServer(
			trackerPort,
			"Sensors UDP server",
		) { tracker: Tracker -> registerTracker(tracker) }

		// Start bridges and WebSocket server
		for (bridge in bridgeProvider(this, computedTrackers) + sequenceOf(WebSocketVRBridge(computedTrackers, this))) {
			tasks.add(Runnable { bridge.startBridge() })
			bridges.add(bridge)
		}

		// Initialize OSC handlers
		vrcOSCHandler = VRCOSCHandler(
			this,
			configManager.vrConfig.vrcOSC,
			computedTrackers,
		)
		vMCHandler = VMCHandler(
			this,
			humanPoseManager,
			configManager.vrConfig.vmc,
		)

		// Initialize OSC router
		val oscHandlers = FastList<OSCHandler>()
		oscHandlers.add(vrcOSCHandler)
		oscHandlers.add(vMCHandler)
		oSCRouter = OSCRouter(configManager.vrConfig.oscRouter, oscHandlers)
		bvhRecorder = BVHRecorder(this)
		for (tracker in computedTrackers) {
			registerTracker(tracker)
		}

		instance = this
	}

	fun hasBridge(bridgeClass: Class<out Bridge?>): Boolean {
		for (bridge in bridges) {
			if (bridgeClass.isAssignableFrom(bridge.javaClass)) {
				return true
			}
		}
		return false
	}

	// FIXME: Code using this function normally uses this to get the SteamVR driver but
	// 		that's because we first save the SteamVR driver bridge and then the feeder in the array.
	// 		Not really a great thing to have.
	@ThreadSafe
	fun <E : Bridge?> getVRBridge(bridgeClass: Class<E>): E? {
		for (bridge in bridges) {
			if (bridgeClass.isAssignableFrom(bridge.javaClass)) {
				return bridgeClass.cast(bridge)
			}
		}
		return null
	}

	fun addOnTick(runnable: Runnable) {
		onTick.add(runnable)
	}

	@ThreadSafe
	fun addNewTrackerConsumer(consumer: Consumer<Tracker>) {
		queueTask {
			newTrackersConsumers.add(consumer)
			for (tracker in trackers) {
				consumer.accept(tracker)
			}
		}
	}

	@ThreadSafe
	fun trackerUpdated(tracker: Tracker?) {
		queueTask {
			humanPoseManager.trackerUpdated(tracker)
			updateSkeletonModel()
			refreshTrackersDriftCompensationEnabled()
			configManager.vrConfig.writeTrackerConfig(tracker)
			configManager.saveConfig()
		}
	}

	@ThreadSafe
	fun addSkeletonUpdatedCallback(consumer: Consumer<HumanSkeleton>) {
		queueTask { humanPoseManager.addSkeletonUpdatedCallback(consumer) }
	}

	@VRServerThread
	override fun run() {
		trackersServer.start()
		while (true) {
			// final long start = System.currentTimeMillis();
			fpsTimer.update()
			do {
				val task = tasks.poll() ?: break
				task.run()
			} while (true)
			for (task in onTick) {
				task.run()
			}
			for (bridge in bridges) {
				bridge.dataRead()
			}
			for (tracker in trackers) {
				tracker.tick(fpsTimer.timePerFrame)
			}
			humanPoseManager.update()
			for (bridge in bridges) {
				bridge.dataWrite()
			}
			vrcOSCHandler.update()
			vMCHandler.update()
			// final long time = System.currentTimeMillis() - start;
			try {
				sleep(1) // 1000Hz
			} catch (error: InterruptedException) {
				LogManager.info("VRServer thread interrupted")
				break
			}
		}
	}

	@ThreadSafe
	fun queueTask(r: Runnable) {
		tasks.add(r)
	}

	@VRServerThread
	private fun trackerAdded(tracker: Tracker) {
		humanPoseManager.trackerAdded(tracker)
		updateSkeletonModel()
		if (tracker.isComputed) {
			vMCHandler.addComputedTracker(tracker)
		}
		refreshTrackersDriftCompensationEnabled()
	}

	@ThreadSecure
	fun registerTracker(tracker: Tracker) {
		configManager.vrConfig.readTrackerConfig(tracker)
		queueTask {
			trackers.add(tracker)
			trackerAdded(tracker)
			for (tc in newTrackersConsumers) {
				tc.accept(tracker)
			}
		}
	}

	@ThreadSafe
	fun updateSkeletonModel() {
		queueTask {
			humanPoseManager.updateSkeletonModelFromServer()
			vrcOSCHandler.setHeadTracker(TrackerUtils.getTrackerForSkeleton(trackers, TrackerPosition.HEAD))
			if (this.getVRBridge(ISteamVRBridge::class.java)?.updateShareSettingsAutomatically() == true) {
				RPCSettingsHandler.sendSteamVRUpdatedSettings(protocolAPI, protocolAPI.rpcHandler)
			}
		}
	}

	fun resetTrackersFull(resetSourceName: String?, bodyParts: List<Int> = ArrayList()) {
		queueTask { humanPoseManager.resetTrackersFull(resetSourceName, bodyParts) }
	}

	fun resetTrackersYaw(resetSourceName: String?, bodyParts: List<Int> = TrackerUtils.allBodyPartsButFingers) {
		queueTask { humanPoseManager.resetTrackersYaw(resetSourceName, bodyParts) }
	}

	fun resetTrackersMounting(resetSourceName: String?, bodyParts: List<Int>? = null) {
		queueTask { humanPoseManager.resetTrackersMounting(resetSourceName, bodyParts) }
	}

	fun clearTrackersMounting(resetSourceName: String?) {
		queueTask { humanPoseManager.clearTrackersMounting(resetSourceName) }
	}

	fun getPauseTracking(): Boolean = humanPoseManager.getPauseTracking()

	fun setPauseTracking(pauseTracking: Boolean, sourceName: String?) {
		queueTask {
			humanPoseManager.setPauseTracking(pauseTracking, sourceName)
			// Toggle trackers as they don't toggle when tracking is paused
			if (this.getVRBridge(ISteamVRBridge::class.java)?.updateShareSettingsAutomatically() == true) {
				RPCSettingsHandler.sendSteamVRUpdatedSettings(protocolAPI, protocolAPI.rpcHandler)
			}
		}
	}

	fun togglePauseTracking(sourceName: String?) {
		queueTask {
			humanPoseManager.togglePauseTracking(sourceName)
			// Toggle trackers as they don't toggle when tracking is paused
			if (this.getVRBridge(ISteamVRBridge::class.java)?.updateShareSettingsAutomatically() == true) {
				RPCSettingsHandler.sendSteamVRUpdatedSettings(protocolAPI, protocolAPI.rpcHandler)
			}
		}
	}

	fun scheduleResetTrackersFull(resetSourceName: String?, delay: Long, bodyParts: List<Int> = ArrayList()) {
		resetTimer(
			resetTimerManager,
			delay,
			onTick = { progress ->
				resetHandler.sendStarted(ResetType.Full, bodyParts, progress, delay.toInt())
			},
			onComplete = {
				queueTask {
					humanPoseManager.resetTrackersFull(resetSourceName, bodyParts)
					resetHandler.sendFinished(ResetType.Full, bodyParts, delay.toInt())
				}
			},
		)
	}

	fun scheduleResetTrackersYaw(resetSourceName: String?, delay: Long, bodyParts: List<Int> = TrackerUtils.allBodyPartsButFingers) {
		resetTimer(
			resetTimerManager,
			delay,
			onTick = { progress ->
				resetHandler.sendStarted(ResetType.Yaw, bodyParts, progress, delay.toInt())
			},
			onComplete = {
				queueTask {
					humanPoseManager.resetTrackersYaw(resetSourceName, bodyParts)
					resetHandler.sendFinished(ResetType.Yaw, bodyParts, delay.toInt())
				}
			},
		)
	}

	fun scheduleResetTrackersMounting(resetSourceName: String?, delay: Long, bodyParts: List<Int>? = null) {
		resetTimer(
			resetTimerManager,
			delay,
			onTick = { progress ->
				resetHandler.sendStarted(ResetType.Mounting, bodyParts, progress, delay.toInt())
			},
			onComplete = {
				queueTask {
					humanPoseManager.resetTrackersMounting(resetSourceName, bodyParts)
					resetHandler.sendFinished(ResetType.Mounting, bodyParts, delay.toInt())
				}
			},
		)
	}

	fun scheduleSetPauseTracking(pauseTracking: Boolean, sourceName: String?, delay: Long) {
		timer.schedule(delay) {
			queueTask { humanPoseManager.setPauseTracking(pauseTracking, sourceName) }
		}
	}

	fun scheduleTogglePauseTracking(sourceName: String?, delay: Long) {
		timer.schedule(delay) {
			queueTask { humanPoseManager.togglePauseTracking(sourceName) }
		}
	}

	fun setLegTweaksEnabled(value: Boolean) {
		queueTask { humanPoseManager.setLegTweaksEnabled(value) }
	}

	fun setSkatingReductionEnabled(value: Boolean) {
		queueTask { humanPoseManager.setSkatingCorrectionEnabled(value) }
	}

	fun setFloorClipEnabled(value: Boolean) {
		queueTask { humanPoseManager.setFloorClipEnabled(value) }
	}

	val trackersCount: Int
		get() = trackers.size
	val allTrackers: List<Tracker>
		get() = FastList(trackers)

	fun getTrackerById(id: TrackerIdT): Tracker? {
		for (tracker in trackers) {
			if (tracker.trackerNum != id.trackerNum) {
				continue
			}

			// Handle synthetic devices
			if (id.deviceId == null && tracker.device == null) {
				return tracker
			}
			if (tracker.device != null && id.deviceId != null && id.deviceId.id == tracker.device.id) {
				// This is a physical tracker, and both device id and the
				// tracker num match
				return tracker
			}
		}
		return null
	}

	fun clearTrackersDriftCompensation() {
		for (t in allTrackers) {
			if (t.isImu()) {
				t.resetsHandler.clearDriftCompensation()
			}
		}
	}

	fun refreshTrackersDriftCompensationEnabled() {
		for (t in allTrackers) {
			if (t.isImu()) {
				t.resetsHandler.refreshDriftCompensationEnabled()
			}
		}
	}

	fun trackerStatusChanged(tracker: Tracker, oldStatus: TrackerStatus, newStatus: TrackerStatus) {
		trackerStatusListeners.forEach { it.onTrackerStatusChanged(tracker, oldStatus, newStatus) }
	}

	fun addTrackerStatusListener(listener: TrackerStatusListener) {
		trackerStatusListeners.add(listener)
	}

	fun removeTrackerStatusListener(listener: TrackerStatusListener) {
		trackerStatusListeners.removeIf { listener == it }
	}

	companion object {
		private val nextLocalTrackerId = AtomicInteger()
		lateinit var instance: VRServer
			private set

		val instanceInitialized: Boolean
			get() = ::instance.isInitialized

		@JvmStatic
		fun getNextLocalTrackerId(): Int = nextLocalTrackerId.incrementAndGet()

		@JvmStatic
		val currentLocalTrackerId: Int
			get() = nextLocalTrackerId.get()
	}
}
