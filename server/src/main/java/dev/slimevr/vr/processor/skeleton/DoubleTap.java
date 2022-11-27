package dev.slimevr.vr.processor.skeleton;

import java.util.LinkedList;
import com.jme3.math.Vector3f;
import dev.slimevr.vr.trackers.Tracker;


// class that monitors the acceleration of the waist, hip, or chest trackers to detect taps
// and use this to trigger a quick reset (if your wondering why no single tap class exists, it's because 
// to many false positives)
public class DoubleTap {

	// server
	private HumanSkeleton skeleton;

	// tap detection
	private LinkedList<float[]> accelList = new LinkedList<>();
	private LinkedList<Float> tapTimes = new LinkedList<>();

	// hyperparameters
	private static final float NEEDED_ACCEL_DELTA = 6.0f;
	private static final float ALLOWED_BODY_ACCEL = 1.5f;
	private static final float CLUMP_TIME = 0.02f;
	private static final float CLUMP_TIME_NS = CLUMP_TIME * 1000000000.0f;
	private static final float TIME_WINDOW = 0.4f;
	private static final float TIME_WINDOW_NS = TIME_WINDOW * 1000000000.0f;

	public DoubleTap(HumanSkeleton skeleton) {
		this.skeleton = skeleton;
	}

	// main function for tap detection
	public void update() {
		if (skeleton == null)
			return;

		Tracker tracker = getTrackerToWatch();
		if (tracker == null)
			return;

		// get the acceleration of the tracker and add it to the list
		Vector3f accel = new Vector3f();
		tracker.getAcceleration(accel);
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
		if (!isUserStatic(tracker)) {
			tapTimes.clear();
			accelList.clear();
		}

		// get the amount of taps in the list
		int tapEvents = getTapEvents();

		// if there are two tap events and the user is moving relatively slowly,
		// reset the skeleton
		if (tapEvents == 2) {
			skeleton.resetTrackersYaw();
			// reset the list
			tapTimes.clear();
			accelList.clear();
		}
	}

	// returns either the chest tracker, hip tracker, or waist tracker depending
	// on which one is available
	// if none are available, returns null
	public Tracker getTrackerToWatch() {
		if (skeleton.chestTracker != null)
			return skeleton.chestTracker;
		else if (skeleton.hipTracker != null)
			return skeleton.hipTracker;
		else if (skeleton.waistTracker != null)
			return skeleton.waistTracker;
		else
			return null;
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
			if (accel.length() > ALLOWED_BODY_ACCEL)
				return false;
		}
		if (skeleton.hipTracker != null && !skeleton.hipTracker.equals(trackerToExclude)) {
			skeleton.hipTracker.getAcceleration(accel);
			if (accel.length() > ALLOWED_BODY_ACCEL)
				return false;
		}
		if (skeleton.waistTracker != null && !skeleton.waistTracker.equals(trackerToExclude)) {
			skeleton.waistTracker.getAcceleration(accel);
			if (accel.length() > ALLOWED_BODY_ACCEL)
				return false;
		}
		if (
			skeleton.leftUpperLegTracker != null
				&& !skeleton.leftFootTracker.equals(trackerToExclude)
		) {
			skeleton.leftUpperLegTracker.getAcceleration(accel);
			if (accel.length() > ALLOWED_BODY_ACCEL)
				return false;
		}
		if (
			skeleton.rightUpperLegTracker != null
				&& !skeleton.rightFootTracker.equals(trackerToExclude)
		) {
			skeleton.rightUpperLegTracker.getAcceleration(accel);
			if (accel.length() > ALLOWED_BODY_ACCEL)
				return false;
		}
		return true;
	}
}
