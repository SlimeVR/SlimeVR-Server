package io.eiren.vr;

import java.util.List;

import essentia.util.ann.ThreadSecure;
import essentia.util.collections.FastList;
import io.eiren.vr.bridge.NamedPipeVRBridge;
import io.eiren.vr.processor.HumanPoseProcessor;
import io.eiren.vr.trackers.HMDTracker;
import io.eiren.vr.trackers.TrackersUDPServer;
import io.eiren.vr.trackers.Tracker;

public class VRServer extends Thread {
	
	private final List<Tracker> trackers = new FastList<>();
	private final HumanPoseProcessor humanPoseProcessor;
	private final TrackersUDPServer trackersServer = new TrackersUDPServer(6969, "Sensors UDP server", this::registerTracker);
	private final NamedPipeVRBridge driverBridge;
	
	public VRServer() {
		super("VRServer");
		HMDTracker hmd = new HMDTracker("HMD");
		humanPoseProcessor = new HumanPoseProcessor(hmd);
		List<? extends Tracker> shareTrackers = humanPoseProcessor.getComputedTrackers();
		driverBridge = new NamedPipeVRBridge(hmd, shareTrackers);
		
		registerTracker(hmd);
		for(int i = 0; i < shareTrackers.size(); ++i)
			registerTracker(shareTrackers.get(i));
	}
	
	@Override
	public void run() {
		trackersServer.start();
		driverBridge.start();
		while(true) {
			final long start = System.currentTimeMillis();
			
			humanPoseProcessor.update();
			
			final long time = System.currentTimeMillis() - start;
			try {
				Thread.sleep(Math.max(1, 10L - time)); // 100Hz
			} catch(InterruptedException e) {
			}
		}
	}
	
	private void autoAssignTracker(Tracker tracker) {
		//
	}
	
	@ThreadSecure
	public void registerTracker(Tracker tracker) {
		synchronized(trackers) {
			trackers.add(tracker);
		}
		autoAssignTracker(tracker);
	}
}
