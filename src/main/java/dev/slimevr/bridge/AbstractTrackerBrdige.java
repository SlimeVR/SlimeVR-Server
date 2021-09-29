package dev.slimevr.bridge;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.tuple.Pair;

import io.eiren.util.ann.ThreadSafe;
import io.eiren.util.ann.VRServerThread;
import io.eiren.util.collections.FastList;
import io.eiren.vr.trackers.Tracker;

/**
 * Abstract bridge handles creation of internal trackers and transfer
 * data between internal and external trackers.
 * <p>Bridge creates an internal copy of both remote and local trackers
 * and readData/writeData methods transfer data between them and real trackers.
 * Internal trackers are required because bridge can run in a separate thread
 * from the server's main thread, and this way we can keep data consistency.
 * @param <R> - types of trackers this bridge recieves
 * @param <S> - types of internal trackers this bridge creates to copy shared trackers
 */
public abstract class AbstractTrackerBrdige<R extends Tracker, S extends Tracker> implements Bridge {
	
	private final Map<Tracker, S> sendTrackersMap = new HashMap<>();
	private final List<Tracker> sendTrackersList = new FastList<>();
	private final Queue<Pair<Tracker, S>> newSendTrackers = new LinkedBlockingQueue<>();
	private final Map<R, R> recieveTrackersMap = new HashMap<>();
	private final List<R> recieveTrackersList = new FastList<>();
	private final Queue<Pair<R, R>> newRecieveTrackers = new LinkedBlockingQueue<>();
	protected final AtomicBoolean newDataRecieved = new AtomicBoolean();
	
	public AbstractTrackerBrdige() {
	}
	
	@BridgeThread
	protected void newDataRecieved() {
		newDataRecieved.set(true);
	}
	
	@ThreadSafe
	protected void cleanup() {
		// TODO CLEAN UP BUFFERS WHEN RECONNECTING
	}

	@BridgeThread
	protected void connectionEstablished() {
		cleanup();
	}

	/**
	 * Process new recieved data
	 */
	@Override
	@VRServerThread
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
					throw new NullPointerException("Lost internal tracker somehow: " + tracker.getName()); // Shouln't really happen, but better to catch it like this
				transferFromInternalTracker(internal, tracker);
			}
		}
	}

	/**
	 * Process new send data
	 */
	@Override
	@VRServerThread
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
				throw new NullPointerException("Lost internal tracker somehow: " + tracker.getName()); // Shouln't really happen, but better to catch it like this
			transferToInternalTracker(tracker, internal);
		}
	}

	@VRServerThread
	protected void newSendTrackerAdded(Tracker source, Tracker internal) {
	}

	@VRServerThread
	protected void newRecieveTrackerAdded(R source, R internal) {
	}

	@VRServerThread
	protected abstract void transferToInternalTracker(Tracker source, S target);

	@VRServerThread
	protected abstract void transferFromInternalTracker(R source, R target);

	@Override
	@ThreadSafe
	public void addSharedTracker(Tracker tracker) {
		S internal = createInternalSharedTracker(tracker);
		newSendTrackers.add(Pair.of(tracker, internal));
	}

	@ThreadSafe
	protected abstract S createInternalSharedTracker(Tracker source);

	@BridgeThread
	protected void registerRecieveTrackers(R source, R internal) {
		newRecieveTrackers.add(Pair.of(source, internal));
	}

	@Override
	@ThreadSafe
	public void removeSharedTracker(Tracker tracker) {
		// TODO Auto-generated method stub
	}
}
