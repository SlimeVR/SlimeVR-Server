package io.eiren.vr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

import io.eiren.util.ann.ThreadSafe;
import io.eiren.util.ann.ThreadSecure;
import io.eiren.util.ann.VRServerThread;
import io.eiren.util.collections.FastList;
import io.eiren.vr.bridge.NamedPipeVRBridge;
import io.eiren.vr.processor.HumanPoseProcessor;
import io.eiren.vr.processor.HumanSkeleton;
import io.eiren.vr.trackers.HMDTracker;
import io.eiren.vr.trackers.TrackersUDPServer;
import io.eiren.yaml.YamlException;
import io.eiren.yaml.YamlFile;
import io.eiren.yaml.YamlNode;
import io.eiren.vr.trackers.Tracker;
import io.eiren.vr.trackers.TrackerConfig;

public class VRServer extends Thread {
	
	private final List<Tracker> trackers = new FastList<>();
	public final HumanPoseProcessor humanPoseProcessor;
	private final TrackersUDPServer trackersServer = new TrackersUDPServer(6969, "Sensors UDP server", this::registerTracker);
	private final NamedPipeVRBridge driverBridge;
	private final Queue<Runnable> tasks = new LinkedBlockingQueue<>();
	private final Map<String, TrackerConfig> configuration = new HashMap<>();
	public final YamlFile config = new YamlFile();
	public final HMDTracker hmdTracker;
	private final List<Consumer<Tracker>> newTrackersConsumers = new FastList<>();
	private final List<Runnable> onTick = new FastList<>();
	
	public VRServer() {
		super("VRServer");
		hmdTracker = new HMDTracker("HMD");
		hmdTracker.position.set(0, 1.8f, 0); // Set starting position for easier debugging
		humanPoseProcessor = new HumanPoseProcessor(this, hmdTracker);
		List<? extends Tracker> shareTrackers = humanPoseProcessor.getComputedTrackers();
		driverBridge = new NamedPipeVRBridge(hmdTracker, shareTrackers);
		
		registerTracker(hmdTracker);
		for(int i = 0; i < shareTrackers.size(); ++i)
			registerTracker(shareTrackers.get(i));
	}
	
	@ThreadSafe
	public TrackerConfig getTrackerConfig(Tracker tracker) {
		synchronized(configuration) {
			TrackerConfig config = configuration.get(tracker.getName());
			if(config == null) {
				config = new TrackerConfig(tracker.getName());
				configuration.put(tracker.getName(), config);
			}
			return config;
		}
	}
	
	private void loadConfig() {
		try {
			config.load(new FileInputStream(new File("vrconfig.yml")));
		} catch(IOException e) {
			e.printStackTrace();
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
	public void addSkeletonUpdatedCallback(Consumer<HumanSkeleton> consumer) {
		queueTask(() -> {
			humanPoseProcessor.addSkeletonUpdatedCallback(consumer);
		});
	}

	@ThreadSafe
	public void saveConfig() {
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
		loadConfig();
		trackersServer.start();
		driverBridge.start();
		while(true) {
			final long start = System.currentTimeMillis();
			do {
				Runnable task = tasks.poll();
				if(task == null)
					break;
				task.run();
			} while(true);
			
			for(int i = 0; i < onTick.size(); ++i) {
				this.onTick.get(i).run();
			}
			
			humanPoseProcessor.update();
			
			final long time = System.currentTimeMillis() - start;
			try {
				Thread.sleep(Math.max(1, 10L - time)); // 100Hz
			} catch(InterruptedException e) {
			}
		}
	}

	@ThreadSafe
	public void queueTask(Runnable r) {
		tasks.add(r);
	}
	
	@VRServerThread
	private void autoAssignTracker(Tracker tracker) {
		humanPoseProcessor.trackerAdded(tracker);
	}
	
	@ThreadSecure
	public void registerTracker(Tracker tracker) {
		TrackerConfig config = getTrackerConfig(tracker);
		tracker.loadConfig(config);
		queueTask(() -> {
			trackers.add(tracker);
			autoAssignTracker(tracker);
			for(int i = 0; i < newTrackersConsumers.size(); ++i)
				newTrackersConsumers.get(i).accept(tracker);
		});
	}
	
	public void resetTrackers() {
		queueTask(() -> {
			humanPoseProcessor.resetTrackers();
		});
	}
	
	public int getTrackersCount() {
		return trackers.size();
	}

	public List<Tracker> getAllTrackers() {
		return new FastList<>(trackers);
	}
}
