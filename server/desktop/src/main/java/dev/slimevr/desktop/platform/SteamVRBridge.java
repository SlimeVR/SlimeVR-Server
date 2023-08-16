package dev.slimevr.desktop.platform;

import dev.slimevr.VRServer;
import dev.slimevr.desktop.platform.ProtobufMessages.*;
import dev.slimevr.config.BridgeConfig;
import dev.slimevr.tracking.trackers.*;
import dev.slimevr.util.ann.VRServerThread;
import io.eiren.util.collections.FastList;
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
		String threadName,
		String bridgeName,
		String bridgeSettingsKey,
		List<Tracker> shareableTrackers
	) {
		super(bridgeName);
		this.bridgeSettingsKey = bridgeSettingsKey;
		this.runnerThread = new Thread(this, threadName);
		this.shareableTrackers = shareableTrackers;
		this.config = server.configManager.getVrConfig().getBridge(bridgeSettingsKey);
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
				VRServer.Companion.getInstance().configManager.saveConfig();
			}
		}
	}

	@Override
	@VRServerThread
	protected Tracker createNewTracker(ProtobufMessages.TrackerAdded trackerAdded) {
		Device device = VRServer.Companion.getInstance().deviceManager
			.createDevice(
				trackerAdded.getTrackerName(),
				trackerAdded.getTrackerSerial(),
				"OpenVR" // TODO : We need the manufacturer
			);

		String displayName;
		boolean needsReset;
		if (trackerAdded.getTrackerId() == 0) {
			if (trackerAdded.getTrackerName().equals("HMD"))
				displayName = "SteamVR Driver HMD";
			else
				displayName = "Feeder App HMD";
			// TODO support needsReset = true for VTubing (GUI toggle?)
			needsReset = false;
		} else {
			displayName = trackerAdded.getTrackerName();
			needsReset = true;
		}

		Tracker tracker = new Tracker(
			device,
			VRServer.getNextLocalTrackerId(),
			trackerAdded.getTrackerSerial(),
			displayName,
			null,
			trackerAdded.getTrackerId(),
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
		VRServer.Companion.getInstance().deviceManager.addDevice(device);
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

		List<Tracker> allTrackers = VRServer.Companion.getInstance().getAllTrackers();
		TrackerRole role = localTracker.getTrackerPosition().getTrackerRole();

		List<Tracker> batteryTrackers = new FastList<>();

		// batteryTrackers is filled with trackers that would give battery data
		// for the SteamVR tracker according to its role. Warning: trackers
		// inside batteryTrackers could be null, so there must be a null check
		// when accessing its data.
		batteryTrackers
			.add(
				TrackerUtils
					.getTrackerForSkeleton(
						allTrackers,
						TrackerPosition.getByTrackerRole(role)
					)
			);
		switch (role) {
			case WAIST -> {
				// Add waist because the first tracker is hip
				batteryTrackers
					.add(
						TrackerUtils
							.getTrackerForSkeleton(
								allTrackers,
								TrackerPosition.WAIST
							)
					);
				// When the chest SteamVR tracking point is disabled, aggregate
				// its battery level alongside waist and hip.
				if (!(config.getBridgeTrackerRole(TrackerRole.CHEST, true))) {
					batteryTrackers
						.add(
							TrackerUtils
								.getTrackerForSkeleton(
									allTrackers,
									TrackerPosition.CHEST
								)
						);
					batteryTrackers
						.add(
							TrackerUtils
								.getTrackerForSkeleton(
									allTrackers,
									TrackerPosition.UPPER_CHEST
								)
						);
				}
			}
			case CHEST -> {
				// Add chest because the first tracker is upperChest
				batteryTrackers
					.add(
						TrackerUtils
							.getTrackerForSkeleton(
								allTrackers,
								TrackerPosition.CHEST
							)
					);
				// When the waist SteamVR tracking point is disabled, aggregate
				// waist and hip battery level with the chest.
				if (!(config.getBridgeTrackerRole(TrackerRole.WAIST, true))) {
					batteryTrackers
						.add(
							TrackerUtils
								.getTrackerForSkeleton(
									allTrackers,
									TrackerPosition.WAIST
								)
						);
					batteryTrackers
						.add(
							TrackerUtils
								.getTrackerForSkeleton(
									allTrackers,
									TrackerPosition.HIP
								)
						);
				}
			}
			case LEFT_FOOT -> {
				batteryTrackers
					.add(
						TrackerUtils
							.getTrackerForSkeleton(
								allTrackers,
								TrackerPosition.LEFT_LOWER_LEG
							)
					);
				// When the left knee SteamVR tracking point is disabled,
				// aggregate its battery level with left ankle and left foot.
				if (!(config.getBridgeTrackerRole(TrackerRole.LEFT_KNEE, true))) {
					batteryTrackers
						.add(
							TrackerUtils
								.getTrackerForSkeleton(
									allTrackers,
									TrackerPosition.LEFT_UPPER_LEG
								)
						);
				}
			}
			case RIGHT_FOOT -> {
				batteryTrackers
					.add(
						TrackerUtils
							.getTrackerForSkeleton(
								allTrackers,
								TrackerPosition.RIGHT_LOWER_LEG
							)
					);
				// When the right knee SteamVR tracking point is disabled,
				// aggregate its battery level with right ankle and right foot.
				if (!(config.getBridgeTrackerRole(TrackerRole.RIGHT_KNEE, true))) {
					batteryTrackers
						.add(
							TrackerUtils
								.getTrackerForSkeleton(
									allTrackers,
									TrackerPosition.RIGHT_UPPER_LEG
								)
						);
				}
			}
			case LEFT_ELBOW -> {
				batteryTrackers
					.add(
						TrackerUtils
							.getTrackerForSkeleton(
								allTrackers,
								TrackerPosition.LEFT_LOWER_ARM
							)
					);
				batteryTrackers
					.add(
						TrackerUtils
							.getTrackerForSkeleton(
								allTrackers,
								TrackerPosition.LEFT_SHOULDER
							)
					);
			}
			case RIGHT_ELBOW -> {
				batteryTrackers
					.add(
						TrackerUtils
							.getTrackerForSkeleton(
								allTrackers,
								TrackerPosition.RIGHT_LOWER_ARM
							)
					);
				batteryTrackers
					.add(
						TrackerUtils
							.getTrackerForSkeleton(
								allTrackers,
								TrackerPosition.RIGHT_SHOULDER
							)
					);
			}
		}

		// If the battery level of the tracker is lower than lowestLevel, then
		// the battery level of the tracker position becomes lowestLevel.
		// Tracker voltage is set if the tracker position has a battery level
		// lower than the lowest level and has a battery voltage (owoTrack
		// devices do not).
		for (Tracker batteryTracker : batteryTrackers) {
			if (
				batteryTracker != null
					&& batteryTracker.getBatteryLevel() != null
					&& batteryTracker.getBatteryLevel() < lowestLevel
			) {
				lowestLevel = batteryTracker.getBatteryLevel();

				if (batteryTracker.getBatteryVoltage() != null) {
					trackerVoltage = batteryTracker.getBatteryVoltage();
				} else {
					trackerVoltage = 0;
				}
			}
		}

		// Internal battery reporting reports 5V max, and <= 3.2V when >=50mV
		// lower than initial reading (e.g. 3.25V down from 3.3V), and ~0V when
		// battery is fine.
		// 3.2V is technically 0%, but the last 5% of battery level is ignored,
		// which makes 3.36V 0% in practice. Refer to batterymonitor.cpp in
		// SlimeVR-Tracker-ESP for exact details.
		// External battery reporting reports anything > 0V.
		// The following should catch internal battery reporting and erroneous
		// readings.
		if (
			((lowestLevel >= 200) || (lowestLevel < 0))
				|| ((trackerVoltage < 3.2) && (lowestLevel <= 0)
					|| ((trackerVoltage >= 5) && (lowestLevel > 150)))
		) {
			return;
		} else {
			trackerLevel = lowestLevel / 100;
			if (trackerVoltage >= 4.3) {
				// TO DO: Add sending whether the tracker is charging from the
				// tracker itself rather than checking voltage.
				isCharging = true;
			}
		}

		Battery.Builder builder = Battery.newBuilder().setTrackerId(localTracker.getTrackerNum());

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
		lastSteamVRStatus = VRServer.Companion.getInstance().statusSystem
			.addStatusInt(status, false);

	}
}
