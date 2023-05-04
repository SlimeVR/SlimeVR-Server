package dev.slimevr.platform;

import dev.slimevr.Main;
import dev.slimevr.VRServer;
import dev.slimevr.bridge.ProtobufBridge;
import dev.slimevr.bridge.ProtobufMessages;
import dev.slimevr.config.BridgeConfig;
import dev.slimevr.tracking.trackers.Device;
import dev.slimevr.tracking.trackers.Tracker;
import dev.slimevr.tracking.trackers.TrackerPosition;
import dev.slimevr.tracking.trackers.TrackerRole;
import dev.slimevr.util.ann.VRServerThread;

import java.util.List;


public abstract class SteamVRBridge extends ProtobufBridge implements Runnable {
	protected final String bridgeSettingsKey;
	protected final TrackerRole[] defaultRoles = new TrackerRole[] { TrackerRole.WAIST,
		TrackerRole.LEFT_FOOT, TrackerRole.RIGHT_FOOT };
	protected final Thread runnerThread;
	protected final List<Tracker> shareableTrackers;
	protected final BridgeConfig config;

	public SteamVRBridge(
		VRServer server,
		Tracker hmd,
		String threadName,
		String bridgeName,
		String bridgeSettingsKey,
		List<Tracker> shareableTrackers
	) {
		super(bridgeName, hmd);
		this.bridgeSettingsKey = bridgeSettingsKey;
		this.runnerThread = new Thread(this, threadName);
		this.shareableTrackers = shareableTrackers;
		this.config = server.getConfigManager().getVrConfig().getBridge(bridgeSettingsKey);
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
		for (Tracker tr : shareableTrackers) {
			TrackerRole role = tr.getTrackerPosition().getTrackerRole();
			changeShareSettings(
				role,
				this.config.getBridgeTrackerRole(role, false)
			);
		}
		runnerThread.start();
	}

	@VRServerThread
	public boolean getShareSetting(TrackerRole role) {
		for (Tracker tr : shareableTrackers) {
			if (tr.getTrackerPosition().getTrackerRole() == role) {
				return sharedTrackers.contains(tr);
			}
		}
		return false;
	}

	@VRServerThread
	public void changeShareSettings(TrackerRole role, boolean share) {
		if (role == null)
			return;
		for (Tracker tr : shareableTrackers) {
			if (tr.getTrackerPosition().getTrackerRole() == role) {
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
	protected Tracker createNewTracker(ProtobufMessages.TrackerAdded trackerAdded) {
		// Todo: We need the manufacturer
		Device device = Main
			.getVrServer()
			.getDeviceManager()
			.createDevice(
				trackerAdded.getTrackerName(),
				trackerAdded.getTrackerSerial(),
				"OpenVR"
			);

		String displayName;
		if (trackerAdded.getTrackerId() == 0)
			displayName = "OpenVR HMD";
		else
			displayName = trackerAdded.getTrackerName();

		Tracker tracker = new Tracker(
			device,
			// FIXME use SteamVR tracker's id for SlimeVR tracker's trackerNum,
			// and use VRServer's unique id for SlimeVR tracker' id
			trackerAdded.getTrackerId(),
			trackerAdded.getTrackerName(),
			displayName,
			null,
			null,
			true,
			true,
			false,
			true,
			false,
			true,
			false,
			false,
			false,
			true
		);

		device.getTrackers().put(0, tracker);
		Main.getVrServer().getDeviceManager().addDevice(device);
		TrackerRole role = TrackerRole.getById(trackerAdded.getTrackerRole());
		if (role != null) {
			tracker.setTrackerPosition(TrackerPosition.getByTrackerRole(role));
		}
		return tracker;
	}

	public abstract boolean isConnected();
}
