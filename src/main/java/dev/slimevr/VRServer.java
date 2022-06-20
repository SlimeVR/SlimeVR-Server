package dev.slimevr;

import dev.slimevr.autobone.AutoBoneHandler;
import dev.slimevr.bridge.Bridge;
import dev.slimevr.bridge.VMCBridge;
import dev.slimevr.config.ConfigManager;
import dev.slimevr.platform.windows.WindowsNamedPipeBridge;
import dev.slimevr.poserecorder.BVHRecorder;
import dev.slimevr.protocol.ProtocolAPI;
import dev.slimevr.serial.SerialHandler;
import dev.slimevr.util.ann.VRServerThread;
import dev.slimevr.vr.DeviceManager;
import dev.slimevr.vr.processor.HumanPoseProcessor;
import dev.slimevr.vr.processor.skeleton.Skeleton;
import dev.slimevr.vr.trackers.*;
import dev.slimevr.vr.trackers.udp.TrackersUDPServer;
import dev.slimevr.websocketapi.WebSocketVRBridge;
import io.eiren.util.OperatingSystem;
import io.eiren.util.ann.ThreadSafe;
import io.eiren.util.ann.ThreadSecure;
import io.eiren.util.collections.FastList;
import solarxr_protocol.datatypes.TrackerIdT;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;


public class VRServer extends Thread {

	public final HumanPoseProcessor humanPoseProcessor;
	public final HMDTracker hmdTracker;
	private final List<Tracker> trackers = new FastList<>();
	private final TrackersUDPServer trackersServer;
	private final List<Bridge> bridges = new FastList<>();
	private final Queue<Runnable> tasks = new LinkedBlockingQueue<>();
	private final List<Consumer<Tracker>> newTrackersConsumers = new FastList<>();
	private final List<Runnable> onTick = new FastList<>();
	private final List<? extends ShareableTracker> shareTrackers;
	private final DeviceManager deviceManager;
	private final BVHRecorder bvhRecorder;
	private final SerialHandler serialHandler;
	private final AutoBoneHandler autoBoneHandler;
	private final ProtocolAPI protocolAPI;

	private final ConfigManager configManager;

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
		humanPoseProcessor = new HumanPoseProcessor(this, hmdTracker);
		shareTrackers = humanPoseProcessor.getComputedTrackers();

		// Start server for SlimeVR trackers
		trackersServer = new TrackersUDPServer(6969, "Sensors UDP server", this::registerTracker);

		// OpenVR bridge currently only supports Windows
		if (OperatingSystem.getCurrentPlatform() == OperatingSystem.WINDOWS) {

			// Create named pipe bridge for SteamVR driver
			WindowsNamedPipeBridge driverBridge = new WindowsNamedPipeBridge(
				this,
				hmdTracker,
				"steamvr",
				"SteamVR Driver Bridge",
				"\\\\.\\pipe\\SlimeVRDriver",
				shareTrackers
			);
			tasks.add(() -> driverBridge.startBridge());
			bridges.add(driverBridge);

			// Create named pipe bridge for SteamVR input
			// TODO: how do we want to handle HMD input from the feeder app?
			WindowsNamedPipeBridge feederBridge = new WindowsNamedPipeBridge(
				this,
				null,
				"steamvr_feeder",
				"SteamVR Feeder Bridge",
				"\\\\.\\pipe\\SlimeVRInput",
				new FastList<ShareableTracker>()
			);
			tasks.add(() -> feederBridge.startBridge());
			bridges.add(feederBridge);
		}

		// Create WebSocket server
		WebSocketVRBridge wsBridge = new WebSocketVRBridge(hmdTracker, shareTrackers, this);
		tasks.add(() -> wsBridge.startBridge());
		bridges.add(wsBridge);

		// Create VMCBridge
		try {
			VMCBridge vmcBridge = new VMCBridge(39539, 39540, InetAddress.getLocalHost());
			tasks.add(() -> vmcBridge.startBridge());
			bridges.add(vmcBridge);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

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
			humanPoseProcessor.trackerUpdated(tracker);
			this.getConfigManager().getVrConfig().writeTrackerConfig(tracker);
			this.getConfigManager().saveConfig();
		});
	}

	@ThreadSafe
	public void addSkeletonUpdatedCallback(Consumer<Skeleton> consumer) {
		queueTask(() -> {
			humanPoseProcessor.addSkeletonUpdatedCallback(consumer);
		});
	}

	@Override
	@VRServerThread
	public void run() {
		trackersServer.start();
		while (true) {
			// final long start = System.currentTimeMillis();
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
			humanPoseProcessor.update();
			for (Bridge bridge : bridges) {
				bridge.dataWrite();
			}
			// final long time = System.currentTimeMillis() - start;
			try {
				Thread.sleep(1); // 1000Hz
			} catch (InterruptedException e) {}
		}
	}

	@ThreadSafe
	public void queueTask(Runnable r) {
		tasks.add(r);
	}

	@VRServerThread
	private void trackerAdded(Tracker tracker) {
		humanPoseProcessor.trackerAdded(tracker);
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
		queueTask(() -> {
			humanPoseProcessor.resetTrackers();
		});
	}

	public void resetTrackersYaw() {
		queueTask(() -> {
			humanPoseProcessor.resetTrackersYaw();
		});
	}

	public void setFloorClip(boolean value) {
		queueTask(() -> {
			humanPoseProcessor.setFloorClip(value);
		});
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

	public DeviceManager getDeviceManager() {
		return deviceManager;
	}

	public ConfigManager getConfigManager() {
		return configManager;
	}
}
