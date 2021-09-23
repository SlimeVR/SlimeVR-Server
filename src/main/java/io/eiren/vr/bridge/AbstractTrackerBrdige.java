package io.eiren.vr.bridge;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.tuple.Pair;

import io.eiren.util.collections.FastList;
import io.eiren.vr.trackers.Tracker;

public abstract class AbstractTrackerBrdige<R extends Tracker, S extends Tracker> implements VRBridge {
	
	private final Map<Tracker, S> sendTrackersMap = new HashMap<>();
	private final List<Tracker> sendTrackersList = new FastList<>();
	private final Queue<Pair<Tracker, S>> newSendTrackers = new LinkedBlockingQueue<>();

	private final Map<R, R> recieveTrackersMap = new HashMap<>();
	private final List<R> recieveTrackersList = new FastList<>();
	private final Queue<Pair<R, R>> newRecieveTrackers = new LinkedBlockingQueue<>();
	
	protected final AtomicBoolean newDataRecieved = new AtomicBoolean();
	
	// TODO CLEAN UP BUFFERS WHEN RECONNECTING
	
	public AbstractTrackerBrdige() {
	}
	
	protected void newDataRecieved() {
		newDataRecieved.set(true);
	}

	/**
	 * Process new recieved data
	 */
	@Override
	public void dataRead() {
		if(newDataRecieved.getAndSet(false)) {
			Pair<R, R> newTrackersPair;
			while((newTrackersPair = newRecieveTrackers.poll()) != null) {
				recieveTrackersMap.put(newTrackersPair.getKey(), newTrackersPair.getValue());
				recieveTrackersList.add(newTrackersPair.getKey());
				newRecieveTrackerAdded(newTrackersPair.getKey(), newTrackersPair.getValue());
			}
			for(int i = 0; i < recieveTrackersList.size(); ++i) {
				R tracker = recieveTrackersList.get(i);
				R internal = recieveTrackersMap.get(tracker);
				if(internal == null)
					throw new NullPointerException("Lost internal tracker somehow: " + tracker.getName()); // Shouln't really happen even, but better to catch it like this
				transferFromInternalTracker(internal, tracker);
			}
		}
	}

	/**
	 * Process new send data
	 */
	@Override
	public void dataWrite() {
		Pair<Tracker, S> newTrackersPair;
		while((newTrackersPair = newSendTrackers.poll()) != null) {
			sendTrackersMap.put(newTrackersPair.getKey(), newTrackersPair.getValue());
			sendTrackersList.add(newTrackersPair.getKey());
			newSendTrackerAdded(newTrackersPair.getKey(), newTrackersPair.getValue());
		}
		for(int i = 0; i < sendTrackersList.size(); ++i) {
			Tracker tracker = sendTrackersList.get(i);
			S internal = sendTrackersMap.get(tracker);
			if(internal == null)
				throw new NullPointerException("Lost internal tracker somehow: " + tracker.getName()); // Shouln't really happen even, but better to catch it like this
			transferToInternalTracker(tracker, internal);
		}
	}
	
	protected void newSendTrackerAdded(Tracker source, Tracker internal) {
	}
	
	protected void newRecieveTrackerAdded(R source, R internal) {
	}
	
	protected abstract void transferToInternalTracker(Tracker source, S target);
	
	protected abstract void transferFromInternalTracker(R source, R target);

	@Override
	public void addSharedTracker(Tracker tracker) {
		S internal = createInternalSharedTracker(tracker);
		newSendTrackers.add(Pair.of(tracker, internal));
	}
	
	protected abstract S createInternalSharedTracker(Tracker source);
	
	protected void registerRecieveTrackers(R source, R internal) {
		newRecieveTrackers.add(Pair.of(source, internal));
	}

	@Override
	public void removeSharedTracker(Tracker tracker) {
		// TODO Auto-generated method stub
	}
}
