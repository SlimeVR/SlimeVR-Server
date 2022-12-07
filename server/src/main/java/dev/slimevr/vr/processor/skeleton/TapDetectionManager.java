package dev.slimevr.vr.processor.skeleton;

import dev.slimevr.config.TapDetectionConfig;
import dev.slimevr.osc.VRCOSCHandler;
import dev.slimevr.vr.trackers.Tracker;


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

	// delay
	private float resetDelayNs = 0.20f * 1000000000.0f;

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
		// TODO add config values
		updateConfig();
	}

	// TODO finish this
	public void updateConfig() {
		this.resetDelayNs = config.getDelay() * 1000000000.0f;
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
		if (
			quickResetDetector.getDoubleTapped()
				&& System.nanoTime() - quickResetDetector.getDetectionTime() > resetDelayNs
		) {
			if (oscHandler != null)
				oscHandler.yawAlign();
			skeleton.resetTrackersYaw();
			quickResetDetector.resetDetector();
            // print
            System.out.println("quick reset");

		}
	}

	private void checkReset() {
		if (
			resetDetector.getDoubleTapped()
				&& System.nanoTime() - resetDetector.getDetectionTime() > resetDelayNs
		) {
			if (oscHandler != null)
				oscHandler.yawAlign();
			skeleton.resetTrackersFull();
			resetDetector.resetDetector();
            // print
            System.out.println("reset");
		}
	}

	private void checkMountingReset() {
		if (
			mountingResetDetector.getDoubleTapped()
				&& System.nanoTime() - resetDelayNs > resetDelayNs
		) {
			skeleton.resetTrackersMounting();
			mountingResetDetector.resetDetector();
            // print
            System.out.println("mounting reset");
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
        return null;
    }

    private Tracker getTrackerToWatchMountingReset() {
        if (skeleton.rightUpperLegTracker != null)
            return skeleton.rightUpperLegTracker;
        return null;

    }

}
