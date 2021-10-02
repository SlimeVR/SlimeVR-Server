package io.eiren.newGui.main;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import io.eiren.gui.TrackersList;
import io.eiren.util.ann.ThreadSafe;
import io.eiren.util.collections.FastList;
import io.eiren.vr.VRServer;
import io.eiren.vr.trackers.*;

import java.util.Comparator;
import java.util.List;

public class TrackersListNew {

	private VRServer server;
	private List<TrackerPanel> trackers;

	Quaternion q = new Quaternion();
	Vector3f v = new Vector3f();
	float[] angles = new float[3];

	public TrackersListNew(VRServer server) {
		this.server = server;
		this.trackers = new FastList<>();

		server.addNewTrackerConsumer(this::newTrackerAdded);
	}

	private class TrackerPanel {
		Tracker tracker;

		public TrackerPanel(Tracker tracker) {
			this.tracker = tracker;
		}


	}

	public void trackersListInit() {
		trackers.sort(Comparator.comparingInt(tr -> getTrackerSort(tr.tracker)));

		Class<? extends Tracker> currentClass = null;
		boolean first = true;

		for(int i = 0; i < trackers.size(); ++i) {

			TrackerPanel tr = trackers.get(i);
			Tracker t = tr.tracker;

			if(t instanceof ReferenceAdjustedTracker)
				t = ((ReferenceAdjustedTracker<?>) t).getTracker();






		}






	}

	private int getTrackerSort(Tracker t) {
		if(t instanceof ReferenceAdjustedTracker)
			t = ((ReferenceAdjustedTracker<?>) t).getTracker();
		if(t instanceof IMUTracker)
			return 0;
		if(t instanceof HMDTracker)
			return 100;
		if(t instanceof ComputedTracker)
			return 200;
		return 1000;
	}

	@ThreadSafe
	public void newTrackerAdded(Tracker t) {
		trackers.add(new TrackerPanel(t));
		//build();
	}

}
