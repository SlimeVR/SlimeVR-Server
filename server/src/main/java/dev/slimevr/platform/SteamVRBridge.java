package dev.slimevr.platform;

import dev.slimevr.Main;
import dev.slimevr.VRServer;
import dev.slimevr.bridge.ProtobufBridge;
import dev.slimevr.bridge.ProtobufMessages;
import dev.slimevr.config.BridgeConfig;
import dev.slimevr.util.ann.VRServerThread;
import dev.slimevr.tracking.Device;
import dev.slimevr.tracking.trackers.*;

import java.util.List;


public abstract class SteamVRBridge extends ProtobufBridge<VRTracker> implements Runnable {
	protected final String bridgeSettingsKey;
	protected final TrackerRole[] defaultRoles = new TrackerRole[] { TrackerRole.WAIST,
		TrackerRole.LEFT_FOOT, TrackerRole.RIGHT_FOOT };
	protected final Thread runnerThread;
	protected final List<? extends ShareableTracker> shareableTrackers;
	protected final BridgeConfig config;

	public SteamVRBridge(
		VRServer server,
		HMDTracker hmd,
		String threadName,
		String bridgeName,
		String bridgeSettingsKey,
		List<? extends ShareableTracker> shareableTrackers
	) {
		super(bridgeName, hmd);
		this.bridgeSettingsKey = bridgeSettingsKey;
		this.runnerThread = new Thread(this, threadName);
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
				Main.getVrServer().getConfigManager().saveConfig();
			}
		}
	}

	@Override
	@VRServerThread
	protected VRTracker createNewTracker(ProtobufMessages.TrackerAdded trackerAdded) {
		// Todo: We need the manufacturer
		Device device = Main
			.getVrServer()
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
		Main.getVrServer().getDeviceManager().addDevice(device);
		TrackerRole role = TrackerRole.getById(trackerAdded.getTrackerRole());
		if (role != null) {
			tracker.setBodyPosition(TrackerPosition.getByTrackerRole(role).orElse(null));
		}
		return tracker;
	}

	public abstract boolean isConnected();
}
