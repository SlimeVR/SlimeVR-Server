package dev.slimevr.vr.processor.skeleton;

import java.util.LinkedList;
import com.jme3.math.Vector3f;

import dev.slimevr.vr.trackers.Tracker;


// class that monitors the acceleration of the waist, hip, or chest trackers to detect taps
// and use this to trigger a varaity of resets (if your wondering why no single tap class exists, it's because 
// to many false positives)
public class TapDetection {

	// server and related classes
	private HumanSkeleton skeleton;

	// tap detection
	private boolean enabled = false;
	private LinkedList<float[]> accelList = new LinkedList<>();
	private LinkedList<Float> tapTimes = new LinkedList<>();
	private Tracker trackerToWatch = null;

	// hyperparameters
	private static final float NS_CONVERTER = 1.0e9f;
	private static final float NEEDED_ACCEL_DELTA = 6.0f;
	private static final float ALLOWED_BODY_ACCEL = 1.5f;
	private static final float ALLOWED_BODY_ACCEL_SQUARED = ALLOWED_BODY_ACCEL * ALLOWED_BODY_ACCEL;
	private static final float CLUMP_TIME_NS = 0.03f * NS_CONVERTER;
	private static final float TIME_WINDOW_NS = 0.6f * NS_CONVERTER;

	// state
	private float detectionTime = -1.0f;
	private boolean doubleTaped = false;
	private boolean tripleTaped = false;

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

	public boolean getDoubleTapped() {
		return doubleTaped;
	}

	public boolean getTripleTapped() {
		return tripleTaped;
	}

	public float getDetectionTime() {
		return detectionTime;
	}

	// reset the lists for detecting taps
	public void resetDetector() {
		tapTimes.clear();
		accelList.clear();
		doubleTaped = false;
		tripleTaped = false;
	}

	// main function for tap detection
	public void update() {
		if (skeleton == null || !enabled)
			return;

		if (trackerToWatch == null)
			return;

		// get the acceleration of the tracker and add it to the list
		Vector3f accel = new Vector3f();
		trackerToWatch.getAcceleration(accel);
		float time = System.nanoTime();
		float[] listval = { accel.length(), time };
		accelList.add(listval);

		// remove old values from the list (if they are too old)
		while (time - accelList.getFirst()[1] > CLUMP_TIME_NS) {
			accelList.removeFirst();
		}

		// check for a tap
		if (getAccelDelta() > NEEDED_ACCEL_DELTA) {
			tapTimes.add(time);
		}

		// remove old taps from the list (if they are too old)
		if (!tapTimes.isEmpty()) {
			while (time - tapTimes.getFirst() > TIME_WINDOW_NS) {
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
		int tapEvents = getTapEvents();

		// if there are two tap events and the user is moving relatively slowly,
		// quick reset
		if (tapEvents == 2) {
			doubleTaped = true;
			detectionTime = time;
		} else if (tapEvents >= 3) {
			detectionTime = time;
			tripleTaped = true;
			doubleTaped = false;
		}
	}

	private float getAccelDelta() {
		float max = -999.9f;
		float min = 999.9f;
		for (int i = 0; i < accelList.size(); i++) {
			float[] val = accelList.get(i);
			if (val[0] > max)
				max = val[0];
			if (val[0] < min)
				min = val[0];
		}
		return max - min;
	}

	// return the number of distinct tap events in tapTimes
	private int getTapEvents() {
		if (tapTimes.isEmpty())
			return 0;

		int tapEvents = 0;
		float lastTapTime = tapTimes.getFirst();
		for (int i = 0; i < tapTimes.size(); i++) {
			if (tapTimes.get(i) - lastTapTime > CLUMP_TIME_NS) {
				tapEvents++;
				lastTapTime = tapTimes.get(i);
			}
		}
		return tapEvents;
	}

	// returns true if the user is not imparting more than allowedBodyAccel of
	// force on any of the torso or upper leg trackers (this sadly implies that
	// you need two or more trackers for this feature to be reliable)
	private boolean isUserStatic(Tracker trackerToExclude) {
		Vector3f accel = new Vector3f();
		if (skeleton.chestTracker != null && !skeleton.chestTracker.equals(trackerToExclude)) {
			skeleton.chestTracker.getAcceleration(accel);
			if (accel.lengthSquared() > ALLOWED_BODY_ACCEL_SQUARED)
				return false;
		}
		if (skeleton.hipTracker != null && !skeleton.hipTracker.equals(trackerToExclude)) {
			skeleton.hipTracker.getAcceleration(accel);
			if (accel.lengthSquared() > ALLOWED_BODY_ACCEL_SQUARED)
				return false;
		}
		if (skeleton.waistTracker != null && !skeleton.waistTracker.equals(trackerToExclude)) {
			skeleton.waistTracker.getAcceleration(accel);
			if (accel.lengthSquared() > ALLOWED_BODY_ACCEL_SQUARED)
				return false;
		}
		if (
			skeleton.leftUpperLegTracker != null
				&& !skeleton.leftUpperLegTracker.equals(trackerToExclude)
		) {
			skeleton.leftUpperLegTracker.getAcceleration(accel);
			if (accel.lengthSquared() > ALLOWED_BODY_ACCEL_SQUARED)
				return false;
		}
		if (
			skeleton.rightUpperLegTracker != null
				&& !skeleton.rightUpperLegTracker.equals(trackerToExclude)
		) {
			skeleton.rightUpperLegTracker.getAcceleration(accel);
			if (accel.lengthSquared() > ALLOWED_BODY_ACCEL_SQUARED)
				return false;
		}
		if (
			skeleton.leftFootTracker != null && !skeleton.leftFootTracker.equals(trackerToExclude)
		) {
			skeleton.leftFootTracker.getAcceleration(accel);
			if (accel.lengthSquared() > ALLOWED_BODY_ACCEL_SQUARED)
				return false;
		}
		if (
			skeleton.rightFootTracker != null && !skeleton.rightFootTracker.equals(trackerToExclude)
		) {
			skeleton.rightFootTracker.getAcceleration(accel);
			if (accel.lengthSquared() > ALLOWED_BODY_ACCEL_SQUARED)
				return false;
		}
		return true;
	}
}
