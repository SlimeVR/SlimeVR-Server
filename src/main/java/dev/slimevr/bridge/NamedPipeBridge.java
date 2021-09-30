package dev.slimevr.bridge;

import dev.slimevr.bridge.ProtobufMessages.ProtobufMessage;
import dev.slimevr.bridge.ProtobufMessages.TrackerAdded;
import io.eiren.util.ann.VRServerThread;
import io.eiren.vr.trackers.TrackerPosition;
import io.eiren.vr.trackers.TrackerRole;
import io.eiren.vr.trackers.VRTracker;

public class NamedPipeBridge extends ProtobufBridge<VRTracker> implements Runnable {
	
	protected final String pipeName;
	protected final Thread runnerThread;
	
	public NamedPipeBridge(String pipeName) {
		this.pipeName = pipeName;
		runnerThread = new Thread(this, "Named pipe thread");
	}

	@Override
	@BridgeThread
	protected void sendMessageReal(ProtobufMessage message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@VRServerThread
	protected VRTracker createNewTracker(TrackerAdded trackerAdded) {
		VRTracker tracker = new VRTracker(trackerAdded.getTrackerId(), trackerAdded.getTrackerSerial(), trackerAdded.getTrackerName(), true, true);
		TrackerRole role = TrackerRole.getById(trackerAdded.getTrackerRole());
		if(role != null) {
			tracker.setBodyPosition(TrackerPosition.getByRole(role));
		}
		return tracker;
	}

	@Override
	@BridgeThread
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	@VRServerThread
	public void startBridge() {
		runnerThread.start();
	}

	
	
	
}
