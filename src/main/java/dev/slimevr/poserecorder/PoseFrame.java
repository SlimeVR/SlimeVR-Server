package dev.slimevr.poserecorder;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import io.eiren.util.collections.FastList;
import io.eiren.vr.trackers.Tracker;

public final class PoseFrame implements Iterable<TrackerFrame[]> {
	
	private final FastList<PoseFrameTracker> trackers;
	
	public PoseFrame(FastList<PoseFrameTracker> trackers) {
		this.trackers = trackers;
	}
	
	public PoseFrame(int initialCapacity) {
		this.trackers = new FastList<PoseFrameTracker>(initialCapacity);
	}
	
	public PoseFrame() {
		this(5);
	}
	
	public PoseFrameTracker addTracker(PoseFrameTracker tracker) {
		trackers.add(tracker);
		return tracker;
	}
	
	public PoseFrameTracker addTracker(Tracker tracker, int initialCapacity) {
		return addTracker(new PoseFrameTracker(tracker.getName(), initialCapacity));
	}
	
	public PoseFrameTracker addTracker(Tracker tracker) {
		return addTracker(tracker, 5);
	}
	
	public PoseFrameTracker removeTracker(int index) {
		return trackers.remove(index);
	}
	
	public PoseFrameTracker removeTracker(PoseFrameTracker tracker) {
		trackers.remove(tracker);
		return tracker;
	}
	
	public void clearTrackers() {
		trackers.clear();
	}
	
	public void fakeClearTrackers() {
		trackers.fakeClear();
	}
	
	public int getTrackerCount() {
		return trackers.size();
	}
	
	public List<PoseFrameTracker> getTrackers() {
		return trackers;
	}
	
	public int getMaxFrameCount() {
		int maxFrames = 0;
		
		for(int i = 0; i < trackers.size(); i++) {
			PoseFrameTracker tracker = trackers.get(i);
			if(tracker != null && tracker.getFrameCount() > maxFrames) {
				maxFrames = tracker.getFrameCount();
			}
		}
		
		return maxFrames;
	}
	
	public int getFrames(int frameIndex, TrackerFrame[] buffer) {
		for(int i = 0; i < trackers.size(); i++) {
			PoseFrameTracker tracker = trackers.get(i);
			buffer[i] = tracker != null ? tracker.safeGetFrame(frameIndex) : null;
		}
		return trackers.size();
	}
	
	public int getFrames(int frameIndex, List<TrackerFrame> buffer) {
		for(int i = 0; i < trackers.size(); i++) {
			PoseFrameTracker tracker = trackers.get(i);
			buffer.add(i, tracker != null ? tracker.safeGetFrame(frameIndex) : null);
		}
		return trackers.size();
	}
	
	public TrackerFrame[] getFrames(int frameIndex) {
		TrackerFrame[] trackerFrames = new TrackerFrame[trackers.size()];
		getFrames(frameIndex, trackerFrames);
		return trackerFrames;
	}
	
	@Override
	public Iterator<TrackerFrame[]> iterator() {
		return new PoseFrameIterator(this);
	}
	
	public class PoseFrameIterator implements Iterator<TrackerFrame[]> {
		
		private final PoseFrame poseFrame;
		private final TrackerFrame[] trackerFrameBuffer;
		
		private int cursor = 0;
		
		public PoseFrameIterator(PoseFrame poseFrame) {
			this.poseFrame = poseFrame;
			trackerFrameBuffer = new TrackerFrame[poseFrame.getTrackerCount()];
		}
		
		@Override
		public boolean hasNext() {
			if(trackers.isEmpty()) {
				return false;
			}
			
			for(int i = 0; i < trackers.size(); i++) {
				PoseFrameTracker tracker = trackers.get(i);
				if(tracker != null && cursor < tracker.getFrameCount()) {
					return true;
				}
			}
			
			return false;
		}
		
		@Override
		public TrackerFrame[] next() {
			if(!hasNext()) {
				throw new NoSuchElementException();
			}
			
			poseFrame.getFrames(cursor++, trackerFrameBuffer);
			
			return trackerFrameBuffer;
		}
	}
}
