package dev.slimevr.tracking.processor.skeleton;


import dev.slimevr.config.TapDetectionConfig;
import dev.slimevr.reset.ResetHandler;
import dev.slimevr.setup.TapSetupHandler;
import dev.slimevr.tracking.processor.HumanPoseManager;
import dev.slimevr.tracking.trackers.Tracker;
import solarxr_protocol.rpc.ResetType;

import java.util.ArrayList;
import java.util.List;


/**
 * Handles tap detection for reset
 */
public class TapDetectionManager {
	private static final String resetSourceName = "TapDetection";
	private static final int tapsForSetupMode = 2;

	// server and related classes
	private final HumanSkeleton skeleton;
	private HumanPoseManager humanPoseManager;
	private TapDetectionConfig config;

	// tap detectors
	private TapDetection yawResetDetector;
	private TapDetection fullResetDetector;
	private TapDetection mountingResetDetector;

	private ArrayList<TapDetection> tapDetectors;

	// number of taps to detect
	private int yawResetTaps = 2;
	private int fullResetTaps = 3;
	private int mountingResetTaps = 3;

	// delay
	private static final float NS_CONVERTER = 1.0e9f;
	private float fullResetDelayNs = 0.20f * NS_CONVERTER;
	private float yawResetDelayNs = 1.00f * NS_CONVERTER;
	private float mountingResetDelayNs = 1.00f * NS_CONVERTER;

	// feedback
	private boolean yawResetAllowPlaySound = true;
	private boolean fullResetAllowPlaySound = true;
	private boolean mountingResetAllowPlaySound = true;

	private ResetHandler resetHandler;
	private TapSetupHandler tapSetupHandler;

	public TapDetectionManager(HumanSkeleton skeleton) {
		this.skeleton = skeleton;
	}

	public TapDetectionManager(
		HumanSkeleton skeleton,
		HumanPoseManager humanPoseManager,
		TapDetectionConfig config,
		ResetHandler resetHandler,
		TapSetupHandler tapSetupHandler,
		List<Tracker> trackers
	) {
		this.skeleton = skeleton;
		this.humanPoseManager = humanPoseManager;
		this.config = config;
		this.resetHandler = resetHandler;
		this.tapSetupHandler = tapSetupHandler;

		updateConfig(trackers);
	}

	public void updateConfig(List<Tracker> trackers) {
		// check the skeleton for new trackers
		yawResetDetector = new TapDetection(skeleton, getTrackerToWatchYawReset());
		fullResetDetector = new TapDetection(skeleton, getTrackerToWatchFullReset());
		mountingResetDetector = new TapDetection(skeleton, getTrackerToWatchMountingReset());

		if (trackers != null) {
			tapDetectors = new ArrayList<>();
			for (Tracker tracker : trackers) {
				TapDetection tapDetector = new TapDetection(skeleton, tracker);
				tapDetector.setEnabled(true);
				tapDetectors.add(tapDetector);
			}
		}

		if (this.config == null) {
			return;
		}

		this.yawResetDelayNs = config.getYawResetDelay() * NS_CONVERTER;
		this.fullResetDelayNs = config.getFullResetDelay() * NS_CONVERTER;
		this.mountingResetDelayNs = config.getMountingResetDelay() * NS_CONVERTER;
		yawResetDetector.setEnabled(config.getYawResetEnabled());
		fullResetDetector.setEnabled(config.getFullResetEnabled());
		mountingResetDetector.setEnabled(config.getMountingResetEnabled());
		yawResetTaps = config.getYawResetTaps();
		fullResetTaps = config.getFullResetTaps();
		mountingResetTaps = config.getMountingResetTaps();
		yawResetDetector.setMaxTaps(yawResetTaps);
		fullResetDetector.setMaxTaps(fullResetTaps);
		mountingResetDetector.setMaxTaps(mountingResetTaps);
		yawResetDetector
			.setNumberTrackersOverThreshold(
				config.getNumberTrackersOverThreshold()
			);
		fullResetDetector
			.setNumberTrackersOverThreshold(
				config.getNumberTrackersOverThreshold()
			);
		mountingResetDetector
			.setNumberTrackersOverThreshold(
				config.getNumberTrackersOverThreshold()
			);
	}

	public void update() {
		if (
			yawResetDetector == null
				|| fullResetDetector == null
				|| mountingResetDetector == null
				|| tapDetectors == null
				|| config == null
		)
			return;

		// if setup mode is enabled, update the tap detectors for each tracker
		if (config.getSetupMode()) {
			for (TapDetection tapDetector : tapDetectors) {
				tapDetector.update();

				if (tapDetector.getTaps() >= tapsForSetupMode) {
					tapSetupHandler.sendTap(tapDetector.getTracker());
					tapDetector.resetDetector();
				}
			}
		} else {
			// update the tap detectors
			yawResetDetector.update();
			fullResetDetector.update();
			mountingResetDetector.update();

			// check if any tap detectors have detected taps
			checkYawReset();
			checkFullReset();
			checkMountingReset();
		}
	}

	private void checkYawReset() {
		boolean tapped = (yawResetTaps <= yawResetDetector.getTaps());

		if (tapped && yawResetAllowPlaySound) {
			this.resetHandler.sendStarted(ResetType.Yaw);
			yawResetAllowPlaySound = false;
		}

		if (
			tapped && System.nanoTime() - yawResetDetector.getDetectionTime() > yawResetDelayNs
		) {
			if (humanPoseManager != null)
				humanPoseManager.resetTrackersYaw(resetSourceName);
			else
				skeleton.resetTrackersYaw(resetSourceName);

			yawResetDetector.resetDetector();
			yawResetAllowPlaySound = true;
		}
	}

	private void checkFullReset() {
		boolean tapped = (fullResetTaps <= fullResetDetector.getTaps());

		if (tapped && fullResetAllowPlaySound) {
			this.resetHandler.sendStarted(ResetType.Full);
			fullResetAllowPlaySound = false;
		}

		if (
			tapped && System.nanoTime() - fullResetDetector.getDetectionTime() > fullResetDelayNs
		) {
			if (humanPoseManager != null)
				humanPoseManager.resetTrackersFull(resetSourceName);
			else
				skeleton.resetTrackersFull(resetSourceName);

			fullResetDetector.resetDetector();
			fullResetAllowPlaySound = true;
		}
	}

	private void checkMountingReset() {
		boolean tapped = (mountingResetTaps <= mountingResetDetector.getTaps());

		if (tapped && mountingResetAllowPlaySound) {
			this.resetHandler.sendStarted(ResetType.Mounting);
			mountingResetAllowPlaySound = false;
		}

		if (
			tapped
				&& System.nanoTime() - mountingResetDetector.getDetectionTime()
					> mountingResetDelayNs
		) {
			skeleton.resetTrackersMounting(resetSourceName);
			mountingResetDetector.resetDetector();
			mountingResetAllowPlaySound = true;
		}
	}


	// returns either the chest tracker, hip tracker, or waist tracker depending
	// on which one is available
	// if none are available, returns null
	private Tracker getTrackerToWatchYawReset() {
		if (skeleton.getUpperChestTracker() != null)
			return skeleton.getUpperChestTracker();
		else if (skeleton.getChestTracker() != null)
			return skeleton.getChestTracker();
		else if (skeleton.getHipTracker() != null)
			return skeleton.getHipTracker();
		else if (skeleton.getWaistTracker() != null)
			return skeleton.getWaistTracker();
		else
			return null;
	}

	private Tracker getTrackerToWatchFullReset() {
		if (skeleton.getLeftUpperLegTracker() != null)
			return skeleton.getLeftUpperLegTracker();
		else if (skeleton.getLeftLowerLegTracker() != null)
			return skeleton.getLeftLowerLegTracker();
		return null;
	}

	private Tracker getTrackerToWatchMountingReset() {
		if (skeleton.getRightUpperLegTracker() != null)
			return skeleton.getRightUpperLegTracker();
		else if (skeleton.getRightLowerLegTracker() != null)
			return skeleton.getRightLowerLegTracker();
		return null;
	}

}
