package dev.slimevr.poserecorder;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import org.apache.commons.lang3.tuple.Pair;

import dev.slimevr.VRServer;
import dev.slimevr.util.ann.VRServerThread;
import dev.slimevr.vr.trackers.Tracker;
import io.eiren.util.collections.FastList;
import io.eiren.util.logging.LogManager;


public class PoseRecorder {

	public class RecordingProgress {

		public final int frame;
		public final int totalFrames;

		public RecordingProgress(int frame, int totalFrames) {
			this.frame = frame;
			this.totalFrames = totalFrames;
		}
	}

	protected PoseFrames poseFrame = null;

	protected int numFrames = -1;
	protected int frameCursor = 0;
	protected long frameRecordingInterval = 60L;
	protected long nextFrameTimeMs = -1L;

	protected CompletableFuture<PoseFrames> currentRecording;
	protected Consumer<RecordingProgress> currentFrameCallback;

	protected final VRServer server;
	FastList<Pair<Tracker, PoseFrameTracker>> trackers = new FastList<Pair<Tracker, PoseFrameTracker>>();

	public PoseRecorder(VRServer server) {
		this.server = server;
		server.addOnTick(this::onTick);
	}

	@VRServerThread
	public void onTick() {
		if (numFrames <= 0) {
			return;
		}

		PoseFrames poseFrame = this.poseFrame;
		List<Pair<Tracker, PoseFrameTracker>> trackers = this.trackers;
		if (poseFrame == null || trackers == null) {
			return;
		}

		if (frameCursor >= numFrames) {
			// If done and hasn't yet, send finished recording
			stopFrameRecording();
			return;
		}

		long curTime = System.currentTimeMillis();
		if (curTime < nextFrameTimeMs) {
			return;
		}

		nextFrameTimeMs += frameRecordingInterval;

		// To prevent duplicate frames, make sure the frame time is always in
		// the future
		if (nextFrameTimeMs <= curTime) {
			nextFrameTimeMs = curTime + frameRecordingInterval;
		}

		// Make sure it's synchronized since this is the server thread
		// interacting with
		// an unknown outside thread controlling this class
		synchronized (this) {
			// A stopped recording will be accounted for by an empty "trackers"
			// list
			int cursor = frameCursor++;
			for (Pair<Tracker, PoseFrameTracker> tracker : trackers) {
				// Add a frame for each tracker
				tracker.getRight().addFrame(cursor, tracker.getLeft());
			}

			if (currentFrameCallback != null) {
				currentFrameCallback.accept(new RecordingProgress(frameCursor, numFrames));
			}

			// If done, send finished recording
			if (frameCursor >= numFrames) {
				stopFrameRecording();
			}
		}
	}

	public synchronized Future<PoseFrames> startFrameRecording(int numFrames, long intervalMs) {
		return startFrameRecording(numFrames, intervalMs, server.getAllTrackers(), null);
	}

	public synchronized Future<PoseFrames> startFrameRecording(
		int numFrames,
		long intervalMs,
		Consumer<RecordingProgress> frameCallback
	) {
		return startFrameRecording(numFrames, intervalMs, server.getAllTrackers(), frameCallback);
	}

	public synchronized Future<PoseFrames> startFrameRecording(
		int numFrames,
		long intervalMs,
		List<Tracker> trackers
	) {
		return startFrameRecording(numFrames, intervalMs, trackers, null);
	}

	public synchronized Future<PoseFrames> startFrameRecording(
		int numFrames,
		long intervalMs,
		List<Tracker> trackers,
		Consumer<RecordingProgress> frameCallback
	) {
		if (numFrames < 1) {
			throw new IllegalArgumentException("numFrames must at least have a value of 1");
		}
		if (intervalMs < 1) {
			throw new IllegalArgumentException("intervalMs must at least have a value of 1");
		}
		if (trackers == null) {
			throw new IllegalArgumentException("trackers must not be null");
		}
		if (trackers.isEmpty()) {
			throw new IllegalArgumentException("trackers must have at least one entry");
		}
		if (!isReadyToRecord()) {
			throw new IllegalStateException("PoseRecorder isn't ready to record!");
		}

		cancelFrameRecording();

		poseFrame = new PoseFrames(trackers.size());

		// Update tracker list
		this.trackers.ensureCapacity(trackers.size());
		for (Tracker tracker : trackers) {
			// Ignore null and computed trackers
			if (tracker == null || tracker.isComputed()) {
				continue;
			}

			// Create a tracker recording
			PoseFrameTracker poseFrameTracker = new PoseFrameTracker(tracker, numFrames);
			poseFrame.addTracker(poseFrameTracker);

			// Pair tracker with recording
			this.trackers.add(Pair.of(tracker, poseFrameTracker));
		}

		this.frameCursor = 0;
		this.numFrames = numFrames;

		frameRecordingInterval = intervalMs;
		nextFrameTimeMs = -1L;

		LogManager
			.info(
				"[PoseRecorder] Recording "
					+ numFrames
					+ " samples at a "
					+ intervalMs
					+ " ms frame interval"
			);

		currentFrameCallback = frameCallback;
		currentRecording = new CompletableFuture<PoseFrames>();
		return currentRecording;
	}

	public synchronized void stopFrameRecording() {
		CompletableFuture<PoseFrames> currentRecording = this.currentRecording;
		if (currentRecording != null && !currentRecording.isDone()) {
			// Stop the recording, returning the frames recorded
			currentRecording.complete(poseFrame);
		}

		numFrames = -1;
		frameCursor = 0;
		trackers.clear();
		poseFrame = null;
	}

	public synchronized void cancelFrameRecording() {
		CompletableFuture<PoseFrames> currentRecording = this.currentRecording;
		if (currentRecording != null && !currentRecording.isDone()) {
			// Cancel the current recording and return nothing
			currentRecording.cancel(true);
		}

		numFrames = -1;
		frameCursor = 0;
		trackers.clear();
		poseFrame = null;
	}

	public synchronized boolean isReadyToRecord() {
		return server.getTrackersCount() > 0;
	}

	public synchronized boolean isRecording() {
		return numFrames > frameCursor;
	}

	public synchronized boolean hasRecording() {
		return currentRecording != null;
	}

	public synchronized Future<PoseFrames> getFramesAsync() {
		return currentRecording;
	}

	public synchronized PoseFrames getFrames() throws ExecutionException, InterruptedException {
		CompletableFuture<PoseFrames> currentRecording = this.currentRecording;
		return currentRecording != null ? currentRecording.get() : null;
	}
}
