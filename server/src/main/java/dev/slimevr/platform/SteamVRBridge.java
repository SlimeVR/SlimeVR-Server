package dev.slimevr.platform;

import dev.slimevr.Main;
import dev.slimevr.VRServer;
import dev.slimevr.bridge.ProtobufBridge;
import dev.slimevr.bridge.ProtobufMessages;
import dev.slimevr.bridge.ProtobufMessages.*;
import dev.slimevr.config.BridgeConfig;
import dev.slimevr.tracking.trackers.Device;
import dev.slimevr.tracking.trackers.Tracker;
import dev.slimevr.tracking.trackers.TrackerPosition;
import dev.slimevr.tracking.trackers.TrackerUtils;
import dev.slimevr.tracking.trackers.TrackerRole;
import dev.slimevr.util.ann.VRServerThread;
import solarxr_protocol.rpc.StatusData;
import solarxr_protocol.rpc.StatusDataUnion;
import solarxr_protocol.rpc.StatusSteamVRDisconnectedT;

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
		boolean needsReset;
		if (trackerAdded.getTrackerId() == 0) {
			displayName = "OpenVR HMD";
			needsReset = false;
		} else {
			displayName = trackerAdded.getTrackerName();
			needsReset = true;
		}

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
			null,
			false,
			false,
			needsReset
		);

		device.getTrackers().put(0, tracker);
		Main.getVrServer().getDeviceManager().addDevice(device);
		TrackerRole role = TrackerRole.getById(trackerAdded.getTrackerRole());
		if (role != null) {
			tracker.setTrackerPosition(TrackerPosition.getByTrackerRole(role));
		}
		return tracker;
	}

	// Battery Status
	@VRServerThread
	protected void writeBatteryUpdate(Tracker localTracker) {
		float lowestLevel = 200; // Arbitrarily higher than expected battery
		// percentage
		float trackerLevel = 0; // Tracker battery percentage on a scale from 0
		// to 100. SteamVR expects a value from 0 to 1.
		float trackerVoltage = 0; // Tracker voltage. This is used to determine
		// if the tracker is being charged. owoTrack
		// devices do not have a tracker voltage.
		boolean isCharging = false;

		List<Tracker> allTrackers = Main.getVrServer().getAllTrackers();
		TrackerRole role = localTracker.getTrackerPosition().getTrackerRole();

		Tracker primaryTracker = null;
		Tracker secondaryTracker = null;
		Tracker tertiaryTracker = null;

		// Given what the role is of localTracker, the tracker positions that
		// make up that role are set to primaryTracker, secondaryTracker, and
		// tertiaryTracker respectively.
		primaryTracker = TrackerUtils
			.getNonInternalTrackerForBodyPosition(
				allTrackers,
				TrackerPosition.getByTrackerRole(role)
			);
		switch (role) {
			case WAIST:
				secondaryTracker = TrackerUtils
					.getNonInternalTrackerForBodyPosition(
						allTrackers,
						TrackerPosition.WAIST
					);
				// When the chest SteamVR tracking point is disabled, aggregate
				// its battery level alongside waist and hip.
				if (!(config.getBridgeTrackerRole(TrackerRole.CHEST, true))) {
					tertiaryTracker = TrackerUtils
						.getNonInternalTrackerForBodyPosition(
							allTrackers,
							TrackerPosition.CHEST
						);
				}
				break;
			case CHEST:
				// When the waist SteamVR tracking point is disabled, aggregate
				// waist and hip battery level with the chest.
				if (!(config.getBridgeTrackerRole(TrackerRole.WAIST, true))) {
					secondaryTracker = TrackerUtils
						.getNonInternalTrackerForBodyPosition(
							allTrackers,
							TrackerPosition.HIP
						);
					tertiaryTracker = TrackerUtils
						.getNonInternalTrackerForBodyPosition(
							allTrackers,
							TrackerPosition.WAIST
						);
				}
				break;
			case LEFT_FOOT:
				secondaryTracker = TrackerUtils
					.getNonInternalTrackerForBodyPosition(
						allTrackers,
						TrackerPosition.LEFT_LOWER_LEG
					);
				// When the left knee SteamVR tracking point is disabled,
				// aggregate its battery level with left ankle and left foot.
				if (!(config.getBridgeTrackerRole(TrackerRole.LEFT_KNEE, true))) {
					tertiaryTracker = TrackerUtils
						.getNonInternalTrackerForBodyPosition(
							allTrackers,
							TrackerPosition.LEFT_UPPER_LEG
						);
				}
				break;
			case RIGHT_FOOT:
				secondaryTracker = TrackerUtils
					.getNonInternalTrackerForBodyPosition(
						allTrackers,
						TrackerPosition.RIGHT_LOWER_LEG
					);
				// When the right knee SteamVR tracking point is disabled,
				// aggregate its battery level with right ankle and right foot.
				if (!(config.getBridgeTrackerRole(TrackerRole.RIGHT_KNEE, true))) {
					tertiaryTracker = TrackerUtils
						.getNonInternalTrackerForBodyPosition(
							allTrackers,
							TrackerPosition.RIGHT_UPPER_LEG
						);
				}
				break;
			case LEFT_ELBOW:
				secondaryTracker = TrackerUtils
					.getNonInternalTrackerForBodyPosition(
						allTrackers,
						TrackerPosition.LEFT_LOWER_ARM
					);
				tertiaryTracker = TrackerUtils
					.getNonInternalTrackerForBodyPosition(
						allTrackers,
						TrackerPosition.LEFT_SHOULDER
					);
				break;
			case RIGHT_ELBOW:
				secondaryTracker = TrackerUtils
					.getNonInternalTrackerForBodyPosition(
						allTrackers,
						TrackerPosition.RIGHT_LOWER_ARM
					);
				tertiaryTracker = TrackerUtils
					.getNonInternalTrackerForBodyPosition(
						allTrackers,
						TrackerPosition.RIGHT_SHOULDER
					);
				break;
		}

		// If the battery level of the tracker is lower than lowestLevel, then
		// the battery level of the tracker position becomes lowestLevel.
		// Tracker voltage is set if the tracker position has a battery level
		// lower than lowest level and has a battery voltage (owoTrack devices
		// do not).
		if (
			(primaryTracker != null) && (primaryTracker.getBatteryLevel() != null)
		) {
			lowestLevel = primaryTracker.getBatteryLevel();

			if (primaryTracker.getBatteryVoltage() != null) {
				trackerVoltage = primaryTracker.getBatteryVoltage();
			}
		}
		if (
			(secondaryTracker != null)
				&& (secondaryTracker.getBatteryLevel() != null)
				&& (secondaryTracker.getBatteryLevel() < lowestLevel)
		) {
			lowestLevel = secondaryTracker.getBatteryLevel();

			if (secondaryTracker.getBatteryVoltage() != null) {
				trackerVoltage = secondaryTracker.getBatteryVoltage();
			}
		}
		if (
			(tertiaryTracker != null)
				&& (tertiaryTracker.getBatteryLevel() != null)
				&& (tertiaryTracker.getBatteryLevel() < lowestLevel)
		) {
			lowestLevel = tertiaryTracker.getBatteryLevel();

			if (tertiaryTracker.getBatteryVoltage() != null) {
				trackerVoltage = tertiaryTracker.getBatteryVoltage();
			}
		}

		if (lowestLevel >= 200) {
			return;
		} else {
			trackerLevel = lowestLevel / 100;
			if (trackerVoltage >= 4.3) {
				// TO DO: Add sending whether the tracker is charging from the
				// tracker itself rather than checking voltage.
				isCharging = true;
			}
		}

		Battery.Builder builder = Battery.newBuilder().setTrackerId(localTracker.getId());

		builder.setBatteryLevel(trackerLevel);
		builder.setIsCharging(isCharging);

		sendMessage(ProtobufMessage.newBuilder().setBattery(builder).build());
	}

	@VRServerThread
	protected void batteryReceived(Battery batteryMessage) {
		Tracker tracker = getInternalRemoteTrackerById(batteryMessage.getTrackerId());

		if (tracker != null) {
			tracker.setBatteryLevel(batteryMessage.getBatteryLevel());

			// Purely for cosmetic purposes, SteamVR does not report device
			// voltage.
			if (batteryMessage.getIsCharging()) {
				tracker.setBatteryVoltage(4.3f);
				// TO DO: Add "tracker.setIsCharging()"
			} else {
				tracker.setBatteryVoltage(3.7f);
			}
		}
	}

	/**
	 * When 0, then it means null
	 */
	protected int lastSteamVRStatus = 0;

	protected void reportDisconnected() {
		if (lastSteamVRStatus != 0) {
			throw new IllegalStateException(
				"lastSteamVRStatus wasn't 0 and it was " + lastSteamVRStatus + " instead"
			);
		}
		var statusData = new StatusSteamVRDisconnectedT();
		statusData.setBridgeSettingsName(bridgeSettingsKey);

		var status = new StatusDataUnion();
		status.setType(StatusData.StatusSteamVRDisconnected);
		status.setValue(statusData);
		lastSteamVRStatus = Main.getVrServer().getStatusSystem().addStatusInt(status, false);

	}

	public abstract boolean isConnected();
}
