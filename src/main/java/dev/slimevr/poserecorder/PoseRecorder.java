package dev.slimevr.poserecorder;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.lang3.tuple.Pair;

import io.eiren.util.ann.VRServerThread;
import io.eiren.util.collections.FastList;
import io.eiren.util.logging.LogManager;
import io.eiren.vr.VRServer;
import io.eiren.vr.trackers.Tracker;

public class PoseRecorder {

	protected PoseFrames poseFrame = null;

	protected int numFrames = -1;
	protected int frameCursor = 0;
	protected long frameRecordingInterval = 60L;
	protected long nextFrameTimeMs = -1L;

	protected CompletableFuture<PoseFrames> currentRecording;

	protected final VRServer server;
	FastList<Pair<Tracker, PoseFrameTracker>> trackers = new FastList<Pair<Tracker, PoseFrameTracker>>();

	public PoseRecorder(VRServer server) {
		this.server = server;
		server.addOnTick(this::onTick);
	}

	@VRServerThread
	public void onTick() {
		if(numFrames > 0) {
			PoseFrames poseFrame = this.poseFrame;
			List<Pair<Tracker, PoseFrameTracker>> trackers = this.trackers;
			if (poseFrame != null && trackers != null) {
				if (frameCursor < numFrames) {
					long curTime = System.currentTimeMillis();
					if (curTime >= nextFrameTimeMs) {
						nextFrameTimeMs += frameRecordingInterval;

						// To prevent duplicate frames, make sure the frame time is always in the future
						if (nextFrameTimeMs <= curTime) {
							nextFrameTimeMs = curTime + frameRecordingInterval;
						}

						int cursor = frameCursor++;
						for(Pair<Tracker, PoseFrameTracker> tracker : trackers) {
							// Add a frame for each tracker
							tracker.getRight().addFrame(cursor, tracker.getLeft());
						}

						// If done, send finished recording
						if(frameCursor >= numFrames) {
							internalStopRecording();
						}
					}
				} else {
					// If done and hasn't yet, send finished recording
					internalStopRecording();
				}
			}
		}
	}

	public synchronized Future<PoseFrames> startFrameRecording(int numFrames, long intervalMs) {
		return startFrameRecording(numFrames, intervalMs, server.getAllTrackers());
	}

	public synchronized Future<PoseFrames> startFrameRecording(int numFrames, long intervalMs, List<Tracker> trackers) {
		if(numFrames < 1) {
			throw new IllegalArgumentException("numFrames must at least have a value of 1");
		}
		if(intervalMs < 1) {
			throw new IllegalArgumentException("intervalMs must at least have a value of 1");
		}
		if(trackers == null) {
			throw new IllegalArgumentException("trackers must not be null");
		}
		if(trackers.isEmpty()) {
			throw new IllegalArgumentException("trackers must have at least one entry");
		}
		if(!isReadyToRecord()) {
			throw new IllegalStateException("PoseRecorder isn't ready to record!");
		}

		cancelFrameRecording();

		poseFrame = new PoseFrames(trackers.size());

		// Update tracker list
		this.trackers.ensureCapacity(trackers.size());
		for(Tracker tracker : trackers) {
			// Ignore null and computed trackers
			if(tracker == null || tracker.isComputed()) {
				continue;
			}

			// Pair tracker with recording
			this.trackers.add(Pair.of(tracker, poseFrame.addTracker(tracker, numFrames)));
		}

		this.frameCursor = 0;
		this.numFrames = numFrames;

		frameRecordingInterval = intervalMs;
		nextFrameTimeMs = -1L;

		LogManager.log.info("[PoseRecorder] Recording " + numFrames + " samples at a " + intervalMs + " ms frame interval");

		currentRecording = new CompletableFuture<PoseFrames>();
		return currentRecording;
	}

	private void internalStopRecording() {
		CompletableFuture<PoseFrames> currentRecording = this.currentRecording;
		if(currentRecording != null && !currentRecording.isDone()) {
			// Stop the recording, returning the frames recorded
			currentRecording.complete(poseFrame);
		}

		numFrames = -1;
		frameCursor = 0;
		trackers.clear();
		poseFrame = null;
	}

	public synchronized void stopFrameRecording() {
		internalStopRecording();
	}

	public synchronized void cancelFrameRecording() {
		CompletableFuture<PoseFrames> currentRecording = this.currentRecording;
		if(currentRecording != null && !currentRecording.isDone()) {
			// Cancel the current recording and return nothing
			currentRecording.cancel(true);
		}

		numFrames = -1;
		frameCursor = 0;
		trackers.clear();
		poseFrame = null;
	}

	public boolean isReadyToRecord() {
		return server.getTrackersCount() > 0;
	}

	public boolean isRecording() {
		return numFrames > frameCursor;
	}

	public boolean hasRecording() {
		return currentRecording != null;
	}

	public Future<PoseFrames> getFramesAsync() {
		return currentRecording;
	}

	public PoseFrames getFrames() throws ExecutionException, InterruptedException {
		CompletableFuture<PoseFrames> currentRecording = this.currentRecording;
		return currentRecording != null ? currentRecording.get() : null;
	}
}
