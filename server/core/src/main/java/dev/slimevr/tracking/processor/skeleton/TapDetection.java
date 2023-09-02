package dev.slimevr.tracking.processor.skeleton;

import dev.slimevr.tracking.trackers.Tracker;

import java.util.LinkedList;


// class that monitors the acceleration of the waist, hip, chest or upper chest trackers to detect taps
// and use this to trigger a varaity of resets (if your wondering why no single tap class exists, it's because
// to many false positives)
public class TapDetection {

	// server and related classes
	private final HumanSkeleton skeleton;

	// tap detection
	private boolean enabled = false;
	private final LinkedList<float[]> accelList = new LinkedList<>();
	private final LinkedList<Float> tapTimes = new LinkedList<>();
	private Tracker trackerToWatch = null;
	private int numberTrackersOverThreshold = 1;

	// hyperparameters
	private static final float NS_CONVERTER = 1.0e9f;
	private static final float NEEDED_ACCEL_DELTA = 6.0f;
	private static final float ALLOWED_BODY_ACCEL = 2.5f;
	private static final float ALLOWED_BODY_ACCEL_SQUARED = ALLOWED_BODY_ACCEL * ALLOWED_BODY_ACCEL;
	private static final float CLUMP_TIME_NS = 0.06f * NS_CONVERTER;
	private float timeWindowNS = 0.6f * NS_CONVERTER;

	// state
	private float detectionTime = -1.0f;
	private int taps = 0;
	private boolean waitForLowAccel = false;

	public TapDetection(HumanSkeleton skeleton) {
		this.skeleton = skeleton;
	}

	public TapDetection(
		HumanSkeleton skeleton,
		Tracker trackerToWatch
	) {
		this.skeleton = skeleton;
		this.trackerToWatch = trackerToWatch;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean getEnabled() {
		return enabled;
	}

	// set the tracker to watch and detect taps on
	public void setTrackerToWatch(Tracker tracker) {
		trackerToWatch = tracker;
	}

	public Tracker getTracker() {
		return trackerToWatch;
	}

	public int getTaps() {
		return taps;
	}

	public float getDetectionTime() {
		return detectionTime;
	}

	public void setNumberTrackersOverThreshold(int numberTrackersOverThreshold) {
		this.numberTrackersOverThreshold = numberTrackersOverThreshold;
	}

	// reset the lists for detecting taps
	public void resetDetector() {
		tapTimes.clear();
		accelList.clear();
		taps = 0;
	}

	// set the max taps this detector is configured to detect
	public void setMaxTaps(int maxTaps) {
		timeWindowNS = 0.3f * maxTaps * NS_CONVERTER;
	}

	// main function for tap detection
	public void update() {
		if (skeleton == null || !enabled)
			return;

		if (trackerToWatch == null)
			return;

		// get the acceleration of the tracker and add it to the list
		float time = System.nanoTime();
		float[] listval = { trackerToWatch.getAcceleration().len(), time };
		accelList.add(listval);

		// remove old values from the list (if they are too old)
		while (time - accelList.getFirst()[1] > CLUMP_TIME_NS) {
			accelList.removeFirst();
		}

		// check for a tap
		float accelDelta = getAccelDelta();
		if (accelDelta > NEEDED_ACCEL_DELTA && !waitForLowAccel) {
			// after a tap is added to the list, a lower acceleration
			// is needed before another tap can be added
			tapTimes.add(time);
			waitForLowAccel = true;
		}

		// if waiting for low accel
		if (getMaxAccel() < ALLOWED_BODY_ACCEL) {
			waitForLowAccel = false;
		}

		// remove old taps from the list (if they are too old)
		if (!tapTimes.isEmpty()) {
			while (time - tapTimes.getFirst() > timeWindowNS) {
				tapTimes.removeFirst();
				if (tapTimes.isEmpty())
					return;
			}
		}

		// if the user is moving their body too much, reset the tap list
		if (!isUserStatic(trackerToWatch)) {
			tapTimes.clear();
			accelList.clear();
		}

		// get the amount of taps in the list
		// and set the detection time
		int newTaps = tapTimes.size();
		if (newTaps > taps) {
			taps = newTaps;
			detectionTime = time;
		}
	}

	private float getAccelDelta() {
		float max = -999.9f;
		float min = 999.9f;
		for (float[] val : accelList) {
			if (val[0] > max)
				max = val[0];
			if (val[0] < min)
				min = val[0];
		}
		return max - min;
	}

	private float getMaxAccel() {
		float max = 0.0f;
		for (float[] val : accelList) {
			if (val[0] > max) {
				max = val[0];
			}
		}
		return max;
	}

	// returns true if the user is not imparting more than allowedBodyAccel of
	// force on any of the torso or upper leg trackers (this sadly implies that
	// you need two or more trackers for this feature to be reliable)
	private boolean isUserStatic(Tracker trackerToExclude) {
		int num = 0;
		if (
			skeleton.getUpperChestTracker() != null
				&& !skeleton.getUpperChestTracker().equals(trackerToExclude)
		) {
			if (
				skeleton.getUpperChestTracker().getAcceleration().lenSq()
					> ALLOWED_BODY_ACCEL_SQUARED
			)
				num++;
		}
		if (
			skeleton.getChestTracker() != null
				&& !skeleton.getChestTracker().equals(trackerToExclude)
		) {
			if (skeleton.getChestTracker().getAcceleration().lenSq() > ALLOWED_BODY_ACCEL_SQUARED)
				num++;
		}
		if (
			skeleton.getHipTracker() != null && !skeleton.getHipTracker().equals(trackerToExclude)
		) {
			if (skeleton.getHipTracker().getAcceleration().lenSq() > ALLOWED_BODY_ACCEL_SQUARED)
				num++;
		}
		if (
			skeleton.getWaistTracker() != null
				&& !skeleton.getWaistTracker().equals(trackerToExclude)
		) {
			if (skeleton.getWaistTracker().getAcceleration().lenSq() > ALLOWED_BODY_ACCEL_SQUARED)
				num++;
		}
		if (
			skeleton.getLeftUpperLegTracker() != null
				&& !skeleton.getLeftUpperLegTracker().equals(trackerToExclude)
		) {
			if (
				skeleton.getLeftUpperLegTracker().getAcceleration().lenSq()
					> ALLOWED_BODY_ACCEL_SQUARED
			)
				num++;
		}
		if (
			skeleton.getRightUpperLegTracker() != null
				&& !skeleton.getRightUpperLegTracker().equals(trackerToExclude)
		) {
			if (
				skeleton.getRightUpperLegTracker().getAcceleration().lenSq()
					> ALLOWED_BODY_ACCEL_SQUARED
			)
				num++;
		}
		if (
			skeleton.getLeftFootTracker() != null
				&& !skeleton.getLeftFootTracker().equals(trackerToExclude)
		) {
			if (
				skeleton.getLeftFootTracker().getAcceleration().lenSq() > ALLOWED_BODY_ACCEL_SQUARED
			)
				num++;
		}
		if (
			skeleton.getRightFootTracker() != null
				&& !skeleton.getRightFootTracker().equals(trackerToExclude)
		) {
			if (
				skeleton.getRightFootTracker().getAcceleration().lenSq()
					> ALLOWED_BODY_ACCEL_SQUARED
			)
				num++;
		}
		return num < numberTrackersOverThreshold;
	}
}
