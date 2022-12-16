package dev.slimevr;

import com.jme3.system.NanoTimer;
import dev.slimevr.autobone.AutoBoneHandler;
import dev.slimevr.bridge.Bridge;
import dev.slimevr.config.ConfigManager;
import dev.slimevr.osc.OSCHandler;
import dev.slimevr.osc.OSCRouter;
import dev.slimevr.osc.VMCHandler;
import dev.slimevr.osc.VRCOSCHandler;
import dev.slimevr.platform.SteamVRBridge;
import dev.slimevr.platform.linux.UnixSocketBridge;
import dev.slimevr.platform.windows.WindowsNamedPipeBridge;
import dev.slimevr.poserecorder.BVHRecorder;
import dev.slimevr.protocol.ProtocolAPI;
import dev.slimevr.serial.SerialHandler;
import dev.slimevr.util.ann.VRServerThread;
import dev.slimevr.tracking.DeviceManager;
import dev.slimevr.tracking.processor.HumanPoseManager;
import dev.slimevr.tracking.processor.skeleton.HumanSkeleton;
import dev.slimevr.tracking.trackers.HMDTracker;
import dev.slimevr.tracking.trackers.ShareableTracker;
import dev.slimevr.tracking.trackers.Tracker;
import dev.slimevr.tracking.trackers.udp.TrackersUDPServer;
import dev.slimevr.websocketapi.WebSocketVRBridge;
import io.eiren.util.OperatingSystem;
import io.eiren.util.ann.ThreadSafe;
import io.eiren.util.ann.ThreadSecure;
import io.eiren.util.collections.FastList;
import io.eiren.util.logging.LogManager;
import solarxr_protocol.datatypes.TrackerIdT;

import java.util.List;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;


public class VRServer extends Thread {

	public final HumanPoseManager humanPoseManager;
	public final HMDTracker hmdTracker;
	private final List<Tracker> trackers = new FastList<>();
	private final TrackersUDPServer trackersServer;
	private final List<Bridge> bridges = new FastList<>();
	private final Queue<Runnable> tasks = new LinkedBlockingQueue<>();
	private final List<Consumer<Tracker>> newTrackersConsumers = new FastList<>();
	private final List<Runnable> onTick = new FastList<>();
	private final List<? extends ShareableTracker> shareTrackers;
	private final OSCRouter oscRouter;
	private final VRCOSCHandler vrcOSCHandler;
	private final VMCHandler vmcHandler;
	private final DeviceManager deviceManager;
	private final BVHRecorder bvhRecorder;
	private final SerialHandler serialHandler;
	private final AutoBoneHandler autoBoneHandler;
	private final ProtocolAPI protocolAPI;
	private final ConfigManager configManager;
	private final Timer timer = new Timer();
	private final NanoTimer fpsTimer = new NanoTimer();

	/**
	 * This function is used by VRWorkout, do not remove!
	 */
	public VRServer() {
		this("vrconfig.yml");
	}

	public VRServer(String configPath) {
		super("VRServer");

		this.configManager = new ConfigManager(configPath);
		this.configManager.loadConfig();

		deviceManager = new DeviceManager(this);

		serialHandler = new SerialHandler();
		autoBoneHandler = new AutoBoneHandler(this);
		protocolAPI = new ProtocolAPI(this);

		hmdTracker = new HMDTracker("HMD");
		hmdTracker.position.set(0, 1.8f, 0); // Set starting position for easier
												// debugging
		// TODO Multiple processors
		humanPoseManager = new HumanPoseManager(this);
		shareTrackers = humanPoseManager.getShareableTracker();

		// Start server for SlimeVR trackers
		int trackerPort = configManager.getVrConfig().getServer().getTrackerPort();
		LogManager.info("Starting the tracker server on port " + trackerPort + "...");
		trackersServer = new TrackersUDPServer(
			trackerPort,
			"Sensors UDP server",
			this::registerTracker
		);

		// OpenVR bridge currently only supports Windows
		final SteamVRBridge driverBridge;
		if (OperatingSystem.getCurrentPlatform() == OperatingSystem.WINDOWS) {

			// Create named pipe bridge for SteamVR driver
			driverBridge = new WindowsNamedPipeBridge(
				this,
				hmdTracker,
				"steamvr",
				"SteamVR Driver Bridge",
				"\\\\.\\pipe\\SlimeVRDriver",
				shareTrackers
			);
			tasks.add(driverBridge::startBridge);
			bridges.add(driverBridge);

			// Create named pipe bridge for SteamVR input
			// TODO: how do we want to handle HMD input from the feeder app?
			WindowsNamedPipeBridge feederBridge = new WindowsNamedPipeBridge(
				this,
				null,
				"steamvr_feeder",
				"SteamVR Feeder Bridge",
				"\\\\.\\pipe\\SlimeVRInput",
				new FastList<>()
			);
			tasks.add(feederBridge::startBridge);
			bridges.add(feederBridge);
		} else if (OperatingSystem.getCurrentPlatform() == OperatingSystem.LINUX) {
			SteamVRBridge linuxBridge = null;
			try {
				linuxBridge = new UnixSocketBridge(
					this,
					hmdTracker,
					"steamvr",
					"SteamVR Driver Bridge",
					"/tmp/SlimeVRDriver",
					shareTrackers
				);
			} catch (Exception ex) {
				LogManager.severe("Failed to initiate Unix socket, disabling driver bridge...", ex);
			}
			driverBridge = linuxBridge;
			if (driverBridge != null) {
				tasks.add(driverBridge::startBridge);
				bridges.add(driverBridge);
			}
		} else {
			driverBridge = null;
		}

		// Add shutdown hook
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				if (driverBridge instanceof UnixSocketBridge linuxBridge) {
					// Auto-close Linux SteamVR bridge on JVM shutdown
					linuxBridge.close();
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}));

		// Create WebSocket server
		WebSocketVRBridge wsBridge = new WebSocketVRBridge(hmdTracker, shareTrackers, this);
		tasks.add(wsBridge::startBridge);
		bridges.add(wsBridge);

		// Initialize OSC handlers
		vrcOSCHandler = new VRCOSCHandler(
			this,
			hmdTracker,
			humanPoseManager,
			driverBridge,
			getConfigManager().getVrConfig().getVrcOSC(),
			shareTrackers
		);
		vmcHandler = new VMCHandler(
			this,
			humanPoseProcessor,
			getConfigManager().getVrConfig().getVMC()
		);

		// Initialize OSC router
		FastList<OSCHandler> oscHandlers = new FastList<>();
		oscHandlers.add(vrcOSCHandler);
		oscHandlers.add(vmcHandler);
		oscRouter = new OSCRouter(getConfigManager().getVrConfig().getOscRouter(), oscHandlers);

		bvhRecorder = new BVHRecorder(this);

		registerTracker(hmdTracker);
		for (Tracker tracker : shareTrackers) {
			registerTracker(tracker);
		}
	}

	public boolean hasBridge(Class<? extends Bridge> bridgeClass) {
		for (Bridge bridge : bridges) {
			if (bridgeClass.isAssignableFrom(bridge.getClass())) {
				return true;
			}
		}
		return false;
	}

	@ThreadSafe
	public <E extends Bridge> E getVRBridge(Class<E> bridgeClass) {
		for (Bridge bridge : bridges) {
			if (bridgeClass.isAssignableFrom(bridge.getClass())) {
				return bridgeClass.cast(bridge);
			}
		}
		return null;
	}


	public void addOnTick(Runnable runnable) {
		this.onTick.add(runnable);
	}

	@ThreadSafe
	public void addNewTrackerConsumer(Consumer<Tracker> consumer) {
		queueTask(() -> {
			newTrackersConsumers.add(consumer);
			for (Tracker tracker : trackers) {
				consumer.accept(tracker);
			}
		});
	}

	@ThreadSafe
	public void trackerUpdated(Tracker tracker) {
		queueTask(() -> {
			humanPoseManager.trackerUpdated(tracker);
			this.getConfigManager().getVrConfig().writeTrackerConfig(tracker);
			this.getConfigManager().saveConfig();
		});
	}

	@ThreadSafe
	public void addSkeletonUpdatedCallback(Consumer<HumanSkeleton> consumer) {
		queueTask(() -> humanPoseManager.addSkeletonUpdatedCallback(consumer));
	}

	@Override
	@VRServerThread
	public void run() {
		trackersServer.start();
		while (true) {
			// final long start = System.currentTimeMillis();
			fpsTimer.update();
			do {
				Runnable task = tasks.poll();
				if (task == null)
					break;
				task.run();
			} while (true);
			for (Runnable task : onTick) {
				task.run();
			}
			for (Bridge bridge : bridges) {
				bridge.dataRead();
			}
			for (Tracker tracker : trackers) {
				tracker.tick();
			}
			humanPoseManager.update();
			for (Bridge bridge : bridges) {
				bridge.dataWrite();
			}
			vrcOSCHandler.update();
			vmcHandler.update();
			// final long time = System.currentTimeMillis() - start;
			try {
				Thread.sleep(1); // 1000Hz
			} catch (InterruptedException ignored) {}
		}
	}

	@ThreadSafe
	public void queueTask(Runnable r) {
		tasks.add(r);
	}

	@VRServerThread
	private void trackerAdded(Tracker tracker) {
		humanPoseManager.trackerAdded(tracker);
	}

	@ThreadSecure
	public void registerTracker(Tracker tracker) {
		this.getConfigManager().getVrConfig().readTrackerConfig(tracker);
		queueTask(() -> {
			trackers.add(tracker);
			trackerAdded(tracker);
			for (Consumer<Tracker> tc : newTrackersConsumers) {
				tc.accept(tracker);
			}
		});
	}

	public void resetTrackers() {
		queueTask(humanPoseManager::resetTrackersFull);
	}

	public void resetTrackersYaw() {
		queueTask(humanPoseManager::resetTrackersYaw);
	}

	public void resetTrackersMounting() {
		queueTask(humanPoseManager::resetTrackersMounting);
	}

	public void scheduleResetTrackers(long delay) {
		TimerTask resetTask = new resetTask();
		timer.schedule(resetTask, delay);
	}

	public void scheduleResetTrackersYaw(long delay) {
		TimerTask yawResetTask = new yawResetTask();
		timer.schedule(yawResetTask, delay);
	}

	public void scheduleResetTrackersMounting(long delay) {
		TimerTask resetMountingTask = new resetMountingTask();
		timer.schedule(resetMountingTask, delay);
	}

	class resetTask extends TimerTask {
		public void run() {
			queueTask(humanPoseManager::resetTrackersFull);
		}
	}

	class yawResetTask extends TimerTask {
		public void run() {
			queueTask(humanPoseManager::resetTrackersYaw);
		}
	}

	class resetMountingTask extends TimerTask {
		public void run() {
			queueTask(humanPoseManager::resetTrackersMounting);
		}
	}

	public void setLegTweaksEnabled(boolean value) {
		queueTask(() -> humanPoseManager.setLegTweaksEnabled(value));
	}

	public void setSkatingReductionEnabled(boolean value) {
		queueTask(() -> humanPoseManager.setSkatingCorrectionEnabled(value));
	}

	public void setFloorClipEnabled(boolean value) {
		queueTask(() -> humanPoseManager.setFloorClipEnabled(value));
	}

	public int getTrackersCount() {
		return trackers.size();
	}

	public List<Tracker> getAllTrackers() {
		return new FastList<>(trackers);
	}

	public Tracker getTrackerById(TrackerIdT id) {
		for (Tracker tracker : trackers) {
			if (tracker.getTrackerNum() != id.getTrackerNum()) {
				continue;
			}

			// Handle synthetic devices
			if (id.getDeviceId() == null && tracker.getDevice() == null) {
				return tracker;
			}

			if (
				tracker.getDevice() != null
					&& id.getDeviceId() != null
					&& id.getDeviceId().getId() == tracker.getDevice().getId()
			) {
				// This is a physical tracker, and both device id and the
				// tracker num match
				return tracker;
			}
		}
		return null;
	}

	public BVHRecorder getBvhRecorder() {
		return this.bvhRecorder;
	}

	public SerialHandler getSerialHandler() {
		return this.serialHandler;
	}

	public AutoBoneHandler getAutoBoneHandler() {
		return this.autoBoneHandler;
	}

	public ProtocolAPI getProtocolAPI() {
		return protocolAPI;
	}

	public TrackersUDPServer getTrackersServer() {
		return trackersServer;
	}

	public OSCRouter getOSCRouter() {
		return oscRouter;
	}

	public VRCOSCHandler getVrcOSCHandler() {
		return vrcOSCHandler;
	}

	public VMCHandler getVMCHandler() {
		return vmcHandler;
	}

	public DeviceManager getDeviceManager() {
		return deviceManager;
	}

	public ConfigManager getConfigManager() {
		return configManager;
	}

	public NanoTimer getFpsTimer() {
		return fpsTimer;
	}

}
