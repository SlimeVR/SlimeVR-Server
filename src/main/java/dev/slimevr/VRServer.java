package dev.slimevr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

import dev.slimevr.bridge.Bridge;
import dev.slimevr.platform.windows.WindowsNamedPipeBridge;
import dev.slimevr.platform.windows.WindowsSteamVRPipeInputBridge;
import dev.slimevr.bridge.VMCBridge;
import dev.slimevr.bridge.WebSocketVRBridge;
import dev.slimevr.util.ann.VRServerThread;
import dev.slimevr.vr.processor.HumanPoseProcessor;
import dev.slimevr.vr.processor.skeleton.HumanSkeleton;
import dev.slimevr.vr.trackers.HMDTracker;
import dev.slimevr.vr.trackers.ShareableTracker;
import dev.slimevr.vr.trackers.Tracker;
import dev.slimevr.vr.trackers.TrackerConfig;
import dev.slimevr.vr.trackers.udp.TrackersUDPServer;
import io.eiren.util.OperatingSystem;
import io.eiren.util.ann.ThreadSafe;
import io.eiren.util.ann.ThreadSecure;
import io.eiren.util.collections.FastList;
import io.eiren.yaml.YamlException;
import io.eiren.yaml.YamlFile;
import io.eiren.yaml.YamlNode;

public class VRServer extends Thread {
	
	private final List<Tracker> trackers = new FastList<>();
	public final HumanPoseProcessor humanPoseProcessor;
	private final TrackersUDPServer trackersServer;
	private final List<Bridge> bridges = new FastList<>();
	private final Queue<Runnable> tasks = new LinkedBlockingQueue<>();
	private final Map<String, TrackerConfig> configuration = new HashMap<>();
	public final YamlFile config = new YamlFile();
	public final HMDTracker hmdTracker;
	private final List<Consumer<Tracker>> newTrackersConsumers = new FastList<>();
	private final List<Runnable> onTick = new FastList<>();
	private final List<? extends ShareableTracker> shareTrackers;
	
	public VRServer() {
		super("VRServer");
		loadConfig();
		hmdTracker = new HMDTracker("HMD");
		hmdTracker.position.set(0, 1.8f, 0); // Set starting position for easier debugging
		// TODO Multiple processors
		humanPoseProcessor = new HumanPoseProcessor(this, hmdTracker);
		shareTrackers = humanPoseProcessor.getComputedTrackers();
		
		// Start server for SlimeVR trackers
		trackersServer = new TrackersUDPServer(6969, "Sensors UDP server", this::registerTracker);
		
		// OpenVR bridge currently only supports Windows
		if(OperatingSystem.getCurrentPlatform() == OperatingSystem.WINDOWS) {
			
			// Create named pipe bridge for SteamVR driver
			WindowsNamedPipeBridge driverBridge = new WindowsNamedPipeBridge(hmdTracker, "steamvr", "SteamVR Driver Bridge", "\\\\.\\pipe\\SlimeVRDriver", shareTrackers);
			tasks.add(() -> driverBridge.startBridge());
			bridges.add(driverBridge);
			
			// Create named pipe bridge for SteamVR input
			// TODO: how do we want to handle HMD input from the feeder app?
			WindowsNamedPipeBridge feederBridge = new WindowsNamedPipeBridge(null, "steamvr_feeder", "SteamVR Feeder Bridge", "\\\\.\\pipe\\SlimeVRInput", new FastList<ShareableTracker>());
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
		} catch(UnknownHostException e) {
			e.printStackTrace();
		}
		
		registerTracker(hmdTracker);
		for(int i = 0; i < shareTrackers.size(); ++i)
			registerTracker(shareTrackers.get(i));
	}
	
	public boolean hasBridge(Class<? extends Bridge> bridgeClass) {
		for(int i = 0; i < bridges.size(); ++i) {
			if(bridgeClass.isAssignableFrom(bridges.get(i).getClass()))
				return true;
		}
		return false;
	}

	@ThreadSafe
	public <E extends Bridge> E getVRBridge(Class<E> bridgeClass) {
		for(int i = 0; i < bridges.size(); ++i) {
			Bridge b = bridges.get(i);
			if(bridgeClass.isAssignableFrom(b.getClass()))
				return bridgeClass.cast(b);
		}
		return null;
	}
	
	@ThreadSafe
	public TrackerConfig getTrackerConfig(Tracker tracker) {
		synchronized(configuration) {
			TrackerConfig config = configuration.get(tracker.getName());
			if(config == null) {
				config = new TrackerConfig(tracker);
				configuration.put(tracker.getName(), config);
			}
			return config;
		}
	}
	
	private void loadConfig() {
		try {
			config.load(new FileInputStream(new File("vrconfig.yml")));
		} catch(FileNotFoundException e) {
			// Config file didn't exist, is not an error
		} catch(YamlException e) {
			e.printStackTrace();
		}
		List<YamlNode> trackersConfig = config.getNodeList("trackers", null);
		for(int i = 0; i < trackersConfig.size(); ++i) {
			TrackerConfig cfg = new TrackerConfig(trackersConfig.get(i));
			synchronized(configuration) {
				configuration.put(cfg.trackerName, cfg);
			}
		}
	}
	
	public void addOnTick(Runnable runnable) {
		this.onTick.add(runnable);
	}
	
	@ThreadSafe
	public void addNewTrackerConsumer(Consumer<Tracker> consumer) {
		queueTask(() -> {
			newTrackersConsumers.add(consumer);
			for(int i = 0; i < trackers.size(); ++i)
				consumer.accept(trackers.get(i));
		});
	}
	
	@ThreadSafe
	public void trackerUpdated(Tracker tracker) {
		queueTask(() -> {
			humanPoseProcessor.trackerUpdated(tracker);
			TrackerConfig tc = getTrackerConfig(tracker);
			tracker.saveConfig(tc);
			saveConfig();
		});
	}

	@ThreadSafe
	public void addSkeletonUpdatedCallback(Consumer<HumanSkeleton> consumer) {
		queueTask(() -> {
			humanPoseProcessor.addSkeletonUpdatedCallback(consumer);
		});
	}

	@ThreadSafe
	public synchronized void saveConfig() {
		List<YamlNode> nodes = config.getNodeList("trackers", null);
		List<Map<String, Object>> trackersConfig = new FastList<>(nodes.size());
		for(int i = 0; i < nodes.size(); ++i) {
			trackersConfig.add(nodes.get(i).root);
		}
		config.setProperty("trackers", trackersConfig);
		synchronized(configuration) {
			Iterator<TrackerConfig> iterator = configuration.values().iterator();
			while(iterator.hasNext()) {
				TrackerConfig tc = iterator.next();
				Map<String, Object> cfg = null;
				for(int i = 0; i < trackersConfig.size(); ++i) {
					Map<String, Object> c = trackersConfig.get(i);
					if(tc.trackerName.equals(c.get("name"))) {
						cfg = c;
						break;
					}
				}
				if(cfg == null) {
					cfg = new HashMap<>();
					trackersConfig.add(cfg);
				}
				tc.saveConfig(new YamlNode(cfg));
			}
		}
		File cfgFile = new File("vrconfig.yml");
		try {
			config.save(new FileOutputStream(cfgFile));
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	@VRServerThread
	public void run() {
		trackersServer.start();
		while(true) {
			//final long start = System.currentTimeMillis();
			do {
				Runnable task = tasks.poll();
				if(task == null)
					break;
				task.run();
			} while(true);
			for(int i = 0; i < onTick.size(); ++i) {
				this.onTick.get(i).run();
			}
			for(int i = 0; i < bridges.size(); ++i)
				bridges.get(i).dataRead();
			for(int i = 0; i < trackers.size(); ++i)
				trackers.get(i).tick();
			humanPoseProcessor.update();
			for(int i = 0; i < bridges.size(); ++i)
				bridges.get(i).dataWrite();
			//final long time = System.currentTimeMillis() - start;
			try {
				Thread.sleep(1); // 1000Hz
			} catch(InterruptedException e) {
			}
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
		TrackerConfig config = getTrackerConfig(tracker);
		tracker.loadConfig(config);
		queueTask(() -> {
			trackers.add(tracker);
			trackerAdded(tracker);
			for(int i = 0; i < newTrackersConsumers.size(); ++i)
				newTrackersConsumers.get(i).accept(tracker);
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
	
	public int getTrackersCount() {
		return trackers.size();
	}

	public List<Tracker> getAllTrackers() {
		return new FastList<>(trackers);
	}
}
