package dev.slimevr.tracking.processor.skeleton;


import dev.slimevr.config.TapDetectionConfig;
import dev.slimevr.osc.VRCOSCHandler;
import dev.slimevr.tracking.trackers.Tracker;

import solarxr_protocol.rpc.ResetType;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.BufferedInputStream;
import java.util.Objects;


// handles tap detection for the skeleton
public class TapDetectionManager {

	// server and related classes
	private HumanSkeleton skeleton;
	private VRCOSCHandler oscHandler;
	private TapDetectionConfig config;

	// tap detectors
	private TapDetection quickResetDetector;
	private TapDetection resetDetector;
	private TapDetection mountingResetDetector;

	// number of taps to detect
	private int quickResetTaps = 2;
	private int resetTaps = 3;
	private int mountingResetTaps = 3;

	// delay
	private static final float NS_CONVERTER = 1.0e9f;
	private float resetDelayNs = 0.20f * NS_CONVERTER;
	private float quickResetDelayNs = 1.00f * NS_CONVERTER;
	private float mountingResetDelayNs = 1.00f * NS_CONVERTER;

	// feedback
	private boolean feedbackSoundEnabled = true;
	private boolean quickResetAllowPlaySound = true;
	private boolean resetAllowPlaySound = true;
	private boolean mountingResetAllowPlaySound = true;

	public TapDetectionManager(HumanSkeleton skeleton) {
		this.skeleton = skeleton;
	}

	public TapDetectionManager(
		HumanSkeleton skeleton,
		VRCOSCHandler oscHandler,
		TapDetectionConfig config
	) {
		this.skeleton = skeleton;
		this.oscHandler = oscHandler;
		this.config = config;

		quickResetDetector = new TapDetection(skeleton, getTrackerToWatchQuickReset());
		resetDetector = new TapDetection(skeleton, getTrackerToWatchReset());
		mountingResetDetector = new TapDetection(skeleton, getTrackerToWatchMountingReset());

		// since this config value is only modified by editing the config file,
		// we can set it here
		quickResetDetector
			.setNumberTrackersOverThreshold(
				config.getNumberTrackersOverThreshold()
			);
		resetDetector
			.setNumberTrackersOverThreshold(
				config.getNumberTrackersOverThreshold()
			);
		mountingResetDetector
			.setNumberTrackersOverThreshold(
				config.getNumberTrackersOverThreshold()
			);

		updateConfig();
	}

	public void updateConfig() {
		this.quickResetDelayNs = config.getQuickResetDelay() * NS_CONVERTER;
		this.resetDelayNs = config.getResetDelay() * NS_CONVERTER;
		this.mountingResetDelayNs = config.getMountingResetDelay() * NS_CONVERTER;
		this.feedbackSoundEnabled = config.getFeedbackSoundEnabled();
		quickResetDetector.setEnabled(config.getQuickResetEnabled());
		resetDetector.setEnabled(config.getResetEnabled());
		mountingResetDetector.setEnabled(config.getMountingResetEnabled());
		quickResetTaps = config.getQuickResetTaps();
		resetTaps = config.getResetTaps();
		mountingResetTaps = config.getMountingResetTaps();
		quickResetDetector.setMaxTaps(quickResetTaps);
		resetDetector.setMaxTaps(resetTaps);
		mountingResetDetector.setMaxTaps(mountingResetTaps);
	}

	public void update() {
		if (quickResetDetector == null || resetDetector == null || mountingResetDetector == null)
			return;
		// update the tap detectors
		quickResetDetector.update();
		resetDetector.update();
		mountingResetDetector.update();

		// check if any tap detectors have detected taps
		checkQuickReset();
		checkReset();
		checkMountingReset();
	}

	private void checkQuickReset() {
		boolean tapped = (quickResetTaps <= quickResetDetector.getTaps());

		if (tapped && quickResetAllowPlaySound && feedbackSoundEnabled) {
			playSound(ResetType.Quick);
			quickResetAllowPlaySound = false;
		}

		if (
			tapped && System.nanoTime() - quickResetDetector.getDetectionTime() > quickResetDelayNs
		) {
			if (oscHandler != null)
				oscHandler.yawAlign();
			skeleton.resetTrackersYaw();
			quickResetDetector.resetDetector();
			if (feedbackSoundEnabled)
				quickResetAllowPlaySound = true;
		}
	}

	private void checkReset() {
		boolean tapped = (resetTaps <= resetDetector.getTaps());

		if (tapped && resetAllowPlaySound && feedbackSoundEnabled) {
			playSound(ResetType.Full);
			resetAllowPlaySound = false;
		}

		if (
			tapped && System.nanoTime() - resetDetector.getDetectionTime() > resetDelayNs
		) {
			if (oscHandler != null)
				oscHandler.yawAlign();
			skeleton.resetTrackersFull();
			resetDetector.resetDetector();
			if (feedbackSoundEnabled)
				resetAllowPlaySound = true;
		}
	}

	private void checkMountingReset() {
		boolean tapped = (mountingResetTaps <= mountingResetDetector.getTaps());

		if (tapped && mountingResetAllowPlaySound && feedbackSoundEnabled) {
			playSound(ResetType.Mounting);
			mountingResetAllowPlaySound = false;
		}

		if (
			tapped
				&& System.nanoTime() - mountingResetDetector.getDetectionTime()
					> mountingResetDelayNs
		) {
			skeleton.resetTrackersMounting();
			mountingResetDetector.resetDetector();
			if (feedbackSoundEnabled)
				mountingResetAllowPlaySound = true;
		}
	}


	// returns either the chest tracker, hip tracker, or waist tracker depending
	// on which one is available
	// if none are available, returns null
	private Tracker getTrackerToWatchQuickReset() {
		if (skeleton.chestTracker != null)
			return skeleton.chestTracker;
		else if (skeleton.hipTracker != null)
			return skeleton.hipTracker;
		else if (skeleton.waistTracker != null)
			return skeleton.waistTracker;
		else
			return null;
	}

	private Tracker getTrackerToWatchReset() {
		if (skeleton.leftUpperLegTracker != null)
			return skeleton.leftUpperLegTracker;
		else if (skeleton.leftLowerLegTracker != null)
			return skeleton.leftLowerLegTracker;
		return null;
	}

	private Tracker getTrackerToWatchMountingReset() {
		if (skeleton.rightUpperLegTracker != null)
			return skeleton.rightUpperLegTracker;
		else if (skeleton.rightLowerLegTracker != null)
			return skeleton.rightLowerLegTracker;
		return null;
	}

	private void playSound(int resetType) {
		new Thread(new Runnable() {

			public void run() {
				String soundName = switch (resetType) {
					case ResetType.Quick -> "/single_beep.wav";
					case ResetType.Full -> "/double_beep.wav";
					case ResetType.Mounting -> "/triple_beep.wav";
					default -> "/single_beep.wav";
				};

				try {
					Clip clip = AudioSystem.getClip();

					BufferedInputStream bufferedStream = new BufferedInputStream(
						Objects.requireNonNull(this.getClass().getResourceAsStream(soundName))
					);
					AudioInputStream inputStream = AudioSystem.getAudioInputStream(bufferedStream);

					clip.open(inputStream);
					clip.start();

				} catch (Exception e) {
					System.err.println(e.getMessage());
					e.printStackTrace();
				}
			}
		}).start();
	}


}
