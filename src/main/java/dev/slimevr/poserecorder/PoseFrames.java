package dev.slimevr.poserecorder;

import dev.slimevr.vr.trackers.TrackerPosition;
import dev.slimevr.vr.trackers.TrackerUtils;
import io.eiren.util.collections.FastList;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;


public final class PoseFrames implements Iterable<TrackerFrame[]> {

	private final FastList<PoseFrameTracker> trackers;

	/**
	 * Creates a {@link PoseFrames} object with the provided list of
	 * {@link PoseFrameTracker}s as the internal {@link PoseFrameTracker} list
	 *
	 * @see {@link FastList}, {@link PoseFrameTracker}
	 */
	public PoseFrames(FastList<PoseFrameTracker> trackers) {
		this.trackers = trackers;
	}

	/**
	 * Creates a {@link PoseFrames} object with the specified initial tracker
	 * capacity
	 *
	 * @see {@link #PoseFrames(FastList)}
	 */
	public PoseFrames(int initialCapacity) {
		this.trackers = new FastList<PoseFrameTracker>(initialCapacity);
	}

	/**
	 * Creates a {@link PoseFrames} object with the default initial tracker
	 * capacity of {@code 5}
	 *
	 * @see {@link #PoseFrames(int)}
	 */
	public PoseFrames() {
		this(5);
	}

	/**
	 * Adds the provided {@link PoseFrameTracker} into the internal
	 * {@link PoseFrameTracker} list
	 *
	 * @return The {@link PoseFrameTracker} provided
	 * @see {@link List#add(Object)}, {@link PoseFrameTracker}
	 */
	public PoseFrameTracker addTracker(PoseFrameTracker tracker) {
		trackers.add(tracker);
		return tracker;
	}

	/**
	 * Removes the {@link PoseFrameTracker} at the specified index from the
	 * internal {@link PoseFrameTracker} list
	 *
	 * @return The {@link PoseFrameTracker} previously at the specified index
	 * @see {@link List#remove(int)}, {@link PoseFrameTracker}
	 */
	public PoseFrameTracker removeTracker(int index) {
		return trackers.remove(index);
	}

	/**
	 * Removes the specified {@link PoseFrameTracker} from the internal
	 * {@link PoseFrameTracker} list
	 *
	 * @return {@code true} if the internal {@link PoseFrameTracker} list
	 * contained the specified {@link PoseFrameTracker}
	 * @see {@link List#remove(Object)}, {@link PoseFrameTracker}
	 */
	public boolean removeTracker(PoseFrameTracker tracker) {
		return trackers.remove(tracker);
	}

	/**
	 * Clears the internal {@link PoseFrameTracker} list
	 *
	 * @see {@link List#clear()}, {@link PoseFrameTracker}
	 */
	public void clearTrackers() {
		trackers.clear();
	}

	/**
	 * Fake clears the internal {@link PoseFrameTracker} list by setting the
	 * size to zero
	 *
	 * @see {@link FastList#fakeClear()}, {@link PoseFrameTracker}
	 */
	public void fakeClearTrackers() {
		trackers.fakeClear();
	}

	/**
	 * @return The number of contained {@link PoseFrameTracker} objects
	 * @see {@link List#size()}, {@link PoseFrameTracker}
	 */
	public int getTrackerCount() {
		return trackers.size();
	}

	/**
	 * @return A list of the contained {@link PoseFrameTracker} objects
	 * @see {@link List}, {@link PoseFrameTracker}
	 */
	public List<PoseFrameTracker> getTrackers() {
		return trackers;
	}

	// #region Data Utilities

	/**
	 * A utility function to get the maximum Y value of the tracker associated
	 * with the {@link TrackerPosition#HMD} tracker position
	 *
	 * @return The maximum Y value of the tracker associated with the
	 * {@link TrackerPosition#HMD} tracker position
	 * @see {@link #getMaxHeight(TrackerPosition)}, {@link TrackerPosition#HMD}
	 */
	public float getMaxHmdHeight() {
		return getMaxHeight(TrackerPosition.HMD);
	}

	/**
	 * A utility function to get the maximum Y value of the tracker associated
	 * with the specified {@link TrackerPosition}
	 *
	 * @return The maximum Y value of the tracker associated with the specified
	 * {@link TrackerPosition}
	 * @see {@link TrackerPosition}
	 */
	public float getMaxHeight(TrackerPosition trackerPosition) {
		float maxHeight = 0f;

		PoseFrameTracker hmd = TrackerUtils
			.findNonComputedHumanPoseTrackerForBodyPosition(trackers, trackerPosition);

		if (hmd == null) {
			return maxHeight;
		}

		for (TrackerFrame frame : hmd) {
			if (frame.hasData(TrackerFrameData.POSITION) && frame.position.y > maxHeight) {
				maxHeight = frame.position.y;
			}
		}

		return maxHeight;
	}
	// #endregion

	/**
	 * @return The maximum number of {@link TrackerFrame}s contained within each
	 * {@link PoseFrameTracker} in the internal {@link PoseFrameTracker} list
	 * @see {@link PoseFrameTracker#getFrameCount()}, {@link PoseFrameTracker}
	 */
	public int getMaxFrameCount() {
		int maxFrames = 0;

		for (PoseFrameTracker tracker : trackers) {
			if (tracker != null && tracker.getFrameCount() > maxFrames) {
				maxFrames = tracker.getFrameCount();
			}
		}

		return maxFrames;
	}

	/**
	 * Using the provided array buffer, get the {@link TrackerFrame}s contained
	 * within each {@link PoseFrameTracker} in the internal
	 * {@link PoseFrameTracker} list at the specified index
	 *
	 * @return The number of frames written to the buffer
	 * @see {@link PoseFrameTracker#safeGetFrame(int)}, {@link TrackerFrame},
	 * {@link PoseFrameTracker}
	 */
	public int getFrames(int frameIndex, TrackerFrame[] buffer) {
		int frameCount = 0;

		for (PoseFrameTracker tracker : trackers) {
			if (tracker == null) {
				continue;
			}

			TrackerFrame frame = tracker.safeGetFrame(frameIndex);

			if (frame == null) {
				continue;
			}

			buffer[frameCount++] = frame;
		}

		return frameCount;
	}

	/**
	 * Using the provided {@link List} buffer, get the {@link TrackerFrame}s
	 * contained within each {@link PoseFrameTracker} in the internal
	 * {@link PoseFrameTracker} list at the specified index
	 *
	 * @return The number of frames written to the buffer
	 * @see {@link PoseFrameTracker#safeGetFrame(int)},
	 * {@link List#set(int, Object)}, {@link TrackerFrame},
	 * {@link PoseFrameTracker}
	 */
	public int getFrames(int frameIndex, List<TrackerFrame> buffer) {
		int frameCount = 0;

		for (PoseFrameTracker tracker : trackers) {
			if (tracker == null) {
				continue;
			}

			TrackerFrame frame = tracker.safeGetFrame(frameIndex);

			if (frame == null) {
				continue;
			}

			buffer.set(frameCount++, frame);
		}

		return frameCount;
	}

	/**
	 * @return The {@link TrackerFrame}s contained within each
	 * {@link PoseFrameTracker} in the internal {@link PoseFrameTracker} list at
	 * the specified index
	 * @see {@link PoseFrameTracker#safeGetFrame(int)}, {@link TrackerFrame},
	 * {@link PoseFrameTracker}
	 */
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

		private final PoseFrames poseFrame;
		private final TrackerFrame[] trackerFrameBuffer;

		private int cursor = 0;

		public PoseFrameIterator(PoseFrames poseFrame) {
			this.poseFrame = poseFrame;
			trackerFrameBuffer = new TrackerFrame[poseFrame.getTrackerCount()];
		}

		@Override
		public boolean hasNext() {
			if (trackers.isEmpty()) {
				return false;
			}

			for (PoseFrameTracker tracker : trackers) {
				if (tracker != null && cursor < tracker.getFrameCount()) {
					return true;
				}
			}

			return false;
		}

		@Override
		public TrackerFrame[] next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}

			poseFrame.getFrames(cursor++, trackerFrameBuffer);

			return trackerFrameBuffer;
		}
	}
}
