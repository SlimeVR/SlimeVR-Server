package dev.slimevr.bridge;

import dev.slimevr.bridge.ProtobufMessages.ProtobufMessage;
import io.eiren.vr.trackers.Tracker;
import io.eiren.vr.trackers.VRTracker;

public class NamedPipeBridge extends ProtobufBridge<VRTracker> {
	
	public NamedPipeBridge() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void sendMessage(ProtobufMessage message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected VRTracker createInternalSharedTracker(Tracker source) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
}
