package dev.slimevr.platform.linux;

import dev.slimevr.bridge.BridgeThread;
import dev.slimevr.bridge.ProtobufBridge;
import dev.slimevr.bridge.ProtobufMessages;
import dev.slimevr.util.ann.VRServerThread;
import dev.slimevr.vr.trackers.HMDTracker;
import dev.slimevr.vr.trackers.VRTracker;

public class UnixSocketBridge extends ProtobufBridge<VRTracker> implements Runnable {

    public UnixSocketBridge(String bridgeName, HMDTracker hmd) {
        super(bridgeName, hmd);
    }

    @Override
    @BridgeThread
    protected boolean sendMessageReal(ProtobufMessages.ProtobufMessage message) {
        return false;
    }

    @Override
    @VRServerThread
    protected VRTracker createNewTracker(ProtobufMessages.TrackerAdded trackerAdded) {
        return null;
    }

    @Override
    @VRServerThread
    public void startBridge() {

    }

    @Override
    @BridgeThread
    public void run() {

    }
}

