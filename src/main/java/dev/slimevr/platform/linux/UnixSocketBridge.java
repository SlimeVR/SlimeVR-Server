package dev.slimevr.platform.linux;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import dev.slimevr.Main;
import dev.slimevr.VRServer;
import dev.slimevr.bridge.BridgeThread;
import dev.slimevr.bridge.PipeState;
import dev.slimevr.bridge.ProtobufBridge;
import dev.slimevr.bridge.ProtobufMessages;
import dev.slimevr.config.BridgeConfig;
import dev.slimevr.util.ann.VRServerThread;
import dev.slimevr.vr.Device;
import dev.slimevr.vr.trackers.*;
import io.eiren.util.logging.LogManager;

import java.io.IOException;
import java.net.StandardProtocolFamily;
import java.net.UnixDomainSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.List;

public class UnixSocketBridge extends ProtobufBridge<VRTracker> implements Runnable {
    public static final String SOCKET_PATH = "/tmp/SlimeVRDriver";
    public static final UnixDomainSocketAddress SOCKET_ADDRESS = UnixDomainSocketAddress.of(SOCKET_PATH);

    protected final String bridgeSettingsKey;
    protected final Thread runnerThread;
    private final TrackerRole[] defaultRoles = new TrackerRole[] { TrackerRole.WAIST,
            TrackerRole.LEFT_FOOT, TrackerRole.RIGHT_FOOT };
    private final List<? extends ShareableTracker> shareableTrackers;
    private final BridgeConfig config;


    private SocketChannel channel;

    public UnixSocketBridge(VRServer server,
        HMDTracker hmd,
        String bridgeSettingsKey,
        String bridgeName,
        List<? extends ShareableTracker> shareableTrackers
    ) {
        super(bridgeName, hmd);
        this.bridgeSettingsKey = bridgeSettingsKey;
        this.runnerThread = new Thread(this, "Named socket thread");
        this.shareableTrackers = shareableTrackers;
        this.config = server.getConfigManager().getVrConfig().getBrige(bridgeSettingsKey);
    }

    @Override
    @VRServerThread
    public void startBridge() {
        for (TrackerRole role : defaultRoles) {
            changeShareSettings(
                    role,
                    this.config.getBridgeTrackerRole(role, true)
            );
        }
        for (ShareableTracker tr : shareableTrackers) {
            TrackerRole role = tr.getTrackerRole();
            changeShareSettings(
                    role,
                    this.config.getBridgeTrackerRole(role, false)
            );
        }
        runnerThread.start();
    }

    @VRServerThread
    public boolean getShareSetting(TrackerRole role) {
        for (ShareableTracker tr : shareableTrackers) {
            if (tr.getTrackerRole() == role) {
                return sharedTrackers.contains(tr);
            }
        }
        return false;
    }

    @VRServerThread
    public void changeShareSettings(TrackerRole role, boolean share) {
        if (role == null)
            return;
        for (ShareableTracker tr : shareableTrackers) {
            if (tr.getTrackerRole() == role) {
                if (share) {
                    addSharedTracker(tr);
                } else {
                    removeSharedTracker(tr);
                }
                config.setBridgeTrackerRole(role, share);
                Main.vrServer.getConfigManager().saveConfig();
            }
        }
    }

    @Override
    @VRServerThread
    protected VRTracker createNewTracker(ProtobufMessages.TrackerAdded trackerAdded) {
        // Todo: We need the manufacturer
        Device device = Main.vrServer
                .getDeviceManager()
                .createDevice(
                        trackerAdded.getTrackerName(),
                        trackerAdded.getTrackerSerial(),
                        "FeederAPP"
                );

        VRTracker tracker = new VRTracker(
                trackerAdded.getTrackerId(),
                trackerAdded.getTrackerSerial(),
                trackerAdded.getTrackerName(),
                true,
                true,
                device
        );

        device.getTrackers().add(tracker);
        Main.vrServer.getDeviceManager().addDevice(device);
        TrackerRole role = TrackerRole.getById(trackerAdded.getTrackerRole());
        if (role != null) {
            tracker.setBodyPosition(TrackerPosition.getByTrackerRole(role).orElse(null));
        }
        return tracker;
    }

    @Override
    @BridgeThread
    public void run() {
        try {
            this.channel = connectChannel();

            while (true) {
                channel.read()
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    @BridgeThread
    protected boolean sendMessageReal(ProtobufMessages.ProtobufMessage message) {
        return false;
    }

    private SocketChannel createSocket() throws IOException {
        channel = SocketChannel.bind(StandardProtocolFamily.UNIX);
        channel.configureBlocking(true);
        channel.connect(SOCKET_ADDRESS);
        LogManager.info("[" + bridgeName + "] Connected to " + SOCKET_PATH);
        return channel;
    }
}

