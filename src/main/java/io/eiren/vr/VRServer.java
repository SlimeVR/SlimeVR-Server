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

import essentia.util.ann.ThreadSafe;
import essentia.util.ann.ThreadSecure;
import essentia.util.collections.FastList;
import io.eiren.vr.bridge.NamedPipeVRBridge;
import io.eiren.vr.processor.HumanPoseProcessor;
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
	
	public VRServer() {
		super("VRServer");
		hmdTracker = new HMDTracker("HMD");
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
			
			humanPoseProcessor.update();
			
			final long time = System.currentTimeMillis() - start;
			try {
				Thread.sleep(Math.max(1, 10L - time)); // 100Hz
			} catch(InterruptedException e) {
			}
		}
	}
	
	public void queueTask(Runnable r) {
		tasks.add(r);
	}
	
	private void autoAssignTracker(Tracker tracker) {
		queueTask(() -> {
			humanPoseProcessor.trackerAdded(tracker);
		});
	}
	
	@ThreadSecure
	public void registerTracker(Tracker tracker) {
		synchronized(trackers) {
			trackers.add(tracker);
		}
		autoAssignTracker(tracker);
	}
	
	public void calibrate(Tracker tracker) {
		if(tracker.getName().startsWith("udp://")) {
			trackersServer.sendCalibrationCommand(tracker);
		}
	}
	
	public void resetTrackers() {
		queueTask(() -> {
			humanPoseProcessor.resetTrackers();
		});
	}
	
	public int getTrackersCount() {
		synchronized(trackers) {
			return trackers.size();
		}
	}
	
	public List<Tracker> getAllTrackers() {
		synchronized(trackers) {
			return new FastList<>(trackers);
		}
	}
}
